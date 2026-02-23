package br.com.singletech.teste.registro.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ApiError", description = "Formato padrao para respostas de erro.")
public class ApiError {


    @Schema(
            description = "Data e hora do erro.",
            example = "2026-02-23T08:00:00",
            type = "string",
            format = "date-time"
    )
    private LocalDateTime timestamp;


    @Schema(
            description = "URI que identifica o tipo de erro.",
            example = "https://api.singletech.com.br/erros/dados-invalidos"
    )
    private String type;
    @Schema(description = "Titulo curto do erro.", example = "Dados invalidos")
    private String title;
    @Schema(description = "Codigo HTTP.", example = "400")
    private Integer status;
    @Schema(description = "Descricao detalhada do erro.", example = "Um ou mais campos estao com valores invalidos.")
    private String detail;
    @Schema(description = "Caminho do endpoint que gerou o erro.", example = "/api/v1/registros")
    private String instance;


    @Schema(description = "Lista de erros por campo (quando aplicavel).")
    private List<FieldErrorDetail> errors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "ApiErrorFieldErrorDetail", description = "Detalhe de erro de validacao por campo.")
    public static class FieldErrorDetail {
        @Schema(description = "Nome do campo com erro.", example = "numeroContrato")
        private String field;
        @Schema(description = "Mensagem do erro.", example = "Numero do contrato e obrigatorio")
        private String message;
        @Schema(description = "Valor rejeitado.", example = " ")
        private Object rejectedValue;
    }
}
