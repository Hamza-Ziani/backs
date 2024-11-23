package com.veviosys.vdigit.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.veviosys.vdigit.models.FrequencySearch;
import com.veviosys.vdigit.models.User;

public interface FrequencySearchRepo extends JpaRepository<FrequencySearch, Long> {

    List<FrequencySearch> findByUser(User user);
    
    

    
    
    Page<FrequencySearch> findByUserUserId(Long userId, Pageable pageable);
    
    
    @Query("SELECT COUNT(s) from FrequencySearch s where s.user=?1 and LOWER(s.name)=LOWER(?2)")
    Long searchCount(User user,String name);
	
	

}
