package com.veviosys.vdigit.models;

import javax.mail.Message;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RecieveMessage {
    
   @JsonIgnore
    @EmbeddedId
 private   RecieveMessagePK id;
   @JsonIgnore
    @ManyToOne
    @MapsId("message_id")
    @JoinColumn(name = "message_id")
   private InterneMessage message;
   
    @ManyToOne
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
 private   User user;
 
  private  int seen;
}