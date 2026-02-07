package com.hp.contaSoft.hibernate.dao.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hp.contaSoft.hibernate.dao.repositories.SystemParameterRepository;
import com.hp.contaSoft.hibernate.entities.SystemParameter;

@Service
public class SystemParameterService {

    @Autowired
    private SystemParameterRepository repository;

    public List<SystemParameter> findAll() {
        return repository.findAll();
    }

    public Optional<SystemParameter> findById(Long id) {
        return repository.findById(id);
    }

    public SystemParameter save(SystemParameter param) {
        return repository.save(param);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<SystemParameter> findByNameIgnoreCase(String name) {
        return repository.findByNameIgnoreCase(name);
    }
}
