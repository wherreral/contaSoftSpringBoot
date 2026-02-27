package com.hp.contaSoft.hibernate.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="AFPFactors")
@Getter
@Setter
public class AFPFactors extends Base{

	public AFPFactors() {
		
	}
	
	@Column (name = "name")
	private String name;
	@Column( name ="percentaje")
	private double percentaje;
	@Column( name ="sis_tasa")
	private double sisTasa;

	public AFPFactors(String name, double percentaje) {
		super();
		this.name = name;
		this.percentaje = percentaje;
		this.sisTasa = 1.49; // SIS default
	}

	public AFPFactors(String name, double percentaje, double sisTasa) {
		super();
		this.name = name;
		this.percentaje = percentaje;
		this.sisTasa = sisTasa;
	}
	
	
}
