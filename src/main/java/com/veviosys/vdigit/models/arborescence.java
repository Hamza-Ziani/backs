package com.veviosys.vdigit.models;

import java.util.List;

import javax.persistence.Column;
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
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class arborescence {
	
	@GeneratedValue(strategy =  GenerationType.AUTO)
	@Id private Long id;
	@Column(length = 50)
	private String name;
	

	
	// @JsonIgnore
	// @OneToMany(mappedBy = "arbo")
	// private List<Folder> folders;
	
	@JsonIgnore
	@OneToMany(mappedBy = "arbo") 
	private List<FolderType> folderType;
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="master")
	private User master;
}
