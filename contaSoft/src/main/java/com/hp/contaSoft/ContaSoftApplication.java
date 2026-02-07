package com.hp.contaSoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import com.hp.contaSoft.configuration.FileStorageProperties;


@SpringBootApplication()
@EnableConfigurationProperties({
    FileStorageProperties.class
})
@ComponentScan(basePackages = {
    "com.hp.contaSoft.hibernate.dao.service",
    "com.hp.contaSoft.rest.api.ui",
    "com.hp.contaSoft"
})
//@EntityScan(basePackages = {"com.hp.pipeline",
	//	"com.hp.hibernate.entities",
		//"com.hp.Controller", 	
		//"com.hp.excel.entities"
		//})
//@EnableJpaRepositories("com.hp.hibernate.dao.repositories,com.hp.hibernate.entities,com.hp.spring.controller")
//@ComponentScan(basePackages = {"com.hp.contaSoft.hibernate.dao.service"})
public class ContaSoftApplication {//extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(ContaSoftApplication.class, args);
	}
}
