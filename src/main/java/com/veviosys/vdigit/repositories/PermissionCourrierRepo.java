package com.veviosys.vdigit.repositories;

import java.util.List;



import com.veviosys.vdigit.controllers.PermissionCourrier;
import com.veviosys.vdigit.models.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionCourrierRepo  extends JpaRepository<PermissionCourrier,Long> {
    List<PermissionCourrier> findByMaster(User master);
  Page<PermissionCourrier> findByMaster(User master,Pageable pageable);

  Page<PermissionCourrier>  findByNomContainingIgnoreCaseAndMaster(String q,User master,Pageable pageable);
}
