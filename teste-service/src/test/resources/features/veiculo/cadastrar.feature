# language: pt
@veiculo @api @limpeza
Funcionalidade: Cadastro de veiculo via registro
  Como sistema de registro
  Quero cadastrar registros com dados do veiculo
  Para iniciar o fluxo com status PENDENTE

  Contexto:
    Dado que a API esta disponivel
    E que o endpoint base e "/api/v1/registros"
    E que o cliente envia "Content-Type" igual a "application/json"

  @smoke @cadastro @positivo
  Cenario: Cadastrar registro com dados validos usando DataTable
    Dado que nao existe registro para o contrato "CT-2026-9001"
    E que tenho os seguintes dados:
      | numeroContrato   | CT-2026-9001 |
      | valorContrato    | 2500.75      |
      | nomeCliente      | Maria Souza  |
      | documentoCliente | 12345678901  |
      | placaVeiculo     | ABC1D23      |
    Quando envio cadastro de registro com os dados da tabela
    Entao o status HTTP deve ser 201
    E a resposta deve conter o campo "id"
    E a resposta deve conter "numeroContrato" igual a "CT-2026-9001"

  @cadastro @negativo
  Cenario: Nao permitir cadastro com contrato duplicado
    Dado que ja existe registro para o contrato "CT-2026-9002"
    Quando envio uma requisicao POST para "/api/v1/registros" com:
      """
      {
        "numeroContrato": "CT-2026-9002",
        "valorContrato": 1800.30,
        "nomeCliente": "Joao Silva",
        "documentoCliente": "12345678901",
        "placaVeiculo": "DEF2G34"
      }
      """
    Entao o status HTTP deve ser 409
    E a resposta deve conter mensagem "CT-2026-9002"

  @cadastro @negativo @validacao
  Esquema do Cenario: Nao permitir cadastro com campos invalidos
    Quando envio uma requisicao POST para "/api/v1/registros" com:
      """
      {
        "numeroContrato": "<numeroContrato>",
        "valorContrato": <valorContrato>,
        "nomeCliente": "<nomeCliente>",
        "documentoCliente": "<documentoCliente>",
        "placaVeiculo": "<placaVeiculo>"
      }
      """
    Entao o status HTTP deve ser 400
    E a resposta deve conter erro de validacao para "<campoInvalido>"

    Exemplos:
      | numeroContrato | valorContrato | nomeCliente | documentoCliente | placaVeiculo | campoInvalido    |
      |                | 1000.00       | Ana         | 12345678901      | ABC1D23      | numeroContrato   |
      | CT-2026-9003   | -10           | Ana         | 12345678901      | ABC1D23      | valorContrato    |
      | CT-2026-9004   | 1000.00       | Ana         | 123              | ABC1D23      | documentoCliente |
