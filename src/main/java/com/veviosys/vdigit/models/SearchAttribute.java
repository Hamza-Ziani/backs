package com.veviosys.vdigit.models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SearchAttribute {
	
	@Id @GeneratedValue(strategy =  GenerationType.AUTO)
	private Long id;
	
	private String name;
	
	@JsonIgnore
	@OneToMany(mappedBy = "attribute")
	private List<SearchAttributeValue>  attributes;
	
	

}
