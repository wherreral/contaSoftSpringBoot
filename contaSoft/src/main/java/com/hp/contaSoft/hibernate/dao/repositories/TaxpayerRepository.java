package com.hp.contaSoft.hibernate.dao.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hp.contaSoft.hibernate.entities.Taxpayer;

@Repository
public interface TaxpayerRepository extends CrudRepository<Taxpayer, Long>{

	Taxpayer findByRut(String rut);
	
	public List<Taxpayer> findAll(Pageable pageable);
	
}
