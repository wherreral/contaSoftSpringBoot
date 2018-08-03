package com.hp.contaSoft.hibernate.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class TemplateDetails extends Base{
	
	@Column
	private String fieldName;
	
	@Column
	private String fieldDescription;
	
	@Column
	private boolean active;
	
	@Column
	private boolean required;
	
	public TemplateDetails() {
		
	}

	public TemplateDetails(String fieldName, String fieldDescription, boolean active, boolean required) {
		super();
		this.fieldName = fieldName;
		this.fieldDescription = fieldDescription;
		this.active = active;
		this.required = required;
	}
	
	
	
}
