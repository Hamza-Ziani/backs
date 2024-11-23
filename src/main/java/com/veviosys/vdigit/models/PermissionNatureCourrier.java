package com.veviosys.vdigit.models;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.veviosys.vdigit.controllers.PermissionCourrier;
import com.veviosys.vdigit.models.pk.PermissionNatureCourrierPK;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PermissionNatureCourrier {
    @JsonIgnore
    @EmbeddedId
 private  PermissionNatureCourrierPK id;

  
    @ManyToOne
    @MapsId("permissionId")
    
    @JoinColumn(name = "permissionCourrier")
   private PermissionCourrier permissionCourrier;
   
    @ManyToOne
    @MapsId("natureId")
    @JoinColumn(name = "nature_id")
 private  Nature nature;
}
