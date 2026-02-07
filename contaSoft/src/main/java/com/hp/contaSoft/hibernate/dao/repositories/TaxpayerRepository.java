package com.hp.contaSoft.hibernate.dao.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hp.contaSoft.hibernate.entities.PayBookInstance;
import com.hp.contaSoft.hibernate.entities.Taxpayer;

@Repository
public interface TaxpayerRepository extends CrudRepository<Taxpayer, Long>{

	Taxpayer findFirstByRut(String rut);
	
	@Query("select p from Taxpayer p where p.rut = :rut AND p.familyId = :familyId")
	Taxpayer findByRutAndFamilyId(@Param("rut") String rut, @Param("familyId") String familyId);
	
	public List<Taxpayer> findAll(Pageable pageable);
	
	@Query("select p from Taxpayer p where p.familyId =:familyId ORDER BY id desc")
	public List<Taxpayer> findAll(@Param("familyId") String familyId);
	
	public List<Taxpayer> findByFamilyId(String familyId);
	
	
}
