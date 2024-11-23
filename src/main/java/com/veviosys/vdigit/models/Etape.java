package com.veviosys.vdigit.models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Etape {

	
	 @Id
	    @GeneratedValue(strategy =  GenerationType.AUTO)
	    private Long id;
	    private String name;
	    private String commentaire;
	    private String quality;
		private int delai;
		private int delaiRet;
	    private int etat;
	    private int numero;
	    private int signataire;
	     
		@JsonIgnore 
		@ManyToOne
		@JoinColumn(name="processus")
		private Processus processus;
		@ManyToMany(mappedBy = "etapes")
	    private List<User> users;

	    @JsonIgnore
	    @ManyToOne
	    @JoinColumn(name = "master")
	    private User master;
}
