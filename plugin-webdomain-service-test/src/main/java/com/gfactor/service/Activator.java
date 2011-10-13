package com.gfactor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.gfactor.service.test.ServiceTester;

public class Activator {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());	

	@Autowired
	private ServiceTester serviceTester;
	
	public void start(){
		logger.info("serviceTester = " + serviceTester);
		serviceTester.Test();
	}
	
	public void stop(){
		
	}
}
