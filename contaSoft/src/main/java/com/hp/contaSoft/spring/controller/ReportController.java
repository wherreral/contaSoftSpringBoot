package com.hp.contaSoft.spring.controller;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.hp.contaSoft.hibernate.dao.repositories.PayBookDetailsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.PayBookInstanceRepository;
import com.hp.contaSoft.hibernate.entities.PayBookDetails;
import com.hp.contaSoft.hibernate.entities.PayBookInstance;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private PayBookInstanceRepository payBookInstanceRepository;

    @Autowired
    private PayBookDetailsRepository payBookDetailsRepository;
    
    // Cache del reporte compilado para evitar compilación en cada request
    private JasperReport cachedJasperReport;
    
    /**
     * Compila el reporte una sola vez al inicializar el controlador
     */
    @PostConstruct
    public void init() {
        try {
            long start = System.currentTimeMillis();
            ClassPathResource jrxml = new ClassPathResource("PayCheckReportNew.jrxml");
            try (InputStream jrxmlInput = jrxml.getInputStream()) {
                cachedJasperReport = JasperCompileManager.compileReport(jrxmlInput);
                long elapsed = System.currentTimeMillis() - start;
                logger.info("✅ JasperReport compilado y cacheado en {} ms", elapsed);
            }
        } catch (Exception e) {
            logger.error("❌ Error al compilar JasperReport en inicialización", e);
            cachedJasperReport = null;
        }
    }

    @GetMapping(value = "/paybook/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<StreamingResponseBody> getPaybookPdf(@PathVariable("id") Long id) {
        long startTotal = System.currentTimeMillis();
        try {
            // Paso 1: Consultar PayBookInstance
            long step1Start = System.currentTimeMillis();
            Optional<PayBookInstance> opt = payBookInstanceRepository.findById(id);
            long step1Time = System.currentTimeMillis() - step1Start;
            logger.info("⏱️  [1/4] Consulta PayBookInstance: {} ms", step1Time);
           
            if (!opt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // Paso 2: Consultar detalles
            long step2Start = System.currentTimeMillis();
            List<com.hp.contaSoft.excel.entities.PayBookDetails> details = 
                payBookDetailsRepository.findAllByPayBookInstance_Id(id);
            long step2Time = System.currentTimeMillis() - step2Start;
            logger.info("⏱️  [2/4] Consulta detalles ({} registros): {} ms", 
                details != null ? details.size() : 0, step2Time);

            // Paso 3: Mapear datos y calcular totales
            long step3Start = System.currentTimeMillis();
            Collection<Map<String, ?>> mappedDetails = mapDetailsToCollection(details);
            
            // Calcular totales para parámetros del reporte
            double totalRemuneracionNoImponible = 0.0;
            double totalDescuentosPrevisionales = 0.0;
            double totalDescuentosPersonales = 0.0;
            double alcanceLiquido = 0.0;
            double liquidoAPagar = 0.0;
            String nombreTrabajador = "Nombre del Trabajador";
            
            if (details != null && !details.isEmpty()) {
                com.hp.contaSoft.excel.entities.PayBookDetails firstDetail = details.get(0);
                
                // Calcular total remuneración no imponible
                totalRemuneracionNoImponible = firstDetail.getColacion() + 
                                               firstDetail.getMovilizacion() + 
                                               firstDetail.getTotalAsignacionFamiliar();
                
                // Calcular total descuentos previsionales
                totalDescuentosPrevisionales = firstDetail.getValorPrevision() + 
                                               firstDetail.getValorSalud() + 
                                               firstDetail.getValorAFC();
                
                // Calcular total descuentos personales
                totalDescuentosPersonales = firstDetail.getApv() + 
                                            firstDetail.getPrestamos() + 
                                            firstDetail.getValorIUT();
                
                // Calcular alcance líquido
                alcanceLiquido = firstDetail.getTotalHaber() - totalDescuentosPrevisionales - totalDescuentosPersonales;
                
                // Calcular líquido a pagar
                liquidoAPagar = alcanceLiquido - firstDetail.getAnticipo();
                
                // Obtener nombre del trabajador (deberías obtenerlo de la BD)
                nombreTrabajador = "Alex Antonio Gomez Peña"; // TODO: Obtener desde BD
            }
            
            long step3Time = System.currentTimeMillis() - step3Start;
            logger.info("⏱️  [3/4] Mapeo de datos y cálculos: {} ms", step3Time);

            // Paso 4: Generar PDF
            long step4Start = System.currentTimeMillis();
            
            // Usar reporte cacheado o compilar si no existe
            JasperReport jasperReport = cachedJasperReport;
            if (jasperReport == null) {
                logger.warn("⚠️  Cache de JasperReport no disponible, compilando...");
                ClassPathResource jrxml = new ClassPathResource("PayCheckReportNew.jrxml");
                try (InputStream jrxmlInput = jrxml.getInputStream()) {
                    jasperReport = JasperCompileManager.compileReport(jrxmlInput);
                }
            }

            // datasource y params
            JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(mappedDetails);
            Map<String, Object> params = new HashMap<>();
            params.put("payBookInstance", opt.get());
            params.put("empresaNombre", "EMPRESA CONTRATISTA TRES ALVAREZ LIMITADA");
            params.put("empresaRut", "76.456.320-4");
            params.put("numeroDocumento", 1);
            params.put("nombreTrabajador", nombreTrabajador);
            params.put("totalRemuneracionNoImponible", totalRemuneracionNoImponible);
            params.put("totalDescuentosPrevisionales", totalDescuentosPrevisionales);
            params.put("totalDescuentosPersonales", totalDescuentosPersonales);
            params.put("alcanceLiquido", alcanceLiquido);
            params.put("liquidoAPagar", liquidoAPagar);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
            long step4Time = System.currentTimeMillis() - step4Start;
            logger.info("⏱️  [4/4] Generación JasperPrint: {} ms", step4Time);

            StreamingResponseBody stream = out -> {
                long exportStart = System.currentTimeMillis();
                try {
                    JasperExportManager.exportReportToPdfStream(jasperPrint, out);
                    long exportTime = System.currentTimeMillis() - exportStart;
                    logger.info("⏱️  Exportación a PDF: {} ms", exportTime);
                } catch (JRException e) {
                    logger.error("Error exporting Jasper report to stream", e);
                    throw new RuntimeException(e);
                }
            };

            long totalTime = System.currentTimeMillis() - startTotal;
            logger.info("⏱️  ========================================");
            logger.info("⏱️  TIEMPO TOTAL (preparación): {} ms", totalTime);
            logger.info("⏱️  ========================================");

            String fileName = "paybook-" + id + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(stream);

        } catch (Exception e) {
            logger.error("Error generating paybook PDF for id {}", id, e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * Método optimizado para mapear detalles a colección con todos los campos
     */
    private Collection<Map<String, ?>> mapDetailsToCollection(List<com.hp.contaSoft.excel.entities.PayBookDetails> details) {
        if (details == null || details.isEmpty()) {
            return new ArrayList<>();
        }
        
        Collection<Map<String, ?>> mappedDetails = new ArrayList<>(details.size());
        for (com.hp.contaSoft.excel.entities.PayBookDetails d : details) {
            Map<String, Object> m = new HashMap<>(40); // tamaño ampliado para todos los campos
            
            // Datos básicos
            m.put("id", d.getId());
            m.put("rut", d.getRut());
            m.put("centroCosto", d.getCentroCosto());
            m.put("diasTrabajados", d.getDiasTrabajados());
            m.put("prevision", d.getPrevision());
            m.put("salud", d.getSalud());
            m.put("saludPorcentaje", d.getSaludPorcentaje());
            
            // Remuneración Imponible
            m.put("sueldoBase", d.getSueldoBase());
            m.put("sueldoMensual", d.getSueldoMensual());
            m.put("gratificacion", d.getGratificacion());
            m.put("bonoProduccion", d.getBonoProduccion());
            m.put("horasExtra", d.getHorasExtra());
            m.put("totalHoraExtra", d.getTotalHoraExtra());
            m.put("totalImponible", d.getTotalImponible());
            
            // Remuneración No Imponible
            m.put("colacion", d.getColacion());
            m.put("movilizacion", d.getMovilizacion());
            m.put("aguinaldo", d.getAguinaldo());
            m.put("asignacionFamiliar", d.getAsignacionFamiliar());
            m.put("totalAsignacionFamiliar", d.getTotalAsignacionFamiliar());
            m.put("descuentoHerramientas", d.getDescuentoHerramientas());
            m.put("totalHaber", d.getTotalHaber());
            
            // Descuentos Previsionales
            m.put("porcentajePrevision", d.getPorcentajePrevision());
            m.put("valorPrevision", d.getValorPrevision());
            m.put("valorSalud", d.getValorSalud());
            m.put("valorAFC", d.getValorAFC());
            m.put("valorSeguroOAccidentes", d.getValorSeguroOAccidentes());
            m.put("rentaLiquidaImponible", d.getRentaLiquidaImponible());
            
            // Descuentos Personales
            m.put("afc", d.getAfc());
            m.put("apv", d.getApv());
            m.put("prestamos", d.getPrestamos());
            m.put("seguroOncologico", d.getSeguroOncologico());
            m.put("seguroOAccidentes", d.getSeguroOAccidentes());
            m.put("valorIUT", d.getValorIUT());
            m.put("anticipo", d.getAnticipo());
            
            // Otros campos
            m.put("valorHora", d.getValorHora());
            
            mappedDetails.add(m);
        }
        return mappedDetails;
    }
}
