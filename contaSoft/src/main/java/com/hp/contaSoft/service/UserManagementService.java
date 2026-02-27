package com.hp.contaSoft.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.contaSoft.hibernate.dao.repositories.GroupCredentialsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.UserRepository;
import com.hp.contaSoft.hibernate.entities.AppUser;
import com.hp.contaSoft.hibernate.entities.GroupCredentials;
import com.hp.contaSoft.hibernate.entities.Role;

@Service
public class UserManagementService {

	private static final Logger logger = LoggerFactory.getLogger(UserManagementService.class);
	private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9@._-]{4,100}$");

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GroupCredentialsRepository groupCredentialsRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public List<Map<String, Object>> getUsersByFamily(String familyId) {
		List<AppUser> users = userRepository.findByGroupCredentials_GcId(familyId);
		return users.stream().map(u -> {
			Map<String, Object> dto = new HashMap<>();
			dto.put("id", u.getId());
			dto.put("username", u.getUsername());
			dto.put("name", u.getName());
			dto.put("phone", u.getPhone());
			dto.put("role", u.getRole() != null ? u.getRole().getRole() : 2);
			dto.put("created", u.getCreated());
			return dto;
		}).collect(Collectors.toList());
	}

	@Transactional
	public AppUser createTenantUser(String username, String password, String name, String phone, String familyId) {
		// Validar username
		if (username == null || username.trim().isEmpty()) {
			throw new IllegalArgumentException("El usuario es obligatorio");
		}
		username = username.trim().toLowerCase();

		if (!USERNAME_PATTERN.matcher(username).matches()) {
			throw new IllegalArgumentException("Usuario invalido. Solo se permiten letras, numeros, @, ., - y _. Minimo 4, maximo 100 caracteres");
		}

		// Validar password
		if (password == null || password.length() < 6) {
			throw new IllegalArgumentException("La contrasena debe tener al menos 6 caracteres");
		}

		// Validar unicidad
		AppUser existing = userRepository.findFirstByUsername(username);
		if (existing != null) {
			throw new RuntimeException("El usuario ya existe");
		}

		// Buscar GroupCredentials del tenant
		GroupCredentials gc = groupCredentialsRepository.findByGcId(familyId);
		if (gc == null) {
			throw new RuntimeException("Tenant no encontrado");
		}

		// Crear usuario con role=2 (USER)
		AppUser user = new AppUser(username, bCryptPasswordEncoder.encode(password));
		user.setName(name);
		user.setPhone(phone);
		user.setRole(new Role(user, 2));
		user.setGroupCredentials(gc);
		userRepository.save(user);

		logger.info("Usuario tenant creado: {} para familia: {}", username, familyId);
		return user;
	}

	@Transactional
	public void deleteTenantUser(Long userId, String familyId, String currentUsername) {
		List<AppUser> familyUsers = userRepository.findByGroupCredentials_GcId(familyId);
		AppUser target = familyUsers.stream()
			.filter(u -> u.getId() == userId)
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Usuario no encontrado en este tenant"));

		// No permitir borrarse a sí mismo
		if (target.getUsername().equals(currentUsername)) {
			throw new RuntimeException("No puedes eliminar tu propio usuario");
		}

		// No permitir borrar admins
		if (target.getRole() != null && target.getRole().getRole() == 1) {
			throw new RuntimeException("No puedes eliminar un usuario administrador");
		}

		userRepository.delete(target);
		logger.info("Usuario eliminado: {} de familia: {}", target.getUsername(), familyId);
	}

	@Transactional
	public void resetPassword(Long userId, String newPassword, String familyId) {
		if (newPassword == null || newPassword.length() < 6) {
			throw new IllegalArgumentException("La contrasena debe tener al menos 6 caracteres");
		}

		List<AppUser> familyUsers = userRepository.findByGroupCredentials_GcId(familyId);
		AppUser target = familyUsers.stream()
			.filter(u -> u.getId() == userId)
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Usuario no encontrado en este tenant"));

		target.setPassword(bCryptPasswordEncoder.encode(newPassword));
		userRepository.save(target);
		logger.info("Contrasena reseteada para usuario: {} de familia: {}", target.getUsername(), familyId);
	}
}
