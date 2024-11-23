package com.veviosys.vdigit.repositories;

import java.awt.print.Pageable;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import com.veviosys.vdigit.models.UserTemplates;

public interface UserTemplateRepository extends JpaRepository<UserTemplates, UUID> {

Page<UserTemplates> findByUserIdAndNameContainingIgnoreCaseOrderByCreationDateDesc(Long userId,String q, org.springframework.data.domain.Pageable page);

}
