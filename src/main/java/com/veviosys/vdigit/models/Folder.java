package com.veviosys.vdigit.models;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Folder {

	@org.hibernate.annotations.Type(type = "uuid-char")
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
	private UUID id;
	private String natureName;
	private String processName;

	
    private String emet__; 
    private String dest__; 
    
	private String emetteur;
	private String favoriteBay;
	private String typeName;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "folder_type")
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	@JsonIdentityReference(alwaysAsId = true)
	private FolderType type;
	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name="arbo")
	// @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
	// property = "id")
	// @JsonIdentityReference(alwaysAsId = true)
	// private arborescence arbo;
	// @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
	// property = "id")
	// @JsonIdentityReference(alwaysAsId = true)
	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name="client_id")
	// private Client client;

	@JsonIgnoreProperties("owned_folders")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id")
	private User owner;

	// @JsonIgnore
	// @ManyToMany(mappedBy = "favorite_folders")
	// private List<User> users;

	//
	private String reference;
	private String finalise;
	private String addedAcces;
	@Column(name = "numero")
	private Long number;
	@Column(name = "folderDate")
	private String date;
	@CreatedDate
	private Date creation_date;
	
    private String dateReception;
	@LastModifiedDate
	private Date last_edit_date;

	private Boolean is_deleted;
	
	private Boolean isArchived;
	// @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
	// property = "id")
	// @JsonIdentityReference(alwaysAsId = true)
	// @ManyToOne
	// private Folder parent_folder;
	private Integer accuse;
	@JsonIgnore
	@OneToMany(mappedBy = "folder",fetch = FetchType.LAZY)
	
	private List<DocumentFolder> documents;

	/*
	 * Relation
	 * 
	 */
	@JsonIgnore
	@OneToMany(mappedBy = "parent",fetch = FetchType.LAZY)
	private List<FoldersRelations> parents;
	
	@JsonIgnore
    @OneToMany(mappedBy = "folder",fetch = FetchType.LAZY)
    private List<Track> trackedEmails;
	
	
	@ManyToMany(fetch = FetchType.EAGER)
	private List<receiver> dest ;
	
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emetteur_id")
    private Sender emetteur_id;
	
	
	@JsonIgnore
	@OneToMany(mappedBy = "child",fetch = FetchType.LAZY)
	private List<FoldersRelations> childs;

	private String receiver;
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nature")
	private Nature nature;
	@JsonIgnoreProperties("courrier")
	@OneToMany(mappedBy = "courrier",fetch = FetchType.LAZY)
	private List<CloneEtape> etapes;

	@Lob
	private String objet;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	private User master;
	@Column(nullable = true)
	private Integer field1;
	@Column(nullable = true)
	private String field2;
	@Column(nullable = true)
	private String field3;
	@Column(nullable = true)
	private String field4;
	private int hasProcess;
	private String autoRef;
	// @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  
	private String userDest;
	private String userSender;
	@Column(nullable = true)

	private int hasBo;

	private String pathBo;

	private String archiveParent;
	public Long getNatureId(){
if(Objects.nonNull(nature))
   return nature.getId();
   return null;

	}
	
	public String findValueByName( HashMap<String,String> parameters ,String attr) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, InvocationTargetException, NoSuchMethodException {
 	 

        //PropertyUtils.setSimpleProperty(this, field.getName(), "A VALUE");
       
  	/*	for(Field field : c.getDeclaredFields()) {
    	      

    	      // System.out.println(field.getName());
    		   if(field.getName().equals(attr)) {
    			return   (String)field.get(field);
    			  
    		   }
    	   } */ 

 
		Object c;
		String data="s";
if(attr.equals("Emetteur/Destinataire(s)")) {
//	  data=Objects.isNull(PropertyUtils.getSimpleProperty(this,"emet__"))?PropertyUtils.getSimpleProperty(this,"dest__").toString().replaceAll(","," "):
//		PropertyUtils.getSimpleProperty(this,"emet__").toString();
	     
        if(Objects.nonNull(this.dest)) {
              List<String> names = new ArrayList();
              
              for (receiver dest : this.dest) {
                  names.add(dest.getName());
              }
              
              data = names.stream()
                      .map(n -> String.valueOf(n))
                      .collect(Collectors.joining(","));
        }
        if(Objects.nonNull(this.emet__)){
             data = this.emet__;
 		}
}else {
	 c=PropertyUtils.getSimpleProperty(this,parameters.get(attr));
	 data=Objects.nonNull(c)?c.toString():"";
}
 		
 		 
  		return data;
	}
}
