package com.hp.contaSoft.hibernate.dao.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hp.contaSoft.hibernate.entities.GeneralConfiguration;

@Repository
public interface GeneralConfigurationRepository extends JpaRepository<GeneralConfiguration, Long> {
    List<GeneralConfiguration> findByNameIgnoreCase(String name);
}
