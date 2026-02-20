package br.com.singletech.teste.registro.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleRegistroNotFound(
            BusinessException ex,
            HttpServletRequest request) {

        log.warn("Recurso não encontrado: {}", ex.getMessage());

        ApiError error = ApiError.builder()
                .type("https://api.singletech.com.br/erros/recurso-nao-encontrado")
                .title("Recurso não encontrado")
                .status(HttpStatus.NOT_FOUND.value())
                .detail(ex.getMessage())
                .instance(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }


    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleRegistroJaExiste(
            BusinessException ex,
            HttpServletRequest request) {

        log.warn("Conflito de recurso: {}", ex.getMessage());

        ApiError error = ApiError.builder()
                .type("https://api.singletech.com.br/erros/conflito")
                .title("Recurso já existe")
                .status(HttpStatus.CONFLICT.value())
                .detail(ex.getMessage())
                .instance(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleTransicaoInvalida(
            BusinessException ex,
            HttpServletRequest request) {

        log.warn("Transição de status inválida: {}", ex.getMessage());

        ApiError error = ApiError.builder()
                .type("https://api.singletech.com.br/erros/transicao-invalida")
                .title("Transição de status inválida")
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .detail(ex.getMessage())
                .instance(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        // Percorre todos os erros de campo e os transforma em FieldErrorDetail
        List<ApiError.FieldErrorDetail> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> ApiError.FieldErrorDetail.builder()
                        .field(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .rejectedValue(fieldError.getRejectedValue())
                        .build())
                .toList();

        log.debug("Erro de validação em {}: {} campos inválidos",
                request.getRequestURI(), fieldErrors.size());

        ApiError error = ApiError.builder()
                .type("https://api.singletech.com.br/erros/dados-invalidos")
                .title("Dados inválidos")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail("Um ou mais campos estão com valores inválidos. Verifique o campo 'errors'.")
                .instance(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .errors(fieldErrors) // Lista detalhada dos campos com problema
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {

        log.warn("Método não suportado: {} em {}", ex.getMethod(), request.getRequestURI());

        ApiError error = ApiError.builder()
                .type("https://api.singletech.com.br/erros/metodo-nao-suportado")
                .title("Método não suportado")
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .detail("O método " + ex.getMethod() + " não é suportado para este endpoint.")
                .instance(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }


    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request) {

        log.warn("Media type não suportado: {}", ex.getContentType());

        ApiError error = ApiError.builder()
                .type("https://api.singletech.com.br/erros/media-type-invalido")
                .title("Tipo de mídia não suportado")
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .detail("O Content-Type '" + ex.getContentType() + "' não é suportado. Use 'application/json'.")
                .instance(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParam(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {

        log.warn("Parâmetro ausente: {}", ex.getParameterName());

        ApiError error = ApiError.builder()
                .type("https://api.singletech.com.br/erros/parametro-ausente")
                .title("Parâmetro obrigatório ausente")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail("O parâmetro '" + ex.getParameterName() + "' é obrigatório e não foi informado.")
                .instance(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        log.warn("Tipo incompatível para o parâmetro '{}': valor recebido '{}'",
                ex.getName(), ex.getValue());

        ApiError error = ApiError.builder()
                .type("https://api.singletech.com.br/erros/tipo-invalido")
                .title("Tipo de parâmetro inválido")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail("O parâmetro '" + ex.getName() + "' recebeu o valor '" +
                        ex.getValue() + "', que não é do tipo esperado.")
                .instance(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        // log.error loga a exception completa (incluindo stack trace) nos logs do servidor
        log.error("Erro inesperado na requisição {}: ", request.getRequestURI(), ex);

        ApiError error = ApiError.builder()
                .type("https://api.singletech.com.br/erros/erro-interno")
                .title("Erro interno do servidor")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .detail("Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.")
                .instance(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}