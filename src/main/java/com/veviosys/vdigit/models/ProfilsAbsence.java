package com.veviosys.vdigit.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
public class ProfilsAbsence {
    @Id
	@GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    
    private String dateDebut;
    private String dateFin;
    @JsonIgnore	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
    private User user;
    @JsonIgnore	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "supleant")
    private User supleant;
    private String userName;
    private String supName;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master")
    private User master;
	
	
    
}