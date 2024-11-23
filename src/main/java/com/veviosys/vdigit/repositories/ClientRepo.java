package com.veviosys.vdigit.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.veviosys.vdigit.models.Client;

public interface ClientRepo extends JpaRepository<Client, Long>{

	
	// List<Client>  findByMasterUserId(Long id);
	 
	
	@Query(value = "SELECT * FROM client WHERE master_id=?1 ",nativeQuery = true)
	List<Client>findclientMaster(Long id);
	
	@Query(value = "SELECT * FROM client WHERE master_id=?1 and name=?2",nativeQuery = true)
	Client findByName(Long id,String name);
}
