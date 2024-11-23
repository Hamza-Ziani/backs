package com.veviosys.vdigit.integrationCm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veviosys.vdigit.integrationCm.models.OredrIntegration;

public interface OrderIntegretionRepo extends JpaRepository<OredrIntegration, Long>
{
  
    OredrIntegration findByName(String name);
}
