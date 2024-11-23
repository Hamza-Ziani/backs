package com.veviosys.vdigit.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.veviosys.vdigit.models.Client;
import com.veviosys.vdigit.models.Contact;
import com.veviosys.vdigit.models.DocumentType;
import com.veviosys.vdigit.models.FolderType;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.repositories.ClientRepo;
import com.veviosys.vdigit.repositories.ContactRepo;
import com.veviosys.vdigit.repositories.DocumentTypeRepo;
import com.veviosys.vdigit.repositories.FolderTypeRepo;
import com.veviosys.vdigit.repositories.UserRepository;
import com.veviosys.vdigit.services.CostumUserDetails;

@RestController
@RequestMapping("api/v1/update")
public class UpdateController {
	@Autowired
	UserRepository ur;
	@Autowired
	ContactRepo cr;
	@Autowired
	ClientRepo clr;
	@Autowired
	DocumentTypeRepo dtr;
	@Autowired
	FolderTypeRepo ftr;

	@RequestMapping(method = RequestMethod.PUT, path = "/client")

	public void editClient(@RequestBody Client cl) {
		// CostumUserDetails user = (CostumUserDetails) SecurityContextHolder
		// .getContext().getAuthentication().getPrincipal();
		// if(user.getUser().getMaster()==null)
		// {
		// cl.setMaster(user.getUser());
		// }
		// else
		// cl.setMaster(user.getUser().getMaster());
		// clr.save(cl);
	}

	@RequestMapping(method = RequestMethod.PUT, path = "/up")
	public User updateUser(@RequestBody User name) {
		CostumUserDetails ctx = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		User user = ctx.getUser();
	
		User admn = user;
		admn.setFullName(name.getFullName());
		admn.setContact(name.getContact());
		admn.setEmail(name.getEmail());
		cr.save(name.getContact());
		System.out.println("---------------"+admn.getEmail());
		return ur.saveAndFlush(admn);

	}

	@RequestMapping("/contact")
	public String updateContact(@RequestBody Contact ct) {
		cr.save(ct);

		return "Done.";
	}

	// @GetMapping("/gr")
	// public Password pw()
	// {
	// CostumUserDetails ctx = (CostumUserDetails) SecurityContextHolder
	// .getContext().getAuthentication().getPrincipal();
	// User user=ctx.getUser();
	// Password p=user.getPw();
	// return p;
	// }
	@GetMapping("/contactUser/{id}")
	public Contact findById(@PathVariable("id") long id) {

		return cr.findById(id).orElse(null);
	}

	@RequestMapping("/ct")
	public User updateContact() {
		CostumUserDetails ctx = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		User user = ctx.getUser();

		return user;
	}

	@PostMapping("/fullname")
	public String updateName(@RequestBody String name) {
		CostumUserDetails ctx = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		User user = ctx.getUser();
		user.setFullName(name);
		ur.save(user);
		return "Updated successfully";
	}

	@RequestMapping("/cltUs")
	public List<Client> cltU() {
		CostumUserDetails ctx = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		User user = ctx.getUser();
		User master;
		if (user.getMaster() == null) {
			master = user;
		} else {
			master = user.getMaster();
		}

		return clr.findclientMaster(master.getUserId());
	}

	@RequestMapping("/upClt")
	public String updatelstCl(@RequestBody Client cl) {
		// CostumUserDetails ctx = (CostumUserDetails) SecurityContextHolder
		// .getContext().getAuthentication().getPrincipal();
		// User user=ctx.getUser();
		// User master;
		// if(user.getMaster()==null)
		// {
		// master=user;
		// }
		// else
		// {master=user.getMaster();}

		// //cr.save(cl.getContact());
		// cl.setMaster(master);
		// clr.save(cl);
		return "OK";
	}

	@RequestMapping("/tstt")
	public List<FolderType> grr() {
		return ftr.findAll();
	}

	@RequestMapping("/doctype")
	public DocumentType updateDT(@RequestBody DocumentType dt) {
		CostumUserDetails user = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		// dt.setCategory(user.getUser().getCategory());
		return dtr.saveAndFlush(dt);
	}

	@RequestMapping("/foldertype")
	public FolderType upfoldType(@RequestBody FolderType ft) {
		CostumUserDetails user = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();

		return ftr.saveAndFlush(ft);

	}

	@DeleteMapping("/{id}")
	public void updateClient(@PathVariable Long id) {
		Client clt = clr.findById(id).orElse(null);
		Contact ct = clt.getContact();
		cr.delete(ct);
		clr.delete(clt);
		// .println("ok !");
	}
}
