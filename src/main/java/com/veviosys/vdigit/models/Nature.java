package com.veviosys.vdigit.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Nature {
	@Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private Long id;
	private String name;
	private String procName;
	@JsonIgnore
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userId")
	@JsonIdentityReference(alwaysAsId = true)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "master")
	private User master;
	@JsonIgnore
	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "process")
	private Processus process;
	@JsonIgnore
	@OneToMany(mappedBy = "nature",  orphanRemoval = true,cascade = CascadeType.PERSIST,fetch = FetchType.LAZY)
	private List<Folder> folders = new ArrayList<Folder>() ;

    private String folderType;
	@JsonIgnore
	@OneToMany(mappedBy = "nature",fetch = FetchType.LAZY)
   private List<PermissionNatureCourrier>permissionNatureCourriers ;
	private String libelle;
	
}
