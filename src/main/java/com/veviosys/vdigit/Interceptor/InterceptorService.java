package com.veviosys.vdigit.Interceptor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.veviosys.vdigit.classes.ErrorResponseDTO;
import com.veviosys.vdigit.configuration.MySessionRegistry;
import com.veviosys.vdigit.models.TempSession;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.repositories.TempSessionRepo;
import com.veviosys.vdigit.services.CostumUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

@Component
public class InterceptorService extends HandlerInterceptorAdapter {

    
    @Autowired
    private MySessionRegistry sessionRegistry;
    @Autowired 
    private TempSessionRepo sessionRepo;
    public void cleanSession(String name){
        List<Object>users= sessionRegistry.getAllPrincipals();
     

                    for (Object object : users) {
                        CostumUserDetails us=(CostumUserDetails)object;
                      
                        if(us.getUsername().equals(name))
                        {
                      
        
                        List<SessionInformation>infos=sessionRegistry.getAllSessions(us,false);
                        for (SessionInformation info :infos) {
                        info.expireNow();
                        sessionRegistry.removeSessionInformation(info.getSessionId());    
                        }}
                    }
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
           
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, 
       Object handler, ModelAndView modelAndView)    {
    
    }
  
 
}
