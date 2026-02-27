package com.hp.contaSoft.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HeaderAliases {

	private static final Set<String> CANONICAL_NAMES = new HashSet<>();
	private static final Map<String, String> ALIAS_MAP = new HashMap<>();

	static {
		registerAliases("RUT", "RUTS", "UTS");
		registerAliases("CENTRO_COSTO", "CC", "CENTROCOSTO");
		registerAliases("SUELDO_BASE", "SUELDO", "SB", "SUELDOBASE");
		registerAliases("DT", "DIAS_TRABAJADOS", "DIAS");
		registerAliases("PREVISION", "AFP", "PREV");
		registerAliases("SALUD", "ISAPRE");
		registerAliases("SALUD_PORCENTAJE", "SALUD_PCT", "SALUDPORCENTAJE");
		registerAliases("BONO", "BONO_PRODUCCION", "BONOPRODUCCION");
		registerAliases("HORAS_EXTRA", "HE", "HORASEXTRA", "HORAS");
		registerAliases("ASIG_FAMILIAR", "AF", "ASIGFAMILIAR", "ASIGNACION_FAMILIAR");
		registerAliases("MOVILIZACION", "MOV");
		registerAliases("COLACION", "COL");
		registerAliases("DESGASTE", "DES_HERRAMIENTA", "DESHERRAMIENTA");
		registerAliases("AFC", "SEGURO_CESANTIA");
		registerAliases("ALCANCE_LIQUIDO", "AL", "ALCANCELIQUIDO", "LIQUIDO");
		registerAliases("DESC_APV_CTA_AH", "APV_CTA_AH", "APVCTAAH", "DESC_APV");
		registerAliases("DESC_PTMO_CCAAFF", "PTMO_CCAAFF", "PTMOCCAAFF", "PRESTAMO_CCAAFF");
		registerAliases("DESC_PTMO_SOLIDARIO", "PTMO_SOLIDARIO", "PTMOSOLIDARIO", "PRESTAMO_SOLIDARIO");
		registerAliases("REGIMEN", "TIPO_CONTRATO", "CONTRATO");
	}

	private static void registerAliases(String canonical, String... aliases) {
		CANONICAL_NAMES.add(canonical);
		for (String alias : aliases) {
			ALIAS_MAP.put(alias, canonical);
		}
	}

	public static String normalize(String header) {
		if (header == null) return "";
		String upper = header.trim().toUpperCase();
		if (CANONICAL_NAMES.contains(upper)) {
			return upper;
		}
		return ALIAS_MAP.getOrDefault(upper, upper);
	}

	public static String normalizeHeaderLine(String headerLine, String separator) {
		if (headerLine == null || headerLine.isEmpty()) return headerLine;
		String[] headers = headerLine.split(separator, -1);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < headers.length; i++) {
			if (i > 0) sb.append(separator);
			sb.append(normalize(headers[i]));
		}
		return sb.toString();
	}
}
