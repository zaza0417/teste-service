package br.com.singletech.teste.registro.entity;

import br.com.singletech.registro.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "registros")
public class Registro {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String numeroContrato;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorContrato;

    private String nomeCliente;

    private String documentoCliente;

    private String placaVeiculo;

    @LastModifiedDate
    private LocalDateTime dataAtualizacao;
}
