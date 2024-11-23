package com.veviosys.vdigit.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.veviosys.vdigit.models.FolderType;
import com.veviosys.vdigit.models.User;


public interface FolderTypeRepo extends JpaRepository<FolderType, Long> {

	@Query(value = "select ft.* from folder_type ft,folder_type_user ftu where ft.id=ftu.folder_type_id and ftu.user_id = ?1", nativeQuery = true)
	List<FolderType> findFolderTypeByMasterId(Long id);

	FolderType findFolderTypeByNameAndMaster (String name,User id);
	@Transactional
	@Modifying
	@Query(value = "insert into folder_type_user (user_id, folder_type_id) values (:idUser,:idFoltype)", nativeQuery = true)
	void addType(@Param("idUser") Long idUs, @Param("idFoltype") Long idM);

	// @Query("select u.userName from User u inner join u.area ar where ar.idArea =
	// :idArea")
	@Query(value = "select ft.* from  folder_type ft inner join folder_type_user ftu where ft.id=ftu.folder_type_id"
			+ " and ftu.user_id=:idU", nativeQuery = true)
	List<FolderType> findByUser(@Param("idU") Long idUser);

	@Query(value = "select ft.* from folder_type ft where ft.id not in"
			+ " (select ftu.folder_type_id from folder_type_user ftu where ftu.user_id=:idU)", nativeQuery = true)
	List<FolderType> findOthers(@Param("idU") Long idUser);

	//DELETE FOLDER TYPE
	@Transactional
	@Modifying
	@Query(value = "delete from folder_type_user where user_id=:idUser and folder_type_id=:idFoltype", nativeQuery = true)
	void delType(@Param("idUser") Long idUs, @Param("idFoltype") Long idM);
	
	//FIND FOLDER TYPE IF EXISTE
	@Query(value="select ft.* from folder_type ft where ft.master=:id and ft.name=:name",nativeQuery = true)
	FolderType findByname(@Param("name")String name,@Param("id")Long id);
	
	// find folders type by master
	@Query(value="select ft.* from folder_type  folder_type_user ftu "
			+ " where ft.master=:idM and ft.id=ftu.folder_type_id and ftu.user_id=:idU",nativeQuery = true)
	List<FolderType> findFoldersType(@Param("idM")Long idM ,@Param("idU")Long idU);
	//find others::
	@Query(value = "select ft.* from folder_type ft where ft.master=:idm and ft.id not in"
			+ " (select ftu.folder_type_id from folder_type_user ftu where ftu.user_id=:idU)", nativeQuery = true)
	List<FolderType> findOthers(@Param("idU") Long idU,@Param("idm") Long idM);
	 @Query(value="select count(*) from folder d where d.folder_type="
	            + ":idType and master_user_id=:idM",nativeQuery = true)
	    int findByType(@Param("idType")Long idType,@Param("idM")Long idM);
	
}