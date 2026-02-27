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

import com.hp.contaSoft.hibernate.dao.repositories._AFamiliarRepository;
import com.hp.contaSoft.hibernate.entities._AFamiliar;

@RestController
@RequestMapping(value = "/api/ui/afamiliar", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class AFamiliarRestController {

    private static final Logger logger = LoggerFactory.getLogger(AFamiliarRestController.class);

    @Autowired
    private _AFamiliarRepository afamiliarRepository;

    @Autowired
    private com.hp.contaSoft.service.ReferenceDataCache referenceDataCache;

    public AFamiliarRestController() {
        logger.info("=== AFamiliarRestController INITIALIZED ===");
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllAFamiliar() {
        try {
            logger.info("Obteniendo lista de Asignación Familiar");
            List<_AFamiliar> list = (List<_AFamiliar>) afamiliarRepository.findAll();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", list);
            response.put("total", list.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al obtener Asignación Familiar: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener Asignación Familiar: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getAFamiliarById(@PathVariable Long id) {
        try {
            logger.info("Obteniendo Asignación Familiar con ID: {}", id);
            _AFamiliar item = afamiliarRepository.findById(id).orElse(null);
            if (item == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Asignación Familiar no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", item);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al obtener Asignación Familiar: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener Asignación Familiar: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
