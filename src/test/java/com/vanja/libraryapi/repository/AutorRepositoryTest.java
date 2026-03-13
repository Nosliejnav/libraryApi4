package com.vanja.libraryapi.repository;

import com.vanja.libraryapi.model.Autor;
import com.vanja.libraryapi.model.GeneroLivro;
import com.vanja.libraryapi.model.Livro;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
public class AutorRepositoryTest {

    @Autowired
    AutorRepository repository;

    @Autowired
    LivroRepository livroRepository;

    @Test
    public void salvarTest(){
        Autor autor = new Autor();
        autor.setNome("José");
        autor.setNacionalidade("Brasileira");
        autor.setDataNascimento(LocalDate.of(1951,1,31));

        var autoSalvo = repository.save(autor);
        System.out.println("Autor salvo: " + autoSalvo);
    }

    @Test
    public void atualizarTest(){
        var id = UUID.fromString("fed1a115-45d6-4f2d-a39c-f4d7146bdbb2");

        Optional<Autor> possivelAutor = repository.findById(id);

        if(possivelAutor.isPresent()) {

            Autor autorEcontrado = possivelAutor.get();
            System.out.println("Dados do Autor:");
            System.out.println(possivelAutor.get());

            autorEcontrado.setDataNascimento(LocalDate.of(1960, 1, 30));

            repository.save(autorEcontrado);
        }
    }

    @Test
    public void listarTest(){
        List<Autor> lista = repository.findAll();
        lista.forEach(System.out::println);
    }

    @Test
    public void countTest(){
        System.out.println("Contagem de autores: " + repository.count());
    }

    @Test
    public void deletePorIdTest(){
        var id = UUID.fromString("f594a244-4808-41ce-b716-f280095fb330");
        repository.deleteById(id);
    }

    @Test
    public void deleteTest(){
        var id = UUID.fromString("41dde997-6bee-49cd-8b36-2f924de2d155");
        var maria = repository.findById(id).get();
        repository.delete(maria);
    }

    @Test
    void salvarAutorComLivrosTest(){
        Autor autor = new Autor();
        autor.setNome("Antonio");
        autor.setNacionalidade("Americana");
        autor.setDataNascimento(LocalDate.of(1970,8,5));

        Livro livro = new Livro();
        livro.setIsbn("20887-84874");
        livro.setPreco(BigDecimal.valueOf(204));
        livro.setGenero(GeneroLivro.MISTERIO);
        livro.setTitulo("O roubo da casa assombrada");
        livro.setDataPublicacao(LocalDate.of(1999,1,2));
        livro.setAutor(autor);

        Livro livro2 = new Livro();
        livro2.setIsbn("99999-84874");
        livro2.setPreco(BigDecimal.valueOf(650));
        livro2.setGenero(GeneroLivro.MISTERIO);
        livro2.setTitulo("O roubo da casa assombrada");
        livro2.setDataPublicacao(LocalDate.of(2000,1,2));
        livro2.setAutor(autor);

        autor.setLivros(new ArrayList<>());
        autor.getLivros().add(livro);
        autor.getLivros().add(livro2);

        repository.save(autor);

//        livroRepository.saveAll(autor.getLivros());
    }
    @Test
    @Transactional
    void listarLivrosAutor(){
        var id = UUID.fromString("860a8259-6cc4-4c35-81f6-bbf35dab4246");
        var autor = repository.findById(id).get();

        // buscar os livros do autor
        List<Livro> livroslista = livroRepository.findByAutor(autor);
        autor.setLivros(livroslista);

        autor.getLivros().forEach(System.out::println);
    }
}
