package com.veviosys.vdigit.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.veviosys.vdigit.models.GroupUser;
import com.veviosys.vdigit.models.User;

@Repository
public interface GroupUserRepo extends JpaRepository<GroupUser, Long>{
 
    
    List<GroupUser>  getGroupUserByMaster(User user);
}
