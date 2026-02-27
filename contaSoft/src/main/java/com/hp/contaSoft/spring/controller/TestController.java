package com.hp.contaSoft.spring.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.apache.poi.util.SystemOutLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.hp.contaSoft.constant.Regimen;
import com.hp.contaSoft.custom.CurrentUser;
import com.hp.contaSoft.excel.entities.PayBookDetails;
import com.hp.contaSoft.hibernate.dao.projection.PrevisionProjection;
import com.hp.contaSoft.hibernate.dao.repositories.AFPFactorsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.AddressRepository;
import com.hp.contaSoft.hibernate.dao.repositories.IUTRepository;
import com.hp.contaSoft.hibernate.dao.repositories.PayBookDetailsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.PayBookInstanceRepository;
import com.hp.contaSoft.hibernate.dao.repositories.TaxpayerRepository;
import com.hp.contaSoft.hibernate.dao.service.FileUtilsService;
import com.hp.contaSoft.hibernate.dao.service.ReportUtilsService;
import com.hp.contaSoft.hibernate.entities.AFPFactors;
import com.hp.contaSoft.hibernate.entities.Address;
import com.hp.contaSoft.hibernate.entities.IUT;
import com.hp.contaSoft.hibernate.entities.PayBookInstance;
import com.hp.contaSoft.hibernate.entities.Taxpayer;
import com.hp.contaSoft.pipeline.PipelineManager;
import com.hp.contaSoft.pipeline.Error.PipelineMessage;



@Controller
public class TestController {

	private String saveDirectory = "D:/";
	public static Logger logger = LoggerFactory.getLogger(TestController.class);;
	
	@Autowired
	PipelineManager pm;
	@Autowired
	PipelineMessage pipelineMessageInput, pipelineMessageOutput;
	@Autowired 
	AFPFactorsRepository afpFactorsrepository;	
	@Autowired
	TaxpayerRepository taxpayerRepository;
	@Autowired
	AddressRepository addressRepository;
	@Autowired
	private FileUtilsService fileUtilsService;
	@Autowired
	private ReportUtilsService reportUtilsService;
	@Autowired
	private IUTRepository iUTRepository;
	@Autowired
	private PayBookInstanceRepository payBookInstanceRepository;
	@Autowired
	private PayBookDetailsRepository payBookDetailsRepository;
	@Autowired
	private com.hp.contaSoft.hibernate.dao.service.SystemParameterService systemParameterService;
	
	/*
	@RequestMapping("/charges")
	public String handleCharge(@RequestParam Long id, HttpServletRequest request, Model model) {
		System.out.println("id="+id);
		
		//List PayBookInstance
		//List<PayBookInstance> listPBI = (List<PayBookInstance>) payBookInstanceRepository.findAllByTaxpayerId(id);
		List<PayBookInstance> listPBI = (List<PayBookInstance>) payBookInstanceRepository.findAllByTaxpayerIdOrderByVersionDesc(id);
		System.out.println("Cantidad de PBI:"+listPBI.size());
		
		
		model.addAttribute("pbi", listPBI);
		
		return "charges";
	}
	*/
	
	@RequestMapping("/chargesDetails")
	public String handleChargeDetails(@RequestParam Long id, HttpServletRequest request, Model model) {
		System.out.println("id="+id);
		
		List<PrevisionProjection> listPBD = (List<PrevisionProjection>) payBookDetailsRepository.getReportByPrevision(id);
		System.out.println("Cantidad de PBD:"+listPBD.size());
		System.out.println(listPBD);
		model.addAttribute("pbd", listPBD);
		
		return "chargesDetails";
	}

