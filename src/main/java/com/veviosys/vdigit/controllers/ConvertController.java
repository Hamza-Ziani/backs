package com.veviosys.vdigit.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.veviosys.vdigit.services.ConvertService;

 
@RestController
@RequestMapping("api/v1/convert")
public class ConvertController {
@Autowired
ConvertService convertService;
	
	@GetMapping("/word/{id}")
	public HashMap<String,String> convertWordPdf(@PathVariable UUID id) throws IOException{
		return convertService.convertWordPdf(id);
	}
	
}
