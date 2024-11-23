package com.veviosys.vdigit.services;

import java.util.List;
import java.util.UUID;

import com.veviosys.vdigit.models.Favoritefolders;
import com.veviosys.vdigit.models.Folder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface FolderFavoriteService {
    
	
	public Page<Favoritefolders>findAll(Pageable pageable);
    public Folder add(UUID folderId);
    // public Favorite_folders update(Long id ,Favorite_folders p);
    // public boolean delete(Long id);
    
    public Page<Folder> getFavouriteFolderByUser(Pageable pageable);
    
    public void delete(UUID folderId);
    public List<UUID> findFolderByUser();
}