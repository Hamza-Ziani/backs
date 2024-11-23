package com.veviosys.vdigit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.veviosys.vdigit.models.Groupe;
import com.veviosys.vdigit.models.PermissionGroup;
import com.veviosys.vdigit.models.pk.PermissionGroupPK;

public interface PermissionGroupRepo extends JpaRepository<PermissionGroup, PermissionGroupPK>{

  List<PermissionGroup>  findByGroup(Groupe groupe);
}
