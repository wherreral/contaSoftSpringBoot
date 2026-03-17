package com.hp.contaSoft.rest.api.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.hp.contaSoft.custom.CurrentUser;
import com.hp.contaSoft.excel.entities.PayBookDetailProcessed;
import com.hp.contaSoft.hibernate.dao.repositories.PayBookDetailProcessedRepository;
import com.hp.contaSoft.hibernate.dao.repositories.PayBookProcessedRepository;
import com.hp.contaSoft.hibernate.dao.service.ProcessingService;
import com.hp.contaSoft.hibernate.entities.PayBookProcessed;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class ProcessedRestController {

	private static final Logger logger = LoggerFactory.getLogger(ProcessedRestController.class);

	@Autowired
	private PayBookProcessedRepository payBookProcessedRepository;

	@Autowired
	private PayBookDetailProcessedRepository payBookDetailProcessedRepository;

	@Autowired
	private ProcessingService processingService;

	/**
	 * Listar todos los procesados por familyId, opcionalmente filtrado por taxpayerId
	 */
	@GetMapping(value = "/api/ui/procesados", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getAllProcessed(
			@RequestParam(required = false) Long taxpayerId,
			Authentication authentication) {
		try {
			if (authentication == null || !(authentication.getPrincipal() instanceof CurrentUser)) {
				Map<String, Object> response = new HashMap<>();
				response.put("success", true);
				response.put("data", new ArrayList<>());
				response.put("total", 0);
				return ResponseEntity.ok(response);
			}

			CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
			String familyId = currentUser.getFamilId();

			List<PayBookProcessed> list;
			if (taxpayerId != null) {
				list = payBookProcessedRepository.findAllByFamilyIdAndTaxpayerId(familyId, taxpayerId);
			} else {
				list = payBookProcessedRepository.findAllByFamilyId(familyId);
			}

			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("data", list);
			response.put("total", list.size());
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			logger.error("Error al obtener procesados: ", e);
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Error: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	/**
	 * Obtener detalles de un procesado
	 */
	@GetMapping(value = "/api/ui/procesados/{id}/details", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getProcessedDetails(
			@PathVariable Long id,
			Authentication authentication) {
		try {
			if (authentication == null || !(authentication.getPrincipal() instanceof CurrentUser)) {
				Map<String, Object> response = new HashMap<>();
				response.put("success", false);
				response.put("message", "No autenticado");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
			}

			CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
			String familyId = currentUser.getFamilId();

			// Validate parent record belongs to user's tenant
			PayBookProcessed processed = payBookProcessedRepository.findByIdAndFamilyId(id, familyId);
			if (processed == null) {
				Map<String, Object> response = new HashMap<>();
				response.put("success", false);
				response.put("message", "Registro procesado no encontrado");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}

			List<PayBookDetailProcessed> details = payBookDetailProcessedRepository
					.findAllByPayBookProcessedIdAndFamilyId(id, familyId);

			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("data", details);
			response.put("total", details.size());
			response.put("header", processed);
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			logger.error("Error al obtener detalles procesados: ", e);
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Error: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	/**
	 * Procesar una liquidación (copia PayBookInstance -> PayBookProcessed)
	 */
	@PostMapping(value = "/v2/process", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> processPayBook(
			@RequestParam Long id,
			Authentication authentication) {
		try {
			if (authentication == null || !(authentication.getPrincipal() instanceof CurrentUser)) {
				Map<String, Object> response = new HashMap<>();
				response.put("success", false);
				response.put("message", "No autenticado");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
			}

			CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
			String familyId = currentUser.getFamilId();

			ProcessingService.ProcessResult result = processingService.processPayBook(id, familyId);

			Map<String, Object> response = new HashMap<>();
			response.put("success", result.isSuccess());
			response.put("message", result.getMessage());
			if (result.isSuccess()) {
				response.put("processedId", result.getProcessedId());
			}
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			logger.error("Error al procesar liquidación: ", e);
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Error al procesar: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}
}
