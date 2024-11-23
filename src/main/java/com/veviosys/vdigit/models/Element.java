package com.veviosys.vdigit.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.*;
import javax.persistence.Entity;

import org.hibernate.HibernateException;
import org.hibernate.annotations.GenericGenerator;

import com.veviosys.vdigit.classes.ElementDraft;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentityGenerator;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "element_")
public class Element {

    @org.hibernate.annotations.Type(type="uuid-char")
    @Id
    @GeneratedValue(generator="MYuuid")
    @GenericGenerator(name="MYuuid", strategy = "com.veviosys.vdigit.models.generator.Manuleinsertgenerator")
//    @GeneratedValue(strategy = GenerationType.IDENTITY, generator="system-uuid")
//    @GenericGenerator(name="system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID idElement;
     
    private String nomElement;
    private boolean isLast;
    private Long master;

    public boolean getis(){
        return isLast;
    }

    public Element() {
    }

    public Element(ElementDraft draft) {


    this.nomElement = draft.getNomElement();
    if (draft.getIsLast() != null)
        this.isLast = draft.getIsLast().equals("1");

//    this.idParent = draft.getIdParent().toString().equalsIgnoreCase("");

        this.idParent = Objects.nonNull(draft.getIdParent()) ? draft.getIdParent() : null;


    }

    @org.hibernate.annotations.Type(type="uuid-char")
    private UUID idParent;

    @ManyToOne
    @JoinColumn(name = "idTypeElement")
    private TypeElement typeElement; 

     

    @Column(columnDefinition = "integer default 0")
    private Integer isVerser; 
    @Column(columnDefinition = "integer default 0")
    private Integer isRonger; 

    private LocalDate dateCreation;
    private int isReserved ;
    @Column(columnDefinition = "varchar(255) default 'none'")
    private String lastPlacement;


}

