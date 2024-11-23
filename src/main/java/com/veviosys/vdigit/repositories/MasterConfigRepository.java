package com.veviosys.vdigit.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veviosys.vdigit.models.MasterConfig;

public interface MasterConfigRepository extends JpaRepository<MasterConfig, Long> {

	List<MasterConfig> findByMasterUserId(Long userId);
	
	List<MasterConfig> findByMasterUserIdAndHasAccess(Long userId,int access);

	MasterConfig findByMasterUserIdAndConfigName(Long userId, String string);

	List<MasterConfig> findByConfigName(String configName);
}
