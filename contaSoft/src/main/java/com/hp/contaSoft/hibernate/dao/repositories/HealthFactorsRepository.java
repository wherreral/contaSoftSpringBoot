package com.hp.contaSoft.hibernate.dao.repositories;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hp.contaSoft.hibernate.entities.HealthFactors;

@Repository
public interface HealthFactorsRepository extends CrudRepository<HealthFactors, Long>{

	HealthFactors findByName(String name);

}
