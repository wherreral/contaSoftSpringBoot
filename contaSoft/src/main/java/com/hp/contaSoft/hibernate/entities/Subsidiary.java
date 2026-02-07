package com.hp.contaSoft.hibernate.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class Subsidiary extends Base{

	@Column(unique = true, nullable = false, length = 12)
	private String subsidiaryId; // Formato: SUC-XXXXX
	
	@Column(nullable = false)
	private String name;
	
	@Column
	private String nickname;
	
	@Column
	private String address;
	
	@Column
	private String familyId;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name="taxpayer_id")
	private Taxpayer taxpayer;

	public Subsidiary() {
		
	}
	
	public Subsidiary(String name) {
		this.name = name;
	}
	
	@PrePersist
	public void generateSubsidiaryId() {
		if (this.subsidiaryId == null || this.subsidiaryId.isEmpty()) {
			// Generar ID basado en timestamp + random para garantizar unicidad
			long timestamp = System.currentTimeMillis();
			int random = (int)(Math.random() * 1000);
			this.subsidiaryId = String.format("SUC-%05d", (timestamp % 100000 + random) % 100000);
		}
	}

	public String getSubsidiaryId() {
		return subsidiaryId;
	}

	public void setSubsidiaryId(String subsidiaryId) {
		this.subsidiaryId = subsidiaryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getFamilyId() {
		return familyId;
	}

	public void setFamilyId(String familyId) {
		this.familyId = familyId;
	}

	public Taxpayer getTaxpayer() {
		return taxpayer;
	}

	public void setTaxpayer(Taxpayer taxpayer) {
		this.taxpayer = taxpayer;
	}

	@Override
	public String toString() {
		return "Subsidiary [subsidiaryId=" + subsidiaryId + ", name=" + name + ", taxpayer=" + (taxpayer != null ? taxpayer.getRut() : "null") + "]";
	}
	
	
	
	
}
