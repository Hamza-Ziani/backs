package com.veviosys.vdigit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veviosys.vdigit.models.Certaficate;



public interface CertificateRepository extends JpaRepository<Certaficate, Long> {

	Certaficate findByUserUserId(Long userId);

	
	
}
