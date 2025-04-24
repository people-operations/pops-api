import csv
import unicodedata
from datetime import datetime
import boto3
import io
import logging

logger = logging.getLogger()
logger.setLevel(logging.INFO)

def normalizar(texto):
    return unicodedata.normalize('NFKD', texto).encode('ASCII', 'ignore').decode('ASCII').strip().lower()

def lambda_handler(event, context):
    # === CONFIGURAÇÕES S3 ===
    bucket_origem = 'bucket-pops-raw-certificacoes'
    bucket_destino = 'bucket-pops-trusted-certificacoes'
    arquivo_s3 = 'colaboradores_certificados_pv.csv'
    arquivo_saida = 'limpo/colaboradores_sem_duplicatas.csv'

    # Cliente S3 com IAM role da Lambda
    s3 = boto3.client('s3')

    try:
        obj = s3.get_object(Bucket=bucket_origem, Key=arquivo_s3)
        conteudo = obj['Body'].read().decode('utf-8')
        csvfile = io.StringIO(conteudo)
    except Exception as e:
        logger.error(f"Erro ao baixar arquivo do S3: {e}")
        raise

    linhas_validas = set()

    campos_obrigatorios = [
        "Nome completo do colaborador",
        "E-mail",
        "Nome do certificado",
        "Tipo do certificado",
        "Data de conclusão",
        "Departamento/time",
        "Área de conhecimento"
    ]
    campos_obrigatorios_normalizados = [normalizar(campo) for campo in campos_obrigatorios]

    leitor = csv.reader(csvfile, delimiter=';')
    cabecalho = next(leitor)
    cabecalho_normalizado = [normalizar(col) for col in cabecalho]

    try:
        indices_obrigatorios = [cabecalho_normalizado.index(campo) for campo in campos_obrigatorios_normalizados]
        idx_data_conclusao = cabecalho_normalizado.index(normalizar("Data de conclusão"))
        idx_nome_colaborador = cabecalho_normalizado.index(normalizar("Nome completo do colaborador"))
    except ValueError as e:
        logger.error(f"Erro ao identificar os índices dos campos obrigatórios: {e}")
        raise

    for linha in leitor:
        if any(not linha[i].strip() for i in indices_obrigatorios):
            continue

        try:
            data_str = linha[idx_data_conclusao].strip()
            data_conclusao = datetime.strptime(data_str, "%Y-%m-%d")
            if not (datetime(2000, 1, 1) <= data_conclusao <= datetime.today()):
                continue
        except Exception:
            continue

        linhas_validas.add(tuple(linha))

    linhas_ordenadas = sorted(linhas_validas, key=lambda linha: linha[idx_nome_colaborador].lower())

    saida_csv = io.StringIO()
    escritor = csv.writer(saida_csv, delimiter=';')
    escritor.writerow(cabecalho)
    for linha in linhas_ordenadas:
        escritor.writerow(linha)

    try:
        s3.put_object(Bucket=bucket_destino, Key=arquivo_saida, Body=saida_csv.getvalue().encode('utf-8'))
        logger.info("Arquivo processado com sucesso e salvo no bucket trusted! ✅📤")
    except Exception as e:
        logger.error(f"Erro ao salvar arquivo no S3: {e}")
        raise

    return {
        'statusCode': 200,
        'body': 'Arquivo processado com sucesso!'
    }
