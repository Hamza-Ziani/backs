package com.veviosys.vdigit.models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InterneMessage {

	
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	private Long id;
	
	private String message;
	
	@ManyToOne
	@JoinColumn(name = "sender_id") 
	
	private User sender;
	
	@OneToMany(mappedBy = "message")
	
    List<RecieveMessage> receivers;	
	
	@ManyToOne
	@JoinColumn(name = "document")
	
	private Document doc;
	private Date  sentDate ;


}
   