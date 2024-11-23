package com.veviosys.vdigit.reposietories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.veviosys.vdigit.models.ElementTypeGroup;

public interface ElementTypeGroupRepository extends JpaRepository<ElementTypeGroup, Long> {
 
	
	Page<ElementTypeGroup> findByGroupLabelContainingIgnoreCase(String l , Pageable page);
	
	
}
