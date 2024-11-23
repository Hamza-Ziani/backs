package com.veviosys.vdigit.classes;

import lombok.Data;

@Data
public class ArchiveClass {

    private String type;
    private String statut;
    private String reference;
    private String objet;
    private String dateDebut;
    private String dateFin;
    private String emetteur;
    private String dest;
    private String order;
}
