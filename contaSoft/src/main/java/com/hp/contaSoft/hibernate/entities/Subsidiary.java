package com.hp.contaSoft.hibernate.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class Subsidiary extends Base{

	
	@Column
	private String name;
	
	@Column
	private String nickname;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name="taxpayer_id")
	private Taxpayer taxpayer;

	public Subsidiary() {
		
	}
	
	public Subsidiary(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Taxpayer getTaxpayer() {
		return taxpayer;
	}

	public void setTaxpayer(Taxpayer taxpayer) {
		this.taxpayer = taxpayer;
	}

	@Override
	public String toString() {
		return "Subsidiary [name=" + name +"]";
	}
	
	
	
	
}
