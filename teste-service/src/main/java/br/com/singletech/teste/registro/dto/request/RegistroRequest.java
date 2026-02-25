package br.com.singletech.teste.registro.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "RegistroRequest", description = "Dados para criacao/atualizacao de um registro.")
public class RegistroRequest {

    @NotBlank(message = "Número do contrato é obrigatório")
    @Size(max = 50, message = "Número do contrato deve ter no máximo 50 caracteres")
    @Schema(
            description = "Numero do contrato (identificador de negocio).",
            example = "CT-2026-0001",
            maxLength = 50,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String numeroContrato;

    @NotNull(message = "Valor do contrato é obrigatório")
    @Positive(message = "Valor do contrato deve ser positivo")
    @Schema(
            description = "Valor total do contrato.",
            example = "1500.50",
            minimum = "0.01",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal valorContrato;

    @NotBlank(message = "Nome do cliente é obrigatório")
    @Size(max = 200, message = "Nome do cliente deve ter no máximo 200 caracteres")
    @Schema(
            description = "Nome completo do cliente.",
            example = "Joao da Silva",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nomeCliente;

    @NotBlank(message = "Documento do cliente é obrigatório")
    @Size(min = 11, max = 14, message = "Documento deve ter entre 11 e 14 caracteres")
    @Schema(
            description = "Documento do cliente (CPF com 11 ou CNPJ com 14 digitos, somente numeros).",
            example = "12345678901",
            minLength = 11,
            maxLength = 14,
            pattern = "\\d{11}|\\d{14}",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String documentoCliente;

    @NotBlank(message = "Placa do veículo é obrigatória")
    @Size(max = 7, message = "Placa deve ter no máximo 7 caracteres")
    @Schema(
            description = "Placa do veiculo no formato Mercosul.",
            example = "ABC1D23",
            minLength = 7,
            maxLength = 7,
            pattern = "[A-Z]{3}[0-9][A-Z][0-9]{2}",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String placaVeiculo;

    @JsonProperty("data_criacao")
    @Schema(
            description = "Data e hora de criacao do registro.",
            example = "2026-02-23T08:00:00",
            type = "string",
            format = "date-time"
    )
    private LocalDateTime dataCriacao;

    @JsonProperty("data_atualizacao")
    @Schema(
            description = "Data e hora da ultima atualizacao do registro.",
            example = "2026-02-23T08:30:00",
            type = "string",
            format = "date-time"
    )
    private LocalDateTime dataAtualizacao;
}
