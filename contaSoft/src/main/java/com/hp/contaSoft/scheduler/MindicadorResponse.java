package com.hp.contaSoft.scheduler;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MindicadorResponse {

	private String version;
	private String autor;
	private String fecha;

	private Map<String, IndicadorEconomico> indicadores = new HashMap<>();

	@JsonAnySetter
	public void setIndicador(String key, Object value) {
		if (value instanceof Map) {
			try {
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) value;
				if (map.containsKey("codigo") && map.containsKey("valor")) {
					IndicadorEconomico ind = new IndicadorEconomico();
					ind.setCodigo((String) map.get("codigo"));
					ind.setNombre((String) map.get("nombre"));
					ind.setUnidadMedida((String) map.get("unidad_medida"));
					ind.setFecha((String) map.get("fecha"));
					Object val = map.get("valor");
					if (val instanceof Number) {
						ind.setValor(((Number) val).doubleValue());
					}
					indicadores.put(key, ind);
				}
			} catch (Exception e) {
				// Ignorar campos que no son indicadores
			}
		}
	}
}
