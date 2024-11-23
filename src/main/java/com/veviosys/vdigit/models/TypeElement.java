package com.veviosys.vdigit.models;

 

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.veviosys.vdigit.classes.TypeDraft;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TypeElement {

    public TypeElement(TypeDraft d){

        intituleTypeElement = d.getNomTypeElement();

        setPicPath(d.getPicPath());


        setMaster(d.getIdParent());

    }


	
	@Id
	@GeneratedValue(strategy =  GenerationType.AUTO)
    private Long idTypeElement; 

    private String intituleTypeElement;
    private String picPath;
 
    @JsonIgnore
    @OneToMany(mappedBy = "typeElement", fetch = FetchType.LAZY)
    private List<Element> elements; 
 
    private Long master; 
    private String data64; 
}