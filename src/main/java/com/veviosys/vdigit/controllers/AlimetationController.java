package com.veviosys.vdigit.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import java.util.UUID;


import javax.mail.MessagingException;
import javax.persistence.Lob;


import com.itextpdf.text.DocumentException;
import com.veviosys.vdigit.classes.CloneEtapeClass;
import com.veviosys.vdigit.classes.DocumnentClass;
import com.veviosys.vdigit.classes.EditEtapeClass;
import com.veviosys.vdigit.classes.File64;
import com.veviosys.vdigit.classes.FolderClass;
import com.veviosys.vdigit.classes.QualityClass;
import com.veviosys.vdigit.classes.SubSearchClass;
import com.veviosys.vdigit.models.Attribute;
import com.veviosys.vdigit.models.Client;

import com.veviosys.vdigit.models.CloneEtape;
import com.veviosys.vdigit.models.Document;
import com.veviosys.vdigit.models.DocumentFolder;
import com.veviosys.vdigit.models.DocumentType;

import com.veviosys.vdigit.models.Etape;
import com.veviosys.vdigit.models.Folder;
import com.veviosys.vdigit.models.FolderType;
import com.veviosys.vdigit.models.GroupUser;
import com.veviosys.vdigit.models.Nature;
import com.veviosys.vdigit.models.Quality;
import com.veviosys.vdigit.models.Sender;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.models.receiver;
import com.veviosys.vdigit.models.zipMail;
import com.veviosys.vdigit.repositories.DocumentRepo;
import com.veviosys.vdigit.services.AlimentationService;
import com.veviosys.vdigit.services.FileStorageServiceImpl;
import com.veviosys.vdigit.services.GroupUserService;
import com.veviosys.vdigit.services.QualityService;
import com.veviosys.vdigit.services.SearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import lombok.Getter;
import lombok.Setter;


//@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path = "api/v1")

public class AlimetationController {

	@Autowired
	private AlimentationService serviceAlimentation;
	@Autowired
	private SearchService searchService;
	@Autowired
    private QualityService qualityService;
	@Autowired
	private GroupUserService  groupUserService;
	@Autowired
	private FileStorageServiceImpl fileStorageServiceImpl;

	public User GetDocuments() {
		return serviceAlimentation.connectedUser();
	}

	public Long GetMasterId() {
		return serviceAlimentation.connectedUserMaster(Long.valueOf(serviceAlimentation.connectedUser().getUserId()));
	}

	@GetMapping(path = "foldertypes")
	public List<FolderType> GetFoldersType() {
		return serviceAlimentation.GetFolderTypes();
	}

	@PostMapping(path = "findFolderSub/{tri}")
	public Page<Folder> findFoldersUsersSub(@Param("filter") int filter, @Param("type") String type, Pageable pageable,@RequestBody SubSearchClass search,@PathVariable String tri ) {
	  return searchService.findFoldersUsersSub(pageable, type, filter,tri,search);
	}
	
	@PostMapping(path = "findMyFolder")
    public Page<Folder> findMyFolders(Pageable pageable,@RequestBody FolderClass folder) {
      return searchService.findMyFolders(pageable,folder);
    }
	
	
	@GetMapping(path = "quality/all")
    public List<Quality> getAllQualityNoPage() {
        return qualityService.getAllQualityNoPage();
    }
	
	@GetMapping(path = "group/all")
    public List<GroupUser> getAllGroupUserNoPage() {
        return groupUserService.getAllGroupUserNoPage();
    }
	
	
	@GetMapping(path = "user/title/{title}")
    public List<User> getUserByTitle(@PathVariable String title) {
        return qualityService.getUserByTitle(title);
    }
	
	@GetMapping(path = "user/parent/{id}")
    public List<User> getUserByParent(@PathVariable Long id) {
        return qualityService.getUserByparent(id);
    }
	
