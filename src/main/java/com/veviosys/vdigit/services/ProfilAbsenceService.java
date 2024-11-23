package com.veviosys.vdigit.services;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.veviosys.vdigit.classes.ProfilsAbsenceClass;
import com.veviosys.vdigit.models.Journal;
import com.veviosys.vdigit.models.ProfilsAbsence;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.repositories.JournalRepo;
import com.veviosys.vdigit.repositories.UserRepository;
import com.veviosys.vdigit.repositories.profilsAbsenceRepo;

@Service
public class ProfilAbsenceService {

    @Autowired
    profilsAbsenceRepo absenceRepo;
    @Autowired
    userService us;
    @Autowired
    UserRepository userRepository;
    @Autowired
    JournalRepo journalRepo;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    mailService serviceMail;

    User getMaster(User u) {
        if (Objects.nonNull(u.getMaster())) {
            return u.getMaster();
        }
        return u;
    }

    public User getmst() {
        CostumUserDetails user = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return user.getUser();
    }

    public List<ProfilsAbsence> checkProfile(Long id_user, String dateFin, String dateDebut) throws ParseException {

        return absenceRepo.findAll(new Specification<ProfilsAbsence>() {

            @Override
            @Nullable
            public Predicate toPredicate(Root<ProfilsAbsence> root, CriteriaQuery<?> query,
                    CriteriaBuilder criteriaBuilder) {

                Predicate p = criteriaBuilder.conjunction();

                p = criteriaBuilder.equal(root.get("user").get("userId"), id_user);

                p = criteriaBuilder.and(p, criteriaBuilder.greaterThanOrEqualTo(root.get("dateFin"), dateDebut));

                return p;
            }
        });

    }

    
    public HashMap<String, String> addProfilAbs(ProfilsAbsenceClass pac) {

        HashMap<String, String> response = new HashMap<>();
        List<ProfilsAbsence> exist;
        try {

            exist = checkProfile(pac.idSup, pac.dateFin, pac.dateDebut);

            if (exist.size() == 0) {

                ProfilsAbsence pa = new ProfilsAbsence();
                pa.setDateDebut(pac.dateDebut);
                pa.setDateFin(pac.dateFin);
                
                
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
                SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
                
                Date dateDebut = formatter.parse(pac.dateDebut);
                Date dateFin = formatter.parse(pac.dateFin);
                
                String stringdateDebut = formatter1.format(dateDebut);
                String stringdateFin = formatter1.format(dateFin);
         
                User u1 = us.connectedUser();
                User master = getMaster(u1);
                pa.setMaster(master);
                User u2 = userRepository.findById(pac.idSup).orElse(null);
                pa.setUserName(u1.getFullName());
                pa.setSupName(u2.getFullName());
                pa.setSupleant(u2);
                pa.setUser(u1);
                absenceRepo.save(pa);
               
                String name = u1.getSexe().equals("M") ? "M " +" "+ u1.getFullName() : "Mme " +" "+ u1.getFullName();
                if(u1.getSexe().equals("")) {
                    name = "M/Mme";
                }
                try {
                    serviceMail.notifyProfilAbs(name, name, u2.getEmail(),
                            stringdateDebut, stringdateFin);
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
                
                
                if (Objects.nonNull(u1.getParent())) {
                    User parent = userRepository.findById(u1.getParent()).orElse(null);
                    
                    String supplentName = u2.getSexe().equals("M") ? "M " +" "+ u2.getFullName() : "Mme " +" "+ u2.getFullName();
                    
                
                    try {
                        
                        serviceMail.notifyProfilAbsParent(name, name,supplentName, parent.getEmail(),
                                pac.dateDebut, pac.dateFin);
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if (master.getSecLevel() == 3) {
                    Journal j = new Journal();
                    String a = "";
                    j.setUser(u1);
                    j.setDate(new Date());
                    j.setTypeEv("Système/Ajout");
                    j.setMode("A");
                    a = " Utilisateur : " + pa.getUser().getFullName() + " | Suppléant : "
                            + pa.getSupleant().getFullName()
                            + " | Date début : " + pa.getDateDebut() + " | Date fin : " + pa.getDateFin();
                    j.setComposante("Profil d'absence");

                    j.setAction(a);
                    j.setMaster(master);

                    journalRepo.save(j);
                }

                response.put("message", "ok");

                return response;
            } else {
                response.put("message", "no");

                return response;
            }

        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            response.put("message", "no");

            return response;
        }

    }

    public Page<ProfilsAbsence> gectAbsList(Pageable pageable) {
        return absenceRepo.findByUserUserId(us.connectedUser().getUserId(), pageable);
    }
    
    public List<User> getProfileAbsBysuplent(long id) {
        List<User> users = new ArrayList<User>();
        List<ProfilsAbsence> profilAbs = absenceRepo.findBySupleant(id);
        for (ProfilsAbsence profil : profilAbs ) {
            users.add(profil.getUser());
        }
        return users;
    }

    public HashMap<String, String> editAbsProfil(ProfilsAbsenceClass pac) {
        User u1 = us.connectedUser();
        User master = getMaster(u1);
        HashMap<String, String> response = new HashMap<>();
        List<ProfilsAbsence> exist;
        try {

            exist = checkProfile(pac.idSup, pac.dateFin, pac.dateDebut);
     
            System.out.println("test : " + exist.size());
            if (exist.size() == 0) {

                ProfilsAbsence pa = absenceRepo.findById(pac.id).orElse(null);

                if (master.getSecLevel() == 3) {
                    Journal j = new Journal();
                    String a = "";

                    j.setUser(u1);
                    j.setDate(new Date());
                    j.setTypeEv("Système/Modification");
                    j.setMode("M");
                    a = " Utilisateur : " + pa.getUser().getFullName() + " --> " + u1.getFullName() + " | Suppléant : "
                            + pa.getSupleant().getFullName() + " --> " + pa.getSupleant().getFullName()
                            + " | Date début : "
                            + pa.getDateDebut() + " --> " + pac.dateDebut + " | Date fin : " + pa.getDateFin() + " --> "
                            + pac.dateFin;
                    j.setComposante("Profil d'absence");

                    j.setAction(a);
                    j.setMaster(master);

                    journalRepo.save(j);
                }
                pa.setUserName(u1.getFullName());

                pa.setUser(u1);

                pa.setDateDebut(pac.dateDebut);
                pa.setDateFin(pac.dateFin);
                absenceRepo.save(pa);

                response.put("message", "ok");

                return response;
            } else {
                response.put("message", "no");

                return response;
            }

        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            response.put("message", "no");

            return response;
        }

    }

    public List<User> getAllUsers() {
        User u1 = us.connectedUser();
        User master = getMaster(u1);
        return userRepository.findByMasterUserIdAndUserIdNotLike(master.getUserId(), u1.getUserId());
    }

}
