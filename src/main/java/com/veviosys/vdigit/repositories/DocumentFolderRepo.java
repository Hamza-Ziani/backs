package com.veviosys.vdigit.repositories;

import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.veviosys.vdigit.models.DocumentFolder;
import com.veviosys.vdigit.models.pk.DocumentFolderPk;

public interface DocumentFolderRepo extends JpaRepository<DocumentFolder, DocumentFolderPk> ,JpaSpecificationExecutor<
DocumentFolder>{

	@Query(value = "select count(df) from DocumentFolder df where df.id= ?1")
	Long count(DocumentFolderPk id);

	/*
	 * @Query(value = "select f from DocumentFolder df inner join Folder f on f.id=df.id.folder_id"
			+ " where df.id.document_id= ?1")
	Page<Folder> findDocumentsByFolderId(UUID id, Pageable pageable);
	*/
	@Query(value = "select df from DocumentFolder df where df.id.folder_id = ?1")
   List<DocumentFolder> findDocumentsByFolderUUID(UUID id);
   @Query(value = "select df from DocumentFolder df where df.id.folder_id = :id")
   List<DocumentFolder> findDocsByFolderUUID(@Param("id") String id);
	 // @Query(value = "select new d.* from Document_Folder df join document d on df.document_id=d.id where df.folder_id=:id",nativeQuery = true)
	  //Page<Document> findDocumentsByFolderUUID(String id,Pageable pageable);

	@Query(value="select df.* from document_folder df,folder fd where  fd.id=df.folder_id and  df.document_id=:id",nativeQuery=true)
	List<DocumentFolder> findFoldersLink(@Param("id") String id);
	 // @Query(value = "select new d.* from Document_Folder df join document d on df.document_id=d.id where df.folder_id=:id",nativeQuery = true)
	  //Page<Document> findDocumentsByFolderUUID(String id,Pageable pageable);
	Page<DocumentFolder>findByDocumentId(String id,Pageable pageable);
	@Modifying
	@Transactional
	@Query(value = "delete from document_folder  where document_folder.document_id=:idDoc and document_folder.folder_id=:idFold",nativeQuery = true)
	void deleteDocsLink(@Param("idDoc")String idDoc,@Param("idFold")String idFold);

}