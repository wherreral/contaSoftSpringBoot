package com.hp.contaSoft.hibernate.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@MappedSuperclass
@ToString
public class Base {

	
	public Base(){
		
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	protected long id;
	
	@Column
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
	@Column
	private String createBy;
	
	@Column
	private String updateBy;
	
	@Column
	private String family;
	
	@Column
	private String type;
	
	@PrePersist
    public void prePersist() {
     
		setValue();
    }
 
    @PreUpdate
    public void preUpdate() {
     
    	setValue();
    }
	
    private void setValue() {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	
        if(auth != null) {
        	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (principal instanceof UserDetails) {
            	System.out.println("UserDetails");
            	createBy  = ((UserDetails) principal).getUsername();
            } else if(principal instanceof AppUser)
            {
            	System.out.println("AppUser");
            	createBy  = ((AppUser) principal).getUsername();
            }
            else {
            	System.out.println("to string");
            	createBy  = principal.toString();
            }
    		//String username = auth.getName();
        	//createBy = username;
    	}
    	
    }
}
