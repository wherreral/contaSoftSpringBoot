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
@Setter
@Getter
@ToString
public class AppUser extends Base {

	@Column
	private String username;
	@Column
	private String password;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name="gc_id")
	private GroupCredentials groupCredentials;
	
}
