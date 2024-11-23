package com.veviosys.vdigit.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veviosys.vdigit.models.DocumentsHeadAndFooter;

public interface DocumentHeadFootRepository extends JpaRepository<DocumentsHeadAndFooter, Long> {

	
	List<DocumentsHeadAndFooter> findByMasterId(Long masterId);

	DocumentsHeadAndFooter findByMasterIdAndType(Long userId, char c);
	
}