	@GetMapping(path = "user/child/{id}")
    public List<User> getChild(@PathVariable Long id) {
        return qualityService.getUserByChild(id);
    }
	

	
	@PostMapping(path = "/nextEtape")
    public CloneEtape nextEtape(@RequestBody CloneEtapeClass cloneetape) {
        return qualityService.getNextEtape(cloneetape);
    } 
	

	
	@GetMapping(path = "clients")
    public List<Client> GetClients() {
        return serviceAlimentation.GetClients();
    }
	public Folder AddFolder() {

		return null;
		// serviceAlimentation.GetFolder();
	}

 
	@GetMapping(path = "documentstypes/{id}/attr2")
	public HashMap<String, Object>getDocumentTypeAttrs2(@PathVariable Long id) throws SQLException {
	    
	   return serviceAlimentation.getAttrsByType2(id);
	 
	}
	 
	
	@PostMapping(path = "foders")
	public ResponseEntity<Folder> postFolder(@RequestBody FolderClass F,@RequestHeader(name = "secondary") String secondary ) throws ParseException {
		
	
			Folder folder = serviceAlimentation.addFolder(F,secondary);
			if (Objects.nonNull(folder))
				return new ResponseEntity<Folder>(folder, HttpStatus.OK);
			// return service.GetFolder();
			return new ResponseEntity<Folder>(folder, HttpStatus.CONFLICT);
		
	}

	@GetMapping(path = "documentstypes")
	public List<DocumentType> getDocumentTypes() {
		return serviceAlimentation.getDocumentTypes();
		// return service.GetFolder();
	}

	@GetMapping(path = "documentstypes/{id}")
	public DocumentType getDocumentType(@PathVariable Long id) {
		return serviceAlimentation.getDocumentTypes().stream().filter(dt -> dt.getId() == id).findFirst().get();

	}

	@GetMapping(path = "documentstypes/{id}/attr")
	public List<Attribute> getDocumentTypeAttrs(@PathVariable Long id) {
		return serviceAlimentation.getAttrsByType(id);

	}

	@Autowired
	DocumentRepo dr;
	// @GetMapping("/files/{id}")
	// public ResponseEntity<Resource> getFile(@PathVariable String id) throws
	// IOException {

	// final String contentType =
	// fileStorageServiceImpl.getDocumentContentType(UUID.fromString(id));
	// Resource file = null;
	// try {
	// UUID idDoc= UUID.fromString(id);
	// Document d= dr.getOne(idDoc);
	// file = fileStorageServiceImpl.load(d,UUID.fromString(id));
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return
	// ResponseEntity.ok().contentType(MediaType.parseMediaTypes(contentType).get(0))
	// .header(HttpHeaders.CONTENT_DISPOSITION,
	// String.format("attachment; filename=\"%s\"", file.getFilename()))
	// .body(file);
	// }

