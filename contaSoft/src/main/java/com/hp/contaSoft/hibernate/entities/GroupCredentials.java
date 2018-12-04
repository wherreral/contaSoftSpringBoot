package com.hp.contaSoft.hibernate.entities;

import javax.persistence.Entity;

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
	
	public GroupCredentials() {
		super();
	}
	
	public GroupCredentials(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}
	
	
	
}
