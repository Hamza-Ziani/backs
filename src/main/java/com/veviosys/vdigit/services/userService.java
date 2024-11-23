package com.veviosys.vdigit.services;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.mail.MessagingException;

import com.veviosys.vdigit.classes.mapClass;
import com.veviosys.vdigit.models.AttributeType;
import com.veviosys.vdigit.models.ClientDoc;
import com.veviosys.vdigit.models.CloneEtape;
import com.veviosys.vdigit.models.Document;
import com.veviosys.vdigit.models.DocumentAttributeValue;
import com.veviosys.vdigit.models.DocumentType;
import com.veviosys.vdigit.models.Entity;
import com.veviosys.vdigit.models.Folder;
import com.veviosys.vdigit.models.Groupe;
import com.veviosys.vdigit.models.InterneMessage;
import com.veviosys.vdigit.models.Journal;
import com.veviosys.vdigit.models.Nature;
import com.veviosys.vdigit.models.NotifMail;
import com.veviosys.vdigit.models.PermissionDocumentType;
import com.veviosys.vdigit.models.PermissionGroup;
import com.veviosys.vdigit.models.PermissionGroupN;
import com.veviosys.vdigit.models.PermissionNatureCourrier;
import com.veviosys.vdigit.models.Processus;
import com.veviosys.vdigit.models.ProfilsAbsence;
import com.veviosys.vdigit.models.RecieveMessage;
import com.veviosys.vdigit.models.RecieveMessagePK;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.models.delayMail;
import com.veviosys.vdigit.reposietories.AttributeTypeRepo;
import com.veviosys.vdigit.repositories.ClientDocRepo;
import com.veviosys.vdigit.repositories.CloneEtapeRepo;
import com.veviosys.vdigit.repositories.DocumentRepo;
import com.veviosys.vdigit.repositories.DocumentTypeRepo;
import com.veviosys.vdigit.repositories.EntityRepo;
import com.veviosys.vdigit.repositories.FolderRepo;
import com.veviosys.vdigit.repositories.GroupRepo;
import com.veviosys.vdigit.repositories.InterneMessageRepo;
import com.veviosys.vdigit.repositories.JournalRepo;
import com.veviosys.vdigit.repositories.NatureRepo;
import com.veviosys.vdigit.repositories.NotifMailRepo;
import com.veviosys.vdigit.repositories.PermissionGroupNRepo;
import com.veviosys.vdigit.repositories.PermissionGroupRepo;
import com.veviosys.vdigit.repositories.ReceiveMessageRepo;
import com.veviosys.vdigit.repositories.UserRepository;
import com.veviosys.vdigit.repositories.delayMailRepo;
import com.veviosys.vdigit.repositories.profilsAbsenceRepo;
import com.veviosys.vdigit.sharedfolders.models.SharedFolder;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class userService {
	@Autowired
	private UserRepository ur;
	@Autowired
	private DocumentRepo dr; 
	@Autowired
	private InterneMessageRepo imr;
	@Autowired
	private ReceiveMessageRepo rmr;
	@Autowired
	CloneEtapeRepo cer;
	@Autowired
	profilsAbsenceRepo par;
	@Autowired
	JournalRepo jr;
	@Autowired
	ClientDocRepo cdr;
	@Autowired
	GroupRepo gr;
	@Autowired
	PermissionGroupRepo pgr;
	@Autowired
	DocumentTypeRepo dtr;
	@Autowired
	NatureRepo nr;
	@Autowired
	private PermissionGroupNRepo pgnr;
	@Autowired
	private SearchService searchService;
	

	@Value("${tentative.number}")
	int tentative;
	public User getUser() {
		CostumUserDetails user = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		return user.getUser();
	}

	public User GetMaster() {
		if (getUser().getMaster() == null) {
			return getUser();
		}
		return getUser().getMaster();
	} 
	// US LOG

	@Autowired
	private UserRepository us;

	@Transactional
	public ResponseEntity<User> uslog() {
	    try {
		CostumUserDetails user = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		User usLog = user.getUser();

		
		// for (Processus pp :usLog.getProcessus()) {
		// //System.out.println(pp.getName());
		// }
		usLog.setLastLogin(new Date());
		us.save(usLog);
		
		  if (GetMaster().getSecLevel() >= 1) {
	            Journal j = new Journal();

	            j.setUser(getUser());
	            j.setDate(new Date());
	            j.setMode("L");
	            j.setComposante("Login");
	            j.setTypeEv("Utilisateur/Connexion");
	            j.setAction("Connexion au site web");

	            if (user.getUser().getMaster() != null) {
	                // usLog.setNomClient(user.getUser().getMaster().getNomClient());

	                j.setMaster(user.getUser().getMaster());

	            } else {
	                j.setMaster(getUser());
	            }
	            jr.save(j);
	        }
	        return new ResponseEntity<User>(usLog, HttpStatus.ACCEPTED);
	        
	    }catch(Exception ex) {
	        ex.printStackTrace();
	        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	
	}
	// GET USERS

	public List<User> getUsers() {
		List<User> us = new ArrayList<User>();
		us = ur.findUsersByMaster(GetMaster().getUserId(), getUser().getUserId());
		if (getUser().getMaster() != null) {
			us.add(getUser().getMaster());
		}

		return us;
	}

	public List<mapClass> getMasters() {
		List<User>masters=us.findMasters();
		List<mapClass>mastersName=new ArrayList<mapClass>();
		
		for (User user : masters) {
			mastersName.add(new mapClass(user.getNomClient(),Integer.valueOf(user.getUserId().toString())));
		}
		return mastersName;
	}

	// SHARE DOCUMENT WITH USERS
	public ResponseEntity sendInterneMessage(List<String> lst, String mess, UUID docId) {
		try {

			List<User> listUsers = new ArrayList<User>();
			for (String name : lst) {
				listUsers.add(ur.findByMasterAndFullNameIgnoreCase(GetMaster(), name));
			}
			InterneMessage msg = new InterneMessage();
			// msg.setReceivers(listUsers);

			msg.setMessage(mess);
			msg.setSender(getUser());
			Document dc = dr.getOne(docId);
			msg.setDoc(dc);
			msg.setSentDate((new Date()));
			msg = imr.saveAndFlush(msg);
			String u = "a ";
			int xx = 0;

			for (User user : listUsers) {
				RecieveMessage rm = new RecieveMessage();
				rm.setId(new RecieveMessagePK(msg.getId(), user.getUserId()));
				rm.setMessage(msg);
				rm.setUser(user);
				if (xx == 0) {
					u += user.getFullName();
					xx = 444;
				} else {
					u += ", " + user.getFullName();
				}
				rm.setSeen(0);
				rmr.save(rm);

			}
			if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() >= 3) {
				Journal j = new Journal();

				j.setUser(connectedUser());
				j.setDate(new Date());
				j.setTypeEv("Envoi");
				// j.setAction( ref + "' dans le type '" + doc.getType().getName() + "'");
				String ev = "Envoi de document ";

				int a = 0;
				for (DocumentAttributeValue ee : dc.getAttributeValues()) {
					if (!ee.getAttribute().getName().equals("Fichier") && ee.getValue().getValue().length() > 0) {
						if (a == 0) {
							ev += ee.getAttribute().getName() + " (" + ee.getValue().getValue() + ")";
		 					a = 546;
						} else { 
							ev += ", " + ee.getAttribute().getName() + " (" + ee.getValue().getValue() + ")";
 
						}
					}
				}

				j.setAction(ev + " " + u);
				j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

				jr.save(j);
			}

			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public User connectedUser() {
		return ((CostumUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
	}

	public Long connectedUserMaster(Long userid) {
		Long id;
		id = ur.findUserMaster(userid);
		if (id != null) {
			return id;
		}
		return userid;
	}

	// GET RECEIVE MESSAGE OF USER
	public Page<InterneMessage> getMessage(Pageable pageable) {
		return imr.findByReceivers_UserUserId(getUser().getUserId(), pageable);

	}
	// Get Receivers Message Of User Connect: 
//	public Page<InterneMessage> getAllDocumentsBySenderOrReceiver(String fullName, Pageable pageable) {
//	    Long userId = getUser().getUserId();
//	    return imr.findBySender_UserIdOrReceivers_User_FullNameContaining(userId, fullName, pageable);
//	}
	public Page<InterneMessage> getAllDocumentsBySenderOrReceiver(String fullName, Pageable pageable) {
	    User user = connectedUser(); 
	    return imr.findDistinctBySenderAndReceiversUserFullNameContainingIgnoreCase(user, fullName, pageable);
	}






	public int getMessageCount() {
		return imr.getCountMessages(getUser().getUserId());
	}

	// SEEN MESSAGE
	public ResponseEntity messageSeen(Long id) {
		RecieveMessage rm = rmr.findById(new RecieveMessagePK(id, getUser().getUserId())).orElseGet(null);
		rm.setSeen(1);
		// System.out.println(rm.getSeen());
		rmr.save(rm);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	public long stepToDo() {
		//return cer.countEtapes(getUser().getUserId());
	    //type=-1&filter=-1&page=0&size=18
	   Pageable pageable = PageRequest.of(1, 1
	           );
	   
        return searchService.findFoldersBySteps(pageable, "-1",-1,-1,"date,desc").getTotalElements();
	}

	public Page<CloneEtape> getStepe(Long type, Long filter, Pageable pageable) throws ParseException {
		// List<CloneEtape> lst =
		// System.out.println(pageable.toString());

		return cer.findStepsTodo(getUser().getUserId(), type, filter, pageable);

	}

	public List<CloneEtape> gEtape(User u) {

		return cer.findAllStepsTodo(u.getUserId(), -1l, 1L);
	}

	public Page<ClientDoc> getDocsClt(Pageable pageable) {

		return cdr.findByMasterOrderBySentDateDesc(pageable, GetMaster().getUserId());
	}

	@Transactional
	public User getUserByUsername(String name) {
		return ur.findByUsernameIgnoreCase(name);
	}

	public User connectedUserAdmin(User u) {
		// User u= ur.findById(userid).orElse(null);
		if (u.getMaster() != null) {
			return u.getMaster();
		}
		return u;
	}

	// TEEEEST
	@Autowired
	@Lazy
	mailService ms;
	@Autowired
	delayMailRepo nmr;
	@Autowired
	NotifMailRepo nm;

	public void TimeStap() throws MessagingException, ParseException, IOException {
		List<User> lst = ur.findAll();
		for (User user : lst) {
			List<CloneEtape> etapes = cer.findAllStepsTodo(user.getUserId(), -1l, 1l);
			for (CloneEtape etape : etapes) {
				List<User> uList = ur.findUsersByEtape(etape.getId());
				for (User user2 : uList) {
					if (user.getUserId().equals(user2.getUserId())) {
						int delaiAlertSup = 1;
						int delaiUs = 1;
						int sentPerDay = 1;
						User master = connectedUserAdmin(user);
						NotifMail notif = nm.findByUserUserId(master.getUserId());
						delayMail settings = nmr.findByUserUserId(master.getUserId());

						if (Objects.nonNull(settings)) {
							delaiAlertSup = settings.getDelaySup();
							delaiUs = settings.getDelayUs();
							sentPerDay = settings.getDelayPerDay();

						}

						Calendar start = Calendar.getInstance();
						Calendar end = Calendar.getInstance();
						start.setTime(new Date());
						end.setTime(etape.getDateFin());
						Date startDate = start.getTime();
						Date endDate = end.getTime();
						long startTime = startDate.getTime();
						long endTime = endDate.getTime();
						long diffTime = endTime - startTime;
						long diffDays = diffTime / (1000 * 60 * 60 * 24);
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        etape.setLate_relance(1);
                        cer.save(etape);
						String date = simpleDateFormat.format(etape.getDateFin());
						if (delaiUs >= diffDays && etape.getDateFin().compareTo(new Date()) < 0)

						{

							String name;
							if (user.getSexe().equals("M")) {
								name = "Mr " + user.getFullName();
							} else {
								name = "Mme " + user.getFullName();

							}
							try {
								ms.alertUserStep(user.getEmail(), etape.getCourrier().getReference(), date, sentPerDay,
										name, notif);
							} catch (Exception e) {
								// TODO: handle exception
							}

						}
						if (delaiAlertSup >= diffDays && etape.getDateFin().compareTo(new Date()) < 0
								&& Objects.nonNull(user.getParent())) {
							String name;
							User pr = ur.getOne(user.getParent());
							if (user.getSexe().equals("M")) {
								name = "Mr " + user.getFullName();
							} else {
								name = "Mme " + user.getFullName();
							}
							try {
								ms.alertUserStep(pr.getEmail(), etape.getCourrier().getReference(), date, sentPerDay,
										name, notif);
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					}
				}
			}
		}
	}

	public void test(String i) {
		System.out.println("from " + i);
		// sssss
	}

	@Autowired
	EntityRepo entityrepo;

	public Entity getUserEntity() {

		return entityrepo.findByUsersUserId(connectedUser().getUserId());
	}

	public int hasAccessTo(Long id, String action) {

		User u = connectedUser();
		DocumentType dt = dtr.findById(id).orElse(null);
		if (Objects.nonNull(dt)) {
			List<Groupe> groups = gr.findGroupeByUsersUserId(u.getUserId());
			for (Groupe groupe : groups) {
				List<PermissionGroup> permissionGroups = pgr.findByGroup(groupe);
				for (PermissionGroup pd : permissionGroups) {

					for (PermissionDocumentType pdt : pd.getPermissionDocument().getPermissionDocumentTypes()) {

						if (dt.getId() == pdt.getDocumentType().getId()
								&& pdt.getPermissionDocument().getAcces().contains(action)

						) {
							return 1;
						}
					}
				}
			}
		}
		return -1;
	}

	public int hasAccessToNat(Long id, String action) {

		User u = connectedUser();
	 

		Nature n = nr.findById(id).orElse(null);
		if (Objects.nonNull(n)) {
			List<Groupe> groups = gr.findGroupeByUsersUserId(u.getUserId());
			for (Groupe groupe : groups) {
				List<PermissionGroupN> permissionGroups = pgnr.findByGroup(groupe);
				for (PermissionGroupN pd : permissionGroups) {

					for (PermissionNatureCourrier pdt : pd.getPermissionCourrier().getPermissionNature()) {

						if (n.getId() == pdt.getNature().getId()
								&& (pdt.getPermissionCourrier().getAcces().contains(action))) {
							return 1;
						}
					}
				}
			}
		}

		return -1;
	}
	public int checkValidity(String uname) {
		User u=ur.findByUsernameIgnoreCase(uname);
		if(Objects.nonNull(u)) {
		 
			if(u.getTentativeNumber()<tentative) {
				return 1;
			}
			 return -1;
		}
		return 0;
	}
}
