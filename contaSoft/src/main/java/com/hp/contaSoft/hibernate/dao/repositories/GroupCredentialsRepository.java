package com.hp.contaSoft.hibernate.dao.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hp.contaSoft.hibernate.entities.AppUser;
import com.hp.contaSoft.hibernate.entities.GroupCredentials;

@Repository
public interface GroupCredentialsRepository extends CrudRepository<GroupCredentials, Long>{

	//AppUser findByUsername(String username);

	
	
}
