package com.veviosys.vdigit.sharedfolders.models;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.veviosys.vdigit.models.RecieveMessagePK;
import com.veviosys.vdigit.models.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SharedWith {
	   @JsonIgnore
	    @EmbeddedId
	 private   RecieveMessagePK id;
	   @JsonIgnore
	    @ManyToOne
	    @MapsId("message_id")
	    @JoinColumn(name = "message_id")
	   private SharedFolder message;
	   
	    @ManyToOne
	    @MapsId("user_id")
	    @JoinColumn(name = "user_id")
	 private   User user;
	 
	  private  int seen;
}
