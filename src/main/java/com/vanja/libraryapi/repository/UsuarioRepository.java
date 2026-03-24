package com.vanja.libraryapi.repository;

import com.vanja.libraryapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {


    Usuario findByLogin(String login);
}
