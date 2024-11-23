package com.veviosys.vdigit.repositories;

import java.util.Date;
import java.util.List;

import com.veviosys.vdigit.models.Journal;
import com.veviosys.vdigit.models.User;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JournalRepo extends JpaRepository<Journal,Long> , JpaSpecificationExecutor<Journal> {
    
    List<Journal>findByMaster(User master);

    @Query(value = " select * from journal j where j.master=:id "+
    " and  j.action_date BETWEEN :dd AND :df    "
    ,nativeQuery = true)
    List<Journal>findByDateMaster(@Param("id")Long id,@Param("dd")Date dd,@Param("df")Date df);
    // ,org.springframework.data.domain.Pageable pageable);
    
    void deleteByUser(User user);
    
}
