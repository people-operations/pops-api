package Handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
    private final String BUCKET_NAME = System.getenv("BUCKET_NAME");
    private final String FILE_NAME = "colaboradores_certificados_pv.csv";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LambdaHandler.class);

    private final String CABECALHO = "Nome completo do colaborador;Departamento/time;E-mail;ID do certificado;Tipo do certificado;Nome do certificado;Instituição emissora;Área de conhecimento;Data de conclusão;Data de vencimento (se aplicável);Carga horária (se aplicável);Modalidade do curso;Obrigatoriedade de certificado;Categoria do conhecimento obtido;Upload do certificado (arquivo);Observações (opcional)";

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        try {
            String body = event.getBody();
            JsonNode jsonNode = objectMapper.readTree(body);

            logger.info("Body recebido: {}", body);

            StringBuilder newContent = new StringBuilder();

            if (jsonNode.isObject()) {
                appendColaboradorCsvLine(newContent, jsonNode);
                uploadCsvToS3(appendOrCreateCsvFile(newContent.toString(), true));
            } else if (jsonNode.isArray()) {
                boolean isSingleFieldFormat = false;
                if (jsonNode.size() > 0) {
                    JsonNode first = jsonNode.get(0);
                    isSingleFieldFormat = first.fieldNames().hasNext() && !first.get(first.fieldNames().next()).isObject();
                }

                if (jsonNode.isArray()) {
                    for (JsonNode json : jsonNode) {
                        appendColaboradorCsvLine(newContent, json);
                    }
                    uploadCsvToS3(appendOrCreateCsvFile(newContent.toString(), true));
                }

            } else {
                return new APIGatewayProxyResponseEvent().withStatusCode(400)
                        .withBody("{\"message\": \"Formato de entrada inválido. Deve ser um JSON objeto ou um array de objetos.\"}");
            }

            return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody("{\"message\": \"CSV enviado com sucesso!\"}");
        } catch (Exception e) {
            logger.error("Erro ao processar CSV", e);
            return new APIGatewayProxyResponseEvent().withStatusCode(500)
                    .withBody("{\"message\": \"Erro ao processar o CSV: " + e.getMessage() + "\"}");
        }
    }

    private String appendOrCreateCsvFile(String newData, boolean incluiCabecalho) {
        StringBuilder csv = new StringBuilder();
        try {
            String existing = s3.getObjectAsString(BUCKET_NAME, FILE_NAME);
            csv.append(existing);
            if (!existing.endsWith("\n")) {
                csv.append("\n");
            }
        } catch (Exception e) {
            if (incluiCabecalho) {
                csv.append(CABECALHO).append("\n");
            }
        }
        csv.append(newData);
        return csv.toString();
    }

    private void uploadCsvToS3(String content) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("text/csv");
        metadata.setContentLength(content.getBytes(StandardCharsets.UTF_8).length);

        s3.putObject(BUCKET_NAME, FILE_NAME, inputStream, metadata);
    }

    private String safe(JsonNode node, String fieldName) {
        JsonNode value = node.get(fieldName);
        String text = value != null ? value.asText().replace(",", " ").replace("\"", "\"\"") : "";
        return text;
    }

    private void appendColaboradorCsvLine(StringBuilder newContent, JsonNode json) {
        newContent.append(safe(json, "nomeColaborador")).append(";");
        newContent.append(safe(json, "departamentoTime")).append(";");
        newContent.append(safe(json, "emailContato")).append(";");
        newContent.append(safe(json, "idCertificado")).append(";");
        newContent.append(safe(json, "tipoCertificado")).append(";");
        newContent.append(safe(json, "nomeCertificado")).append(";");
        newContent.append(safe(json, "instituicaoEmissora")).append(";");
        newContent.append(safe(json, "areaConhecimento")).append(";");
        newContent.append(safe(json, "dataConclusao")).append(";");
        newContent.append(safe(json, "dataVencimento")).append(";");
        newContent.append(safe(json, "cargaHoraria")).append(";");
        newContent.append(safe(json, "modalidadeCurso")).append(";");
        newContent.append(safe(json, "certificadoObrigatorio")).append(";");
        newContent.append(safe(json, "categoriaConhecimento")).append(";");
        newContent.append(safe(json, "certificado")).append(";");
        newContent.append(safe(json, "observacao")).append("\n");
    }
}
