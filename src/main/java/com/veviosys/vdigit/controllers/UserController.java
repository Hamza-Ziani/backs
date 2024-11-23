package com.veviosys.vdigit.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.Principal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.veviosys.vdigit.classes.mailClass;
import com.veviosys.vdigit.models.ClientDoc;
import com.veviosys.vdigit.models.CloneEtape;
import com.veviosys.vdigit.models.Entity;
import com.veviosys.vdigit.models.Folder;
import com.veviosys.vdigit.models.InterneMessage;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.models.receiver;
import com.veviosys.vdigit.repositories.UserRepository;
import com.veviosys.vdigit.services.AlimentationService;
import com.veviosys.vdigit.services.CostumUserDetails;
import com.veviosys.vdigit.services.FileStorageServiceImpl;
import com.veviosys.vdigit.services.SearchService;
import com.veviosys.vdigit.services.SeconnderService;
import com.veviosys.vdigit.services.mailService;
import com.veviosys.vdigit.services.masterService;
import com.veviosys.vdigit.services.userService;
import com.veviosys.vdigit.sharedfolders.models.SharedFolder;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController

@CrossOrigin(origins = "*")
@RequestMapping("api/v1")
@Slf4j
public class UserController {
 
    @Autowired
    private masterService masterService;
	@Autowired
	private mailService ms;
	@Autowired
	private userService userService;
	@Autowired
	private SearchService ss;
	@Autowired
	private masterService mst;
	@Autowired
	private SeconnderService seconnderService;
	@Autowired
	AlimentationService as;

	

	@GetMapping("api/csrf")
	public CsrfToken reset(CsrfToken token) throws AddressException, MessagingException, IOException {

	return token;
	}
	
	@ResponseBody
	@GetMapping("/logo/{name}")
	public ResponseEntity<Resource> getLogo(@PathVariable String name) throws MalformedURLException {
		// String pathname = id+filename;
		// TODO Auto-generated method stub
		Resource r = fileService.loadLogoBrut(name);

		HttpHeaders respHeaders = new HttpHeaders();

		// respHeaders.setContentLength(file.toFile().length());
		respHeaders.setContentDispositionFormData("attachment", name);

		return new ResponseEntity<org.springframework.core.io.Resource>(r, respHeaders, HttpStatus.OK);

	}

	@Autowired
	private FileStorageServiceImpl fileService;
	
	
	@GetMapping("/api/allreceivers")
    public List<receiver> getReceiversWithoutPage() {
        return masterService.getReceiversWithoutPage();
    }

	@PostMapping(path = "user/profile")
	public ResponseEntity addImage(@RequestParam("file") MultipartFile file) throws IOException {
		return fileService.addImage(file);
	}

	@GetMapping(path = "user/profile/img")
	public Map<String, String> getImg() throws IOException {
		return fileService.getImg();
	}

	@PostMapping("/uppw")
	public ResponseEntity upPW(@RequestBody List<String> lst,@RequestHeader(name = "secondary") String secondary) {
		String fpw = lst.get(0);
		String npw = lst.get(1);
		return mst.editPw(fpw, npw,secondary);
	}

	// @GetMapping("/login")
	// public String login() {
	// SecurityContextHolder.getContext().setAuthentication(null);
	// ;

	// return "DISCONNECTED";
	// }
	@GetMapping("user/profile/img/delete")
	public Object deleteImg() throws IOException {
		Boolean res = fileService.deleteImage();

		return null;

	}

	@RequestMapping(value = "/uslog", method = RequestMethod.GET, produces = "application/json")

	public ResponseEntity<User> getUtilisateur() {

		return userService.uslog();
	}

	@RequestMapping("/api/user")
	public String user(Principal user) {
		CostumUserDetails users = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		if (users.getUser().getMaster() == null)
			return users.getUser().getLogo();
		else
			return users.getUser().getMaster().getLogo();
	}

	@PostMapping("/api/sendmail/document")
	public ResponseEntity sendDocument(@RequestBody mailClass mailClass) throws MessagingException, IOException {

		// ms.sendDocument(mailClass);
		ms.sendwithAtt(mailClass);
		return new ResponseEntity(HttpStatus.OK);

	}
	
	

