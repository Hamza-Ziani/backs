package com.veviosys.vdigit.sharedfolders.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.veviosys.vdigit.sharedfolders.models.SharedFolder;
import com.veviosys.vdigit.sharedfolders.services.SharedFolderService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/sharefolder")
public class SharedFolderController {
	@Autowired
	SharedFolderService sharedFolderService;

	@PostMapping("/share/{id}")
	public ResponseEntity shareDocument(@PathVariable UUID id,
			  @RequestParam("entities") List<Long> lst, @RequestParam("message")String message) {
		 try {
			 sharedFolderService.shareWithEntities(id, lst,message);
			 return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
			// TODO: handle exception
		}
		

	}

	@GetMapping("/count")
		public int getCount() { 
			return sharedFolderService.count();
		}
	
	@GetMapping("get/all")
	public Page<SharedFolder>getAll(Pageable pageable){
		return sharedFolderService.getAll(pageable);
	}
	
	
	// End point to get all shared folders by sender with optional fullName filter
	@GetMapping("get/all/by-sender")
	public Page<SharedFolder> getAllBySender(@RequestParam(value = "q", required = false) String fullName, Pageable pageable) {
	    System.out.print(fullName); // For debugging purposes
	    return sharedFolderService.getAllBySender(fullName, pageable);
	}

	

	
	
	@DeleteMapping("delete/{id}")
    public ResponseEntity deleteshare(@PathVariable Long id){
        
        return sharedFolderService.deleteshare(id);
    }
}
