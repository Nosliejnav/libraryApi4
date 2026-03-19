package com.vanja.libraryapi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginVIewController {

    @GetMapping("/login")
    public String paginaLogin(){
        return "login";
    }
}
