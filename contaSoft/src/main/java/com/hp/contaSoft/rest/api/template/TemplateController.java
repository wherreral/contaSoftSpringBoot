package com.hp.contaSoft.rest.api.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.contaSoft.custom.CurrentUser;
import com.hp.contaSoft.hibernate.dao.repositories.TemplateDetailsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.TemplateRepository;
import com.hp.contaSoft.hibernate.dao.repositories.TaxpayerRepository;
import com.hp.contaSoft.service.TemplateService;
import com.hp.contaSoft.hibernate.entities.Template;
import com.hp.contaSoft.hibernate.entities.TemplateDefiniton;
import com.hp.contaSoft.hibernate.entities.Taxpayer;

@RestController
@RequestMapping(value = "/api/templates", produces = "application/json")
public class TemplateController {
    
    private static final Logger logger = LoggerFactory.getLogger(TemplateController.class);
    
    @Autowired
    private TemplateService templateService;
    
    @Autowired
    private TemplateRepository templateRepository;
    
    @Autowired
    private TemplateDetailsRepository templateDetailsRepository;
    
    @Autowired
    private TaxpayerRepository taxpayerRepository;
    
    // FamilyId por defecto para desarrollo (cuando no hay autenticación JWT)
    // TODO: En producción, remover esta constante y requerir autenticación
    private static final String DEFAULT_FAMILY_ID_FOR_DEV = null; // Cambiar a un gcId válido si se necesita
    
    private String getCurrentFamilyId(Authentication auth) {
        logger.info("getCurrentFamilyId - auth: {}", auth);
        if (auth != null) {
            logger.info("getCurrentFamilyId - principal type: {}", auth.getPrincipal().getClass().getName());
            if (auth.getPrincipal() instanceof CurrentUser) {
                CurrentUser currentUser = (CurrentUser) auth.getPrincipal();
                logger.info("getCurrentFamilyId - familyId: {}, username: {}", currentUser.getFamilId(), currentUser.getUsername());
                return currentUser.getFamilId();
            }
        }
        
        // Para desarrollo: si no hay autenticación, intentar obtener el primer familyId disponible
        if (DEFAULT_FAMILY_ID_FOR_DEV == null) {
            logger.warn("getCurrentFamilyId - No hay autenticación, buscando familyId por defecto...");
            try {
                Iterable<Taxpayer> allTaxpayers = taxpayerRepository.findAll();
                for (Taxpayer tp : allTaxpayers) {
                    if (tp.getFamilyId() != null) {
                        logger.info("getCurrentFamilyId - Usando familyId por defecto: {}", tp.getFamilyId());
                        return tp.getFamilyId();
                    }
                }
            } catch (Exception e) {
                logger.error("Error buscando familyId por defecto: ", e);
            }
        } else {
            return DEFAULT_FAMILY_ID_FOR_DEV;
        }
        
        logger.warn("getCurrentFamilyId - returning null, no se pudo obtener familyId");
        return null;
    }
    
