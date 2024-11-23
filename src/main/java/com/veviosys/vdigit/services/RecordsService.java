package com.veviosys.vdigit.services;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.veviosys.vdigit.models.Document;
import com.veviosys.vdigit.models.Folder;
import com.veviosys.vdigit.repositories.DocumentRepo;
import com.veviosys.vdigit.repositories.FolderRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecordsService {
	
	
	final private DocumentRepo repo;
	final private FolderRepo repoFolder;
	public void setArchived(List<UUID> uuids) {
		
		for (UUID uuid : uuids) {
		    
		    Document doc = repo.findById(uuid).orElse(null);
		    Folder folder = repoFolder.findById(uuid).orElse(null);
		    
		    if(Objects.nonNull(doc)) {
		        repo.updateArchived(uuid);
		    }
		    
		    if(Objects.nonNull(folder)) {
		        repoFolder.updateArchived(uuid); 
		    }
		            
			
		}
		
	}

}
