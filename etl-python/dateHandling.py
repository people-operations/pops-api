import boto3
import csv
import io
import re
from datetime import datetime
import unicodedata
import logging

# Configuração do logger
logger = logging.getLogger()
logger.setLevel(logging.INFO)

# Função para normalizar o texto (remoção de acentos, etc.)
def normalizar(texto):
    return unicodedata.normalize('NFKD', texto).encode('ASCII', 'ignore').decode('ASCII').strip().lower()

# Inicializando clientes S3 e SNS
s3_client = boto3.client('s3')
sns_client = boto3.client('sns')

TOPIC_ARN = 'arn:aws:sns:us-east-1:848479655698:expiration-waning'  # Substituir com o seu ARN real
SNS_TOPIC_ARN = 'arn:aws:sns:us-east-1:848479655698:email-sender'  # Substituir com o seu ARN real

# Função para verificar se o arquivo é do mês atual
def eh_arquivo_do_mes_atual(file_key: str) -> bool:
    current_year_month = datetime.now().strftime('%Y%m')
    pattern = rf"colaboradores[_\-]?{current_year_month}\.csv$"
    return re.search(pattern, file_key) is not None

# Função para adicionar assinatura dinâmica ao SNS
def adicionar_assinatura_dinamica(email):
    try:
        response = sns_client.subscribe(
            TopicArn=TOPIC_ARN,
            Protocol='email',
            Endpoint=email
        )
        print(f"Assinatura criada com sucesso para o e-mail {email}")
    except Exception as e:
        print(f"Erro ao criar assinatura para {email}: {e}")

# Função lambda principal
def lambda_handler(event, context):
    # Configuração do S3
    bucket_origem = 'bucket-pops-trusted-certificacoes'
    arquivo_s3 = 'limpo/colaboradores_sem_duplicatas.csv'
    bucket_destino = 'bucket-pops-trusted-certificacoes'

    try:
        # Baixando o arquivo do S3
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
        idx_data_venc = 9
        idx_nome = 0
        idx_email = 2
        idx_certificado = 5
    except ValueError as e:
        logger.error(f"Erro: {e}")
        raise Exception("Uma ou mais colunas necessárias não foram encontradas.")

    colaboradores_vencendo = []
    sem_data = []

    # Data atual
    data_atual = datetime.now()

    for linha in leitor:
        data_str = linha[idx_data_venc].strip()
        if data_str:
            try:
                data = datetime.strptime(data_str, "%Y-%m-%d")

                # Verificar se a data de vencimento é no mês atual e no mesmo dia ou depois da data atual
                if data.year == data_atual.year and data.month == data_atual.month and data >= data_atual:
                    nome = linha[idx_nome]
                    email = linha[idx_email]
                    certificado = linha[idx_certificado]
                    data_validade = linha[idx_data_venc]

                    # Adicionar na lista de colaboradores vencendo
                    colaboradores_vencendo.append(f"{nome} - {email} - {certificado} (Vencimento: {data_validade})")

                    # Adiciona assinatura para o SNS
                    adicionar_assinatura_dinamica(email)

            except Exception:
                sem_data.append(linha)
        else:
            sem_data.append(linha)

    # Montar a mensagem com os colaboradores que estão com certificado vencendo
    if colaboradores_vencendo:
        mensagem_resumo = f"""
        Olá,

        Abaixo estão os colaboradores com certificados prestes a vencer:

        {"\n".join(colaboradores_vencendo)}
        
        Não deixe para última hora! A equipe está disponível para qualquer dúvida.

        Atenciosamente,
        Equipe de Certificação

        *Essa é uma mensagem automática, por gentileza não responder.
        """
        try:
            sns_client.publish(
                TopicArn=TOPIC_ARN,
                Subject='Notificação de Certificados Vencendo',
                Message=mensagem_resumo
            )
            logger.info(f"Email enviado com resumo dos certificados vencendo.")
        except Exception as e:
            logger.error(f"Erro ao enviar e-mail de resumo: {e}")

    # Salvando arquivos no S3 por pasta (ano/mês)
    pastas = {}
    for linha in sem_data:
        # Lógica de processamento para salvar as linhas sem data
        pass

    # Envio de e-mail de processamento finalizado via SNS
    try:
        mensagem_final = f"""
        Olá,

        O processamento dos certificados foi finalizado.

        Colaboradores com certificados vencendo: {len(colaboradores_vencendo)}
        Registros sem data: {len(sem_data)}

        Caso haja irregularidade nos dados fornecidos, contatar suporte dedicado.

        Atenciosamente,
        Equipe de Processamento de Certificação

        *Essa é uma mensagem automática, por gentileza não responder.
        """
        sns_client.publish(
            TopicArn=SNS_TOPIC_ARN,
            Subject="Relatório de Processamento de Certificações",
            Message=mensagem_final
        )
        logger.info("E-mail de processamento finalizado enviado via SNS.")
    except Exception as e:
        logger.error(f"Erro ao enviar e-mail via SNS: {e}")

    return {
        'statusCode': 200,
        'body': 'Arquivos organizados e e-mail enviado com sucesso! ✅'
    }