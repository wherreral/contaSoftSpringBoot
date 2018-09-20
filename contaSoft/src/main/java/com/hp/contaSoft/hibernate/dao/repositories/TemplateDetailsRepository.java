package com.hp.contaSoft.hibernate.dao.repositories;



import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hp.contaSoft.hibernate.entities.IUT;
import com.hp.contaSoft.hibernate.entities.TemplateDefiniton;


@Repository
public interface TemplateDetailsRepository extends CrudRepository<TemplateDefiniton, Long>
{

	

}
