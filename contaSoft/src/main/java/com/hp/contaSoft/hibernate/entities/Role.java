package com.hp.contaSoft.hibernate.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Role extends Base{
	
	
	@OneToOne
	private AppUser user;
	
	private Integer role;

	
	
	public Role(AppUser user, Integer role) {
		super();
		this.user = user;
		this.role = role;
	}



	public Role() {
		super();
	}
	
	
}
