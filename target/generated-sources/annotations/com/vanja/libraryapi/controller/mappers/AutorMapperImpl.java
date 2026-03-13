package com.vanja.libraryapi.controller.mappers;

import com.vanja.libraryapi.controller.dto.AutorDTO;
import com.vanja.libraryapi.model.Autor;
import java.time.LocalDate;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-13T17:04:18-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.6 (Amazon.com Inc.)"
)
@Component
public class AutorMapperImpl implements AutorMapper {

    @Override
    public Autor toEntity(AutorDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Autor autor = new Autor();

        autor.setNome( dto.nome() );
        autor.setDataNascimento( dto.dataNascimento() );
        autor.setNacionalidade( dto.nacionalidade() );
        autor.setId( dto.id() );

        return autor;
    }

    @Override
    public AutorDTO toDTO(Autor autor) {
        if ( autor == null ) {
            return null;
        }

        UUID id = null;
        String nome = null;
        LocalDate dataNascimento = null;
        String nacionalidade = null;

        id = autor.getId();
        nome = autor.getNome();
        dataNascimento = autor.getDataNascimento();
        nacionalidade = autor.getNacionalidade();

        AutorDTO autorDTO = new AutorDTO( id, nome, dataNascimento, nacionalidade );

        return autorDTO;
    }
}
