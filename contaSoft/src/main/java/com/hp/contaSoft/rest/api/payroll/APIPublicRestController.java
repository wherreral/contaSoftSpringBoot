package com.hp.contaSoft.rest.api.payroll;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.contaSoft.hibernate.dao.repositories.GroupCredentialsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.PayBookInstanceRepository;
import com.hp.contaSoft.hibernate.dao.repositories.TaxpayerPageRepository;
import com.hp.contaSoft.hibernate.dao.repositories.TaxpayerRepository;
import com.hp.contaSoft.hibernate.dao.repositories.TemplateDetailsRepository;
import com.hp.contaSoft.hibernate.dao.repositories.UserRepository;
import com.hp.contaSoft.hibernate.entities.AppUser;
import com.hp.contaSoft.hibernate.entities.GroupCredentials;
import com.hp.contaSoft.hibernate.entities.Role;

@RestController
@RequestMapping("/public/api/")
public class APIPublicRestController {

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
	GroupCredentialsRepository groupCredentialsRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public Logger logger = LoggerFactory.getLogger(APIPublicRestController.class);
	
	@CrossOrigin("http://localhost:3000")
	@PostMapping("/sign-up")
	public Boolean SignUp(@RequestBody AppUser user) {
		
		logger.warn("sign-up");
		
		//0. Validate user doesn't exist
		AppUser au = userRepository.findByUsername(user.getUsername());
		if(au != null) {
			logger.info("User already exist");
			return false;
		}
		//1. Create family credentials
		GroupCredentials gc = new GroupCredentials("name","type",UUID.randomUUID().toString());
		groupCredentialsRepository.save(gc);
		
		//2.Set Role
		user.setRole(new Role(user,1));
		
		//3. Persist User
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setGroupCredentials(gc);
		userRepository.save(user);
		
		//2.1 We actually need to create and return a Family credentials for the new user
		
		//3. Save session information
		// Maybe we need to save the sing-up stamp, user-agent, device
		// Maybe we can do this as a filter?
		
		//4. What is the role of the user?
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
