package com.veviosys.vdigit.bulkadd.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.veviosys.vdigit.bulkadd.model.Lot;

public interface LotRepository extends JpaRepository<Lot, Long> {

	
	Page<Lot> findBySubmitedTrue(Pageable page);
}
