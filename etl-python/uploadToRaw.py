import requests
import pandas as pd
import json
from io import StringIO
import base64
import os

API_JAVA_URL = os.environ.get("API_JAVA_URL")

def lambda_handler(event, context):
    try:
        if event.get("isBase64Encoded", False):
            csv_bytes = base64.b64decode(event['body'])
            csv_str = csv_bytes.decode("utf-8")
        else:
            csv_str = event['body']

        # Usa StringIO para ler CSV a partir da string
        csv_io = StringIO(csv_str)
        df = pd.read_csv(csv_io)

        # Renomeia as colunas conforme seu mapeamento
        df = df.rename(columns={
            "Nome do Colaborador": "nomeColaborador",
            "Departamento/Time": "departamentoTime",
            "E-mail de Contato": "emailContato",
            "Código": "idCertificado",
            "Tipo de Certificado": "tipoCertificado",
            "Nome do Certificado": "nomeCertificado",
            "Instituição Emissora": "instituicaoEmissora",
            "Área do Conhecimento": "areaConhecimento",
            "Data de Conclusão": "dataConclusao",
            "Data de Vencimento": "dataVencimento",
            "Carga Horária": "cargaHoraria",
            "Modalidade": "modalidadeCurso",
            "Certificado Obrigatório?": "certificadoObrigatorio",
            "Categoria do Conhecimento Obtido": "categoriaConhecimento",
            "Arquivo de Certificado": "certificado",
            "Comentários Adicionais": "observacao"
        })

        df = df.fillna("")
        lista_json = df.to_dict(orient="records")

        response = requests.post(API_JAVA_URL, json=lista_json)

        return {
            "statusCode": 200,
            "body": json.dumps({
                "message": "Dados enviados com sucesso!",
                "lambdaJavaResponse": response.text
            })
        }

    except Exception as e:
        return {
            "statusCode": 500,
            "body": json.dumps({
                "message": "Erro ao processar",
                "error": str(e)
            })
        }