package com.veviosys.vdigit.controllers;

import java.util.List;

import javax.persistence.Entity;
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
import com.veviosys.vdigit.models.PermissionGroupN;
import com.veviosys.vdigit.models.PermissionNatureCourrier;
import com.veviosys.vdigit.models.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PermissionCourrier {
    @Id 
	@GeneratedValue(strategy =  GenerationType.AUTO)
	private Long id;
    private String nom;
    private String Description;
    private String acces;
    @JsonIgnore   
	@OneToMany(mappedBy = "permissionCourrier")
   private List<PermissionNatureCourrier>permissionNature;	
    @JsonIgnore   
    @OneToMany(mappedBy = "permissionCourrier")
    private List<PermissionGroupN>permissionGroupNature;
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userId")
	@JsonIdentityReference(alwaysAsId = true)
	@ManyToOne
	@JoinColumn(name = "master")
	private User master;
}