    @GetMapping
    public ResponseEntity<?> listTemplates(Authentication auth) {
        logger.info("=== listTemplates START ===");
        try {
            String familyId = getCurrentFamilyId(auth);
            logger.info("listTemplates - familyId obtenido: {}", familyId);
            if (familyId == null) {
                logger.warn("listTemplates - Usuario no autenticado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Usuario no autenticado"));
            }
            
            List<Template> templates = templateService.getTemplatesByFamilyId(familyId);
            logger.info("listTemplates - templates encontrados: {}", templates.size());
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            logger.error("Error al listar templates: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error al listar templates: " + e.getMessage()));
        }
    }
    
    @GetMapping("/field-definitions")
    public ResponseEntity<?> getFieldDefinitions() {
        logger.info("=== getFieldDefinitions START ===");
        try {
            List<TemplateDefiniton> definitions = (List<TemplateDefiniton>) templateDetailsRepository.findAll();
            logger.info("getFieldDefinitions - definiciones encontradas: {}", definitions.size());
            return ResponseEntity.ok(definitions);
        } catch (Exception e) {
            logger.error("Error al obtener definiciones de campos: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error al obtener definiciones: " + e.getMessage()));
        }
    }
    
    @GetMapping("/taxpayers")
    public ResponseEntity<?> getTaxpayers(Authentication auth) {
        logger.info("=== getTaxpayers START ===");
        try {
            String familyId = getCurrentFamilyId(auth);
            logger.info("getTaxpayers - familyId: {}", familyId);
            if (familyId == null) {
                // Para desarrollo, devolver todos los taxpayers
                List<Taxpayer> allTaxpayers = (List<Taxpayer>) taxpayerRepository.findAll();
                logger.info("getTaxpayers - devolviendo todos los taxpayers: {}", allTaxpayers.size());
                return ResponseEntity.ok(allTaxpayers);
            }
            
            List<Taxpayer> taxpayers = taxpayerRepository.findByFamilyId(familyId);
            logger.info("getTaxpayers - taxpayers encontrados: {}", taxpayers.size());
            return ResponseEntity.ok(taxpayers);
        } catch (Exception e) {
            logger.error("Error al obtener taxpayers: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error al obtener taxpayers: " + e.getMessage()));
        }
    }
    
    @GetMapping("/by-taxpayer/{taxpayerId}")
    public ResponseEntity<?> getTemplatesByTaxpayer(@PathVariable Long taxpayerId, Authentication auth) {
        logger.info("=== getTemplatesByTaxpayer START - taxpayerId: {} ===", taxpayerId);
        try {
            // Verificar que el taxpayer existe
            Taxpayer taxpayer = taxpayerRepository.findById(taxpayerId).orElse(null);
            if (taxpayer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Contribuyente no encontrado"));
            }
            
            // Obtener templates del taxpayer
            List<Template> templates = templateRepository.findByTaxpayerId(taxpayerId);
            logger.info("getTemplatesByTaxpayer - templates encontrados: {}", templates.size());
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            logger.error("Error al obtener templates por taxpayer: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error al obtener templates: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getTemplate(@PathVariable Long id, Authentication auth) {
        logger.info("=== getTemplate START - id: {} ===", id);
        try {
            String familyId = getCurrentFamilyId(auth);
            logger.info("getTemplate - familyId: {}", familyId);
            if (familyId == null) {
                logger.warn("getTemplate - Usuario no autenticado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Usuario no autenticado"));
            }
            
            Template template = templateRepository.findById(id)
                .orElseThrow(() -> new Exception("Template no encontrado"));
            logger.info("getTemplate - template encontrado: {}", template.getName());

            // Validar que pertenece al mismo familyId (por taxpayer o por family directamente)
            String templateFamilyId = template.getTaxpayer() != null
                ? template.getTaxpayer().getFamilyId()
                : template.getFamily();
            if (templateFamilyId == null || !templateFamilyId.equals(familyId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("No tienes permiso para acceder a este template"));
            }

            return ResponseEntity.ok(template);
        } catch (Exception e) {
            logger.error("Error al obtener template: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error al obtener template: " + e.getMessage()));
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createTemplate(@RequestBody TemplateDTO dto, Authentication auth) {
        logger.info("=== createTemplate START - name: {}, taxpayerId: {} ===", dto.getName(), dto.getTaxpayerId());
        try {
            String familyId = getCurrentFamilyId(auth);
            logger.info("createTemplate - familyId: {}", familyId);
            
            // Validar datos
            if (dto.getName() == null || dto.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("El nombre del template es obligatorio"));
            }
            
            // Validar que se proporcionó un taxpayerId
            if (dto.getTaxpayerId() == null) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Debe seleccionar un contribuyente (taxpayer)"));
            }
            
            // Si se proporciona value, validarlo
            if (dto.getValue() != null && !dto.getValue().trim().isEmpty() && !dto.getValue().equals("{}")) {
                templateService.validateTemplateFields(dto.getValue());
            } else {
                dto.setValue("{}"); // Template vacío inicialmente
            }
            
            // Buscar el taxpayer por ID
            Taxpayer taxpayer = taxpayerRepository.findById(dto.getTaxpayerId())
                .orElse(null);
            if (taxpayer == null) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("No se encontró el contribuyente seleccionado"));
            }
            
            // Validar que el taxpayer pertenece al familyId del usuario (si hay autenticación)
            if (familyId != null && !taxpayer.getFamilyId().equals(familyId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("No tienes permiso para crear templates para este contribuyente"));
            }
            
            Template template = new Template();
            template.setTaxpayer(taxpayer);
            template.setName(dto.getName());
            template.setValue(dto.getValue());
            template.setDescription(dto.getDescription());
            template.setActive(false); // Por defecto inactivo
            template.setFamily(familyId);
            
            template = templateRepository.save(template);
            
            // Si se solicitó activar inmediatamente
            if (dto.isActivateImmediately()) {
                template = templateService.activateTemplate(template.getId(), familyId);
            }
            
            return ResponseEntity.ok(template);
        } catch (Exception e) {
            logger.error("Error al crear template: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error al crear template: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTemplate(@PathVariable Long id, @RequestBody TemplateDTO dto, Authentication auth) {
        logger.info("=== updateTemplate START - id: {}, name: {} ===", id, dto.getName());
        try {
            String familyId = getCurrentFamilyId(auth);
            logger.info("updateTemplate - familyId: {}", familyId);
            if (familyId == null) {
                logger.warn("updateTemplate - Usuario no autenticado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Usuario no autenticado"));
            }
            
            Template template = templateRepository.findById(id)
                .orElseThrow(() -> new Exception("Template no encontrado"));

            // No se puede modificar el template default
            if (template.isDefaultTemplate()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("El template por defecto no se puede modificar"));
            }

            // Validar que pertenece al mismo familyId
            String templateFamilyId = template.getTaxpayer() != null
                ? template.getTaxpayer().getFamilyId()
                : template.getFamily();
            if (templateFamilyId == null || !templateFamilyId.equals(familyId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("No tienes permiso para modificar este template"));
            }

            // Validar campos
            if (dto.getValue() != null && !dto.getValue().trim().isEmpty() && !dto.getValue().equals("{}")) {
                templateService.validateTemplateFields(dto.getValue());
            }
            
            template.setName(dto.getName());
            template.setValue(dto.getValue());
            template.setDescription(dto.getDescription());
            
            return ResponseEntity.ok(templateRepository.save(template));
        } catch (Exception e) {
            logger.error("Error al actualizar template: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error al actualizar template: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTemplate(@PathVariable Long id, Authentication auth) {
        logger.info("=== deleteTemplate START - id: {} ===", id);
        try {
            String familyId = getCurrentFamilyId(auth);
            logger.info("deleteTemplate - familyId: {}", familyId);
            if (familyId == null) {
                logger.warn("deleteTemplate - Usuario no autenticado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Usuario no autenticado"));
            }
            
            Template template = templateRepository.findById(id)
                .orElseThrow(() -> new Exception("Template no encontrado"));

            // No se puede eliminar el template default
            if (template.isDefaultTemplate()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("El template por defecto no se puede eliminar"));
            }

            // Validar que pertenece al mismo familyId
            String templateFamilyId = template.getTaxpayer() != null
                ? template.getTaxpayer().getFamilyId()
                : template.getFamily();
            if (templateFamilyId == null || !templateFamilyId.equals(familyId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("No tienes permiso para eliminar este template"));
            }

            if (template.isActive()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("No se puede eliminar el template activo"));
            }
            
            templateRepository.delete(template);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error al eliminar template: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error al eliminar template: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activateTemplate(@PathVariable Long id, Authentication auth) {
        logger.info("=== activateTemplate START - id: {} ===", id);
        try {
            String familyId = getCurrentFamilyId(auth);
            logger.info("activateTemplate - familyId: {}", familyId);
            if (familyId == null) {
                logger.warn("activateTemplate - Usuario no autenticado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Usuario no autenticado"));
            }
            
            Template activated = templateService.activateTemplate(id, familyId);
            return ResponseEntity.ok(activated);
        } catch (Exception e) {
            logger.error("Error al activar template: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error al activar template: " + e.getMessage()));
        }
    }
    
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
    
    // DTO interno
    public static class TemplateDTO {
        private String name;
        private String value;
        private String description;
        private boolean activateImmediately;
        private Long taxpayerId;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public boolean isActivateImmediately() { return activateImmediately; }
        public void setActivateImmediately(boolean activateImmediately) { 
            this.activateImmediately = activateImmediately; 
        }
        
        public Long getTaxpayerId() { return taxpayerId; }
        public void setTaxpayerId(Long taxpayerId) { this.taxpayerId = taxpayerId; }
    }
}
