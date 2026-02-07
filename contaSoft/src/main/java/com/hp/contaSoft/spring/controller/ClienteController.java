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

import com.hp.contaSoft.hibernate.dao.repositories.TaxpayerRepository;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    @Autowired
    private TaxpayerRepository taxpayerRepository;

    /**
     * Página principal de gestión de clientes
     */
    @GetMapping
    @Secured("ROLE_ANONYMOUS")
    public String clientesPage(HttpServletRequest request, Model model) {
        logger.debug("Accediendo a la página de gestión de clientes");
        
        try {
            // Podríamos cargar datos iniciales si es necesario
            // pero en este caso lo haremos vía AJAX desde el frontend
            
            return "clientes";
        } catch (Exception e) {
            logger.error("Error al cargar página de clientes: ", e);
            throw e;
        }
    }
}