	@PostMapping("/api/sendmail/documents/{id}")
	public ResponseEntity sendDocuments(@RequestBody mailClass mailClass, @PathVariable UUID id)
			throws MessagingException, IOException {

		// ms.sendDocument(mailClass);
		ms.sendLinkZip(mailClass, id);
		return new ResponseEntity(HttpStatus.OK);

	}

	
	@GetMapping("/api/getusers")
	public List<User> getUsers() {
		return userService.getUsers();
	}
	
	@GetMapping("/api/getuser")
    public ResponseEntity<User> getUser() {
	    CostumUserDetails user = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
	    
	    return new ResponseEntity<User>(user.getUser(),HttpStatus.OK);
    }

	@PostMapping("/api/sharedoc/{id}/{message}")
	public ResponseEntity shareDocument(@PathVariable(name = "id") UUID id,
			@PathVariable(name = "message") String message, @RequestBody List<String> lst) {
		userService.sendInterneMessage(lst, message, id);
		return new ResponseEntity<>(HttpStatus.OK);

	}

	
	@GetMapping("/api/getMessage/user")
	public Page<InterneMessage> getMessages(Pageable pageable) {
		return userService.getMessage(pageable);

	}
	
//	Get Receivers Message Of User Connect: 
//	@GetMapping("/api/getAllDocumentsPartager/user")
//	public Page<InterneMessage> getAllDocumentsBySenderOrReceiver(
//	    @RequestParam(value = "q", required = false) String fullName, 
//	    Pageable pageable
//	) {
//	    return userService.getAllDocumentsBySenderOrReceiver(fullName, pageable);
//	}
	@GetMapping("/api/getAllDocumentsPartager/user")
	public Page<InterneMessage> getAllBySender(@RequestParam("q") String fullName, Pageable pageable) {
	    return userService.getAllDocumentsBySenderOrReceiver(fullName, pageable);
	}






	@GetMapping("/api/getmessage/count")
	public int getCount() {
		return userService.getMessageCount();
	}

	@PostMapping("/api/seen/message")
	public ResponseEntity seenMessage(@RequestBody Long id) {
		return userService.messageSeen(id);
	}

	@GetMapping("/api/count/steps")
	public long countStpesOfNow() {
		return userService.stepToDo();
	}

	@GetMapping("/api/steps/todo/{tri}")
	public Page<Folder> findFoldersBySteps(@Param("filter") int filter, @Param("type") String type,@PathVariable String tri,@Param("myOnly") int myOnly, Pageable pageable) {

		return ss.findFoldersBySteps(pageable, type, filter,myOnly,tri);
	}

	@GetMapping("/trash/docs")
	public Page<ClientDoc> getClientDocs(Pageable pageable) {
		return userService.getDocsClt(pageable);

	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		SecurityContextHolder.clearContext();
		session.removeAttribute("SPRING_SECURITY_CONTEXT");
		session.invalidate();

		return "done";
	}

	@GetMapping("test")
	public void test() throws MessagingException, ParseException, IOException {/*
																				 * userService.TimeStap()
																				 */
	}

	// USER CONTROLLER
	@GetMapping("entity/user")
	public Entity getUserEntity() {
		return userService.getUserEntity();
	}

	@GetMapping("access/nature/{id}/{action}")
	public int hasAccessToNat(@PathVariable Long id, @PathVariable String action) {
		return userService.hasAccessToNat(id, action);
	}
	
	// add secondary User 
	@PutMapping("/secondary/addsecondary/{seconndery_id}")
	public ResponseEntity<?> addSecondary(@PathVariable("seconndery_id") Long seconndery_id) {
		return seconnderService.addSecondary(seconndery_id);
	}
	
	//get all users without secondary
	@GetMapping("/secondary/users")
	public List<User> getAllUsersWithoutSecondary() {
		return seconnderService.getAllUsersWithoutSecondary();
	}
	@GetMapping("/secondary/user")
	public ResponseEntity<User> getSecondaryUser() {
		return  seconnderService.getSecondaryUser();
	}

}
