package br.com.singletech.teste.registro.service;


import br.com.singletech.teste.registro.dto.request.RegistroRequest;
import br.com.singletech.teste.registro.dto.response.RegistroResponse;
import br.com.singletech.teste.registro.entity.Registro;
import br.com.singletech.teste.registro.mapper.RegistroMapper;
import br.com.singletech.teste.registro.repository.RegistroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
public class RegistroService {

    private final RegistroRepository repository;
    private final RegistroMapper mapper;

    public RegistroResponse criar(RegistroRequest request) {

        Registro entity = mapper.toEntity(request);

        Registro salvo = repository.save(entity);

        return mapper.toResponse(salvo);
    }
}
