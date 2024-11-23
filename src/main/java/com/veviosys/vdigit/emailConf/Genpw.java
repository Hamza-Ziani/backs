package com.veviosys.vdigit.emailConf;

import java.util.Random;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Genpw {
	 

	    private String emailTo;
	    private String subject;
	    
	
	    public String gennewpw()
		{
	    	String gen="";
	    	 String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	         String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
	         String specialCharacters = "_-";
	         String numbers = "1234567890";
	         String combinedChars = capitalCaseLetters + lowerCaseLetters + specialCharacters + numbers;
	         Random random = new Random();
	         char[] password = new char[10];
	         password[0] = lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length()));
	         password[1] = capitalCaseLetters.charAt(random.nextInt(capitalCaseLetters.length()));
	         password[2] = specialCharacters.charAt(random.nextInt(specialCharacters.length()));
	         password[3] = numbers.charAt(random.nextInt(numbers.length()));
	        
	         for(int i = 4; i< 10 ; i++) {
	             password[i] =combinedChars.charAt(random.nextInt(combinedChars.length()));
	          }
	         for (int i=0;i<10;i++)
	         {
	        	 gen+=password[i];
	         }
	         
	         return gen;
	     
		}
}
