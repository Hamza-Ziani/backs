package com.veviosys.vdigit.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import com.veviosys.vdigit.classes.DocumnentClass;
import com.veviosys.vdigit.classes.FolderClass;
import com.veviosys.vdigit.classes.Search;
import com.veviosys.vdigit.classes.SearchResultColumn;
import com.veviosys.vdigit.classes.mapSearch;
import com.veviosys.vdigit.models.Document;
import com.veviosys.vdigit.models.DocumentVersion;
import com.veviosys.vdigit.models.Folder;
import com.veviosys.vdigit.models.FrequencySearch;
import com.veviosys.vdigit.models.SearchAttributeValue;
import com.veviosys.vdigit.models.Track;
import com.veviosys.vdigit.repositories.FolderTypeRepo;
import com.veviosys.vdigit.services.FolderService;
import com.veviosys.vdigit.services.ReportsService;
import com.veviosys.vdigit.services.SearchService;

import net.sf.jasperreports.engine.JRException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Value;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path = "api/v1/search")
public class SearchController {

	@Autowired
	private SearchService searchService;
	@Autowired
	private FolderService folderService;
	@Autowired
	private ReportsService reportsService;
	@Value("${type.dep}")
	private String dep;
	@Value("${type.arr}")
	private String arr;
	
	/*
	 * @PostMapping(path = "folders") public List<Folder> searchFolders(@RequestBody
	 * FolderClass fc) { //.println(fc.toString()); return
	 * searchService.searchFolders(fc); }
	 * 
	 */
    // Recharche In Reporting :	
	@PostMapping(path = "folders")
	public Page<Folder> searchFolders(@RequestBody FolderClass fc,@RequestHeader(name = "secondary") String secondary , Pageable page) {
		//.println(fc.toString());
		return searchService.searchFolders(fc,secondary, page);
	}

	@GetMapping(path = "courrier")
	public Page<Folder> searchFolders(Pageable page) {
		return searchService.searchFoldersAccusation(page);
	}
	@Autowired
	FolderTypeRepo foderTypeRepo;
	 /*
     @PostMapping("/courrier/export")
    public List<HashMap<String, String>> exportCourrier(@RequestBody FolderClass cc, Pageable pageable) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        List<HashMap<String, String>> exportedList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> exportedItem = new HashMap<String, String>();
        List<Folder> pageFolder = searchService.searchFoldersAll(cc);
        String lbl;
        String v;

        String type;
                if(Objects.nonNull(cc.getType()))
                {
                    type = foderTypeRepo.getOne(cc.getType()).getCat();
                    if(type.equals(dep))
						lbl = "Entité émettrice";
                        else if(type.equals(arr))
                        lbl = "Affectation";
                      
                        else
                            lbl="Nature";
                }
                else
                    lbl="Nature";



        for (Folder folder : pageFolder) {
 
            if(Objects.nonNull(folder.getAutoRef()))
            exportedItem.put("Ref", String.valueOf(folder.getAutoRef()));
            else
                exportedItem.put("Ref", String.valueOf("N/A"));
            exportedItem.put("Date",String.valueOf(folder.getDate()));
			
            if(Objects.nonNull(folder.getNatureName()))
            exportedItem.put(lbl,folder.getNatureName());
            else
            {
                exportedItem.put(lbl,"N/A");
            }

            String emetOrDest ="";

                    if(Objects.nonNull(folder.getDest__()) )
                    {
                        if(!folder.getDest__().equals(""))
                        {

                        if(folder.getDest__().startsWith(","))
                        emetOrDest =     (String) folder.getDest__().subSequence(2, folder.getDest__().length());
                        else
                        emetOrDest = folder.getDest__();

                        }

                        else
                            emetOrDest ="N/A";


                    }
                    else if(Objects.nonNull(folder.getEmet__())) {
                        if(!folder.getEmet__().equals(""))
                            emetOrDest = folder.getEmet__();
                            else
                                emetOrDest ="N/A";
                    }
                    else {
                        emetOrDest ="N/A";
                    }

            exportedItem.put("Destinataire(s) / Emetteur", String.valueOf(emetOrDest));

            exportedItem.put("Objet", String.valueOf(folder.getObjet()));


 

            exportedList.add(exportedItem);
            exportedItem = new HashMap<String, String>();
        }

        return exportedList;
    }

*/
	// @GetMapping(path = "folder/{id}")
	// public Folder searchFolder(@PathVariable UUID id) {
	// 	FolderClass fc = new FolderClass();
	// 	fc.setClient((long) 1);
	// 	fc.setId(UUID.randomUUID().toString());
	// 	//.println(fc.toString());
	// 	return searchService.searchFolders(fc).stream().filter(f -> f.getId() == id).findFirst().orElse(null);// .getReference();
	// }

