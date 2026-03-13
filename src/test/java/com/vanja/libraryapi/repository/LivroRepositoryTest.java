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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
class LivroRepositoryTest {

    @Autowired
    LivroRepository repository;

    @Autowired
    AutorRepository autorRepository;

    @Test
    void salvarTest(){
        Livro livro = new Livro();
        livro.setIsbn("90887-84874");
        livro.setPreco(BigDecimal.valueOf(100));
        livro.setGenero(GeneroLivro.CIENCIA);
        livro.setTitulo("Ciencias");
        livro.setDataPublicacao(LocalDate.of(1980,1,2));

        Autor autor = autorRepository
                .findById(UUID.fromString("87b8a130-fa6f-4b50-beb7-5b2d6aa2d54a"))
                .orElse(null);

        // Buscando um autor já existente no banco de dados para associar ao livro
        livro.setAutor(autor);

        repository.save(livro);
    }

    @Test
    void salvarAutorELivroTest(){
        Livro livro = new Livro();
        livro.setIsbn("90887-84874");
        livro.setPreco(BigDecimal.valueOf(100));
        livro.setGenero(GeneroLivro.FICCAO);
        livro.setTitulo("Terceiro Livro");
        livro.setDataPublicacao(LocalDate.of(1980,1,2));

        Autor autor = new Autor();
        autor.setNome("José");
        autor.setNacionalidade("Brasileira");
        autor.setDataNascimento(LocalDate.of(1951,1,31));

        autorRepository.save(autor);

        livro.setAutor(autor);

        repository.save(livro);
    }

    @Test
    void salvarCascadeTest(){
        Livro livro = new Livro();
        livro.setIsbn("90887-84874");
        livro.setPreco(BigDecimal.valueOf(100));
        livro.setGenero(GeneroLivro.FICCAO);
        livro.setTitulo("Outro Livro");
        livro.setDataPublicacao(LocalDate.of(1980,1,2));

        Autor autor = new Autor();
        autor.setNome("João");
        autor.setNacionalidade("Brasileira");
        autor.setDataNascimento(LocalDate.of(1951,1,31));

        livro.setAutor(autor);

        repository.save(livro);
    }

    @Test
    void atualizarAutorDoLivro(){
        UUID id = UUID.fromString("41dde997-6bee-49cd-8b36-2f924de2d155");
        var livroParaAtualizar = repository.findById(id).orElse(null);

        UUID idAutor = UUID.fromString("fed1a115-45d6-4f2d-a39c-f4d7146bdbb2");
        Autor maria = autorRepository.findById(idAutor).orElse(null);

        livroParaAtualizar.setAutor(maria);

        repository.save(livroParaAtualizar);
    }

    @Test
    void deletar(){
        UUID id = UUID.fromString("aaabef83-f65f-435c-aad2-6347ee9e3e54");
        repository.deleteById(id);
    }

    @Test
    void deletarCascade(){
        UUID id = UUID.fromString("e7a42287-40af-4194-a0e8-435e8b8642ed");
        repository.deleteById(id);
    }

    @Test
    @Transactional
    void buscarLivroTest(){
        UUID id = UUID.fromString("13e9fe02-f76a-4cbb-b37d-33cfa3a88a76");
        Livro livro = repository.findById(id).orElse(null);
        System.out.println("Livro:");
        System.out.println(livro.getTitulo());

//        System.out.println("Autor:");
//        System.out.println(livro.getAutor().getNome());
    }
    @Test
    void pesquisaPorTituloTest(){
        List<Livro> lista = repository.findByTitulo("O roubo da casa assombrada");
        lista.forEach(System.out::println);
    }

    @Test
    void pesquisaPorISBNTest(){
//        List<Livro> lista = repository.findByIsbn("20887-84874");
//        lista.forEach(System.out::println);

        Optional<Livro> livro = repository.findByIsbn("20887-84874");
        livro.ifPresent(System.out::println );
    }

    @Test
    void pesquisaPorTituloEPrecoTest(){
        var preco = BigDecimal.valueOf(204.00);
        var tituloPesquisa  = "O roubo da casa assombrada";

        List<Livro> lista = repository.findByTituloAndPreco(tituloPesquisa , preco );
        lista.forEach(System.out::println);
    }

    @Test
    void listarLivroComQueryJPQL(){
        var resultado = repository.listarTodosOrdenadosPorTituloAndPreco();
        resultado.forEach(System.out::println);
    }

    @Test
    void listarAutoresDosLivros(){
        var resultado = repository.listarAutorDosLivros();
        resultado.forEach(System.out::println);
    }

    @Test
    void listarTitulosNaoRepetidosDosLivros(){
        var resultado = repository.listarNomesDiferentesLivros();
        resultado.forEach(System.out::println);
    }

    @Test
    void listarGenerosDelivrosAutoresBrasileiros(){
        var resultado = repository.listarGenerosAutoresBrasileiros();
        resultado.forEach(System.out::println);
    }

    @Test
    void listarPorGeneroQueryParamTest(){
        var resultado = repository.findByGenero(GeneroLivro.MISTERIO, "preco");
        resultado.forEach(System.out::println);
    }

    @Test
    void listarPorGeneroPositionalParamTest(){
        var resultado = repository.findByGeneroPositionalParameters("preco",GeneroLivro.MISTERIO);
        resultado.forEach(System.out::println);
    }

    @Test
    void deletePorGeneroTest(){
        repository.deleteByGenero(GeneroLivro.CIENCIA);
    }

    @Test
    void updateDataPublicacaoTest(){
        repository.updateDataPublicacao(LocalDate.of(2000,1,1));
    }
}