package com.veviosys.vdigit.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.veviosys.vdigit.classes.AttributeClass;
import com.veviosys.vdigit.classes.GroupClass;
import com.veviosys.vdigit.classes.LdapMap;
import com.veviosys.vdigit.classes.LdapUser;
import com.veviosys.vdigit.classes.NatureClass;
import com.veviosys.vdigit.classes.PermissionClass;
import com.veviosys.vdigit.classes.ProcessRequest;
import com.veviosys.vdigit.classes.ProfilsAbsenceClass;
import com.veviosys.vdigit.classes.QualityClass;
import com.veviosys.vdigit.classes.TypeDraft;
import com.veviosys.vdigit.classes.emetteurClass;
import com.veviosys.vdigit.classes.mapSearch;
import com.veviosys.vdigit.configuration.MySessionRegistry;
import com.veviosys.vdigit.exceptions.StorageConfig.ExpireException;
import com.veviosys.vdigit.exceptions.StorageConfig.InvalidException;
import com.veviosys.vdigit.exceptions.StorageConfig.NotFoundException;
import com.veviosys.vdigit.exceptions.StorageConfig.UsedPathException;
import com.veviosys.vdigit.models.Attribute;
import com.veviosys.vdigit.models.Contact;
import com.veviosys.vdigit.models.DocumentType;
import com.veviosys.vdigit.models.ElementTypeGroup;
import com.veviosys.vdigit.models.Etape;
import com.veviosys.vdigit.models.GroupUser;
import com.veviosys.vdigit.models.Groupe;
import com.veviosys.vdigit.models.Journal;
import com.veviosys.vdigit.models.MasterConfig;
import com.veviosys.vdigit.models.Nature;
import com.veviosys.vdigit.models.NotifMail;
import com.veviosys.vdigit.models.PermissionDocument;
import com.veviosys.vdigit.models.Processus;
import com.veviosys.vdigit.models.ProfilsAbsence;
import com.veviosys.vdigit.models.Quality;
import com.veviosys.vdigit.models.ReportAttributesConfig;
import com.veviosys.vdigit.models.Sender;
import com.veviosys.vdigit.models.StorageVolumeRequest;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.models.delayMail;
import com.veviosys.vdigit.models.receiver;
import com.veviosys.vdigit.services.CostumUserDetails;
import com.veviosys.vdigit.services.ElementService;
import com.veviosys.vdigit.services.GroupUserService;
import com.veviosys.vdigit.services.IldapService;
import com.veviosys.vdigit.services.QualityService;
import com.veviosys.vdigit.services.masterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Secured({ "ROLE_MASTER" })
@RequestMapping("api/v1")
public class AdminController {

    @Autowired
    private masterService ms;
    @Autowired
    IldapService ldapService;
    @Autowired
    private QualityService qualityService;
    @Autowired
    private GroupUserService groupUserService;
    // job69
    @Autowired
    ElementService elmService;
    // job96

    @PostMapping("/majtype/{idtype}")
    public ResponseEntity updateType(@RequestBody TypeDraft draft, @PathVariable Long idtype) throws IOException {
        return ResponseEntity.ok(elmService.updateType(draft, idtype));
    }

    @Secured("ROLE_MASTER")
    @GetMapping(path = "quality/all/page")
    public Page<Quality> getAllQualityPage(Pageable pageable,@RequestParam(name = "q") String q) {
        return qualityService.getAllQualityPage(pageable,q);
    }

    @SuppressWarnings("rawtypes")
    @Secured("ROLE_MASTER")
    @PostMapping(path = "quality/addEdit/{id}")
    public ResponseEntity EditQuality(@RequestBody QualityClass qualityClass, @PathVariable(required = false) Long id) {
        return qualityService.EditQuality(qualityClass, id);
    }

    @SuppressWarnings("rawtypes")
    @Secured("ROLE_MASTER")
    @PostMapping(path = "quality/addEdit")
    public ResponseEntity AddQuality(@RequestBody QualityClass qualityClass) {
        return qualityService.addQuality(qualityClass);
    }
 
    //++++++++++++++++++++++++++++++++++++ Add Quality +++++++++++++++++++++++++++++++++++++++++++++++
    @SuppressWarnings("rawtypes")
    @Secured("ROLE_MASTER")
    @PostMapping(path = "quality/resBo")
    public ResponseEntity refBo(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");  // Extract the 'ids' list from the request object
        if (ids == null) {
            return ResponseEntity.badRequest().body("Invalid input: 'ids' key not found or null");
        }
        System.out.println(ids);
        return qualityService.refBo(ids);
    }
  //++++++++++++++++++++++++++++++++++++ End Add Quality +++++++++++++++++++++++++++++++++++++++++++++++
    
