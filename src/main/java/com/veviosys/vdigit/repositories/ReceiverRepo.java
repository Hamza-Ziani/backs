package com.veviosys.vdigit.repositories;

import java.util.List;

import javax.transaction.Transactional;

import com.veviosys.vdigit.models.ElementTypeGroup;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.models.receiver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReceiverRepo extends JpaRepository<receiver, Long> {
    Page<receiver> findReceiverByMasterUserId(Long id, Pageable pageable);
    receiver findReceiverByNameAndMasterUserId(String name,Long id);
    receiver findReceiverByIdAndMasterUserId(Long name,Long id);
    receiver findReceiverByNameAndMaster(String name,User u);
    receiver findReceiverByEmail(String name);
    List<receiver> findReceiverByMasterUserId(Long id);
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO folder_receiver (receiver, folder) VALUES (:id2, :id1)",nativeQuery = true)
    void createFolderDest(@Param("id1")String id,@Param("id2")Long idRec);
    @Transactional
    @Modifying
    @Query(value = "delete folder_receiver where folder=:id",nativeQuery = true)
    void deleteFolderRelation(@Param("id")String id);
    
    Page<receiver> findByNameContainingIgnoreCase(String l , Pageable page);
}
