package com.hp.contaSoft.scheduler;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class IndicadoresScheduler {

	private static final Logger logger = LoggerFactory.getLogger(IndicadoresScheduler.class);
	private static final String MINDICADOR_URL = "https://mindicador.cl/api";

	private final RestTemplate restTemplate;
	private final ConcurrentHashMap<String, IndicadorEconomico> indicadores = new ConcurrentHashMap<>();

	public IndicadoresScheduler(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@PostConstruct
	public void init() {
		logger.info("Inicializando carga de indicadores economicos...");
		actualizarIndicadores();
	}

	@Scheduled(fixedRate = 3600000) // cada 1 hora
	public void actualizarIndicadores() {
		try {
			long start = System.currentTimeMillis();
			MindicadorResponse response = restTemplate.getForObject(MINDICADOR_URL, MindicadorResponse.class);

			if (response != null && response.getIndicadores() != null && !response.getIndicadores().isEmpty()) {
				indicadores.putAll(response.getIndicadores());
				long elapsed = System.currentTimeMillis() - start;

				IndicadorEconomico uf = indicadores.get("uf");
				IndicadorEconomico utm = indicadores.get("utm");
				IndicadorEconomico dolar = indicadores.get("dolar");

				logger.info("Indicadores actualizados en {} ms - UF={}, UTM={}, Dolar={}",
						elapsed,
						uf != null ? uf.getValor() : "N/A",
						utm != null ? utm.getValor() : "N/A",
						dolar != null ? dolar.getValor() : "N/A");
			} else {
				logger.warn("Respuesta vacia de mindicador.cl, manteniendo valores anteriores");
			}
		} catch (Exception e) {
			logger.error("Error al consultar mindicador.cl, manteniendo valores anteriores: {}", e.getMessage());
		}
	}

	public IndicadorEconomico getIndicador(String codigo) {
		return indicadores.get(codigo);
	}

	public Map<String, IndicadorEconomico> getAll() {
		return Collections.unmodifiableMap(indicadores);
	}

	public double getValorUF() {
		IndicadorEconomico uf = indicadores.get("uf");
		return uf != null ? uf.getValor() : 0.0;
	}

	public double getValorUTM() {
		IndicadorEconomico utm = indicadores.get("utm");
		return utm != null ? utm.getValor() : 0.0;
	}

	@Configuration
	static class RestTemplateConfig {
		@Bean
		public RestTemplate restTemplate() {
			return new RestTemplate();
		}
	}
}
