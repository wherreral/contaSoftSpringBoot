package com.hp.contaSoft.hibernate.dao.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hp.contaSoft.hibernate.entities.SystemParameter;

@Repository
public interface SystemParameterRepository extends JpaRepository<SystemParameter, Long> {
    List<SystemParameter> findByNameIgnoreCase(String name);
}
