package com.veviosys.vdigit.models;

import java.util.Date;
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

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class ClientDoc {
    	
	@GeneratedValue(strategy =  GenerationType.AUTO)
	@Id private Long id;
	
private String email;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "emetteur_id")
private Sender emetteur_id;

private String message;
private String nom;
private String prenom;
private String num;
private String titre;
private String objet;
private Date sentDate;
    private Long master;
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "clientDoc")
    // @JsonIgnore
    private List<Document> documents ;
}