   // get file by Id:	
	@GetMapping("/files/view/{id}")
	public File64 getB64File(@PathVariable String id,@RequestHeader(name = "secondary") String secondary) throws IOException {
		File64 file64 = null;
		try {
			file64 = fileStorageServiceImpl.loadBase64String(UUID.fromString(id),secondary);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file64;
	}

	@PostMapping(path = "documents")
	public ResponseEntity<Map<String, String>> postDocument(@RequestBody DocumnentClass document,@RequestHeader(name = "secondary") String secondary) throws SQLException {

		// return new Document();
		Map<String, String> MapId = serviceAlimentation.addDocument(document,secondary);
		if (Objects.nonNull(MapId))
			return new ResponseEntity<Map<String, String>>(MapId, HttpStatus.OK);
		return new ResponseEntity<Map<String, String>>(MapId, HttpStatus.CONFLICT);
		// return service.GetFolder();
	}

	@PostMapping(path = "documentfile/{id}/{convert}/{crb}/{type}")
	public ResponseEntity postDocumentAndFile(@RequestParam("file") MultipartFile file,@RequestParam(name = "img",required = false) MultipartFile img, @PathVariable UUID id,
			@PathVariable int convert, @PathVariable int crb, float x, float y, float h, float w, String val,
			@PathVariable int type) {

		try {

		//	fileStorageServiceImpl.save(file, id, convert, crb, x, y, h, w, val, type,img);
		fileStorageServiceImpl.saveFile(file, id, convert, crb, x, y, h, w, val, type,img);
		} catch (IOException | DocumentException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		// return new Document();
		return new ResponseEntity<>(HttpStatus.OK);
		// return service.GetFolder();
	}

	// @PostMapping(path = "documentfile/{id}/{convert}/{crb}")
	// public ResponseEntity postDocumentAndFile(@RequestParam("file") MultipartFile
	// file, @PathVariable UUID id,@PathVariable int convert,@PathVariable int crb)
	// {

	// try {
	// fileStorageServiceImpl.save(file, id,convert,crb);
	// } catch (IOException | DocumentException e) {
	// return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	// }

	// // return new Document();
	// return new ResponseEntity<>(HttpStatus.OK);
	// // return service.GetFolder();
	// }

	@GetMapping(path = "documents")
	public List<Document> getDocuments() {

		// document.id=UUID.randomUUID();

		return serviceAlimentation.getDocuments();
		// return service.GetFolder();
	}

	@GetMapping(path = "logo")
	public Logo getLogo() {

		// document.id=UUID.randomUUID();
		Logo l = new Logo();
		l.setBase64(serviceAlimentation.getLogo());
		return l;
		// return "hello";
	}

	/*************************************************************************************************************************
	 **************************************************************************************************************************
	 ***************************************** Linking
	 * Region*******************************************************************
	 **************************************************************************************************************************
	 ***************************************************************************************************************************/

	// folder to folder
	@PutMapping("link/folder/{id}")
	public Page<Folder> availableFolders(@RequestBody FolderClass fc, @PathVariable UUID id, Pageable pageable) {

		return searchService.availableFoldersToLinkSearch(fc, id, pageable);

	}

	@PostMapping("link/folder/{id}")
	public void linkFolders(@PathVariable UUID id, @RequestBody List<UUID> foldersIds) {

		serviceAlimentation.linkFolderToFolder(id, foldersIds);

	}

	@PostMapping("linking/{id}/{id2}")
	public void linkDocFolder(@PathVariable UUID id, @PathVariable UUID id2) {
		serviceAlimentation.link(id, id2);

	}

	@PostMapping("link/document/{id}")
	public void linkDocument(@PathVariable UUID id, @RequestBody List<UUID> foldersIds) {

		serviceAlimentation.linkDocumetToFolders(id, foldersIds);

	}

	@PostMapping("link/documents/{id}")
	public void linkDocuments(@PathVariable UUID id, @RequestBody List<UUID> foldersIds) {

		serviceAlimentation.linkDocumentsToFolder(id, foldersIds);

	}

	// link accusation --> courrier
	@PostMapping("link/accusation/{id}/{id2}")
	public ResponseEntity linkAccuCourrier(@PathVariable UUID id, @PathVariable UUID id2) {
		return serviceAlimentation.linkAccus(id, id2);
	}

	//
	@PutMapping("link/document/{id}")
	public Page<Folder> linkDocumentAvilableFolders(@RequestBody FolderClass fc, @PathVariable UUID id,
			Pageable pageable) {

		return searchService.availableFoldersToLinkDocument(fc, id, pageable);

	}

	@DeleteMapping("link/document/{did}/f/{fid}")
	public void unlinkDocument(@PathVariable UUID did, @PathVariable UUID fid) {

		serviceAlimentation.unlinkDocumetToFolders(did, fid);

	}

	/*
	 * Common functionalities
	 * 
	 */
	@GetMapping("folder/{id}/documents")
	public ResponseEntity<Page<DocumentFolder>> getFolderDocuments(@PathVariable UUID id, Pageable pageable) {

		return serviceAlimentation.getFolderDocuments(id, pageable);
	}
	@GetMapping("folder/{id}/documents/list")
    public ResponseEntity<List<DocumentFolder>> getFolderDocumentsList(@PathVariable UUID id) {

        return serviceAlimentation.getFolderDocumentsList(id);
    }


	@GetMapping("folder/{id}/etape")
	public Page<DocumentFolder> getEtape(@PathVariable UUID id, Pageable pageable) {

		return serviceAlimentation.getDocEtape(id, pageable);
	}

	@PostMapping("link/documentFolders/{id}")
	public void unlinkDocuments(@PathVariable String id, @RequestBody List<String> fid) {

		serviceAlimentation.unlinkDocumetToFolder(id, fid);

	}

	// ADD DOCUMENT BY CLIENT
	@PostMapping("add/document/client")
	public Map<String, String> addDocumentByClient(@RequestBody DocumnentClass dc) {
		return serviceAlimentation.addDocsByClient(dc);
	}

	// EDIT DOC ADDED BY CLIENT
	@PostMapping("add/document/client/confirme/{id}")

	public ResponseEntity ConfirmeDocClient(@RequestBody DocumnentClass dc, @PathVariable(name = "id") UUID id)
			throws SQLException {
		return serviceAlimentation.ConfirmeDocClient(dc, id);
	}

	// GET NAture
	@GetMapping("/nature/getall")
	public List<Nature> gNatures(@RequestParam("action") String action) {
		return serviceAlimentation.gNatures(action);
	}

	@GetMapping("/process/steps/{id}")
	public List<Etape> gSteps(@PathVariable Long id) {
		return serviceAlimentation.findStepsByProcess(id);
	}

	// EDIT STATE AND INSTR
//	@PostMapping("/editstep/{id}")
//	public ResponseEntity editstep(@PathVariable Long id, @RequestBody EditEtapeClass editEtape) throws ParseException, MessagingException, IOException {
//	   
//		return serviceAlimentation.editStep(id,editEtape);
//	}
	
	@PostMapping("/editstep/{id}")
    public ResponseEntity editstep(@PathVariable Long id, @RequestBody EditEtapeClass editEtape,@RequestHeader(name = "secondary") String secondary,@Param("directbo") int directbo,@Param("normal") int normal,@Param("arrskip") int arrskip) throws ParseException, MessagingException, IOException {
       
        return serviceAlimentation.editStep(id,editEtape,directbo,normal,arrskip,secondary);
    }

	@GetMapping("/laststep/{id}")

	public ResponseEntity laststep(@PathVariable Long id,@Param("abondonne") int abondonne,@RequestHeader(name = "secondary") String secondary,@Param("clot") int clot,@Param("comment") String comment) throws ParseException {
		return serviceAlimentation.lastStepValidate(id,abondonne,clot,comment,secondary);
	}

	@GetMapping("/step/comment/{id}/{Step}/{idStep}")
	public CloneEtape getComment(@PathVariable UUID id, @PathVariable int Step,@PathVariable Long idStep) {
		return serviceAlimentation.getComment(id, Step,idStep);
	}

	@GetMapping("/courrier/{id}/steps")
	public List<CloneEtape> getSteps(@PathVariable UUID id) {
		return serviceAlimentation.courrierSteps(id);
	}

	// GET version doc
	@GetMapping("documentVersion/{id}")
	public File64 get(@PathVariable Long id,@RequestHeader(name = "secondary") String secondary) throws IOException {
		return fileStorageServiceImpl.loadBase64StringVersion(id,secondary);
	}

//	@PostMapping("/stepback/{id}")
//
//	public ResponseEntity backToPrevious(@PathVariable Long id, @RequestBody String comm) throws MessagingException, IOException {
//		return serviceAlimentation.returnCourrier(id, comm);
//	}
	
	@PostMapping("/stepback/{id}")

    public ResponseEntity backToPrevious(@PathVariable Long id, @RequestBody String comm,@RequestHeader(name = "secondary") String secondary) throws MessagingException, IOException {
        return serviceAlimentation.returnCourrier1(id, comm,secondary);
    }

	@GetMapping("/step/motif/{id}/{Step}/{idStep}")
	public CloneEtape getMotif(@PathVariable UUID id, @PathVariable int Step,@PathVariable Long idStep) {
		return serviceAlimentation.getMotif(id, Step,idStep);
	}



	@GetMapping("/remove/clientdoc/{id}")
	public ResponseEntity deleteClientDoc(@PathVariable Long id) {
		return serviceAlimentation.deleteClientDoc(id);
	}

	@PostMapping("/save/zip/{sec}/{pw}")
	public zipMail saveZip(@PathVariable int sec, @PathVariable String pw, @RequestBody List<UUID> docs)
			throws IOException

	{
		zipMail zMail = new zipMail();
		zMail.setHasPassword(sec);
		zMail.setPassowrd(pw);
		return fileStorageServiceImpl.saveZip(docs, zMail);

	}

	@GetMapping("nature/{id}")
	public List<Etape> findByNature(@PathVariable Long id) {
		return serviceAlimentation.getStepsByNature(id);
	}

	@GetMapping("proc/{id}")
	public List<Etape> findByProc(@PathVariable Long id) {
		return serviceAlimentation.getStepsByProc(id);
	}

	@GetMapping("receivers")
	public List<receiver> getAllReceivers() {
		return serviceAlimentation.getAllReceivers();
	}

	@GetMapping("senders")
	public List<Sender> getAllSenders() {
		return serviceAlimentation.getAllSenders();
	}
	
	@GetMapping("/step/folder/{id}")
	public CloneEtape findCurrentStepByFolder(@PathVariable UUID id) {
		return serviceAlimentation.findCurrentStepByFolder(id);
		
	}
	
	@GetMapping("/files/download/{id}")
	public File64 downloadFile(@PathVariable String id,@RequestHeader(name = "secondary") String secondary) throws IOException {
		File64 file64 = null;
		try {
			file64 = fileStorageServiceImpl.downfile(UUID.fromString(id),secondary);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file64;
	}
}

@Getter
@Setter
class Logo {
	@Lob
	String Base64;
}
