package com.hp.contaSoft.hibernate.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
public class Template extends Base{

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "taxpayer_id", nullable = false)
	private Taxpayer taxpayer;
	
	@Column(nullable = false, length = 100)
	private String name;
	
	@Column(columnDefinition = "TEXT")
	private String value;
	
	@Column(nullable = false)
	private boolean isActive = false;
	
	@Column(length = 500)
	private String description;
	
	public Template() {
		
	}

	public Template(String name, String value) {
		this.name = name;
		this.value = value;
		this.isActive = false;
	}
	
	public Template(String name, String value, String description, boolean isActive) {
		this.name = name;
		this.value = value;
		this.description = description;
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "Template [ name=" + name + ", value=" + value + ", isActive=" + isActive + "]";
	}

	// Getters para serialización JSON (sin @JsonBackReference)
	public Long getTaxpayerId() {
		return taxpayer != null ? taxpayer.getId() : null;
	}
	
	public String getTaxpayerName() {
		return taxpayer != null ? taxpayer.getName() : null;
	}
	
	public String getTaxpayerRut() {
		return taxpayer != null ? taxpayer.getRut() : null;
	}


	
		
}
