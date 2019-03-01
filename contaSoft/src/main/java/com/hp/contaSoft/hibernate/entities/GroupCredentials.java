package com.hp.contaSoft.hibernate.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
public class GroupCredentials extends Base {

	private String name;
	private String type;
	private String gcId;
	
	
	
	public GroupCredentials() {
		super();
	}



	public GroupCredentials(String name, String type, String gcId) {
		super();
		this.name = name;
		this.type = type;
		this.gcId = gcId;
	}
	   
	
	
	
}