    //++++++++++++++++++++++++++++++++++++ Remove Quality: +++++++++++++++++++++++++++++++++++++++++++++++
    @SuppressWarnings("rawtypes")
    @Secured("ROLE_MASTER")
    @PostMapping("/removeRefFromothers")
    public ResponseEntity<Void> removeRefFromothers(@RequestBody List<Long> ids) {
        qualityService.removeRefFromothers(ids);
        return ResponseEntity.ok().build();
    }
    //++++++++++++++++++++++++++++++++++++ End Remove Quality: +++++++++++++++++++++++++++++++++++++++++++++++
    
    
    @SuppressWarnings("rawtypes")
    @Secured("ROLE_MASTER")
    @DeleteMapping(path = "quality/delete/{id}")
    public ResponseEntity DeleteQuality(@PathVariable Long id) {
        return qualityService.deleteQuality(id);
    }
    
    @Secured("ROLE_MASTER")
    @GetMapping(path = "group/all/page")
    public Page<GroupUser> getAllgroupPage(Pageable pageable) {
        return groupUserService.getAllGroupUserPage(pageable);
    }

    @Secured("ROLE_MASTER")
    @GetMapping(path = "group/addEdit/{id}/{name}")
    public ResponseEntity Editgroup(@PathVariable String name,@PathVariable(required = false) Long id) {
        return groupUserService.EditGroupUser(name, id);
    }

    @Secured("ROLE_MASTER")
    @GetMapping(path = "group/addEdit/{name}")
    public ResponseEntity Addgroup(@PathVariable String name) {
        return groupUserService.addGroupUser(name);
    }
    
    @SuppressWarnings("rawtypes")
    @Secured("ROLE_MASTER")
    @DeleteMapping(path = "group/delete/{id}")
    public ResponseEntity deletegroup(@PathVariable Long id) {
        return groupUserService.deleteGroupUser(id);
    }
    
  
    // ADD NEW USER
    @Secured("ROLE_MASTER")
    @RequestMapping(method = RequestMethod.POST, path = "/add/user/{sexe}/{mat}")
    public ResponseEntity<User> sendPw(@RequestBody User ur, @PathVariable String sexe, @PathVariable String mat)
            throws AddressException, MessagingException, IOException {
        // mail.send(gen);
        return ms.addUser(ur, sexe, mat);
        // new ResponseEntity<>(HttpStatus.OK);
    }
 
    @Secured("ROLE_MASTER")
    @PostMapping("/edituser")
    public ResponseEntity editUser(@RequestBody User u) {
        return ms.editUser(u);
    }

    @Secured("ROLE_MASTER")
    @PostMapping("/search/users")
    public Page<User> searchUsers(@RequestBody User u, Pageable pageable) {
        return ms.searchUsers(u, pageable);
    }

    @Secured("ROLE_MASTER")
    @PostMapping("/search/filter/users")
    public Page<User> searchUsersFilter(@RequestBody String u, Pageable pageable) {
        return ms.findByFilter(u, pageable);
    }

    @GetMapping("/getuser/{id}")
    public User getUser(@PathVariable Long id) {
        return ms.getUserById(id);
    }

    @GetMapping("/user/contact/{id}")
    public Contact getUserContact(@PathVariable Long id) {
        return ms.findContactByUserId(id);
    }

    @GetMapping("/users/{id}")
    public List<User> getUsersWithoutUserToEdit(@PathVariable Long id) {
        return ms.getUsersWithoutUserToEdit(id);
    }

