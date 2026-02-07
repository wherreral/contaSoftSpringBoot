package com.hp.contaSoft.hibernate.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;

@Data
public class PayBookDetails extends Base{

	
	@Column
	private double dt;
	
	@Column
	private double baseincome;
	
	@Column
	private double gratif;
	
	@Column
	private double bono;
	
	@Column
	private double taxableincome;
	
	@Column
	private double charges;
	
	@Column
	private double transportation;
	
	@Column
	private double food;
	
	@Column
	private double haber;
	
	@Column
	private double afp;
	
	@Column
	private double health;
	
	@Column
	private double apvcta;
	
	@Column
	private double ptmo;
	
	@Column
	private double oncoensurance;
	
	@Column
	private double iut;
	
	@Column
	private double afce;
	
	@Column
	private double achs;
	
	@Column
	private double totaldiscount;
	
	@Column
	private double alcliq;
	
	@Column
	private double anticipos;
	
	@Column
	private double saldoliquido;
	
	@Column
	private String RUT;
	
	@Column
	private String CENTRO_COSTO;
	
	@Column
	private double SUELDO_BASE;
	
	@Column
	private int DT;
	
	@Column
	private double BONO;
	
	@Column
	private double HORAS_EXTRA;
	
	@Column
	private double AGUINALDO;
	
	@Column
	private int ASIG_FAMILIAR;
	
	@Column
	private double MOVILIZACION;
	
	@Column
	private double COLACION;
	
	@Column
	private double DESCUENTOS;
	
	@Column
	private double DES_HERRAMIENTA;
	
	@Column
	private String PREVISION;
	
	@Column
	private String SALUD;
	
	@Column
	private double AFC;
	
	@Column
	private double APV;
	
	@Column
	private double PTMO;
	
	@Column
	private double SEGURO_ONCO;
	
	@Column
	private String SEGURO_ACCIDENTES;
	
	@Column
	private double ANTICIPO;

	
	public PayBookDetails() {
		
	}

	@ManyToOne
	@JoinColumn(name="payBookInstance_id")
	private PayBookInstance payBookInstance; 

	public PayBookDetails(String rUT, String cENTRO_COSTO, double sUELDO_BASE, int dT, String pREVISION, String sALUD) {
		super();
		RUT = rUT;
		CENTRO_COSTO = cENTRO_COSTO;
		SUELDO_BASE = sUELDO_BASE;
		DT = dT;
		PREVISION = pREVISION;
		SALUD = sALUD;
	}
	
	
	
	
	
	
	
}
