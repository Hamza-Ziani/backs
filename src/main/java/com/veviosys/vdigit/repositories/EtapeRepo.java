package com.veviosys.vdigit.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.veviosys.vdigit.models.Etape;

public interface EtapeRepo extends JpaRepository<Etape,Long>{
	@Transactional
	@Modifying
	@Query(value = "INSERT INTO user_etape (user_id, etape_id) VALUES (:id1, :id2)",nativeQuery = true)
	void addUserEtape(@Param("id1") Long idUs,@Param("id2")Long idEt);
	
	@Transactional
	@Modifying
	@Query(value = "delete from user_etape where etape_id=:id2",nativeQuery = true)
	void deleteUserEtape(@Param("id2")Long idEt);
	List<Etape>	findEtapeByProcessusIdOrderByNumeroAsc(Long id);
	
	List<Etape> findByQuality(String quality);
	
}
