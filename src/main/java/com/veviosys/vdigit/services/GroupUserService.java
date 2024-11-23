package com.veviosys.vdigit.services;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.veviosys.vdigit.models.GroupUser;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.repositories.GroupUserRepo;

@Service
public class GroupUserService {

    @Autowired
    GroupUserRepo grouUserRepo;

    public User connectedUser() {
        return ((CostumUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

    public List<GroupUser> getAllGroupUserNoPage() {
        return grouUserRepo.getGroupUserByMaster(Objects.nonNull(connectedUser().getMaster()) ? connectedUser().getMaster() : connectedUser());
    }

    public Page<GroupUser> getAllGroupUserPage(Pageable pageable) {

        List<GroupUser> list = grouUserRepo.getGroupUserByMaster(connectedUser());

        int start = (int) pageable.getOffset();
        int end = (int) (start + pageable.getPageSize()) > list.size() ? list.size() : (start + pageable.getPageSize());
        Page<GroupUser> pages = new PageImpl<GroupUser>(list.subList(start, end), pageable, list.size());

        return pages;
    }

    public ResponseEntity EditGroupUser(String name, Long id) {

        try {
            
          
            GroupUser group = grouUserRepo.findById(id).get();

            if (Objects.nonNull(group)) {

                group.setId(id);
                group.setGroupName(name);

                grouUserRepo.save(group);
                return new ResponseEntity(HttpStatus.OK);
            } else {
                return new ResponseEntity(HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity addGroupUser(String name) {

        try {
 
           
            
            GroupUser group = new GroupUser();

            group.setGroupName(name);

            group.setMaster(connectedUser());
            
            grouUserRepo.save(group);

            return new ResponseEntity(HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity deleteGroupUser(Long id) {
        try {

            grouUserRepo.delete(grouUserRepo.findById(id).get());
            

            return new ResponseEntity(HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
