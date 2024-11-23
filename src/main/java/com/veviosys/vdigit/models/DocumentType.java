package com.veviosys.vdigit.models;

import java.util.ArrayList;
import java.util.List;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DocumentType {

	
	@Id 
	@GeneratedValue(strategy =  GenerationType.AUTO)
	private Long id;
	// @ManyToOne
	// @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	// @JsonIdentityReference(alwaysAsId = true)
	// @JoinColumn(name = "category_id")
	// private Category category;
	@Column(length = 150)
	
	private int selected;
	private int	isVersionable;
	private  String name;
	private  String libelle;
	@JsonIgnore
	@OneToMany(mappedBy = "type",  orphanRemoval = true,cascade = CascadeType.PERSIST,fetch = FetchType.LAZY)
	private List<Document> documents = new ArrayList<Document>() ;
	@JsonIgnore
	@ManyToMany(mappedBy = "docs_type",fetch = FetchType.LAZY)
	private List<User> users;
	@JsonIgnore
	@ManyToMany(mappedBy = "document_types")
	private List<Attribute> attributes;
	

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="master")
	private User master;

	@JsonIgnore
	@OneToMany(mappedBy = "documentType",fetch = FetchType.LAZY)
   private List<PermissionDocumentType>permissionDocumentTypes ;
	
    @JsonIgnore
    @ManyToOne
    @JoinColumn()
    ElementTypeGroup group;
}
