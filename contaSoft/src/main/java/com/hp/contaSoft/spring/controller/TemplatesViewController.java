package com.hp.contaSoft.spring.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/templates")
public class TemplatesViewController {

    private static final Logger logger = LoggerFactory.getLogger(TemplatesViewController.class);

    /**
     * Página principal de gestión de templates
     */
    @GetMapping
    @Secured("ROLE_ANONYMOUS")
    public String templatesPage(HttpServletRequest request, Model model) {
        logger.debug("Accediendo a la página de gestión de templates");
        
        try {
            return "templates";
        } catch (Exception e) {
            logger.error("Error al cargar página de templates: ", e);
            throw e;
        }
    }
}
