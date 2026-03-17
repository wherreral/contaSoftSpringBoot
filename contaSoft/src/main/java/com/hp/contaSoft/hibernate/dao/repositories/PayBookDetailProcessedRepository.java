package com.hp.contaSoft.hibernate.dao.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hp.contaSoft.excel.entities.PayBookDetailProcessed;

@Repository
public interface PayBookDetailProcessedRepository extends CrudRepository<PayBookDetailProcessed, Long> {

	@Query("SELECT p FROM PayBookDetailProcessed p WHERE p.payBookProcessedId = :processedId AND p.familyId = :familyId")
	List<PayBookDetailProcessed> findAllByPayBookProcessedIdAndFamilyId(@Param("processedId") Long processedId, @Param("familyId") String familyId);
}
