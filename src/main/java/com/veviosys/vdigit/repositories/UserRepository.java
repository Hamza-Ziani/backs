package com.veviosys.vdigit.repositories;
 
import java.util.List;


import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
 
import com.veviosys.vdigit.models.User; 
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

	// @Query(value="select user.user_id from user where( master=:id or master is
	// null) and full_name=:name ",nativeQuery=true)
	User findByMasterAndFullNameIgnoreCase(User u, String fullName);

	User findByUsernameIgnoreCase(String username);

	@Query(value = "select * from UTILISATEUR u where u.username=?1", nativeQuery = true)
	User searchByname(String username);
 
    @Transactional
	@Modifying
	@Query(value = "UPDATE UTILISATEUR u set u.secondary = :id_seconndry WHERE u.user_id = :user_id",nativeQuery = true)
	
	void setSecondaryUser(@Param("id_seconndry") Long id,@Param("user_id") Long userId);
    
    @Query(value = "select count(*) from UTILISATEUR u where u.username=:name and u.from_ldap =:fromLdap", nativeQuery = true)
    int findByUsernameAndFromLdap(@Param("name") String name,@Param("fromLdap") int fromLdap);
    
    @Transactional
   	@Modifying
   	@Query(value = "UPDATE UTILISATEUR u set u.secondary = 0 WHERE u.user_id = :user_id",nativeQuery = true)
   	void removeSecondaryUser(@Param("user_id") Long userId);
    
    @Query(value = "SELECT u.* FROM UTILISATEUR u where u.user_id NOT IN (SELECT us.secondary from UTILISATEUR us where us.secondary IS NOT NULL ) and u.user_id != :id",nativeQuery = true)
    List<User> getAllUsersWithoutSencondry(@Param("id") Long id);

    @Query(value = "SELECT u.* FROM UTILISATEUR u where u.user_id  IN  (SELECT us.secondary from UTILISATEUR us where us.user_id = :id )",nativeQuery = true)
    User getSecondaryUser(@Param("id") Long id);
    
    @Query(value = "SELECT u.* FROM UTILISATEUR u where u.secondary  = :id",nativeQuery = true)
    User getUserSecondary(@Param("id") Long id);
    
	@Query(value = "SELECT u.master FROM UTILISATEUR u WHERE u.user_id = ?1", nativeQuery = true)
	Long findUserMaster(Long id);

	User findByEmailIgnoreCase(String email);

	User findByMasterAndMatIgnoreCase(User m, String mat);

	User findByisClient(Long id);

	 
	List<User> findByParent(Long id);
	
	@Query(value = "SELECT * FROM UTILISATEUR u WHERE u.user_id = ?1 ", nativeQuery = true)
	List<User> findByChild(Long id);
	
	@Query(value = "SELECT * FROM UTILISATEUR u WHERE u.user_id = ?1 ", nativeQuery = true)
    User findByUserChild(Long parentId);
	
	List<User> findByTitle(String title);
	
	
	List<User> findUsersByMaster(User ms);

	@Query(value = "SELECT u.* FROM UTILISATEUR u WHERE u.master= ?1 and u.is_client is null", nativeQuery = true)
	List<User> findUserByMaster(Long id);

	@Query(value = "SELECT u.* FROM UTILISATEUR u WHERE u.master= ?1 and u.user_id!=?2 and u.is_client is null", nativeQuery = true)
	List<User> findUsersByMaster(Long id, Long id2);

	@Query(value = "select u.* from UTILISATEUR u , user_gestion_etape ue , clone_etape e where "
			+ " ue.user_id=u.user_id and ue.clone_etape_id=e.id and e.id=:id", nativeQuery = true)
	List<User> findUsersByEtape(@Param("id") Long idEtp);

	@Query(value = "SELECT u.* FROM UTILISATEUR u WHERE (u.master= ?1 or u.user_id=?1) and u.is_client is null", nativeQuery = true)
	List<User> findAllWithMaster(Long id);

	@Query(value = "Select pa.id from profils_absence pa where pa.supleant=?1", nativeQuery = true)
	List<Long> findProfilsAbsByUser(Long id);

	@Query(value = "SELECT * FROM UTILISATEUR  WHERE  master is NULL", nativeQuery = true)
	List<User> findMasters();
	// NB  ONLY ONE MASTER
	List<User>findByUserIdNotLike(Long id);
	Page<User> findByMasterUserIdAndEmailContainingIgnoreCaseOrMasterUserIdAndUsernameContainingIgnoreCaseOrMasterUserIdAndFullNameContainingIgnoreCaseOrMasterUserIdAndMatContainingIgnoreCaseOrMasterUserIdAndTitleContainingIgnoreCase(Long id,String param,Long id1,String param1,Long id2,String param2,Long id3,String param3,Long id4,String param4,Pageable pageable);
	List<User>findByEntityIsNull();
List<User>findUserByEntityId(Long id);
List<User>findUserByEntityIsNull();
Page<User>findUserByTentativeNumber(Integer tentative,Pageable pageable);
List<User>findByMasterUserIdAndUserIdNotLike(Long master,Long id);

}
 