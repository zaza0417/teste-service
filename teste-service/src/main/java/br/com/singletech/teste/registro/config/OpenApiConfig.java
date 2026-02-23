package br.com.singletech.teste.registro.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI testeServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Teste Service API")
                        .version("1.0.0")
                        .description("API para gerenciamento de registros do treinamento Single Tech.")
                        .contact(new Contact()
                                .name("Equipe Single Tech")
                                .email("contato@singletech.com.br")
                                .url("https://singletech.com.br"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://singletech.com.br")))
                .addTagsItem(new Tag()
                        .name("Registros")
                        .description("Operacoes do CRUD de registros."));
    }
}
