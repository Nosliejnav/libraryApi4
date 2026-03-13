package com.vanja.libraryapi.repository;

import com.vanja.libraryapi.service.TransacaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TransacacoesTest {

    @Autowired
    TransacaoService transacaoService;

    /**
     * Commit -> confirmar as alterações
     * Rollback -> desfazer as alterações
     */

    @Test
    void transacaoSimples() {
        transacaoService.executar();
    }
        // salvar um livro
        // salvar o autor
        // alugar o livro
        // enviar email pro locatário
        // notificar que o livro saiu da livraria

    @Test
    void transacaoEstadoManaged() {
        transacaoService.atualizacaoSemAtualizar();
    }
}
