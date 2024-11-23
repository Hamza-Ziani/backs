package com.veviosys.vdigit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veviosys.vdigit.models.SupportTech;

public interface SupportTechRepo extends JpaRepository<SupportTech, Long> {

	
    SupportTech findByUserId(Long Id);
    SupportTech findByUsername(String username);
}
