package com.veviosys.vdigit.repositories;

import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import com.veviosys.vdigit.models.FKFOldersFavorite;
import com.veviosys.vdigit.models.Favoritefolders;
import com.veviosys.vdigit.models.Folder;
import com.veviosys.vdigit.models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface FolderFavoriteRepo extends JpaRepository<Favoritefolders,FKFOldersFavorite>,JpaSpecificationExecutor<Favoritefolders>{
    
  Page<Favoritefolders> findByIdUser(User user,Pageable page);
    
  Favoritefolders deleteById(Favoritefolders favoritefolders);
@Transactional
@Modifying    
Object deleteByIdFolder(Folder folder);

  @Query(value = "select f.folder_id from favoritefolders f where f.user_id = :userId",nativeQuery = true)
    List<UUID> findFolderByUser(@Param("userId") long userId);
  
    @Transactional
    @Modifying
    @Query(value = "delete user_favorite_folders from user_favorite_folders  where folder_id = :folder",nativeQuery = true)
  void deleteByFolder(@Param("folder") String FOLDER);


}