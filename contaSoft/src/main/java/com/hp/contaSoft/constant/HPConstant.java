package com.hp.contaSoft.constant;

import java.util.EnumMap;
import java.util.Map;

public class HPConstant {

	public static final String FILE_EXTENSION = "csv";
	public static final java.util.Set<String> VALID_FILE_EXTENSIONS =
		new java.util.HashSet<>(java.util.Arrays.asList("csv", "xls", "xlsx"));
	public static final String REPORT_EXTENSION = "pdf";
	public static final String PAYROLL_FILE_STATUS = "Pendiente";
	public static final int SLEEP_TIME = 0;
	
	public static Map<Meses, String> monthMap = new EnumMap<Meses, String>(Meses.class);
	static {
		monthMap.put(Meses.ENERO, "enero");
		monthMap.put(Meses.FEBRERO, "febrero");
		monthMap.put(Meses.MARZO, "marzo");
		monthMap.put(Meses.ABRIL, "abril");
		monthMap.put(Meses.MAYO, "mayo");
		monthMap.put(Meses.JUNIO, "JUNIO");
		monthMap.put(Meses.JULIO, "julio");
		monthMap.put(Meses.AGOSTO, "agosto");
		monthMap.put(Meses.SEPTIEMBRE, "septiembre");
		monthMap.put(Meses.OCTUBRE, "octubre");
		monthMap.put(Meses.NOVIEMBRE, "noviembre");
		monthMap.put(Meses.DICIEMBRE, "diciembre");
	}
}
