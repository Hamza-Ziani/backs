package com.veviosys.vdigit.repositories;


import com.veviosys.vdigit.models.StorageVolumeRequest;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageVolumeRequestRepo extends JpaRepository<StorageVolumeRequest, Long> {
    
}
