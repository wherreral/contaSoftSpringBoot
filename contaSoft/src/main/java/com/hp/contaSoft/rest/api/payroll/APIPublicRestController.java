package com.hp.contaSoft.rest.api.payroll;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.contaSoft.hibernate.entities.AppUser;
import com.hp.contaSoft.service.UserRegistrationService;

@RestController
@RequestMapping(value = "/public/api/", produces = MediaType.APPLICATION_JSON_VALUE)
public class APIPublicRestController {

	@Autowired
	private UserRegistrationService userRegistrationService;

	public Logger logger = LoggerFactory.getLogger(APIPublicRestController.class);

	@CrossOrigin("http://localhost:3000")
	@PostMapping(value = "/sign-up", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> SignUp(@RequestBody AppUser user) {

		logger.warn("sign-up");
		Map<String, Object> response = new HashMap<>();

		try {
			userRegistrationService.registerUser(user.getUsername(), user.getPassword());
			response.put("success", true);
			response.put("message", "Cuenta creada exitosamente");
			return ResponseEntity.ok(response);

		} catch (IllegalArgumentException e) {
			logger.warn("Validación fallida: {}", e.getMessage());
			response.put("success", false);
			response.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

		} catch (RuntimeException e) {
			logger.warn("Error de registro: {}", e.getMessage());
			response.put("success", false);
			response.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		}
	}

	@CrossOrigin("http://localhost:3000")
	@PostMapping("/sign-in")
	public Boolean SignIn(@RequestBody AppUser user) {

		logger.warn("SignIn");
		return true;
	}
}
