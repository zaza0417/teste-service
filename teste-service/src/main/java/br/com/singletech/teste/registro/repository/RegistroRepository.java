package br.com.singletech.teste.registro.repository;

import br.com.singletech.teste.registro.entity.Registro;
import br.com.singletech.teste.registro.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroRepository extends JpaRepository<Registro, Long> {

    Optional<Registro> findRegistroByNumeroContrato(String numeroContrato);

    Page<Registro> findRegistroByStatus(Status status, Pageable pageable);

    List<Registro> findRegistroByDocumentoCliente(String documentoCliente);

    List<Registro> findRegistroByPlacaVeiculo(String placaVeiculo);

    List<Registro> findRegistroByDataCriacaoBetween(LocalDateTime inicio, LocalDateTime fim);

}
