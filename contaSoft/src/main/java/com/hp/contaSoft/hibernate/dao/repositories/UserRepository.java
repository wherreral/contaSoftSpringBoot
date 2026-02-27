package com.hp.contaSoft.hibernate.dao.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hp.contaSoft.hibernate.entities.AppUser;

@Repository
public interface UserRepository extends CrudRepository<AppUser, Long>{

	AppUser findFirstByUsername(String username);

	List<AppUser> findByGroupCredentials_GcId(String gcId);

}
