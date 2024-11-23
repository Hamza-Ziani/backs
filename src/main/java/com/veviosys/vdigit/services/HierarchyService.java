package com.veviosys.vdigit.services;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class HierarchyService {
   String R="dsd";
	@Autowired
	private UserRepository userRepository;	
	
	  public  User connectedUser() {return  ((CostumUserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal()).getUser();}
	  
	public List<HashMap<String, Object>> getHierarchyData()
	{
		User connectedUser = Objects.isNull(connectedUser().getMaster())? connectedUser(): connectedUser().getMaster();
		List<HashMap<String, Object>> hierarchyData =  new ArrayList<HashMap<String,Object>>();
		
		HashMap<String, Object> user = new HashMap<String, Object>();
		
		
		user.put("key", connectedUser.getUserId());
		user.put("name", connectedUser.getFullName());
		user.put("title", connectedUser.getTitle());
		user.put("mat", connectedUser.getMat());
		user.put("sex", connectedUser.getSexe());
		user.put("parent", connectedUser.getParent());
		user.put("pic", getUserPic(connectedUser));
		
		hierarchyData.add(user);
		
		List<User> users =  userRepository.findUserByMaster(connectedUser.getUserId());
		
		
		for (User us : users) {
			user = new HashMap<String, Object>();
			user.put("key", us.getUserId());
			user.put("name", us.getFullName());
			user.put("title", us.getTitle());
			user.put("parent", us.getParent());
		user.put("mat", us.getMat());
		user.put("sex",us.getSexe());
		user.put("pic", getUserPic(us));
			hierarchyData.add(user);
		}
		
		
		
		return hierarchyData;
	}
	public String getUserPic(User _user) {

	 	String ret="";
	 	if(_user.getPicPath()!=null) {
			byte[] btyes= "".getBytes();
	 		 try {
	 			File f = new File(_user.getPicPath());
	 			//System.err.println(btyes.length +"");
	 			btyes = Files.readAllBytes(f.toPath());
	 			ret = "data:" + _user.getContetType() + ";base64,"
		 				+ Base64.getEncoder().encodeToString(btyes);
	 			//System.err.println(btyes.length +"");
	 			
	 		} catch (Exception e) {
				// TODO: handle exception
	 			e.printStackTrace();
			}
	 		
	 				return ret ;
		}
	 	return null;
	
	 }
	
	public List<User> getUserChilds()
	{ 
	 
	return userRepository.findByParent(connectedUser().getUserId());
	}
}
