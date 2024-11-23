package com.veviosys.vdigit.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import com.veviosys.vdigit.models.Entity;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.repositories.EntityRepo;
import com.veviosys.vdigit.repositories.UserRepository;

import enums.EntityCat; 

@Service
public class EntityService {

	@Autowired
	userService serviceUser;
	@Autowired
	EntityRepo entityRepo;
	@Autowired UserRepository userRepository;
	public List<Entity>getAll(){
		return entityRepo.findByMasterUserId(serviceUser.connectedUserMaster(serviceUser.connectedUser().getUserId())) ;
	}
	
	@Secured({ "ROLE_MASTER" })
	public Page<Entity>getEntities(Pageable pageable){
		return entityRepo.findByMasterUserId(serviceUser.connectedUserMaster(serviceUser.connectedUser().getUserId()),pageable) ;
	}
	
	
	@Secured({ "ROLE_MASTER" })
	public void editEntity(Long id,int cat,List<Long> users,String name)
	{
		Entity e=entityRepo.findById(id).orElse(null);
		e.setName(name);
		e.setCat(cat==0?EntityCat.BO:cat==1?EntityCat.ENTITY:EntityCat.DELEGATION);
		for (User user : e.getUsers()) {
		 user.setEntity(null);
		 userRepository.save(user);
		}
		for (Long idU : users) {
			User user=userRepository.findById(idU).orElse(null);
		
			 user.setEntity(e);
			 userRepository.save(user);
		}
	}
	@Secured({ "ROLE_MASTER" })
	public void deleteEntity(Long id) {
		entityRepo.delete(entityRepo.findById(id).orElse(null));
	}
	
	@Secured({ "ROLE_MASTER" })
	public List<User>getUsersEntity(Long id){
		return userRepository.findUserByEntityId(id);
	} 
}
