import boto3
import csv
import io
from datetime import datetime
import unicodedata
import logging
import os

logger = logging.getLogger()
logger.setLevel(logging.INFO)

def normalizar(texto):
    return unicodedata.normalize('NFKD', texto).encode('ASCII', 'ignore').decode('ASCII').strip().lower()

def lambda_handler(event, context):
    s3 = boto3.client('s3')

    bucket_origem = os.getenv('BUCKET_ORIGEM', 'bucket-pops-trusted-certificacoes')
    arquivo_s3 = 'limpo/colaboradores_sem_duplicatas.csv'
    bucket_destino = os.getenv('BUCKET_DESTINO', 'bucket-pops-trusted-certificacoes')

    try:
        obj = s3.get_object(Bucket=bucket_origem, Key=arquivo_s3)
        conteudo = obj['Body'].read().decode('utf-8')
        csvfile = io.StringIO(conteudo)
    except Exception as e:
        logger.error(f"Erro ao ler o arquivo do S3: {e}")
        raise

    leitor = csv.reader(csvfile, delimiter=';')
    cabecalho = next(leitor)
    cabecalho_normalizado = [normalizar(col) for col in cabecalho]

    try:
        idx_data_venc = cabecalho_normalizado.index(normalizar("Data de vencimento (se aplicável)"))
    except ValueError:
        raise Exception("Coluna 'Data de vencimento (se aplicável)' não encontrada.")

    pastas = {}
    sem_data = []

    for linha in leitor:
        data_str = linha[idx_data_venc].strip()
        if data_str:
            try:
                data = datetime.strptime(data_str, "%Y-%m-%d")
                ano = data.strftime("%Y")
                mes = data.strftime("%b").lower()
                pasta = f"{ano}/{mes}/"
                nome_arquivo = f"colaboradores_{data.strftime('%Y%m')}.csv"
                caminho = pasta + nome_arquivo

                pastas.setdefault(caminho, []).append(linha)
            except Exception:
                sem_data.append(linha)
        else:
            sem_data.append(linha)

    # Upload dos arquivos com data
    for caminho, linhas in pastas.items():
        saida_csv = io.StringIO()
        escritor = csv.writer(saida_csv, delimiter=';')
        escritor.writerow(cabecalho)
        escritor.writerows(linhas)

        try:
            s3.put_object(
                Bucket=bucket_destino,
                Key=caminho,
                Body=saida_csv.getvalue().encode('utf-8')
            )
            logger.info(f"Arquivo salvo: {caminho}")
        except Exception as e:
            logger.error(f"Erro ao salvar {caminho} na S3: {e}")

    # Upload dos registros sem data
    if sem_data:
        saida_sem_data = io.StringIO()
        escritor = csv.writer(saida_sem_data, delimiter=';')
        escritor.writerow(cabecalho)
        escritor.writerows(sem_data)

        try:
            s3.put_object(
                Bucket=bucket_destino,
                Key='sem_data/colaboradores_sem_data.csv',
                Body=saida_sem_data.getvalue().encode('utf-8')
            )
            logger.info("Arquivo sem data salvo com sucesso.")
        except Exception as e:
            logger.error(f"Erro ao salvar arquivo sem data: {e}")

    return {
        'statusCode': 200,
        'body': 'Arquivos organizados por ano/mês e enviados para a S3 com sucesso! ✅'
    }