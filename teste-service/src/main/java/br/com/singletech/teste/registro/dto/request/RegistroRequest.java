package br.com.singletech.teste.registro.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroRequest {

    @NotBlank(message = "Número do contrato é obrigatório")
    @Size(max = 50, message = "Número do contrato deve ter no máximo 50 caracteres")
    private String numeroContrato;

    @NotNull(message = "Valor do contrato é obrigatório")
    @Positive(message = "Valor do contrato deve ser positivo")
    private BigDecimal valorContrato;

    @NotBlank(message = "Nome do cliente é obrigatório")
    @Size(max = 200, message = "Nome do cliente deve ter no máximo 200 caracteres")
    private String nomeCliente;

    @NotBlank(message = "Documento do cliente é obrigatório")
    @Size(min = 11, max = 14, message = "Documento deve ter entre 11 e 14 caracteres")
    private String documentoCliente;

    @NotBlank(message = "Placa do veículo é obrigatória")
    @Size(max = 7, message = "Placa deve ter no máximo 7 caracteres")
    private String placaVeiculo;
}