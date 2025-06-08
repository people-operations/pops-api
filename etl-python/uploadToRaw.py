import requests
import pandas as pd
import json
from io import BytesIO
import base64
import os
import logging

# Configuração de logging
logger = logging.getLogger()
logger.setLevel(logging.INFO)

API_JAVA_URL = os.environ.get("API_JAVA_URL")

def lambda_handler(event, context):
    try:
        logger.info("Iniciando processamento do evento.")

        # Verifica e decodifica o corpo se necessário
        if event.get("isBase64Encoded", False):
            excel_bytes = base64.b64decode(event['body'])
            logger.info("Corpo do evento decodificado de base64.")
        else:
            excel_bytes = event['body'].encode("utf-8")
            logger.info("Corpo do evento interpretado como texto.")

        # Lê o Excel com pandas, colunas de A a P
        excel_io = BytesIO(excel_bytes)
        df = pd.read_excel(excel_io, usecols="A:P")

        # Remove espaços extras dos nomes das colunas
        df.columns = df.columns.str.strip()

        logger.info("Excel carregado com sucesso. Linhas: %d", len(df))

        # Renomeia as colunas
        df = df.rename(columns={
            "Nome do Colaborador": "nomeColaborador",
            "Departamento/Time": "departamentoTime",
            "E-mail de Contato": "emailContato",
            "ID do Certificado": "idCertificado",
            "Tipo de Certificado": "tipoCertificado",
            "Nome do Certificado": "nomeCertificado",
            "Instituição Emissora": "instituicaoEmissora",
            "Área do Conhecimento": "areaConhecimento",
            "Data de Conclusão": "dataConclusao",
            "Data de Vencimento": "dataVencimento",
            "Carga Horária": "cargaHoraria",
            "Modalidade do Curso": "modalidadeCurso",
            "Certificado obrigatório para função?": "certificadoObrigatorio",
            "Categoria do conhecimento obtido": "categoriaConhecimento",
            "Anexo": "certificado",
            "Comentários Adicionais": "observacao"
        })

        df = df.fillna("")
        lista_json = df.to_dict(orient="records")

        # LOG do JSON enviado
        logger.info("Payload a ser enviado para a API Java:\n%s", json.dumps(lista_json, indent=2))

        response = requests.post(API_JAVA_URL, json=lista_json)
        logger.info("Resposta da API Java: %s", response.text)

        return {
            "statusCode": 200,
            "body": json.dumps({
                "message": "Dados enviados com sucesso!",
                "lambdaJavaResponse": response.text
            })
        }

    except Exception as e:
        logger.exception("Erro ao processar o arquivo Excel.")
        return {
            "statusCode": 500,
            "body": json.dumps({
                "message": "Erro ao processar o arquivo Excel.",
                "error": str(e)
            })
        }
