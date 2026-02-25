package br.com.singletech.teste.registro.dto.response;

import br.com.singletech.teste.registro.entity.enums.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "RegistroResponse", description = "Representacao de um registro.")
public class RegistroResponse {

    @Schema(
            description = "Identificador unico do registro.",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long id;

    @Schema(
            description = "Numero do contrato (identificador de negocio).",
            example = "CT-2026-0001",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String numeroContrato;

    @Schema(
            description = "Data e hora de criacao do registro.",
            example = "2026-02-23T08:00:00",
            type = "string",
            format = "date-time",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("data_criacao")
    private LocalDateTime dataCriacao;

    @Schema(
            description = "Status atual do registro.",
            example = "PENDENTE",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Status status;

    @Schema(
            description = "Valor total do contrato.",
            example = "1500.50",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal valorContrato;

    @Schema(
            description = "Nome completo do cliente.",
            example = "Joao da Silva",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nomeCliente;

    @Schema(
            description = "Documento do cliente (CPF ou CNPJ, somente numeros).",
            example = "12345678901",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String documentoCliente;

    @Schema(
            description = "Placa do veiculo.",
            example = "ABC1D23",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String placaVeiculo;

    @Schema(
            description = "Data e hora da ultima atualizacao do registro.",
            example = "2026-02-23T08:30:00",
            type = "string",
            format = "date-time"
    )
    @JsonProperty("data_atualizacao")
    private LocalDateTime dataAtualizacao;
}
