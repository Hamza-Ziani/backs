package com.veviosys.vdigit.controllers;

import java.util.List;
import java.util.UUID;

import com.veviosys.vdigit.models.Folder;
import com.veviosys.vdigit.services.FolderFavoriteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/favorite-folder")
public class FolderFavoriteController {
    @Autowired
    private FolderFavoriteService folderFavoriteService;

    @GetMapping(value="/Find")
    public ResponseEntity <Page<Folder>> findall(Pageable pageable){
        return  new ResponseEntity<Page<Folder>>(folderFavoriteService.getFavouriteFolderByUser(pageable), HttpStatus.OK) ;
    } 
    
    @PutMapping("/{folderId}")
    public ResponseEntity<Folder> add(@PathVariable("folderId") UUID folderId ){
       
        return new ResponseEntity<Folder>(folderFavoriteService.add(folderId), HttpStatus.CREATED) ;
    }
    


    @DeleteMapping("/{folderId}")
       public ResponseEntity<?> delete(@PathVariable("folderId") UUID folderId ){

            folderFavoriteService.delete(folderId);
           return ResponseEntity.ok().build();
       }
    @GetMapping("/user")
    public ResponseEntity<List<UUID>> findFolderByUser(){
        return ResponseEntity.ok(folderFavoriteService.findFolderByUser());
    }
}