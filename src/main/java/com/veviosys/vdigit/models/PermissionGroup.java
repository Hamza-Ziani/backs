package com.veviosys.vdigit.models;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.veviosys.vdigit.controllers.PermissionCourrier;
import com.veviosys.vdigit.models.pk.PermissionGroupPK;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PermissionGroup {
    

    @JsonIgnore
    @EmbeddedId
 private  PermissionGroupPK id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("permission_id")
    
    @JoinColumn(name = "permission_id")
   private PermissionDocument permissionDocument;
   
   @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("group_id")
    @JoinColumn(name = "group_id")
 private Groupe group;
 

}