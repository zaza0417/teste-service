package br.com.singletech.teste.registro.controller;


import br.com.singletech.teste.registro.exception.ApiError;
import br.com.singletech.teste.registro.dto.request.RegistroRequest;
import br.com.singletech.teste.registro.dto.response.RegistroResponse;
import br.com.singletech.teste.registro.entity.enums.Status;
import br.com.singletech.teste.registro.service.impl.RegistroServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/registros")
@RequiredArgsConstructor
@Tag(name = "Registros", description = "Operacoes do CRUD de registros.")
public class RegistroController {

    private final RegistroServiceImpl registroService;


    @PostMapping
    @Operation(
            summary = "Criar registro",
            description = "Cria um novo registro. O status inicial e definido como PENDENTE e a resposta inclui o header Location.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Dados para criacao do registro.",
                    content = @Content(schema = @Schema(implementation = RegistroRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Registro criado",
                    headers = @Header(name = "Location", description = "URI do recurso criado"),
                    content = @Content(schema = @Schema(implementation = RegistroResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Erro de validacao",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflito de dados (ex.: contrato ja cadastrado)",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
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
    @Operation(
            summary = "Buscar registro por ID",
            description = "Busca um registro pelo seu identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Registro encontrado",
                    content = @Content(schema = @Schema(implementation = RegistroResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parametro invalido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Registro nao encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public ResponseEntity<RegistroResponse> buscarPorId(
            @Parameter(description = "ID do registro.", example = "1", required = true)
            @PathVariable Long id) {
        RegistroResponse response = registroService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    @Operation(
            summary = "Listar registros",
            description = "Retorna uma lista paginada de registros. Quando o parametro 'status' for informado, filtra pelo status."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista paginada de registros"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parametro invalido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public ResponseEntity<Page<RegistroResponse>> listar(
            @Parameter(
                    description = "Filtra por status. Se ausente, retorna todos.",
                    schema = @Schema(implementation = Status.class)
            )
            @RequestParam(required = false) Status status,
            @ParameterObject Pageable pageable) {

        Page<RegistroResponse> page = (status != null)
                ? registroService.listarTodosPendentes(status, pageable)
                : registroService.listarTodos(pageable);

        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar registro",
            description = "Atualiza os dados de um registro existente.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Dados para atualizacao do registro.",
                    content = @Content(schema = @Schema(implementation = RegistroRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Registro atualizado",
                    content = @Content(schema = @Schema(implementation = RegistroResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Erro de validacao",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Registro nao encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflito de dados",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public ResponseEntity<RegistroResponse> atualizar(
            @Parameter(description = "ID do registro.", example = "1", required = true)
            @PathVariable Long id,
            @RequestBody @Valid RegistroRequest request) {

        RegistroResponse response = registroService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Atualizar status do registro",
            description = "Atualiza apenas o status do registro, validando regras de transicao."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Status atualizado",
                    content = @Content(schema = @Schema(implementation = RegistroResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parametro invalido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Registro nao encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Transicao de status invalida",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public ResponseEntity<RegistroResponse> atualizarStatus(
            @Parameter(description = "ID do registro.", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(
                    description = "Novo status do registro.",
                    required = true,
                    schema = @Schema(implementation = Status.class)
            )
            @RequestParam Status status) {

        RegistroResponse response = registroService.atualizarStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Remover registro",
            description = "Remove um registro pelo seu identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro removido"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Registro nao encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public ResponseEntity<Void> remover(
            @Parameter(description = "ID do registro.", example = "1", required = true)
            @PathVariable Long id) {
        registroService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
