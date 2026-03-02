package br.com.singletech.teste.registro.bdd.steps;

import br.com.singletech.teste.registro.bdd.context.TestContext;
import br.com.singletech.teste.registro.repository.RegistroRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.path.json.JsonPath;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class CommonSteps {

    @Autowired
    private TestContext context;

    @Autowired
    private RegistroRepository registroRepository;

    @Autowired
    private MockMvc mockMvc;

    @Dado("que o endpoint base e {string}")
    public void definirEndpointBase(String endpointBase) {
        context.setEndpointBase(endpointBase);
    }

    @Dado("que o cliente envia {string} igual a {string}")
    public void definirHeader(String header, String value) {
        context.addHeader(header, value);
    }

    @Dado("que a API esta disponivel")
    public void validarApiDisponivel() throws Exception {
        MvcResult result = mockMvc.perform(get("/actuator/health")).andReturn();
        Assertions.assertThat(result.getResponse().getStatus()).isEqualTo(200);
    }

    @Dado("que o banco esta limpo")
    public void limparBanco() {
        registroRepository.deleteAll();
    }

    @Dado("que tenho os seguintes dados:")
    public void salvarDadosTabela(DataTable dataTable) {
        Map<String, String> dados = dataTable.asMap(String.class, String.class);
        context.putData("tabela", dados);
    }

    @Quando("envio uma requisicao {word} para {string}")
    public void enviarRequisicaoSemBody(String metodo, String caminho) throws Exception {
        executarRequisicao(metodo, caminho, null);
    }

    @Quando("envio uma requisicao {word} para {string} com:")
    public void enviarRequisicaoComBody(String metodo, String caminho, String payload) throws Exception {
        context.setRequestBody(payload);
        executarRequisicao(metodo, caminho, payload);
    }

    @Entao("o status HTTP deve ser {int}")
    public void validarStatusHttp(int status) {
        Assertions.assertThat(context.getResponseStatus()).isEqualTo(status);
    }

    @Entao("a resposta deve conter o campo {string}")
    public void validarCampoExiste(String campo) {
        Object value = JsonPath.from(context.getResponseBody()).get(campo);
        Assertions.assertThat(value).isNotNull();
    }

    @Entao("a resposta deve conter {string} igual a {string}")
    public void validarCampoString(String campo, String valor) {
        Assertions.assertThat(JsonPath.from(context.getResponseBody()).getString(campo)).isEqualTo(valor);
    }

    @Entao("a resposta deve conter {string} igual a {int}")
    public void validarCampoNumerico(String campo, int valor) {
        Assertions.assertThat(JsonPath.from(context.getResponseBody()).getInt(campo)).isEqualTo(valor);
    }

    @Entao("a resposta deve conter mensagem {string}")
    public void validarMensagem(String mensagem) {
        String detail = JsonPath.from(context.getResponseBody()).getString("detail");
        Assertions.assertThat(detail).contains(mensagem);
    }

    @Entao("a resposta deve conter erro de validacao para {string}")
    public void validarErroValidacao(String campo) {
        Assertions.assertThat(JsonPath.from(context.getResponseBody()).getList("errors.field", String.class))
                .contains(campo);
    }

    private void executarRequisicao(String metodo, String caminho, String body) throws Exception {
        String pathFinal = resolverPath(caminho);
        MockHttpServletRequestBuilder request = switch (metodo.toUpperCase()) {
            case "GET" -> get(pathFinal);
            case "POST" -> post(pathFinal);
            case "PUT" -> put(pathFinal);
            case "PATCH" -> patch(pathFinal);
            case "DELETE" -> delete(pathFinal);
            default -> throw new IllegalArgumentException("Metodo HTTP nao suportado: " + metodo);
        };

        for (Map.Entry<String, String> header : context.getHeaders().entrySet()) {
            request.header(header.getKey(), header.getValue());
        }

        if (body != null && !body.isBlank()) {
            request.contentType(MediaType.APPLICATION_JSON).content(body);
        }

        MvcResult result = mockMvc.perform(request).andReturn();
        context.setResponseStatus(result.getResponse().getStatus());
        context.setResponseBody(result.getResponse().getContentAsString());
    }

    private String resolverPath(String caminho) {
        if (caminho.startsWith("/")) {
            return caminho;
        }

        String base = context.getEndpointBase();
        if (base == null || base.isBlank()) {
            return "/" + caminho;
        }

        if (base.endsWith("/") && caminho.startsWith("/")) {
            return base + caminho.substring(1);
        }

        if (!base.endsWith("/") && !caminho.startsWith("/")) {
            return base + "/" + caminho;
        }

        return base + caminho;
    }
}
