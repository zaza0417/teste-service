package br.com.singletech.teste.registro.support;

import br.com.singletech.teste.registro.TesteServiceApplication;
import br.com.singletech.teste.registro.dto.request.RegistroRequest;
import br.com.singletech.teste.registro.entity.Registro;
import br.com.singletech.teste.registro.entity.enums.Status;
import br.com.singletech.teste.registro.repository.RegistroRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TesteServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    protected final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Autowired
    protected RegistroRepository registroRepository;

    @BeforeEach
    void limparBase() {
        registroRepository.deleteAll();
    }

    protected RegistroRequest novoRegistroRequestValido(String sufixo) {
        RegistroRequest request = new RegistroRequest();
        request.setNumeroContrato("CT-2026-" + sufixo);
        request.setValorContrato(new BigDecimal("1500.50"));
        request.setNomeCliente("Cliente " + sufixo);
        request.setDocumentoCliente("1234567" + sufixo);
        request.setPlacaVeiculo("ABC1D23");
        return request;
    }

    protected Registro salvarRegistro(Status status, String sufixo) {
        Registro registro = new Registro();
        registro.setNumeroContrato("CT-2026-" + sufixo);
        registro.setValorContrato(new BigDecimal("1500.50"));
        registro.setNomeCliente("Cliente " + sufixo);
        registro.setDocumentoCliente("9876543" + sufixo);
        registro.setPlacaVeiculo("XYZ1A" + sufixo.substring(Math.max(0, sufixo.length() - 2)));
        registro.setStatus(status);
        return registroRepository.save(registro);
    }

    protected String toJson(Object payload) throws IOException {
        return objectMapper.writeValueAsString(payload);
    }

    protected JsonNode toJsonNode(String payload) throws IOException {
        return objectMapper.readTree(payload);
    }

    protected void validarSchema(String responseBody, String schemaPath) throws IOException {
        validarSchema(toJsonNode(responseBody), schemaPath);
    }

    protected void validarSchema(JsonNode node, String schemaPath) throws IOException {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        JsonNode schemaNode;

        try (InputStream input = new ClassPathResource(schemaPath).getInputStream()) {
            schemaNode = objectMapper.readTree(input);
        }

        JsonSchema schema = factory.getSchema(schemaNode);
        Set<ValidationMessage> erros = schema.validate(node);

        assertThat(erros)
                .withFailMessage("JSON fora do schema %s: %s", schemaPath, erros)
                .isEmpty();
    }
}
