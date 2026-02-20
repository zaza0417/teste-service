package br.com.singletech.teste.registro.controller;


import br.com.singletech.teste.registro.dto.request.RegistroRequest;
import br.com.singletech.teste.registro.dto.response.RegistroResponse;
import br.com.singletech.teste.registro.entity.enums.Status;
import br.com.singletech.teste.registro.service.impl.RegistroServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/registros")
@RequiredArgsConstructor
public class RegistroController {

    private final RegistroServiceImpl registroService;


    @PostMapping
    public ResponseEntity<RegistroResponse> criar(@RequestBody @Valid RegistroRequest request) {
        RegistroResponse response = registroService.criar(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<RegistroResponse> buscarPorId(@PathVariable Long id) {
        RegistroResponse response = registroService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<Page<RegistroResponse>> listar(
            @RequestParam(required = false) Status status,
            Pageable pageable) {

        Page<RegistroResponse> page = (status != null)
                ? registroService.listarTodosPendentes(status, pageable)
                : registroService.listarTodos(pageable);

        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistroResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid RegistroRequest request) {

        RegistroResponse response = registroService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<RegistroResponse> atualizarStatus(
            @PathVariable Long id,
            @RequestParam Status status) {

        RegistroResponse response = registroService.atualizarStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        registroService.remover(id);
        return ResponseEntity.noContent().build();
    }
}