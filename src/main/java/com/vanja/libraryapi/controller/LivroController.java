package com.vanja.libraryapi.controller;

import com.vanja.libraryapi.controller.dto.CadastroLivroDTO;
import com.vanja.libraryapi.controller.dto.ErroResposta;
import com.vanja.libraryapi.controller.dto.ResultadoPesquisaLivroDTO;
import com.vanja.libraryapi.controller.mappers.LivroMapper;
import com.vanja.libraryapi.exceptions.RegistroDuplicadoException;
import com.vanja.libraryapi.model.GeneroLivro;
import com.vanja.libraryapi.model.Livro;
import com.vanja.libraryapi.service.LivroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("livros")
@RequiredArgsConstructor
public class LivroController implements GenericController {

    private final LivroService service;
    private final LivroMapper mapper;

    @PostMapping
    public ResponseEntity<Void> salvar(@RequestBody @Valid CadastroLivroDTO dto) {

        // mapear dto para entidade
        Livro livro = mapper.toEntity(dto);
        // enviar a entidade para o serviçe validar e salvar na base
        service.salvar(livro);
        // criar url para acesso dos dados do livro
        var url = gerarHeaderLocation(livro.getId());
        // retornar codigo created com header location
        return ResponseEntity.created(url).build();
    }

    @GetMapping("{id}")
    public ResponseEntity<ResultadoPesquisaLivroDTO> obterDetalhes(
            @PathVariable("id") String id){
        return service.obterPorId(UUID.fromString(id))
                .map(livro -> {
                    var dto = mapper.toDTO(livro);
                    return ResponseEntity.ok(dto);
                }).orElseGet(() -> ResponseEntity.notFound().build() );
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable("id") String id){
        return service.obterPorId(UUID.fromString(id))
                .map(livro -> {
                    service.deletar(livro);
                    return ResponseEntity.noContent().build();
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<ResultadoPesquisaLivroDTO>> pesquisa(
            @RequestParam(value = "isbn", required = false )
            String isbn,
            @RequestParam(value = "titulo", required = false )
            String titulo,
            @RequestParam(value = "nome-autor", required = false )
            String nomeAutor,
            @RequestParam(value = "genero", required = false )
            GeneroLivro genero,
            @RequestParam(value = "ano-Publicacao", required = false )
            Integer anoPublicacao,
            @RequestParam(value = "pagina", defaultValue = "0")
            Integer pagina,
            @RequestParam(value = "tamanho-pagina", defaultValue = "10")
            Integer tamanhoPagina
    ){
        Page<Livro> paginaResultado = service.pesquisa(
                isbn, titulo, nomeAutor, genero, anoPublicacao, pagina, tamanhoPagina);

        Page<ResultadoPesquisaLivroDTO> resultado = paginaResultado.map(mapper::toDTO);

        return ResponseEntity.ok(resultado);
    }

    @PutMapping
    public ResponseEntity<Object> atualizar(
            @PathVariable("id") String id,@RequestBody @Valid CadastroLivroDTO dto){
        return service.obterPorId(UUID.fromString(id))
                .map(livro -> {
                    Livro entidadeAux = mapper.toEntity(dto);

                    livro.setDataPublicacao(entidadeAux.getDataPublicacao());
                    livro.setIsbn(entidadeAux.getIsbn());
                    livro.setPreco(entidadeAux.getPreco());
                    livro.setGenero(entidadeAux.getGenero());
                    livro.setTitulo(entidadeAux.getTitulo());
                    livro.setAutor(entidadeAux.getAutor());

                    service.atualizar(livro);

                    return ResponseEntity.noContent().build();

                }).orElseGet(() -> ResponseEntity.notFound().build() );
    }
}
