package com.veviosys.vdigit.Interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@Order(1)
public class InterceptorConfig implements WebMvcConfigurer{
    

    @Autowired InterceptorService interceptorService;
  
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // TODO Auto-generated method stub
        // WebMvcConfigurer.super.addInterceptors(registry);
  registry.addInterceptor(interceptorService);
    }
    

} 
