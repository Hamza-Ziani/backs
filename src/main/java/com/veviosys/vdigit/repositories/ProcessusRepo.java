package com.veviosys.vdigit.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import com.veviosys.vdigit.models.Etape;
import com.veviosys.vdigit.models.Nature;
import com.veviosys.vdigit.models.Processus;
import com.veviosys.vdigit.models.User;

public interface ProcessusRepo extends JpaRepository<Processus, Long>{
    Page<Processus>findByMasterUserIdAndParentIsNull(Long master,Pageable p);
    List<Processus> findByMasterUserIdAndParentIsNull(Long master);
    @Query(value = "select e.id from etape e,processus p where p.id=e.processus and p.id=:id ",nativeQuery = true)
    List<Long>findByPro(@Param("id") Long id);
    Processus findProcessusByNaturesId(Long id);
    Page<Processus>findByParent(Long parent,Pageable p);
    List<Processus>findByParent(Long parent); 
}
