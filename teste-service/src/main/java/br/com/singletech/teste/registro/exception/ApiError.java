package br.com.singletech.teste.registro.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {


    private LocalDateTime timestamp;


    private String type;
    private String title;
    private Integer status;
    private String detail;
    private String instance;


    private List<FieldErrorDetail> errors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldErrorDetail {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}