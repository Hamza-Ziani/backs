package com.veviosys.vdigit.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.swing.filechooser.FileSystemView;
import javax.transaction.Transactional;
import javax.xml.bind.DatatypeConverter;
import javax.persistence.criteria.Predicate;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.veviosys.vdigit.classes.AttributeClass;
import com.veviosys.vdigit.classes.GroupClass;
import com.veviosys.vdigit.classes.NatureClass;
import com.veviosys.vdigit.classes.PermissionClass;
import com.veviosys.vdigit.classes.ProcessRequest;
import com.veviosys.vdigit.classes.ProfilsAbsenceClass;
import com.veviosys.vdigit.classes.StorageDetail;
import com.veviosys.vdigit.classes.emetteurClass;
import com.veviosys.vdigit.classes.mapSearch;
import com.veviosys.vdigit.configuration.MySessionRegistry;
import com.veviosys.vdigit.controllers.PermissionCourrier;
import com.veviosys.vdigit.emailConf.Genpw;
import com.veviosys.vdigit.exceptions.StorageConfig.ExpireException;
import com.veviosys.vdigit.exceptions.StorageConfig.InvalidException;
import com.veviosys.vdigit.exceptions.StorageConfig.NotFoundException;
import com.veviosys.vdigit.exceptions.StorageConfig.UsedPathException;
import com.veviosys.vdigit.models.Attribute;
import com.veviosys.vdigit.models.Client;
import com.veviosys.vdigit.models.Contact;
import com.veviosys.vdigit.models.Document;
import com.veviosys.vdigit.models.DocumentType;
import com.veviosys.vdigit.models.ElementTypeGroup;
import com.veviosys.vdigit.models.Etape;
import com.veviosys.vdigit.models.FolderType;
import com.veviosys.vdigit.models.Groupe;
import com.veviosys.vdigit.models.Journal;
import com.veviosys.vdigit.models.MasterConfig;
import com.veviosys.vdigit.models.Nature;
import com.veviosys.vdigit.models.NotifMail;
import com.veviosys.vdigit.models.Password;
import com.veviosys.vdigit.models.PermissionDocument;
import com.veviosys.vdigit.models.PermissionDocumentType;
import com.veviosys.vdigit.models.PermissionGroup;
import com.veviosys.vdigit.models.PermissionGroupN;
import com.veviosys.vdigit.models.PermissionNatureCourrier;
import com.veviosys.vdigit.models.Processus;
import com.veviosys.vdigit.models.ProfilsAbsence;
import com.veviosys.vdigit.models.ReportAttributesConfig;
import com.veviosys.vdigit.models.Role;
import com.veviosys.vdigit.models.Sender;
import com.veviosys.vdigit.models.StorageVolumeRequest;
import com.veviosys.vdigit.models.SupportTech;
import com.veviosys.vdigit.models.TempSession;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.models.delayMail;
import com.veviosys.vdigit.models.receiver;
import com.veviosys.vdigit.models.pk.PermissionDocumentTypePK;
import com.veviosys.vdigit.models.pk.PermissionGroupPK;
import com.veviosys.vdigit.models.pk.PermissionNatureCourrierPK;
import com.veviosys.vdigit.reposietories.AttributeRepo;
import com.veviosys.vdigit.repositories.ClientRepo;
import com.veviosys.vdigit.repositories.ContactRepo;
import com.veviosys.vdigit.repositories.DocumentRepo;
import com.veviosys.vdigit.repositories.DocumentTypeRepo;
import com.veviosys.vdigit.repositories.EtapeRepo;
import com.veviosys.vdigit.repositories.FolderRepo;
import com.veviosys.vdigit.repositories.FolderTypeRepo;
import com.veviosys.vdigit.repositories.GroupRepo;
import com.veviosys.vdigit.repositories.JournalRepo;
import com.veviosys.vdigit.repositories.MasterConfigRepository;
import com.veviosys.vdigit.repositories.NatureRepo;
import com.veviosys.vdigit.repositories.NotifMailRepo;
import com.veviosys.vdigit.repositories.PasswordRepo;
import com.veviosys.vdigit.repositories.PermissionCourrierRepo;
import com.veviosys.vdigit.repositories.PermissionDocumentRepo;
import com.veviosys.vdigit.repositories.PermissionDocumentTypeRepo;
import com.veviosys.vdigit.repositories.PermissionGroupNRepo;
import com.veviosys.vdigit.repositories.PermissionGroupRepo;
import com.veviosys.vdigit.repositories.PermissionNatureRepo;
import com.veviosys.vdigit.repositories.ProcessusRepo;
import com.veviosys.vdigit.repositories.ReceiverRepo;
import com.veviosys.vdigit.repositories.ReportAttributesConfigRepo;
import com.veviosys.vdigit.repositories.RoleRepo;
import com.veviosys.vdigit.repositories.SenderRepo;
import com.veviosys.vdigit.repositories.StorageVolumeRequestRepo;
import com.veviosys.vdigit.repositories.SupportTechRepo;
import com.veviosys.vdigit.repositories.TempSessionRepo;
import com.veviosys.vdigit.repositories.UserRepository;
import com.veviosys.vdigit.repositories.delayMailRepo;
import com.veviosys.vdigit.repositories.profilsAbsenceRepo;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.criteria.Root;

import org.apache.logging.log4j.util.Strings;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import net.bytebuddy.utility.RandomString;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;

@Service
@Slf4j
public class masterService {

    @Autowired
    UserRepository ur;
    
    @Lazy
    @Autowired
    mailService mail;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepo rr;

    @Autowired
    private DocumentRepo dr;
    @Autowired
    private FolderRepo fr;
    @Autowired
    private FolderTypeRepo ftr;
    @Autowired
    private DocumentTypeRepo dtr;
    @Autowired
    private PermissionDocumentRepo pdr;
    @Autowired
    private PermissionDocumentTypeRepo pdtr;
    @Autowired
    private GroupRepo gr;
    @Autowired
    private PermissionGroupRepo pgr;
    @Autowired
    private PermissionGroupNRepo pgnr;
    @Autowired
    ProcessusRepo prorepo;
    @Autowired
    EtapeRepo er;
    @Autowired
    NatureRepo nr;
    @Autowired
    profilsAbsenceRepo par;
    @Autowired
    NotifMailRepo nmr;
    @Autowired
    delayMailRepo dmr;
    @Autowired
    PermissionCourrierRepo pcr;
    @Autowired
    PermissionNatureRepo pnr;
    @Autowired
    ReceiverRepo receiverRepo;
    @Autowired
    SenderRepo senderRepo;
    @Autowired
    SupportTechRepo supportTechRepo;
    @Autowired
    MasterConfigRepository configRepository;
    @Autowired
    ContactRepo cr;
    @Autowired
    JournalRepo jre;
    @Autowired
    AttributeRepo attributeRepo;
    @Autowired
    MasterConfigService configService;
    @Autowired
    ProfilAbsenceService profilAbsenceService;
    @Value("${support.mail}")
    private String supportTech;
    @Value("${storage.reqeust.validation}")
    private int validationTime;
    @Value("${tentative.number}")
    int tentative;
    @Autowired
    ReportAttributesConfigRepo racr;
    @Autowired
    StorageVolumeRequestRepo storageVolumeRequestRepo;

    public User getmst() {
        CostumUserDetails user = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return user.getUser();
    }

    // CREATE NEW USER
    public ResponseEntity<User> addUser(User us, String sx, String mat)
            throws AddressException, MessagingException, IOException {
        if (ur.findByEmailIgnoreCase(us.getEmail()) != null) {
            return new ResponseEntity(HttpStatus.GONE);

        }
        if (ur.findByMasterAndMatIgnoreCase(getmst(), mat) != null) {
            return new ResponseEntity(HttpStatus.IM_USED);

        }
        if (ur.findByMasterAndFullNameIgnoreCase(getmst(), us.getFullName()) != null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        }
        if (ur.findByUsernameIgnoreCase(us.getUsername()) != null) {
            return new ResponseEntity(HttpStatus.HTTP_VERSION_NOT_SUPPORTED);

        }
        Genpw gpw = new Genpw();
        // Password pw = new Password();
        String thePw = gpw.gennewpw();
        gpw.setEmailTo(us.getEmail());
        gpw.setSubject("Bienvenue sur documania courrier");
        // pw.setGeneratedPassword(passwordEncoder.encode(thePw));
        // pw.setPassword(passwordEncoder.encode(thePw));
        // pw.setIsValid(1);
        us.setPassword(passwordEncoder.encode(thePw));
        us.setSexe(sx);
        us.setRegistrationDate(new Date());
        us.setIsValid(1);
        
        us.setSecondary(0);
        us.setParent(Objects.nonNull(us.getParent()) ? us.getParent() : 0L);
        us.setIsValid(1);
        us.setTentativeNumber(0);

        us.setMaster(getmst());
        // us.setCategory(getmst().getCategory());
        us.setRoles(rr.findByRole("USER"));
        Set<Role> d = rr.findByRole("USER");
        for (Role role : d) {
            System.out.println(role.getRole() + role.getId());
        }
        us.setMat(mat);
        if (Objects.isNull(us.getParent())) {
            us.setParent(0L);
        }

        String name;

        if (us.getFullName().split(" ").length > 1) {
            name = us.getFullName().split(" ")[1];
        } else {
            name = us.getFullName();
        }

        name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        mail.send(gpw, thePw, us.getUsername(), sx, name);
        System.out.println(us.getUsername());
        ur.save(us);
        return new ResponseEntity<User>(us, HttpStatus.OK);

    }

