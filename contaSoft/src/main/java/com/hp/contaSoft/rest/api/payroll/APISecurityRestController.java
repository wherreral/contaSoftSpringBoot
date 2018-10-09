package com.hp.contaSoft.rest.api.payroll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.contaSoft.hibernate.dao.repositories.PayBookInstanceRepository;
import com.hp.contaSoft.hibernate.dao.repositories.TaxpayerPageRepository;
import com.hp.contaSoft.hibernate.dao.repositories.TaxpayerRepository;
import com.hp.contaSoft.hibernate.dao.repositories.TemplateDetailsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.UserRepository;
import com.hp.contaSoft.hibernate.entities.AppUser;

@RestController
@RequestMapping("/public/api/")
public class APISecurityRestController {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	TaxpayerRepository taxpayerRepository; 
	
	@Autowired
	PayBookInstanceRepository payBookInstanceRepository;
	
	@Autowired
	TemplateDetailsRepository templateDetailsRepository;
	
	@Autowired
	TaxpayerPageRepository taxpayerPageRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public Logger logger = LoggerFactory.getLogger(APISecurityRestController.class);
	
	@CrossOrigin("http://localhost:3000")
	@PostMapping("/sign-up")
	public Boolean SignUp(@RequestBody AppUser user) {
		
		logger.warn("sign-up");
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userRepository.save(user);

		
		return true;
	}
	
	@CrossOrigin("http://localhost:3000")
	@PostMapping("/sign-in")
	public Boolean SignIn(@RequestBody AppUser user) {
		
		logger.warn("SignIn");
		//logger.warn(user);
		//logger.warn(password);
		
		return true;
	}
	
	
}
