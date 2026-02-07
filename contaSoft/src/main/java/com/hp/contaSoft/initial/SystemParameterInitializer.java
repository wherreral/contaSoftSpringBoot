package com.hp.contaSoft.initial;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.hp.contaSoft.hibernate.dao.repositories.SystemParameterRepository;
import com.hp.contaSoft.hibernate.entities.SystemParameter;

@Component
public class SystemParameterInitializer {

    private static final Logger logger = LoggerFactory.getLogger(SystemParameterInitializer.class);

    @Autowired
    private SystemParameterRepository repository;

    @EventListener(ContextRefreshedEvent.class)
    public void onContextRefreshed(ContextRefreshedEvent event) {
        try {
            String key = "AFC";
            List<SystemParameter> existing = repository.findByNameIgnoreCase(key);
            if (existing == null || existing.isEmpty()) {
                SystemParameter p = new SystemParameter(key, "0.6", "Parametro AFC inicial");
                repository.save(p);
                logger.info("Inserted global SystemParameter {} with value {}", key, p.getValue());
            } else {
                logger.info("SystemParameter {} already exists, skipping initialization", key);
            }
        } catch (Exception e) {
            logger.error("Error initializing SystemParameter AFC", e);
        }

        // Inicializar parámetro para método de cálculo de bono
        try {
            String bonoMethodKey = "BONO_CALCULATION_METHOD";
            List<SystemParameter> existingBonoMethod = repository.findByNameIgnoreCase(bonoMethodKey);
            if (existingBonoMethod == null || existingBonoMethod.isEmpty()) {
                SystemParameter p = new SystemParameter(
                    bonoMethodKey,
                    "ALGEBRAIC",
                    "Método de cálculo de bono cuando viene ALCANCE_LIQUIDO: ALGEBRAIC o NEWTON_RAPHSON"
                );
                repository.save(p);
                logger.info("Inserted SystemParameter {} with value {}", bonoMethodKey, p.getValue());
            } else {
                logger.info("SystemParameter {} already exists, skipping initialization", bonoMethodKey);
            }
        } catch (Exception e) {
            logger.error("Error initializing SystemParameter BONO_CALCULATION_METHOD", e);
        }
    }
}
