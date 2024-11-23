package com.veviosys.vdigit.configuration;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

public class CustomLogoutSuccessHandler extends 
SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

    

    @Override
    public void onLogoutSuccess(
      HttpServletRequest request, 
      HttpServletResponse response, 
      Authentication authentication) 
      throws IOException, ServletException {
 
        String refererUrl = request.getHeader("Referer");
       //System.out.println("success");
        
        super.onLogoutSuccess(request, response, authentication);
    }
    
}
