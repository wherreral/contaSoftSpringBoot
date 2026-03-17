package com.hp.contaSoft.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.hp.contaSoft.excel.entities.PayBookDetails;

public class ExcelParser {

	/**
	 * Parsea un archivo Excel (xls o xlsx) y produce List<PayBookDetails>,
	 * equivalente a lo que CsvToBeanBuilder produce con @CsvBindByName.
	 */
	public static List<PayBookDetails> parse(InputStream inputStream) throws Exception {
		long start = System.currentTimeMillis();
		List<PayBookDetails> result = new ArrayList<>();

		Workbook workbook = WorkbookFactory.create(inputStream);
		Sheet sheet = workbook.getSheetAt(0);

		// Leer header row para construir mapa columna → índice
		Row headerRow = sheet.getRow(0);
		if (headerRow == null) {
			workbook.close();
			throw new IllegalArgumentException("El archivo Excel no tiene cabecera en la primera fila");
		}

		Map<String, Integer> headerMap = new HashMap<>();
		for (int i = 0; i < headerRow.getLastCellNum(); i++) {
			Cell cell = headerRow.getCell(i);
			if (cell != null) {
				String raw = getCellAsString(cell).trim();
				String headerName = HeaderAliases.normalize(raw);
				if (!headerName.isEmpty()) {
					headerMap.put(headerName, i);
					if (!raw.equalsIgnoreCase(headerName)) {
						System.out.println("ExcelParser header[" + i + "]: '" + raw + "' -> '" + headerName + "'");
					}
				}
			}
		}

		System.out.println("=== ExcelParser headerMap: " + headerMap.keySet() + " ===");
		System.out.println("=== Contiene MOVILIZACION? " + headerMap.containsKey("MOVILIZACION") + " ===");
		System.out.println("=== Contiene COLACION? " + headerMap.containsKey("COLACION") + " ===");
		System.out.println("=== Contiene DESGASTE? " + headerMap.containsKey("DESGASTE") + " ===");

		// Procesar filas de datos (desde fila 1)
		for (int rowIdx = 1; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
			Row row = sheet.getRow(rowIdx);
			if (row == null) continue;

			// Saltar filas completamente vacías
			boolean hasData = false;
			for (int c = 0; c < row.getLastCellNum(); c++) {
				Cell cell = row.getCell(c);
				if (cell != null && cell.getCellType() != CellType.BLANK) {
					hasData = true;
					break;
				}
			}
			if (!hasData) continue;

			PayBookDetails detail = new PayBookDetails();

			// Campos requeridos
			detail.setRut(getStringValue(row, headerMap, "RUT"));
			detail.setNombreTrabajador(getStringValue(row, headerMap, "NOMBRE_TRABAJADOR"));
			detail.setCentroCosto(getStringValue(row, headerMap, "CENTRO_COSTO"));
			detail.setSueldoBase(getDoubleValue(row, headerMap, "SUELDO_BASE"));
			detail.setDiasTrabajados((int) getDoubleValue(row, headerMap, "DT"));
			detail.setPrevision(getStringValue(row, headerMap, "PREVISION"));
			detail.setSalud(getStringValue(row, headerMap, "SALUD"));
			detail.setSaludPorcentaje(getDoubleValue(row, headerMap, "SALUD_PORCENTAJE"));

			// Campos opcionales
			detail.setBonoProduccion(getDoubleValue(row, headerMap, "BONO"));
			detail.setHorasExtra(getDoubleValue(row, headerMap, "HORAS_EXTRA"));
			detail.setAsignacionFamiliar((int) getDoubleValue(row, headerMap, "ASIG_FAMILIAR"));
			double movi = getDoubleValue(row, headerMap, "MOVILIZACION");
			double colac = getDoubleValue(row, headerMap, "COLACION");
			double desg = getDoubleValue(row, headerMap, "DESGASTE");
			detail.setMovilizacion(movi);
			detail.setColacion(colac);
			detail.setDescuentoHerramientas(desg);

			detail.setDescApvCtaAh(getDoubleValue(row, headerMap, "DESC_APV_CTA_AH"));
			detail.setDescPtmoCcaaff(getDoubleValue(row, headerMap, "DESC_PTMO_CCAAFF"));
			detail.setDescPtmoSolidario(getDoubleValue(row, headerMap, "DESC_PTMO_SOLIDARIO"));

			// AFC del archivo es un monto calculado, no un porcentaje.
			// No lo seteamos como afc (porcentaje). Se guarda solo para auditoría más abajo.
			if (headerMap.containsKey("ALCANCE_LIQUIDO")) {
				double al = getDoubleValue(row, headerMap, "ALCANCE_LIQUIDO");
				System.out.println("ExcelParser ALCANCE_LIQUIDO raw=" + al + " para RUT=" + detail.getRut());
				detail.setAlcanceLiquido(al > 0 ? al : null);
			} else {
				System.out.println("ExcelParser: columna ALCANCE_LIQUIDO NO encontrada en headerMap");
			}
			if (headerMap.containsKey("REGIMEN")) {
				detail.setRegimen(getStringValue(row, headerMap, "REGIMEN"));
			}

			// Anticipos y Mutual (campos opcionales del archivo)
			if (headerMap.containsKey("ANTICIPOS")) {
				detail.setAnticipo(getDoubleValue(row, headerMap, "ANTICIPOS"));
			}
			if (headerMap.containsKey("MUTUAL")) {
				detail.setMutual(getDoubleValue(row, headerMap, "MUTUAL"));
			}

			// Campos calculados — guardar valor original del archivo para auditoría
			if (headerMap.containsKey("SIS")) {
				double sisArchivo = getDoubleValue(row, headerMap, "SIS");
				if (sisArchivo != 0) {
					detail.setSisArchivoOriginal(sisArchivo);
				}
			}
			if (headerMap.containsKey("IUT")) {
				double iutArchivo = getDoubleValue(row, headerMap, "IUT");
				if (iutArchivo != 0) {
					detail.setIutArchivoOriginal(iutArchivo);
				}
			}
			if (headerMap.containsKey("AFC")) {
				double afcArchivo = getDoubleValue(row, headerMap, "AFC");
				if (afcArchivo != 0) {
					detail.setAfcArchivoOriginal(afcArchivo);
				}
			}

			result.add(detail);
		}

		workbook.close();

		long elapsed = System.currentTimeMillis() - start;
		System.out.println("Excel parsed: " + result.size() + " rows in " + elapsed + "ms");

		return result;
	}

