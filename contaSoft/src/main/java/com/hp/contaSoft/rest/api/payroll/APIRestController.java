package com.hp.contaSoft.rest.api.payroll;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.contaSoft.hibernate.dao.repositories.PayBookInstanceRepository;
import com.hp.contaSoft.hibernate.dao.repositories.TaxpayerPageRepository;
import com.hp.contaSoft.hibernate.dao.repositories.TaxpayerRepository;
import com.hp.contaSoft.hibernate.dao.repositories.TemplateDetailsRepository;
import com.hp.contaSoft.hibernate.entities.PayBookInstance;
import com.hp.contaSoft.hibernate.entities.Taxpayer;
import com.hp.contaSoft.hibernate.entities.TemplateDefiniton;
import com.hp.contaSoft.rest.api.form.LoginForm;
import com.hp.contaSoft.rest.api.payroll.exception.ClientNotFoundException;

@RestController
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RequestMapping("/api")
public class APIRestController {

	@Autowired
	TaxpayerRepository taxpayerRepository; 
	
	@Autowired
	PayBookInstanceRepository payBookInstanceRepository;
	
	@Autowired
	TemplateDetailsRepository templateDetailsRepository;
	
	@Autowired
	TaxpayerPageRepository taxpayerPageRepository;

	public Logger logger = LoggerFactory.getLogger(APIRestController.class);
	
	/**
	 * 
	 * @param pageable object
	 * @return all the clients info by pages
	 */
	
	@GetMapping("/clientsByPage")
	public Page<Taxpayer> clientsByPage(Pageable pageable) {
		
		Page<Taxpayer> clients = (Page<Taxpayer>) taxpayerPageRepository.findAll(pageable);
		return clients;
	}
	
	
	
	/**
	 * @return: return all the clients Info
	 */
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/clients")
	public List<Taxpayer> getClients() {
		List<Taxpayer> clients = (List<Taxpayer>) taxpayerRepository.findAll();
		return clients;
	}
	
	/**
	 * 
	 * @param clientId
	 * @return return specific client info
	 */
	@GetMapping("/clients/{clientId}")
	public Optional<Taxpayer> getClient(@PathVariable Long clientId) {

		Optional<Taxpayer> client = taxpayerRepository.findById(clientId);
		
		if ( !client.isPresent()) {
			throw new ClientNotFoundException("Client not found");
		}
			
		return client;
	}
	
	
	@GetMapping("/paybookinstance/{clientId}")
	public List<PayBookInstance>getPaybookInstanceById(@PathVariable Long clientId) {
		
		List<PayBookInstance> payBookInstanceList = (List<PayBookInstance>)payBookInstanceRepository.findAllByTaxpayerId(clientId);
		
		return payBookInstanceList;
	}
	
	
	/*@GetMapping("/paybookinstance/{clientRut}")
	public List<PayBookInstance>getPaybookinstance(@PathVariable String clientRut) {
		
		List<PayBookInstance> payBookInstanceList = (List<PayBookInstance>)payBookInstanceRepository.findAllByRut(clientRut);
		
		return payBookInstanceList;
	}*/
	
	/**
	 * Add/Update client template
	 * 
	 * @return
	 */
	@PostMapping("/addTemplates")
	public String AddTemplatesDetail(@RequestBody String clientFields) {
		
		String[] fields = clientFields.split("\\r?\\n");
		System.out.println(fields.length + " "+fields);
	
		//0. Check all required fields are within clientFields 
		//1. Check rest all the fields are in Template Definition
		//2. Add template to the client
		
		
		Iterable<TemplateDefiniton> templates = templateDetailsRepository.findAll();
		return clientFields;
	}
	
	@GetMapping("/getTemplates")
	public Iterable<TemplateDefiniton> GetTemplatesDetail() {
		logger.info("Get Templates");
		
		Iterable<TemplateDefiniton> templates = templateDetailsRepository.findAll();
		return templates;
	}
	
	
	
	@PostMapping("/login")
	public String login(@RequestBody  LoginForm data) {
		
		
		System.out.println("AQUI");
		
		logger.warn(data.toString());
		logger.warn(data.getUsername());
		logger.warn(data.getPassword());
		
		
		return "test";
	}
	
}
