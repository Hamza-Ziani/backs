package com.veviosys.vdigit.repositories;

import com.veviosys.vdigit.models.PermissionDocument;
import com.veviosys.vdigit.models.User;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionDocumentRepo extends JpaRepository<PermissionDocument,Long> {
	
	List<PermissionDocument> findByMaster(User master);
	Page<PermissionDocument> findByMaster(User master,Pageable pageable);

	Page<PermissionDocument> findByNomContainingIgnoreCaseAndMaster(String q,User master,Pageable pageable);
    
}