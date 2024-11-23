package com.veviosys.vdigit.models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class CloneEtape {
    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private Long id;
    private String name;
    @Column(length=800)
    private String commentaire;
    private int etat;
    private int numero;
    private int signataire;
    private int delaiRet;
    private String suppName;
    //private String Quality;
    private int late_relance = 0 ;
   // @Nullable
    private int isLast;
    private Date dateDebut;
    private Date dateTraitement;
    private Date dateFin;
    private int isBack;
    private int delai;
    private Long passed;
    private int statusPassed;
    private int returnBo;
    private String quality;
    @ManyToMany(mappedBy = "gestionetape")
    private List<User> users;

    @ManyToOne
    @JsonIgnoreProperties("etapes")
    @JoinColumn(name = "courrier")
    private Folder courrier;

    @ManyToOne
    @JoinColumn(name = "traitant")
    private User traitant;
    @Column(length=800)
    private String motifDeRetour;

    @OneToMany(mappedBy = "etape")
    private List<EtapeDetail> details;
}