package br.com.singletech.teste.registro.controller;

import br.com.singletech.teste.registro.dto.request.RegistroRequest;
import br.com.singletech.teste.registro.entity.Registro;
import br.com.singletech.teste.registro.entity.enums.Status;
import br.com.singletech.teste.registro.support.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RegistroControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/v1/registros";

    @Test
    void postDeveCriarComSucesso() throws Exception {
        RegistroRequest request = novoRegistroRequestValido("0001");
        request.setPlacaVeiculo("ABC1D23");
        request.setDocumentoCliente("12345678901");

        MvcResult result = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(BASE_URL + "/")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.numeroContrato").value(request.getNumeroContrato()))
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andReturn();

        validarSchema(result.getResponse().getContentAsString(), "schemas/registro-response.schema.json");
    }

    @Test
    void postDeveValidarCamposObrigatorios() throws Exception {
        RegistroRequest request = new RegistroRequest();
        request.setNumeroContrato("");
        request.setValorContrato(new BigDecimal("-1"));

        MvcResult result = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.length()").value(5))
                .andReturn();

        validarSchema(result.getResponse().getContentAsString(), "schemas/api-error.schema.json");
    }

    @Test
    void getPorIdDeveRetornarRegistroExistente() throws Exception {
        Registro salvo = salvarRegistro(Status.PENDENTE, "0002");

        MvcResult result = mockMvc.perform(get(BASE_URL + "/{id}", salvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(salvo.getId()))
                .andExpect(jsonPath("$.numeroContrato").value(salvo.getNumeroContrato()))
                .andReturn();

        validarSchema(result.getResponse().getContentAsString(), "schemas/registro-response.schema.json");
    }

    @Test
    void getPorIdDeveRetornar404QuandoInexistente() throws Exception {
        MvcResult result = mockMvc.perform(get(BASE_URL + "/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andReturn();

        validarSchema(result.getResponse().getContentAsString(), "schemas/api-error.schema.json");
    }

    @Test
    void getListaDeveRetornarPaginado() throws Exception {
        salvarRegistro(Status.PENDENTE, "0003");
        salvarRegistro(Status.PROCESSADO, "0004");
        salvarRegistro(Status.REJEITADO, "0005");

        MvcResult result = mockMvc.perform(get(BASE_URL)
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sort", "id,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andReturn();

        JsonNode body = toJsonNode(result.getResponse().getContentAsString());
        validarSchema(body.path("content").get(0), "schemas/registro-response.schema.json");
    }

    @Test
    void putDeveAtualizarComSucesso() throws Exception {
        Registro salvo = salvarRegistro(Status.PENDENTE, "0006");
        RegistroRequest request = novoRegistroRequestValido("0900");
        request.setDocumentoCliente("11122233344");
        request.setPlacaVeiculo("DEF2G34");

        MvcResult result = mockMvc.perform(put(BASE_URL + "/{id}", salvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(salvo.getId()))
                .andExpect(jsonPath("$.numeroContrato").value("CT-2026-0900"))
                .andExpect(jsonPath("$.placaVeiculo").value("DEF2G34"))
                .andReturn();

        validarSchema(result.getResponse().getContentAsString(), "schemas/registro-response.schema.json");
    }

    @Test
    void deleteDeveRemoverComSucesso() throws Exception {
        Registro salvo = salvarRegistro(Status.PENDENTE, "0007");

        mockMvc.perform(delete(BASE_URL + "/{id}", salvo.getId()))
                .andExpect(status().isNoContent());

        assertThat(registroRepository.findById(salvo.getId())).isEmpty();
    }

    @Test
    void deleteDeveRetornarRestricaoQuandoStatusNaoPermite() throws Exception {
        Registro salvo = salvarRegistro(Status.REGISTRADO, "0008");

        MvcResult result = mockMvc.perform(delete(BASE_URL + "/{id}", salvo.getId()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail", containsString("Remocao nao permitida")))
                .andReturn();

        validarSchema(result.getResponse().getContentAsString(), "schemas/api-error.schema.json");
    }
}
