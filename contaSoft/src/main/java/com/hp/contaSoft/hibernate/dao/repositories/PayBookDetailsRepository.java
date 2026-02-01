package com.hp.contaSoft.hibernate.dao.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hp.contaSoft.excel.entities.PayBookDetails;
import com.hp.contaSoft.hibernate.dao.projection.IdProjection;
import com.hp.contaSoft.hibernate.dao.projection.PrevisionProjection;


@Repository
public interface PayBookDetailsRepository extends CrudRepository<PayBookDetails, Long>{

	
	List<PayBookDetails> findAllByPayBookInstance_Id(Long id); 
	
	@Query("from PayBookDetails p where p.payBookInstance.id = :id")
	List<PayBookDetails> findByPayBookInstanceId(@Param("id") Long id);
	
	
	@Query("select p.prevision as prevision, sum(p.valorPrevision) as suma from PayBookDetails p where p.payBookInstance.id = :id group by prevision")
	List<PrevisionProjection> getReportByPrevision(@Param("id") Long id);

	
	@Query("select p.id as id, p.rut as rut, p.sueldoBase as sueldo_base,"
			+ "p.gratificacion as gratificacion, p.horasExtra as horasExtra, p.bonoProduccion as bonoProduccion "
			+ "from PayBookDetails p")
	List<IdProjection> getAll();
	
	@Query("select p.id as id, p.rut as rut, p.sueldoBase as sueldo_base,"
			+ "p.gratificacion as gratificacion, p.horasExtra as horasExtra, p.bonoProduccion as bonoProduccion "
			+ "from PayBookDetails p where p.payBookInstance.id = :id")
	List<IdProjection> getAllbyId(@Param("id") Long id);
	
}
