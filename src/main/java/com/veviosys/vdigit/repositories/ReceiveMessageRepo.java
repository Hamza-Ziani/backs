package com.veviosys.vdigit.repositories;

import com.veviosys.vdigit.models.RecieveMessage;
import com.veviosys.vdigit.models.RecieveMessagePK;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiveMessageRepo extends JpaRepository<RecieveMessage,RecieveMessagePK> {
    
}