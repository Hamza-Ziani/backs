package com.veviosys.vdigit.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.veviosys.vdigit.models.FrequencySearch;
import com.veviosys.vdigit.models.SearchAttributeValue;
import com.veviosys.vdigit.models.pk.SearchAttributeValuePk;

public interface SearchAttrValRepo extends JpaRepository<SearchAttributeValue, SearchAttributeValuePk> {
	
	List<SearchAttributeValue> findBySearch(FrequencySearch search);
	Page<SearchAttributeValue> findBySearch(FrequencySearch search,Pageable page);
	
	
	@Transactional
	@Modifying
	@Query("DELETE FROM SearchAttributeValue sav WHERE sav.search.id=:id")
	int deleteBySearchId(@Param("id") long id );
	
}
