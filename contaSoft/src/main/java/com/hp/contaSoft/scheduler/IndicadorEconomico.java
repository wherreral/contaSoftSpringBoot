package com.hp.contaSoft.scheduler;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndicadorEconomico {

	private String codigo;
	private String nombre;

	@JsonProperty("unidad_medida")
	private String unidadMedida;

	private double valor;
	private String fecha;
}
