package com.veviosys.vdigit.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.models.arborescence;

public interface arborescenceRepository extends JpaRepository<arborescence,Long> {
	
	
 List<arborescence>findByMaster(User user);
 arborescence findByName(String name);
}
