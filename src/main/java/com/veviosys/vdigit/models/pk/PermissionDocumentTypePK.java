package com.veviosys.vdigit.models.pk;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class PermissionDocumentTypePK implements Serializable{

    
    private static final long serialVersionUID = 1L;

    @Column(name = "doctype_id")
    Long doctypeId;
 
    @Column(name = "permission_id")
    Long permissionId;
    
}