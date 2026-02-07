package com.hp.contaSoft.hibernate.dao.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hp.contaSoft.hibernate.entities.PayBookInstance;

@Repository
public interface PayBookInstanceRepository extends CrudRepository<PayBookInstance, Long>{

	//@Query("SELECT version FROM PayBookInstance p WHERE p.rut = :rut")
	//public int findVersion(@Param("rut") String rut);
	
	@Query("select p from PayBookInstance p where p.rut =:rut and p.month=:month and p.familyId = :familyId ORDER BY id desc")
	List<PayBookInstance> getVersionByRutAndMonth(@Param("rut") String rut,@Param("month") String month, @Param("familyId") String familyId);
	
	public List<PayBookInstance> findAllByTaxpayerId(Long id);
	
	public List<PayBookInstance> findAllByTaxpayerIdOrderByVersionDesc(Long id);
	
	@Query("SELECT p FROM PayBookInstance p LEFT JOIN FETCH p.payBookDetails WHERE p.id = :id")
	PayBookInstance findByIdWithDetails(@Param("id") Long id);
	
	@Query("SELECT p FROM PayBookInstance p WHERE p.id = :id AND p.familyId = :familyId")
	PayBookInstance findByIdAndFamilyId(@Param("id") Long id, @Param("familyId") String familyId);
	
	public List<PayBookInstance> findAllByRut(String rut);
	
	@Query("SELECT p FROM PayBookInstance p WHERE p.familyId = :familyId ORDER BY p.id DESC")
	List<PayBookInstance> findAllByFamilyId(@Param("familyId") String familyId);
	
	@Query("SELECT p FROM PayBookInstance p WHERE p.familyId = :familyId AND p.taxpayer.id = :taxpayerId ORDER BY p.version DESC")
	List<PayBookInstance> findAllByFamilyIdAndTaxpayerId(@Param("familyId") String familyId, @Param("taxpayerId") Long taxpayerId);
	
}
