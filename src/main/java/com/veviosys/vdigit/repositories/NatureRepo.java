package com.veviosys.vdigit.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import com.veviosys.vdigit.models.Nature;
import com.veviosys.vdigit.models.User;

public interface NatureRepo extends JpaRepository<Nature, Long> {
 
    List<Nature> findByMaster(User master);

    @Query(value="select * from nature where master=:idU",nativeQuery = true)
    List<Nature>findByM(@Param("idU")Long id);
    Page<Nature> findByMaster(User master,Pageable pageable);
    // List<Nature> findByFolders(List<Folder> folders);
    List<Nature> findByPermissionNatureCourriers_PermissionCourrierId(Long id);
//  List<DocumentType> findBypermissionDocumentTypes_PermissionDocumentId(Long per
    Page<Nature> findByNameContainingIgnoreCaseAndMaster(String l , Pageable page,User master);
 //   List<Nature> findNatureByPermissionNatureCourriers_PermissionCourrierPermissionGroupNatureGroupUsers_UserUserIdLike(Long userId);
}
   