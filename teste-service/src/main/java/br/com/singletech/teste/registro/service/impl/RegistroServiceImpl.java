package br.com.singletech.teste.registro.service.impl;


import br.com.singletech.teste.registro.dto.request.RegistroRequest;
import br.com.singletech.teste.registro.dto.response.RegistroResponse;
import br.com.singletech.teste.registro.entity.Registro;
import br.com.singletech.teste.registro.entity.enums.Status;
import br.com.singletech.teste.registro.exception.BusinessException;
import br.com.singletech.teste.registro.mapper.RegistroMapper;
import br.com.singletech.teste.registro.repository.RegistroRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static br.com.singletech.teste.registro.entity.enums.Status.*;


@Service
@RequiredArgsConstructor
public class RegistroServiceImpl {

    private final RegistroRepository repository;
    private final RegistroMapper mapper;

    @Transactional
    public RegistroResponse criar(RegistroRequest request) {

        Optional<Registro> temReg = repository.findRegistroByNumeroContrato(request.getNumeroContrato());

        if (temReg.isPresent()) {
            throw new BusinessException(
                    "Já existe registro para o contrato: " + request.getNumeroContrato()
            );
        }

        Registro entity = mapper.toEntity(request);

        entity.setStatus(PENDENTE);
        Registro salvo = repository.save(entity);

        return mapper.toResponse(salvo);
    }

    public RegistroResponse buscarPorId(Long id) {

        Optional<Registro> temReg = repository.findById(id);

        if (temReg.isEmpty()) {
            throw new BusinessException(
                "Registro não encontrado para o id:" + id
            );
        }

        return mapper.toResponse(temReg.get());
    }


    public Page<RegistroResponse> listarTodos(Pageable pageable) {
        Page<Registro> page = repository.findAll(pageable);
        return page.map(mapper::toResponse);
    }

    public Page<RegistroResponse> listarTodosPendentes(Status status, Pageable pageable) {
        Page<Registro> page = repository.findRegistroByStatus(PENDENTE, pageable);
        return page.map(mapper::toResponse);
    }

    @Transactional
    public RegistroResponse atualizar(Long id, RegistroRequest request) {

        Optional<Registro> temReg = repository.findById(id);

        if (temReg.isEmpty()) {
            throw new BusinessException(
                    "Registro não encontrado para o id:" + id
            );
        }

        Registro entity = mapper.toEntity(request);


        entity.setValorContrato(request.getValorContrato());
        entity.setNomeCliente(request.getNomeCliente());
        entity.setDocumentoCliente(request.getDocumentoCliente());
        entity.setPlacaVeiculo(request.getPlacaVeiculo());
        entity.setDataAtualizacao(LocalDateTime.now());
        Registro salvo = repository.save(entity);

        return mapper.toResponse(salvo);
    }


    @Transactional
    public RegistroResponse atualizarStatus(Long id, Status novoStatus) {

        Registro entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Registro não encontrado para o id: " + id));

        validarTransicaoStatus(entity.getStatus(), novoStatus);
        entity.setStatus(novoStatus);
        entity.setDataAtualizacao(LocalDateTime.now());


        Registro salvo = repository.save(entity);
        return mapper.toResponse(salvo);
    }

    private void validarTransicaoStatus(Status atual, Status novo) {
        if (atual == null || novo == null) {
            throw new BusinessException("Status atual/novo não pode ser nulo.");
        }

        switch (atual) {
            case PENDENTE -> {
                if (novo != PROCESSADO && novo != Status.REJEITADO) {
                    throw new BusinessException("Transição inválida: " + atual + " -> " + novo);
                }
            }
            case PROCESSADO -> {
                if (novo != REGISTRADO && novo != Status.REJEITADO) {
                    throw new BusinessException("Transição inválida: " + atual + " -> " + novo);
                }
            }
            case REGISTRADO, REJEITADO -> throw new BusinessException(
                    "Transição inválida: registro em " + atual + " não pode ser alterado."
            );
            default -> throw new BusinessException("Status atual desconhecido: " + atual);
        }
    }

    @Transactional
    public void remover(Long id) {

        Registro entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Registro não encontrado para o id: " + id));

         repository.delete(entity);
    }



}
