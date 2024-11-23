package com.veviosys.vdigit.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.veviosys.vdigit.models.Entity;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.services.EntityService;

@RestController 
@RequestMapping("api/v1/entity")
public class EntityController {
	
	@Autowired
	EntityService entityService;
	@GetMapping("/all")
	public List<Entity> getEntities(){
return entityService.getAll();
	}	
	@PostMapping("edit")
	public void addEntity(@RequestParam("users") List<Long>users, @RequestParam("cat") int cat,@RequestParam("id") Long id,@RequestParam("name") String name) {
		entityService.editEntity(id, cat, users,name);
	} 
	@GetMapping("/find")
	Page<Entity>getEntities(Pageable pageable){
		return entityService.getEntities(pageable);
	}
	@DeleteMapping("delete/{id}")
	public ResponseEntity deleteEntity(@PathVariable Long id) {
		try {
			entityService.deleteEntity(id);
		return new ResponseEntity<>(HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);


		}
	}
	
	@GetMapping("/users/{id}")
	List<User>getUsersEntity(@PathVariable Long id){
		return entityService.getUsersEntity(id);
	}
	 
}  
