package com.veviosys.vdigit.integrationCm.controller;





import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.veviosys.vdigit.classes.Login;
import com.veviosys.vdigit.integrationCm.services.IntegrationService;



@RestController
@RequestMapping("api/v1/integration")
public class IntegrationController {
    
       @Autowired
    private IntegrationService integrationService; 
    
    
       @Secured("ROLE_MASTER")
    @GetMapping("order/{name}")
    public  Map<String, String> orderCheck(@PathVariable String name) {
       
       return integrationService.checkOrder(name);
       
    }
    
       @Secured("ROLE_MASTER")
    @PostMapping("chaine")
    public  boolean getChaine(@RequestHeader(name = "secondary") String secondary) {
       
        return integrationService.getChaineConnexion(secondary);
       
    }
    
       @Secured("ROLE_MASTER")
    @PostMapping("attribute")
    public  boolean getAttribute(@RequestHeader(name = "secondary") String secondary) {
       
       return integrationService.getAttribute(secondary);
    }
    
       @Secured("ROLE_MASTER")
    @PostMapping("docType")
    public boolean getDocType(@RequestHeader(name = "secondary") String secondary) {
       
        return  integrationService.getTypeDocument(secondary);
       
    }
       
     
    @GetMapping("export-docs/{idFolder}")
    public boolean exportDocuments(@PathVariable UUID id ) throws IOException {
        
        return integrationService.exportDocuments(id);
    }
    
      
    @GetMapping("export-doc/{idDoc}/{folderId}")
    public boolean exportDocument(@PathVariable("idDoc") UUID id,@PathVariable("folderId") UUID folderId) throws IOException {
        
        return integrationService.exportDocument(id,folderId);
    }
}
