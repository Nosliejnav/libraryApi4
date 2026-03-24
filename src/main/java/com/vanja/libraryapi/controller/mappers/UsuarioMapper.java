package com.vanja.libraryapi.controller.mappers;

import com.vanja.libraryapi.controller.dto.UsuarioDTO;
import com.vanja.libraryapi.model.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    Usuario toEntity(UsuarioDTO dto);
}
