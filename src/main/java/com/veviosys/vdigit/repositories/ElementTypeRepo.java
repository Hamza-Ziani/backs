package com.veviosys.vdigit.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.veviosys.vdigit.models.Element;
import com.veviosys.vdigit.models.TypeElement;
import org.springframework.data.jpa.repository.Query;

public interface ElementTypeRepo  extends JpaRepository<TypeElement, Long>{


   
 TypeElement findByMasterAndIdTypeElementNotLike(Long idParent, Long idNew);
    TypeElement getTypeElementByMaster(Long master);

    List<TypeElement> getTypeElementsByIdTypeElementLessThanOrderByIdTypeElementAsc(Long idType);

//    @Query(value = "select * from type_element te ORDER BY te.id_type_element DESC limit 2", nativeQuery = true)
//    List<TypeElement> getLastType();
//
//    @Query(value = "select * from type_element te ORDER BY te.id_type_element asc limit ?1", nativeQuery = true)
//    List<TypeElement> getFirstType(Long lim);
//
//    @Query(value = "select * from type_element te ORDER BY te.id_type_element DESC limit ?1", nativeQuery = true)
//    List<TypeElement> getLastType(Long lim);




    List<TypeElement> findTypeElementByMasterOrderByIdTypeElementAsc(Long id,Pageable pageable);
    List<TypeElement> findTypeElementByMasterOrderByIdTypeElementDesc(Long id,Pageable pageable);
//   List<TypeElement> findTypeElementOrderByIdTypeElementDesc(Pageable pageable);

    @Query(value = "select * from type_element te ORDER BY te.id_type_element DESC", nativeQuery = true)
    List<TypeElement> getLastType(Long lim, Pageable pageable);

    @Query(value = "select * from type_element te ORDER BY te.id_type_element asc", nativeQuery = true)
    List<TypeElement> getFirstType(Long lim, Pageable pageable);

   TypeElement getTypeElementByIntituleTypeElement(String nom);
     
}
