package com.veviosys.vdigit.controllers;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import com.veviosys.vdigit.classes.DocumnentClass;
import com.veviosys.vdigit.classes.FolderClass;
import com.veviosys.vdigit.models.Folder;
import com.veviosys.vdigit.services.AlimentationService;
import com.veviosys.vdigit.services.updateDocumentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/edit")
public class EditingController {

	@Autowired
	private AlimentationService alimentationService;
	@Autowired
	private updateDocumentService updateDocumentService;

	@PutMapping("folder/{id}")
	public Folder editFolder(@PathVariable UUID id, @RequestBody FolderClass folder,@RequestHeader(name = "secondary") String secondary) {

		return alimentationService.editFolder(id, folder,secondary);

	}

	@DeleteMapping("folder/{id}")
	public void deleteFolder(@PathVariable UUID id,@RequestHeader(name = "secondary") String secondary) {

		alimentationService.deleteFolder(id,secondary);

	}
 
	@DeleteMapping("folder/without-access/{id}")
    public void deleteFolderWithoutAccess(@PathVariable UUID id,@RequestHeader(name = "secondary") String secondary) {

        alimentationService.deleteFolderWithoutAccess(id,secondary);

    }

	@PostMapping(value = "/document")
	public ResponseEntity editDocument(@RequestBody DocumnentClass dt,@RequestHeader(name = "secondary") String secondary) throws SQLException {
		return updateDocumentService.updateDoc(dt,secondary);

	}

	@PostMapping(value = "/document/process")
	public ResponseEntity editDocProcess(@RequestBody DocumnentClass dc) throws SQLException {
		return updateDocumentService.editDocProcess(dc);
	}

	@DeleteMapping(value = "/delete/{id}")
	public ResponseEntity deleteDocument(@PathVariable UUID id,@RequestHeader(name = "secondary") String secondary) {
		return updateDocumentService.delete(id,secondary);

	}

	@PostMapping(value = "/delete/docs")
	public ResponseEntity deleteDocuments(@RequestBody List<UUID> docs,@RequestHeader(name = "secondary") String secondary) {
		return updateDocumentService.deleteDocs(docs,secondary);

	}

	@DeleteMapping(value = "/deletedocclient/{id}")
	public ResponseEntity deleteDocClient(@PathVariable Long id) {
		return updateDocumentService.deleteDocClt(id);

	}

	@DeleteMapping(value = "/delete/version/{id}")
	public ResponseEntity deleteDocumentVersion(@PathVariable Long id) {
		return updateDocumentService.deleteDocVers(id);

	}

	@GetMapping(value = "docID/{id}")
	public DocumnentClass findBYid(@PathVariable UUID id) {
		return updateDocumentService.FindById(id);
	}

	@GetMapping("/access/document/delete/{id}")
	public int hasAccessToDelete(@PathVariable UUID id) {
		return updateDocumentService.hasAccessToDelete(id);
	}

	@GetMapping("/access/document/edit/{id}")
	public int hasAccessToEdit(@PathVariable UUID id) {
		return updateDocumentService.hasAccessToEdit(id);
	}

	@GetMapping("/access/courrier/delete/{id}")
	public int hasAccessToDeleteN(@PathVariable UUID id) {
		return updateDocumentService.hasAccessToDeleteN(id);
	}

	@GetMapping("/access/courrier/edit/{id}")
	public int hasAccessToEditN(@PathVariable UUID id) {
		return updateDocumentService.hasAccessToEditN(id);
	}
}
