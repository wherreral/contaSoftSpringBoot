package com.hp.contaSoft.hibernate.dao.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.hp.contaSoft.hibernate.entities.Taxpayer;

public interface TaxpayerPageRepository extends PagingAndSortingRepository<Taxpayer, Long> {

	
	
}
