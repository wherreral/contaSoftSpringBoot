package com.hp.contaSoft.hibernate.dao.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.contaSoft.excel.entities.PayBookDetailProcessed;
import com.hp.contaSoft.excel.entities.PayBookDetails;
import com.hp.contaSoft.hibernate.dao.repositories.PayBookDetailProcessedRepository;
import com.hp.contaSoft.hibernate.dao.repositories.PayBookDetailsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.PayBookInstanceRepository;
import com.hp.contaSoft.hibernate.dao.repositories.PayBookProcessedRepository;
import com.hp.contaSoft.hibernate.entities.PayBookInstance;
import com.hp.contaSoft.hibernate.entities.PayBookProcessed;

@Service
public class ProcessingService {

	private static final Logger logger = LoggerFactory.getLogger(ProcessingService.class);

	@Autowired
	private PayBookInstanceRepository payBookInstanceRepository;

	@Autowired
	private PayBookDetailsRepository payBookDetailsRepository;

	@Autowired
	private PayBookProcessedRepository payBookProcessedRepository;

	@Autowired
	private PayBookDetailProcessedRepository payBookDetailProcessedRepository;

	@Autowired
	private ReportUtilsService reportUtilsService;

	@Transactional
	public ProcessResult processPayBook(Long payBookInstanceId, String familyId) {
		// 1. Load and validate
		PayBookInstance pbi = payBookInstanceRepository.findByIdAndFamilyId(payBookInstanceId, familyId);
		if (pbi == null) {
			return ProcessResult.error("Liquidación no encontrada");
		}

		// 2. Idempotency check
		if ("PROCESADO".equals(pbi.getStatus())) {
			return ProcessResult.error("Esta liquidación ya fue procesada");
		}

		// 3. Create PayBookProcessed snapshot
		PayBookProcessed processed = new PayBookProcessed();
		processed.setOriginalPayBookInstanceId(pbi.getId());
		processed.setFamilyId(pbi.getFamilyId());
		processed.setVersion(pbi.getVersion());
		processed.setRut(pbi.getRut());
		processed.setMonth(pbi.getMonth());
		processed.setYear(pbi.getYear());
		processed.setDetails(pbi.getDetails());
		processed.setFileName(pbi.getFileName());
		processed.setStatus("PROCESADO");
		if (pbi.getTaxpayer() != null) {
			processed.setTaxpayerId(pbi.getTaxpayer().getId());
			processed.setTaxpayerRut(pbi.getTaxpayer().getRut());
			processed.setTaxpayerName(pbi.getTaxpayer().getName());
		}
		PayBookProcessed savedProcessed = payBookProcessedRepository.save(processed);

		// 4. Copy details
		List<PayBookDetails> details = payBookDetailsRepository.findAllByPayBookInstanceIdAndFamilyId(payBookInstanceId, familyId);
		for (PayBookDetails src : details) {
			PayBookDetailProcessed dest = new PayBookDetailProcessed();
			dest.setPayBookProcessedId(savedProcessed.getId());
			dest.setFamilyId(src.getFamilyId());
			dest.setRut(src.getRut());
			dest.setNombreTrabajador(src.getNombreTrabajador());
			dest.setCentroCosto(src.getCentroCosto());
			dest.setSueldoBase(src.getSueldoBase());
			dest.setDiasTrabajados(src.getDiasTrabajados());
			dest.setPrevision(src.getPrevision());
			dest.setSalud(src.getSalud());
			dest.setSaludPorcentaje(src.getSaludPorcentaje());
			dest.setBonoProduccion(src.getBonoProduccion());
			dest.setAguinaldo(src.getAguinaldo());
			dest.setHorasExtra(src.getHorasExtra());
			dest.setAsignacionFamiliar(src.getAsignacionFamiliar());
			dest.setMovilizacion(src.getMovilizacion());
			dest.setColacion(src.getColacion());
			dest.setDescuentoHerramientas(src.getDescuentoHerramientas());
			dest.setAfc(src.getAfc());
			dest.setAlcanceLiquido(src.getAlcanceLiquido());
			dest.setApv(src.getApv());
			dest.setPrestamos(src.getPrestamos());
			dest.setSeguroOncologico(src.getSeguroOncologico());
			dest.setSeguroOAccidentes(src.getSeguroOAccidentes());
			dest.setAnticipo(src.getAnticipo());
			dest.setMutual(src.getMutual());
			dest.setDescApvCtaAh(src.getDescApvCtaAh());
			dest.setDescPtmoCcaaff(src.getDescPtmoCcaaff());
			dest.setDescPtmoSolidario(src.getDescPtmoSolidario());
			dest.setRegimen(src.getRegimen());
			// Calculated fields
			dest.setSueldoMensual(src.getSueldoMensual());
			dest.setGratificacion(src.getGratificacion());
			dest.setValorHora(src.getValorHora());
			dest.setTotalImponible(src.getTotalImponible());
			dest.setTotalHaber(src.getTotalHaber());
			dest.setPorcentajePrevision(src.getPorcentajePrevision());
			dest.setValorPrevision(src.getValorPrevision());
			dest.setValorSalud(src.getValorSalud());
			dest.setValorAFC(src.getValorAFC());
			dest.setTotalAsignacionFamiliar(src.getTotalAsignacionFamiliar());
			dest.setValorIUT(src.getValorIUT());
			dest.setValorSeguroOAccidentes(src.getValorSeguroOAccidentes());
			dest.setRentaLiquidaImponible(src.getRentaLiquidaImponible());
			dest.setTotalHoraExtra(src.getTotalHoraExtra());
			dest.setAfcEmpleador(src.getAfcEmpleador());
			dest.setSisEmpleador(src.getSisEmpleador());
			dest.setCotCapitalizacion(src.getCotCapitalizacion());
			dest.setCotRentabilidadProtegida(src.getCotRentabilidadProtegida());
			dest.setCotExpectativasVidaSis(src.getCotExpectativasVidaSis());
			payBookDetailProcessedRepository.save(dest);
		}

		// 5. Update original status
		pbi.setStatus("PROCESADO");
		payBookInstanceRepository.save(pbi);

		// 6. Generate PDF reports (non-critical)
		try {
			reportUtilsService.generateReports(payBookInstanceId);
		} catch (Exception e) {
			logger.warn("PDF generation failed for PayBookInstance {}: {}", payBookInstanceId, e.getMessage());
		}

		return ProcessResult.success(savedProcessed.getId());
	}

	public static class ProcessResult {
		private final boolean success;
		private final String message;
		private final Long processedId;

		private ProcessResult(boolean success, String message, Long processedId) {
			this.success = success;
			this.message = message;
			this.processedId = processedId;
		}

		public static ProcessResult success(Long processedId) {
			return new ProcessResult(true, "Liquidación procesada exitosamente", processedId);
		}

		public static ProcessResult error(String message) {
			return new ProcessResult(false, message, null);
		}

		public boolean isSuccess() {
			return success;
		}

		public String getMessage() {
			return message;
		}

		public Long getProcessedId() {
			return processedId;
		}
	}
}
