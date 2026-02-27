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
import org.springframework.web.bind.annotation.*;

import com.hp.contaSoft.hibernate.dao.repositories.HealthFactorsRepository;
import com.hp.contaSoft.hibernate.entities.HealthFactors;

@RestController
@RequestMapping(value = "/api/ui/health", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class HealthRestController {

    private static final Logger logger = LoggerFactory.getLogger(HealthRestController.class);

    @Autowired
    private HealthFactorsRepository healthFactorsRepository;

    @Autowired
    private com.hp.contaSoft.service.ReferenceDataCache referenceDataCache;

    public HealthRestController() {
        logger.info("=== HealthRestController INITIALIZED ===");
    }

    /**
     * Obtener todas las Isapres
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllHealth() {
        try {
            logger.info("Obteniendo lista de todas las Isapres");
            List<HealthFactors> healthList = (List<HealthFactors>) healthFactorsRepository.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", healthList);
            response.put("total", healthList.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al obtener Isapres: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener las Isapres: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtener una Isapre por ID
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getHealthById(@PathVariable Long id) {
        try {
            logger.info("Obteniendo Isapre con ID: {}", id);
            
            HealthFactors health = healthFactorsRepository.findById(id).orElse(null);
            
            if (health == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Isapre no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", health);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al obtener Isapre: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener la Isapre: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Crear una nueva Isapre
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createHealth(@RequestBody HealthDTO healthDTO) {
        try {
            logger.info("Creando nueva Isapre: {}", healthDTO.getName());
            
            // Validar que no exista una Isapre con el mismo nombre
            HealthFactors existingHealth = healthFactorsRepository.findByName(healthDTO.getName());
            if (existingHealth != null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Ya existe una Isapre con ese nombre");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            // Crear la nueva Isapre
            HealthFactors health = new HealthFactors();
            health.setName(healthDTO.getName());
            health.setNickname(healthDTO.getNickname());
            health.setPecentaje(healthDTO.getPercentaje());
            
            // Guardar la Isapre
            HealthFactors savedHealth = healthFactorsRepository.save(health);
            referenceDataCache.reload();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Isapre creada exitosamente");
            response.put("data", savedHealth);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error al crear Isapre: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al crear la Isapre: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Actualizar una Isapre existente
     */
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateHealth(@PathVariable Long id, @RequestBody HealthDTO healthDTO) {
        try {
            logger.info("Actualizando Isapre con ID: {}", id);
            
            HealthFactors health = healthFactorsRepository.findById(id).orElse(null);
            if (health == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Isapre no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            // Validar que no exista otra Isapre con el mismo nombre
            HealthFactors existingHealth = healthFactorsRepository.findByName(healthDTO.getName());
            if (existingHealth != null && existingHealth.getId() != id) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Ya existe otra Isapre con ese nombre");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            // Actualizar datos de la Isapre
            health.setName(healthDTO.getName());
            health.setNickname(healthDTO.getNickname());
            health.setPecentaje(healthDTO.getPercentaje());
            
            // Guardar cambios
            HealthFactors updatedHealth = healthFactorsRepository.save(health);
            referenceDataCache.reload();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Isapre actualizada exitosamente");
            response.put("data", updatedHealth);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al actualizar Isapre: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al actualizar la Isapre: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Eliminar una Isapre
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteHealth(@PathVariable Long id) {
        try {
            logger.info("Eliminando Isapre con ID: {}", id);
            
            HealthFactors health = healthFactorsRepository.findById(id).orElse(null);
            if (health == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Isapre no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            // Eliminar la Isapre
            healthFactorsRepository.delete(health);
            referenceDataCache.reload();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Isapre eliminada exitosamente");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al eliminar Isapre: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al eliminar la Isapre: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * DTO para recibir datos de Isapre desde el frontend
     */
    public static class HealthDTO {
        private String name;
        private String nickname;
        private double percentaje;

        // Getters y Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public double getPercentaje() {
            return percentaje;
        }

        public void setPercentaje(double percentaje) {
            this.percentaje = percentaje;
        }
    }
}
