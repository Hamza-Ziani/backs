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
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.veviosys.vdigit.models.Folder;
import com.veviosys.vdigit.models.User;

public interface FolderRepo extends JpaRepository<Folder, UUID> , JpaSpecificationExecutor<Folder> {

	List<Folder> findFolderByMasterUserId(Long Id);
	
	
	Page<Folder> findFolderByMasterUserId(Long Id,Pageable page);
	
	// @Query(value = "select f from Folder f where f.id= ?1")
	Page<Folder> findByFavoriteBayOrFavoriteBayLikeOrFavoriteBayLikeOrFavoriteBayLike(String p1,String p2,String p3,String p4,Pageable page);

	@Query(value = "select f from Folder f where f.id= ?1")
	Folder findByID(UUID id);
	
	@Transactional
	@Modifying
	@Query(value = "insert into folder_document (document_id,folder_id) values (:document,:folder)", nativeQuery = true)
	void addDocument(UUID document, UUID folder);
	
	@Query(value = "select count(*) from folder_document where = :document and folder_id=:folder)", nativeQuery = true)
	Long countExist(UUID document, UUID folder);

	@Modifying
	@Transactional
	@Query(value="UPDATE folder SET owner_id=:idM where owner_id=:id",nativeQuery=true)
	void changeToMaster(@Param("idM")Long idM,@Param("id")Long id);

	List<Folder> findFoldersByOwner(User owner);
	/*
	@Query(value = "select f from Folder where f.parent_folder.id= ?1")
	Page<Folder> getFolderChildsById(UUID parentFolderId, Pageable pageable);
      */
	
	@Query(value="Select folder_id from folder_dest where dest_id In (?1)",nativeQuery=true)
	List<UUID> findFolderDest(List<Long> ids);
	
	@Query(value = "select count(f) from Folder f where f.master.userId= ?1 and Lower(f.reference)=lower(?2)")
	Long countOfRef(Long one, String reference);

	/*@Query(value="SELECT  f.*  from folder as f LEFT join "
			+ " folders_relations as fr on f.id=fr.child_id and fr.parent_id='1623cb8c-0c06-44fd-ba29-337ec0b1a818'",nativeQuery=true)
	Page<Folder> availableFoldersToLink(String id,Pageable page);
	
	 
	 select f , (case  when EXISTS(select 1 from FoldersRelations as fr WHERE fr.id.child_id=f.id and fr.parent_id ='1623cb8c-0c06-44fd-ba29-337ec0b1a818' ) THEN f.field1=1 ELSE 0 END)
	  
	  
	  */
	  
	//    @Query(value = "select f from Folder f,FoldersRelations fr where f.id=fr.id.child_id "
	//     		+ "and fr.id.parent_id = ?1")
	//    Page<Folder> findFolderChilds(UUID parentid,Pageable pageable);
	@Query(value = "select f from Folder f,FoldersRelations fr where f.id=fr.id.child_id "
	+ "and fr.id.parent_id = ?1")
List<Folder> findFoldersChilds(UUID parentid);
	   @Query(value = "select f from Folder f,FoldersRelations fr where f.id=fr.id.child_id "
	    		+ "and fr.id.parent_id = ?1 ")
	   List<Folder> findFolderChilds(UUID parentid);

	   @Query(value = "select f.* from folder f,folders_relations fr where f.id=fr.child_id "
	    		+ "and fr.parent_id = :parentid", nativeQuery= true)
		List<Folder> findLinkedFolders(@Param("parentid")String parentid);
	   
	   
	   //search folder linked folders with a document
	   @Query(value = "select f.* from folder f,document_folder df where f.id=df.folder_id and df.document_id=:docId", nativeQuery= true)
		List<Folder> findLinkedFoldersOfDocument(@Param("docId") String docId);
		@Query(value="SELECT f from Folder f where f.id=?1")
		Page<Object> test(UUID id ,Pageable page);
  
	  @Query(value = "SELECT `id`, `creation_date`, `date`, (case when EXISTS(SELECT * from document_folder WHERE document_id=df.document_id AND folder_id=df.folder_id) then 1 else null END) as field1 "
			  + ", `field2`, `field3`, `field4`, `is_deleted`, `last_edit_date`, `number`, `reference`, `client_id`, `master_user_id`, `owner_id`, `parent_folder_id`,"
			  + " `folder_type` FROM `folder` LEFT join document_folder df on id=df.folder_id AND df.document_id= :id ORDER BY "
			  + "`field1` DESC",nativeQuery=true)
	  List<Object> findAvailableFolders(String id);
	   
	   
	  Page<Folder>findByDocumentsDocumentId(UUID id,Pageable pageable);
	  
	  
	  @Transactional
	    @Modifying
	    @Query("update Folder f set f.isArchived=true where f.id=:uuid ")
	    void updateArchived(UUID uuid);
	 
}
