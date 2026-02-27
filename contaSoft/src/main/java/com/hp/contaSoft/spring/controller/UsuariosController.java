package com.hp.contaSoft.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsuariosController {

	@GetMapping("/usuarios")
	public String showUsuarios() {
		return "usuarios";
	}
}
