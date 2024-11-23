package com.veviosys.vdigit.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.veviosys.vdigit.controllers.PermissionCourrier;
import com.veviosys.vdigit.models.Groupe;
import com.veviosys.vdigit.models.PermissionGroup;
import com.veviosys.vdigit.models.User;

public interface GroupRepo extends JpaRepository<Groupe,Long>{

	@Transactional 
	@Modifying
	@Query(value="insert into user_group values(:idUs,:idgrp)",nativeQuery = true)
	void	addUsersGroup(@Param("idgrp") Long idGrp,@Param("idUs")Long idUs);

	@Transactional 
	@Modifying
	@Query(value="delete from user_group where user_id=:idUs and group_id=:idgrp",nativeQuery = true)
	void	removeUsersGroup(@Param("idgrp") Long idGrp,@Param("idUs")Long idUs);
	List<Groupe> findByMaster(User master);

	List<Groupe>findGroupeByUsersUserId(Long id);
 
	List<Groupe>  findByGroupNameContainingIgnoreCaseAndMaster(String q,User master);
}
