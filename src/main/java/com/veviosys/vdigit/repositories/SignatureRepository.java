package com.veviosys.vdigit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veviosys.vdigit.models.Signature;

public interface SignatureRepository extends JpaRepository<Signature, Long> {

	
	Signature findByUserUserId(Long Id);
}
