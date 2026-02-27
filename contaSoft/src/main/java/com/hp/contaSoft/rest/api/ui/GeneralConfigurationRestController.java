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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.contaSoft.hibernate.dao.service.GeneralConfigurationService;
import com.hp.contaSoft.hibernate.entities.GeneralConfiguration;

@RestController
@RequestMapping(value = "/api/ui/configuracion-general", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class GeneralConfigurationRestController {

    private static final Logger logger = LoggerFactory.getLogger(GeneralConfigurationRestController.class);

    @Autowired
    private GeneralConfigurationService service;

    @GetMapping
    public ResponseEntity<?> list() {
        try {
            List<GeneralConfiguration> list = service.findAll();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", list);
            response.put("total", list.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching GeneralConfiguration", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al cargar datos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestBody GeneralConfiguration conf) {
        try {
            // If a configuration with the same name already exists, update it instead
            if (conf.getName() != null) {
                List<GeneralConfiguration> existing = service.findByNameIgnoreCase(conf.getName());
                if (existing != null && !existing.isEmpty()) {
                    GeneralConfiguration ex = existing.get(0);
                    ex.setValue(conf.getValue());
                    ex.setDescription(conf.getDescription());
                    service.save(ex);
                    Map<String, Object> resp = new HashMap<>();
                    resp.put("success", true);
                    return ResponseEntity.ok(resp);
                }
            }

            service.save(conf);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (Exception e) {
            logger.error("Error creating GeneralConfiguration", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody GeneralConfiguration conf) {
        try {
            return service.findById(id).map(existing -> {
                existing.setValue(conf.getValue());
                existing.setDescription(conf.getDescription());
                service.save(existing);
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", true);
                return ResponseEntity.ok(resp);
            }).orElseGet(() -> {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            });
        } catch (Exception e) {
            logger.error("Error updating GeneralConfiguration", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
