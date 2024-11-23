package com.veviosys.vdigit.repositories;

import java.util.List;

import com.veviosys.vdigit.models.TempSession;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TempSessionRepo  extends JpaRepository<TempSession, Long>{
    
    List<TempSession> findByUserId(Long id);
}
