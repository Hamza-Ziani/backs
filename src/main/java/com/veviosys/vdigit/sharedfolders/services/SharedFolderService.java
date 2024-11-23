package com.veviosys.vdigit.sharedfolders.services; 

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.veviosys.vdigit.models.Document;
import com.veviosys.vdigit.models.Folder;
import com.veviosys.vdigit.models.InterneMessage;
import com.veviosys.vdigit.models.RecieveMessage;
import com.veviosys.vdigit.models.RecieveMessagePK; 
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.repositories.EntityRepo;
import com.veviosys.vdigit.repositories.FolderRepo; 
import com.veviosys.vdigit.repositories.UserRepository;
import com.veviosys.vdigit.services.userService;
import com.veviosys.vdigit.sharedfolders.models.SharedFolder;
import com.veviosys.vdigit.sharedfolders.models.SharedWith;
import com.veviosys.vdigit.sharedfolders.repository.SharedFolderRepo;
import com.veviosys.vdigit.sharedfolders.repository.SharedWithRepo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SharedFolderService {

	@Autowired
	SharedFolderRepo sharedFolderRepo;
	@Autowired 
	SharedWithRepo sharedWithRepo;
	@Autowired 
	FolderRepo folderRepo;
	@Autowired
	EntityRepo entityRepo;
	@Autowired 
	UserRepository userRepository;
	@Autowired userService serviceUser;
	public void shareWithEntities(UUID folderId,List<Long>users,String message)
	{
		List<User> listUsers = new ArrayList<User>();
		int i=0;
		String entity="";
		Folder dc = folderRepo.findById(folderId).orElse(null);
 
		for (Long id : users) {
			 User user =userRepository.findById(id).orElse(null);
			listUsers.add(user);
		 
		}
	 SharedFolder msg = new SharedFolder();
	//	 msg.setReceivers(listUsers);
 
		msg.setMessage(message);
		msg.setSender(serviceUser.connectedUser());
		//msg.setMessage(message);
	 
		folderRepo.save(dc);
		msg.setFolder(dc);
		msg.setSentDate((new Date()));
		msg = sharedFolderRepo.saveAndFlush(msg);
		String u = "a ";
		int xx = 0;

		for (User user : listUsers) {
			SharedWith rm = new SharedWith();
			 
			rm.setId(new RecieveMessagePK(msg.getId(), user.getUserId()));
			rm.setMessage(msg);
			rm.setUser(user);
			if (xx == 0) {
				u += user.getFullName();
				xx = 444;
			} else {
				u += ", " + user.getFullName();
			}
			rm.setSeen(0);
		rm=	sharedWithRepo.save(rm);
System.out.println(rm.getSeen());
		}
	}
	public Page<SharedFolder>getAll(Pageable pageable){
		User u=serviceUser.connectedUser(); 
		Page<SharedFolder>p=sharedFolderRepo.findByReceiversUserUserIdOrderBySentDateDesc(u.getUserId(), pageable);
		List<SharedFolder>sf=p.getContent();
		for (SharedFolder sharedFolder : sf) {
			List<SharedWith>sw=	sharedFolder.getReceivers();
			for (SharedWith swi : sw) {
				if(swi.getUser().getUserId().equals(u.getUserId()) && swi.getSeen()==0){
					swi.setSeen(1);
					sharedWithRepo.save(swi);
				}

				} 
			}
		
		return p;
	}
	
	// Get all shared folders by sender and filter by fullName (or title) and pagination
	public Page<SharedFolder> getAllBySender(String fullName, Pageable pageable) {
	    User user = serviceUser.connectedUser(); 
	    Page<SharedFolder> p;
	    if (fullName == null || fullName.isEmpty()) {
	        p = sharedFolderRepo.findBySender(user, pageable); // Add this repository method
	    } else {
	        p = sharedFolderRepo.findBySenderAndReceiversUserFullNameContainingIgnoreCase(user, fullName, pageable);
	    }

	    return p;
	}



	public ResponseEntity deleteshare(Long id){
	    log.info("{}",serviceUser.connectedUser().getUserId());
	    sharedWithRepo.delete(sharedWithRepo.findByMessageIdAndUser(id,serviceUser.connectedUser()));
	    
	    sharedFolderRepo.delete(sharedFolderRepo.findById(id).get());
	    
        return new ResponseEntity(HttpStatus.OK);
	}
	public int count(){
	return	sharedFolderRepo.findByReceiversUserUserIdAndReceiversSeen(serviceUser.connectedUser().getUserId(),0).size();
	}
}