	private static String getCellAsString(Cell cell) {
		if (cell == null) return "";
		switch (cell.getCellType()) {
			case STRING:
				return cell.getStringCellValue();
			case NUMERIC:
				double num = cell.getNumericCellValue();
				if (num == Math.floor(num) && !Double.isInfinite(num)) {
					return String.valueOf((long) num);
				}
				return String.valueOf(num);
			case BOOLEAN:
				return String.valueOf(cell.getBooleanCellValue());
			case FORMULA:
				// Evaluar el valor cacheado de la fórmula
				switch (cell.getCachedFormulaResultType()) {
					case STRING:
						return cell.getStringCellValue();
					case NUMERIC:
						double fnum = cell.getNumericCellValue();
						if (fnum == Math.floor(fnum) && !Double.isInfinite(fnum)) {
							return String.valueOf((long) fnum);
						}
						return String.valueOf(fnum);
					case BOOLEAN:
						return String.valueOf(cell.getBooleanCellValue());
					default:
						return "";
				}
			default:
				return "";
		}
	}

	private static String getStringValue(Row row, Map<String, Integer> headerMap, String column) {
		Integer idx = headerMap.get(column);
		if (idx == null) return "";
		Cell cell = row.getCell(idx);
		return getCellAsString(cell).trim();
	}

	private static double getDoubleValue(Row row, Map<String, Integer> headerMap, String column) {
		Integer idx = headerMap.get(column);
		if (idx == null) return 0.0;
		Cell cell = row.getCell(idx);
		if (cell == null) return 0.0;

		CellType effectiveType = cell.getCellType();
		if (effectiveType == CellType.FORMULA) {
			effectiveType = cell.getCachedFormulaResultType();
		}

		switch (effectiveType) {
			case NUMERIC:
				return cell.getNumericCellValue();
			case STRING:
				String val = cell.getStringCellValue().trim();
				if (val.isEmpty()) return 0.0;
				// Remover símbolo de moneda y espacios
				val = val.replace("$", "").replace(" ", "").trim();
				if (val.equals("-") || val.equals("--") || val.isEmpty()) return 0.0;
				if (val.contains(",") && val.contains(".")) {
					if (val.lastIndexOf(",") > val.lastIndexOf(".")) {
						val = val.replace(".", "").replace(",", ".");
					} else {
						val = val.replace(",", "");
					}
				} else if (val.contains(",")) {
					String[] parts = val.split(",");
					if (parts.length == 2 && parts[1].length() == 3) {
						val = val.replace(",", "");
					} else {
						val = val.replace(",", ".");
					}
				}
				return Double.parseDouble(val);
			default:
				return 0.0;
		}
	}
}
