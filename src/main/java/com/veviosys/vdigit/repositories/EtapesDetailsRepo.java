package com.veviosys.vdigit.repositories;

import com.veviosys.vdigit.models.EtapeDetail;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EtapesDetailsRepo extends JpaRepository<EtapeDetail,Long>
{
    
}