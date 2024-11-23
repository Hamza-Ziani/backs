package com.veviosys.vdigit.models;

import javax.annotation.Generated;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Data
public class Signature {

	
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	private  Long id;
	
	private String intitule;
	
	private String signGraphicPath;
	
	private String contentType;
	
	private String certificatePath ;
	
	private String certificatePW;
	
	private Boolean withText;
	
	@JsonIgnore
	@OneToOne()
	@JoinColumn(name = "user_id")
	private User user;
	
	
}
