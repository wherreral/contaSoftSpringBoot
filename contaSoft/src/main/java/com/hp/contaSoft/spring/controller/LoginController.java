package com.hp.contaSoft.spring.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
    
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    /**
     * Endpoint de logout: borra la cookie HttpOnly JWT_TOKEN desde el servidor.
     * JavaScript no puede borrar cookies HttpOnly, por eso es necesario este endpoint.
     */
    @PostMapping("/api/auth/logout")
    @ResponseBody
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("JWT_TOKEN", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("message", "Sesión cerrada");
        return ResponseEntity.ok(body);
    }
}
