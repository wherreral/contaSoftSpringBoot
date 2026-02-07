package com.hp.contaSoft.hibernate.dao.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//import org.apache.poi.util.SystemOutLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.SystemPropertyUtils;

import com.hp.contaSoft.constant.HPConstant;
import com.hp.contaSoft.excel.entities.PayBookDetails;
import com.hp.contaSoft.hibernate.dao.repositories.AFPFactorsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.AFPFactorsRepositoryImpl;
import com.hp.contaSoft.hibernate.dao.repositories.IUTRepository;
import com.hp.contaSoft.hibernate.dao.repositories.PayBookDetailsRepository;

import com.hp.contaSoft.hibernate.dao.repositories.PayBookInstanceRepository;
import com.hp.contaSoft.hibernate.dao.repositories.TaxpayerRepository;
import com.hp.contaSoft.hibernate.dao.repositories._AFamiliarRepository;
import com.hp.contaSoft.hibernate.entities.AFPFactors;
import com.hp.contaSoft.hibernate.entities.AfpFactorNickname;
import com.hp.contaSoft.hibernate.dao.repositories.AfpFactorNicknameRepository;
import com.hp.contaSoft.hibernate.entities.PayBookInstance;
import com.hp.contaSoft.hibernate.entities.Taxpayer;
import com.hp.contaSoft.initial.PostConstructBean;
import com.hp.contaSoft.pipeline.Error.PipelineMessage;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

import ch.qos.logback.core.net.SyslogOutputStream;

@Service
public class FileUtilsService {

	public FileUtilsService() {
		
	}
	
	@Autowired
	AFPFactorsRepository afpFactorsRepository;
	@Autowired
	AfpFactorNicknameRepository afpFactorNicknameRepository;
    @Autowired
    private com.hp.contaSoft.hibernate.dao.service.SystemParameterService systemParameterService;

	/**
	 * Resolve an AFPFactors by the incoming prevision string.
	 * Strategy:
	 *  - normalize (trim, remove leading 'AFP ')
	 *  - try name exact case-insensitive
	 *  - try nickname case-insensitive (global entries in afp_factor_nickname)
	 *  - return null if not found
	 */
	private AFPFactors resolveAfp(String prevision) {
		if (prevision == null) return null;
		String s = prevision.trim();
		// remove leading 'AFP ' if present (case-insensitive)
		if (s.length() > 4 && s.toUpperCase().startsWith("AFP ")) {
			s = s.substring(4).trim();
		}

		try {
			AFPFactors af = afpFactorsRepository.findByNameIgnoreCase(s);
			if (af != null) return af;
		} catch (Exception e) {
			// ignore and try nickname lookup
		}

		try {
			java.util.List<AfpFactorNickname> nicks = afpFactorNicknameRepository.findByNicknameIgnoreCase(s);
			if (nicks != null && !nicks.isEmpty()) {
				AfpFactorNickname nick = nicks.get(0);
				return afpFactorsRepository.findById(nick.getAfpFactorId()).orElse(null);
			}
		} catch (Exception e) {
			// ignore
		}

		return null;
	}
	@Autowired
	TaxpayerRepository taxpayerRepository;
	@Autowired
	PayBookInstanceRepository payBookInstanceRepository;
	@Autowired
	PayBookDetailsRepository payBookDetailsRepository;
	@Autowired
	IUTRepository iUTRepository;
	@Autowired
	_AFamiliarRepository aFamiliarRepository;
	
