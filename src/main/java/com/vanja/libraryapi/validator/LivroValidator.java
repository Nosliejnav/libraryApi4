package com.vanja.libraryapi.validator;

import com.vanja.libraryapi.exceptions.CampoInvalidoException;
import com.vanja.libraryapi.exceptions.RegistroDuplicadoException;
import com.vanja.libraryapi.model.Livro;
import com.vanja.libraryapi.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LivroValidator {

    private static final int ANO_EXIGENCIA_PRECO = 2020;

    private final LivroRepository repository;

    public void validar(Livro livro){
        if (existeLivroComIsbn(livro)) {
            throw new RegistroDuplicadoException("ISBN já cadastrado!");
        }

        if (isPrecoObrigatorioNulo(livro)) {
            throw new CampoInvalidoException("preço", "Para livros com ano de publicação a partir de 2020, o preço é obrigatório.");
        }
    }

    private boolean isPrecoObrigatorioNulo(Livro livro){
        return livro.getPreco() == null &&
                livro.getDataPublicacao() != null && // Verificação de segurança adicionada
                livro.getDataPublicacao().getYear() >= ANO_EXIGENCIA_PRECO;
    }

    public boolean existeLivroComIsbn(Livro livro){
        Optional<Livro> livroEncotrado = repository.findByIsbn(livro.getIsbn());

        if (livro.getId() == null){
            return livroEncotrado.isPresent();
        }

        return livroEncotrado
                .map(Livro::getId)
                .stream()
                .anyMatch(id -> !id.equals(livro.getId()));
    }
}
