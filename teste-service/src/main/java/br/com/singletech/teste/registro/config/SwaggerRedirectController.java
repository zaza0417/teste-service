package br.com.singletech.teste.registro.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerRedirectController {

    @GetMapping({"/swagger-ui.html", "/swagger/"})
    public String redirectToSwagger() {
        return "redirect:/swagger";
    }
}
