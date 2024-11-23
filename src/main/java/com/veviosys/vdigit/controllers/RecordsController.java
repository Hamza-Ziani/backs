package com.veviosys.vdigit.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.veviosys.vdigit.services.RecordsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v2/records")
@RequiredArgsConstructor
public class RecordsController {

	final private RecordsService recordsService;
	
	@GetMapping
	public void setArchived(@RequestParam("ids") List<UUID> ids) {

		recordsService.setArchived(ids);
	
	}
	
}
