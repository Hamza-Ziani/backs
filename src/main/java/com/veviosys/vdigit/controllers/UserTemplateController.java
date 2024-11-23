package com.veviosys.vdigit.controllers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.veviosys.vdigit.classes.mapSearch;
import com.veviosys.vdigit.models.User;

import com.veviosys.vdigit.models.UserTemplates;

import com.veviosys.vdigit.services.UserTemplateService;


@RestController()
@RequestMapping("api/v1/user-template")
public class UserTemplateController {

@Autowired private UserTemplateService service;
@GetMapping()
public Page<UserTemplates> GetUserTeplates(Pageable page, @RequestParam String q ) {

 


return service.getUserTemplates(q,page);


}
@PostMapping
public UserTemplates addUserTemplate(@RequestBody Map<String, String> ut) {



return service.createUserTemplate(ut);



}

@DeleteMapping("{id}")
public void deteteUserTemplate(@PathVariable UUID id) {


 


service.deleeteUserTemplate(id);


}

@GetMapping("{id}")
public UserTemplates getUserTemplate(@PathVariable UUID id) {


	


return service.getUserTemplate(id);


}

@PutMapping("{id}")
public UserTemplates editUserFolder(@PathVariable  UUID id,@RequestBody Map<String, String> ut) {



return service.editUserTemplate(id, ut);



}

}
