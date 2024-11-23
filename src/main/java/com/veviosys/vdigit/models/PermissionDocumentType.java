package com.veviosys.vdigit.models;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.veviosys.vdigit.models.pk.PermissionDocumentTypePK;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PermissionDocumentType {
    @JsonIgnore
    @EmbeddedId
 private  PermissionDocumentTypePK id;

  
    @ManyToOne
    @MapsId("permission_id")
    
    @JoinColumn(name = "permission_id")
   private PermissionDocument permissionDocument;
   
    @ManyToOne
    @MapsId("doctype_id")
    @JoinColumn(name = "doctype_id")
 private  DocumentType documentType;
 
   
}