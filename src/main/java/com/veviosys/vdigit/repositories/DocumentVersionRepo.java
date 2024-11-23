package com.veviosys.vdigit.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veviosys.vdigit.models.Document;
import com.veviosys.vdigit.models.DocumentVersion;

public interface DocumentVersionRepo extends JpaRepository<DocumentVersion, Long> {

	
	List<DocumentVersion> findByDocument(Document document);
	
}
