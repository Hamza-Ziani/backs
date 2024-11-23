package com.veviosys.vdigit.models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Setter
public class Sender {
    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private Long id;
    private String name;
    private String email;
    private String Code;
    @JsonIgnore
	@ManyToOne
	@JoinColumn(name="master")
	private User master;
	
    @JsonIgnore
    @OneToMany(mappedBy = "emetteur_id",fetch = FetchType.LAZY)
    private List<Folder> folders;
    
    @JsonIgnore
    @OneToMany(mappedBy = "emetteur_id",fetch = FetchType.LAZY)
    private List<ClientDoc> clientDoc;
}
 