    // RESETE USER PASSWORD
    public ResponseEntity resetPassword(String email)
            throws AddressException, MessagingException, IOException {
        // .println(email);
        User us = ur.findByEmailIgnoreCase(email);
        if (Objects.nonNull(us)) {
            if (us.getFromLdap() == 0) {
                Genpw gpw = new Genpw();
                String thePw = gpw.gennewpw();
                gpw.setEmailTo(email);
                gpw.setSubject("Récupération de mot de passe : ");

                us.setPassword(passwordEncoder.encode(thePw));

                mail.resetpw(gpw, thePw, us.getUsername(), us.getSexe(), us.getFullName().split(" ")[1]);

                if (ur.getOne(connectedUserMaster(us.getUserId())).getSecLevel() == 3) {
                    Journal j = new Journal();

                    j.setUser(us);
                    j.setDate(new Date());
                    j.setAction("Récupération de mot de passe");
                    j.setMaster(ur.getOne(connectedUserMaster(us.getUserId())));

                    j.setTypeEv("Récupération");

                    jr.save(j);
                }
                return new ResponseEntity(HttpStatus.OK);
            }

        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
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

    // EDIT USER PASSWORD
    public ResponseEntity editPw(String fpw, String npw, String secondary) {
        User us = getmst();
        // Password P = us.getPw();
        String ffpw = us.getPassword();
        // .println(passwordEncoder.matches(fpw, ffpw));
        if (passwordEncoder.matches(fpw, ffpw)) {
            us.setPassword(passwordEncoder.encode(npw));
            ur.save(us);
            // pr.save(P);
            if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
                Journal j = new Journal();

                j.setUser(connectedUser());
                j.setDate(new Date());
                j.setAction("Modification de mot de passe");
                j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));
                if (secondary.equals("true")) {

                    User user = ur.getSecondaryUser(connectedUser().getUserId());

                    j.setTypeEv("Modification/Secondaire Profil");

                    if (Objects.nonNull(user)) {
                        j.setConnectedSacondaryName(user.getFullName());
                    }

                } else {
                    j.setTypeEv("Modification");
                }
                jr.save(j);
            }
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        }
    }

    // GET USERS BY MASTER (Pagination)
    public Page<User> findUsersByMaster(Pageable pageable) {
        User us = getmst();
        List<User> users = new ArrayList<User>();

        users = ur.findUserByMaster(us.getUserId());
        /*
         * for (int i = 0; i <users.size(); i++) {
         * //.println("ALL"+users.get(i).getIsClient());
         * 
         * if(users.get(i).getIsClient()!=null) {
         * 
         * users.remove(i);
         * 
         * } }
         */

        int start = (int) pageable.getOffset();
        int end = (int) (start + pageable.getPageSize()) > users.size() ? users.size()
                : (start + pageable.getPageSize());
        Page<User> pages = new PageImpl<User>(users.subList(start, end), pageable, users.size());
        return pages;

    }

    // REMOVE USER
    @Transactional
    public ResponseEntity deleteUser(Long us, String secondary) {
        try {

            dr.changeToMaster(getmst().getUserId(), us);
            fr.changeToMaster(getmst().getUserId(), us);

            String name = ur.findById(us).orElse(null).getFullName();

            User usere = ur.findById(us).get();

            usere.setSecondary(0);

            ur.save(usere);
            jre.deleteByUser(usere);
            ur.delete(ur.findById(us).orElse(null));

            if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
                Journal j = new Journal();

                j.setUser(connectedUser());
                j.setDate(new Date());
                j.setAction("Suppression de l'utilisateur (" + name + ")");
                j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));
                if (secondary.equals("true")) {

                    User user = ur.getSecondaryUser(connectedUser().getUserId());

                    j.setTypeEv("Suppression/Secondaire Profil");

                    if (Objects.nonNull(user)) {
                        j.setConnectedSacondaryName(user.getFullName());
                    }

                } else {
                    j.setTypeEv("Suppression");
                }
                jr.save(j);
            }
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // EDIT MASTER LOGO
    public User editLogo(String nom, String logo, String secondary) throws IOException {
        User us = getmst();
        if (us.getMaster() == null) {

            String imageString = logo.split(",")[1];

            // create a buffered image
            BufferedImage image = null;
            byte[] imageByte;

            imageByte = Base64.getDecoder().decode(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
            String activePath = configService.findActivePath() + "\\logo";
            final File saveInRootPath = new File(activePath);
            if (!saveInRootPath.exists()) {
                saveInRootPath.mkdirs();
            }
            String path = activePath + "\\" + us.getUserId() + ".png";
            // write the image to a file
            File outputfile = new File(path);
            ImageIO.write(image, "png", outputfile);

            try {
                configService.addNewConfig("LOGO_PATH", path);
            } catch (Exception e) {
                // TODO: handle exception
            }
            us.setLogo(logo);
            us.setNomClient(nom);
            ur.save(us);
            if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
                Journal j = new Journal();

                j.setUser(connectedUser());
                j.setDate(new Date());
                j.setAction("Modification de logo ");
                j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));
                if (secondary.equals("true")) {

                    User user = ur.getSecondaryUser(connectedUser().getUserId());

                    j.setTypeEv("Modification/Secondaire Profil");

                    if (Objects.nonNull(user)) {
                        j.setConnectedSacondaryName(user.getFullName());
                    }

                } else {
                    j.setTypeEv("Modification");
                }
                jr.save(j);
            }
            // return us;
        }
        return us;
    }

    // ADD CLIENT

    public ResponseEntity addClient(Client cl, String mail) throws AddressException, MessagingException

    {
        // CostumUserDetails user = (CostumUserDetails)
        // SecurityContextHolder.getContext().getAuthentication()
        // .getPrincipal();

        // if (user.getUser().getMaster() == null) {

        // cl.setMaster(user.getUser());
        // } else
        // cl.setMaster(user.getUser().getMaster());
        // Client clS = cltr.findByName(cl.getMaster().getUserId(), cl.getName());
        // if (clS == null) {
        // User u = new User();
        // // GENERATE USER NAME
        // String genUsername = cl.getName().replace(' ', '_') + "_";

        // Random rndm = new Random();

        // genUsername += rndm.nextInt();

        // u.setUsername(genUsername);
        // u.setEmail(mail);
        // if (getmst().getMaster() == null)
        // u.setMaster(getmst());
        // else {
        // u.setMaster(getmst().getMaster());
        // }
        // Genpw gpw = new Genpw();
        // Password pw = new Password();
        // String thePw = gpw.gennewpw();
        // gpw.setEmailTo(u.getEmail());
        // gpw.setSubject("Bienvenue sur DOCUMANIA");
        // pw.setGeneratedPassword(passwordEncoder.encode(thePw));
        // pw.setPassword(passwordEncoder.encode(thePw));
        // ur.save(u);
        // pw = pr.saveAndFlush(pw);
        // this.mail.sendCredentialToClient(cl.getName(), genUsername, gpw, thePw);
        // cl = cltr.save(cl);
        // u.setIsClient(cl.getId());
        // u.setPw(pw);
        // u= ur.saveAndFlush(u); // SEND MAIL TO USER
        // if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel()
        // == 3) {
        // Journal j = new Journal();

        // j.setUser(connectedUser());
        // j.setDate(new Date());
        // j.setTypeEv("Ajout ");
        // j.setAction("Ajout d'un nouveau utilisateur Nom ("+u.getFullName()+"), Email
        // (" + u.getEmail()
        // +"), Num Tel ("+u.getContact().getPhone()+"), Adresse
        // ("+u.getContact().getAdresse()+")"

        // );
        // j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

        // jr.save(j);
        // }
        // return new ResponseEntity(HttpStatus.OK);
        // }

        // else {

        // }

        return new ResponseEntity(HttpStatus.CONFLICT);
    }

    // EDIT ACCESS TO CLIENT DOCUMENT (Master to users)

    public ResponseEntity editAccess(Long id, int etat, String secondary) {
        User u = ur.findById(id).orElse(null);
        u.setSecondary(0);
        u.setHasAccessSecondary(etat);
        ur.save(u);
        if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
            Journal j = new Journal();

            j.setUser(connectedUser());
            j.setDate(new Date());
            j.setAction("Modification de l'accés sur flux courriers sur l'utilisateur (" + u.getFullName() + ")");
            j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));
            if (secondary.equals("true")) {

                User user = ur.getSecondaryUser(connectedUser().getUserId());

                j.setTypeEv("Modification/Secondaire Profil");

                if (Objects.nonNull(user)) {
                    j.setConnectedSacondaryName(user.getFullName());
                }

            } else {
                j.setTypeEv("Modification");
            }
            jr.save(j);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    public ResponseEntity editAccessFlux(Long id, int etat, String secondary) {
        User u = ur.findById(id).orElse(null);
        u.setHasAccessClient(etat);
        ur.save(u);
        if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
            Journal j = new Journal();

            j.setUser(connectedUser());
            j.setDate(new Date());
            j.setAction("Modification de l'accés sur flux courriers sur l'utilisateur (" + u.getFullName() + ")");
            j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));
            if (secondary.equals("true")) {

                User user = ur.getSecondaryUser(connectedUser().getUserId());

                j.setTypeEv("Modification/Secondaire Profil");

                if (Objects.nonNull(user)) {
                    j.setConnectedSacondaryName(user.getFullName());
                }

            } else {
                j.setTypeEv("Modification");
            }
            jr.save(j);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    // CREATE NEW PRIVILEGE TO DOCTYPE
    public ResponseEntity addPrivilege(PermissionClass pc, String secondary) {
        PermissionDocument pd = new PermissionDocument();
        pd.setAcces(pc.acces);
        pd.setDescription(pc.description);
        pd.setNom(pc.name);
        pd.setMaster(getmst());
        pd = pdr.save(pd);
        String ac = "";
        int b = 0;
        String t = "";
        String name = "";
        for (Long id : pc.documentType) {
            DocumentType dt = dtr.findId(id);
            PermissionDocumentType pdt = new PermissionDocumentType();
            pdt.setId(new PermissionDocumentTypePK(dt.getId(), pd.getId()));
            pdt.setPermissionDocument(pd);
            pdt.setDocumentType(dt);
            pdtr.save(pdt);
            if (b == 0) {
                t = "  " + dt.getName();
                b = 456;
            } else {
                t += " --> " + dt.getName();
            }
            // .println(t);
        }

        if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
            Journal j = new Journal();
            String a = "";
            a = " Nom : " + pd.getNom() + " | ";
            a += " Description : " + pd.getDescription() + " | Acces :  ";
            if (pd.getAcces().equals("R"))
                a += " Lecture  ";

            if (pd.getAcces().equals("RW"))
                a += " Lecture et écriture";
            if (pd.getAcces().equals("RWD"))
                a += " Lecture , écriture et suppression ";

            j.setUser(connectedUser());
            j.setDate(new Date());
            j.setMode("AP");
            j.setComposante("Privilége type document");
            if (secondary.equals("true")) {

                User user = ur.getSecondaryUser(connectedUser().getUserId());

                j.setTypeEv("Système/Ajout/Secondaire Profil");

                if (Objects.nonNull(user)) {
                    j.setConnectedSacondaryName(user.getFullName());
                }

            } else {
                j.setTypeEv("Système/Ajout");
            }
            j.setAction(a + " | Type document : " + t);
            j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

            jr.save(j);
        }
        return new ResponseEntity<>(HttpStatus.OK);

    }

    // ADD GROUPE
    @Secured({ "ROLE_MASTER" })
    public ResponseEntity addGroup(GroupClass gc, String secondary) { // gr.addUsersGroup(g.getGroupId(),id);

        Groupe g = new Groupe();
        g.setGroupName(gc.name);
        g.setUsers(new ArrayList<User>());
        g.setMaster(getmst());
        g = gr.saveAndFlush(g);
        String usr = " Utilisateurs : ";
        String perm = "";
        String permC = "";
        int a = 0;

        for (Long id : gc.users) {
            User u = ur.findById(id).orElse(null);
            gr.addUsersGroup(g.getGroupId(), id);
            if (a == 0) {
                usr += " " + u.getFullName();
                a = 456;
            } else {
                usr += " &&&& " + u.getFullName();

            }
            // g.getUsers().add(u);
        }
        a = 0;

        List<PermissionDocument> prmssion = new ArrayList<PermissionDocument>();
        for (Long permissionDocument : gc.permission) {
            PermissionDocument pd = pdr.findById(permissionDocument).orElse(null);
            prmssion.add(pd);
            if (a == 0) {
                perm += "  " + pd.getNom();
                a = 456;
            } else {
                perm += " &&&& " + pd.getNom();

            }
        }
        a = 0;
        List<PermissionCourrier> pr = new ArrayList<PermissionCourrier>();
        for (Long permissionn : gc.permissionN) {
            PermissionCourrier pd = pcr.findById(permissionn).orElse(null);
            PermissionGroupN pgn = new PermissionGroupN(new PermissionGroupPK(g.getGroupId(), permissionn), pd, g);
            if (a == 0) {
                permC += " " + pd.getNom();
                a = 456;
            } else {
                permC += " &&&& " + pd.getNom();

            }
            pgnr.save(pgn);

        }
        for (PermissionDocument permissionDocument : prmssion) {
            PermissionGroup pg = new PermissionGroup(new PermissionGroupPK(g.getGroupId(), permissionDocument.getId()),
                    permissionDocument, g);
            pgr.save(pg);
        }
        if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
            Journal j = new Journal();

            j.setUser(connectedUser());
            j.setDate(new Date());
            if (secondary.equals("true")) {

                User user = ur.getSecondaryUser(connectedUser().getUserId());

                j.setTypeEv("Système/Ajout/Secondaire Profil");
                if (Objects.nonNull(user)) {
                    j.setConnectedSacondaryName(user.getFullName());
                }

            } else {
                j.setTypeEv("Système/Ajout");
            }
            // j.setTypeEv("Ajout");
            j.setComposante("Groupe");
            j.setMode("AG");
            // j.setAction("Ajout de groupe Nom ("+g.getGroupName()+"), utilisateur(s)
            // "+usr+", privilège type document "+perm+" et privilège nature courrier
            // "+permC);
            j.setAction("Groupe : " + g.getGroupName() + "   |  " + usr + "  | Pemission document : " + perm
                    + "  | Pemission courrier : " + perm);
            j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

            jr.save(j);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    // GET DOC PERMISSION BY MASTER (ALL)
    @Secured({ "ROLE_MASTER" })
    public List<PermissionDocument> getAllPermission() {
        return pdr.findByMaster(getmst());
    }

    // GET NATURE PERMISSION BY MASTER (ALL)
    @Secured({ "ROLE_MASTER" })
    public List<PermissionCourrier> getPermNature() {
        return pcr.findByMaster(getmst());
    }

    // GET ALL GROUPS (PAGE)
    @Secured({ "ROLE_MASTER" })
    public Page<Groupe> getGroupes(Pageable pageable,String q) {
        List<Groupe> groups = gr.findByGroupNameContainingIgnoreCaseAndMaster(q,getmst());
        int start = (int) pageable.getOffset();
        int end = (int) (start + pageable.getPageSize()) > groups.size() ? groups.size()
                : (start + pageable.getPageSize());
        Page<Groupe> pages = new PageImpl<Groupe>(groups.subList(start, end), pageable, groups.size());
        return pages;
    }

    // EDIT GROUP
    @Secured({ "ROLE_MASTER" })
    public ResponseEntity editGroup(GroupClass gc) {
        try {
            Groupe g = gr.findById(gc.id).orElse(null);
            // .println(gc.id + "Name" + gc.name);

            g.setGroupName(gc.name);
            // SPLICE EXISTING USERS
            List<User> users = new ArrayList<User>();
            for (Long id : gc.users) {
                User u = ur.findById(id).orElse(null);
                users.add(u);
                // .
                if (g.getUsers().indexOf(u) == -1) {
                    System.err.println(u.getUsername());
                    gr.addUsersGroup(g.getGroupId(), id);
                }

                // g.getUsers().add(u);
            }
            for (User user : g.getUsers()) {
                // .println(user.getUserId());
                if (users.indexOf(user) == -1) {
                    gr.removeUsersGroup(g.getGroupId(), user.getUserId());
                }
            }

            // REMOVE AND ADD PERMISSIONS DOCS
            List<PermissionDocument> permissionDocuments = new ArrayList<PermissionDocument>();
            for (Long id : gc.permission) {
                PermissionGroup pg = pgr.findById(new PermissionGroupPK(g.getGroupId(), id)).orElse(null);
                PermissionDocument pd = pdr.getOne(id);
                permissionDocuments.add(pd);
                if (pg == null) {
                    pg = new PermissionGroup(new PermissionGroupPK(g.getGroupId(), id), pdr.getOne(id), g);
                    pgr.save(pg);
                }

            }
            for (PermissionGroup permissionGroup : g.getPermissionGroup()) {
                if (permissionDocuments.indexOf(permissionGroup.getPermissionDocument()) == -1) {
                    // .println(permissionGroup.getPermissionDocument().getId());
                    pgr.delete(permissionGroup);

                }

            }
            // ADD PERMISSIONS Nature

            List<PermissionCourrier> permissionNatures = new ArrayList<PermissionCourrier>();
            for (Long permissionn : gc.permissionN) {
                PermissionCourrier pd = pcr.findById(permissionn).orElse(null);
                PermissionGroupN pgn = pgnr.findById(new PermissionGroupPK(g.getGroupId(), permissionn)).orElse(null);
                permissionNatures.add(pd);
                if (pgn == null) {
                    pgn = new PermissionGroupN(new PermissionGroupPK(g.getGroupId(), permissionn), pd, g);
                    pgnr.save(pgn);
                }
            }
            // Remove PERMISSIONS Nature
            for (PermissionGroupN permissionGroupN : g.getPermissionGroupN()) {
                if (permissionNatures.indexOf(permissionGroupN.getPermissionCourrier()) == -1) {
                    pgnr.delete(permissionGroupN);
                }
            }
            gr.save(g);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }

    // DELETE GROUP
    @Secured({ "ROLE_MASTER" })
    public ResponseEntity deleteGroup(Long id) {
        Groupe g = gr.findById(id).orElse(null);
        //
        //
        for (User u : g.getUsers()) {
            gr.removeUsersGroup(g.getGroupId(), u.getUserId());
        }
        for (PermissionGroup pg : g.getPermissionGroup()) {
            pgr.delete(pg);
        }
        for (PermissionGroupN pg : g.getPermissionGroupN()) {
            pgnr.delete(pg);
        }
        gr.delete(g);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // CREATE NEW PROCESS
    @Secured({ "ROLE_MASTER" })
    public Processus addProcess(String name, String md) {
        Processus p = new Processus();
        p.setDateCreation(new Date());
        p.setMaster(getmst());
        p.setModel(md);
        p.setName(name);
        p = prorepo.saveAndFlush(p);

        return p;
    }

    @Secured({ "ROLE_MASTER" })
    // Edit process
    public ResponseEntity editProcess(Long id, ProcessRequest processRequest) {
        Processus p = prorepo.findById(id).orElse(null);
        // p.setName(name);
        /** CREATING NEW VERSION */
        Processus editedProc = new Processus();
        editedProc.setDateCreation(new Date());
        editedProc.setMaster(getmst());
        editedProc.setModel(p.getModel());
        editedProc.setName(p.getName());
        editedProc.setParent(id);
        editedProc = prorepo.saveAndFlush(editedProc);
        /** Set old steps to new version */
        setNewProcess(p.getEtapes(), editedProc, false);

        p.setModel(processRequest.getModel());
        p.setName(p.getName());
        p = prorepo.saveAndFlush(p);
        setNewProcess(processRequest.getEtapes(), p, true);

        return new ResponseEntity(HttpStatus.OK);

    }

    public void setNewProcess(List<Etape> steps, Processus pro, boolean newSteps) {

        if (newSteps) {
            int i = 1;
            for (Etape cc : steps) {
                Etape e = new Etape();

                List<User> users = new ArrayList<User>();
                e.setCommentaire("");
                e.setMaster(getmst());
                e.setSignataire(cc.getSignataire());
                String st = "";
                if (cc.getSignataire() == 1) {
                    st = "Oui";
                } else {
                    st = "Non";
                }

                e.setNumero(i);
                i++;
                e.setDelai(cc.getDelai());
                e.setQuality(cc.getQuality());
                e.setEtat(0);
                e.setName(cc.getName());
                e.setDelaiRet(cc.getDelaiRet());
                e.setProcessus(pro);
                e = er.saveAndFlush(e);
                int ind = 0;
                for (User us : cc.getUsers()) {

                    User u = ur.findByMasterAndFullNameIgnoreCase(connectedUser(), us.getFullName());
                    if (connectedUser().getMaster() == null && us.getFullName().equals(connectedUser().getFullName())) {
                        u = new User();
                        u = connectedUser();
                    }
                    // User u =ur.getOne();
                    // .println(Objects.isNull(u));

                    er.addUserEtape(u.getUserId(), e.getId());

                }

            }
        } else {
            for (Etape step : steps) {
                step.setProcessus(pro);
                er.saveAndFlush(step);
            }
        }

    }

    // CREATE STEPS OF PROCESS
    @Secured({ "ROLE_MASTER" })
    public ResponseEntity addEtape(List<Etape> etapes, Long id, String secondary) {
        int i = 1;

        Processus p = prorepo.findById(id).orElse(null);
        String a = p.getName() + " --> ";
        int indx = 0;
        for (Etape cc : etapes) {
            Etape e = new Etape();

            List<User> users = new ArrayList<User>();
            e.setCommentaire("");
            e.setMaster(getmst());
            e.setSignataire(cc.getSignataire());
            String st = "";
            if (cc.getSignataire() == 1) {
                st = "Oui";
            } else {
                st = "Non";
            }
            if (indx == 0) {
                a += "  " + cc.getName() + " && " + cc.getDelai() + " && " + cc.getDelaiRet() + " && " + st + " ~ ";
                indx = 456;
            } else {
                a += " ::: " + cc.getName() + " && " + cc.getDelai() + " && " + cc.getDelaiRet() + " && " + st + " ~ ";
            }
            e.setNumero(i);
            i++;
            e.setDelai(cc.getDelai());
            e.setQuality(cc.getQuality());
            e.setEtat(0);
            e.setQuality(cc.getQuality());
            e.setName(cc.getName());
            e.setDelaiRet(cc.getDelaiRet());
            e.setProcessus(p);
            e = er.saveAndFlush(e);
            int ind = 0;
            for (User us : cc.getUsers()) {

                User u = ur.findByMasterAndFullNameIgnoreCase(connectedUser(), us.getFullName());
                if (connectedUser().getMaster() == null && us.getFullName().equals(connectedUser().getFullName())) {
                    u = new User();
                    u = connectedUser();
                }
                // User u =ur.getOne();
                // .println(Objects.isNull(u));
                if (ind == 0) {
                    a += us.getFullName();
                    ind = 33;
                } else {
                    a += " // " + us.getFullName();
                }

                er.addUserEtape(u.getUserId(), e.getId());

            }
            if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
                Journal j = new Journal();

                j.setUser(connectedUser());
                j.setDate(new Date());
                j.setMode("AE");
                j.setComposante("Processus");
                j.setAction(a);
                if (secondary.equals("true")) {

                    User user = ur.getSecondaryUser(connectedUser().getUserId());

                    j.setTypeEv("Système/Ajout/Secondaire Profil");

                    if (Objects.nonNull(user)) {
                        j.setConnectedSacondaryName(user.getFullName());
                    }

                } else {
                    j.setTypeEv("Système/Ajout");
                }
                j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

                jr.save(j);
            }
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    // GET PROCESS Versions (PAGE)
    public Page<Processus> findByParent(Long idParent, Pageable pageable) {
        return prorepo.findByParent(idParent, pageable);
    }

    // EDIT STEPS
    @Secured({ "ROLE_MASTER" })
    public ResponseEntity editEtape(List<Etape> etapes, Long id) {

        return null;
    }

    // GET PROCESS BY MASTER (PAGE)
    public Page<Processus> getAllProcess(Pageable pageable) {
        return prorepo.findByMasterUserIdAndParentIsNull(getmst().getUserId(), pageable);
    }

    // GET PROCESS BY MASTER (ALL)

    public List<Processus> getAllProcessList() {
        return prorepo.findByMasterUserIdAndParentIsNull(getmst().getUserId());

    }

    // ADD NEW NATURE
    @Secured({ "ROLE_MASTER" })
    public ResponseEntity addNature(NatureClass n, String secondary) {

        Nature ntr = new Nature();
        Processus p = prorepo.findById(n.process).orElse(null);

        ntr.setProcess(p);
        ntr.setMaster(getmst());
        ntr.setName(n.name);
        ntr.setProcName(p.getName());
        ntr.setFolderType(n.type);
        // String name=ft.getName();
        // if(name.equals("Courrier départ")) {
        // ntr.setLibelle("affc");
        // }else if(name.equals("Courrier arrivé")) {
        // ntr.setLibelle("ent");
        // }
        // else {

        // ntr.setLibelle("nat");
        // }

        nr.save(ntr);
        if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
            Journal j = new Journal();
            String a = "";

            j.setUser(connectedUser());
            j.setDate(new Date());
            j.setMode("A");
            a += "Nom : " + n.name + " | Processus : " + p.getName();
            j.setComposante("Nature");
            if (secondary.equals("true")) {

                User user = ur.getSecondaryUser(connectedUser().getUserId());

                j.setTypeEv("Système/Ajout/Secondaire Profil");
                if (Objects.nonNull(user)) {
                    j.setConnectedSacondaryName(user.getFullName());
                }

            } else {
                j.setTypeEv("Système/Ajout");
            }
            j.setAction(a);
            j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

            jr.save(j);
        }
        return new ResponseEntity(HttpStatus.OK);

    }

    // EDIT NATURE COURRIER
    @Secured({ "ROLE_MASTER" })
    public ResponseEntity editNature(NatureClass n, String secondary) {
        Nature ntr = nr.findById(n.id).orElse(null);
        Processus p = prorepo.findById(n.process).orElse(null);
        if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
            Journal j = new Journal();
            String a = "";

            j.setUser(connectedUser());
            j.setDate(new Date());

            j.setMode("M");
            a += "Nom : " + ntr.getName() + " --> " + " | Processus : " + ntr.getProcess().getName() + " --> "
                    + p.getName();
            j.setComposante("Nature");
            if (secondary.equals("true")) {

                User user = ur.getSecondaryUser(connectedUser().getUserId());

                j.setTypeEv("Système/Modification/Secondaire Profil");
                if (Objects.nonNull(user)) {
                    j.setConnectedSacondaryName(user.getFullName());
                }

            } else {
                j.setTypeEv("Système/Modification");
            }
            j.setAction(a);
            j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

            jr.save(j);
        }
        ntr.setProcess(p);
        ntr.setName(n.name);
        ntr.setFolderType(n.type);
        System.out.println("tppppppppp" + n.type);
        ntr.setProcName(p.getName());
        nr.save(ntr);

        return new ResponseEntity(HttpStatus.OK);
    }

    // DELETE NATURE
    public ResponseEntity deleteNature(Long id, String secondary) {
        try {
            Nature n = nr.getOne(id);
            if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
                Journal j = new Journal();
                String a = "";

                j.setUser(connectedUser());
                j.setDate(new Date());

                j.setMode("S");
                a += "Nom : " + n.getName() + " | Processus : " + n.getProcess().getName();
                j.setComposante("Nature");
                if (secondary.equals("true")) {

                    User user = ur.getSecondaryUser(connectedUser().getUserId());

                    j.setTypeEv("Système/Suppression/Secondaire Profil");
                    if (Objects.nonNull(user)) {
                        j.setConnectedSacondaryName(user.getFullName());
                    }

                } else {
                    j.setTypeEv("Système/Suppression");
                }
                j.setAction(a);
                j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

                jr.save(j);
            }
            nr.delete(n);
            return new ResponseEntity(HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);

        }

    }

    // Get users and master
    public List<User> getAllUsers() {
        return ur.findAllWithMaster(getmst().getUserId());
    }

    // LIST D'absence
    // LIST D'absence
    @Autowired
    JournalRepo jr;

    // GET JOURNAL BY PAGE AND DATE
    public Page<Journal> getJournals(Pageable pageable, Date d1, Date d2) {

        List<Journal> list = jr.findByDateMaster(getmst().getUserId(), d1, d2);
        int start = (int) pageable.getOffset();
        int end = (int) (start + pageable.getPageSize()) > list.size() ? list.size() : (start + pageable.getPageSize());
        Page<Journal> pages = new PageImpl<Journal>(list.subList(start, end), pageable, list.size());
        return pages;
        // return jr.findByDateMaster(getmst().getUserId(),d1, d2,pageable );
    }
    // GET Profil abs by page

    public Page<ProfilsAbsence> gectAbsList(Pageable pageable) {
        // List<ProfilsAbsence> list = par.findByMaster(getmst());
        // int start = (int) pageable.getOffset();
        // int end = (int) (start + pageable.getPageSize()) > list.size() ? list.size()
        // : (start + pageable.getPageSize());
        // Page<ProfilsAbsence> pages = new PageImpl<ProfilsAbsence>(list.subList(start,
        // end), pageable, list.size());
        return par.findByMasterUserId(getmst().getUserId(), pageable);
    }

    // ADD NEW PROFIL ABS
    @Secured({ "ROLE_MASTER" })
    public HashMap<String, String> addProfilAbs(ProfilsAbsenceClass pac, String secondary) {

        HashMap<String, String> response = new HashMap<>();
        List<ProfilsAbsence> exist;
        try {
            exist = profilAbsenceService.checkProfile(pac.idSup, pac.dateFin, pac.dateDebut);
            System.out.println("size : " + pac.idUser);
            System.out.println("dd : " + ur.findById(pac.idUser).get().getFullName());
            if (exist.size() == 0) {
                ProfilsAbsence pa = new ProfilsAbsence();
                pa.setDateDebut(pac.dateDebut);
                pa.setDateFin(pac.dateFin);
                pa.setMaster(getmst());
                User u1 = ur.findById(pac.idUser).orElse(null);
                User u2 = ur.findById(pac.idSup).orElse(null);
                pa.setUserName(u1.getFullName());
                pa.setSupName(u2.getFullName());
                pa.setSupleant(u2);
                pa.setUser(u1);
                par.save(pa);
                if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
                    Journal j = new Journal();
                    String a = "";

                    j.setUser(connectedUser());
                    j.setDate(new Date());
                    j.setMode("A");
                    a = " Utilisateur : " + pa.getUser().getFullName() + " | Suppléant : "
                            + pa.getSupleant().getFullName()
                            + " | Date début : " + pa.getDateDebut() + " | Date fin : " + pa.getDateFin();
                    j.setComposante("Profil d'absence");
                    if (secondary.equals("true")) {

                        User user = ur.getSecondaryUser(connectedUser().getUserId());

                        j.setTypeEv("Système/Ajout/Secondaire Profil");
                        if (Objects.nonNull(user)) {
                            j.setConnectedSacondaryName(user.getFullName());
                        }

                    } else {
                        j.setTypeEv("Système/Ajout");
                    }
                    j.setAction(a);
                    j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

                    jr.save(j);
                }

                response.put("message", "ok");

                return response;
            } else {
                response.put("message", "no");

                return response;
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            response.put("message", "no");

            return response;

        }

    }

    // ADD OR EDIT NOTIF MAIL
    @Secured({ "ROLE_MASTER" })
    public ResponseEntity addNotifMail(NotifMail notifMail, String secondary) throws MessagingException {
        try {
            // TEST MAIL IF IS VALID
            System.out.println(notifMail.getSsl());
            mail.mailNotifTest(notifMail.getMail(), notifMail.getPassword(), notifMail.getHost(), notifMail.getPort());
            NotifMail notifMail2 = nmr.findByUserUserId(connectedUser().getUserId());
            User u = getmst();
            // EDIT
            if (Objects.nonNull(notifMail2)) {
                if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
                    Journal j = new Journal();
                    String a = "";

                    j.setUser(connectedUser());
                    j.setDate(new Date());
                    j.setMode("M");
                    a += "Email : " + notifMail2.getMail() + " --> " + notifMail.getMail() + " | Port : "
                            + notifMail2.getPort() + " --> " + notifMail.getPort() + " | Host : " + notifMail2.getHost()
                            + " --> " + notifMail.getHost();
                    j.setComposante("Notification");
                    if (secondary.equals("true")) {

                        User user = ur.getSecondaryUser(connectedUser().getUserId());

                        j.setTypeEv("Système/Modification/Secondaire Profil");
                        if (Objects.nonNull(user)) {
                            j.setConnectedSacondaryName(user.getFullName());
                        }

                    } else {
                        j.setTypeEv("Système/Modification");
                    }
                    j.setAction(a);
                    j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

                    jr.save(j);
                }
                notifMail2.setHost(notifMail.getHost());
                notifMail2.setPort(notifMail.getPort());
                notifMail2.setMail(notifMail.getMail());

                notifMail2.setPassword(notifMail.getPassword());
                notifMail2 = nmr.saveAndFlush(notifMail2);
                u.setNotifMail(notifMail2);
            }

            // CREATE
            else {

                notifMail.setUser(getmst());
                notifMail = nmr.saveAndFlush(notifMail);
                u.setNotifMail(notifMail);
                if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
                    Journal j = new Journal();
                    String a = "";

                    j.setUser(connectedUser());
                    j.setDate(new Date());

                    j.setMode("A");
                    a += "Email : " + notifMail.getMail() + " | Port : " + notifMail.getPort() + " | Host : "
                            + notifMail.getHost();
                    j.setComposante("Notification");
                    if (secondary.equals("true")) {

                        User user = ur.getSecondaryUser(connectedUser().getUserId());

                        j.setTypeEv("Système/Ajout/Secondaire Profil");
                        if (Objects.nonNull(user)) {
                            j.setConnectedSacondaryName(user.getFullName());
                        }

                    } else {
                        j.setTypeEv("Système/Ajout");
                    }
                    j.setAction(a);
                    j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

                    jr.save(j);
                }
            }
            ur.save(u);

        } catch (AuthenticationFailedException e) {

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    // ADD OR EDIT DELAY SEND MAILS CONFIG
    @Secured({ "ROLE_MASTER" })
    public ResponseEntity addConfigDelay(delayMail dm, String secondary) {

        User u = getmst();
        delayMail delayMail = dmr.findByUserUserId(u.getUserId());
        if (Objects.isNull(delayMail)) {
            delayMail = new delayMail();
            delayMail.setDelayPerDay(dm.getDelayPerDay());
            delayMail.setDelaySup(dm.getDelaySup());
            delayMail.setDelayUs(dm.getDelayUs());
            delayMail = dmr.saveAndFlush(delayMail);
            if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
                Journal j = new Journal();
                String a = "";

                j.setUser(connectedUser());
                j.setDate(new Date());
                j.setMode("A");
                a += "Délai utilisateur : " + delayMail.getDelayUs() + " | Délai supérieur : " + delayMail.getDelaySup()
                        + " | Nombre de relance : " + delayMail.getDelayPerDay();
                j.setComposante("Relance");
                if (secondary.equals("true")) {

                    User user = ur.getSecondaryUser(connectedUser().getUserId());

                    j.setTypeEv("Système/Ajout/Secondaire Profil");
                    if (Objects.nonNull(user)) {
                        j.setConnectedSacondaryName(user.getFullName());
                    }

                } else {
                    j.setTypeEv("Système/Ajout");
                }
                j.setAction(a);
                j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

                jr.save(j);
            }
            u.setDelayMail(delayMail);
        } else {
            if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
                Journal j = new Journal();
                String a = "";

                j.setUser(connectedUser());
                j.setDate(new Date());

                j.setMode("M");
                a += "Délai utilisateur : " + delayMail.getDelayUs() + " --> " + dm.getDelayUs()
                        + " | Délai supérieur : " + delayMail.getDelaySup() + " --> " + dm.getDelaySup()
                        + " | Nombre de relance : " + delayMail.getDelayPerDay() + " --> " + dm.getDelayPerDay();
                j.setComposante("Relance");
                if (secondary.equals("true")) {

                    User user = ur.getSecondaryUser(connectedUser().getUserId());

                    j.setTypeEv("Système/Modification/Secondaire Profil");
                    if (Objects.nonNull(user)) {
                        j.setConnectedSacondaryName(user.getFullName());
                    }

                } else {
                    j.setTypeEv("Système/Modification");
                }
                j.setAction(a);
                j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

                jr.save(j);
            }
            delayMail.setDelayPerDay(dm.getDelayPerDay());
            delayMail.setDelaySup(dm.getDelaySup());
            delayMail.setDelayUs(dm.getDelayUs());
            delayMail.setUser(getmst());
            delayMail = dmr.saveAndFlush(delayMail);
            u.setDelayMail(delayMail);
        }
        ur.save(u);

        return new ResponseEntity(HttpStatus.OK);

    }

    // DELETE PROCESS
    @Secured({ "ROLE_MASTER" })
    public ResponseEntity deleteProcess(Long id, String secondary) {
        try {
            Processus p = prorepo.findById(id).orElse(null);
            String name = p.getName();
            if (p.getNatures().size() == 0) {
                for (Etape etp : p.getEtapes()) {
                    // .println(etp.getId());
                    er.deleteUserEtape(etp.getId());
                    er.delete(etp);

                }

            }
            prorepo.delete(p);
            if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
                Journal j = new Journal();
                String a = "";

                j.setUser(connectedUser());
                j.setDate(new Date());

                j.setMode("A");
                a = " Processus : " + name;
                j.setComposante("Processus");
                if (secondary.equals("true")) {

                    User user = ur.getSecondaryUser(connectedUser().getUserId());

                    j.setTypeEv("Système/Suppression/Secondaire Profil");
                    if (Objects.nonNull(user)) {
                        j.setConnectedSacondaryName(user.getFullName());
                    }

                } else {
                    j.setTypeEv("Système/Suppression");
                }
                j.setAction(a);
                j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

                jr.save(j);
            }

            return new ResponseEntity(HttpStatus.OK);

        } catch (Exception e) {

            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            // TODO: handle exception
        }

    }

    // EDIT PROFIL ABS
    @Secured({ "ROLE_MASTER" })
    public HashMap<String, String> editAbsProfil(ProfilsAbsenceClass pac, String secondary) {

        HashMap<String, String> response = new HashMap<>();

        List<ProfilsAbsence> exist;
        try {

            exist = profilAbsenceService.checkProfile(pac.idSup, pac.dateFin, pac.dateDebut);

            if (exist.size() == 0) {

                ProfilsAbsence pa = par.findById(pac.id).orElse(null);
                User u1 = ur.findById(pac.idUser).orElse(null);
                User u2 = ur.findById(pac.idSup).orElse(null);
                if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
                    Journal j = new Journal();
                    String a = "";

                    j.setUser(connectedUser());
                    j.setDate(new Date());
                    j.setMode("M");
                    a = " Utilisateur : " + pa.getUser().getFullName() + " --> " + u1.getFullName() + " | Suppléant : "
                            + pa.getSupleant().getFullName() + " --> " + u2.getFullName() + " | Date début : "
                            + pa.getDateDebut() + " --> " + pac.dateDebut + " | Date fin : " + pa.getDateFin() + " --> "
                            + pac.dateFin;
                    j.setComposante("Profil d'absence");
                    if (secondary.equals("true")) {

                        User user = ur.getSecondaryUser(connectedUser().getUserId());

                        j.setTypeEv("Système/Modification/Secondaire Profil");
                        if (Objects.nonNull(user)) {
                            j.setConnectedSacondaryName(user.getFullName());
                        }

                    } else {
                        j.setTypeEv("Système/Modification");
                    }
                    j.setAction(a);
                    j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

                    jr.save(j);
                }
                pa.setUserName(u1.getFullName());
                pa.setSupName(u2.getFullName());

                pa.setUser(u1);
                pa.setSupleant(u2);
                pa.setDateDebut(pac.dateDebut);
                pa.setDateFin(pac.dateFin);
                par.save(pa);

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

    // DELETE PROFIL ABS
    @Secured({ "ROLE_MASTER" })
    public ResponseEntity deleteAbsProfil(Long id, String secondary) {
        ProfilsAbsence pa = par.findById(id).orElse(null);
        if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
            Journal j = new Journal();
            String a = "";

            j.setUser(connectedUser());
            j.setDate(new Date());
            j.setMode("S");
            a = " Utilisateur : " + pa.getUser().getFullName() + " | Suppléant : " + pa.getSupleant().getFullName()
                    + " | Date début : " + pa.getDateDebut() + " | Date fin : " + pa.getDateFin();
            j.setComposante("Profil d'absence");
            if (secondary.equals("true")) {

                User user = ur.getSecondaryUser(connectedUser().getUserId());

                j.setTypeEv("Système/Suppression/Secondaire Profil");
                if (Objects.nonNull(user)) {
                    j.setConnectedSacondaryName(user.getFullName());
                }

            } else {
                j.setTypeEv("Système/Suppression");
            }
            j.setAction(a);
            j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

            jr.save(j);
        }
        par.delete(pa);

        return new ResponseEntity(HttpStatus.OK);

    }

    // GET MAIL DELAY
    public delayMail getDelayMail() {
        return dmr.findByUserUserId(getmst().getUserId());
        // getmst().getDelayMail();
    }

    // GET NOTIF MAIL
    public NotifMail getNotifMail() {

        NotifMail nm = nmr.findByUserUserId(getmst().getUserId());

        if (Objects.nonNull(nm)) {
            nm.setPassword("**********");
            return nm;
        }
        return null;
    }

    // ADD PRIV NATURE
    @Secured({ "ROLE_MASTER" })
    public ResponseEntity addPrivNature(PermissionClass pc, String secondary) {
        PermissionCourrier pd = new PermissionCourrier();
        // PermissionDocument pd = new PermissionDocument();

        pd.setAcces(pc.acces);

        pd.setDescription(pc.description);
        pd.setNom(pc.name);
        pd.setMaster(getmst());
        pd = pcr.save(pd);
        String t = " ";
        int b = 0;
        for (Long id : pc.documentType) {
            Nature dt = nr.findById(id).orElse(null);
            if (b == 0) {
                t = "  " + dt.getName();
                b = 456;
            } else {
                t += " --> " + dt.getName();
            }
            PermissionNatureCourrier pdt = new PermissionNatureCourrier();
            pdt.setId(new PermissionNatureCourrierPK(dt.getId(), pd.getId()));
            pdt.setPermissionCourrier(pd);
            pdt.setNature(dt);
            pnr.save(pdt);
        }
        if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
            Journal j = new Journal();
            String a = "";
            a = " Nom : " + pd.getNom() + " | Acces : ";

            if (pd.getAcces().contains("R"))
                a += " Lecture  ";

            if (pd.getAcces().contains("W"))
                a += " Modification ";
            if (pd.getAcces().contains("RWD"))
                a += " Suppression ";
            a += " |  Description : " + pd.getDescription() + " | ";

            j.setUser(connectedUser());
            j.setDate(new Date());
            j.setMode("AP");
            if (secondary.equals("true")) {

                User user = ur.getSecondaryUser(connectedUser().getUserId());

                j.setTypeEv("Système/Ajout/Secondaire Profil");
                if (Objects.nonNull(user)) {
                    j.setConnectedSacondaryName(user.getFullName());
                }

            } else {
                j.setTypeEv("Système/Ajout");
            }
            j.setComposante("Privilége nature courrier");

            j.setAction(a + " Nature courrier : " + t);
            j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

            jr.save(j);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // EDIT LEVEL OF LOG
    @Secured({ "ROLE_MASTER" })
    public ResponseEntity editJournal(int lvl) {
        User u = getmst();

        u.setSecLevel(lvl);

        ur.save(u);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    // GET NATURE BY PAGE
    public Page<Nature> getNr(Pageable pageable,String q) {
        return nr.findByNameContainingIgnoreCaseAndMaster(q, pageable,getmst());
        // return null;
    }

    // RUBRIQUE Destinataire

    public Page<receiver> getReceivers(Pageable pageable) {
        return receiverRepo.findReceiverByMasterUserId(connectedUser().getUserId(), pageable);
    }

    public List<receiver> getReceiversWithoutPage() {
        return receiverRepo.findAll();
    }

    public ResponseEntity addReceiver(receiver receiver) {
        User u = connectedUser();

        receiver r = new receiver();
        r.setName(receiver.getName());
        if(receiver.getEmail().equals("")) {
            r.setEmail(null);   
        }else {
            r.setEmail(receiver.getEmail()); 
        }
        
        r.setEligible(receiver.isEligible());
        r.setMaster(u);
        receiverRepo.save(r);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    public ResponseEntity editReceiver(Long id, receiver receiver) {
        User u = connectedUser();

        Optional<com.veviosys.vdigit.models.receiver> a = receiverRepo.findById(id);

        log.info("{}", a.get().getName());

        if (Objects.nonNull(a)) {

            a.get().setName(receiver.getName());
            a.get().setEmail(receiver.getEmail());
            a.get().setEligible(receiver.isEligible());
            a.get().setMaster(u);
            receiverRepo.save(a.get());

            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    public ResponseEntity deletReceiver(Long id) {

        receiverRepo.delete(receiverRepo.getOne(id));
        return new ResponseEntity<>(HttpStatus.OK);

    }

    // RUBRIQUE Emetteur
    public Page<Sender> getSenders(Pageable pageable,String q) {
        return senderRepo.findSenderByNameContainingIgnoreCaseAndMasterUserId(q,connectedUser().getUserId(), pageable);
    }

    public ResponseEntity addSender(emetteurClass emetteur) {
        User u = connectedUser();
       
//            if (emetteur.getCode() != null && emetteur.getEmail() != null) {
//                mail.emetteurEmail(connectedUser().getFullName(), emetteur.getCode(), emetteur.getEmail());
//            }

            Sender a = senderRepo.findSenderByNameAndMasterUserId(emetteur.getName(), u.getUserId());
            if (Objects.isNull(a)) {

                Sender r = new Sender();
                r.setMaster(u);
                r.setName(emetteur.getName());
                r.setCode(emetteur.getCode());
                r.setEmail(emetteur.getEmail());
                senderRepo.save(r);
                return new ResponseEntity<>(HttpStatus.OK);

            }
            return new ResponseEntity<>(HttpStatus.CONFLICT);


      

    }

    public ResponseEntity editSender(Long id, emetteurClass emetteur) {
        User u = connectedUser();

        Sender a = senderRepo.findSenderByNameAndMasterUserId(emetteur.getName(), connectedUserMaster(u.getUserId()));

        System.out.println(a.getName());

        if (Objects.nonNull(a)) {

            Sender r = new Sender();
            r.setId(id);
            r.setMaster(u);
            r.setName(emetteur.getName());
            r.setCode(emetteur.getCode());
            r.setEmail(emetteur.getEmail());
            senderRepo.save(r);
            return new ResponseEntity<>(HttpStatus.OK);

        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

    }

    public ResponseEntity deletSender(Long id) {

        senderRepo.delete(senderRepo.getOne(id));
        return new ResponseEntity<>(HttpStatus.OK);

    }

    // TEST CONNECTION TO STORAGE DISK
    public List<Long> testConnectToDisk(String path) {
        File file = new File(path);
        if (file.exists()) {
            List<Long> space = new ArrayList<Long>();
            space.add(file.getTotalSpace() / 1024 / 1024 / 1024);
            space.add(file.getFreeSpace() / 1024 / 1024 / 1024);
            return space;
        }
        return null;
    }

    // GET ALL DISK (LOCAL AND NETWORK)
    public List<StorageDetail> getDisks() {

        List<StorageDetail> disks = new ArrayList<StorageDetail>();
        File[] paths;
        FileSystemView fsv = FileSystemView.getFileSystemView();
        paths = File.listRoots();
        for (File path : paths) {
            StorageDetail sd = new StorageDetail();

            sd.setPath("" + path);
            sd.setName(fsv.getSystemTypeDescription(path));
            List<Long> detail = testConnectToDisk(path.toString());
            if (Objects.nonNull(detail)) {
                sd.setAccess(1);
                sd.setFreeSpace(detail.get(1));
                sd.setSize(detail.get(0));

            } else {
                sd.setAccess(0);
            }
            disks.add(sd);
        }
        return disks;

    }

    // GET DIRECTORY OF FOLDER
    public List<String> getDirectories(String path) {
        List<String> folders = new ArrayList<String>();
        File folder = new File(path);
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                folders.add(fileEntry.getName());
            }
        }
        return folders;
    }

    // ADD NEW DIRECTORY IN PATH
    public void addDirectory(String path) throws Exception {
        File file = new File(path);
        // .println("added"+path);
        if (!file.mkdir()) {
            System.out.println(path);
            throw new Exception("Invalid name");
        }
    }

    // EDIT THE ACTIVE PATH
    public void editActivePath(String path) throws AddressException, MessagingException, IOException {
        User u = connectedUser();

        MasterConfig mc = configRepository.findByMasterUserIdAndConfigName(u.getUserId(), "PATH_EDIT");

        MasterConfig ms = configRepository.findByMasterUserIdAndConfigName(u.getUserId(), "SUPPORT_MAIL");

        MasterConfig genKey = configRepository.findByMasterUserIdAndConfigName(u.getUserId(), "GEN_KEY");

        if (Objects.isNull(mc)) {

            mc = new MasterConfig();
            mc.setConfigName("PATH_EDIT");
            mc.setMaster(u);
        }
        mc.setConfigValue(path);
        if (Objects.isNull(genKey)) {
            genKey = new MasterConfig();
            genKey.setConfigName("GEN_KEY");
            genKey.setMaster(u);

        }
        if (Objects.isNull(ms)) {
            ms = new MasterConfig();
            ms.setConfigName("SUPPORT_MAIL");
            ms.setConfigValue(supportTech);
            ms = configRepository.saveAndFlush(ms);
        }

        User user = ur.findByEmailIgnoreCase(supportTech);

        if (Objects.nonNull(user)) {
            SupportTech support = new SupportTech();
            support.setUserId(user.getUserId());
            support.setUsername(user.getUsername());
            support.setPw(user.getPassword());
            support.setMasterId(connectedUserMaster(connectedUser().getUserId()));
            supportTechRepo.save(support);
        }

        genKey.setConfigValue(UUID.randomUUID().toString().subSequence(0, 10).toString());
        mc.setConfigValue(path);
        mc = configRepository.saveAndFlush(mc);
        genKey = configRepository.saveAndFlush(genKey);

        // SEND MAIL TO SUPPORT
        System.out.println(ms.getConfigValue() + "," + genKey.getConfigValue());
        mail.notifSupport(u.getUserId(), ms.getConfigValue(), genKey.getConfigValue());
    }

    // CONFIRME OR REFUSE THE NEW PATH
    public ResponseEntity confirmeEditPath(SupportTech tech, int state) {
        SupportTech t = supportTechRepo.findByUsername(tech.getUsername());
        if (passwordEncoder.matches(tech.getPw(), t.getPw())) {
            User master = ur.findById(t.getMasterId()).orElse(null);

            MasterConfig mc = configRepository.findByMasterUserIdAndConfigName(master.getUserId(), "PATH_EDIT");
            // MasterConfig
            // genKey=configRepository.findByMasterUserIdAndConfigName(master.getUserId(),
            // "GEN_KEY");

            if (Objects.nonNull(mc)) {
                if (state == 1) {

                    // List<MasterConfig> dataToEdit =
                    // configRepository.findByMasterUserIdAndConfigName("PATH_CONFIRMED");
                    MasterConfig toConf = configRepository.findByMasterUserIdAndConfigName(master.getUserId(),
                            "PATH_CONFIRMED");
                    if (Objects.isNull(toConf)) {
                        toConf = new MasterConfig();
                        toConf.setMaster(master);
                        toConf.setConfigName("PATH_CONFIRMED");
                    }
                    toConf.setConfigValue(mc.getConfigValue());
                    configRepository.save(toConf);
                    configRepository.delete(mc);

                } else {

                    configRepository.delete(mc);

                }
                // .println("M heeere ");
                MasterConfig config = configRepository.findByMasterUserIdAndConfigName(master.getUserId(), "GEN_KEY");
                configRepository.delete(config);
                supportTechRepo.delete(t);
            }

            return new ResponseEntity<>(HttpStatus.OK);

        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    // TEST IF MASTER AND GEN KEY EXIST
    public ResponseEntity testExist(Long id, String key) {

        MasterConfig config = configRepository.findByMasterUserIdAndConfigName(id, "GEN_KEY");
        if (Objects.nonNull(config)) {
            // .println(key);
            if (key.equals(config.getConfigValue())) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            }

            // User master=ur.findById(id).orElse(null);

        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    // TEST LOG WITH SUPPORT
    public ResponseEntity testLogin(SupportTech tech) {
        // .println(tech.getUsername());
        SupportTech t = supportTechRepo.findByUsername(tech.getUsername());

        log.info("{}", t.getUsername());
        if (Objects.nonNull(t)) {
            if (passwordEncoder.matches(tech.getPw(), t.getPw())) {

                return new ResponseEntity<>(HttpStatus.OK);

            }
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

    }

    // public ResponseEntity procEdit(Long id,List<Etape>steps)
    // {

    // }
    public User getUserById(Long id) {
        User u = ur.findById(id).orElse(null);
        u.setContact(cr.findContactByUserUserId(id));
        return u;
    }

    public Contact findContactByUserId(Long id) {
        return cr.findContactByUserUserId(id);
    }

    public List<User> getUsersWithoutUserToEdit(Long id) {
        return ur.findByUserIdNotLike(id);
    }

    public ResponseEntity editUser(User u) {

        User user = ur.findById(u.getUserId()).orElse(null);
        if (Objects.nonNull(user)) {

            System.out.println(u.getHasAccessClient());

            user.setParent(Objects.nonNull(u.getParent()) ? u.getParent() : 0L);
            user.setMat(u.getMat());
            user.setSexe(u.getSexe());
            user.setTitle(u.getTitle());
            user.setHasAccessClient(u.getHasAccessClient());
            user.setGroupId(u.getGroupId());
            user.setHasAccessSecondary(u.getHasAccessSecondary());
            user.setFullName(u.getFullName());
            user.setContact(u.getContact());
            user.setEmail(u.getEmail());
            if (u.getHasAccessSecondary() == 0) {
                user.setSecondary(0);
            }
            // cr.save(u.getContact());
            ur.save(user);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);

    }

    public Page<User> searchUsers(User u, Pageable pageable) {
        return ur.findAll(new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.conjunction();
                p = criteriaBuilder.equal(root.get("master"), connectedUser());
                if (Objects.nonNull(u.getUsername())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("username"), "%" + u.getUsername() + "%"));
                }
                if (Objects.nonNull(u.getMat())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("mat"), "%" + u.getMat() + "%"));
                }
                if (Objects.nonNull(u.getEmail())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("email"), "%" + u.getEmail() + "%"));
                }
                if (Objects.nonNull(u.getFullName())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("fullName"), "%" + u.getFullName() + "%"));
                }
                if (Objects.nonNull(u.getTitle())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("title"), "%" + u.getTitle() + "%"));

                }
                if (Objects.nonNull(u.getParent())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("parent"), u.getParent()));
                }
                if (Objects.nonNull(u.getMat())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("title"), "%" + u.getMat() + "%"));

                }
                if (Objects.nonNull(u.getSexe())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("sexe"), u.getSexe()));
                }
                if (Objects.nonNull(u.getContact().getAdresse())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("contact").get("adresse"),
                            "%" + u.getContact().getAdresse() + "%"));

                }
                System.out.println(u.getContact().getCity());
                if (Objects.nonNull(u.getContact().getCity())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("contact").get("city"),
                            "%" + u.getContact().getCity() + "%"));

                }
                if (Objects.nonNull(u.getContact().getFax())) {
                    p = criteriaBuilder.and(p,
                            criteriaBuilder.like(root.get("contact").get("fax"), "%" + u.getContact().getFax() + "%"));

                }
                if (Objects.nonNull(u.getContact().getGsm())) {
                    p = criteriaBuilder.and(p,
                            criteriaBuilder.like(root.get("contact").get("gsm"), "%" + u.getContact().getGsm() + "%"));

                }

                if (Objects.nonNull(u.getContact().getPhone())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("contact").get("phone"),
                            "%" + u.getContact().getPhone() + "%"));

                }
                if (Objects.nonNull(u.getContact().getState())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("contact").get("state"),
                            "%" + u.getContact().getState() + "%"));

                }
                if (Objects.nonNull(u.getContact().getZip())) {
                    p = criteriaBuilder.and(p,
                            criteriaBuilder.like(root.get("contact").get("zip"), "%" + u.getContact().getZip() + "%"));
                }
                // zip: "dsfsdfsfd"
                return p;
            }
        }, pageable);
    }

    public Page<User> findByFilter(String p, Pageable pageable) {
        String param = p.equals(" ") ? "" : p;
        // param="%"+param+"%";
        System.out.println(param);
        Long id = connectedUser().getUserId();
        return ur
                .findByMasterUserIdAndEmailContainingIgnoreCaseOrMasterUserIdAndUsernameContainingIgnoreCaseOrMasterUserIdAndFullNameContainingIgnoreCaseOrMasterUserIdAndMatContainingIgnoreCaseOrMasterUserIdAndTitleContainingIgnoreCase(
                        id, param, id, param, id, param, id, param, id, param, pageable);
    }

    public List<DocumentType> getDocumentTypesByPermission(Long d) {
        return dtr.findBypermissionDocumentTypes_PermissionDocumentId(d);
    }

    public List<Nature> getNaturesByPermission(Long d) {
        return nr.findByPermissionNatureCourriers_PermissionCourrierId(d);
    }

    public Page<PermissionDocument> getPerms(Pageable pageable,String q) {
        return pdr.findByNomContainingIgnoreCaseAndMaster(q,connectedUser(), pageable);
    }

    public Page<PermissionCourrier> getPermsNat(Pageable pageable,String q) {
        return pcr.findByNomContainingIgnoreCaseAndMaster(q,connectedUser(), pageable);
        // pdr.findByMaster(connectedUser(),pageable);
    }

    public PermissionDocument editPermission(PermissionClass perm) {
        PermissionDocument pd = pdr.findById(perm.id).orElse(null);

        List<PermissionDocumentType> docs = new ArrayList<PermissionDocumentType>();
        pd.setAcces(perm.acces);
        pd.setDescription(perm.description);
        pd.setNom(perm.name);
        for (PermissionDocumentType p : pd.getPermissionDocumentTypes()) {
            pdtr.delete(p);
        }
        for (Long id : perm.documentType) {
            DocumentType dt = dtr.findById(id).orElse(null);
            PermissionDocumentType pdt = new PermissionDocumentType();
            pdt.setId(new PermissionDocumentTypePK(dt.getId(), pd.getId()));
            pdt.setPermissionDocument(pd);
            pdt.setDocumentType(dt);
            pdt = pdtr.saveAndFlush(pdt);
            docs.add(pdt);
        }
        pd.setPermissionDocumentTypes(docs);
        return pdr.saveAndFlush(pd);

    }

    public void deletePemissionDoc(Long id) {
        PermissionDocument pd = pdr.findById(id).orElse(null);
        for (PermissionDocumentType p : pd.getPermissionDocumentTypes()) {
            pdtr.delete(p);
        }
        pdr.delete(pd);
    }

    public PermissionCourrier editPermNat(PermissionClass perm) {
        PermissionCourrier pd = pcr.findById(perm.id).orElse(null);

        List<PermissionNatureCourrier> docs = new ArrayList<PermissionNatureCourrier>();
        pd.setAcces(perm.acces);
        pd.setDescription(perm.description);
        pd.setNom(perm.name);
        for (PermissionNatureCourrier p : pd.getPermissionNature()) {
            pnr.delete(p);
        }
        for (Long id : perm.documentType) {
            Nature dt = nr.findById(id).orElse(null);
            PermissionNatureCourrier pdt = new PermissionNatureCourrier();
            pdt.setId(new PermissionNatureCourrierPK(dt.getId(), pd.getId()));
            pdt.setPermissionCourrier(pd);
            pdt.setNature(dt);
            pdt = pnr.saveAndFlush(pdt);
            docs.add(pdt);
        }
        pd.setPermissionNature(docs);
        return pcr.saveAndFlush(pd);

    }

    public void deletePermNature(Long id) {
        PermissionCourrier p = pcr.findById(id).orElse(null);
        for (PermissionNatureCourrier pnc : p.getPermissionNature()) {
            pnr.delete(pnc);
        }
        pcr.delete(p);
    }

    public List<Groupe> getGroupsByMaster() {
        return gr.findByMaster(connectedUser());
    }

    public List<Groupe> getGroups() {
        return gr.findAll();
    }

    public Page<Attribute> findAttrsByMaster(Pageable pageable,String q) {
        return attributeRepo.findByNameContainingIgnoreCaseAndMasterOrderByIdAsc(q,connectedUser(), pageable);
    }

    public Attribute editAttribute(AttributeClass attr) {
        Attribute a = attributeRepo.findById(attr.id).orElse(null);
        if (Objects.nonNull(a)) {

            a.setLibelle(attr.libelle);
            a.setLabelar(!Objects.isNull(attr.labelar) ? attr.labelar : "*");
            a.setLabelfr(!Objects.isNull(attr.labelfr) ? attr.labelfr : "*");
            a.setLabeleng(!Objects.isNull(attr.labeleng) ? attr.labeleng : "*");
            a.setDefaultValue(!Objects.isNull(attr.defaultValue) ? attr.defaultValue : "*");

        }

        return attributeRepo.saveAndFlush(a);
    }

    public void deleteAttribute(Long id) {
        attributeRepo.delete(attributeRepo.findById(id).orElse(null));
    }

    /// ---------------REPORTS ATTRIBUTES CONFIG---------------------
    public ReportAttributesConfig editReportAttribute(ReportAttributesConfig ra) {
        // ReportAttributesConfig rac=racr.findById(ra.getId());
        return racr.saveAndFlush(ra);
    }

    public List<ReportAttributesConfig> getAttrs() {
        return racr.findByMaster(getmst().getUserId());
    }

    public List<User> findUsersWithoutEntity() {
        return ur.findByEntityIsNull();
    }

    @Autowired
    private MySessionRegistry sessionRegistry;
    @Autowired
    TempSessionRepo tempSessionRepo;

    // Logout User
    public void logoutUser(Long id) {
        List<Object> users = sessionRegistry.getAllPrincipals();
        User u = ur.findById(id).orElse(null);
        for (Object object : users) {
            CostumUserDetails us = (CostumUserDetails) object;

            if (us.getUsername().equals(u.getUsername())) {

                List<SessionInformation> infos = sessionRegistry.getAllSessions(us, false);

                for (SessionInformation info : infos) {
                    TempSession tmp = new TempSession();
                    tmp.setUserId(us.getUser().getUserId());
                    tmp.setSession(info.getSessionId());
                    tmp.setUsername(us.getUser().getUsername());
                    tempSessionRepo.save(tmp);
                    info.expireNow();
                    sessionRegistry.removeSessionInformation(info.getSessionId());
                }
            }
        }

    }

    // Remove storage request

    public void reqMail() {

    }

    public StorageVolumeRequest removePathRequest(String path)
            throws AddressException, MessagingException, IOException {
        User u = connectedUser();
        RandomString randString = new RandomString(6);
        String str = randString.nextString();
        StorageVolumeRequest svr = new StorageVolumeRequest();
        // svr.setRequestId(UUID.randomUUID());
        svr.setRequestDate(new Date());
        Calendar date = Calendar.getInstance();
        System.out.println(date.getTime().toString());
        date.setTime(new Date());
        date.add(Calendar.MINUTE, validationTime);
        svr.setEndlDate(date.getTime());
        svr.setGenPass(str);
        svr.setMasterId(u.getUserId());
        svr.setPath(path);
        mail.storageVolumeRequest(u.getFullName(), u.getEmail(), str, validationTime, path);
        svr = storageVolumeRequestRepo.saveAndFlush(svr);
        return svr;
    }

    public Boolean storageRemoveVerify(Long requestId, String code)
            throws IOException, ExpireException, InvalidException, UsedPathException, NotFoundException {

        User u = connectedUser();

        StorageVolumeRequest storageVolumeRequest = storageVolumeRequestRepo.findById(requestId).orElse(null);
        if (!Objects.isNull(storageVolumeRequest)) {

            if (storageVolumeRequest.getIsStillValid() && u.getUserId().equals(storageVolumeRequest.getMasterId())) {
                if (storageVolumeRequest.getGenPass().equals(code)) {

                    List<Document> docs = dr.findByPathServerContaining(storageVolumeRequest.getPath());
                    if (docs.size() > 0) {
                        // EXP
                        throw new UsedPathException("Used");
                    } else {
                        configService.removeConfig(
                                configRepository.findByMasterUserIdAndConfigName(u.getUserId(), "STORAGE_PATH"),
                                storageVolumeRequest.getPath());
                        storageVolumeRequestRepo.delete(storageVolumeRequest);
                    }

                } else {
                    throw new InvalidException("INV");
                }

            } else {
                throw new ExpireException("EXP");
            }

        }

        else {
            throw new NotFoundException("NF");
        }

        return null;
    }

    public Page<User> findLockedAccounts(Pageable pageable) {
        return ur.findUserByTentativeNumber(tentative, pageable);
    }

    public void activateAccount(Long id) {
        User u = ur.findById(id).orElse(null);
        u.setTentativeNumber(0);
        ur.save(u);
    }

    public void desactivateAccount(Long id) {
        User u = ur.findById(id).orElse(null);
        u.setTentativeNumber(tentative);
        ur.save(u);
    }

    public Page<receiver> getDestSearch(Pageable pageable, String q) {
        // TODO Auto-generated method stub
        return (Page<receiver>) receiverRepo.findByNameContainingIgnoreCase(q, pageable);
    }
}

// public Predicate toPredicate(Root<Folder> root, CriteriaQuery<?> query,
// CriteriaBuilder criteriaBuilder) {
// Predicate p = criteriaBuilder.conjunction();