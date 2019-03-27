package com.hp.contaSoft.hibernate.dao.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import com.hp.contaSoft.hibernate.entities.AppUser;

@Repository
public interface UserRepository extends CrudRepository<AppUser, Long>{

	AppUser findByUsername(String username);

	
	
}
