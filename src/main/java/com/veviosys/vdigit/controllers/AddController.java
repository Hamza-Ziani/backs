package com.veviosys.vdigit.controllers;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.veviosys.vdigit.repositories.CategoryRepo;
import com.veviosys.vdigit.repositories.ContactRepo;
import com.veviosys.vdigit.repositories.DocumentTypeRepo;
import com.veviosys.vdigit.repositories.FolderTypeRepo;
import com.veviosys.vdigit.services.AlimentationService;
import com.veviosys.vdigit.services.FileStorageServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("api/v1/add")
public class AddController {

	@Autowired
	FolderTypeRepo FTR;
	@Autowired
	DocumentTypeRepo DTR;
	// @Autowired
	// ClientRepo CLR;
	@Autowired
	CategoryRepo CR;
	ContactRepo cd;
	@Autowired
	FileStorageServiceImpl files;
	@Autowired
	AlimentationService alimentationService;
	// @Autowired
	// private mailService mail;
	// @Autowired
	// private masterService ms;

	// @RequestMapping(method = RequestMethod.POST, path = "/client/{email}")
	// public ResponseEntity addClient(@RequestBody Client cl, @PathVariable(name = "email") String email)
	// 		throws AddressException, MessagingException

	// {
	// 	return ms.addClient(cl, email);
	// }

	// @GetMapping("/clients")
	// public List<Client> getClients()
	// { Long id;
	// CostumUserDetails user = (CostumUserDetails) SecurityContextHolder
	// .getContext().getAuthentication().getPrincipal();
	// if(user.getUser().getMaster()==null)
	// {
	// id=user.getUser().getUserId();
	// }
	// else
	// id=user.getUser().getMaster().getUserId();

	// return CLR.findclientMaster(id);
	// }
	@PostMapping("/down")
	public ResponseEntity<StreamingResponseBody> eee(@RequestBody List<UUID> docs)   {
		try {
			return files.zipFiles(docs);
		} catch (Exception e) {
			
			System.err.println(e.getMessage());
			return null;
		}
	
		// files.download(docs);
	}
	// @GetMapping("/clientspage")
	// public Page<Client> getClients(Pageable pageable)
	// { Long id;
	// CostumUserDetails user = (CostumUserDetails) SecurityContextHolder
	// .getContext().getAuthentication().getPrincipal();
	// if(user.getUser().getMaster()==null)
	// {
	// id=user.getUser().getUserId();
	// }
	// else
	// id=user.getUser().getMaster().getUserId();

	// List<Client> clients=CLR.findclientMaster(id);
	// int start =(int) pageable.getOffset();
	// int end =(int) (start + pageable.getPageSize()) > clients.size() ?
	// clients.size() : (start + pageable.getPageSize());
	// Page<Client> pages = new PageImpl<Client>(clients.subList(start, end),
	// pageable, clients.size());
	// return pages;
	// }

	// @RequestMapping(method=RequestMethod.POST,path="/add/user/{sexe}")
	// public String addUser(@RequestBody User ur,@PathVariable String sexe) throws
	// AddressException, MessagingException, IOException
	// {

	// //mail.send(gen);
	// ms.addUser(ur,sexe);
	// return "Done .";
	// }
}
