package com.veviosys.vdigit.repositories;

import java.util.UUID;

import com.veviosys.vdigit.models.zipMail;

import org.springframework.data.jpa.repository.JpaRepository;

public interface zipMailRepo  extends JpaRepository<zipMail, UUID>{
    
}
