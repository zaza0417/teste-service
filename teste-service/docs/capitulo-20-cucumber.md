# Capitulo 20 - Cucumber com Spring Boot (Implementacao no projeto)

## 1. Objetivo
Este projeto foi configurado para executar especificacoes Gherkin em portugues e conecta-las a testes automatizados com Cucumber + Spring Boot.

## 2. O que foi implementado

### 2.1 Dependencias no `pom.xml`
Foram adicionadas dependencias de teste:
- `io.cucumber:cucumber-java`
- `io.cucumber:cucumber-spring`
- `io.cucumber:cucumber-junit-platform-engine`
- `org.junit.platform:junit-platform-suite`
- `io.rest-assured:rest-assured` (JsonPath para validacoes)

Essas dependencias cobrem:
- Leitura e execucao de features Gherkin.
- Integracao de ciclo de vida do Cucumber com contexto Spring.
- Execucao via JUnit Platform.
- Validacao de JSON de resposta com JsonPath.

### 2.2 Estrutura criada
```
src/test/java/br/com/singletech/teste/registro/bdd
├── CucumberTest.java
├── CucumberSpringConfiguration.java
├── context
│   └── TestContext.java
├── hooks
│   └── CucumberHooks.java
└── steps
    ├── CommonSteps.java
    └── VeiculoSteps.java

src/test/resources/features/veiculo
├── cadastrar.feature
├── consultar.feature
├── atualizar.feature
└── remover.feature
```

## 3. Classe de execucao (`CucumberTest`)
Arquivo: `src/test/java/br/com/singletech/teste/registro/bdd/CucumberTest.java`

Configuracoes aplicadas:
- `@Suite`: suite de teste JUnit Platform.
- `@IncludeEngines("cucumber")`: engine Cucumber.
- `@SelectClasspathResource("features/veiculo")`: seleciona somente as features do capitulo.
- `GLUE_PROPERTY_NAME`: pacote `br.com.singletech.teste.registro.bdd`.
- `PLUGIN_PROPERTY_NAME`: gera relatorios `pretty`, `html`, `json` e `junit`.
- `FEATURES_PROPERTY_NAME`: reforca a pasta de features em classpath.

## 4. Integracao com Spring (`CucumberSpringConfiguration`)
Arquivo: `src/test/java/br/com/singletech/teste/registro/bdd/CucumberSpringConfiguration.java`

Configuracoes:
- `@CucumberContextConfiguration`: habilita ponte Cucumber <-> Spring.
- `@SpringBootTest(webEnvironment = RANDOM_PORT)`: sobe contexto completo de integracao.
- `@AutoConfigureMockMvc`: habilita `MockMvc` para execucao dos steps HTTP no contexto de teste.
- `@ActiveProfiles("test")`: usa `application-test.yml`.

## 5. Compartilhamento de estado (`TestContext`)
Arquivo: `src/test/java/br/com/singletech/teste/registro/bdd/context/TestContext.java`

Implementacao com `@ScenarioScope`:
- Nova instancia por cenario.
- Compartilhada entre todos os steps do mesmo cenario.
- Estado limpo no inicio/fim por hooks.

Dados guardados:
- endpoint base.
- headers da requisicao.
- body atual.
- status HTTP e body da resposta.
- mapa generico de dados intermediarios (ids, placas, tabela).

## 6. Hooks (`CucumberHooks`)
Arquivo: `src/test/java/br/com/singletech/teste/registro/bdd/hooks/CucumberHooks.java`

Hooks implementados:
- `@BeforeAll` e `@AfterAll`: pontos globais.
- `@Before(order = 0)`: limpa contexto por cenario.
- `@Before("@limpeza", order = 1)`: limpa banco por tag.
- `@BeforeStep` e `@AfterStep`: pontos para auditoria/screenshot/log detalhado.
- `@After`: limpa contexto ao fim do cenario.

## 7. Step Definitions

### 7.1 `CommonSteps`
Arquivo: `src/test/java/br/com/singletech/teste/registro/bdd/steps/CommonSteps.java`

Responsabilidades:
- Steps reutilizaveis de infraestrutura:
  - API disponivel.
  - endpoint base.
  - headers.
  - limpeza de banco.
- Execucao HTTP generica (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`).
- Validacoes genericas:
  - status HTTP.
  - existencia de campo.
  - igualdade de campo string/int.
  - mensagem de erro (`detail`).
  - campo de erro de validacao (`errors.field`).
- Suporte a `DataTable` com `asMap()`.

### 7.2 `VeiculoSteps`
Arquivo: `src/test/java/br/com/singletech/teste/registro/bdd/steps/VeiculoSteps.java`

Responsabilidades de dominio:
- Preparar pre-condicoes (registros existentes/inexistentes).
- Consulta por ID (HTTP).
- Consulta por placa (localizacao em listagem paginada).
- Atualizacao de status via `PATCH /api/v1/registros/{id}/status?status=...`.
- Remocao via `DELETE /api/v1/registros/{id}`.
- Validacao de remocao no banco.
- Cadastro por `DataTable` montando payload JSON.

## 8. Features por dominio

### 8.1 `cadastrar.feature`
Cenarios:
- Cadastro positivo usando `DataTable`.
- Cadastro negativo por contrato duplicado (409).
- Validacoes de entrada com `Scenario Outline` (400 + campo invalido).

### 8.2 `consultar.feature`
Cenarios:
- Consulta por ID existente.
- Consulta com localizacao por placa na lista paginada.

### 8.3 `atualizar.feature`
Cenarios:
- Transicao valida de status (`PENDENTE -> PROCESSADO`).
- Transicao invalida com retorno 422.

### 8.4 `remover.feature`
Cenarios:
- Remocao permitida para `PENDENTE` (204).
- Remocao negada para `REGISTRADO` (409).

## 9. DataTable
Uso implementado:
- Step Gherkin:
  `Dado que tenho os seguintes dados:`
- Conversao:
  `dataTable.asMap(String.class, String.class)`
- Reuso:
  dados ficam no `TestContext` para steps seguintes.

## 10. Relatorios gerados
Configurados no runner:
- `pretty` (console)
- `html:target/cucumber-reports/cucumber.html`
- `json:target/cucumber-reports/cucumber.json`
- `junit:target/cucumber-reports/cucumber.xml`

## 11. Execucao

### 11.1 Executar suite Cucumber
```bash
mvn test -Dtest=CucumberTest
```

### 11.2 Filtrar por tags
```bash
mvn test -Dtest=CucumberTest -Dcucumber.filter.tags="@smoke"
mvn test -Dtest=CucumberTest -Dcucumber.filter.tags="@api and not @remocao"
mvn test -Dtest=CucumberTest -Dcucumber.filter.tags="@veiculo or @consulta"
```

## 12. Boas praticas aplicadas
- Steps declarativos de negocio.
- Reuso de steps comuns em `CommonSteps`.
- Estado compartilhado isolado por cenario com `@ScenarioScope`.
- Hooks com tag para limpeza controlada.
- Features separadas por operacao para facilitar manutencao.
- Tags para organizacao e execucao seletiva.

## 13. Observacao de aderencia ao dominio atual
O enunciado cita "veiculo", mas a API do projeto e de `registros` (`/api/v1/registros`).
Para manter executavel no codigo atual:
- os cenarios de "veiculo" foram mapeados para o agregado `Registro`;
- consulta por placa foi implementada via localizacao na listagem paginada atual.

Se no futuro for criado endpoint dedicado de veiculo/placa, os steps podem ser ajustados sem alterar o modelo de organizacao Cucumber aqui estabelecido.
