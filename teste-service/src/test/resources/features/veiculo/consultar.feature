# language: pt
@veiculo @api @limpeza @consulta
Funcionalidade: Consulta de veiculo via registros
  Como consumidor da API
  Quero consultar registros por id e por placa
  Para localizar dados previamente cadastrados

  Contexto:
    Dado que o endpoint base e "/api/v1/registros"
    E que o cliente envia "Content-Type" igual a "application/json"

  Cenario: Consultar registro por id
    Dado que existe registro para consulta por id
    Quando consulto o registro criado por id
    Entao o status HTTP deve ser 200
    E a resposta deve conter o campo "id"

  Cenario: Consultar registros e localizar por placa
    Dado que existe registro para consulta por placa "QWE4R56"
    Quando consulto registros para localizar a placa "QWE4R56"
    Entao o status HTTP deve ser 200
    E deve existir ao menos 1 registro com a placa consultada
