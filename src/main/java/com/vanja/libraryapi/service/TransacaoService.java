package com.vanja.libraryapi.service;

import com.vanja.libraryapi.model.Autor;
import com.vanja.libraryapi.model.GeneroLivro;
import com.vanja.libraryapi.model.Livro;
import com.vanja.libraryapi.repository.AutorRepository;
import com.vanja.libraryapi.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class TransacaoService {

    @Autowired
    private AutorRepository autorRepository;
    @Autowired
    private LivroRepository livroRepository;

    /// livro (titulo,..., nome_arquivo) -> id.png
    @Transactional
    public void salvarLivroComFoto(){
        //salva o livro
        //repository.save(livro);

        //pega o id do livro = livro.getID();
        //var id = livro.getId();

        //salvar foto do livro -> bucket na nuvem
        //bucketService.salvar(Livro.getFoto(), id + ".png");

        //atualizar o nome arquivo que foi salvo
        //livro.setNomeArquivoFoto(id + ".png");
        //repository.save(livro); -> Não necessario
    }

    @Transactional
    public void atualizacaoSemAtualizar(){
       var livro = livroRepository
               .findById(UUID.fromString("94dd63c6-119a-4dd8-b9da-91e267bde73a"))
                .orElse(null);

       livro.setDataPublicacao(LocalDate.of(2024,6,1));

//       livroRepository.save(livro); -> Não necessario
    }

    @Transactional
    public void executar(){
        // salva o autor
        Autor autor = new Autor();
        autor.setNome("Teste Francisco");
        autor.setNacionalidade("Brasileira");
        autor.setDataNascimento(LocalDate.of(1951,1,31));

        autorRepository.save(autor);

        // salva o livro
        Livro livro = new Livro();
        livro.setIsbn("90887-84874");
        livro.setPreco(BigDecimal.valueOf(100));
        livro.setGenero(GeneroLivro.FICCAO);
        livro.setTitulo("Teste Livro do Francisca");
        livro.setDataPublicacao(LocalDate.of(1980,1,2));

        livro.setAutor(autor);

        livroRepository.save(livro);

        if (autor.getNome().equals("Teste Francisco")){
            throw new RuntimeException("Rollback!");
        }
    }
}
