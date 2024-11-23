package com.veviosys.vdigit.controllers;

import java.util.ArrayList;
import java.util.List;

import com.veviosys.vdigit.models.DocumentsHeadAndFooter;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.services.CostumUserDetails;
import com.veviosys.vdigit.services.DocumentHeadFootService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/document-head-foot")
@CrossOrigin(origins = "*")
public class DocumentHeadFootController {

	
	@Autowired DocumentHeadFootService documentHeadFootService;
    
	public User accountUser() {
		User  user=  ((CostumUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
	    if(java.util.Objects.isNull(user.getMaster()))
	    	return user;
	    else
	    	return user.getMaster();
	
	
	}
	

	
	@GetMapping
	public ResponseEntity<List<DocumentsHeadAndFooter>> getHeadAndFoot()
	{
		List<DocumentsHeadAndFooter> headAndFooter = documentHeadFootService.getDocumenHeadFoot(accountUser().getUserId());
		
		return ResponseEntity.ok(java.util.Objects.isNull(headAndFooter)? new ArrayList<DocumentsHeadAndFooter>() : headAndFooter);
	}
	
	@GetMapping(path = "header")
	public ResponseEntity<DocumentsHeadAndFooter> getHeader()
	{
		
		return ResponseEntity.ok(documentHeadFootService.getDocumenHeadFoot(accountUser().getUserId()).stream().filter(d -> d.getType() == 'h').findFirst().orElse(null));
	}
	
	@GetMapping("footer")
	public ResponseEntity<DocumentsHeadAndFooter> getFooter()
	{
		
		return ResponseEntity.ok(documentHeadFootService.getDocumenHeadFoot(accountUser().getUserId()).stream().filter(d -> d.getType() == 'f').findFirst().orElse(null));
	}
	
	@PutMapping("header")
	public ResponseEntity<DocumentsHeadAndFooter> updateHeader(@RequestBody String newHeader) {
		// TODO Auto-generated method stub
	 
		Long masterId = accountUser().getUserId();
		//System.out.println("=================>"+masterId);
          return ResponseEntity.ok(documentHeadFootService.updateHeader(masterId,newHeader));
	}
	
	
	@PutMapping("footer")
	public ResponseEntity<DocumentsHeadAndFooter> updateFooter(@RequestBody String newHeader) {
		// TODO Auto-generated method stub
		Long masterId = accountUser().getUserId();
          return ResponseEntity.ok(documentHeadFootService.updateFooter(masterId,newHeader));
	}
	
	
	
	
	
	
	
}
