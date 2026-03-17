package com.hp.contaSoft.hibernate.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cotizacion_empleador_ley21735")
@Getter
@Setter
@NoArgsConstructor
public class CotizacionEmpleadorLey21735 extends Base {

	@Column(name = "letra", length = 5)
	private String letra;

	@Column(name = "fecha_desde")
	@Temporal(TemporalType.DATE)
	private Date fechaDesde;

	@Column(name = "fecha_hasta")
	@Temporal(TemporalType.DATE)
	private Date fechaHasta;

	@Column(name = "tasa_capitalizacion")
	private double tasaCapitalizacion;

	@Column(name = "tasa_rentabilidad_protegida")
	private double tasaRentabilidadProtegida;

	@Column(name = "tasa_expectativas_vida_sis")
	private double tasaExpectativasVidaSis;

	@Column(name = "tasa_total")
	private double tasaTotal;

	public CotizacionEmpleadorLey21735(String letra, Date fechaDesde, Date fechaHasta,
			double tasaCapitalizacion, double tasaRentabilidadProtegida,
			double tasaExpectativasVidaSis) {
		this.letra = letra;
		this.fechaDesde = fechaDesde;
		this.fechaHasta = fechaHasta;
		this.tasaCapitalizacion = tasaCapitalizacion;
		this.tasaRentabilidadProtegida = tasaRentabilidadProtegida;
		this.tasaExpectativasVidaSis = tasaExpectativasVidaSis;
		this.tasaTotal = tasaCapitalizacion + tasaRentabilidadProtegida + tasaExpectativasVidaSis;
	}
}
