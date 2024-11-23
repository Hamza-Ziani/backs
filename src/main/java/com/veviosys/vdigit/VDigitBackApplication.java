package com.veviosys.vdigit;

 
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication; 
import org.springframework.context.annotation.ComponentScan;
 


 
@SpringBootApplication
@ComponentScan({ "com.veviosys.vdigit", "services","com.veviosys.vdigit.configuration" })
 
public class VDigitBackApplication implements CommandLineRunner {
 
	public static void main(String[] args) {
 
		SpringApplication.run(VDigitBackApplication.class, args);
	
}

	 

	@Override
	public void run(String... args) throws Exception {

	}
 
 
}

