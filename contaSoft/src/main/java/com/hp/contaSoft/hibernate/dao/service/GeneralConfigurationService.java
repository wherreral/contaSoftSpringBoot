package com.hp.contaSoft.hibernate.dao.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hp.contaSoft.hibernate.dao.repositories.GeneralConfigurationRepository;
import com.hp.contaSoft.hibernate.entities.GeneralConfiguration;

@Service
public class GeneralConfigurationService {

    @Autowired
    private GeneralConfigurationRepository repository;

    public List<GeneralConfiguration> findAll() {
        return repository.findAll();
    }

    public Optional<GeneralConfiguration> findById(Long id) {
        return repository.findById(id);
    }

    public GeneralConfiguration save(GeneralConfiguration conf) {
        return repository.save(conf);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<GeneralConfiguration> findByNameIgnoreCase(String name) {
        return repository.findByNameIgnoreCase(name);
    }
}
