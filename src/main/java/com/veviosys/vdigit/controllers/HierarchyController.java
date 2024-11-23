package com.veviosys.vdigit.controllers;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.services.HierarchyService;

@RestController
@CrossOrigin(origins = "*")

@RequestMapping("api/v1/hierarchy")
public class HierarchyController {

	
	@Autowired HierarchyService hierarchyService; 
	
	
	@GetMapping("data")
	public ResponseEntity<List<HashMap<String, Object>>> getHierarchyData()
	{
		
		try {
			return new ResponseEntity<List<HashMap<String,Object>>>(hierarchyService.getHierarchyData(),HttpStatus.OK);
		 			
		} catch (Exception e) {
			
			e.fillInStackTrace();
			return new ResponseEntity<List<HashMap<String,Object>>>(hierarchyService.getHierarchyData(),HttpStatus.CONFLICT);
			
		}
		
		 
	}
	@GetMapping("childs")
	public ResponseEntity<List<User>> getChilds()
	{
	 
	try {
	return new ResponseEntity<List<User>>(hierarchyService.getUserChilds(),HttpStatus.OK);
	 
	} catch (Exception e) {
	 
	e.fillInStackTrace();
	return new ResponseEntity<List<User>>(hierarchyService.getUserChilds(),HttpStatus.CONFLICT);
	 
	}
	 
	 
	}
	
}
