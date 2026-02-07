package com.hp.contaSoft.hibernate.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class HealthFactors extends Base{

	public HealthFactors() {
		
	}
	
	@Column
	private String name;
	
	@Column
	private String nickname;
	
	@Column
	private double pecentaje;

	public HealthFactors(String name, double pecentaje) {
		super();
		this.name = name;
		this.pecentaje = pecentaje;
	}
	
	
}
