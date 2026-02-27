package com.hp.contaSoft.service;

import java.util.UUID;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.contaSoft.hibernate.dao.repositories.GroupCredentialsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.TemplateRepository;
import com.hp.contaSoft.hibernate.dao.repositories.UserRepository;
import com.hp.contaSoft.hibernate.entities.AppUser;
import com.hp.contaSoft.hibernate.entities.GroupCredentials;
import com.hp.contaSoft.hibernate.entities.Role;
import com.hp.contaSoft.hibernate.entities.Template;

@Service
public class UserRegistrationService {

	private static final Logger logger = LoggerFactory.getLogger(UserRegistrationService.class);

	private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9@._-]{4,100}$");

	private static final String DEFAULT_TEMPLATE_VALUE =
		"{\"RUT\":\"RUT\",\"CENTRO_COSTO\":\"CENTRO_COSTO\",\"SUELDO_BASE\":\"SUELDO_BASE\"," +
		"\"DT\":\"DT\",\"PREVISION\":\"PREVISION\",\"SALUD\":\"SALUD\",\"SALUD_PORCENTAJE\":\"SALUD_PORCENTAJE\"}";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GroupCredentialsRepository groupCredentialsRepository;

	@Autowired
	private TemplateRepository templateRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Transactional
	public AppUser registerUser(String username, String password) {
		// 1. Sanitizar username
		if (username == null || username.trim().isEmpty()) {
			throw new IllegalArgumentException("El usuario es obligatorio");
		}
		username = username.trim().toLowerCase();

		if (!USERNAME_PATTERN.matcher(username).matches()) {
			throw new IllegalArgumentException("Usuario inválido. Solo se permiten letras, números, @, ., - y _. Mínimo 4, máximo 100 caracteres");
		}

		// 2. Validar password
		if (password == null || password.length() < 6) {
			throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
		}

		// 3. Validar unicidad
		AppUser existing = userRepository.findFirstByUsername(username);
		if (existing != null) {
			throw new RuntimeException("El usuario ya existe");
		}

		// 4. Crear GroupCredentials
		GroupCredentials gc = new GroupCredentials("name", "type", UUID.randomUUID().toString());
		groupCredentialsRepository.save(gc);
		logger.info("GroupCredentials creado con gcId: {}", gc.getGcId());

		// 5. Crear AppUser
		AppUser user = new AppUser(username, bCryptPasswordEncoder.encode(password));
		user.setRole(new Role(user, 1));
		user.setGroupCredentials(gc);
		userRepository.save(user);
		logger.info("Usuario creado: {}", username);

		// 6. Crear template default
		Template defaultTemplate = new Template("Default", DEFAULT_TEMPLATE_VALUE, "Template por defecto", false);
		defaultTemplate.setFamily(gc.getGcId());
		defaultTemplate.setDefaultTemplate(true);
		defaultTemplate.setTaxpayer(null);
		templateRepository.save(defaultTemplate);
		logger.info("Template default creado para familia: {}", gc.getGcId());

		return user;
	}
}
