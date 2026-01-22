package com.test;

import org.springframework.beans.factory.annotation.Autowired;

import com.hp.contaSoft.hibernate.dao.service.FileUtilsService;

public class Test4 {

	@Autowired
	private FileUtilsService fileUtilsService;
	
	public static void main(String args[]) {
		System.out.println("hola");
		
		//System.out.println(fileUtilsService);
		
	}
	
}
