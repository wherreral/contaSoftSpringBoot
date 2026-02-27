package com.hp.contaSoft.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.contaSoft.hibernate.dao.repositories.TaxpayerRepository;
import com.hp.contaSoft.hibernate.dao.repositories.TemplateDetailsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.TemplateRepository;
import com.hp.contaSoft.hibernate.entities.Template;
import com.hp.contaSoft.hibernate.entities.Taxpayer;
import com.hp.contaSoft.hibernate.entities.TemplateDefiniton;

@Service
public class TemplateService {

    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);

    private static final String DEFAULT_TEMPLATE_VALUE =
        "{\"RUT\":\"RUT\",\"CENTRO_COSTO\":\"CENTRO_COSTO\",\"SUELDO_BASE\":\"SUELDO_BASE\"," +
        "\"DT\":\"DT\",\"PREVISION\":\"PREVISION\",\"SALUD\":\"SALUD\",\"SALUD_PORCENTAJE\":\"SALUD_PORCENTAJE\"}";

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private TemplateDetailsRepository templateDetailsRepository;

    @Autowired
    private TaxpayerRepository taxpayerRepository;
    
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
        ensureDefaultTemplatesExist(familyId);
        return templateRepository.findAllByFamilyId(familyId);
    }

    @Transactional
    public void ensureDefaultTemplatesExist(String familyId) {
        List<Taxpayer> taxpayers = taxpayerRepository.findByFamilyId(familyId);
        if (taxpayers.isEmpty()) return;

        List<Template> existing = templateRepository.findByTaxpayerFamilyId(familyId);
        Set<Long> taxpayerIdsWithTemplate = existing.stream()
            .filter(t -> t.getTaxpayer() != null)
            .map(t -> t.getTaxpayer().getId())
            .collect(Collectors.toSet());

        for (Taxpayer tp : taxpayers) {
            if (!taxpayerIdsWithTemplate.contains(tp.getId())) {
                Template defaultTemplate = new Template("Default", DEFAULT_TEMPLATE_VALUE, "Template por defecto", false);
                defaultTemplate.setTaxpayer(tp);
                defaultTemplate.setDefaultTemplate(true);
                defaultTemplate.setFamily(familyId);
                templateRepository.save(defaultTemplate);
                logger.info("Template default creado para cliente existente: {} (id={})", tp.getName(), tp.getId());
            }
        }
    }

    public Optional<Template> getDefaultTemplateByFamilyId(String familyId) {
        return templateRepository.findDefaultByFamily(familyId);
    }
}
