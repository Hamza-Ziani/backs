package com.veviosys.vdigit.models;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;




@Entity
@Data
public class MasterConfig {

	@Id
	@GeneratedValue(strategy =  GenerationType.AUTO)
	private Long id;
	
	
	@JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "master")
	private User master;
    
	private String configName;
	
	private String configValue;
	
	private int hasAccess;
	
	
}
