package br.com.singletech.teste.registro.service.impl;

import br.com.singletech.teste.registro.dto.request.RegistroRequest;
import br.com.singletech.teste.registro.dto.response.RegistroResponse;
import br.com.singletech.teste.registro.entity.Registro;
import br.com.singletech.teste.registro.entity.enums.Status;
import br.com.singletech.teste.registro.exception.BusinessException;
import br.com.singletech.teste.registro.exception.RegistroExistenteException;
import br.com.singletech.teste.registro.exception.StatusException;
import br.com.singletech.teste.registro.mapper.RegistroMapper;
import br.com.singletech.teste.registro.repository.RegistroRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static br.com.singletech.teste.registro.entity.enums.Status.PENDENTE;
import static br.com.singletech.teste.registro.entity.enums.Status.PROCESSADO;
import static br.com.singletech.teste.registro.entity.enums.Status.REGISTRADO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistroServiceImplTest {

    // Mock da camada de persistencia:
    // usamos para simular respostas do banco sem acessar banco real.
    @Mock
    private RegistroRepository repository;

    // Mock do mapper:
    // controla a conversao entity <-> dto durante os testes.
    @Mock
    private RegistroMapper mapper;

    // Classe real sob teste.
    // O Mockito injeta automaticamente os mocks acima nela.
    @InjectMocks
    private RegistroServiceImpl service;

    // @Nested organiza os testes por comportamento/caso de uso.
    // Isso melhora a leitura dos cenarios no relatorio.
    @Nested
    class Cadastro {

        @Test
        void deveCadastrarComSucesso() {
            // Arrange: monta dados de entrada e de saida esperados.
            RegistroRequest request = Fixture.requestValido();
            Registro entity = Fixture.entityNovo();
            Registro salvo = Fixture.entityPersistido(1L, PENDENTE);
            RegistroResponse response = Fixture.response(1L, PENDENTE);

            // when(...).thenReturn(...):
            // define comportamento esperado dos mocks para este cenario.
            when(repository.findRegistroByNumeroContrato(request.getNumeroContrato())).thenReturn(Optional.empty());
            when(mapper.toEntity(request)).thenReturn(entity);
            when(repository.save(entity)).thenReturn(salvo);
            when(mapper.toResponse(salvo)).thenReturn(response);

            // Act: executa o metodo real que queremos testar.
            RegistroResponse resultado = service.criar(request);

            // Assert: valida o resultado retornado.
            // assertEquals compara o valor esperado com o valor real.
            assertEquals(1L, resultado.getId());
            assertEquals(PENDENTE, resultado.getStatus());

            // verify(...) confirma se as colaboracoes esperadas ocorreram.
            verify(repository).findRegistroByNumeroContrato(request.getNumeroContrato());
            verify(repository).save(entity);
            verify(mapper).toResponse(salvo);
        }

        @Test
        void deveLancarExcecaoQuandoCadastroComPlacaDuplicada() {
            RegistroRequest request = Fixture.requestValido();
            Registro existente = Fixture.entityPersistido(99L, PENDENTE);

            when(repository.findRegistroByNumeroContrato(request.getNumeroContrato())).thenReturn(Optional.of(existente));

            // assertThrows valida que o metodo lancou a excecao esperada.
            assertThrows(RegistroExistenteException.class, () -> service.criar(request));

            verify(repository).findRegistroByNumeroContrato(request.getNumeroContrato());

            // never() garante que essas operacoes NAO foram chamadas.
            verify(repository, never()).save(any());
            verify(mapper, never()).toEntity(any());
        }
    }

    @Nested
    class BuscaPorId {

        @Test
        void deveBuscarPorIdExistente() {
            Long id = 10L;
            Registro entity = Fixture.entityPersistido(id, PENDENTE);
            RegistroResponse response = Fixture.response(id, PENDENTE);

            when(repository.findById(id)).thenReturn(Optional.of(entity));
            when(mapper.toResponse(entity)).thenReturn(response);

            RegistroResponse resultado = service.buscarPorId(id);

            assertEquals(id, resultado.getId());
            verify(repository).findById(id);
            verify(mapper).toResponse(entity);
        }

        @Test
        void deveLancarExcecaoQuandoIdInexistente() {
            Long id = 404L;
            when(repository.findById(id)).thenReturn(Optional.empty());

            assertThrows(BusinessException.class, () -> service.buscarPorId(id));

            verify(repository).findById(id);
            verify(mapper, never()).toResponse(any());
        }
    }

    @Nested
    class AtualizacaoStatus {

        @Test
        void deveAtualizarStatusComTransicaoValida() {
            Long id = 20L;
            Registro entity = Fixture.entityPersistido(id, PENDENTE);
            Registro salvo = Fixture.entityPersistido(id, PROCESSADO);
            RegistroResponse response = Fixture.response(id, PROCESSADO);

            when(repository.findById(id)).thenReturn(Optional.of(entity));
            when(repository.save(entity)).thenReturn(salvo);
            when(mapper.toResponse(salvo)).thenReturn(response);

            RegistroResponse resultado = service.atualizarStatus(id, PROCESSADO);

            assertEquals(PROCESSADO, resultado.getStatus());
            verify(repository).findById(id);
            verify(repository).save(entity);
            verify(mapper).toResponse(salvo);
        }

        @Test
        void deveLancarExcecaoQuandoTransicaoDeStatusInvalida() {
            Long id = 21L;
            Registro entity = Fixture.entityPersistido(id, PENDENTE);
            when(repository.findById(id)).thenReturn(Optional.of(entity));

            assertThrows(StatusException.class, () -> service.atualizarStatus(id, REGISTRADO));

            verify(repository).findById(id);
            verify(repository, never()).save(any());
            verify(mapper, never()).toResponse(any());
        }
    }

    // Fixture centraliza massa de dados de teste.
    // Evita duplicacao de codigo e deixa os testes mais focados no comportamento.
    private static final class Fixture {

        private static RegistroRequest requestValido() {
            RegistroRequest request = new RegistroRequest();
            request.setNumeroContrato("CT-2026-0001");
            request.setValorContrato(new BigDecimal("1500.50"));
            request.setNomeCliente("Joao da Silva");
            request.setDocumentoCliente("12345678901");
            request.setPlacaVeiculo("ABC1D23");
            return request;
        }

        private static Registro entityNovo() {
            Registro entity = new Registro();
            entity.setNumeroContrato("CT-2026-0001");
            entity.setValorContrato(new BigDecimal("1500.50"));
            entity.setNomeCliente("Joao da Silva");
            entity.setDocumentoCliente("12345678901");
            entity.setPlacaVeiculo("ABC1D23");
            return entity;
        }

        private static Registro entityPersistido(Long id, Status status) {
            Registro entity = entityNovo();
            entity.setId(id);
            entity.setStatus(status);
            entity.setDataCriacao(LocalDateTime.now().minusDays(1));
            entity.setDataAtualizacao(LocalDateTime.now());
            return entity;
        }

        private static RegistroResponse response(Long id, Status status) {
            RegistroResponse response = new RegistroResponse();
            response.setId(id);
            response.setNumeroContrato("CT-2026-0001");
            response.setValorContrato(new BigDecimal("1500.50"));
            response.setNomeCliente("Joao da Silva");
            response.setDocumentoCliente("12345678901");
            response.setPlacaVeiculo("ABC1D23");
            response.setStatus(status);
            response.setDataCriacao(LocalDateTime.now().minusDays(1));
            response.setDataAtualizacao(LocalDateTime.now());
            return response;
        }
    }
}
