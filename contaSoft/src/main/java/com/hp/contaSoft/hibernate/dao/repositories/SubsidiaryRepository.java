package com.hp.contaSoft.hibernate.dao.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hp.contaSoft.hibernate.entities.Subsidiary;
import com.hp.contaSoft.hibernate.entities.Taxpayer;

@Repository
public interface SubsidiaryRepository extends CrudRepository<Subsidiary, Long> {
	
	List<Subsidiary> findByTaxpayer(Taxpayer taxpayer);
	
	List<Subsidiary> findByTaxpayerId(Long taxpayerId);
	
	Subsidiary findBySubsidiaryId(String subsidiaryId);
	
	@Query("SELECT s FROM Subsidiary s WHERE s.familyId = :familyId")
	List<Subsidiary> findAllByFamilyId(@Param("familyId") String familyId);
	
	@Query("SELECT s FROM Subsidiary s WHERE s.familyId = :familyId AND s.taxpayer.id = :taxpayerId")
	List<Subsidiary> findAllByFamilyIdAndTaxpayerId(@Param("familyId") String familyId, @Param("taxpayerId") Long taxpayerId);
	
}
