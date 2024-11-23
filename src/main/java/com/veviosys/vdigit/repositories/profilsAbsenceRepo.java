package com.veviosys.vdigit.repositories;

import java.util.List;

import com.veviosys.vdigit.models.ProfilsAbsence;
import com.veviosys.vdigit.models.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface profilsAbsenceRepo extends JpaRepository<ProfilsAbsence,Long> , JpaSpecificationExecutor<ProfilsAbsence>
{
    List<ProfilsAbsence>findByMaster(User master);
    
    List<ProfilsAbsence>findByUserUserId(Long id);

    @Query(value = "select distinct u.user_id from utilisateur u, profils_absence pa where u.user_id=:suid and"
    +" pa.supleant=:uid and DATEDIFF( STR_TO_DATE(pa.date_fin,'%Y-%m-%d' ) ,NOW())>0 ",nativeQuery = true)
    Long findBySuppAndDate(@Param("uid") Long uid,@Param("suid") Long suid);

    Page<ProfilsAbsence> findByMasterUserId(Long id,Pageable pageable);
    Page<ProfilsAbsence> findByUserUserId(Long id,Pageable pageable);
 List<ProfilsAbsence>findBySupleantAndDateFinLessThanEqualAndDateDebutGreaterThanEqual(User u,String date,String date1);

 @Query(value = "select * from  profils_absence pa where "
 +" pa.supleant=:uid and DATEDIFF( STR_TO_DATE(pa.date_fin,'%Y-%m-%d' ) ,NOW())>0 ",nativeQuery = true)
 List<ProfilsAbsence>findPaByUser(@Param("uid") Long uid);
 List<ProfilsAbsence>findByUserUserIdAndDateFinLessThanEqualAndDateDebutGreaterThanEqual(Long u,String dateFin,String dateDebut);
 
 @Query(value = "select * from profils_absence pa where pa.supleant = ?",nativeQuery = true)
 List<ProfilsAbsence> findBySupleant(Long id);
 
 @Query(value = "SELECT count(*) FROM profils_absence where user_id = :idUser and date_fin >= str_to_date(:date,'%Y-%m-%d') or  str_to_date(:dateDebut,'%Y-%m-%d') between date_debut and date_fin",nativeQuery = true)
 int checkifExist(@Param("idUser") Long idSup,@Param("date") String date,@Param("dateDebut") String dateDebut);
 
 
}