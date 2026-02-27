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
import com.hp.contaSoft.hibernate.dao.repositories.SubsidiaryRepository;
import com.hp.contaSoft.hibernate.entities.PayBookDetails;
import com.hp.contaSoft.hibernate.entities.PayBookInstance;
import com.hp.contaSoft.hibernate.entities.Subsidiary;
import com.hp.contaSoft.hibernate.entities.Taxpayer;

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

    @Autowired
    private SubsidiaryRepository subsidiaryRepository;
    
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

            // Paso 3: Resolver subsidiaries y mapear datos con totales por registro
            long step3Start = System.currentTimeMillis();

            // Cargar subsidiaries del taxpayer una sola vez
            Taxpayer taxpayer = opt.get().getTaxpayer();
            List<Subsidiary> subs = (taxpayer != null)
                ? subsidiaryRepository.findByTaxpayerId(taxpayer.getId())
                : new ArrayList<>();

            Collection<Map<String, ?>> mappedDetails = mapDetailsToCollection(details, taxpayer, subs);
            
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
    private Collection<Map<String, ?>> mapDetailsToCollection(
            List<com.hp.contaSoft.excel.entities.PayBookDetails> details,
            Taxpayer taxpayer,
            List<Subsidiary> subs) {
        if (details == null || details.isEmpty()) {
            return new ArrayList<>();
        }

        Collection<Map<String, ?>> mappedDetails = new ArrayList<>(details.size());
        for (com.hp.contaSoft.excel.entities.PayBookDetails d : details) {
            Map<String, Object> m = new HashMap<>(60);

            // Datos básicos
            m.put("id", d.getId() != null ? d.getId() : 0L);
            m.put("rut", d.getRut() != null ? d.getRut() : "");
            m.put("centroCosto", d.getCentroCosto() != null ? d.getCentroCosto() : "");
            m.put("diasTrabajados", d.getDiasTrabajados());
            m.put("prevision", toTitleCase(d.getPrevision()));
            m.put("salud", toTitleCase(d.getSalud()));
            m.put("saludPorcentaje", d.getSaludPorcentaje() / 100);

            // Remuneración Imponible
            m.put("sueldoBase", roundUp(d.getSueldoBase()));
            m.put("sueldoMensual", roundUp(d.getSueldoMensual()));
            m.put("gratificacion", roundUp(d.getGratificacion()));
            m.put("bonoProduccion", roundUp(d.getBonoProduccion()));
            m.put("horasExtra", roundUp(d.getHorasExtra()));
            m.put("totalHoraExtra", roundUp(d.getTotalHoraExtra()));
            m.put("totalImponible", roundUp(d.getTotalImponible()));

            // Remuneración No Imponible
            m.put("colacion", roundUp(d.getColacion()));
            m.put("movilizacion", roundUp(d.getMovilizacion()));
            m.put("aguinaldo", roundUp(d.getAguinaldo()));
            m.put("asignacionFamiliar", d.getAsignacionFamiliar());
            m.put("totalAsignacionFamiliar", roundUp(d.getTotalAsignacionFamiliar()));
            m.put("descuentoHerramientas", roundUp(d.getDescuentoHerramientas()));
            m.put("totalHaber", roundUp(d.getTotalHaber()));

            // Descuentos Previsionales
            m.put("porcentajePrevision", d.getPorcentajePrevision() / 100);
            m.put("valorPrevision", roundUp(d.getValorPrevision()));
            m.put("valorSalud", roundUp(d.getValorSalud()));
            m.put("valorAFC", roundUp(d.getValorAFC()));
            m.put("valorSeguroOAccidentes", roundUp(d.getValorSeguroOAccidentes()));
            m.put("rentaLiquidaImponible", roundUp(d.getRentaLiquidaImponible()));

            // Descuentos Personales
            m.put("afc", roundUp(d.getAfc()));
            m.put("apv", d.getApv());
            m.put("prestamos", roundUp(d.getPrestamos()));
            m.put("seguroOncologico", roundUp(d.getSeguroOncologico()));
            m.put("seguroOAccidentes", d.getSeguroOAccidentes() != null ? d.getSeguroOAccidentes() : "");
            m.put("valorIUT", roundUp(d.getValorIUT()));
            m.put("anticipo", roundUp(d.getAnticipo()));
            m.put("descApvCtaAh", roundUp(d.getDescApvCtaAh()));
            m.put("descPtmoCcaaff", roundUp(d.getDescPtmoCcaaff()));
            m.put("descPtmoSolidario", roundUp(d.getDescPtmoSolidario()));

            // Otros campos
            m.put("valorHora", roundUp(d.getValorHora()));

            // Aportes empleador
            m.put("afcEmpleador", roundUp(d.getAfcEmpleador()));
            m.put("sisEmpleador", roundUp(d.getSisEmpleador()));

            // === Totales calculados por registro ===

            double totalRemuneracionNoImponible = roundUp(d.getColacion()) +
                roundUp(d.getMovilizacion()) + roundUp(d.getTotalAsignacionFamiliar()) + roundUp(d.getDescuentoHerramientas());
            m.put("totalRemuneracionNoImponible", roundUp(totalRemuneracionNoImponible));

            double totalDescuentosPrevisionales = roundUp(d.getValorPrevision()) +
                roundUp(d.getValorSalud()) + roundUp(d.getValorAFC());
            m.put("totalDescuentosPrevisionales", roundUp(totalDescuentosPrevisionales));

            double totalDescuentosPersonales = roundUp(d.getDescApvCtaAh()) +
                roundUp(d.getDescPtmoCcaaff()) + roundUp(d.getDescPtmoSolidario()) + roundUp(d.getValorIUT());
            m.put("totalDescuentosPersonales", roundUp(totalDescuentosPersonales));

            // Alcance líquido: usar valor del CSV si existe, sino calcular
            double alcanceLiquido;
            if (d.getAlcanceLiquido() != null && d.getAlcanceLiquido() > 0) {
                alcanceLiquido = roundUp(d.getAlcanceLiquido());
            } else {
                alcanceLiquido = roundUp(d.getTotalHaber() - totalDescuentosPrevisionales);
            }
            m.put("alcanceLiquido", alcanceLiquido);

            // Líquido a pagar = alcance líquido - descuentos personales - anticipos
            double liquidoAPagar = alcanceLiquido - roundUp(totalDescuentosPersonales) - roundUp(d.getAnticipo());
            m.put("liquidoAPagar", roundUp(liquidoAPagar));
            m.put("liquidoEnPalabras", convertirMontoPalabras(liquidoAPagar));

            // Total costo empleador
            m.put("totalCostoEmpleador", roundUp(d.getTotalCostoEmpleador()));

            // === Empresa y sucursal por centroCosto ===
            String empresaNombre = "";
            String empresaRut = "";
            String subsidiaryNombre = "";

            if (taxpayer != null) {
                String centroCosto = d.getCentroCosto();
                if (centroCosto != null && !centroCosto.trim().isEmpty()) {
                    String cc = centroCosto.trim();
                    Subsidiary matched = null;
                    for (Subsidiary sub : subs) {
                        if (sub.getName() != null && sub.getName().trim().equalsIgnoreCase(cc)) {
                            matched = sub;
                            break;
                        }
                    }
                    if (matched == null) {
                        for (Subsidiary sub : subs) {
                            if (sub.getSubsidiaryId() != null && sub.getSubsidiaryId().trim().equalsIgnoreCase(cc)) {
                                matched = sub;
                                break;
                            }
                        }
                    }
                    if (matched != null) {
                        empresaNombre = taxpayer.getName();
                        empresaRut = taxpayer.getRut();
                        subsidiaryNombre = matched.getName();
                    }
                }
            }
            m.put("empresaNombre", empresaNombre);
            m.put("empresaRut", empresaRut);
            m.put("subsidiaryNombre", subsidiaryNombre);

            mappedDetails.add(m);
        }
        return mappedDetails;
    }

    private static final String[] UNIDADES = {
        "", "UN", "DOS", "TRES", "CUATRO", "CINCO", "SEIS", "SIETE", "OCHO", "NUEVE",
        "DIEZ", "ONCE", "DOCE", "TRECE", "CATORCE", "QUINCE",
        "DIECISEIS", "DIECISIETE", "DIECIOCHO", "DIECINUEVE", "VEINTE",
        "VEINTIUN", "VEINTIDOS", "VEINTITRES", "VEINTICUATRO", "VEINTICINCO",
        "VEINTISEIS", "VEINTISIETE", "VEINTIOCHO", "VEINTINUEVE"
    };

    private static final String[] DECENAS = {
        "", "", "", "TREINTA", "CUARENTA", "CINCUENTA",
        "SESENTA", "SETENTA", "OCHENTA", "NOVENTA"
    };

    private static final String[] CENTENAS = {
        "", "CIENTO", "DOSCIENTOS", "TRESCIENTOS", "CUATROCIENTOS", "QUINIENTOS",
        "SEISCIENTOS", "SETECIENTOS", "OCHOCIENTOS", "NOVECIENTOS"
    };

    private String convertirMontoPalabras(double monto) {
        long entero = Math.round(Math.abs(monto));
        if (entero == 0) {
            return "CERO PESOS";
        }
        String resultado = convertirNumero(entero).trim();
        return resultado + " PESOS";
    }

    private String convertirNumero(long numero) {
        if (numero == 0) return "";
        if (numero == 100) return "CIEN";

        if (numero < 30) {
            return UNIDADES[(int) numero];
        }
        if (numero < 100) {
            int decena = (int) (numero / 10);
            int unidad = (int) (numero % 10);
            return DECENAS[decena] + (unidad > 0 ? " Y " + UNIDADES[unidad] : "");
        }
        if (numero < 1000) {
            int centena = (int) (numero / 100);
            long resto = numero % 100;
            return CENTENAS[centena] + (resto > 0 ? " " + convertirNumero(resto) : "");
        }
        if (numero < 1000000) {
            long miles = numero / 1000;
            long resto = numero % 1000;
            String milesStr = (miles == 1) ? "MIL" : convertirNumero(miles) + " MIL";
            return milesStr + (resto > 0 ? " " + convertirNumero(resto) : "");
        }
        if (numero < 1000000000L) {
            long millones = numero / 1000000;
            long resto = numero % 1000000;
            String millonesStr = (millones == 1) ? "UN MILLON" : convertirNumero(millones) + " MILLONES";
            return millonesStr + (resto > 0 ? " " + convertirNumero(resto) : "");
        }
        return String.valueOf(numero);
    }

    /**
     * Trunca a 2 decimales y redondea al alza (ceil) para obtener un entero.
     * Ej: 539000.833 -> truncar -> 539000.83 -> ceil -> 539001
     */
    private double roundUp(double value) {
        double truncated = Math.floor(value * 100) / 100;
        return Math.ceil(truncated);
    }

    private String toTitleCase(String text) {
        if (text == null || text.isEmpty()) return "";
        String[] words = text.trim().toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return sb.toString();
    }
}
