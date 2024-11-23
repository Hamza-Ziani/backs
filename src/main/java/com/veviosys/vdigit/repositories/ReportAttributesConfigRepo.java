package com.veviosys.vdigit.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veviosys.vdigit.models.ReportAttributesConfig;
 

public interface ReportAttributesConfigRepo extends JpaRepository<ReportAttributesConfig,Long> {

	List <ReportAttributesConfig> findByMaster(Long master);
	List<ReportAttributesConfig> findByActiveAndMaster(Integer active,Long master);
	 
}
