package com.hp.contaSoft.spring.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/procesados")
public class ProcessadosController {

	private static final Logger logger = LoggerFactory.getLogger(ProcessadosController.class);

	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
	@Secured("ROLE_ANONYMOUS")
	public String procesadosPage(@RequestParam(required = false) Long taxpayerId, HttpServletRequest request, Model model) {
		logger.debug("Accediendo a la página de procesados");
		if (taxpayerId != null) {
			model.addAttribute("selectedTaxpayerId", taxpayerId);
		}
		return "procesados";
	}
}
