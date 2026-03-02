# language: pt
@veiculo @api @limpeza @atualizacao
Funcionalidade: Atualizacao de status de registro
  Como sistema de registro
  Quero atualizar status do registro
  Para refletir o andamento do processo

  Contexto:
    Dado que o endpoint base e "/api/v1/registros"
    E que o cliente envia "Content-Type" igual a "application/json"

  Cenario: Atualizar status de PENDENTE para PROCESSADO
    Dado que existe registro para consulta por id
    Quando atualizo o status do registro para "PROCESSADO"
    Entao o status HTTP deve ser 200
    E a resposta deve conter "status" igual a "PROCESSADO"

  Cenario: Nao permitir transicao invalida de status
    Dado que existe registro com status "REGISTRADO" para remocao
    Quando atualizo o status do registro para "PROCESSADO"
    Entao o status HTTP deve ser 422
    E a resposta deve conter mensagem "registro em REGISTRADO"
