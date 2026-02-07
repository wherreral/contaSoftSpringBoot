package com.hp.contaSoft.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.contaSoft.hibernate.dao.repositories.TemplateDetailsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.TemplateRepository;
import com.hp.contaSoft.hibernate.entities.Template;
import com.hp.contaSoft.hibernate.entities.TemplateDefiniton;

@Service
public class TemplateService {
    
    @Autowired
    private TemplateRepository templateRepository;
    
    @Autowired
    private TemplateDetailsRepository templateDetailsRepository;
    
    @Transactional
    public Template activateTemplate(Long templateId, String familyId) {
        // Desactivar todos los templates del taxpayer (por familyId)
        List<Template> allTemplates = templateRepository.findByTaxpayerFamilyId(familyId);
        allTemplates.forEach(t -> t.setActive(false));
        templateRepository.saveAll(allTemplates);
        
        // Activar el seleccionado
        Template template = templateRepository.findById(templateId)
            .orElseThrow(() -> new EntityNotFoundException("Template no encontrado"));
        template.setActive(true);
        return templateRepository.save(template);
    }
    
    public void validateTemplateFields(String valueJson) throws Exception {
        if (valueJson == null || valueJson.trim().isEmpty() || valueJson.equals("{}")) {
            throw new Exception("El template debe tener al menos un campo mapeado");
        }
        
        // Parsear JSON y validar que contenga todos los campos obligatorios
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> mapping = mapper.readValue(valueJson, new TypeReference<Map<String, String>>(){});
        
        List<String> required = Arrays.asList("RUT", "CENTRO_COSTO", "SUELDO_BASE", 
                                               "DT", "PREVISION", "SALUD", "SALUD_PORCENTAJE");
        
        for (String field : required) {
            if (!mapping.containsKey(field)) {
                throw new Exception("Falta el campo obligatorio: " + field);
            }
        }
    }
    
    public Optional<Template> getActiveTemplateByFamilyId(String familyId) {
        return templateRepository.findActiveByFamilyId(familyId);
    }
    
    public List<Template> getTemplatesByFamilyId(String familyId) {
        return templateRepository.findByTaxpayerFamilyId(familyId);
    }
}
