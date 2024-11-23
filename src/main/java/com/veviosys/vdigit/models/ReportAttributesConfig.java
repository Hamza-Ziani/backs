package com.veviosys.vdigit.models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReportAttributesConfig {
	@Id 
	@GeneratedValue(strategy =  GenerationType.AUTO)
	private Long id;
	private String name;
	private String libelle;
	private Integer active ;
	private Long master;
	
}
