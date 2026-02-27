package com.hp.contaSoft.spring.controller;

import com.hp.contaSoft.custom.CurrentUser;
import com.hp.contaSoft.report.monthly.MonthlyClientSummaryReportData;
import com.hp.contaSoft.report.monthly.MonthlyClientSummaryReportService;
import com.hp.contaSoft.report.monthly.MonthlyClientSummaryRequest;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class MonthlyClientSummaryReportController {

    private static final Logger logger = LoggerFactory.getLogger(MonthlyClientSummaryReportController.class);

    @Autowired
    private MonthlyClientSummaryReportService monthlyClientSummaryReportService;

    private JasperReport cachedJasperReport;

    @PostConstruct
    public void init() {
        try {
            ClassPathResource jrxml = new ClassPathResource("reports/MonthlyClientSummaryReport.jrxml");
            try (InputStream input = jrxml.getInputStream()) {
                cachedJasperReport = JasperCompileManager.compileReport(input);
                logger.info("MonthlyClientSummaryReport compilado y cacheado");
            }
        } catch (Exception e) {
            logger.error("Error compilando MonthlyClientSummaryReport en inicialización", e);
            cachedJasperReport = null;
        }
    }

    @PostMapping(value = "/monthly-client-summary/pdf")
    public ResponseEntity<byte[]> generateMonthlyClientSummaryPdf(@RequestBody MonthlyClientSummaryRequest request,
                                                                  Authentication authentication) {
        try {
            String familyId = null;
            String generatedBy = "sistema";
            if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CurrentUser) {
                CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
                familyId = currentUser.getFamilId();
                generatedBy = currentUser.getUsername();
            }
            if (familyId == null || familyId.trim().isEmpty()) {
                return ResponseEntity.status(401)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(toJsonBytes(error("No autenticado")));
            }

            final MonthlyClientSummaryReportData reportData =
                    monthlyClientSummaryReportService.buildReportData(request, familyId, generatedBy);

            JasperReport jasperReport = cachedJasperReport;
            if (jasperReport == null) {
                ClassPathResource jrxml = new ClassPathResource("reports/MonthlyClientSummaryReport.jrxml");
                try (InputStream input = jrxml.getInputStream()) {
                    jasperReport = JasperCompileManager.compileReport(input);
                }
            }

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("clientName", reportData.getClientName());
            params.put("clientRut", reportData.getClientRut());
            params.put("periodLabel", reportData.getPeriodLabel());
            params.put("generatedAt", reportData.getGeneratedAt());
            params.put("generatedBy", reportData.getGeneratedBy());

            params.put("workersCount", reportData.getWorkersCount());
            params.put("totalImponible", reportData.getTotalImponible());
            params.put("totalNoImponible", reportData.getTotalNoImponible());
            params.put("totalHaber", reportData.getTotalHaber());
            params.put("totalDctoPrevisional", reportData.getTotalDctoPrevisional());
            params.put("totalDctoPersonal", reportData.getTotalDctoPersonal());
            params.put("totalDescuentos", reportData.getTotalDescuentos());
            params.put("totalAlcanceLiquido", reportData.getTotalAlcanceLiquido());

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData.getRows());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
            byte[] pdfBytes;
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                JasperExportManager.exportReportToPdfStream(jasperPrint, out);
                pdfBytes = out.toByteArray();
            }

            String fileName = "resumen_mensual_cliente_" +
                    request.getClientId() + "_" +
                    (request.getMonth() != null ? request.getMonth() : "mes") + "_" +
                    (request.getYear() != null ? request.getYear() : "anio") + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(toJsonBytes(error(ex.getMessage())));
        } catch (Exception ex) {
            logger.error("Error generando resumen mensual por cliente", ex);
            return ResponseEntity.status(500)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(toJsonBytes(error("Error generando reporte")));
        }
    }

    private Map<String, Object> error(String message) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("success", false);
        m.put("message", message);
        return m;
    }

    private byte[] toJsonBytes(Map<String, Object> payload) {
        String message = payload.get("message") != null ? String.valueOf(payload.get("message")) : "";
        String json = "{\"success\":false,\"message\":\"" + escapeJson(message) + "\"}";
        return json.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