	@RequestMapping("/redirectImportBook")
    public void redirectImportBook(
    		HttpServletRequest request,
    		HttpServletResponse response,
            @RequestParam MultipartFile[] fileUpload,
            Model model
    		) {
         
		try {
		
        System.out.println("description: " + request.getParameter("description"));
        
        if (fileUpload != null && fileUpload.length > 0) {
            for (MultipartFile aFile : fileUpload){
                 
                System.out.println("Procesing File: " + aFile.getOriginalFilename());
                
                // Leer el archivo una vez y crear dos streams independientes
                byte[] fileBytes = aFile.getBytes();
                InputStream is = new ByteArrayInputStream(fileBytes);
                InputStream is2 = new ByteArrayInputStream(fileBytes);
                
                /***
                 * Start Validation Pipeline
                 * 0.Validate file name
                 * 1.Validate Headers
                 * 2.Create PayBookInstance
                 * 3.Process csv file
                 * 4.Create PayBookDetail
                 * 5.Calculate Fields NEED TO ADD
                 */
                
                System.out.println("START PIPELINE");
                
                // set pipeline message
                pipelineMessageInput.setFileNameInput(aFile.getOriginalFilename());
                pipelineMessageInput.setIsInput(is);
                pipelineMessageInput.setIsInput2(is2);
                
                //set chain name to execute
                pm.setChainName("UploadPayrollFile");
                
                pipelineMessageOutput =  pm.execute( pipelineMessageInput);
                
                if(pipelineMessageOutput.isValid())
                {
                	//return succesfull
                	System.out.println("PIPELINE final VALIDO");
                }
                else {
                	//return error
                	System.out.println("PIPELINE final INVALIDO");
                }

                
                
                	
	            /**
	             * Return to the view
	             */
                //List of Taxpayers for the view
                //TaxpayerRepositoryImpl taxpayerRepository = new TaxpayerRepositoryImpl();
                //List<Taxpayer> taxpayers = taxpayerRepository.getListAll();
                List<Taxpayer> taxpayers = (List<Taxpayer>) taxpayerRepository.findAll();
    			model.addAttribute("taxpayers",taxpayers);
            }
            
                    
                        
            
            
            
        }
        	// redirect to
        	response.sendRedirect("http://localhost:3000/dashboard/");
	        
	        
	        
		}catch(Exception e)
		{
			//System.out.println(e.getMessage().toString());
			e.printStackTrace();
			System.out.println(e.getLocalizedMessage());
			
			System.out.println("Entre ACA");
			
			
			// redirect to
        	try {
				response.sendRedirect("http://localhost:3000/dashboard/");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
    }
	
	
	
	@RequestMapping("/importBook2")
    public String handleFileUpload(
    		HttpServletRequest request,
            @RequestParam MultipartFile[] fileUpload,
            Model model
    		) {
         
		try {
		
        System.out.println("description: " + request.getParameter("description"));
        
        if (fileUpload != null && fileUpload.length > 0) {
            for (MultipartFile aFile : fileUpload){
                 
                System.out.println("Procesing File: " + aFile.getOriginalFilename());
                
                InputStream is = aFile.getInputStream();
                InputStream is2 = aFile.getInputStream();
                
                /***
                 * Start Validation Pipeline
                 * 0.Validate file name
                 * 1.Validate Headers
                 * 2.Create PayBookInstance
                 * 3.Process csv file
                 * 4.Create PayBookDetail
                 * 5.Calculate Fields NEED TO ADD
                 */
                
                System.out.println("START PIPELINE");
                
                // set pipeline message
                pipelineMessageInput.setFileNameInput(aFile.getOriginalFilename());
                pipelineMessageInput.setIsInput(is);
                pipelineMessageInput.setIsInput2(is2);
                
                //set chain name to execute
                pm.setChainName("UploadPayrollFile");
                
                pipelineMessageOutput =  pm.execute( pipelineMessageInput);
                
                if(pipelineMessageOutput.isValid())
                {
                	//return succesfull
                	System.out.println("PIPELINE final VALIDO");
                }
                else {
                	//return error
                	System.out.println("PIPELINE final INVALIDO");
                }

                
                
                	
	            /**
	             * Return to the view
	             */
                //List of Taxpayers for the view
                //TaxpayerRepositoryImpl taxpayerRepository = new TaxpayerRepositoryImpl();
                //List<Taxpayer> taxpayers = taxpayerRepository.getListAll();
                List<Taxpayer> taxpayers = (List<Taxpayer>) taxpayerRepository.findAll();
    			model.addAttribute("taxpayers",taxpayers);
            }
            
                    
                        
            
            
            
        }
        
        
 
	        // returns to the view "Result"
	        return "result";
		}catch(Exception e)
		{
			//System.out.println(e.getMessage().toString());
			e.printStackTrace();
			System.out.println(e.getLocalizedMessage());
			
			System.out.println("Entre ACA");
			
			
    		return "result";
		}
    }
	
	
	/**
	 * Nuevo endpoint para importar archivo CSV via AJAX
	 * Retorna JSON con resultado de la operación
	 * Si es exitoso, retorna URL para redirigir a la página de PayBookInstances del cliente
	 * Si hay error, retorna mensaje de error para mostrar en popup
	 */
	@org.springframework.transaction.annotation.Transactional
	@RequestMapping(value = "/importBookAjax", produces = "application/json")
	@org.springframework.web.bind.annotation.ResponseBody
    public java.util.Map<String, Object> handleFileUploadAjax(
    		HttpServletRequest request,
            @RequestParam MultipartFile[] fileUpload,
            Model model,
            Authentication authentication
    		) {
		
		java.util.Map<String, Object> response = new java.util.HashMap<>();
         
		try {
		
        System.out.println("description: " + request.getParameter("description"));
        
        // Obtener familyId del usuario autenticado
        String familyId = null;
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CurrentUser) {
        	CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        	familyId = currentUser.getFamilId();
        	System.out.println("FamilyId obtenido: " + familyId);
        } else {
        	System.out.println("No se pudo obtener familyId del usuario autenticado");
        	response.put("success", false);
        	response.put("errorMessage", "Usuario no autenticado");
        	return response;
        }
        
        if (fileUpload != null && fileUpload.length > 0) {
            for (MultipartFile aFile : fileUpload){
                 
                System.out.println("Procesing File: " + aFile.getOriginalFilename());
                
                // Leer el archivo una vez y crear dos streams independientes
                byte[] fileBytes = aFile.getBytes();
                InputStream is = new ByteArrayInputStream(fileBytes);
                InputStream is2 = new ByteArrayInputStream(fileBytes);
                
                /***
                 * Start Validation Pipeline
                 * 0.Validate file name
                 * 1.Validate Headers
                 * 2.Create PayBookInstance
                 * 3.Process csv file
                 * 4.Create PayBookDetail
                 * 5.Calculate Fields NEED TO ADD
                 */
                
                System.out.println("START PIPELINE");
                
                // Crear nueva instancia de PipelineMessage para evitar problemas con singleton
                PipelineMessage newPipelineMessage = new PipelineMessage();
                newPipelineMessage.setFileNameInput(aFile.getOriginalFilename());
                newPipelineMessage.setIsInput(is);
                newPipelineMessage.setIsInput2(is2);
                newPipelineMessage.setFamilyId(familyId);
                
                //set chain name to execute
                pm.setChainName("UploadPayrollFile");
                
                PipelineMessage pipelineResult = pm.execute(newPipelineMessage);
                
                if(pipelineResult.isValid())
                {
                	//return succesfull
                	System.out.println("PIPELINE final VALIDO");
                	
                	// Obtener el PayBookInstance procesado y su cliente asociado
                	PayBookInstance pbiProcessed = pipelineResult.getPayBookInstance();
                	if(pbiProcessed != null && pbiProcessed.getTaxpayer() != null) {
                		Long clientId = pbiProcessed.getTaxpayer().getId();
                		System.out.println("Redirigiendo a charges con clientId: " + clientId);
                		// Retornar éxito con URL de redirección
                		response.put("success", true);
                		response.put("redirectUrl", "/charges");
                		response.put("message", "Archivo importado correctamente");
                		return response;
                	}
                }
                else {
                	//return error
                	System.out.println("PIPELINE final INVALIDO");
                	response.put("success", false);
                	response.put("errorMessage", pipelineResult.getErrorMessageOutput());
                	return response;
                }
            }
        }
        
        // Si no hay archivos
        response.put("success", false);
        response.put("errorMessage", "No se seleccionó ningún archivo");
        return response;
        
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println(e.getLocalizedMessage());
			System.out.println("Entre ACA");
			
			// Retornar error
			response.put("success", false);
			response.put("errorMessage", "Error al procesar el archivo: " + e.getMessage());
			return response;
		}
    }
	
	
	@RequestMapping("/importBook")
	public String doImportBook(HttpServletRequest request, Model model) {
		return "import";
	}
	
	
	
	@RequestMapping(value="/process", method = { RequestMethod.POST, RequestMethod.GET })
	public String handleProcess(@RequestParam Long id, HttpServletRequest request, Model model) {

		System.out.println("process post");
		System.out.println("id="+id);
		
		/**
		 * 0. I NEED TO CHECK WHETHER THE PAYBOOKINSTANCE IS ALREADY PROCESSED 		
		 */
		
		/**
		 * 1. I NEED TO CHANGE THE STATUS OF THE PAYBOOKINSTANCE  		
		 */
		
		reportUtilsService.generateReports(id);
		
		List<PayBookDetails> listPBD = payBookDetailsRepository.findByPayBookInstanceId(id);
		System.out.println("Cantidad de PBI:"+listPBD.size());
		model.addAttribute("pbd", listPBD);
		
		return "resultProcess";
	}
	

	@RequestMapping(value="/charges", method = { RequestMethod.POST, RequestMethod.GET })
	public String handleCharge(@RequestParam(required = false) Long id, HttpServletRequest request, Model model, Authentication authentication) {

		// Require authentication
		if (authentication == null || !authentication.isAuthenticated()
				|| !(authentication.getPrincipal() instanceof CurrentUser)) {
			return "redirect:/login";
		}

		CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
		String familyId = currentUser.getFamilId();

		List<PayBookInstance> listPBI;
		if (id != null) {
			listPBI = (List<PayBookInstance>) payBookInstanceRepository.findAllByFamilyIdAndTaxpayerId(familyId, id);
		} else {
			listPBI = (List<PayBookInstance>) payBookInstanceRepository.findAllByFamilyId(familyId);
		}
		System.out.println("Cantidad de PBI:"+listPBI.size());
		model.addAttribute("pbi", listPBI);

		// Taxpayers for client filter dropdown
		List<Taxpayer> taxpayers = taxpayerRepository.findByFamilyId(familyId);
		model.addAttribute("taxpayers", taxpayers);
		model.addAttribute("selectedClientId", id);

		return "charges";
	}
	
	/**
	 * Endpoint AJAX para obtener los PayBookDetails de un PayBookInstance
	 * Retorna JSON con la lista de detalles
	 */
	@RequestMapping(value="/api/paybookdetails", method = RequestMethod.GET, produces = "application/json")
	@org.springframework.web.bind.annotation.ResponseBody
	public List<PayBookDetails> getPayBookDetails(@RequestParam Long pbiId, Authentication authentication) {
		System.out.println("=== Obteniendo PayBookDetails para PBI id=" + pbiId + " ===");
		
		// Obtener familyId del usuario autenticado
		String familyId = null;
		if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CurrentUser) {
			CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
			familyId = currentUser.getFamilId();
			System.out.println("FamilyId obtenido: " + familyId);
		}
		
		// Verificar si el PayBookInstance existe y pertenece al usuario
		PayBookInstance pbi = null;
		if (familyId != null) {
			pbi = payBookInstanceRepository.findByIdAndFamilyId(pbiId, familyId);
		} else {
			java.util.Optional<PayBookInstance> pbiOpt = payBookInstanceRepository.findById(pbiId);
			if (pbiOpt.isPresent()) {
				pbi = pbiOpt.get();
			}
		}
		
		if (pbi != null) {
			System.out.println("PayBookInstance encontrado: id=" + pbi.getId() + ", rut=" + pbi.getRut() + ", month=" + pbi.getMonth());
			System.out.println("PayBookDetails en memoria: " + (pbi.getPayBookDetails() != null ? pbi.getPayBookDetails().size() : "NULL"));
		} else {
			System.out.println("PayBookInstance NO encontrado con id=" + pbiId + " para familyId=" + familyId);
			return new java.util.ArrayList<>(); // Retornar lista vacía si no se encuentra
		}
		
		// Obtener los detalles del PayBookInstance
		List<PayBookDetails> listPBD = payBookDetailsRepository.findAllByPayBookInstanceIdAndFamilyId(pbi.getId(), familyId);
		System.out.println("PayBookDetails encontrados: " + listPBD.size());

		// Recalcular valores necesarios para la vista por si provienen de imports antiguos
		for (PayBookDetails d : listPBD) {
			try {
				// Si afc==0 usamos el parámetro global AFC (solo para INDEFINIDO)
				if (d.getRegimenEffective() == Regimen.PLAZO_FIJO) {
					d.setAfc(0.0);
				} else if (d.getAfc() == 0.0) {
					try {
						java.util.List<com.hp.contaSoft.hibernate.entities.SystemParameter> params = systemParameterService.findByNameIgnoreCase("AFC");
						if (params != null && !params.isEmpty()) {
							String v = params.get(0).getValue();
							if (v != null && !v.isEmpty()) {
								v = v.replace(',', '.').trim();
								d.setAfc(Double.parseDouble(v));
							}
						}
					} catch (Exception ex) {
						System.err.println("Error leyendo parametro AFC en controller: " + ex.getMessage());
					}
				}
				// Asegurar que totalImponible está calculado
				if (d.getTotalImponible() == 0) {
					d.calculateTotalHoraExtra();
					d.calculateSueldoMensual();
					d.calculateGratificacion();
					d.calculateTotalImponible();
				}
				// Recalcular AFC y valores de previsión y salud
				d.calculateAfc();
				d.calculatePrevision();
				d.calculateSalud();
			} catch (Exception ex) {
				System.err.println("Error recalculando valores para detalle id=" + d.getId() + " : " + ex.getMessage());
			}
		}
		
		// Listar todos los PayBookDetails para debug
		Iterable<PayBookDetails> allDetails = payBookDetailsRepository.findAll();
		int count = 0;
		for (PayBookDetails d : allDetails) {
			count++;
			if (count <= 5) {
				System.out.println("Detail id=" + d.getId() + ", rut=" + d.getRut() + 
					", pbiId=" + (d.getPayBookInstance() != null ? d.getPayBookInstance().getId() : "NULL"));
			}
		}
		System.out.println("Total PayBookDetails en BD: " + count);
		
		return listPBD;
	}

