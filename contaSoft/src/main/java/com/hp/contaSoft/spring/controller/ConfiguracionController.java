package com.hp.contaSoft.spring.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hp.contaSoft.hibernate.dao.repositories.AFPFactorsRepository;

@Controller
@RequestMapping("/configuracion")
public class ConfiguracionController {

    private static final Logger logger = LoggerFactory.getLogger(ConfiguracionController.class);

    @Autowired
    private AFPFactorsRepository afpFactorsRepository;

    /**
     * Página principal de configuraciones del sistema
     */
    @GetMapping
    @Secured("ROLE_ANONYMOUS")
    public String configuracionPage(HttpServletRequest request, Model model) {
        logger.debug("Accediendo a la página de configuraciones del sistema");
        
        try {
            // Los datos se cargarán vía AJAX desde el frontend
            return "configuracion";
        } catch (Exception e) {
            logger.error("Error al cargar página de configuraciones: ", e);
            throw e;
        }
    }
}
