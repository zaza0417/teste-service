package br.com.singletech.teste.registro.bdd.hooks;

import br.com.singletech.teste.registro.bdd.context.TestContext;
import br.com.singletech.teste.registro.repository.RegistroRepository;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.BeforeStep;
import org.springframework.beans.factory.annotation.Autowired;

public class CucumberHooks {

    @Autowired
    private TestContext context;

    @Autowired
    private RegistroRepository registroRepository;

    @BeforeAll
    public static void beforeAll() {
        // Hook global para preparar recursos compartilhados, se necessario.
    }

    @Before(order = 0)
    public void beforeScenario() {
        context.clear();
    }

    @Before(value = "@limpeza", order = 1)
    public void limparBancoAntesDoCenario() {
        registroRepository.deleteAll();
    }

    @BeforeStep
    public void beforeStep() {
        // Hook de passo para auditoria/log em suites maiores.
    }

    @AfterStep
    public void afterStep() {
        // Hook de passo para screenshot/log detalhado, quando necessario.
    }

    @After(order = 0)
    public void afterScenario() {
        context.clear();
    }

    @AfterAll
    public static void afterAll() {
        // Hook global para liberar recursos compartilhados, se necessario.
    }
}
