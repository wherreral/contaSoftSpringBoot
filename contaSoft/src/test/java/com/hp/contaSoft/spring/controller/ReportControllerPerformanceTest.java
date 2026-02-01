package com.hp.contaSoft.spring.controller;

import com.hp.contaSoft.hibernate.dao.repositories.PayBookDetailsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.PayBookInstanceRepository;
import com.hp.contaSoft.hibernate.entities.PayBookInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;

/**
 * Test de performance para medir los tiempos de generación de PDF
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ReportControllerPerformanceTest {

    private static final Logger logger = LoggerFactory.getLogger(ReportControllerPerformanceTest.class);

    @Autowired
    private ReportController reportController;

    @Autowired
    private PayBookInstanceRepository payBookInstanceRepository;

    @Autowired
    private PayBookDetailsRepository payBookDetailsRepository;

    @Test
    public void testPdfGenerationPerformance() throws Exception {
        // Obtener un PayBookInstance real de la BD
        Iterable<PayBookInstance> iterable = payBookInstanceRepository.findAll();
        List<PayBookInstance> instances = new java.util.ArrayList<>();
        iterable.forEach(instances::add);
        
        if (instances.isEmpty()) {
            logger.warn("No hay PayBookInstances en la BD para probar");
            return;
        }

        Long testId = instances.get(0).getId();
        logger.info("=== Iniciando test de performance para PayBook ID: {} ===", testId);

        // Medir tiempo total
        long startTotal = System.currentTimeMillis();

        // Paso 1: Consultar PayBookInstance
        long startQuery1 = System.currentTimeMillis();
        Optional<PayBookInstance> opt = payBookInstanceRepository.findById(testId);
        long endQuery1 = System.currentTimeMillis();
        logger.info("⏱️  Tiempo consulta PayBookInstance: {} ms", (endQuery1 - startQuery1));

        // Paso 2: Consultar detalles
        long startQuery2 = System.currentTimeMillis();
        List<com.hp.contaSoft.excel.entities.PayBookDetails> details = 
            payBookDetailsRepository.findAllByPayBookInstance_Id(testId);
        long endQuery2 = System.currentTimeMillis();
        logger.info("⏱️  Tiempo consulta detalles ({} registros): {} ms", 
            details != null ? details.size() : 0, (endQuery2 - startQuery2));

        // Paso 3: Generar PDF completo
        long startPdf = System.currentTimeMillis();
        ResponseEntity<StreamingResponseBody> response = reportController.getPaybookPdf(testId);
        long endPdf = System.currentTimeMillis();
        logger.info("⏱️  Tiempo generación PDF (preparación): {} ms", (endPdf - startPdf));

        // Paso 4: Ejecutar el streaming (esto compila el JRXML y genera el PDF)
        if (response.getBody() != null) {
            long startStream = System.currentTimeMillis();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                response.getBody().writeTo(baos);
            } catch (java.io.IOException e) {
                logger.error("Error al escribir PDF", e);
            }
            long endStream = System.currentTimeMillis();
            logger.info("⏱️  Tiempo streaming/exportación PDF: {} ms", (endStream - startStream));
            logger.info("📄 Tamaño PDF generado: {} KB", baos.size() / 1024);
        }

        long endTotal = System.currentTimeMillis();
        logger.info("⏱️  ========================================");
        logger.info("⏱️  TIEMPO TOTAL: {} ms ({} segundos)", 
            (endTotal - startTotal), (endTotal - startTotal) / 1000.0);
        logger.info("⏱️  ========================================");
    }

    @Test
    public void testMultiplePdfGenerations() throws Exception {
        Iterable<PayBookInstance> iterable = payBookInstanceRepository.findAll();
        List<PayBookInstance> instances = new java.util.ArrayList<>();
        iterable.forEach(instances::add);
        
        if (instances.isEmpty()) {
            logger.warn("No hay PayBookInstances en la BD para probar");
            return;
        }

        Long testId = instances.get(0).getId();
        int iterations = 3;

        logger.info("=== Test de {} generaciones consecutivas ===", iterations);

        for (int i = 0; i < iterations; i++) {
            long start = System.currentTimeMillis();
            
            ResponseEntity<StreamingResponseBody> response = reportController.getPaybookPdf(testId);
            if (response.getBody() != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                response.getBody().writeTo(baos);
            }
            
            long end = System.currentTimeMillis();
            logger.info("Iteración {}: {} ms", (i + 1), (end - start));
        }
    }
}
