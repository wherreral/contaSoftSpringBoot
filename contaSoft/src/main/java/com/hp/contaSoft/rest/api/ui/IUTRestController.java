package com.hp.contaSoft.rest.api.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hp.contaSoft.hibernate.dao.repositories.IUTRepository;
import com.hp.contaSoft.hibernate.entities.IUT;

@RestController
@RequestMapping(value = "/api/ui/iut", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class IUTRestController {

	private static final Logger logger = LoggerFactory.getLogger(IUTRestController.class);

	@Autowired
	private IUTRepository iutRepository;

	public IUTRestController() {
		logger.info("=== IUTRestController INITIALIZED ===");
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getAllIUT() {
		try {
			logger.info("Obteniendo lista de IUT");
			List<IUT> list = (List<IUT>) iutRepository.findAll();

			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("data", list);
			response.put("total", list.size());

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("Error al obtener IUT: ", e);
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Error al obtener IUT: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> getIUTById(@PathVariable Long id) {
		try {
			logger.info("Obteniendo IUT con ID: {}", id);
			IUT item = iutRepository.findById(id).orElse(null);
			if (item == null) {
				Map<String, Object> errorResponse = new HashMap<>();
				errorResponse.put("success", false);
				errorResponse.put("message", "IUT no encontrado");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
			}
			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("data", item);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("Error al obtener IUT: ", e);
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Error al obtener IUT: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}
}
