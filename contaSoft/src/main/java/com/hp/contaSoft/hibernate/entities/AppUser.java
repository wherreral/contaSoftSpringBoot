package com.hp.contaSoft.hibernate.entities;


import java.util.ArrayList;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
public class AppUser extends Base {



	@Column
	private String username;
	@Column
	private String password;
	@Column
	private String name;
	@Column
	private String phone;
	
	
	
	public AppUser(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	public AppUser() {
		super();
	}

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name="gc_id")
	private GroupCredentials groupCredentials;
	
	@OneToOne(mappedBy="user", cascade={CascadeType.ALL})
	private Role role;
	
}
