package com.hp.contaSoft.hibernate.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;

import com.hp.contaSoft.constant.Regimen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.ToString;


@Entity
@ToString
public class Taxpayer extends Base {

	@Column
	private String name;
	
	@Column
	private String lastname;
	
	@Column
	private String rut;
	
	@Column
	private String familyId; //this field indicate which family created this record
    
	@Enumerated(EnumType.STRING)
	@Column
	private Regimen regimen;
	
	@JsonManagedReference
	@OneToMany(mappedBy = "taxpayer", cascade=CascadeType.ALL)
    private List<Template> templates = new ArrayList<>();
	
	@JsonManagedReference
	@OneToMany(mappedBy="taxpayer", cascade=CascadeType.ALL)
	private List<Address> address = new ArrayList<>();
	
	@JsonManagedReference
	@OneToMany(mappedBy="taxpayer", cascade=CascadeType.ALL)
	private List<Subsidiary> subsidiary = new ArrayList<>();
	
	@JsonIgnore
	@OneToMany(mappedBy="taxpayer", cascade=CascadeType.ALL)
	private List<PayBookInstance> payBookInstance = new ArrayList<>();
	
	public Taxpayer() {
		
	}
	
	public Taxpayer(String name, String rut) {
		this.name = name;
		this.rut = rut;
	}

	public Taxpayer(String name, String rut, Address address) {
		this.name = name;
		this.rut = rut;
		address.setTaxpayer(this);
		this.address.add(address);
	}
	
	public Taxpayer(String name, String rut, Address address,String familyId) {
		this.name = name;
		this.rut = rut;
		address.setTaxpayer(this);
		this.address.add(address);
		this.familyId = familyId;
	}
	

	public Taxpayer(String name, String rut, Address address, Subsidiary subsidiary) {
		this.name = name;
		this.rut = rut;
		address.setTaxpayer(this);
		this.address.add(address);
		subsidiary.setTaxpayer(this);
		this.subsidiary.add(subsidiary);
	}
	public Taxpayer(String name, String rut, Address address, String familyId, Subsidiary subsidiary) {
        this.name = name;
		this.rut = rut;
		this.familyId = familyId;
		address.setTaxpayer(this);
		this.address.add(address);
		subsidiary.setTaxpayer(this);
		this.subsidiary.add(subsidiary);
    }

    public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getLastname() {
		return lastname;
	}


	public void setLastname(String lastname) {
		this.lastname = lastname;
	}


	public String getRut() {
		return rut;
	}


	public void setRut(String rut) {
		this.rut = rut;
	}
	
	
	public List<Address> getAddress() {
		return address;
	}

	public void setAddress(List<Address> address) {
		this.address = address;
	}

	
	
	public List<Subsidiary> getSubsidiary() {
		return subsidiary;
	}

	public void setSubsidiary(List<Subsidiary> subsidiary) {
		this.subsidiary = subsidiary;
	}
	
	
	public List<PayBookInstance> getPayBookInstance() {
		return payBookInstance;
	}

	public void setPayBookInstance(List<PayBookInstance> payBookInstance) {
		this.payBookInstance = payBookInstance;
	}

	
	public List<Template> getTemplates() {
		return templates;
	}

	public void setTemplates(List<Template> templates) {
		this.templates = templates;
	}

	public String getFamilyId() {
		return familyId;
	}

	public void setFamilyId(String familyId) {
		this.familyId = familyId;
	}

	public Regimen getRegimen() {
		return regimen;
	}

	public void setRegimen(Regimen regimen) {
		this.regimen = regimen;
	}

	
	
	
	
	
	
		
	
	
	
	
}
