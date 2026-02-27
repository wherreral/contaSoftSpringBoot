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
				String headerName = HeaderAliases.normalize(getCellAsString(cell).trim());
				if (!headerName.isEmpty()) {
					headerMap.put(headerName, i);
				}
			}
		}

		System.out.println("=== ExcelParser headerMap: " + headerMap.keySet() + " ===");
		System.out.println("=== Contiene ALCANCE_LIQUIDO? " + headerMap.containsKey("ALCANCE_LIQUIDO") + " ===");

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
			detail.setMovilizacion(getDoubleValue(row, headerMap, "MOVILIZACION"));
			detail.setColacion(getDoubleValue(row, headerMap, "COLACION"));
			detail.setDescuentoHerramientas(getDoubleValue(row, headerMap, "DESGASTE"));

			detail.setDescApvCtaAh(getDoubleValue(row, headerMap, "DESC_APV_CTA_AH"));
			detail.setDescPtmoCcaaff(getDoubleValue(row, headerMap, "DESC_PTMO_CCAAFF"));
			detail.setDescPtmoSolidario(getDoubleValue(row, headerMap, "DESC_PTMO_SOLIDARIO"));

			if (headerMap.containsKey("AFC")) {
				detail.setAfc(getDoubleValue(row, headerMap, "AFC"));
			}
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
				return cell.getStringCellValue();
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

		switch (cell.getCellType()) {
			case NUMERIC:
				return cell.getNumericCellValue();
			case STRING:
				String val = cell.getStringCellValue().trim().replace(",", ".");
				if (val.isEmpty()) return 0.0;
				return Double.parseDouble(val);
			default:
				return 0.0;
		}
	}
}
