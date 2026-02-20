package br.com.singletech.teste.registro.dto.response;

import br.com.singletech.teste.registro.entity.enums.Status;
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
public class RegistroResponse {

    private Long id;

    private String numeroContrato;

    private LocalDateTime dataCriacao;

    private Status status;

    private BigDecimal valorContrato;

    private String nomeCliente;

    private String documentoCliente;

    private String placaVeiculo;

    private LocalDateTime dataAtualizacao;
}