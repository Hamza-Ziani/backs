package com.veviosys.vdigit.classes;

import lombok.Data;

@Data
public class ArchiveCourrier {

    private String id;
    private String objet;
    private String status;
    private String date;
    private String ref;
    private String Type;
    private String destinateur;
    private String emetteur;
    private String folderId;
    private String TypeAttribut;
    private String TypeFamilles;
}
