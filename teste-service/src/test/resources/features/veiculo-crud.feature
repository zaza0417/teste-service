# language: pt
@three_amigos @veiculo @crud
Funcionalidade: CRUD de Veiculo
  Como sistema de registro
  Quero gerenciar veiculos
  Para manter os dados consistentes e rastreaveis

  # Sessao Three Amigos (simulada)
  # Negocio (PO): placa e renavam devem ser unicos; anoFabricacao nao pode ser futuro.
  # QA: validar retornos 201/200/204 e falhas 400/404/409 com mensagens claras.
  # Dev: contrato REST em /api/v1/veiculos e validacoes de formato no request.

  Contexto:
    Dado que o endpoint base de veiculos e "/api/v1/veiculos"
    E que o cliente envia "Content-Type" igual a "application/json"

  @cadastro @positivo @smoke
  Cenario: Cadastrar veiculo com dados validos
    Dado que nao existe veiculo com placa "ABC1D23"
    E que nao existe veiculo com renavam "12345678901"
    Quando enviar uma requisicao POST para "/api/v1/veiculos" com:
      """
      {
        "placa": "ABC1D23",
        "renavam": "12345678901",
        "chassi": "9BWZZZ377VT004251",
        "modelo": "Onix LT",
        "marca": "Chevrolet",
        "ano_fabricacao": 2024
      }
      """
    Entao o status da resposta deve ser 201
    E a resposta deve conter o campo "id"
    E a resposta deve conter "placa" igual a "ABC1D23"

  @cadastro @negativo @regressao
  Cenario: Nao permitir cadastro de veiculo com placa ja existente
    Dado que ja existe veiculo com placa "ABC1D23"
    Quando enviar uma requisicao POST para "/api/v1/veiculos" com:
      """
      {
        "placa": "ABC1D23",
        "renavam": "10987654321",
        "chassi": "9BWZZZ377VT004252",
        "modelo": "HB20",
        "marca": "Hyundai",
        "ano_fabricacao": 2023
      }
      """
    Entao o status da resposta deve ser 409
    E a resposta deve conter mensagem "Veiculo ja cadastrado para a placa informada"

  @cadastro @negativo @validacao @outline
  Esquema do Cenario: Nao permitir cadastro com formato invalido
    Quando enviar uma requisicao POST para "/api/v1/veiculos" com:
      """
      {
        "placa": "<placa>",
        "renavam": "<renavam>",
        "chassi": "<chassi>",
        "modelo": "Polo",
        "marca": "Volkswagen",
        "ano_fabricacao": <ano_fabricacao>
      }
      """
    Entao o status da resposta deve ser 400
    E a resposta deve conter erro de validacao para "<campo_invalido>"

    Exemplos:
      | placa   | renavam     | chassi            | ano_fabricacao | campo_invalido |
      | AB12345 | 12345678901 | 9BWZZZ377VT004253 | 2024           | placa          |
      | ABC1D23 | 12345       | 9BWZZZ377VT004253 | 2024           | renavam        |
      | ABC1D23 | 12345678901 | 123               | 2024           | chassi         |
      | ABC1D23 | 12345678901 | 9BWZZZ377VT004253 | 2030           | ano_fabricacao |

  @consulta @positivo
  Cenario: Consultar veiculo por id existente
    Dado que existe veiculo com id 10
    Quando enviar uma requisicao GET para "/api/v1/veiculos/10"
    Entao o status da resposta deve ser 200
    E a resposta deve conter "id" igual a 10

  @consulta @negativo
  Cenario: Retornar nao encontrado ao consultar id inexistente
    Dado que nao existe veiculo com id 99999
    Quando enviar uma requisicao GET para "/api/v1/veiculos/99999"
    Entao o status da resposta deve ser 404
    E a resposta deve conter mensagem "Veiculo nao encontrado"

  @atualizacao @positivo
  Cenario: Atualizar dados de veiculo com sucesso
    Dado que existe veiculo com id 20
    Quando enviar uma requisicao PUT para "/api/v1/veiculos/20" com:
      """
      {
        "placa": "DEF2G34",
        "renavam": "98765432109",
        "chassi": "9BWZZZ377VT004300",
        "modelo": "Corolla",
        "marca": "Toyota",
        "ano_fabricacao": 2022
      }
      """
    Entao o status da resposta deve ser 200
    E a resposta deve conter "placa" igual a "DEF2G34"

  @atualizacao @negativo
  Cenario: Nao permitir atualizar para placa ja cadastrada em outro veiculo
    Dado que existe veiculo com id 21
    E que existe outro veiculo com placa "ZZZ9Z99"
    Quando enviar uma requisicao PUT para "/api/v1/veiculos/21" com:
      """
      {
        "placa": "ZZZ9Z99",
        "renavam": "45612378901",
        "chassi": "9BWZZZ377VT004301",
        "modelo": "Argo",
        "marca": "Fiat",
        "ano_fabricacao": 2021
      }
      """
    Entao o status da resposta deve ser 409
    E a resposta deve conter mensagem "Placa ja vinculada a outro veiculo"
