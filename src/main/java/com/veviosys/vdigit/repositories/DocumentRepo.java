package com.veviosys.vdigit.repositories;

import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.veviosys.vdigit.models.Document;

public interface DocumentRepo extends JpaRepository<Document, UUID>,JpaSpecificationExecutor<Document>  {


	@Modifying
	@Transactional
	@Query(value="UPDATE document SET owner=:idM where owner=:id",nativeQuery=true)
	void changeToMaster(@Param("idM")Long idM, @Param("id")Long id);

	@Query(value="SELECT d.file_name from Document where d.id=':id'",nativeQuery=true)
	String findFileNameById(@Param("id")UUID id);
	//SELECT count(*) FROM document_attribute_value as d,attribute as a,attribute_value as v WHERE d.attribute_id=a.id AND a.name='Reference' and d.value=v.id and v.value='xyz'
	@Query(value="SELECT count(*) FROM document as w ,document_attribute_value as d,attribute as a,attribute_value as v"
			+ " WHERE w.id=d.document_id and d.attribute_id=a.id and a.name=:attName and d.value=v.id and v.value=:val and w.master=:master",nativeQuery=true)
	Long existCountCheck(@Param("master")Long id,@Param("attName")String Attrname ,@Param("val")String ref);

	String findFilenameById(UUID id);
	
	@Query(value="select d.* from document d where d.doc_type=:idType and master=:idM",nativeQuery = true)
    List<Document>findByType(@Param("idType")Long idType,@Param("idM")Long idM);
     
	@Query("select d.contentType from Document d where d.id=?1")
     String findContentTypeById(UUID id);
	@Query(value="select * from document d where d.master=:idM and d.client_id  IS NOT NULL",nativeQuery=true)
	List<Document>findDocsClient(@Param("idM")Long idM);
	
	@Query(value="select count(*) from document d where d.master=:idM and d.client_id  IS NOT NULL",nativeQuery=true)
	int docsNumberByClients(@Param("idM")Long idM);
	List<Document> findByPathServerContaining(String pathServer);
	List<Document> findByIdIn(List<UUID> ids);
	
	@Transactional
    @Modifying
    @Query("update Document d set d.isArchived=true where d.id=:uuid ")
    void updateArchived(UUID uuid);
}
/*
 
 */