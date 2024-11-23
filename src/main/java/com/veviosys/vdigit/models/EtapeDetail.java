package com.veviosys.vdigit.models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.print.DocFlavor.STRING;

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
public class EtapeDetail {
    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private Long id;
    @Column(length=800)
    private String motif;
    @Column(length=800)
    private String instruction;
    private Date dateRet;
    private Date dateAvance;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "etape")
    private CloneEtape etape;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")

    private User user;
    private String suppName;

}