package com.hp.contaSoft.utils;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HeaderAliases {

	private static final Set<String> CANONICAL_NAMES = new HashSet<>();
	private static final Map<String, String> ALIAS_MAP = new HashMap<>();

	static {
		registerAliases("RUT", "RUTS", "UTS", "R.U.T");
		registerAliases("NOMBRE_TRABAJADOR", "NOMBRE_DE_LOS_TRABAJADORES", "NOMBRE_DEL_TRABAJADOR", "NOMBRE");
		registerAliases("CENTRO_COSTO", "CC", "CENTROCOSTO", "OBRA");
		registerAliases("SUELDO_BASE", "SUELDO", "SB", "SUELDOBASE");
		registerAliases("DT", "DIAS_TRABAJADOS", "DIAS");
		registerAliases("PREVISION", "AFP", "PREV", "ORGANISMO_PREVISIONAL", "A.F.P");
		registerAliases("SALUD", "ISAPRE", "ORGANISMO");
		registerAliases("SALUD_PORCENTAJE", "SALUD_PCT", "SALUDPORCENTAJE");
		registerAliases("BONO", "BONO_PRODUCCION", "BONOPRODUCCION");
		registerAliases("HORAS_EXTRA", "HE", "HORASEXTRA", "HORAS");
		registerAliases("ASIG_FAMILIAR", "AF", "ASIGFAMILIAR", "ASIGNACION_FAMILIAR");
		registerAliases("MOVILIZACION", "MOV", "MOVI");
		registerAliases("COLACION", "COL", "COLAC.");
		registerAliases("DESGASTE", "DES_HERRAMIENTA", "DESHERRAMIENTA", "DES._HERRAM");
		registerAliases("AFC", "SEGURO_CESANTIA", "AFC_E", "AFCE");
		registerAliases("ALCANCE_LIQUIDO", "AL", "ALCANCELIQUIDO", "LIQUIDO");
		registerAliases("DESC_APV_CTA_AH", "APV_CTA_AH", "APVCTAAH", "DESC_APV");
		registerAliases("DESC_PTMO_CCAAFF", "PTMO_CCAAFF", "PTMOCCAAFF", "PRESTAMO_CCAAFF");
		registerAliases("DESC_PTMO_SOLIDARIO", "PTMO_SOLIDARIO", "PTMOSOLIDARIO", "PRESTAMO_SOLIDARIO", "PTO._SOLIDARIO", "PTO_SOLIDARIO");
		registerAliases("REGIMEN", "TIPO_CONTRATO", "CONTRATO");
		registerAliases("ANTICIPOS", "ANTICIPO");
		registerAliases("MUTUAL");
		registerAliases("SIS");
		registerAliases("IUT");
		registerAliases("DSCTO.", "DSCTO");
	}

	private static void registerAliases(String canonical, String... aliases) {
		String canonicalKey = toCanonicalToken(canonical);
		CANONICAL_NAMES.add(canonicalKey);
		for (String alias : aliases) {
			ALIAS_MAP.put(toCanonicalToken(alias), canonicalKey);
		}
	}

	public static String normalize(String header) {
		String upper = toCanonicalToken(header);
		if (CANONICAL_NAMES.contains(upper)) {
			return upper;
		}
		return ALIAS_MAP.getOrDefault(upper, upper);
	}

	private static String toCanonicalToken(String value) {
		if (value == null) return "";
		String normalized = Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
				.replaceAll("\\p{M}+", "")
				.toUpperCase()
				.replaceAll("[^A-Z0-9]+", "_")
				.replaceAll("_+", "_")
				.replaceAll("^_|_$", "");
		return normalized;
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
