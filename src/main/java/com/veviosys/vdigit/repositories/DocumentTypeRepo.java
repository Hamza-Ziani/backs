package com.veviosys.vdigit.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.veviosys.vdigit.classes.DocTypeAttrRequiredClass;
import com.veviosys.vdigit.models.Category;
import com.veviosys.vdigit.models.DocumentType;
import com.veviosys.vdigit.models.PermissionDocument;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.models.receiver;

public interface DocumentTypeRepo extends JpaRepository<DocumentType, Long> {
	
	// List<DocumentType> findByCategory(Category category);
		// List<DocumentType> findByCategory(Category category);
	
    @Query(value = "SELECT dty.* from document_type dty,doc_type_user dtu where dtu.doc_type_id=dty.id and dtu.user_id=?2 and id in (select distinct pdt.doctype_id from permission_document pd, permission_document_type pdt where pdt.permission_id=pd.id and pd.acces like %?1% and pd.id in (select pg.permission_id from groupe g , user_group ug,permission_group pg where g.group_id=ug.group_id and pg.group_id=ug.group_id and ug.user_id=?3))", nativeQuery = true)
    List<DocumentType>getDocAccessList(String access, Long master,Long userId);

    
		@Query(value="select dt.* from  document_type dt , doc_type_user dtu where dt.id=dtu.doc_type_id"
		+ " and dtu.user_id=:idU and dt.master=:idM",nativeQuery = true)
List<DocumentType>findByUser(@Param("idU")Long idU,@Param("idM")Long idM);
		
		@Query(value="select dt.* from  document_type dt , doc_type_user dtu where dt.id=dtu.doc_type_id"
		        + " and dtu.user_id=:idU and dt.master=:idM and dt.libelle like %:query%",nativeQuery = true)
		List<DocumentType> findByNameContainingIgnoreCase(@Param("idU")Long idU,@Param("idM")Long idM,@Param("query") String l );
		
 @Query(value="select distinct dt.* from UTILISATEUR u , document_type dt where dt.master=:idM and dt.id not in"
			+ " (select dtu.doc_type_id from doc_type_user dtu where dtu.user_id=:idU)",nativeQuery = true)
	List<DocumentType>findOthers(@Param("idU")Long idU,@Param("idM")Long idM);
 
 @Query(value="select distinct dt.* from UTILISATEUR u , document_type dt where dt.master=:idM and dt.id not in"
         + " (select dtu.doc_type_id from doc_type_user dtu where dtu.user_id=:idU) and dt.libelle like %:query%",nativeQuery = true)
 List<DocumentType> findByNameContainingIgnoreCaseOthers(@Param("idU")Long idU,@Param("idM")Long idM,@Param("query") String l);
 
 
  @Transactional
	@Modifying
	@Query(value="insert into doc_type_user (user_id, doc_type_id) values (:idUser,:idDoctype)",nativeQuery = true)
	void addType(@Param("idUser") Long idUs,@Param("idDoctype")Long idM);
  
  @Transactional 
  @Modifying
  @Query(value="delete from doc_type_user where user_id=:idUser and doc_type_id=:idDoc",nativeQuery = true)
	 void delType(@Param("idUser") Long idUs,@Param("idDoc")Long idM);
  
  @Transactional
  @Modifying
  @Query(value="insert into type_document_attribute(attribute_id, document_type_id,required,rep,visib) values (:idAttr,:idDocType,:req,:rep,:visib)",nativeQuery=true)
  void typeAttr(@Param("idDocType")Long idDT,@Param("idAttr")Long idAt,@Param("req") Integer required,@Param("rep") Integer rep,@Param("visib") Integer visib);
  @Transactional
  @Modifying
  @Query(value="update type_document_attribute set required=:req,rep=:rep,visib=:visib where attribute_id=:idAttr and document_type_id= :idDocType",nativeQuery=true)
  void editTypeAttr(@Param("idDocType")Long idDT,@Param("idAttr")Long idAt,@Param("req") Integer required,@Param("rep") Integer rep,@Param("visib") Integer visib);
  
  DocumentType findByNameAndMaster(String name,User u); 
  
  @Query(value = "SELECT dt.* FROM document_type dt WHERE dt.id=:id",nativeQuery = true)
  DocumentType findId(@Param("id")Long id);
  
  @Transactional
  @Modifying
  @Query(value=" delete from type_document_attribute where document_type_id=:id",nativeQuery = true)
  void deleteDocumentType(@Param("id")Long id);

  
  @Query(value = "SELECT * FROM type_document_attribute dt WHERE document_type_id=:id order by attribute_id asc",nativeQuery = true)
 List<Object> finDocTypeAttrRequiredClasses(@Param("id")Long id);
 List<DocumentType> findBypermissionDocumentTypes_PermissionDocumentId(Long permissionDocument);

 @Query(value = "select dt.* from  document_type dt , doc_type_user dtu where dt.id=dtu.doc_type_id"
		 + " and dtu.user_id=:idU and dt.master=:idM  and dt.group_goup_id is NULL", nativeQuery = true)
		 List<DocumentType> findByUserNonGroup(@Param("idU") Long idU, @Param("idM") Long idM);

}