    @GetMapping("/gg")
    public Contact getcont() {
        com.veviosys.vdigit.services.CostumUserDetails user = (com.veviosys.vdigit.services.CostumUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return user.getUser().getContact();
    }

    @Secured("ROLE_MASTER")
    @PostMapping("/uplg")
    public User uplg(@RequestBody List<String> lst, @RequestHeader(name = "secondary") String secondary)
            throws IOException {
        String nom = lst.get(0);
        String logo = lst.get(1);
        return ms.editLogo(nom, logo, secondary);
    }

    @Secured("ROLE_MASTER")
    @GetMapping("users")
    public Page<User> findUsers(Pageable pageable) {
        return ms.findUsersByMaster(pageable);
    }

    @Secured("ROLE_MASTER")
    @DeleteMapping(value = { "/delete/user/{id}" })
    public ResponseEntity deleteUser(@PathVariable(name = "id") Long us,
            @RequestHeader(name = "secondary") String secondary) {
        return ms.deleteUser(us, secondary);
    }

    @Secured("ROLE_MASTER")
    @PostMapping(value = { "/edit/access/user/{id}/{etat}" })
    public ResponseEntity editAccess(@PathVariable(name = "id") Long id, @PathVariable(name = "etat") int etat,
            @RequestHeader(name = "secondary") String secondary) {
        return ms.editAccessFlux(id, etat, secondary);
    }

    @Secured("ROLE_MASTER")
    @PostMapping(value = { "/edit/accessec/user/{id}/{etat}" })
    public ResponseEntity editAccessSec(@PathVariable(name = "id") Long id, @PathVariable(name = "etat") int etat,
            @RequestHeader(name = "secondary") String secondary) {
        return ms.editAccess(id, etat, secondary);
    }

    @Secured("ROLE_MASTER")
    @PostMapping("/master/security/privilege/nature/add")
    public ResponseEntity addPermission(@RequestBody PermissionClass pc,
            @RequestHeader(name = "secondary") String secondary) {
        return ms.addPrivNature(pc, secondary);
    }

    @Secured("ROLE_MASTER")
    @PostMapping("/master/security/privilege/add")
    public ResponseEntity addPermissionNature(@RequestBody PermissionClass pc,
            @RequestHeader(name = "secondary") String secondary) {
        return ms.addPrivilege(pc, secondary);
    }

    @Secured("ROLE_MASTER")
    @PostMapping("/master/security/group/add")
    public ResponseEntity addGroup(@RequestBody GroupClass gc, @RequestHeader(name = "secondary") String secondary) {
        return ms.addGroup(gc, secondary);
    }

    @Secured("ROLE_MASTER")
    @GetMapping("/master/security/getPermission")
    public List<PermissionDocument> getPermission() {
        return ms.getAllPermission();
    }

    @Secured("ROLE_MASTER")
    @GetMapping("/master/security/getPermission/nature")
    public List<PermissionCourrier> getPermissionNature() {
        return ms.getPermNature();
    }

    @Secured("ROLE_MASTER")
    @GetMapping("/master/security/getGroups")
    public Page<Groupe> getGroups(Pageable pageable,@RequestParam(name = "q") String q) {
        return ms.getGroupes(pageable,q);
    }

    @Secured("ROLE_MASTER")
    @PostMapping("/master/security/group/edit")
    public ResponseEntity editGroup(@RequestBody GroupClass gc) {
        return ms.editGroup(gc);
    }

    @Secured("ROLE_MASTER")
    @DeleteMapping("/master/security/group/delete/{id}")
    public ResponseEntity editGroup(@PathVariable(name = "id") Long id) {
        return ms.deleteGroup(id);
    }

    @Secured("ROLE_MASTER")
    @PostMapping("/add/process/{name}")
    public Processus addProcess(@PathVariable String name, @Nullable @RequestBody String model) {
        return ms.addProcess(name, model);
    }

    @Secured("ROLE_MASTER")
    @PostMapping("/add/etapes/{id}")
    public ResponseEntity addEtapes(@PathVariable Long id, @RequestBody List<Etape> etapes,
            @RequestHeader(name = "secondary") String secondary) {
        return ms.addEtape(etapes, id, secondary);
    }

    @Secured("ROLE_MASTER")
    @PostMapping("/process/edit/{id}")
    public ResponseEntity editProcess(@PathVariable Long id, @RequestBody ProcessRequest model) {
        ms.editProcess(id, model);
        return null;

    }

    @GetMapping("/process/version/{idParent}")
    public Page<Processus> findByParent(@PathVariable Long idParent, Pageable pageable) {
        return ms.findByParent(idParent, pageable);
    }
    
    @Secured("ROLE_MASTER")
    @GetMapping("/api/process/getall")
    public Page<Processus> getProcess(Pageable pageable) {
        return ms.getAllProcess(pageable);
    }

    @Secured("ROLE_MASTER")
    @GetMapping("/api/process")
    public List<Processus> getAllProcessList() {
        return ms.getAllProcessList();
    }

    
    @Secured("ROLE_MASTER")
    @PostMapping("/api/nature/add")
    public ResponseEntity addNature(@RequestBody NatureClass n, @RequestHeader(name = "secondary") String secondary) {
        return ms.addNature(n, secondary);
    }

    @Secured("ROLE_MASTER")
    @PostMapping("/api/nature/edit")
    public ResponseEntity editNature(@RequestBody NatureClass n, @RequestHeader(name = "secondary") String secondary) {
        return ms.editNature(n, secondary);
    }

    @Secured("ROLE_MASTER")
    @DeleteMapping("/api/nature/delete/{id}")
    public ResponseEntity deleteNature(@PathVariable Long id, @RequestHeader(name = "secondary") String secondary) {
        return ms.deleteNature(id, secondary);
    }

    @Secured("ROLE_MASTER")
    @GetMapping("/api/getusersmaster")
    public List<User> gUsers() {
        return ms.getAllUsers();
    }

    // ABS LIST
    @Secured("ROLE_MASTER")
    @GetMapping("/api/profilsabsence")
    public Page<ProfilsAbsence> getAbsPage(Pageable pageable) {
        return ms.gectAbsList(pageable);
    }

    @Secured("ROLE_MASTER")
    @PostMapping("/api/profilsabsence/add")
    public HashMap<String, String> addProfilAbs(@RequestBody ProfilsAbsenceClass pac,
            @RequestHeader(name = "secondary") String secondary) {
        return ms.addProfilAbs(pac, secondary);
    }

    @Secured("ROLE_MASTER")
    @GetMapping("/api/journal/edit/{etat}")
    public ResponseEntity editJournal(@PathVariable int etat) {
        return ms.editJournal(etat);
    }

    @Secured("ROLE_MASTER")
    @PostMapping("/api/notifmail/add")
    public ResponseEntity addNotifMail(@RequestBody NotifMail nm, @RequestHeader(name = "secondary") String secondary)
            throws MessagingException {
        // .println(nm.getMail());
        return ms.addNotifMail(nm, secondary);
    }

    @Secured("ROLE_MASTER")
    @PostMapping("/api/delaymail/add")
    public ResponseEntity addNotifMail(@RequestBody delayMail dm, @RequestHeader(name = "secondary") String secondary)
            throws MessagingException {
        // System.out.println(dm.getDelayPerDay() + "ee" + dm.getDelaySup() + "ee" +
        // dm.getDelayUs() + "ee");
        return ms.addConfigDelay(dm, secondary);
    }

    @Secured("ROLE_MASTER")
    @DeleteMapping("/api/process/delete/{id}")
    public ResponseEntity deleteProcess(@PathVariable Long id, @RequestHeader(name = "secondary") String secondary) {
        return ms.deleteProcess(id, secondary);
    }

    @Secured("ROLE_MASTER")
    @PostMapping("/api/profilabs/edit")
    public HashMap<String, String> deleteProcess(@RequestBody ProfilsAbsenceClass pac,
            @RequestHeader(name = "secondary") String secondary) {
        return ms.editAbsProfil(pac, secondary);
    }

    @Secured("ROLE_MASTER")
    @DeleteMapping("/api/profilabs/delete/{id}")
    public ResponseEntity deleteProfilAbs(@PathVariable Long id, @RequestHeader(name = "secondary") String secondary) {
        return ms.deleteAbsProfil(id, secondary);
    }

    @Secured("ROLE_MASTER")
    // NOTIF OF EMAIL (get actuall config)
    @GetMapping("api/settings/delaymail")
    public delayMail getConfigDelay() {
        return ms.getDelayMail();
    }

    
    @Secured("ROLE_MASTER")
    // NOTIF OF EMAIL (get actuall config)
    @GetMapping("api/settings/notifmail")
    public NotifMail getConfigNotif() {
        return ms.getNotifMail();
    }

    @Secured("ROLE_MASTER")
    @GetMapping("/api/journal/getall/{dd}/{df}")
    public Page<Journal> gettJournal(Pageable pageable, @PathVariable Date dd, @PathVariable Date df) {
        return ms.getJournals(pageable, dd, df);
    }
    
    
    @Secured("ROLE_MASTER")
    @GetMapping("/api/nat")
    public Page<Nature> getNatures(Pageable pageable,@RequestParam(name = "q") String q) {
        return ms.getNr(pageable,q);
    }

    // RUBRIQUE LDAP
    @Secured("ROLE_MASTER")
    @PostMapping("/api/ldap/setup/")
    public ResponseEntity setupLdap(@RequestBody LdapMap l) {
        return ldapService.setupLDAP(l.h, l.p, l.us, l.pw);
    }

    @Secured("ROLE_MASTER")
    @GetMapping("/api/ldap/check")
    public MasterConfig checkConfig() {
        return ldapService.checkConfig();
    }
    
    @Secured("ROLE_MASTER")
    @GetMapping("/api/ldap/getusers/{f}")
    public List<List<mapSearch>> loadUsersFromLDAP(@PathVariable String f) {
        return ldapService.getUsersFromLdap(f);
    }

    @Secured("ROLE_MASTER")
    @PostMapping("/api/ldap/importusers")
    public List<LdapUser> addUsers(@RequestBody List<LdapUser> list) {
        return ldapService.addUsers(list);
    }

    // Rubrique Dest/emet
    
    @GetMapping("/api/receivers")
    public Page<receiver> getReceivers(Pageable pageable) {
        return ms.getReceivers(pageable);
    }

    @Secured("ROLE_MASTER")
    @PostMapping("/api/receiver/add")
    public ResponseEntity addReceiver(@RequestBody receiver receiver) {
        return ms.addReceiver(receiver);
    }

    @Secured("ROLE_MASTER")
    @PostMapping("/api/receiver/edit/{id}")
    public ResponseEntity editReciever(@PathVariable Long id, @RequestBody receiver receiver) {
        return ms.editReceiver(id, receiver);
    }

    @Secured("ROLE_MASTER")
    @GetMapping("/api/receiver/delete/{id}")
    public ResponseEntity deleteReceiver(@PathVariable Long id) {
        return ms.deletReceiver(id);
    }

    @Secured("ROLE_MASTER")
    @GetMapping("/api/senders")
    public Page<Sender> getSenders(Pageable pageable,@RequestParam(name ="q") String q) {
        return ms.getSenders(pageable,q);
    }

    @Secured("ROLE_MASTER")
    @PostMapping("/api/sender/add")
    public ResponseEntity addSender(@RequestBody emetteurClass emetteur) {
        return ms.addSender(emetteur);
    }

    @Secured("ROLE_MASTER")
    @PostMapping("/api/sender/edit/{id}")
    public ResponseEntity editSender(@PathVariable Long id, @RequestBody emetteurClass emetteur) {
        return ms.editSender(id, emetteur);
    }

    @Secured("ROLE_MASTER")
    @GetMapping("/api/sender/delete/{id}")
    public ResponseEntity deleteSender(@PathVariable Long id) {
        return ms.deletSender(id);
    }

    @PostMapping("api/storage/test")
    public List<Long> testAccessToStorage(@RequestBody String path) {
        return ms.testConnectToDisk(path);
    }

   // @GetMapping("api/storage/get")
   // public List<StorageDetail> getAvailabe() {
   //     return ms.getDisks();
   // }
    
    // storage hide 

//    @PostMapping("api/storage/get/folder")
//    public List<String> getDirectories(@RequestBody String path) {
//        return ms.getDirectories(path);
//    }

    @Secured("ROLE_MASTER")
    @PostMapping("api/storage/add/folder")
    public void addDirectory(@RequestBody String path) throws Exception {
        ms.addDirectory(path);
    }

    @Secured("ROLE_MASTER")
    @PostMapping("api/storage/editpath")
    public void editPath(@RequestBody String path) throws AddressException, MessagingException, IOException {
        ms.editActivePath(path);
    }

    @Secured("ROLE_MASTER")
    @GetMapping("permission/documenttype/{id}")
    public List<DocumentType> getAvailabe(@PathVariable Long id) {
        return ms.getDocumentTypesByPermission(id);
    }

    @Secured("ROLE_MASTER")
    @GetMapping("permission/nature/{id}")
    public List<Nature> getNaturesPerm(@PathVariable Long id) {
        return ms.getNaturesByPermission(id);
    }
    
    @Secured("ROLE_MASTER")
    @GetMapping("permision")
    public Page<PermissionDocument> getPemissions(Pageable pageable,@RequestParam(name = "q") String q) {
        return ms.getPerms(pageable,q);
    }

    @Secured("ROLE_MASTER")
    @GetMapping("permision/nat")
    public Page<PermissionCourrier> getPemissionsNat(Pageable pageable,@RequestParam(name = "q") String q) {
        return ms.getPermsNat(pageable,q);
    }

    @Secured("ROLE_MASTER")
    @PostMapping("/master/security/privilege/doc/edit")
    public ResponseEntity<PermissionDocument> editPermission(@RequestBody PermissionClass pc) {
        PermissionDocument pd = ms.editPermission(pc);
        return new ResponseEntity<>(pd, HttpStatus.OK);

    }
    @Secured("ROLE_MASTER")
    @DeleteMapping("/master/security/privilege/doc/delete/{id}")
    public ResponseEntity deletePermDoc(@PathVariable Long id) {
        try {
            ms.deletePemissionDoc(id);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }
    }
    @Secured("ROLE_MASTER")
    @PostMapping("/master/security/privilege/nat/edit")
    public ResponseEntity<PermissionCourrier> editPermissionNat(@RequestBody PermissionClass pc) {
        PermissionCourrier pd = ms.editPermNat(pc);
        return new ResponseEntity<>(pd, HttpStatus.OK);

    }
    @Secured("ROLE_MASTER")
    @DeleteMapping("/master/security/privilege/nat/delete/{id}")
    public ResponseEntity deletePermNat(@PathVariable Long id) {
        try {
            ms.deletePermNature(id);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }
    }

    @GetMapping("/groups/all")
    public List<Groupe> getGroupsByMaster() {
        return ms.getGroupsByMaster();
    }

    @Secured("ROLE_MASTER")
    @GetMapping("/groups/users/all")
    public List<Groupe> getGroups() {
        return ms.getGroups();
    }

    @Secured("ROLE_MASTER")
    @GetMapping("/attrs/all")
    public Page<Attribute> getAttrsByMaster(Pageable pageable,@RequestParam(name = "q") String q) {
        return ms.findAttrsByMaster(pageable,q);
    }

    @Secured("ROLE_MASTER")
    @PostMapping("/attrs/edit")
    public Attribute editAttribute(@RequestBody AttributeClass attr) {
        return ms.editAttribute(attr);

    }

    @Secured("ROLE_MASTER")
    @GetMapping("/attrs/delete/{id}")
    public ResponseEntity deleteAttribute(@PathVariable Long id) {
        try {
            ms.deleteAttribute(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            // TODO: handle exception
        }

    }

    @Autowired
    private MySessionRegistry sessionRegistry;

    @Secured("ROLE_MASTER")
    @GetMapping("/users/connected")
    public List<User> listLoggedInUsers() {
        final List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
        List<User> users = new ArrayList<User>();
        for (final Object principal : allPrincipals) {

            CostumUserDetails u = (CostumUserDetails) principal;
            List<SessionInformation> infos = sessionRegistry.getAllSessions(principal, false);
            if (infos.size() >= 1) {

                users.add(u.getUser());
            }

        }
        return users;
    }

    @Secured("ROLE_MASTER")
    @GetMapping("/config/report/attrs")
    List<ReportAttributesConfig> getAttrs() {
        return ms.getAttrs();
    }

    @Secured("ROLE_MASTER")
    @PostMapping("/config/report/attr/edit")
    ReportAttributesConfig editReportAttribute(@RequestBody ReportAttributesConfig rac) {
        return ms.editReportAttribute(rac);
    }

    @GetMapping("users/entity")
    public List<User> findUsersWithoutEntity() {
        return ms.findUsersWithoutEntity();
    }

// Logout User
    @GetMapping("logout/user/{id}")
    public void logoutUser(@PathVariable Long id) {
        ms.logoutUser(id);
    }

    @PostMapping("/config/path/remove/request")
    public StorageVolumeRequest removePathRequest(@RequestBody String rac)
            throws AddressException, MessagingException, IOException {
        return ms.removePathRequest(rac);
    }

    @PostMapping("/config/path/remove/verify/{id}")
    public Boolean storageRemoveVerify(@RequestBody String rac, @PathVariable Long id)
            throws IOException, ExpireException, InvalidException, UsedPathException, NotFoundException {
        return ms.storageRemoveVerify(id, rac);
    }

    @Secured("ROLE_MASTER")
    @GetMapping("accounts/locked")
    public Page<User> findDesactivatedAccounts(Pageable pageable) {
        return ms.findLockedAccounts(pageable);
    }

    @Secured("ROLE_MASTER")
    @GetMapping("accounts/active/{id}")
    public void activeAccount(@PathVariable Long id) {
        ms.activateAccount(id);
    }

    @Secured("ROLE_MASTER")
    @GetMapping("accounts/desactive/{id}")
    public void desactiveAccount(@PathVariable Long id) {
        ms.desactivateAccount(id);
    }

    @Secured("ROLE_MASTER")
    @GetMapping("destsearch")
    public ResponseEntity<Page<receiver>> getAll(Pageable pageable, @RequestParam(name = "q") String q) {
        return ResponseEntity.ok(ms.getDestSearch(pageable, q));

    }
}
