package com.vanja.libraryapi.repository;

import com.vanja.libraryapi.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AutorRepository extends JpaRepository<Autor, UUID> {

    List<Autor> findByNome(String nome);
    List<Autor> findByNacionalidade(String nacionalidade);
    List<Autor> findByNomeAndNacionalidade(String nome, String nacionalidade);

    // O método anterior estava causando erro 500 pois o nome do atributo na entidade Autor é "dataNascimento",
    // mas o Spring Data JPA pode se confundir se não estiver exatamente igual.
    // Vamos garantir que o nome do método reflita exatamente os campos da entidade.
    // Na entidade Autor: nome, dataNascimento, nacionalidade.
    
    Optional<Autor> findByNomeAndDataNascimentoAndNacionalidade(
            String nome, LocalDate dataNascimento, String nacionalidade
    );

}
