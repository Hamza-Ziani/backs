package com.veviosys.vdigit.repositories;

import com.veviosys.vdigit.models.PermissionDocumentType;
import com.veviosys.vdigit.models.pk.PermissionDocumentTypePK;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionDocumentTypeRepo extends JpaRepository<PermissionDocumentType,PermissionDocumentTypePK>{
    
}