package com.veviosys.vdigit.repositories;

import com.veviosys.vdigit.models.ClientDoc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientDocRepo extends JpaRepository<ClientDoc, Long> {


   Page<ClientDoc> findByMasterOrderBySentDateDesc(Pageable pageabe,Long master);
}
