import boto3
import os
import csv
import io
import re
from datetime import datetime
import unicodedata
import logging

# Configuração do logger
logger = logging.getLogger()
logger.setLevel(logging.INFO)

# Função para normalizar o texto
def normalizar(texto):
    return unicodedata.normalize('NFKD', texto).encode('ASCII', 'ignore').decode('ASCII').strip().lower()

# Inicializando clientes S3 e SNS
s3_client = boto3.client('s3')
sns_client = boto3.client('sns')

TOPIC_ARN = os.getenv('TOPIC_ARN', 'arn:aws:sns:us-east-1:848479655698:expiration-waning')
SNS_TOPIC_ARN = os.getenv('SNS_TOPIC_ARN', 'arn:aws:sns:us-east-1:848479655698:etl-report')

def lambda_handler(event, context):
    # Configuração do S3
    bucket_origem = os.getenv('BUCKET_ORIGEM', 'bucket-pops-trusted-certificacoes')
    arquivo_s3 = 'limpo/colaboradores_sem_duplicatas.csv'

    try:
        obj = s3_client.get_object(Bucket=bucket_origem, Key=arquivo_s3)
        conteudo = obj['Body'].read().decode('utf-8')
        csvfile = io.StringIO(conteudo)
    except Exception as e:
        logger.error(f"Erro ao ler o arquivo do S3: {e}")
        raise

    leitor = csv.reader(csvfile, delimiter=';')
    cabecalho = next(leitor)
    cabecalho_normalizado = [normalizar(col) for col in cabecalho]

    try:
        idx_data_venc = 9  # Índices fixos conforme seu arquivo
        idx_nome = 0
        idx_email = 2
        idx_certificado = 5
    except ValueError as e:
        logger.error(f"Erro: {e}")
        raise Exception("Uma ou mais colunas necessárias não foram encontradas.")

    colaboradores_vencendo = []
    colaboradores_vencidos = []
    sem_data = []

    data_atual = datetime.now()

    for linha in leitor:
        data_str = linha[idx_data_venc].strip()
        if data_str:
            try:
                data = datetime.strptime(data_str, "%Y-%m-%d")

                nome = linha[idx_nome]
                email = linha[idx_email]
                certificado = linha[idx_certificado]
                data_validade = linha[idx_data_venc]

                if data.year == data_atual.year and data.month == data_atual.month:
                    if data >= data_atual:
                        # Certificado vencendo ainda este mês
                        colaboradores_vencendo.append(f"{nome} - {email} - {certificado} (Vencimento: {data_validade})")
                    else:
                        # Certificado já vencido este mês
                        colaboradores_vencidos.append(f"{nome} - {email} - {certificado} (Vencido em: {data_validade})")

                    # (Opcional) Criar assinatura no SNS
                    try:
                        sns_client.subscribe(
                            TopicArn=TOPIC_ARN,
                            Protocol='email',
                            Endpoint=email
                        )
                        logger.info(f"Assinatura criada para {email}")
                    except Exception as e:
                        logger.error(f"Erro ao criar assinatura para {email}: {e}")

            except Exception:
                sem_data.append(linha)
        else:
            sem_data.append(linha)

    # Construir mensagem combinada
    mensagem_resumo = "Olá,\n\n"

    if colaboradores_vencendo:
        mensagem_resumo += "🔵 Colaboradores com certificados vencendo no mês atual:\n"
        mensagem_resumo += "\n".join(colaboradores_vencendo) + "\n\n"
    else:
        mensagem_resumo += "🔵 Nenhum colaborador com certificado vencendo este mês.\n\n"

    if colaboradores_vencidos:
        mensagem_resumo += "🔴 Colaboradores com certificados já vencidos no mês atual:\n"
        mensagem_resumo += "\n".join(colaboradores_vencidos) + "\n\n"
    else:
        mensagem_resumo += "🔴 Nenhum colaborador com certificado vencido este mês.\n\n"

    mensagem_resumo += (
        "Não deixe para última hora! A equipe está disponível para qualquer dúvida.\n\n"
        "Atenciosamente,\n"
        "Equipe de Certificação\n\n"
        "*Essa é uma mensagem automática, por gentileza não responder."
    )

    # Enviar a mensagem para o tópico de alerta de vencimento
    try:
        sns_client.publish(
            TopicArn=TOPIC_ARN,
            Subject='Relatório de Certificados - Vencendo e Vencidos',
            Message=mensagem_resumo
        )
        logger.info("Email enviado com resumo dos certificados.")
    except Exception as e:
        logger.error(f"Erro ao enviar e-mail de resumo: {e}")

    # Enviar o e-mail técnico para o time de Infra de TI
    try:
        mensagem_final = f"""
[PROCESSAMENTO AUTOMÁTICO - PIPELINE DE CERTIFICADOS]

O processo de ETL para tratamento de certificados foi concluído.

Resumo da execução:
- Certificados processados com vencimento no mês atual: {len(colaboradores_vencendo)}
- Certificados identificados como vencidos: {len(colaboradores_vencidos)}
- Registros identificados sem data de vencimento: {len(sem_data)}

O pipeline realizou a leitura, classificação e organização dos dados conforme os critérios de vencimento de certificados.

Caso sejam observadas inconsistências nos dados extraídos ou problemas no pipeline, acionar a equipe responsável.

Atenciosamente,
Sistema de Processamento de Certificações

*Mensagem automática. Não responder este e-mail.
"""
        sns_client.publish(
            TopicArn=SNS_TOPIC_ARN,
            Subject="[ETL] Relatório Técnico de Processamento de Certificados",
            Message=mensagem_final
        )
        logger.info("E-mail técnico de processamento enviado via SNS.")
    except Exception as e:
        logger.error(f"Erro ao enviar e-mail técnico de processamento final: {e}")

    return {
        'statusCode': 200,
        'body': 'Listas de vencidos/vencendo geradas e e-mails enviados com sucesso! ✅'
    }