	@PostMapping(path = "save")
	public ResponseEntity<String> saveSearch(@RequestBody Search s,@RequestHeader(name = "secondary") String secondary) {
		try {
			searchService.saveSearch(s,secondary);
		} catch (Exception e) {
//System.err.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);

	}

	@PutMapping(path = "edit")
	public ResponseEntity<String> editSearch(@RequestBody Search s) {

		searchService.editSearch(s);

		return new ResponseEntity<>(HttpStatus.CREATED);

	}

	@GetMapping("most")
	public Page<FrequencySearch> getMostSearchs(Pageable page) {

		return searchService.getMostSearches(page);

	}

	@GetMapping("most/{id}")
	public List<SearchAttributeValue> getMostSearch(@PathVariable Long id) {

		return searchService.getSearchAttributes(id);

	}
	/*
	 * getSearchAttributes
	 * 
	 * @GetMapping("/searchs/get") public Page<FrequencySearch>
	 * getMostSearch(@RequestParam("page") int page ,@RequestParam("size")int size){
	 * 
	 * return searchService.getMostSearches(page,size);
	 * 
	 * }
	 */

	@PostMapping(path = "document")
	public List<List<SearchResultColumn>> searchDoc(@RequestBody DocumnentClass dc,@RequestHeader(name = "secondary") String secondary, Pageable pageable) throws SQLException {

		return searchService.rechMulti2(dc,secondary, pageable);
	}

	@PostMapping(path = "document/fulltext")
	public Page<Document> searchFullText(@RequestBody String text, Pageable pageable) throws SQLException {

		return searchService.fullTextSearch(text, pageable);
	}

	@DeleteMapping("most/{id}")
	public void deleteSearch(@PathVariable Long id) {
		searchService.deleteSearch(id);
	}

	@GetMapping("/folders/{id}")
	public Page<Folder> findFolders(@PathVariable(name = "id") String id, Pageable pageable) {
		//.println(id);
		return folderService.getFoldersbyDoc(id, pageable);
	}

	@PostMapping(path = "foldersreplace/{id}")
	public Page<Folder> searchFolders(@PathVariable(name = "id") UUID id, @RequestBody FolderClass fc, Pageable page,@RequestHeader(name = "secondary") String secondary) {
		return searchService.searchFolders(fc, page, id,secondary);
	}

	// doc ajouté par clients
	@GetMapping("documents/client")
	public Page<Document> getDocs(Pageable page) {
		return searchService.getDocs(page);
	}

	@GetMapping("documentscount/client")
	public int countDocsClient() {
		return searchService.getDocNumber();
	}

	// SEARCH VERSIONS OF DOC BY ID DOC

	@GetMapping("/documentVersion/{id}")
	public Page<DocumentVersion> getVersionsById(Pageable pageable, @PathVariable(name = "id") UUID id) {
		return searchService.getVersionDocs(id, pageable);
	}
	
	@GetMapping("/emailtracked/{id}")
    public Page<Track> getEmailTrackedByFolderId(Pageable pageable, @PathVariable(name = "id") UUID id) {
        return searchService.getEmailTracked(id, pageable);
    }

	@GetMapping("doc/{id}")
	public Document finddoc(@PathVariable UUID id) {
		return searchService.findDoc(id);
	}
	 
	@PostMapping("/courrier/export/{formatFile}")
	public ResponseEntity<Map<String,String>> exportCourrier(@RequestBody FolderClass cc,
            @PathVariable("formatFile") String formatFile, Pageable pageable,HttpServletResponse response,@RequestHeader(name = "secondary") String secondary) throws JRException, IOException   {
    
        return ResponseEntity.ok(reportsService.exportCourrier(cc, formatFile, pageable,secondary));

    }
	@PostMapping("/export/journal/{dd}/{df}")
    public ResponseEntity<Map<String,String>> exportJournal( @PathVariable(required = false) Date dd, @PathVariable(required = false) Date df) throws JRException, IOException   {
    
        return ResponseEntity.ok(reportsService.exportJournal(dd,df));

    }
}
