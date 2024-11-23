package com.veviosys.vdigit.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.veviosys.vdigit.models.DocumentFullText;


public interface DocumentFulltextRepository extends JpaRepository<DocumentFullText, UUID> ,JpaSpecificationExecutor<DocumentFullText>{
	
	
	List<DocumentFullText> findByFullTextContainingIgnoreCaseAndMasterId(String fullText,Long id);

}
