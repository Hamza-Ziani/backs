package com.veviosys.vdigit.repositories;

import java.util.List;

import com.veviosys.vdigit.models.Sender;
import com.veviosys.vdigit.models.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SenderRepo extends JpaRepository<Sender, Long> {
    Page<Sender> findSenderByMasterUserId(Long id, Pageable pageable);
    Sender findSenderByNameAndMasterUserId(String name,Long id);
    List<Sender> findSenderByMasterUserId(Long id);
    Sender findSenderByNameAndMaster(String name,User u);
    
    @Query(value = "select * from sender where name = :name and master = :master", nativeQuery = true)   
    Sender findSenderByNameAQuery(@Param("name") String name,@Param("master") long u);
    Page<Sender> findSenderByNameContainingIgnoreCaseAndMasterUserId(String q,Long id, Pageable pageable);
}
