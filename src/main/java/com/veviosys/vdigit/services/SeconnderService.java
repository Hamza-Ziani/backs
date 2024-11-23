package com.veviosys.vdigit.services;


import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;

import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.veviosys.vdigit.models.Journal;
import com.veviosys.vdigit.models.Quality;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.repositories.JournalRepo;
import com.veviosys.vdigit.repositories.QualityRepo;
import com.veviosys.vdigit.repositories.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@AllArgsConstructor
public class SeconnderService {
  

	@Autowired
	private final UserRepository userRepo;


	@Autowired
	JournalRepo jr;
	
	@Autowired
	mailService serviceMail;

	BCryptPasswordEncoder passwordEncoder;
	
	
	 
	public Long connectedUserMaster(Long userid) {
		Long id;
		id = userRepo.findUserMaster(userid);
		if (id != null) {
			return id;
		}
		return userid;
	}
	// GET MASTER OF CONNECTED USER
	public User getMaster() {
		return userRepo.getOne(connectedUserMaster(connectedUser().getUserId()));
	}
	// GET CONNECTED USER

	public User connectedUser() {
		return ((CostumUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
	}

	
	public ResponseEntity<User> addSecondary(Long seconndery_id) {
		
		// get user to add secondary to him
		User LoggedUser = connectedUser();
		
		
		
		if( seconndery_id == 0) {

			 User user = userRepo.getSecondaryUser(LoggedUser.getUserId());
			 
			 userRepo.removeSecondaryUser(LoggedUser.getUserId());
			 String name = LoggedUser.getSexe().equals("M") ? "M " +" "+ LoggedUser.getFullName() : "Mme " +" "+ LoggedUser.getFullName();
			 
             if(LoggedUser.getSexe().equals("")) {
                 name = "M/Mme";
             }
				try {
					serviceMail.notifyProfilSecSupp(name, user.getFullName(), user.getEmail(),
							"Désactivation", "supprimé");
					System.out.println("Sent !!!!!!");
				} catch (AddressException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (MessagingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (userRepo.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() >= 1) {
						Journal j = new Journal();

						j.setUser(connectedUser());
						j.setDate(new Date());
						j.setAction("supprimer son profil secondaire ");
						j.setComposante("Secondaire Profil");
						j.setMode("S");
						j.setTypeEv("Utilisateur/Suppression");
						j.setAction(" Profil supprimé :" +  user.getFullName() );
						j.setMode("S");
						j.setMaster(userRepo.getOne(connectedUserMaster(connectedUser().getUserId())));

						jr.save(j);
			} 
			 
		     return new ResponseEntity<User>(HttpStatus.OK);
		} else {
			// check if user exist
			if (LoggedUser != null) {
			
				// set to user

			 userRepo.setSecondaryUser(seconndery_id,LoggedUser.getUserId());
						
			 User user = userRepo.getSecondaryUser(LoggedUser.getUserId());
			 
 String name = LoggedUser.getSexe().equals("M") ? "M " +" "+ LoggedUser.getFullName() : "Mme " +" "+ LoggedUser.getFullName();
             
             if(LoggedUser.getSexe().equals("")) {
                 name = "M/Mme";
             }
				try {
					serviceMail.notifyProfilSec(name, user.getFullName(), user.getEmail(),
							"Désignation", "déclaré");
					System.out.println("Sent !!!!!!");

				} catch (AddressException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (MessagingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			 if (userRepo.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() >= 1) {
					Journal j = new Journal();

					j.setUser(connectedUser());
					j.setDate(new Date());
					j.setTypeEv("Utlisateur/Ajout ");
					j.setComposante("Secondaire Profil");
					String h = "";
					
					j.setAction("User : " + LoggedUser.getFullName() + " | Secondaire : " + user.getFullName());
							
					j.setMode("A");
					j.setMaster(userRepo.findById(connectedUserMaster(connectedUser().getUserId())).orElse(null));

					jr.save(j);
				}
			     
		     return new ResponseEntity<User>(user,HttpStatus.OK);
		      
		     
				
			} else {
				 
				return new ResponseEntity<User>(HttpStatus.NOT_FOUND);

			}

		}

	}
	
	public List<User> getAllUsersWithoutSecondary() {
		
		User LoggedUser = connectedUser();
		
		List<User> users = userRepo.getAllUsersWithoutSencondry(LoggedUser.getUserId());
	              
	    return users;
	
	}
	
	public ResponseEntity<User> getSecondaryUser() {
		
			
			// get user to add secondary to him
			User LoggedUser = connectedUser(); 
			
			User user = userRepo.getSecondaryUser(LoggedUser.getUserId());
		    
	        return new ResponseEntity<User>(user,HttpStatus.OK);
	
	}
	
	public User GetMaster() {
        if (getUser().getMaster() == null) {
            return getUser();
        }
        return getUser().getMaster();
    } 
	public User getUser() {
        CostumUserDetails user = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return user.getUser();
    }
	public ResponseEntity<User> getSecondaryUserLogin(String username,String password) {
				
		// get user to add secondary to him
        
		if (Objects.nonNull(username) && Objects.nonNull(password)) {
		    
		    User user = userRepo.findByUsernameIgnoreCase(username);
		    
		    System.out.println("connected user : " + user.getFullName());
		    
		    if(Objects.nonNull(user)) {
		        
		        byte[] decodedBytes = Base64.getDecoder().decode(password);
                String decodedString = new String(decodedBytes);
                
		        if(passwordEncoder.matches(decodedString.toString(), user.getPassword())) {
		            
                    User SecondaryUser = userRepo.getUserSecondary(user.getUserId());
                    
                    //System.out.println("connected user : " + SecondaryUser.getFullName());
                    
                    if(Objects.isNull(user) || Objects.isNull(SecondaryUser)) {
                        return new ResponseEntity<User>(HttpStatus.NOT_FOUND); 
                    }
                
                
                    if (GetMaster().getSecLevel() >= 1) {
                        Journal j = new Journal();

                        j.setUser(SecondaryUser);
                        j.setDate(new Date());
                        j.setMode("L");
                        j.setComposante("Login");
                        j.setTypeEv("Utilisateur/Connexion/Secondaire Profil");
                        j.setAction(user.getFullName() + " connecté à " + SecondaryUser.getFullName());
                        j.setConnectedSacondaryName(user.getFullName());
                        if (SecondaryUser.getMaster() != null) {
                            // usLog.setNomClient(user.getUser().getMaster().getNomClient());

                            j.setMaster(SecondaryUser.getMaster());

                        } else {
                            j.setMaster(SecondaryUser);
                        }
                        jr.save(j);
                    }
                     
                     
                    
                    
                    return new ResponseEntity<User>(SecondaryUser,HttpStatus.OK); 
                    
		        }else{
		            return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
		        }
  
	        }else {
	            return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
	        }
		    
		} else {
		    return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
		}
	


    }

	
	
}
