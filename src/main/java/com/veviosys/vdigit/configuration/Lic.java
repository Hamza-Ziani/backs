package com.veviosys.vdigit.configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.veviosys.vdigit.license.LicenseVerify;



@Component
public class Lic {

	
	@Value("${licpath}")
	private String licPath;
	
	@Bean
	public LicenseVerify load() {
		
		return new LicenseVerify();
		
	}
	
	private Boolean lastCheck;
	 public Boolean  vef() {
		 
		 lastCheck = load().verify(licPath);
		 
		 return lastCheck;	
	}
	 
	 
	public Boolean lastCheck() {
		
		
		return lastCheck;
		
	}
	 
	 
	
	
}