	@Transactional
	public PipelineMessage processCSVFile(PipelineMessage pmInput) 
	{
		
		try 
		{	
			PipelineMessage pmOutput = pmInput;
			
			System.out.println("Method:processCSVFile- i!=0");
			System.out.println(pmOutput);
			TimeUnit.SECONDS.sleep(HPConstant.SLEEP_TIME);
			
			//TaxpayerRepositoryImpl taxpayerRepository = new TaxpayerRepositoryImpl();
			
			/*parse CSV file*/
			System.out.println("=== INICIO PROCESO CSV ===");
			
			// Leer el stream completo para poder diagnosticarlo y luego parsearlo
			java.io.InputStream originalStream = pmOutput.getIsInput2();
			System.out.println("InputStream obtenido: " + (originalStream != null ? "OK" : "NULL"));
			
			java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len;
			int totalBytes = 0;
			while ((len = originalStream.read(buffer)) > -1) {
				baos.write(buffer, 0, len);
				totalBytes += len;
			}
			baos.flush();
			byte[] fileBytes = baos.toByteArray();
			System.out.println("Bytes leídos del stream: " + totalBytes);
			System.out.println("Contenido del archivo:");
			System.out.println(new String(fileBytes, "UTF-8"));
			
			// Crear stream para diagnóstico
			java.io.BufferedReader br = new java.io.BufferedReader(
				new java.io.InputStreamReader(new java.io.ByteArrayInputStream(fileBytes))
			);
			
			String headerLine = br.readLine();
			System.out.println("=== DIAGNÓSTICO CSV ===");
			System.out.println("Header leído: " + headerLine);
			if(headerLine != null) {
				String[] headers = headerLine.split(";");
				System.out.println("Cantidad de columnas en header del archivo: " + headers.length);
				System.out.println("Columnas: " + java.util.Arrays.toString(headers));
			}
			
			// Leer primera línea de datos para comparar
			String firstDataLine = br.readLine();
			if(firstDataLine != null) {
				String[] dataFields = firstDataLine.split(";", -1); // -1 para incluir campos vacíos
				System.out.println("Primera línea de datos: " + firstDataLine);
				System.out.println("Cantidad de campos en datos: " + dataFields.length);
				System.out.println("Campos: " + java.util.Arrays.toString(dataFields));
			}
			br.close();
			
			// Crear nuevo stream desde los bytes para el parser
			Reader reader = new InputStreamReader(new java.io.ByteArrayInputStream(fileBytes));
			
			CsvToBean<PayBookDetails> cb = new CsvToBean<>();
	        HeaderColumnNameMappingStrategy<PayBookDetails> hc = new HeaderColumnNameMappingStrategy<PayBookDetails>();
	        hc.setType(PayBookDetails.class);
	    
	        cb = new CsvToBeanBuilder<PayBookDetails>(reader)
	          .withType(PayBookDetails.class)
	          .withMappingStrategy(hc)
	          .withIgnoreLeadingWhiteSpace(true)
	          .withSeparator(';')
	          .withThrowExceptions(false)
	          .build();
	    
	        //List of PayBookDetails - Entity with file data
	        List<PayBookDetails> detailsList = cb.parse();
	        
	        // Capturar errores de parsing
	        if(cb.getCapturedExceptions() != null && !cb.getCapturedExceptions().isEmpty()) {
	        	System.err.println("=== ERRORES DE PARSING CSV ===");
	        	for(Exception ex : cb.getCapturedExceptions()) {
	        		System.err.println("Error: " + ex.getMessage());
	        		if(ex instanceof com.opencsv.exceptions.CsvRequiredFieldEmptyException) {
	        			System.err.println("Revise el diagnóstico arriba para ver discrepancia de columnas");
	        		}
	        		ex.printStackTrace();
	        	}
	        }
	        
			System.out.println("DetailList="+detailsList );
			System.out.println("DetailList SIZE="+detailsList.size());

	        //Repository for PayBookDetails
	        //PayBookDetailsRepositoryImpl payBookDetailsRepository = new PayBookDetailsRepositoryImpl();
	        
	        //Get List of PayBookDetails from PayBookInstance
	        // Obtener el PayBookInstance desde la base de datos para asegurar que esté "managed"
        PayBookInstance pbiFromMessage = pmInput.getPayBookInstance();
        System.out.println("=== DEBUG: PayBookInstance del mensaje ===");
        System.out.println("PBI ID desde mensaje: " + (pbiFromMessage != null ? pbiFromMessage.getId() : "NULL"));
        
        if (pbiFromMessage == null || pbiFromMessage.getId() == 0) {
            System.out.println("ERROR: PayBookInstance es NULL o no tiene ID");
            pmInput.setValid(false);
            pmInput.setErrorMessageOutput("Error: PayBookInstance no encontrado");
            return pmInput;
        }
        
        // Obtener el PayBookInstance "managed" desde el repositorio con sus detalles
        PayBookInstance pbi = payBookInstanceRepository.findByIdWithDetails(pbiFromMessage.getId());
        System.out.println("PBI desde repositorio: " + (pbi != null ? pbi.getId() : "NULL"));
	        
	        if (pbi == null) {
	            System.out.println("ERROR: No se encontró PayBookInstance en la base de datos con ID: " + pbiFromMessage.getId());
	            pmInput.setValid(false);
	            pmInput.setErrorMessageOutput("Error: PayBookInstance no encontrado en base de datos");
	            return pmInput;
	        }
	        
	        // Obtener o inicializar la lista de detalles
	    	List<PayBookDetails> pbd = pbi.getPayBookDetails();
	    	if (pbd == null) {
	    	    pbd = new java.util.ArrayList<>();
	    	    pbi.setPayBookDetails(pbd);
	    	    System.out.println("Lista de PayBookDetails inicializada (estaba NULL)");
	    	}
	    	
	    	System.out.println("=== ESTADO INICIAL ===");
	    	System.out.println("Total PayBookDetails en BD: " + pbd.size());
	        
	    	System.out.println("=== INICIO LOOP DETAILS ===");
	    	System.out.println("PayBookInstance ID: " + pbi.getId());
	    	System.out.println("PayBookInstance Version: " + pbi.getVersion());
	    	System.out.println("Cantidad de detalles a procesar: " + detailsList.size());
	    	System.out.println("Lista pbd inicial size: " + pbd.size());
	    	
	    	int processedCount = 0;
	    	//Loop List of PayBookDetails
	        for(PayBookDetails detail : detailsList)
	        {
	        	processedCount++;
	        	System.out.println("--- Procesando detalle #" + processedCount + " ---");

	        	/*****
            	 * 5.Calculate Fields
            	 * @author williams
            	 * @description: calculate fields. Maybe we can use Threads.
            	 */

	        	/**
	        	 * DETECCIÓN DE ALCANCE_LIQUIDO EN CSV
	        	 * Si el CSV contiene el campo ALCANCE_LIQUIDO (> 0), calcular el BONO automáticamente
	        	 */
	        	boolean calcularBonoAutomatico = false;
	        	Double alcanceLiquidoTarget = detail.getAlcanceLiquido();

	        	if (alcanceLiquidoTarget != null && alcanceLiquidoTarget > 0.0) {
	        		System.out.println("=== MODO CÁLCULO AUTOMÁTICO DE BONO ===");
	        		System.out.println("RUT: " + detail.getRut() + " - ALCANCE_LIQUIDO del CSV: " + alcanceLiquidoTarget);
	        		calcularBonoAutomatico = true;

	        		// Guardar el bono original del CSV (si existe) para comparación
	        		double bonoOriginalCSV = detail.getBonoProduccion();
	        		System.out.println("BONO original en CSV: " + bonoOriginalCSV);
	        	}

	        	/**
	        	 * static values
	        	 */

				// Resolve AFP by name or nickname (no 'contains')
				AFPFactors afp = resolveAfp(detail.getPrevision());
	    		System.out.println("afp="+afp);
	    		TimeUnit.SECONDS.sleep(HPConstant.SLEEP_TIME);
	        	if(afp!= null)
	    			detail.setPorcentajePrevision(afp.getPercentaje());

	        	/**
	        	 * PASO 1: Calcular valores base (sin bono si se va a calcular automáticamente)
	        	 */
	        	detail.calculateSueldoMensual();
	        	detail.calculateGratificacion();
	        	detail.calculateValorHora();
	        	detail.calculateTotalHoraExtra();

	        	// Si NO vamos a calcular bono automáticamente, continuar con el flujo normal
	        	if (!calcularBonoAutomatico) {
	        		detail.calculateTotalImponible(); // Usa el bono del CSV
	        	}

	        	/**
	        	 * PASO 2: Asignación Familiar (se calcula antes de los descuentos)
	        	 */
	        	Double amount = aFamiliarRepository.getAsigFamiliarAmount(
	        		detail.getSueldoMensual() + detail.getGratificacion() +
	        		detail.getBonoProduccion() + detail.getAguinaldo() + detail.getTotalHoraExtra()
	        	);
	    		System.out.println("amount="+amount);
	        	if(amount != null)
	    			detail.calculateAsignacionFamiliar(amount);
	        	else
	        		detail.calculateAsignacionFamiliar(0d);

				/**
				 * PASO 3: Configurar AFC desde SystemParameter si no viene en CSV
				 */
				if (detail.getAfc() == 0.0) {
					try {
						java.util.List<com.hp.contaSoft.hibernate.entities.SystemParameter> params = systemParameterService.findByNameIgnoreCase("AFC");
						if (params != null && !params.isEmpty()) {
							String val = params.get(0).getValue();
							if (val != null && !val.isEmpty()) {
								val = val.replace(',', '.').trim();
								detail.setAfc(Double.parseDouble(val));
							}
						}
					} catch (Exception ex) {
						System.err.println("Error leyendo parametro AFC: " + ex.getMessage());
					}
				}

				/**
				 * PASO 4: CÁLCULO AUTOMÁTICO DE BONO (si viene ALCANCE_LIQUIDO)
				 */
				if (calcularBonoAutomatico) {
					System.out.println("=== INICIANDO CÁLCULO AUTOMÁTICO DE BONO ===");

					// Obtener método de cálculo desde SystemParameter
					String calculationMethod = "ALGEBRAIC"; // Default
					try {
						java.util.List<com.hp.contaSoft.hibernate.entities.SystemParameter> methodParams =
							systemParameterService.findByNameIgnoreCase("BONO_CALCULATION_METHOD");
						if (methodParams != null && !methodParams.isEmpty()) {
							calculationMethod = methodParams.get(0).getValue();
							System.out.println("Método de cálculo configurado: " + calculationMethod);
						}
					} catch (Exception ex) {
						System.err.println("Error leyendo BONO_CALCULATION_METHOD, usando default: " + ex.getMessage());
					}

					// Calcular el bono usando el método seleccionado
					long startTime = System.nanoTime();
					double bonoCalculado = detail.calculateBonoFromAlcanceLiquido(alcanceLiquidoTarget, calculationMethod);
					long endTime = System.nanoTime();

					double tiempoMs = (endTime - startTime) / 1_000_000.0;
					System.out.println("Tiempo de cálculo: " + tiempoMs + " ms");

					// Establecer el bono calculado
					detail.setBonoProduccion(bonoCalculado);
					System.out.println("BONO establecido: " + bonoCalculado);

					// Ahora calcular el total imponible con el nuevo bono
					detail.calculateTotalImponible();
				}

				/**
				 * PASO 5: Calcular descuentos previsionales
				 */
				detail.calculateAfc();
				detail.calculatePrevision();
				detail.calculateSalud();

				/**
				 * PASO 6: Calcular totales
				 */
	        	detail.calculateTotalHaber();
	        	detail.calculateRentaLiquidaImponible();
	        	detail.calculateSeguroAccidentes();

	        	/**
	        	 * PASO 7: Calcular Impuesto IUT
	        	 */
	        	System.out.println("detail.getRentaLiquidaImponible()="+detail.getRentaLiquidaImponible());
	        	Double iut = iUTRepository.getIUT(detail.getRentaLiquidaImponible());

				if (iut != null) {
					System.out.println("iut=" + iut);
					detail.calculateValorUIT(iut);
				}

				/**
				 * PASO 8: VERIFICACIÓN FINAL (si calculamos bono automáticamente)
				 */
				if (calcularBonoAutomatico) {
					System.out.println("=== VERIFICACIÓN FINAL ===");

					// Calcular alcance líquido REAL con el bono calculado
					double alcanceLiquidoCalculado = detail.getTotalHaber() - detail.getTotalDctoPrevisional();
					double diferencia = Math.abs(alcanceLiquidoCalculado - alcanceLiquidoTarget);

					System.out.println("ALCANCE_LIQUIDO Target (CSV): " + alcanceLiquidoTarget);
					System.out.println("ALCANCE_LIQUIDO Calculado:    " + alcanceLiquidoCalculado);
					System.out.println("Diferencia:                   " + diferencia);

					if (diferencia > 1.0) { // Tolerancia de $1
						System.err.println("ADVERTENCIA: Diferencia significativa en alcance líquido para RUT " +
										 detail.getRut());
					} else {
						System.out.println("OK: Alcance líquido dentro de tolerancia");
					}
				}

	        	//Add PayBookDetail to PayBookInstance
	        	System.out.println("Agregando detalle a pbd. Tamaño antes: " + pbd.size());
	        	pbd.add(detail);
	        	System.out.println("Tamaño después: " + pbd.size());

	        	//Set the bidirectional relationship
	        	detail.setPayBookInstance(pbi);
	        	detail.setFamilyId(pmInput.getFamilyId());

	        	System.out.println("Guardando detalle RUT: " + detail.getRut() + " con PayBookInstance ID: " + (pbi != null ? pbi.getId() : "NULL"));

	        	//Persist PayBookDetail
	        	PayBookDetails savedDetail = payBookDetailsRepository.save(detail);
	        	System.out.println("Detalle guardado con ID: " + savedDetail.getId());

	        	//Detach PayBookDetail
	        	//Persist PayBookDetail
	        	payBookDetailsRepository.save(detail);

	        	//Detach PayBookDetail
		        //payBookDetailsRepository.detach(detail);
	        }
	        
	        System.out.println("=== FIN LOOP DETAILS ===");
	        System.out.println("Total detalles procesados: " + processedCount);
	        System.out.println("PayBookDetails en memoria: " + pbd.size());
	        
	        //Persist TaxPayer
	        //Taxpayer tax = pmInput.getTaxpayerInput();
	        //PayBookInstance tax = pmInput.getPayBookInstance();
	        
	        // Guardar el PayBookInstance con los detalles agregados
	        System.out.println("=== GUARDANDO PayBookInstance con " + pbd.size() + " detalles ===");
	        pbi.setPayBookDetails(pbd);
	        PayBookInstance savedPbi = payBookInstanceRepository.save(pbi);
	        System.out.println("PayBookInstance guardado con ID: " + savedPbi.getId());
	        System.out.println("Detalles guardados: " + savedPbi.getPayBookDetails().size());
	        
	        // Actualizar el PipelineMessage con la instancia guardada
	        pmInput.setPayBookInstance(savedPbi);
	        
	        //List PayBookDetail
	        //System.out.println("Se lista objeto PaybookDetail");
	    	//payBookDetailsRepository.listAll();
	    	
	    	//TimeUnit.SECONDS.sleep(30);
	    	
	        return pmOutput;
	        
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return pmInput;
	    

	}

	
	@Transactional
	public PipelineMessage insertPayBookInstance(PipelineMessage pmInput) {
			
		try {

			System.out.println("Method insertPayBookInstance ");
			System.out.println("pmInput:"+pmInput);
			TimeUnit.SECONDS.sleep(HPConstant.SLEEP_TIME);
			
			
			//TaxpayerRepositoryImpl taxpayerRepository = new TaxpayerRepositoryImpl();
			Taxpayer tax = pmInput.getTaxpayerInput();

			//PayBookInstanceRepositoryImpl payBookInstanceRepositoryImpl = new PayBookInstanceRepositoryImpl();
            PayBookInstance pbi = new PayBookInstance(pmInput.getFileMonthOutput(), "Test Upload", pmInput.getFileNameInput(), pmInput.getFileClientRutOutput(), tax, HPConstant.PAYROLL_FILE_STATUS);
            pbi.setFamilyId(pmInput.getFamilyId());
			
			
			//Calculate VERSION of the Instance
			Lock lock = new ReentrantLock();
			lock.lock();
			long version=0;
			try {
				List<PayBookInstance> list = payBookInstanceRepository.getVersionByRutAndMonth(pmInput.getFileClientRutOutput(),pmInput.getFileMonthOutput(), pmInput.getFamilyId());
				if(!list.isEmpty())
				{
					version = list.get(0).getVersion();
					version=version + 1;
				}
				else
				{
					version =  1;
				}


				pbi.setVersion((int) version);
			}
			catch(Exception e)
			{
				pmInput.setValid(false);
				e.printStackTrace();
			}
			finally {
				lock.unlock();
			}

			 
			
			
			
            
            //save PayBookInstance
            tax.getPayBookInstance().add(pbi);
            Taxpayer savedTax = taxpayerRepository.save(tax);
            
            // Obtener el PayBookInstance con ID desde el Taxpayer guardado
            // Buscar el PayBookInstance que acabamos de agregar (el último con la version correcta)
            PayBookInstance savedPbi = null;
            for (PayBookInstance instance : savedTax.getPayBookInstance()) {
                if (instance.getVersion() == pbi.getVersion() && instance.getRut().equals(pbi.getRut())) {
                    savedPbi = instance;
                    break;
                }
            }
            
            if (savedPbi != null) {
                pbi = savedPbi; // Usar la instancia con ID
                pmInput.setPayBookInstance(pbi);
            }
            
            System.out.println("tax="+tax);
            System.out.println("PayBookInstance guardado con ID: " + pbi.getId());
            
            //list PayBookInstance
            //System.out.println("Lista PayBookInstance");
            //payBookInstanceRepository..listAll();

            
            return pmInput;
		}
		catch(Exception e) {
			pmInput.setValid(false);
			e.printStackTrace();
		}
		
		return pmInput;

	}
	
	public boolean validateMonth(String monthName) throws InterruptedException {
		
		
		System.out.println("Method validateMonth ");
		System.out.println("Month:"+monthName);
		TimeUnit.SECONDS.sleep(HPConstant.SLEEP_TIME);
		
		Locale spanish = new Locale("es", "ES");
		Date date;
		
		try {
			date = new SimpleDateFormat("MMMM", spanish).parse(monthName);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			System.out.println(cal.get(Calendar.MONTH));
			return true;
		
		} catch (ParseException e) {
			
			e.printStackTrace();
			return false;
		}
		
	}
	
	public Taxpayer validateTaxpayerByRut(String rut) throws InterruptedException {
		
		try {
		
			System.out.println("Method validateTaxpayerByRut ");
			System.out.println("Rut:"+rut);
			TimeUnit.SECONDS.sleep(HPConstant.SLEEP_TIME);
			
	        Taxpayer tax = taxpayerRepository.findFirstByRut(rut);
			
	        System.out.println("Taxpayer:"+tax);
			TimeUnit.SECONDS.sleep(HPConstant.SLEEP_TIME);
	        
			if (tax != null)
	        	return tax;
	        else
	        	return null;
	        
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}finally {
			
		}
	}
	
	
	public PipelineMessage validateFileName(PipelineMessage pmInput) {
		
		String fileName = pmInput.getFileNameInput();
		PipelineMessage pmOutput = pmInput;
		
		
		try {
			
			System.out.println("FileName:"+fileName);
			
			/**
			 *Validate CSV Extension
			 */
			String fileNames[] = fileName.split("\\.");
			String extension = fileNames[1];
			System.out.println("extension:"+extension);
			if (!extension.equalsIgnoreCase(HPConstant.FILE_EXTENSION)) {
				pmOutput.setValid(false);
				//pm.setErrorMessage(getErrorMessage(01));
				return pmOutput;
			}
			
			/**
			 *Get & SET RUT & MONTH
			 */
			String fileNam[] = fileNames[0].split("_");
	        System.out.println("RUT:"+fileNam[0]+ " Mes:"+fileNam[1]);
	        String rut = fileNam[0];
	        String month = fileNam[1];
	        pmOutput.setFileMonthOutput(month);
	        pmOutput.setFileClientRutOutput(rut);
	        
	        
			/**
			 *Validate RUT
			 */
	        Taxpayer tax = validateTaxpayerByRut(rut);
	        System.out.println("TAX="+tax);
	        
			if( tax == null) {
				pmOutput.setValid(false);
				//pm.setErrorMessage(getErrorMessage(02));
				return pmOutput;
			}
			pmOutput.setTaxpayerInput(tax);
			
	 		/**
			 *Validate MONTH
			 */
			System.out.println("Method Validate MONTH");
			System.out.println("Month:"+month);
			TimeUnit.SECONDS.sleep(HPConstant.SLEEP_TIME);
			 
			
			if(!validateMonth(month)) {
				System.out.println("CAGO");
				pmOutput.setValid(false);
				//pm.setErrorMessage(getErrorMessage(03));
				return pmOutput;
			}
            pmOutput.setValid(Boolean.TRUE);
			
		}catch(Exception e) {
			System.err.println(e.getMessage());
			pmOutput.setValid(false);
			return pmOutput;
		}
		
		finally {
			
			
		}
        
		return pmOutput;
        
        
	}
	
	public PipelineMessage validateHeaders(PipelineMessage pmInput) {
		
		PipelineMessage pmOutput = pmInput;
		
        try {
        	boolean validheader = false;
        	boolean validA = false;
        	boolean validB = false;
        	boolean validC = false;
        	InputStream is = pmOutput.getIsInput();
        	
        	System.out.println("=== VALIDATE HEADERS ===");
        	System.out.println("RUT del archivo: " + pmOutput.getFileClientRutOutput());
        	
        	Reader reader = new InputStreamReader(is);
            CSVReader csvReader = new CSVReader(reader);
			String[] nextRecord = csvReader.readNext();
			
			if(nextRecord == null || nextRecord.length == 0) {
				System.err.println("ERROR: No se pudo leer la cabecera del archivo");
				pmOutput.setValid(false);
				pmOutput.setErrorMessageOutput("No se pudo leer la cabecera del CSV");
				return pmOutput;
			}
			
			System.out.println("Cabecera:"+nextRecord[0]);
			String fileHeaders = nextRecord[0];
			
			//List<String> fileHeaders = Arrays.asList(nextRecord[0]);
			//fileHeaders.forEach(fileHeader -> System.out.println("Cabecera2;"+fileHeader));
			
			/**
			 * Obtain Map with the templates 
			 */
			String rutArchivo = pmOutput.getFileClientRutOutput();
			System.out.println("Buscando template para RUT: " + rutArchivo);
			System.out.println("Templates disponibles: " + PostConstructBean.taxpayerTemplates.keySet());
			
			Map<String, String> template = PostConstructBean.taxpayerTemplates.get(rutArchivo);
			if(template == null) {
				System.err.println("ERROR: No existe template para RUT " + rutArchivo);
				pmOutput.setValid(false);
				pmOutput.setErrorMessageOutput("No existe configuración de template para el RUT " + rutArchivo);
				return pmOutput;
			}
			
			String templateHeaders =  template.get("RENTA");
			if(templateHeaders == null) {
				System.err.println("ERROR: No existe template RENTA para RUT " + rutArchivo);
				pmOutput.setValid(false);
				pmOutput.setErrorMessageOutput("No existe template RENTA para el RUT " + rutArchivo);
				return pmOutput;
			}
			System.out.println("templateHeaders:"+templateHeaders);
			/**
			 * 2. compare fileHeaders and templateHeaders
			           two ways to compare: a.- string vs string directly; lenght (equals doesnt work cause or the columns order)			
									        b.- parse strings and compare every value
			           						c.- validate required headers
					If there is a mistmatch i must return which header we are missing
			 */
			
			//a
			
			System.out.println("Largo FILE:"+ fileHeaders.length() + " Largo template"+templateHeaders.length());
			if(fileHeaders.length() != templateHeaders.length())
				{
					pmOutput.setValid(false);
					//pm.setErrorMessage(getErrorMessage(01));
					return pmOutput;
				}
				
			//b to be implement!
			
			String[] fileArrayHeaders = fileHeaders.split(";");
			String[] templateArrayHeaders = templateHeaders.split(";");
			
			int fileLenght = fileArrayHeaders.length;
			
			reader.close();
			csvReader.close();
			return pmOutput;
			
		} catch (IOException e) {
			e.printStackTrace();
			pmOutput.setValid(false);
		}
        
		return pmOutput;}
	
	
}
