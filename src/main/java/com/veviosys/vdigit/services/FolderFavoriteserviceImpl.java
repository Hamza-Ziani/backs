
package com.veviosys.vdigit.services;

import java.util.List;
import java.util.UUID;

import com.veviosys.vdigit.models.Favoritefolders;
import com.veviosys.vdigit.models.Folder;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.repositories.FolderFavoriteRepo;
import com.veviosys.vdigit.repositories.FolderRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class FolderFavoriteserviceImpl implements FolderFavoriteService {
    @Autowired
    public FolderFavoriteRepo folderFavoriteRepo;
    @Autowired
    FolderRepo fr;

    @Override
    public Page<Favoritefolders> findAll(Pageable page) {

        return folderFavoriteRepo.findAll(page);
    }

    public User connectedUser() {
        return ((CostumUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

    @Override
    public Folder add(UUID folderId) {
        // Favoritefolders favoritefolders = new Favoritefolders();
        // FKFOldersFavorite id = new FKFOldersFavorite();
        User u = connectedUser();
        // id.setUser(connectedUser());
        Folder f = fr.findById(folderId).orElse(null);
        if (f.getFavoriteBay().equals(" ")||f.getFavoriteBay().equals(null)) {
            f.setFavoriteBay(u.getUserId().toString());
        } else if (f.getFavoriteBay().indexOf(u.getUserId().toString(), 0) == -1) {
            f.setFavoriteBay(f.getFavoriteBay() + "/" + u.getUserId().toString());

        }
        // Folder folder = new Folder();
        // folder.setId(folderId);
        // id.setFolder(folder);
        // favoritefolders.setId(id);

        return fr.saveAndFlush(f);
    }

    @Override
    public Page<Folder> getFavouriteFolderByUser(Pageable page) {
        // FKFOldersFavorite pkey = new FKFOldersFavorite();
        // pkey.setUser(connectedUser());
        // Favoritefolders ff=new Favoritefolders();
        // ff.setId(pkey);
        // List<Favoritefolders> result = folderFavoriteRepo.findBy(Example.of(ff));
        String u = connectedUser().getUserId().toString();
        return fr.findByFavoriteBayOrFavoriteBayLikeOrFavoriteBayLikeOrFavoriteBayLike(u, "%/" + u + "/%", "%/" + u,
                u + "/%", page);
    }

    @Override
    public void delete(UUID folderId) {
        // Favoritefolders favoritefolders = new Favoritefolders();
        // FKFOldersFavorite id = new FKFOldersFavorite();
        Long u = connectedUser().getUserId();
        Folder f = fr.findById(folderId).orElse(null);
        String[] d = f.getFavoriteBay().split("/");
        String fav = f.getFavoriteBay();
        if (fav.indexOf("/", 0) == -1) {
            f.setFavoriteBay(" ");
        }

        else if (d.length > 1) {
            if (d[0].equals(u.toString())) {
                f.setFavoriteBay(fav.replace(u + "/", ""));

            } else {
                f.setFavoriteBay(fav.replace("/" + u, ""));

            }

        }

        // else if (d.length>2) {

        // }
        fr.save(f);
        // id.setUser(connectedUser());
        // Folder folder = new Folder();
        // folder.setId(folderId);
        // id.setFolder(folder);
        // favoritefolders.setId(id);
        // folderFavoriteRepo.delete(favoritefolders);
    }

    public List<UUID> findFolderByUser() {

        return folderFavoriteRepo.findFolderByUser(connectedUser().getUserId());

    }

}