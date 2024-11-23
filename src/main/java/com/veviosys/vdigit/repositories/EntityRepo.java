package com.veviosys.vdigit.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.veviosys.vdigit.models.Entity;

public interface EntityRepo  extends JpaRepository<Entity, Long>  {
Entity findByName(String name); 
Entity findByUsersUserId(Long id);
List<Entity>findByMasterUserId(Long id);
Page<Entity>findByMasterUserId(Long id,Pageable pageable);
}
 