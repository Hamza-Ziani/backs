package com.veviosys.vdigit.repositories;

import com.veviosys.vdigit.controllers.PermissionCourrier;
import com.veviosys.vdigit.models.PermissionNatureCourrier;
import com.veviosys.vdigit.models.pk.PermissionNatureCourrierPK;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionNatureRepo  extends JpaRepository<PermissionNatureCourrier,PermissionNatureCourrierPK>{
    
}
