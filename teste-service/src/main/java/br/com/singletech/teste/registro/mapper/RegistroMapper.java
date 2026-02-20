package br.com.singletech.teste.registro.mapper;


import br.com.singletech.teste.registro.dto.request.RegistroRequest;
import br.com.singletech.teste.registro.dto.response.RegistroResponse;
import br.com.singletech.teste.registro.entity.Registro;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RegistroMapper {

    Registro toEntity(RegistroRequest request);

    RegistroResponse toResponse(Registro entity);

    List<RegistroResponse> toResponseList(List<Registro> entities);


}
