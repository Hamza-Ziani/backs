package com.veviosys.vdigit.reposietories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veviosys.vdigit.models.AttributeType;

public interface AttributeTypeRepo extends JpaRepository<AttributeType, Long> {

	AttributeType findByName(String name);
}
