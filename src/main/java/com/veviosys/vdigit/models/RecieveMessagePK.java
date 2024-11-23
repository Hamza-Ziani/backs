package com.veviosys.vdigit.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class RecieveMessagePK implements Serializable {
    

    
    private static final long serialVersionUID = 1L;

    @Column(name = "message_id")
    Long messsageId;
 
    @Column(name = "user_id")
    Long userId;
}