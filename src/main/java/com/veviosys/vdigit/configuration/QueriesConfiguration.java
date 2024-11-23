package com.veviosys.vdigit.configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import com.veviosys.vdigit.repositories.CloneEtapeRepo;
import com.veviosys.vdigit.repositories.DocumentRepo;
import com.veviosys.vdigit.repositories.FolderTypeRepo;
import com.veviosys.vdigit.repositories.profilsAbsenceRepo;

import lombok.Data;

@Component
@Configuration
@ComponentScan(basePackages = "com.veviosys.vdigit")
@PropertySource({ "classpath:application.properties",
		"classpath:application-${documania.datasource.select}.properties" })

public class  QueriesConfiguration {



	
	public QueriesConfiguration(
			@Value("${repositories.cloneetaperepo.count}") String v,
			@Value("${repositories.cloneetaperepo.adduseretape}") String a,
			@Value("${repositories.cloneetaperepo.deletesteps}") String c,
			@Value("${repositories.cloneetaperepo.findtodostpes}") String h,
			@Value("${repositories.foldertyperepo.findfolderstype}") String fft,
			@Value("${repositories.documentrepo.existcountcheck}") String ecc,
			@Value("${repositories.profilsabsencerepo.findbysuppanddate}") String ech,
			@Value("${repositories.profilsabsencerepo.findpabyuser}") String eex
			) throws NoSuchMethodException, SecurityException {
		super();
		
		//${repositories.cloneetaperepo.count}
		Class[] aarg = new Class[1];  
        aarg[0] = Long.class;  
		Method method = CloneEtapeRepo.class.getMethod("countEtapes",aarg);
	    Query queryAnnotation = method.getAnnotation(Query.class);
	    changeAnnotationValue(queryAnnotation, "value", v);
	    
	  //${repositories.cloneetaperepo.adduseretape}
	  	aarg = new Class[2];  
	    aarg[0] = Long.class;  
	    aarg[1] = Long.class; 
	    
	  	method = CloneEtapeRepo.class.getMethod("addUserEtape",aarg);
	  	queryAnnotation = method.getAnnotation(Query.class);
	  	changeAnnotationValue(queryAnnotation, "value", a);
		
	  	
	  //${repositories.cloneetaperepo.deletesteps}
	  	aarg = new Class[1];  
	    aarg[0] = Long.class;
	    
	  	method = CloneEtapeRepo.class.getMethod("deleteSteps",aarg);
	  	queryAnnotation = method.getAnnotation(Query.class);
	  	changeAnnotationValue(queryAnnotation, "value", c);
		
	  	
	  	 //${repositories.cloneetaperepo.findtodostpes}
	  	aarg = new Class[4];  
	  	aarg[0] = Long.class;
	  	aarg[1] = Long.class;
	  	aarg[2] = Long.class;
	  	aarg[3] = Pageable.class;
	  	
	    
	  	method = CloneEtapeRepo.class.getMethod("findStepsTodo",aarg);
	  	queryAnnotation = method.getAnnotation(Query.class);
	  	changeAnnotationValue(queryAnnotation, "value", h);
	  	
	  	
	 	 //${repositories.cloneetaperepo.findtodostpes}
	  	aarg = new Class[3];  
	  	aarg[0] = Long.class;
	  	aarg[1] = Long.class;
	 	aarg[2] = Long.class;
	  	method = CloneEtapeRepo.class.getMethod("findAllStepsTodo",aarg);
	  	queryAnnotation = method.getAnnotation(Query.class);
	  	changeAnnotationValue(queryAnnotation, "value", h);
		
	  //${repositories.cloneetaperepo.adduseretape}
	  	aarg = new Class[2];  
	    aarg[0] = Long.class;  
	    aarg[1] = Long.class; 
	    
	  	method = CloneEtapeRepo.class.getMethod("addUserEtape",aarg);
	  	queryAnnotation = method.getAnnotation(Query.class);
	  	changeAnnotationValue(queryAnnotation, "value", a);
	  	
	  	
	  //${repositories.foldertyperepo.findfolderstype}
	  	aarg = new Class[2];   
	    aarg[0] = Long.class;  
	    aarg[1] = Long.class; 
	    
	  	method = FolderTypeRepo.class.getMethod("findFoldersType",aarg);
	  	queryAnnotation = method.getAnnotation(Query.class);
	  	changeAnnotationValue(queryAnnotation, "value", fft);
	  	
	    //${repositories.documentrepo.existcountcheck}
	  	aarg = new Class[3];  
	    aarg[0] = Long.class;  
	    aarg[1] = String.class;
	    aarg[2] = String.class;
	  	method = DocumentRepo.class.getMethod("existCountCheck",aarg);
	  	queryAnnotation = method.getAnnotation(Query.class);
	  	changeAnnotationValue(queryAnnotation, "value", ecc);
		

		  aarg = new Class[2];  
		  aarg[0] = Long.class;  
		  aarg[1] = Long.class; 
		  //${repositories.profilsabsencerepo.findbysuppanddate}
			method = profilsAbsenceRepo.class.getMethod("findBySuppAndDate",aarg);
			queryAnnotation = method.getAnnotation(Query.class);
			changeAnnotationValue(queryAnnotation, "value",ech);
			
			  aarg = new Class[1];  
			  aarg[0] = Long.class;   
			  //${repositories.profilsabsencerepo.findpabyuser}
				method = profilsAbsenceRepo.class.getMethod("findPaByUser",aarg);
				queryAnnotation = method.getAnnotation(Query.class);
				changeAnnotationValue(queryAnnotation, "value",eex);	
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public static Object changeAnnotationValue(Annotation annotation, String key, String newValue){
	    Object handler = Proxy.getInvocationHandler(annotation);
	    Field f;
	    try {
	        f = handler.getClass().getDeclaredField("memberValues");
	    } catch (NoSuchFieldException | SecurityException e) {
	        throw new IllegalStateException(e);
	    }
	    f.setAccessible(true);
	    Map<String, Object> memberValues;
	    try {
	        memberValues = (Map<String, Object>) f.get(handler);
	    } catch (IllegalArgumentException | IllegalAccessException e) {
	        throw new IllegalStateException(e);
	    }
	    Object oldValue = memberValues.get(key);
	    if (oldValue == null || oldValue.getClass() != newValue.getClass()) {
	        throw new IllegalArgumentException();
	    }
	    
	    Set<Entry<String, Object>> set = memberValues.entrySet();
	   for (Entry<String, Object> entry : set) {
		
		   
		  
		//    //System.out.println(entry.getKey() +" === Key");
		   //.println(entry.getValue() +" === Val");
	}
	    memberValues.put(key,newValue);
	    
	    Set<Entry<String, Object>> set2 = memberValues.entrySet();
	   for (Entry<String, Object> entry : set2) {
		
		   
		  
		//    //System.out.println(entry.getKey() +" === Key");
		//    //System.out.println(entry.getValue() +" === Val");
	}
	    return oldValue;
	}
	
	
      
	
 
}
