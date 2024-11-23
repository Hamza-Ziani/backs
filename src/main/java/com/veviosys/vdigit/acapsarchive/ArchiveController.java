package com.veviosys.vdigit.acapsarchive;




import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;
import com.veviosys.vdigit.classes.ArchiveClass;
import com.veviosys.vdigit.classes.ArchiveCourrier;
import com.veviosys.vdigit.classes.ArchiveEtapes;
import com.veviosys.vdigit.classes.ArchiveFormatResponse;
import com.veviosys.vdigit.classes.Fichier;
import com.veviosys.vdigit.classes.File64;
import com.veviosys.vdigit.models.MasterConfig;
import com.veviosys.vdigit.models.Sender;
import com.veviosys.vdigit.services.MasterConfigService;

@RestController
@RequestMapping("api/v1/archive")
public class ArchiveController {

    @Autowired
    ArchiveService archiveService;
    
  
    
    @Value("${acaps.archivedb.fileStorage}")
    private String path;
    
    @PostMapping(value = "search",consumes = {"application/json"})
    public Page<ArchiveCourrier> searchCourrier(@RequestBody ArchiveClass searchOptions,Pageable pageable) {
        
        return archiveService.searchCourrier(searchOptions,pageable);
        
    }
    
    @GetMapping(value = "/etapes/{folderid}")
    public Page<ArchiveEtapes> getEtapes(@PathVariable String folderid,Pageable pageable) {
        
        return archiveService.getEtapes(folderid,pageable);
        
    }
    
    
    @GetMapping(value = "/depart/{CourrierId}")
    public Page<Fichier> getDepartFiles(@PathVariable String CourrierId,Pageable pageable) {
        
        return archiveService.getDepartFiles(CourrierId,pageable);
        
    }
    @GetMapping(value = "/interne/{CourrierId}")
    public Page<Fichier> getInterneFiles(@PathVariable String CourrierId,Pageable pageable) {
        
        return archiveService.getInterneFiles(CourrierId,pageable);
        
    }
    
    @GetMapping(value = "/piece/{CourrierId}")
    public Page<Fichier> getpieceFiles(@PathVariable String CourrierId,Pageable pageable) {
        
        return archiveService.getPieceFiles(CourrierId,pageable);
        
    }
    
    @GetMapping(value = "/examen/{CourrierId}")
    public Page<Fichier> getExamenFiles(@PathVariable String CourrierId,Pageable pageable) {
        
        return archiveService.getExamenFiles(CourrierId,pageable);
        
    }
    
    @GetMapping(value = "/arrive/{CourrierId}")
    public Page<ArchiveFormatResponse> getArriveFiles(@PathVariable String CourrierId,Pageable pageable) {
        
        return archiveService.getArriveFiles(CourrierId,pageable);
        
    }
    
    @GetMapping(value = "/senders")
    public List<Sender> getSenders() {
        
        return archiveService.getSenders();
        
    }
    
    @PostMapping(value = "/files/download")
    public void getFile(@RequestBody Fichier fichier,HttpServletResponse response) throws IOException  {
           
            

            File downloadFile= new File(path + fichier.getName());
           
                byte[] isr = Files.readAllBytes(downloadFile.toPath());
                ByteArrayOutputStream out = new ByteArrayOutputStream(isr.length);
                out.write(isr, 0, isr.length);

                response.setContentType(fichier.getContentType());
                // Use 'inline' for preview and 'attachement' for download in browser.
                response.addHeader("Content-Disposition", "inline; filename=" + fichier.getName());

                OutputStream os;
                try {
                    os = response.getOutputStream();
                    out.writeTo(os);
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    
                }  
            
    }
    @PostMapping(value = "/files/download/arrive")
    public File64 getFileArrive(@RequestBody Fichier fichier) throws IOException  {
    
         return archiveService.getArriveFiles(fichier);
    }
    
}
