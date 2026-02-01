package com.hp.contaSoft.initial;

import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hp.contaSoft.hibernate.dao.repositories.PayBookDetailsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.PayBookInstanceRepository;
import com.hp.contaSoft.hibernate.entities.PayBookInstance;
import com.hp.contaSoft.pipeline.Error.PipelineMessage;
import com.hp.contaSoft.pipeline.PipelineManager;

@Component
@ConditionalOnProperty(name = "app.autotest.enabled", havingValue = "true", matchIfMissing = false)
public class AutoTestRunner {

    @Autowired
    private PipelineManager pm;
    
    @Autowired
    private PipelineMessage pipelineMessageInput;
    
    @Autowired
    private PayBookInstanceRepository payBookInstanceRepository;
    
    @Autowired
    private PayBookDetailsRepository payBookDetailsRepository;

    @Transactional
    public void runTest() {
        System.out.println("\n\n=================================================");
        System.out.println("========== AUTO TEST INICIADO ==========");
        System.out.println("=================================================\n");
        
        try {
            Thread.sleep(2000); // Esperar 2 segundos para asegurar que todo esté iniciado
            
            // Cargar el archivo CSV desde resources
            ClassPathResource resource = new ClassPathResource("15961705-3_ENERO.csv");
            InputStream inputStream = resource.getInputStream();
            InputStream inputStream2 = resource.getInputStream();
            
            String fileName = "15961705-3_ENERO.csv";
            
            System.out.println("Procesando archivo: " + fileName);
            
            // Configurar pipeline message
            pipelineMessageInput.setFileNameInput(fileName);
            pipelineMessageInput.setIsInput(inputStream);
            pipelineMessageInput.setIsInput2(inputStream2);
            
            // Ejecutar pipeline
            pm.setChainName("UploadPayrollFile");
            PipelineMessage pipelineMessageOutput = pm.execute(pipelineMessageInput);
            
            if (pipelineMessageOutput.isValid()) {
                System.out.println("\n✓ Pipeline ejecutado exitosamente");
            } else {
                System.out.println("\n✗ Pipeline falló");
                System.out.println("Error: " + pipelineMessageOutput.getErrorMessageOutput());
            }
            
            // Esperar un poco para que se complete el guardado
            Thread.sleep(2000);
            
            // Verificar resultados
            System.out.println("\n--- VERIFICACIÓN DE DATOS ---");
            
            List<PayBookInstance> instances = (List<PayBookInstance>) payBookInstanceRepository.findAll();
            System.out.println("Total PayBookInstances: " + instances.size());
            
            if (instances.size() > 0) {
                for (PayBookInstance instance : instances) {
                    System.out.println("  - ID: " + instance.getId() + 
                                     " | Month: " + instance.getMonth() + 
                                     " | RUT: " + instance.getRut() + 
                                     " | Version: " + instance.getVersion() +
                                     " | Details: " + instance.getPayBookDetails().size());
                }
            }
            
            List<com.hp.contaSoft.excel.entities.PayBookDetails> details = 
                (List<com.hp.contaSoft.excel.entities.PayBookDetails>) payBookDetailsRepository.findAll();
            System.out.println("Total PayBookDetails: " + details.size());
            
            if (details.size() > 0) {
                System.out.println("  Primeros 3 registros:");
                for (int i = 0; i < Math.min(3, details.size()); i++) {
                    com.hp.contaSoft.excel.entities.PayBookDetails detail = details.get(i);
                    System.out.println("    - ID: " + detail.getId() + 
                                     " | RUT: " + detail.getRut() + 
                                     " | Sueldo: " + detail.getSueldoBase() +
                                     " | PayBookInstance ID: " + 
                                     (detail.getPayBookInstance() != null ? 
                                      detail.getPayBookInstance().getId() : "NULL"));
                }
            }
            
            // Validar resultados
            System.out.println("\n--- RESULTADO DEL TEST ---");
            if (instances.size() >= 1 && details.size() >= 1) {
                System.out.println("✓✓✓ TEST EXITOSO ✓✓✓");
                System.out.println("Se encontró al menos 1 PayBookInstance y 1 PayBookDetail");
                
                // Verificar que los detalles tienen la relación correcta
                boolean relationOk = true;
                for (com.hp.contaSoft.excel.entities.PayBookDetails detail : details) {
                    if (detail.getPayBookInstance() == null) {
                        relationOk = false;
                        System.out.println("✗ ADVERTENCIA: Detalle ID " + detail.getId() + 
                                         " no tiene PayBookInstance asignado");
                    }
                }
                
                if (relationOk) {
                    System.out.println("✓ Todas las relaciones están correctas");
                }
            } else {
                System.out.println("✗✗✗ TEST FALLIDO ✗✗✗");
                System.out.println("No se encontraron los registros esperados");
                System.out.println("Se esperaba: 1+ PayBookInstance y 1+ PayBookDetail");
                System.out.println("Se encontró: " + instances.size() + " PayBookInstance y " + 
                                 details.size() + " PayBookDetail");
            }
            
        } catch (Exception e) {
            System.out.println("\n✗✗✗ ERROR EN AUTO TEST ✗✗✗");
            e.printStackTrace();
        }
        
        System.out.println("\n=================================================");
        System.out.println("========== AUTO TEST FINALIZADO ==========");
        System.out.println("=================================================\n\n");
    }
}
