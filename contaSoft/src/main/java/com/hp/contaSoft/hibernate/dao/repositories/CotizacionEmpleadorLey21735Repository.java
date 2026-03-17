package com.hp.contaSoft.hibernate.dao.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.hp.contaSoft.hibernate.entities.CotizacionEmpleadorLey21735;

public interface CotizacionEmpleadorLey21735Repository extends CrudRepository<CotizacionEmpleadorLey21735, Long> {

	List<CotizacionEmpleadorLey21735> findAll();
}
