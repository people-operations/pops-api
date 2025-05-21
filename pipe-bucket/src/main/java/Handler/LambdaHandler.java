package Handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
    private final String BUCKET_NAME = System.getenv("BUCKET_NAME");
    private final String FILE_NAME = "colaboradores_certificados_pv.csv";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LambdaHandler.class);

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        try {
            String body = event.getBody();
            JsonNode json = objectMapper.readTree(body);

            logger.info("Body recebido: {}", body);

            StringBuilder csv = new StringBuilder();
            String header = "Nome completo do colaborador,Departamento/time,E-mail,ID do certificado,Tipo do certificado,Nome do certificado,Instituição emissora,Área de conhecimento,Data de conclusão,Data de vencimento (se aplicável),Carga horária (se aplicável),Modalidade do curso,Obrigatoriedade de certificado,Categoria do conhecimento obtido,Upload do certificado (arquivo),Observações (opcional)\n\n";

            try {
                String existingFileContent = s3.getObjectAsString(BUCKET_NAME, FILE_NAME);
                csv.append(existingFileContent);
            } catch (Exception e) {
                csv.append(header);
            }

            csv.append(safe(json, "nomeColaborador")).append(",");
            csv.append(safe(json, "departamentoTime")).append(",");
            csv.append(safe(json, "emailContato")).append(",");
            csv.append(safe(json, "idCertificado")).append(",");
            csv.append(safe(json, "tipoCertificado")).append(",");
            csv.append(safe(json, "nomeCertificado")).append(",");
            csv.append(safe(json, "instituicaoEmissora")).append(",");
            csv.append(safe(json, "areaConhecimento")).append(",");
            csv.append(safe(json, "dataConclusao")).append(",");
            csv.append(safe(json, "dataVencimento")).append(",");
            csv.append(safe(json, "cargaHoraria")).append(",");
            csv.append(safe(json, "modalidadeCurso")).append(",");
            csv.append(safe(json, "certificadoObrigatorio")).append(",");
            csv.append(safe(json, "categoriaConhecimento")).append(",");
            csv.append(safe(json, "certificado")).append(",");
            csv.append(safe(json, "observacao")).append("\n");

            ByteArrayInputStream inputStream = new ByteArrayInputStream(csv.toString().getBytes(StandardCharsets.UTF_8));
            s3.putObject(BUCKET_NAME, FILE_NAME, inputStream, null);

            return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody("{\"message\": \"CSV gerado e enviado com sucesso!\"}");

        } catch (Exception e) {
            logger.error("Error processing the CSV", e);
            return new APIGatewayProxyResponseEvent().withStatusCode(500).withBody("{\"message\": \"Erro ao processar o CSV: " + e.getMessage() + "\"}");
        }
    }

    private String safe(JsonNode node, String fieldName) {
        JsonNode value = node.get(fieldName);
        return value != null ? value.asText().replace(",", " ") : "";
    }
}
