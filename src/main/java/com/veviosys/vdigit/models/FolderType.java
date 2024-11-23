package com.veviosys.vdigit.models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.JoinColumn;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter@Setter
public class FolderType {
	
	@GeneratedValue(strategy =  GenerationType.AUTO)
	@Id private Long id;
	
	@JsonIgnore
	@ManyToMany(mappedBy = "folder_types")
	private List<User> users;
	@JsonIgnore
	@OneToMany(mappedBy = "type")
	private List<Folder> folders;
	
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="arbo")
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	@JsonIdentityReference(alwaysAsId = true)
	private arborescence arbo;
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="master")
	private User master;
	
	private int selected;
	
	
	private int premierNiveau;

	private Long seq;
	private int seqIsActive;
	private String orderRef;
	private String separatorRef;
	private String configText;
	private String cat;
}
