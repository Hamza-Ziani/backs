package com.veviosys.vdigit.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.veviosys.vdigit.models.Element;
import org.springframework.data.jpa.repository.Query;

public interface ElementRepository extends JpaRepository<Element,UUID> {

    @Query(value = "select * from element e where e.id_espace_versement_ = ?1 and e.is_ronger = 0 and e.master = ?2", nativeQuery = true)//removed idUser!!!!!
    Page<Element> getElmByEspVers(Long idEsp, Long idUser, Pageable pageable);

    List<Element> findElementByNomElement(String name);

    List<Element> getElementsByIdParent(UUID idParent);


    @Query(value = "select e.* from element_ e join type_element te on e.id_type_element = te.id_type_element and e.id_parent = ?1 and e.id_type_element = ?2", nativeQuery = true)
    List<Element> getRechHierData(String idParent, Long idType);

    List<Element> findElementByMaster(Long id_master);
  
  List<Element> getElementsByTypeElementIdTypeElement(Long idType);

}
