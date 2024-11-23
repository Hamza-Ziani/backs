package com.veviosys.vdigit.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.veviosys.vdigit.models.Folder;
import com.veviosys.vdigit.models.FolderType;
import com.veviosys.vdigit.services.FolderService;

import lombok.AllArgsConstructor;

@CrossOrigin("*")
@RestController
@RequestMapping("api/v1/folder")
@AllArgsConstructor
public class FolderController {
    
	
	@Autowired
    private final FolderService folderService;
   

	
    @GetMapping("last-week")
    public ResponseEntity<Page<Folder>> getAllFolders(Pageable pageable){
        return ResponseEntity.ok(folderService.getLastWeekFolders(pageable));
    }
    @GetMapping("all")
    public ResponseEntity<List<Page<Folder>>> getRecentFolder(Pageable pageable){
        return ResponseEntity.ok(folderService.getRecentFolder(pageable));
    }
    
    @GetMapping("last-month")
    public ResponseEntity<Page<Folder>> getAllFoldersMonth(Pageable pageable){
        return ResponseEntity.ok(folderService.getLastMonthFolders(pageable));
    }
    @GetMapping("OLD")
    public ResponseEntity<Page<Folder>> getOLDFolders(Pageable pageable){
        return ResponseEntity.ok(folderService.getOLDFolders(pageable));
    }
    @GetMapping("{id}/childs")
    public ResponseEntity<Page<Folder>> getOLDFolders(@PathVariable UUID id,Pageable pageable,@RequestHeader(name = "secondary") String secondary){
        return ResponseEntity.ok(folderService.getFolderChilds(id, pageable,secondary));
    }
    // @GetMapping("count")
    // public ResponseEntity<Long> CountFolderbyMaster(){
     //     return ResponseEntity.ok(folderService.Countfolder());
    // }
    
    @PostMapping("{id}/childs")
    public void deleteLinks(@PathVariable(name = "id") UUID parent,@RequestBody List<UUID> childs)
    {
        //.println("test controller");
        folderService.deleteFolderFolderLinks(parent, childs);
    }
    @GetMapping("/gettype/{id}")
    public FolderType getType(@PathVariable Long id)
{
    return folderService.findTypeById(id); 
}    
    
    
    
   
}