    //@RequestMapping("/willy")
    @GetMapping("/willy")
    public String home(Model model) {
        model.addAttribute("message", "Welcome to Spring Boot JSP!");


        return taxpayerRepository.findAll().toString();
        //return "test"; // This will resolve to /WEB-INF/jsp/home-page.jsp
    }
	
	@RequestMapping({"/", "/home"})
	public String doTest(HttpServletRequest request, Model model, Authentication authentication) {
		
		logger.debug("Method '/'");
		logger.info("INFO");

		// Si no hay autenticación, redirigir al login
		if (authentication == null || !authentication.isAuthenticated() 
				|| !(authentication.getPrincipal() instanceof CurrentUser)) {
			return "redirect:/login";
		}
		
		try {
			
			//List Taxpayer - Filter by familyId
			List<Taxpayer> taxpayers = new ArrayList<>();
			CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
			String familyId = currentUser.getFamilId();
			if (familyId != null && !familyId.isEmpty()) {
				taxpayers = taxpayerRepository.findByFamilyId(familyId);
				logger.info("Loaded {} taxpayers for familyId: {}", taxpayers.size(), familyId);
			} else {
				logger.warn("doTest - FamilyId is null or empty for authenticated user");
			}
			System.out.println("Cantidad de clientes:"+taxpayers.size());
			
			//List Address
			List<Address> addr = (List<Address>) addressRepository.findAll();
			System.out.println("Cantidad de direcciones:"+addr.size());
			
			//AFP List
			List<AFPFactors> list = new ArrayList<>();
			Iterable<AFPFactors> afpFactorsIterator = afpFactorsrepository.findAll();
			afpFactorsIterator.forEach(list::add);
			
			//UIT List
			List<IUT> IUTlist = new ArrayList<>();
			Iterable<IUT> IUTIterator = iUTRepository.findAll();
			IUTIterator.forEach(IUTlist::add);
			
			
			//Model
			model.addAttribute("taxpayers", taxpayers);
			model.addAttribute("afp", list);
			model.addAttribute("IUT", IUTlist);
			
		}catch(Exception e) {
			throw e;
		}
		
		return "home-page";

		
	}


    // java
    @GetMapping(value = "/api/paybookinstances", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public org.springframework.http.ResponseEntity<java.util.List<PayBookInstance>> getAllPayBookInstances() {
        java.util.List<PayBookInstance> list = (java.util.List<PayBookInstance>) payBookInstanceRepository.findAll();
        return org.springframework.http.ResponseEntity.ok(list);
    }

    // java
    @GetMapping(value = "/api/v1/clients", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public org.springframework.http.ResponseEntity<java.util.List<Taxpayer>> getAllClients() {
        java.util.List<Taxpayer> list = (java.util.List<Taxpayer>) taxpayerRepository.findAll();
        return org.springframework.http.ResponseEntity.ok(list);
    }
    
    @GetMapping(value = "/api/paybookdetails/all", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public org.springframework.http.ResponseEntity<?> getAllPayBookDetails() {
        java.util.List<com.hp.contaSoft.excel.entities.PayBookDetails> list = (java.util.List<com.hp.contaSoft.excel.entities.PayBookDetails>) payBookDetailsRepository.findAll();
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("total", list.size());
        response.put("data", list);
        return org.springframework.http.ResponseEntity.ok(response);
    }

}

