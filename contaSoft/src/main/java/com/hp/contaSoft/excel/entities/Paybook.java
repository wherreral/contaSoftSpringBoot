package com.hp.contaSoft.excel.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.hp.contaSoft.hibernate.entities.PayBookInstance;
import com.hp.contaSoft.hibernate.entities.Taxpayer;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

import lombok.Data;

@Entity
public class Paybook {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column
	@CsvBindByName
	private String RUT;
	
	@Column
	@CsvBindByName
	private String CENTRO_COSTO;
	
	@Column
	@CsvBindByName
	private double SUELDO_BASE;
	
	@Column
	@CsvBindByName
	private int DT;
	
	@Column
	@CsvBindByName
	private double BONO;
	
	@Column
	@CsvBindByName
	private double HORAS_EXTRA;
	
	@Column
	@CsvBindByName
	private double AGUINALDO;
	
	@Column
	@CsvBindByName
	private int ASIG_FAMILIAR;
	
	@Column
	@CsvBindByName
	private double MOVILIZACION;
	
	@CsvBindByName
	private double COLACION;
	
	@CsvBindByName
	private double DESCUENTOS;
	
	@CsvBindByName
	private double DES_HERRAMIENTA;
	
	@CsvBindByName
	private String PREVISION;
	
	@CsvBindByName
	private String SALUD;
	
	//PORCENTAJE
	@CsvBindByName
	private double AFC;
	
	@CsvBindByName
	private double APV;
	
	@CsvBindByName
	private double PTMO;
	
	@CsvBindByName
	private double SEGURO_ONCO;
	
	@CsvBindByName
	private String SEGURO_ACCIDENTES;
	
	@CsvBindByName
	private double ANTICIPO;
	
	public Paybook() {
		
	}
	
	@ManyToOne
	@JoinColumn(name="paybookinstance_id")
	private PayBookInstance payBookInstance;

	public String getRUT() {
		return RUT;
	}

	public void setRUT(String rUT) {
		RUT = rUT;
	}

	public String getCENTRO_COSTO() {
		return CENTRO_COSTO;
	}

	public void setCENTRO_COSTO(String cENTRO_COSTO) {
		CENTRO_COSTO = cENTRO_COSTO;
	}

	public double getSUELDO_BASE() {
		return SUELDO_BASE;
	}

	public void setSUELDO_BASE(double sUELDO_BASE) {
		SUELDO_BASE = sUELDO_BASE;
	}

	public int getDT() {
		return DT;
	}

	public void setDT(int dT) {
		DT = dT;
	}

	public double getBONO() {
		return BONO;
	}

	public void setBONO(double bONO) {
		BONO = bONO;
	}

	public double getHORAS_EXTRA() {
		return HORAS_EXTRA;
	}

	public void setHORAS_EXTRA(double hORAS_EXTRA) {
		HORAS_EXTRA = hORAS_EXTRA;
	}

	public double getAGUINALDO() {
		return AGUINALDO;
	}

	public void setAGUINALDO(double aGUINALDO) {
		AGUINALDO = aGUINALDO;
	}

	public int getASIG_FAMILIAR() {
		return ASIG_FAMILIAR;
	}

	public void setASIG_FAMILIAR(int aSIG_FAMILIAR) {
		ASIG_FAMILIAR = aSIG_FAMILIAR;
	}

	public double getMOVILIZACION() {
		return MOVILIZACION;
	}

	public void setMOVILIZACION(double mOVILIZACION) {
		MOVILIZACION = mOVILIZACION;
	}

	public double getCOLACION() {
		return COLACION;
	}

	public void setCOLACION(double cOLACION) {
		COLACION = cOLACION;
	}

	public double getDESCUENTOS() {
		return DESCUENTOS;
	}

	public void setDESCUENTOS(double dESCUENTOS) {
		DESCUENTOS = dESCUENTOS;
	}

	public double getDES_HERRAMIENTA() {
		return DES_HERRAMIENTA;
	}

	public void setDES_HERRAMIENTA(double dES_HERRAMIENTA) {
		DES_HERRAMIENTA = dES_HERRAMIENTA;
	}

	public String getPREVISION() {
		return PREVISION;
	}

	public void setPREVISION(String pREVISION) {
		PREVISION = pREVISION;
	}

	public String getSALUD() {
		return SALUD;
	}

	public void setSALUD(String sALUD) {
		SALUD = sALUD;
	}

	public double getAFC() {
		return AFC;
	}

	public void setAFC(double aFC) {
		AFC = aFC;
	}

	public double getAPV() {
		return APV;
	}

	public void setAPV(double aPV) {
		APV = aPV;
	}

	public double getPTMO() {
		return PTMO;
	}

	public void setPTMO(double pTMO) {
		PTMO = pTMO;
	}

	public double getSEGURO_ONCO() {
		return SEGURO_ONCO;
	}

	public void setSEGURO_ONCO(double sEGURO_ONCO) {
		SEGURO_ONCO = sEGURO_ONCO;
	}

	public String getSEGURO_ACCIDENTES() {
		return SEGURO_ACCIDENTES;
	}

	public void setSEGURO_ACCIDENTES(String sEGURO_ACCIDENTES) {
		SEGURO_ACCIDENTES = sEGURO_ACCIDENTES;
	}

	public double getANTICIPO() {
		return ANTICIPO;
	}

	public void setANTICIPO(double aNTICIPO) {
		ANTICIPO = aNTICIPO;
	}
	
	
	
	
	
	
}
