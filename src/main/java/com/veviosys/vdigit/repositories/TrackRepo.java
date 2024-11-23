package com.veviosys.vdigit.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veviosys.vdigit.models.Folder;
import com.veviosys.vdigit.models.Track;



public interface TrackRepo extends JpaRepository<Track, Long> {
   
    List<Track> findByFolder(Folder folder);
} 
