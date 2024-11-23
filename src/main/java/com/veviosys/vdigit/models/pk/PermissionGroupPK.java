package com.veviosys.vdigit.models.pk;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class PermissionGroupPK implements Serializable{

    
    private static final long serialVersionUID = 1L;

    @Column(name = "group_id")
    Long groupId;
 
    
    @Column(name = "permission_id")
    Long permissionId;
    
    
}