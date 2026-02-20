package br.com.singletech.registro.entity.enums;


public enum Status {

    PENDENTE("Pendente"),
    PROCESSADO("Processado"),
    REGISTRADO("Registrado"),
    REJEITADO("Rejeitado");

    private String descricao;

    Status(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
