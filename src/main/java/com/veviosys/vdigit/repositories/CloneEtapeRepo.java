package com.veviosys.vdigit.repositories;

import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import com.veviosys.vdigit.models.CloneEtape;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CloneEtapeRepo extends JpaRepository<CloneEtape,Long>{
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO `user_gestion_etape` (`user_id`, `clone_etape_id`) VALUES (:id1, :id2);",nativeQuery = true)
    void addUserEtape(@Param("id1") Long idUs,@Param("id2")Long idEt);
    @Transactional
    @Modifying
    @Query(value = "delete from `user_gestion_etape` where clone_etape_id=:id2",nativeQuery = true)
    void deleteSteps(@Param("id2")Long idEt);
    

    //Count steps
//    @Query(value = "SELECT  COUNT( DISTINCT *) from UTILISATEUR u , user_gestion_etape ue , clone_etape e,profils_absence pa where "+
//    "      (DATEDIFF(NOW(),e.date_debut)>=0 and e.etat=0 and ue.clone_etape_id=e.id)"+
//    "  and ( ( ue.user_id=u.user_id and u.user_id=?1  ) or  "+
//    "   ( ue.user_id=pa.user and pa.supleant=?1 and u.user_id=pa.supleant))",nativeQuery = true)
   @Query(value = "SELECT count(DISTINCT e.id) from utilisateur u , user_gestion_etape ue , clone_etape e,profils_absence pa where (DATEDIFF(NOW(),e.date_debut)>=0 and e.etat=0 and ue.clone_etape_id=e.id) and ( ( ue.user_id=u.user_id and u.user_id=:id  ) or  ( ue.user_id=pa.user_id and pa.supleant=:id and u.user_id=pa.supleant and DATEDIFF( NOW(),STR_TO_DATE(pa.date_fin,'%Y-%m-%d' ) )>0))",nativeQuery = true)
    int countEtapes(@Param("id") Long idUs);
    
      


    // FIND STEPS TO DO suppleant (With pagination)
    @Query(value = "select * from clone_etape where "+
    " id in (SELECT e.id from utilisateur u , user_gestion_etape ue , clone_etape e,profils_absence pa where "+
   "      (DATEDIFF(NOW(),e.date_debut)>=0 and e.etat=0 and ue.clone_etape_id=e.id)"+
   "  and ( ( ue.user_id=u.user_id and u.user_id=?1  ) or  "+
            "   ( ue.user_id=pa.user_id and pa.supleant=?1 and u.user_id=pa.supleant)) )", nativeQuery = true)
    Page<CloneEtape> findStepsTodo(Long idUs,Long type,Long filter,Pageable pageable );
   
    // FIND STEPS TO DO suppleant (Without pagination)
  
    @Query(value = "select * from clone_etape where "+
    " id in (SELECT e.id from utilisateur u , user_gestion_etape ue , clone_etape e,profils_absence pa where "+
   "      (DATEDIFF(NOW(),e.date_debut)>=0 and e.etat=0 and ue.clone_etape_id=e.id)"+
   "  and ( ( ue.user_id=u.user_id and u.user_id=?1  ) or  "+
            "   ( ue.user_id=pa.user_id and pa.supleant=?1 and u.user_id=pa.supleant)) )", nativeQuery = true)
   List<CloneEtape> findAllStepsTodo(Long idUs,Long type,Long filter);

   List<CloneEtape> findByCourrierIdOrderByNumeroAsc(UUID id);
   
   List<CloneEtape> findByQuality(String quality);
   
   CloneEtape findByCourrierIdAndEtatAndDateFinIsNotNull(UUID id,int etat);
} 