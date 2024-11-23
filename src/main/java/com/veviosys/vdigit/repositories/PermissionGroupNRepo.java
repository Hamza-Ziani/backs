package com.veviosys.vdigit.repositories;

import java.util.List;

import com.veviosys.vdigit.models.Groupe;
import com.veviosys.vdigit.models.PermissionGroupN;
import com.veviosys.vdigit.models.pk.PermissionGroupPK;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionGroupNRepo  extends JpaRepository<PermissionGroupN, PermissionGroupPK>{
    List<PermissionGroupN>  findByGroup(Groupe groupe);

}
