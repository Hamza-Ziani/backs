package com.veviosys.vdigit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veviosys.vdigit.models.Seq;

public interface SeqRepo extends JpaRepository<Seq, Long> {

    
    Seq findByType(String type);
}
