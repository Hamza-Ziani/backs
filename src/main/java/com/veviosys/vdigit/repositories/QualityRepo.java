package com.veviosys.vdigit.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veviosys.vdigit.models.Quality;
import com.veviosys.vdigit.models.User;

public interface QualityRepo extends JpaRepository<Quality, Long> {
 
    List<Quality> getQualityByMaster(User master);
    
    List<Quality> findQualityByNameContainingIgnoreCaseAndMaster(String q,User master);
    
    Quality getQualityByCode(String code);
}
