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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.hp.contaSoft.custom.CurrentUser;
import com.hp.contaSoft.service.UserManagementService;

@RestController
@RequestMapping(value = "/api/ui/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class UserManagementRestController {

	private static final Logger logger = LoggerFactory.getLogger(UserManagementRestController.class);

	@Autowired
	private UserManagementService userManagementService;

	@GetMapping
	public ResponseEntity<?> listUsers(Authentication authentication) {
		try {
			CurrentUser currentUser = getCurrentUser(authentication);
			if (currentUser == null) {
				return unauthorized();
			}

			List<Map<String, Object>> users = userManagementService.getUsersByFamily(currentUser.getFamilId());

			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("data", users);
			response.put("total", users.size());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("Error listando usuarios", e);
			return errorResponse(e.getMessage());
		}
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createUser(@RequestBody Map<String, String> body, Authentication authentication) {
		try {
			CurrentUser currentUser = getCurrentUser(authentication);
			if (currentUser == null) {
				return unauthorized();
			}

			String username = body.get("username");
			String password = body.get("password");
			String name = body.get("name");
			String phone = body.get("phone");

			userManagementService.createTenantUser(username, password, name, phone, currentUser.getFamilId());

			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("message", "Usuario creado exitosamente");
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return errorResponse(e.getMessage());
		} catch (RuntimeException e) {
			return errorResponse(e.getMessage());
		} catch (Exception e) {
			logger.error("Error creando usuario", e);
			return errorResponse("Error interno al crear usuario");
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication authentication) {
		try {
			CurrentUser currentUser = getCurrentUser(authentication);
			if (currentUser == null) {
				return unauthorized();
			}

			userManagementService.deleteTenantUser(id, currentUser.getFamilId(), currentUser.getUsername());

			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("message", "Usuario eliminado exitosamente");
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			return errorResponse(e.getMessage());
		} catch (Exception e) {
			logger.error("Error eliminando usuario", e);
			return errorResponse("Error interno al eliminar usuario");
		}
	}

	@PutMapping(value = "/{id}/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body, Authentication authentication) {
		try {
			CurrentUser currentUser = getCurrentUser(authentication);
			if (currentUser == null) {
				return unauthorized();
			}

			String newPassword = body.get("newPassword");
			userManagementService.resetPassword(id, newPassword, currentUser.getFamilId());

			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("message", "Contrasena actualizada exitosamente");
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return errorResponse(e.getMessage());
		} catch (RuntimeException e) {
			return errorResponse(e.getMessage());
		} catch (Exception e) {
			logger.error("Error reseteando contrasena", e);
			return errorResponse("Error interno al resetear contrasena");
		}
	}

	private CurrentUser getCurrentUser(Authentication authentication) {
		if (authentication == null || !(authentication.getPrincipal() instanceof CurrentUser)) {
			return null;
		}
		return (CurrentUser) authentication.getPrincipal();
	}

	private ResponseEntity<?> unauthorized() {
		Map<String, Object> response = new HashMap<>();
		response.put("success", false);
		response.put("message", "No autenticado");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

	private ResponseEntity<?> errorResponse(String message) {
		Map<String, Object> response = new HashMap<>();
		response.put("success", false);
		response.put("message", message);
		return ResponseEntity.badRequest().body(response);
	}
}
