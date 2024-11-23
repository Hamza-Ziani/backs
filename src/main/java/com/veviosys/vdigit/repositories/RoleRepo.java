package com.veviosys.vdigit.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veviosys.vdigit.models.Role;

public interface RoleRepo extends JpaRepository<Role,Long>{
	
	Set<Role>findByRole(String role);

}
