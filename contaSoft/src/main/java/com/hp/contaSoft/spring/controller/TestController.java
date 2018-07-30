package com.hp.contaSoft.spring.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.swing.plaf.synth.SynthSplitPaneUI;

import org.apache.commons.collections4.IteratorUtils;
//import org.apache.poi.util.SystemOutLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.hp.contaSoft.excel.entities.PayBookDetails;
import com.hp.contaSoft.excel.entities.Paybook;
import com.hp.contaSoft.hibernate.dao.projection.PrevisionProjection;
import com.hp.contaSoft.hibernate.dao.repositories.AFPFactorsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.AddressRepository;
import com.hp.contaSoft.hibernate.dao.repositories.IUTRepository;
import com.hp.contaSoft.hibernate.dao.repositories.PayBookDetailsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.PayBookInstanceRepository;
import com.hp.contaSoft.hibernate.dao.repositories.TaxpayerRepository;
import com.hp.contaSoft.hibernate.dao.service.FileUtilsService;
import com.hp.contaSoft.hibernate.entities.AFPFactors;
import com.hp.contaSoft.hibernate.entities.Address;
import com.hp.contaSoft.hibernate.entities.IUT;
import com.hp.contaSoft.hibernate.entities.PayBookInstance;
import com.hp.contaSoft.hibernate.entities.Subsidiary;
import com.hp.contaSoft.hibernate.entities.Taxpayer;
import com.hp.contaSoft.hibernate.entities.Template;
import com.hp.contaSoft.pipeline.PipelineManager;
import com.hp.contaSoft.pipeline.Error.PipelineMessage;
import com.hp.contaSoft.utils.FileUtils;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvException;

import ch.qos.logback.core.net.SyslogOutputStream;



@Controller
public class TestController {

	private String saveDirectory = "D:/";
	
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
	private IUTRepository iUTRepository;
	@Autowired
	private PayBookInstanceRepository payBookInstanceRepository;
	@Autowired
	private PayBookDetailsRepository payBookDetailsRepository;
	
	
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
	
	@RequestMapping("/chargesDetails")
	public String handleChargeDetails(@RequestParam Long id, HttpServletRequest request, Model model) {
		System.out.println("id="+id);
		
		List<PrevisionProjection> listPBD = (List<PrevisionProjection>) payBookDetailsRepository.getReportByPrevision(id);
		System.out.println("Cantidad de PBD:"+listPBD.size());
		System.out.println(listPBD);
		model.addAttribute("pbd", listPBD);
		
		return "chargesDetails";
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
                
                System.out.println(pipelineMessageOutput);
                
                if(pipelineMessageOutput.isValid())
                {
                	//return succesfull
                }
                else {
                	//return error
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
	
	
	
	@RequestMapping("/")
	public String doTest(HttpServletRequest request, Model model) {
		try {
			
			/*//New Address
			Address address = new Address("Tu Casa","4");
			Address add = new Address("calle1", "2");
			Address add2 = new Address("Mi Casa","3");
			
			//NewTaxpayer
			Taxpayer tp = new Taxpayer("Williams SA","15961703-3", address);
			Taxpayer tp2 = new Taxpayer("Marco SA","15961704-3", add);
			Taxpayer tp3 = new Taxpayer("Copec SA","15961705-3", add2);
			
			tp.setTemplate(new Template("Remu","RUT;CENTRO_COSTO;SUELDO BASE;DT"));
			
			taxpayerRepository.save(tp);
			taxpayerRepository.save(tp2);
			taxpayerRepository.save(tp3);*/
			
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
