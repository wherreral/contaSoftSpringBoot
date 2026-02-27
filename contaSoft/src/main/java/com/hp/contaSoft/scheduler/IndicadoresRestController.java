package com.hp.contaSoft.scheduler;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/ui/indicadores", produces = MediaType.APPLICATION_JSON_VALUE)
public class IndicadoresRestController {

	@Autowired
	private IndicadoresScheduler indicadoresScheduler;

	@GetMapping
	public ResponseEntity<Map<String, IndicadorEconomico>> getAll() {
		return ResponseEntity.ok(indicadoresScheduler.getAll());
	}

	@GetMapping("/{codigo}")
	public ResponseEntity<IndicadorEconomico> getByCodigo(@PathVariable String codigo) {
		IndicadorEconomico indicador = indicadoresScheduler.getIndicador(codigo);
		if (indicador == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(indicador);
	}
}
