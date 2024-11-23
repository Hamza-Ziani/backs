package com.veviosys.vdigit.reposietories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import com.veviosys.vdigit.models.Attribute;
import com.veviosys.vdigit.models.User;

public interface AttributeRepo extends JpaRepository<Attribute, Long> {

	@Query(value="select a.id from attribute a where a.name=:name and a.master=:id",nativeQuery = true)
	Long findIdbyName(@Param("name")String name,@Param("id")Long id);
List<Attribute>findByMasterOrderByIdAsc(User master);
	Attribute findByNameAndMaster(String name,User u);
Page<Attribute>findByMasterOrderByIdAsc(User master,Pageable pageable);
  List<Attribute> findByConfigId(Long id);
  Page<Attribute> findByNameContainingIgnoreCaseAndMasterOrderByIdAsc(String q,User master,Pageable pageable);
  Attribute findByMasterAndName(User master,String name);
}
