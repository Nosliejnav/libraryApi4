package com.vanja.libraryapi.controller.mappers;

import com.vanja.libraryapi.controller.dto.CadastroLivroDTO;
import com.vanja.libraryapi.controller.dto.ResultadoPesquisaLivroDTO;
import com.vanja.libraryapi.model.Livro;
import com.vanja.libraryapi.repository.AutorRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = AutorMapper.class )
public abstract class LivroMapper {

    @Autowired
    AutorRepository autorRepository;

    @Mapping(target = "autor", expression = "java( autorRepository.findById(dto.idAutor()).orElse(null) )")
    public abstract Livro toEntity(CadastroLivroDTO dto);

    public abstract ResultadoPesquisaLivroDTO toDTO(Livro livro);
}
