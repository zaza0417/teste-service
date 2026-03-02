package br.com.singletech.teste.registro.bdd.steps;

import br.com.singletech.teste.registro.bdd.context.TestContext;
import br.com.singletech.teste.registro.entity.Registro;
import br.com.singletech.teste.registro.entity.enums.Status;
import br.com.singletech.teste.registro.repository.RegistroRepository;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.path.json.JsonPath;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class VeiculoSteps {

    @Autowired
    private RegistroRepository registroRepository;

    @Autowired
    private TestContext context;

    @Autowired
    private MockMvc mockMvc;

    @Dado("que nao existe registro para o contrato {string}")
    public void garantirContratoInexistente(String numeroContrato) {
        registroRepository.findRegistroByNumeroContrato(numeroContrato)
                .ifPresent(registroRepository::delete);
    }

    @Dado("que ja existe registro para o contrato {string}")
    public void criarRegistroComContrato(String numeroContrato) {
        registroRepository.save(novoRegistro(numeroContrato, "AAA1A11", Status.PENDENTE));
    }

    @Dado("que existe registro para consulta por id")
    public void criarRegistroParaConsultaPorId() {
        Registro salvo = registroRepository.save(novoRegistro("CT-2026-0101", "BBB2B22", Status.PENDENTE));
        context.putData("registroId", salvo.getId());
    }

    @Quando("consulto o registro criado por id")
    public void consultarRegistroCriadoPorId() throws Exception {
        Long id = (Long) context.getData("registroId");
        MvcResult result = mockMvc.perform(get(context.getEndpointBase() + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        armazenarResposta(result);
    }

    @Dado("que existe registro para consulta por placa {string}")
    public void criarRegistroParaConsultaPorPlaca(String placa) {
        registroRepository.save(novoRegistro("CT-2026-0201", placa, Status.PENDENTE));
    }

    @Quando("consulto registros para localizar a placa {string}")
    public void consultarRegistrosParaLocalizarPlaca(String placa) throws Exception {
        context.putData("placaConsulta", placa);
        MvcResult result = mockMvc.perform(get(context.getEndpointBase())
                        .queryParam("page", "0")
                        .queryParam("size", "50")
                        .queryParam("sort", "id,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        armazenarResposta(result);
    }

    @Entao("deve existir ao menos {int} registro com a placa consultada")
    public void validarConsultaPorPlaca(int quantidadeMinima) {
        String placa = (String) context.getData("placaConsulta");
        List<String> placas = JsonPath.from(context.getResponseBody()).getList("content.placaVeiculo", String.class);

        long quantidade = placas.stream().filter(placa::equals).count();
        Assertions.assertThat(quantidade).isGreaterThanOrEqualTo(quantidadeMinima);
    }

    @Quando("atualizo o status do registro para {string}")
    public void atualizarStatusDoRegistro(String status) throws Exception {
        Long id = (Long) context.getData("registroId");
        MvcResult result = mockMvc.perform(patch(context.getEndpointBase() + "/" + id + "/status")
                        .queryParam("status", status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        armazenarResposta(result);
    }

    @Dado("que existe registro com status {string} para remocao")
    public void criarRegistroParaRemocao(String status) {
        Registro salvo = registroRepository.save(novoRegistro("CT-2026-0301", "CCC3C33", Status.valueOf(status)));
        context.putData("registroId", salvo.getId());
    }

    @Quando("removo o registro criado")
    public void removerRegistroCriado() throws Exception {
        Long id = (Long) context.getData("registroId");
        MvcResult result = mockMvc.perform(delete(context.getEndpointBase() + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        armazenarResposta(result);
    }

    @Entao("o registro deve ter sido removido do banco")
    public void validarRemocao() {
        Long id = (Long) context.getData("registroId");
        Assertions.assertThat(registroRepository.findById(id)).isEmpty();
    }

    @Quando("envio cadastro de registro com os dados da tabela")
    public void enviarCadastroComDadosDaTabela() throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, String> dados = (Map<String, String>) context.getData("tabela");

        String payload = """
                {
                  \"numeroContrato\": \"%s\",
                  \"valorContrato\": %s,
                  \"nomeCliente\": \"%s\",
                  \"documentoCliente\": \"%s\",
                  \"placaVeiculo\": \"%s\"
                }
                """.formatted(
                dados.get("numeroContrato"),
                dados.get("valorContrato"),
                dados.get("nomeCliente"),
                dados.get("documentoCliente"),
                dados.get("placaVeiculo")
        );

        MvcResult result = mockMvc.perform(post(context.getEndpointBase())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andReturn();

        armazenarResposta(result);
    }

    private Registro novoRegistro(String numeroContrato, String placa, Status status) {
        Registro registro = new Registro();
        registro.setNumeroContrato(numeroContrato);
        registro.setValorContrato(new BigDecimal("1500.00"));
        registro.setNomeCliente("Cliente BDD");
        registro.setDocumentoCliente("12345678901");
        registro.setPlacaVeiculo(placa);
        registro.setStatus(status);
        return registro;
    }

    private void armazenarResposta(MvcResult result) throws Exception {
        context.setResponseStatus(result.getResponse().getStatus());
        context.setResponseBody(result.getResponse().getContentAsString());
    }
}
