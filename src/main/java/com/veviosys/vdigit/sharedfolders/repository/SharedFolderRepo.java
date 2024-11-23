package com.veviosys.vdigit.sharedfolders.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.veviosys.vdigit.models.Folder;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.sharedfolders.models.SharedFolder;

import java.util.List;
import java.util.UUID;
 

public interface SharedFolderRepo extends JpaRepository<SharedFolder,Long>{	
    
    
	  Page<SharedFolder> findByReceiversUserUserIdOrderBySentDateDesc(Long id ,Pageable pageable);
	  List<SharedFolder> findByReceiversUserUserIdAndReceiversSeen(Long id,int seen);
	  
	  
      
	  // Filter by sender and fullName, and order by creation date descending
	    Page<SharedFolder> findBySender(User user, Pageable pageable);
	    Page<SharedFolder> findBySenderAndReceiversUserFullNameContainingIgnoreCase(User sender, String fullName, Pageable pageable);



      
    
	  List<SharedFolder>  findByFolderId(UUID id);
}
