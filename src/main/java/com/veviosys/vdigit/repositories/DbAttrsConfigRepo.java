package com.veviosys.vdigit.repositories;

import com.veviosys.vdigit.models.DbAttrsConfig;
import com.veviosys.vdigit.models.User;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
@Transactional
public interface DbAttrsConfigRepo  extends JpaRepository<DbAttrsConfig,Long> {
	
    Page<DbAttrsConfig>findByMaster(User master,Pageable pageable);


   
}
