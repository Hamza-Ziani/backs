package com.veviosys.vdigit.sharedfolders.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.veviosys.vdigit.models.RecieveMessagePK;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.sharedfolders.models.SharedFolder;
import com.veviosys.vdigit.sharedfolders.models.SharedWith; 

public interface SharedWithRepo extends JpaRepository<SharedWith,RecieveMessagePK> {

	List<SharedWith>  findByMessageId(long id);
	
	SharedWith  findByMessageIdAndUser(Long id,User userId);
}
