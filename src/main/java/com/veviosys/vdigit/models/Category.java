package com.veviosys.vdigit.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category 
{
	@Id
	@GeneratedValue(strategy =  GenerationType.AUTO)
	private Long id;
	@Column(length = 100)
	private String name;
	@Column(length = 1000)
	private String description;
	
	// @OneToMany(mappedBy = "category")
	// private List<DocumentType> documents_type;

	@Column(nullable = true)
	private Integer field1;
	@Column(nullable = true)
	private String field2;
	@Column(nullable = true)
	private String field3;
	@Column(nullable = true)
	private String field4;
	 
	
	
	
}
