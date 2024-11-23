package com.veviosys.vdigit.controllers;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.veviosys.vdigit.classes.ProfilsAbsenceClass;
import com.veviosys.vdigit.models.FolderType;
import com.veviosys.vdigit.models.ProfilsAbsence;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.services.ProfilAbsenceService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/profilabs")
public class ProfilAbsenceController {

	
	@Autowired
	ProfilAbsenceService absenceService;
	
	@GetMapping("")
	public Page<ProfilsAbsence> getAbsPage(Pageable pageable) {
		return absenceService.gectAbsList(pageable);
	}
	@GetMapping("/users")
	public List<User>getUsersMaster(){
		return absenceService.getAllUsers();
	}
	@PostMapping("/add")
	public HashMap<String,String> addProfilAbs(@RequestBody ProfilsAbsenceClass pac) throws ParseException {
		return absenceService.addProfilAbs(pac);
	}
	

	@PostMapping("/edit")
	public  HashMap<String, String> deleteProcess(@RequestBody ProfilsAbsenceClass pac) {
		return absenceService.editAbsProfil(pac);
	}
	@GetMapping("/getAbs/{id}")
    public List<User> getProfileAbsBysuplentId(@PathVariable long id) {
        return absenceService.getProfileAbsBysuplent(id);
    }
}
 