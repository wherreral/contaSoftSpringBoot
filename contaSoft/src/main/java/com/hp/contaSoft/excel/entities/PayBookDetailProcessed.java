package com.hp.contaSoft.excel.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "pay_book_detail_processed")
public class PayBookDetailProcessed {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private Long payBookProcessedId;

	@Column
	private String familyId;

	// --- Datos del empleado ---
	@Column
	private String rut;

	@Column
	private String nombreTrabajador;

	@Column
	private String centroCosto;

	@Column
	private double sueldoBase;

	@Column
	private int diasTrabajados;

	@Column
	private String prevision;

	@Column
	private String salud;

	@Column
	private double saludPorcentaje;

	@Column
	private double bonoProduccion;

	@Column
	private double aguinaldo;

	@Column
	private double horasExtra;

	@Column
	private int asignacionFamiliar;

	@Column
	private double movilizacion;

	@Column
	private double colacion;

	@Column
	private double descuentoHerramientas;

	@Column
	private double afc;

	@Column
	private Double alcanceLiquido;

	@Column
	private int apv;

	@Column
	private double prestamos;

	@Column
	private double seguroOncologico;

	@Column
	private String seguroOAccidentes;

	@Column
	private double anticipo;

	@Column
	private double mutual;

	@Column
	private double descApvCtaAh;

	@Column
	private double descPtmoCcaaff;

	@Column
	private double descPtmoSolidario;

	@Column
	private String regimen;

	// --- Campos calculados (snapshot) ---
	@Column
	private double sueldoMensual;

	@Column
	private double gratificacion;

	@Column
	private double valorHora;

	@Column
	private double totalImponible;

	@Column
	private double totalHaber;

	@Column
	private double porcentajePrevision;

	@Column
	private double valorPrevision;

	@Column
	private double valorSalud;

	@Column
	private double valorAFC;

	@Column
	private double totalAsignacionFamiliar;

	@Column
	private double valorIUT;

	@Column
	private double valorSeguroOAccidentes;

	@Column
	private double rentaLiquidaImponible;

	@Column
	private double totalHoraExtra;

	@Column
	private double afcEmpleador;

	@Column
	private double sisEmpleador;

	@Column
	private double cotCapitalizacion;

	@Column
	private double cotRentabilidadProtegida;

	@Column
	private double cotExpectativasVidaSis;

	public PayBookDetailProcessed() {
	}
}
