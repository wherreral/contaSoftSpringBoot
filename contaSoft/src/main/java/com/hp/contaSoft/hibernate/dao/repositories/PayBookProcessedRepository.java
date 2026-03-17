package com.hp.contaSoft.hibernate.dao.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hp.contaSoft.hibernate.entities.PayBookProcessed;

@Repository
public interface PayBookProcessedRepository extends CrudRepository<PayBookProcessed, Long> {

	@Query("SELECT p FROM PayBookProcessed p WHERE p.familyId = :familyId ORDER BY p.id DESC")
	List<PayBookProcessed> findAllByFamilyId(@Param("familyId") String familyId);

	@Query("SELECT p FROM PayBookProcessed p WHERE p.familyId = :familyId AND p.taxpayerId = :taxpayerId ORDER BY p.id DESC")
	List<PayBookProcessed> findAllByFamilyIdAndTaxpayerId(@Param("familyId") String familyId, @Param("taxpayerId") Long taxpayerId);

	@Query("SELECT p FROM PayBookProcessed p WHERE p.id = :id AND p.familyId = :familyId")
	PayBookProcessed findByIdAndFamilyId(@Param("id") Long id, @Param("familyId") String familyId);
}
