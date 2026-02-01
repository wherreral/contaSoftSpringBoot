package com.hp.contaSoft.hibernate.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PayBookInstance extends Base{

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name="taxpayer_id")
	private Taxpayer taxpayer;
	
	@OneToMany(mappedBy="payBookInstance", cascade=CascadeType.ALL)
	private List<com.hp.contaSoft.excel.entities.PayBookDetails> payBookDetails = new ArrayList<>();
	
	
	//column to control the version of the book
	@Column
	private int version;
	
	@Column
	private String rut;
	
	@Column
	private String month;
	
	@Column
	private String details;
	
	@Column
	private String fileName;
	
	@Column
	private String status;
	
	public PayBookInstance() {
		
	}

	public PayBookInstance(Taxpayer taxpayer, String month, String details) {
		super();
		this.taxpayer = taxpayer;
		this.month = month;
		this.details = details;
	}
	
	public PayBookInstance(String month, String details) {
		super();
		this.month = month;
		this.details = details;
	}

	public PayBookInstance(String month, String details, String fileName) {
		super();
		this.month = month;
		this.details = details;
		this.fileName = fileName;
	}
	
	public PayBookInstance(String month, String details, String fileName, String rut) {
		super();
		this.month = month;
		this.details = details;
		this.fileName = fileName;
		this.rut = rut;
	}
	
	public PayBookInstance(String month, String details, String fileName, String rut, Taxpayer tax) {
		super();
		this.month = month;
		this.details = details;
		this.fileName = fileName;
		this.rut = rut;
		this.taxpayer = tax; 
	}
	
	public PayBookInstance(String month, String details, String fileName, String rut, Taxpayer tax, String status) {
		super();
		this.month = month;
		this.details = details;
		this.fileName = fileName;
		this.rut = rut;
		this.taxpayer = tax; 
		this.status = status;
	}
	
	
	public List<com.hp.contaSoft.excel.entities.PayBookDetails> getPayBookDetails() {
		return payBookDetails;
	}

	public void setPayBookDetails(List<com.hp.contaSoft.excel.entities.PayBookDetails> payBookDetails) {
		this.payBookDetails = payBookDetails;
	}

	@Override
	public String toString() {
		return "PayBookInstance [version=" + version + ", month=" + month + ", details="
				+ details + "]";
	}
	
	
	
	
}
