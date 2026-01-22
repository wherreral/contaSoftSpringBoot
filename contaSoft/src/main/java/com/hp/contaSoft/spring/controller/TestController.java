package com.hp.contaSoft.spring.controller;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
	public String handleCharge(@RequestParam Long id, HttpServletRequest request, Model model) {

		System.out.println("charges post");
		System.out.println("id="+id);
		
		List<PayBookInstance> listPBI = (List<PayBookInstance>) payBookInstanceRepository.findAllByTaxpayerIdOrderByVersionDesc(id);
		System.out.println("Cantidad de PBI:"+listPBI.size());
		model.addAttribute("pbi", listPBI);
		
		return "charges";
	}

    //@RequestMapping("/willy")
    @GetMapping("/willy")
    public String home(Model model) {
        model.addAttribute("message", "Welcome to Spring Boot JSP!");


        return taxpayerRepository.findAll().toString();
        //return "test"; // This will resolve to /WEB-INF/jsp/home-page.jsp
    }
	
	@RequestMapping("/")
	@Secured("ROLE_ANONYMOUS")
	public String doTest(HttpServletRequest request, Model model) {
		
		logger.debug("Method '/'");
		logger.trace("TRACE");
		logger.info("INFO");
		logger.warn("WARN");
		logger.error("error");
		
		try {
			
			//List Taxpayer
			List<Taxpayer> taxpayers = (List<Taxpayer>) taxpayerRepository.findAll();
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
	
}
