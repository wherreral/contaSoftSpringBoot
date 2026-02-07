package com.hp.contaSoft.hibernate.dao.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hp.contaSoft.hibernate.entities.Template;

@Repository
public interface TemplateRepository extends CrudRepository<Template, Long> {
    
    @Query("SELECT t FROM Template t WHERE t.taxpayer.id = :taxpayerId")
    List<Template> findByTaxpayerId(@Param("taxpayerId") Long taxpayerId);
    
    @Query("SELECT t FROM Template t WHERE t.taxpayer.id = :taxpayerId AND t.isActive = :isActive")
    Optional<Template> findByTaxpayerIdAndIsActive(@Param("taxpayerId") Long taxpayerId, @Param("isActive") boolean isActive);
    
    @Query("SELECT t FROM Template t WHERE t.taxpayer.id = :taxpayerId AND t.name = :name")
    Optional<Template> findByTaxpayerIdAndName(@Param("taxpayerId") Long taxpayerId, @Param("name") String name);
    
    @Query("SELECT COUNT(t) FROM Template t WHERE t.taxpayer.id = :taxpayerId AND t.isActive = true")
    int countActiveTemplates(@Param("taxpayerId") Long taxpayerId);
    
    @Query("SELECT t FROM Template t WHERE t.taxpayer.familyId = :familyId")
    List<Template> findByTaxpayerFamilyId(@Param("familyId") String familyId);
    
    @Query("SELECT t FROM Template t WHERE t.taxpayer.familyId = :familyId AND t.isActive = true")
    Optional<Template> findActiveByFamilyId(@Param("familyId") String familyId);
}
