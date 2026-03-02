# language: pt
@veiculo @api @limpeza @remocao
Funcionalidade: Remocao de registro
  Como sistema de registro
  Quero remover registros de forma segura
  Para garantir consistencia de regras de negocio

  Contexto:
    Dado que o endpoint base e "/api/v1/registros"
    E que o cliente envia "Content-Type" igual a "application/json"

  Cenario: Remover registro com status PENDENTE
    Dado que existe registro com status "PENDENTE" para remocao
    Quando removo o registro criado
    Entao o status HTTP deve ser 204
    E o registro deve ter sido removido do banco

  Cenario: Nao permitir remocao quando status for REGISTRADO
    Dado que existe registro com status "REGISTRADO" para remocao
    Quando removo o registro criado
    Entao o status HTTP deve ser 409
    E a resposta deve conter mensagem "Remocao nao permitida"
