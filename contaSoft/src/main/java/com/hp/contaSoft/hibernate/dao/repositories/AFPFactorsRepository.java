package com.hp.contaSoft.hibernate.dao.repositories;

import org.springframework.context.annotation.Primary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hp.contaSoft.hibernate.entities.AFPFactors;

@Repository
@Primary
public interface AFPFactorsRepository extends CrudRepository<AFPFactors, Long>{

	AFPFactors findByName(String prevision);

	// Case-insensitive exact name lookup
	AFPFactors findByNameIgnoreCase(String prevision);

}
