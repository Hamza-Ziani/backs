package com.veviosys.vdigit.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.google.gson.Gson;
import com.veviosys.vdigit.classes.AttributeClass;
import com.veviosys.vdigit.classes.CourrierInfo;
import com.veviosys.vdigit.classes.DocumnentClass;
import com.veviosys.vdigit.classes.EditEtapeClass;
import com.veviosys.vdigit.classes.File64;
import com.veviosys.vdigit.classes.FolderClass;
import com.veviosys.vdigit.classes.UserClassEtape;
import com.veviosys.vdigit.classes.mapSearch;
import com.veviosys.vdigit.integrationCm.services.IntegrationService;
import com.veviosys.vdigit.models.Attribute;
import com.veviosys.vdigit.models.AttributeValue;
import com.veviosys.vdigit.models.Client;
import com.veviosys.vdigit.models.ClientDoc;
import com.veviosys.vdigit.models.CloneEtape;
import com.veviosys.vdigit.models.Document;
import com.veviosys.vdigit.models.DocumentAttributeValue;
import com.veviosys.vdigit.models.DocumentFolder;
import com.veviosys.vdigit.models.DocumentFullText;
import com.veviosys.vdigit.models.DocumentType;
import com.veviosys.vdigit.models.ElementTypeGroup;
import com.veviosys.vdigit.models.Etape;
import com.veviosys.vdigit.models.EtapeDetail;
import com.veviosys.vdigit.models.Folder;
import com.veviosys.vdigit.models.FolderType;
import com.veviosys.vdigit.models.FoldersRelations;
import com.veviosys.vdigit.models.Groupe;
import com.veviosys.vdigit.models.Journal;
import com.veviosys.vdigit.models.Nature;
import com.veviosys.vdigit.models.PermissionDocumentType;
import com.veviosys.vdigit.models.PermissionGroup;
import com.veviosys.vdigit.models.PermissionGroupN;
import com.veviosys.vdigit.models.PermissionNatureCourrier;
import com.veviosys.vdigit.models.Processus;
import com.veviosys.vdigit.models.ProfilsAbsence;
import com.veviosys.vdigit.models.Quality;
import com.veviosys.vdigit.models.Sender;
import com.veviosys.vdigit.models.Seq;
import com.veviosys.vdigit.models.Track;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.models.receiver;
import com.veviosys.vdigit.models.zipMail;
import com.veviosys.vdigit.models.pk.DocumentAttributeValuePK;
import com.veviosys.vdigit.models.pk.DocumentFolderPk;
import com.veviosys.vdigit.models.pk.FoldersRelationsPk;
import com.veviosys.vdigit.reposietories.AttributeRepo;
import com.veviosys.vdigit.reposietories.AttributeValueRepo;
import com.veviosys.vdigit.repositories.ClientDocRepo;
import com.veviosys.vdigit.repositories.ClientRepo;
import com.veviosys.vdigit.repositories.CloneEtapeRepo;
import com.veviosys.vdigit.repositories.DocumentAttributeValueRepo;
import com.veviosys.vdigit.repositories.DocumentFolderRepo;
import com.veviosys.vdigit.repositories.DocumentFulltextRepository;
import com.veviosys.vdigit.repositories.DocumentRepo;
import com.veviosys.vdigit.repositories.DocumentTypeRepo;
import com.veviosys.vdigit.repositories.EtapeRepo;
import com.veviosys.vdigit.repositories.EtapesDetailsRepo;
import com.veviosys.vdigit.repositories.FolderFavoriteRepo;
import com.veviosys.vdigit.repositories.FolderRepo;
import com.veviosys.vdigit.repositories.FolderTypeRepo;
import com.veviosys.vdigit.repositories.FoldersRelationsRepo;
import com.veviosys.vdigit.repositories.GroupRepo;
import com.veviosys.vdigit.repositories.JournalRepo;
import com.veviosys.vdigit.repositories.NatureRepo;
import com.veviosys.vdigit.repositories.PermissionGroupNRepo;
import com.veviosys.vdigit.repositories.PermissionGroupRepo;
import com.veviosys.vdigit.repositories.ProcessusRepo;
import com.veviosys.vdigit.repositories.QualityRepo;
import com.veviosys.vdigit.repositories.ReceiverRepo;
import com.veviosys.vdigit.repositories.SenderRepo;
import com.veviosys.vdigit.repositories.SeqRepo;
import com.veviosys.vdigit.repositories.TrackRepo;
import com.veviosys.vdigit.repositories.UserRepository;
import com.veviosys.vdigit.repositories.profilsAbsenceRepo;
import com.veviosys.vdigit.repositories.zipMailRepo;
import com.veviosys.vdigit.sharedfolders.models.SharedFolder;
import com.veviosys.vdigit.sharedfolders.models.SharedWith;
import com.veviosys.vdigit.sharedfolders.repository.SharedFolderRepo;
import com.veviosys.vdigit.sharedfolders.repository.SharedWithRepo;
import lombok.extern.slf4j.Slf4j;
import java.io.FileWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.WebUtils;

@Service
@Slf4j
public class AlimentationService {

    static final String ARRIVE = "arr";
    static final String DEPART = "dep";

    @Autowired
    private UserRepository ur;
    @Autowired
    private SeqRepo seqRepo;
    @Lazy
    @Autowired
    private IntegrationService integrationService;
    @Autowired
    private FolderTypeRepo ftr;
    @Autowired
    private TrackRepo trackRepo;
    @Autowired
    private ClientRepo cltr;
    @Autowired
    private FolderRepo fr;
    @Autowired
    private SharedFolderRepo sharedFolderRepo;
    @Autowired
    private SharedWithRepo sharedWithRepo;
    @Autowired
    private DocumentTypeRepo dtr;
    @Autowired
    private DocumentRepo dr;
    @Autowired
    private AttributeValueRepo avr;
    @Autowired
    private DocumentAttributeValueRepo davr;
    @Autowired
    private AttributeRepo attr;
    @Autowired
    private DocumentFolderRepo documentFolderRepo;
    @Autowired
    MasterConfigService masterConfigService;
    @Autowired
    private FileStorageServiceImpl fileStorageServiceImpl;
    @Autowired
    private FoldersRelationsRepo foldersRelationsRepo;
    @Autowired
    private NatureRepo nr;
    @Autowired
    private FolderRepo folderRepo;
    @Autowired
    private CloneEtapeRepo ctr;
    @Autowired
    private PermissionGroupRepo pgr;
    @Autowired
    private QualityRepo qualityRepo;
    @Autowired
    EtapesDetailsRepo edr;
    @Autowired
    updateDocumentService documentService;
    @Autowired
    JournalRepo jr;
    @Autowired
    private DocumentFulltextRepository documentFulltextRepository;
    @Autowired
    ClientDocRepo cdr;
    @Autowired
    profilsAbsenceRepo absenceRepo;
    @Autowired
    GroupRepo grpR;
    @Autowired
    private PermissionGroupNRepo pgnr;
    @Autowired
    SenderRepo senderRepo;
    @Autowired
    ReceiverRepo receiverRepo;
    @Autowired
    private userService us;
    @Autowired
    mailService mailservice;

    public AlimentationService() {

    }

    // GET MASTER OF CONNECTED USER
    public User getMaster() {
        return ur.getOne(connectedUserMaster(connectedUser().getUserId()));
    }
    // GET CONNECTED USER

    public User connectedUser() {
        return ((CostumUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

    // GET MASTER ID OF CONNECTED USER
    public Long connectedUserMaster(Long userid) {
        Long id;
        id = ur.findUserMaster(userid);
        if (id != null) {
            return id;
        }
        return userid;
    }

    // GET FOLDERS TYPE BY MASTER
    public List<FolderType> GetFolderTypes() {
        return ftr.findFolderTypeByMasterId(connectedUserMaster(Long.valueOf(connectedUser().getUserId())));
    }

    public List<Client> GetClients() {

        return null;

        // cltr.findByMasterUserId((connectedUserMaster(Long.valueOf(connectedUser().getUserId()))));
    }

    // public Folder GetFolder() {
    // Folder f = new Folder();
    // f.setType(GetFolderTypes().get(0));
    // f.setClient(GetClients().get(0));
    // f.setOwner(ur.getOne(connectedUser().getUserId()));
    // f.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));
    // f.setClient(GetClients().get(0));
    // f.setIs_deleted(false);
    // return fr.saveAndFlush(f);
    // }

    // ADD FOLDER (return 0 if ref exist----count==0)
    public Folder addFolder(FolderClass fc, String secondary) throws ParseException {

        Long count = 0L;
        if (count == 0) {
            Folder f = new Folder();
            // .println(fc.toString());
            FolderType ft = ftr.getOne(fc.getType());
            Calendar cal = Calendar.getInstance();
            f.setCreation_date(cal.getTime());
            f.setType(ft);
            f.setTypeName(ft.getName());
            f.setIs_deleted(false);
            f.setFavoriteBay(" ");
            f.setReference(fc.getReference());
            User mst = ur.getOne(connectedUserMaster(connectedUser().getUserId()));
            f.setEmetteur(mst.getNomClient());
            f.setReceiver(fc.getReceiver());
            f.setAccuse(fc.getAccuse());
            String em = "";

            Nature nature = null;
            if (ft.getSeqIsActive() == 1) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy");
                String autoRef = ft.getOrderRef();
                autoRef = autoRef.replace("ref", ft.getSeq().toString());
                ft.setSeq(ft.getSeq() + 1);
                ftr.save(ft);
                autoRef = autoRef.replace("dt", dateFormat.format(new Date()));
                autoRef = autoRef.replace("txt", ft.getConfigText());
                autoRef = autoRef.replace("#", ft.getSeparatorRef().equals("#") ? "" : ft.getSeparatorRef());
                f.setAutoRef(autoRef);

            } else {
                f.setAutoRef(null);
            }
            if (Objects.nonNull(fc.getNature())) {
                nature = nr.findById(fc.getNature()).orElse(null);
                f.setNature(nature);
                f.setNatureName(nature.getName());
                f.setProcessName(nature.getProcName());
            }
            if (fc.getMode() == 2) {
                // f.setEmetteur_id(senderRepo.findById(fc.getSender()).get());
                f.setEmet__(fc.getSender());
                Sender sender = senderRepo.findSenderByNameAQuery(fc.getSender(), getMaster().getUserId());
                if (Objects.isNull(sender)) {
                    Sender sender1 = new Sender();
                    sender1.setName(fc.getSender());
                    sender1.setMaster(getMaster());
                    senderRepo.save(sender1);
                }
                // em = " | Emetteur : " + senderRepo.findById(fc.getSender()).get().getName();
                em = " | Emetteur : " + fc.getSender();
                // Sender e = senderRepo.findSenderByNameAndMaster(fc.getSender(), mst);
                // f.setSender(e);
                // .println(e.getName());
            }
            f.setDate(fc.getDate());
            f.setNumber(null);
            f.setObjet(fc.getObjet());
            f.setOwner(ur.getOne(connectedUser().getUserId()));
            f.setMaster(mst);
            f.setHasProcess(1);
            f.setHasBo(0);
            f.setFinalise("encours");

            // Date dateRec = new SimpleDateFormat("yyyy-mm-dd
            // HH:mm").parse(fc.getDateReception().replace("T"," "));
            f.setDateReception(fc.getDateReception());
            Folder folderReturn = fr.saveAndFlush(f);
            int ind = 0;
            if (fc.getMode() == 1) {

                if (Objects.nonNull(fc.getNewSenders())) {
                    if (fc.getNewSenders().size() != 0) {
                        receiver rec = new receiver();

                        for (String dest : fc.getNewSenders()) {
                            receiver recieve = receiverRepo.findReceiverByNameAndMaster(dest, getMaster());
                            if (Objects.isNull(recieve)) {
                                rec.setName(dest);
                                rec.setEligible(false);
                                rec.setEmail(null);
                                rec.setMaster(getMaster());
                                rec = receiverRepo.save(rec);
                                fc.getDest().add(rec.getId());
                                rec = new receiver();
                            }

                        }
                    }
                }

                String dests = "";
                List<receiver> receiver = new ArrayList();
                for (Long id : fc.getDest()) {

                    receiver.add(receiverRepo.findById(id).get());

                    dests += ", " + receiverRepo.findById(id).get().getName();
                    f.setDest__(dests);
                    // receiver r = receiverRepo.findReceiverByNameAndMaster(name, mst);
                    // receiverRepo.createFolderDest(folderReturn.getId().toString(), r.getId());
                    if (ind == 0) {
                        em = " | Destinataire(s) : " + receiverRepo.findById(id).get().getName();
                        ind = 1;
                    } else {
                        em += ", " + receiverRepo.findById(id).get().getName();
                    }
                }
                f.setDest(receiver);
            }

            f.setAddedAcces(null);
            CloneEtape a = new CloneEtape();
            if (Objects.nonNull(nature)) {
                List<Etape> etpProc = er.findEtapeByProcessusIdOrderByNumeroAsc(nature.getProcess().getId());
                for (Etape etp : etpProc) {
                    CloneEtape ce = new CloneEtape();
                    if (etp.getNumero() == 1) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(folderReturn.getCreation_date());
                        c.add(Calendar.DATE, etp.getDelai());
                        ce.setDateDebut(folderReturn.getCreation_date());
                        ce.setDateFin(c.getTime());

                    }

                    else {
                        ce.setDateDebut(null);
                        ce.setDateFin(null);

                    }
                    ce.setQuality(etp.getQuality());
                    ce.setDelai(etp.getDelai());
                    ce.setDelaiRet(etp.getDelaiRet());
                    ce.setCommentaire(null);
                    ce.setCourrier(folderReturn);
                    ce.setSignataire(etp.getSignataire());
                    ce.setName(etp.getName());
                    ce.setEtat(0);
                    ce.setIsBack(0);
                    ce.setNumero(etp.getNumero());
                    if (ce.getNumero() == nature.getProcess().getEtapes().size()) {
                        ce.setIsLast(etp.getNumero());
                    }
                    a = ctr.saveAndFlush(ce);

                    if (ft.getCat().equals(DEPART)) {

                        ctr.addUserEtape(connectedUser().getUserId(), a.getId());

                    } else if (ft.getCat().equals(ARRIVE)) {
                        a = ctr.saveAndFlush(ce);
                        for (User us : etp.getUsers()) {

                            ctr.addUserEtape(us.getUserId(), a.getId());
                        }
                    }
                }
            } else {
                folderReturn.setHasProcess(0);
                folderReturn = fr.saveAndFlush(folderReturn);
            }
            if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() >= 1) {
                Journal j = new Journal();

                j.setUser(connectedUser());
                j.setDate(new Date());
                j.setComposante("Courrier");
                String h = "";
                if (secondary.equals("true")) {

                    User user = ur.getSecondaryUser(connectedUser().getUserId());

                    j.setTypeEv("Utlisateur/Ajout/Secondaire Profil ");

                    if (Objects.nonNull(user)) {
                        j.setConnectedSacondaryName(user.getFullName());
                    }

                } else {
                    j.setTypeEv("Utlisateur/Ajout");
                }
                j.setAction("Réference : " + folderReturn.getReference() + " | Date : " + folderReturn.getDate()
                        + " | Type :" + folderReturn.getType().getName() + em + h + " | Objet : "
                        + folderReturn.getObjet());

                if (Objects.nonNull(folderReturn.getNature())) {
                    h = " | Nature :" + folderReturn.getNature().getName();
                }

                j.setMode("A");
                j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

                jr.save(j);
            }
            return f;
        }
        return null;

    }

    public List<DocumentType> getDocumentTypes() {
        // return dtr.findByCategory(
        // ur.getOne(connectedUserMaster(Long.valueOf(connectedUser().getUserId()))).getCategory());
        return null;
    }

    
   
    // GET Attributs of document type

    public List<Attribute> getAttrsByType(Long id) {
        // Long a= new Long(72);
        // //.println(a);
        List<Attribute> ls = new ArrayList<Attribute>();
        List<Object> lst = dtr.finDocTypeAttrRequiredClasses(id);

        for (Object obj : lst) {

            Long var = new Long(((Object[]) obj)[0].toString());
            int var2 = new Integer(((Object[]) obj)[2].toString());
            int var1 = new Integer(((Object[]) obj)[3].toString());
            int var3 = new Integer(((Object[]) obj)[4].toString());
            System.out.println(var1);
            Attribute atr = attr.findById(var).orElse(null);
            atr.setLibelle(atr.getLibelle());
            atr.setRequired(var2);
            atr.setVisib(var3);
            atr.setRep(var1);
            ls.add(atr);

        }
        return ls;
    }

    public HashMap<String, Object> getAttrsByType2(Long id) {
        // Long a= new Long(72);
        // //.println(a);

        HashMap<String, Object> map = new HashMap<>();
        List<Attribute> ls = new ArrayList<Attribute>();
        List<Object> lst = dtr.finDocTypeAttrRequiredClasses(id);

        for (Object obj : lst) {

            Long var = new Long(((Object[]) obj)[0].toString());
            int var2 = new Integer(((Object[]) obj)[2].toString());
            int var1 = new Integer(((Object[]) obj)[3].toString());
            int var3 = new Integer(((Object[]) obj)[4].toString());
            System.out.println(var1);
            Attribute atr = attr.findById(var).orElse(null);
            atr.setLibelle(atr.getLibelle());
            atr.setRequired(var2);
            atr.setVisib(var3);
            atr.setRep(var1);
            ls.add(atr);

        }

        map.put("attrs", ls);

        return map;
    }

    public List<Document> getDocuments() {
        // TODO Auto-generated method stub
        return dr.findAll();
    }

    public String getDocument(UUID id) {
        // TODO Auto-generated method stub
        return dr.getOne(id).getOwner().getFullName();
    }

    @Value("${db.name}")
    private String db;
    @Value("${db.user}")
    private String dbus;
    @Value("${db.pw}")
    private String dbpw;

    // ADD DOCUMENT IN TABLE DOCUMENT AND GENERATED TABLE BY TYPE
    public Map<String, String> addDoc(DocumnentClass dc) throws SQLException {
        DocumentType dt = dtr.findId(dc.type);
        Map<String, String> idMap = new HashMap<>();

        UUID id = UUID.randomUUID();
        String query = "insert into " + dt.getName().replace(' ', '_').replace("'", "_") + dt.getId()
                + " (id,upload_date,last_edit_date,owner,master,content_type,file_name";

        for (AttributeClass attrsClass : dc.attrs) {

            query += "," + attrsClass.name.replace(' ', '_').replace("'", "_");

        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        // String date = format.format(new Date());
        SimpleDateFormat format11 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date11 = format11.format(new Date());

        query += ") values ('" + id + "','" + (new Date()).toString() + "',null,'" + connectedUser().getUserId() + "','"
                + connectedUserMaster(connectedUser().getUserId()) + "',null,'" + dc.getFileName() + "'";
        for (AttributeClass attrsClass : dc.attrs) {
            if (attrsClass.type.equals("date")) {
                // SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                String date1 = format.format(new Date(attrsClass.val));
                attrsClass.val = date1;
            }
            // if(attr.findByName("Fichier").getId()!=6)
            // {
            query += ",'" + attrsClass.val.replace("'", "''") + "'";
            // }

        }
        query += ")";
        // .println(query);
        Connection conn = DriverManager.getConnection(db, dbus, dbpw);
        conn.createStatement().executeUpdate(query);
        conn.close();
        idMap.put("id", id.toString());

        return idMap;
    }

    // ADD DOCUMENT IN TABLE DOCUMENT AND GENERATED TABLE BY TYPE (USED)
    public Map<String, String> addDocument(DocumnentClass dc, String secondary) throws SQLException {
        String ref = "";
        DocumentType dt = dtr.findId(dc.type);
        /*
         * for (AttributeClass at : dc.getAttrs()) { if (at.name.equals("Réference")) {
         * ref = at.val; } }
         */
        int hasAccess = hasAccessTo(dc.getType(), "C");

        // dr.existCountCheck(connectedUserMaster(connectedUser().getUserId()),
        // "Réference", ref);
        // System.err.println("+++++++++++++++++++++++++++++++++++++" + ref +
        // "+++++++++++++++++++++++++++++++++++++");
        // System.err.println("+++++++++++++++++++++++++++++++++++++" + count +
        // "+++++++++++++++++++++++++++++++++++++");

        // count =
        if (hasAccess == 1) {
            Map<String, String> idMap = new HashMap<>();
            Document doc = new Document();
            doc.setType(dtr.findById(dc.type).orElse(null));
            doc.setOwner(ur.findById(connectedUser().getUserId()).orElse(null));
            doc.setUpload_date(Calendar.getInstance().getTime());
            doc.setIs_deleted(false);
            doc.setLast_edit_date(null);
            doc.setFileName(dc.getFileName());
            doc.setMaster(ur.findById(connectedUserMaster(connectedUser().getUserId())).orElse(null));
            doc.setFolders(null);
            Date date1 = doc.getUpload_date();
            LocalDate localDate = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            String path = masterConfigService.findActivePath() + "\\upload" + "\\"
                    + Base64.getEncoder().encodeToString((String.valueOf(doc.getUpload_date().getYear())).getBytes())
                    + "\\"
                    + Base64.getEncoder().encodeToString((String.valueOf(doc.getUpload_date().getMonth())).getBytes())
                    + "\\" + Base64.getEncoder().encodeToString((String.valueOf(localDate.getDayOfMonth())).getBytes())
                    + "\\";

            doc.setPathServer(path);
            if (dc.getIsGenerated()) {

                // Long attrId = attr.findIdbyName("HTML_CONTENT",
                // ur.getOne(connectedUserMaster(connectedUser().getUserId())).getUserId());
                // av = new AttributeValue();
                // av.setValue(dc.getHtmlContent());
                // av.setDocumenAttribute(null);
                // av = avr.saveAndFlush(av);
                // Long valId = av.getId();
                // dav = new DocumentAttributeValue();
                // dav.setId(new DocumentAttributeValuePK(doc.getId(), av.getId()));
                // dav.setAttribute(attr.getOne(attrId));
                // dav.setDocument(doc);
                // dav.setValue(avr.getOne(valId));
                // davr.save(dav);
                doc.setHTML_CONTENT(dc.getHtmlContent());
            }
            doc = dr.saveAndFlush(doc);
            final String id = doc.getId().toString();

            if (dc.getIsGenerated()) {

                doc.setHTML_CONTENT(createNewPathTextFilePath(dc.getHtmlContent(), id));
            }

            AttributeValue av;
            DocumentAttributeValue dav;
            for (AttributeClass a : dc.getAttrs()) {
                System.out.println(a.val);
                av = new AttributeValue();
                av.setValue(a.val);
                av.setDocumenAttribute(null);
                av = avr.saveAndFlush(av);
                Long valId = av.getId();
                dav = new DocumentAttributeValue();
                dav.setId(new DocumentAttributeValuePK(doc.getId(), av.getId()));
                dav.setAttribute(attr.findById(a.id).orElse(null));
                dav.setDocument(doc);
                dav.setValue(avr.findById(valId).orElse(null));
                davr.save(dav);
            }
            Connection conn = DriverManager.getConnection(db, dbus, dbpw);

            // dt.getName().replace(' ', '_').replace("'", "_") +
            String query = "insert into d" + dt.getId() + " (id,upload_date,last_edit_date,owner,master,file_name";
            query += ", type_name,content_type ";
            for (AttributeClass attrsClass : dc.attrs) {
                // .println(attrsClass.name.replace("'", "_"));
                String tempAttrName = "";
                if (!attrsClass.name.equals("HTML_CONTENT")) {
                    if (attrsClass.name.equals("Date"))
                        tempAttrName = "Date_";
                    else
                        tempAttrName = attrsClass.name;
                    query += "," + tempAttrName.replace(' ', '_').replace("'", "_");
                }
            }
            List<String> listAttrs = new ArrayList();

            SimpleDateFormat format11 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date11 = format11.format(new Date());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String date = format.format(new Date());
            query += ") values ( ? , ? ,null, ? , ? , ? ";
            query += ", ? , ? ";
            for (AttributeClass attrsClass : dc.attrs) {
                if (!attrsClass.name.equals("HTML_CONTENT")) {
                    listAttrs.add(attrsClass.val.replace("'", "''"));
                    query += " , ?";
                }
            }
            query += ")";
            System.out.println(query);

            PreparedStatement pe = conn.prepareStatement(query);
            pe.setString(1, id);
            pe.setString(2, date11);
            pe.setLong(3, connectedUser().getUserId());
            pe.setLong(4, connectedUserMaster(connectedUser().getUserId()));
            pe.setString(5, dc.getFileName());
            pe.setString(6, doc.getType().getLibelle().replace("'", "''"));
            pe.setString(7, dc.getContent());
            int i = 8;
            for (String string : listAttrs) {
                System.out.println(string);
                pe.setString(i, string);
                i++;
            }

            pe.executeUpdate();
            conn.close();
            idMap.put("id", id);

            if (Objects.nonNull(dc.getFullText()) && !dc.getFullText().equals("")) {

                DocumentFullText documentFullText = new DocumentFullText(doc.getId(), dc.getFullText(),
                        getMaster().getUserId(), dc.getType());
                documentFulltextRepository.save(documentFullText);

            }
            if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() >= 1) {
                Journal j = new Journal();

                j.setUser(connectedUser());
                j.setDate(new Date());
                j.setTypeEv("Ajout");

                // j.setAction( ref + "' dans le type '" + doc.getType().getName() + "'");
                String ev = "Type : " + doc.getType().getName();

                int a = 0;
                for (AttributeClass ee : dc.attrs) {
                    if (!ee.name.equals("Fichier") && ee.val.length() > 0) {
                        if (a == 4456) {
                            ev += ee.name + " : " + ee.val;
                            a = 546;
                        } else {
                            ev += "| " + ee.name + " : " + ee.val + "";

                        }
                    }
                }

                j.setMode("A");
                j.setAction(ev);
                j.setComposante("Document");
                if (secondary.equals("true")) {

                    User user = ur.getSecondaryUser(connectedUser().getUserId());

                    j.setTypeEv("Utilisateur/Ajout/Secondaire Profil");
                    if (Objects.nonNull(user)) {
                        j.setConnectedSacondaryName(user.getFullName());
                    }

                } else {
                    j.setTypeEv("Utilisateur/Ajout");
                }
                j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

                jr.save(j);
            }

            return idMap;

        }
        return null;

    }

    String createNewPathTextFilePath(String data, String vuuid) {

        Date n = new Date();
        String path = masterConfigService.findActivePath() + "\\textcontent" + "\\"
                + Base64.getEncoder().encodeToString((String.valueOf(n.getYear())).getBytes()) + "\\"
                + Base64.getEncoder().encodeToString((String.valueOf(n.getMonth())).getBytes()) + "\\"
                + Base64.getEncoder().encodeToString((String.valueOf(n.getDay())).getBytes());

        File f = new File(path);
        if (!f.exists())
            f.mkdirs();

        try {
            FileWriter myWriter = new FileWriter(path + "\\" + vuuid + ".documania");
            myWriter.write(data);
            myWriter.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return path + "\\" + vuuid + ".documania";

    }

    String createNewPathVoicePath(String data, String vuuid) {

        Date n = new Date();
        String path = masterConfigService.findActivePath() + "\\voice" + "\\"
                + Base64.getEncoder().encodeToString((String.valueOf(n.getYear())).getBytes()) + "\\"
                + Base64.getEncoder().encodeToString((String.valueOf(n.getMonth())).getBytes()) + "\\"
                + Base64.getEncoder().encodeToString((String.valueOf(n.getDay())).getBytes());

        File f = new File(path);
        if (!f.exists())
            f.mkdirs();

        try {
            FileWriter myWriter = new FileWriter(path + "\\" + vuuid + ".documania");
            myWriter.write(data);
            myWriter.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return path + "\\" + vuuid + ".documania";

    }

    public Map<String, String> addDocument(MultipartFile file, DocumnentClass dc) {

        Map<String, String> idMap = new HashMap<>();

        Document doc = new Document();
        // doc.setId(UUID.randomUUID());
        doc.setType(dtr.getOne(dc.type));
        doc.setOwner(ur.getOne(connectedUser().getUserId()));
        doc.setUpload_date((new Date()));
        doc.setIs_deleted(false);
        doc.setLast_edit_date(null);
        doc.setFileName(dc.getFileName());
        doc.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

        doc.setFolders(null);
        doc = dr.saveAndFlush(doc);
        final String id = doc.getId().toString();
        AttributeValue av;
        DocumentAttributeValue dav;
        for (AttributeClass a : dc.getAttrs()) {
            // .println(a.val);
            av = new AttributeValue();
            av.setValue(a.val);
            av.setDocumenAttribute(null);
            av = avr.saveAndFlush(av);
            Long valId = av.getId();
            dav = new DocumentAttributeValue();
            dav.setId(new DocumentAttributeValuePK(doc.getId(), av.getId()));
            dav.setAttribute(attr.getOne(a.id));
            dav.setDocument(doc);
            dav.setValue(avr.getOne(valId));
            davr.save(dav);
        }
        idMap.put("id", id);
        // file

        return idMap;
    }

    /*
     *************************************************************************************************************************
     **************************************************************************************************************************
     ***************************************** 
     * 
     * Linking
     * Region*******************************************************************
     **************************************************************************************************************************
     **************************************************************************************************************************
     * 
     */

    // LINK FOLDER WITH FOLDERS (Parent->Childs)
    public void linkFolderToFolder(UUID id, List<UUID> folders) {

        Folder f;
        f = fr.findByID(id);

        // List<Folder> foldersList =
        // fr.findFolderByMasterUserId(ur.getOne(connectedUserMaster(Long.valueOf(connectedUser().getUserId()))).getUserId());
        // for(Folder fo:foldersList)
        // {
        if (Objects.nonNull(f))
            for (UUID fId : folders) {
                FoldersRelationsPk foldersRelationsPk = new FoldersRelationsPk(id, fId);
                if (!foldersRelationsRepo.existsById(foldersRelationsPk)) {
                    FoldersRelations foldersRelations = new FoldersRelations(foldersRelationsPk,
                            fr.findByID(foldersRelationsPk.getParent_id()),
                            fr.findByID(foldersRelationsPk.getChild_id()));
                    foldersRelationsRepo.save(foldersRelations);
                }
            }
        // }

    }

    // transfered to search service
    // available folders to link with
    public Page<Folder> availableFolders(UUID id, Pageable pageable) {
        return fr.findAll(new Specification<Folder>() {

            @Override
            public Predicate toPredicate(Root<Folder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                // return criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.function("datediff",
                // Integer.class,root.get("creation_date"),criteriaBuilder.literal(new Date())),
                // 30);

                Predicate p = criteriaBuilder.conjunction();
                p = criteriaBuilder.equal(root.get("master"), getMaster());
                p = criteriaBuilder.and(p, criteriaBuilder.isNull(root.get("parent_folder")));
                p = criteriaBuilder.and(p, criteriaBuilder.notEqual(root.get("id"), id));
                return p;

            }
        }, pageable);
    }

    // LINK DOCUMENT WITH FOLDERS (child-->parents)
    public void linkDocumetToFolders(UUID id, List<UUID> foldersIds) {

        Document d;
        d = dr.getOne(id);
        String typeName = d.getType().getName();

        // List<Folder> folders = new Array

        if (Objects.nonNull(d) && foldersIds.size() > 0) {
            // List<Folder> foldersList = fr.findFolderByMasterUserId(
            // ur.getOne(connectedUserMaster(Long.valueOf(connectedUser().getUserId()))).getUserId());
            // for (Folder fo : foldersList) {

            // }
            for (UUID fId : foldersIds) {
                Folder fo = fr.getOne(fId);
                if (documentFolderRepo.count(new DocumentFolderPk(d.getId(), fId)) < 1)

                {
                    // zaze
                    // //.println(typeName);

                    // .println(fo.getFinalise());
                    if (!Objects.isNull(fo.getFinalise()))
                        if (fo.getFinalise().equals("accusation") && typeName.equals("Accusé de réception")) {
                            fo.setAddedAcces("added");
                            fo.setFinalise("fini");
                            fr.save(fo);

                        }
                    documentFolderRepo.save(new DocumentFolder(new DocumentFolderPk(d.getId(), fId), d, fo));
                }
                // .println(typeName);

            }
        }

    }

    public void linkDocumentsToFolder(UUID id, List<UUID> docsList) {

        Folder d;
        // .println("-----------sssssssssssssssddddddddds");

        d = fr.getOne(id);

        // List<Folder> folders = new Array
        for (UUID uuid : docsList) {
            // .println("-----------ssssssssssssssss");
            Document doc = dr.getOne(uuid);
            documentFolderRepo.save(new DocumentFolder(new DocumentFolderPk(uuid, d.getId()), doc, d));

        }
        // if (Objects.nonNull(d) && docsList.size() > 0) {
        // List<Folder> foldersList = fr.findFolderByMasterUserId(
        // ur.getOne(connectedUserMaster(Long.valueOf(connectedUser().getUserId()))).getUserId());
        // for (Folder fo : foldersList) {
        // for (UUID fId : foldersIds) {
        // if (documentFolderRepo.count(new DocumentFolderPk(d.getId(), fId)) < 1)

        // documentFolderRepo.save(new DocumentFolder(new DocumentFolderPk(d.getId(),
        // fId), d, fo));
        // }

        // }
        // }

    }

    // LINK ONE TO ONE doc folder
    public void link(UUID id, UUID id2) {
        Document d = dr.findById(id).orElse(null);
        String typeName = d.getType().getName();
        Folder f = fr.findByID(id2);

        documentFolderRepo.save(new DocumentFolder(new DocumentFolderPk(d.getId(), f.getId()), d, f));
        if (Objects.nonNull(f.getFinalise())) {

            if (f.getFinalise().equals("accusation") && typeName.equals("Accusé de réception")) {
                f.setFinalise("fini");
                f.setAddedAcces("added");
                fr.save(f);
            }
        }

    }

    // LINK ACUSE WITH COURRIER
    public ResponseEntity linkAccus(UUID id, UUID id2) {
        Document d = dr.findById(id).orElse(null);
        Folder f = fr.findByID(id2);
        f.setAddedAcces("added");
        if (f.getFinalise().equals("accusation")) {
            f.setFinalise("fini");
        }
        documentFolderRepo.save(new DocumentFolder(new DocumentFolderPk(d.getId(), f.getId()), d, f));
        fr.save(f);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // GET LOGO OF CONNECTED MASTER
    public String getLogo() {

        CostumUserDetails users = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if (users.getUser().getMaster() == null)
            return users.getUser().getLogo();
        else
            return users.getUser().getMaster().getLogo();
    }
    /*
     *************************************************************************************************************************
     **************************************************************************************************************************
     ***************************************** Editing
     * Region*******************************************************************
     **************************************************************************************************************************
     **************************************************************************************************************************
     */

    public Folder editFolder(UUID id, FolderClass folder, String secondary) {
        Folder f;
        f = fr.findByID(id);
        if (f == null)
            return null;
        int acc = us.hasAccessToNat(f.getNatureId(), "W");
        if (acc == 1) {
            try {

                f.setType(ftr.getOne((long) folder.getType()));
                Folder fold = fr.findById(id).orElse(null);
                User mst = ur.getOne(connectedUserMaster(connectedUser().getUserId()));

                String t = fold.getType().getName(), r = fold.getReference(), d = fold.getDate(),
                        nt = fold.getNatureName(), obj = fold.getObjet();
                String ds = "";
                if (Objects.nonNull(fold.getEmet__())) {
                    ds = fold.getEmet__();
                }
                if (Objects.nonNull(fold.getDest__())) {
                    ds = fold.getDest__();
                }
                f.setReference(folder.getReference());

                f.setDest__(null);
                f.setEmet__(null);
                // f.setReceiver(folder.getReceiver());
                String dst = "";
                if (folder.getMode() == 2) {

                    // Sender e = senderRepo.findSenderByNameAndMaster(folder.getSender(), mst);
                    f.setDest(null);
                    f.setEmet__(folder.getSender());
                    Sender sender = senderRepo.findSenderByNameAQuery(folder.getSender(), getMaster().getUserId());
                    if (Objects.isNull(sender)) {
                        Sender sender1 = new Sender();
                        sender1.setName(folder.getSender());
                        sender1.setMaster(getMaster());
                        senderRepo.save(sender1);
                    }
                    // f.setEmetteur_id(senderRepo.findById(folder.getSender()).get());
                    ;
                    // dst = senderRepo.findById(folder.getSender()).get().getName();
                }
                String dests = "";
                if (folder.getMode() == 1) {
                    // if (Objects.nonNull(f.getReceivers())) {
                    // receiverRepo.deleteFolderRelation(id.toString());
                    // }
                    /*
                     * for (String name : folder.getDest()) {
                     * // receiver res = receiverRepo.findReceiverByNameAndMaster(name, mst);
                     * // receiverRepo.createFolderDest(f.getId().toString(), res.getId());
                     * name.trim();
                     * dests += ", " + name;
                     * }
                     */
                    f.setEmet__(null);

                    if (folder.getNewSenders().size() != 0) {
                        receiver rec = new receiver();

                        for (String dest : folder.getNewSenders()) {
                            receiver recieve = receiverRepo.findReceiverByNameAndMaster(dest, getMaster());
                            if (Objects.isNull(recieve)) {
                                rec.setName(dest);
                                rec.setEligible(false);
                                rec.setEmail(null);
                                rec.setMaster(getMaster());
                                rec = receiverRepo.save(rec);
                                folder.getDest().add(rec.getId());
                                rec = new receiver();
                            }

                        }
                    }
                    // f.setEmetteur_id(null);

                    // f.setDest__(dests);
                    List<receiver> receiver = new ArrayList();
                    for (Long idRec : folder.getDest()) {

                        receiver.add(receiverRepo.findById(idRec).get());

                        dests += ", " + receiverRepo.findById(idRec).get().getName();

                    }
                    f.setDest(receiver);
                    dst = dests;
                }

                FolderType ft = ftr.getOne(folder.getType());
                f.setType(ft);
                f.setTypeName(ft.getName());
                f.setDateReception(folder.getDate());
                f.setLast_edit_date(Calendar.getInstance().getTime());
                f.setObjet(folder.getObjet());
                Nature nature = nr.findById(folder.getNature()).orElse(null);
                Nature n1 = f.getNature();
                f.setAccuse(folder.getAccuse());
                f.setNature(nature);
                f.setNatureName(nature.getName());
                f.setProcessName(nature.getProcess().getName());
                f = fr.saveAndFlush(f);
                if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() >= 1) {
                    Journal j = new Journal();

                    j.setUser(connectedUser());
                    j.setDate(new Date());
                    j.setTypeEv("Utilisateur/Modification");
                    j.setMode("M");
                    if (secondary.equals("true")) {

                        User user = ur.getSecondaryUser(connectedUser().getUserId());

                        j.setTypeEv("Utilisateur/Modification/Secondaire Profil");

                        if (Objects.nonNull(user)) {
                            j.setConnectedSacondaryName(user.getFullName());
                        }

                    } else {
                        j.setTypeEv("Utilisateur/Modification");
                    }
                    // j.setAction("Modification de courrier '"+f.getReference()+"'");

                    j.setAction(" Id :" + f.getId() +" -->" + f.getId() + " | Type :" + t + " -->" + f.getType().getName() + "| Réference : " + r + " -->"
                            + f.getReference() + "| Date : " + d + " -->" + f.getDate()
                            + " | Destinataire(s) / Emetteur : " + ds + " -->" + dst + " | Nature :" + nt + " -->"
                            + f.getNatureName() + " | Objet : " + obj + " -->" + f.getObjet());
                    j.setComposante("Courrier");
                    // + " || " +" Réference : " + f.getReference() + "| Date : "+f.getDate()+
                    // "| Type : "+f.getType().getName()+
                    // "| Destinataire : "+ f.getDestinataire().getFullName()+"| Nature :
                    // "+f.getNature().getName()+
                    // "| Objet : "+f.getObjet()
                    j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

                    jr.save(j);
                }

                if (Objects.nonNull(n1)) {
                    if (!n1.getId().equals(folder.getNature())) {

                        for (int i = 0; i < f.getEtapes().size(); i++) {
                            for (EtapeDetail ed : f.getEtapes().get(i).getDetails()) {
                                edr.delete(ed);
                            }
                            ctr.deleteSteps(f.getEtapes().get(i).getId());
                            ctr.delete(f.getEtapes().get(i));
                        }

                        CloneEtape a = new CloneEtape();
                        List<Etape> etpProc = er.findEtapeByProcessusIdOrderByNumeroAsc(nature.getProcess().getId());
                        System.out.println(
                                "SELECTEEEEEEEEEEEEEEEEEEEEEEEEEED****************************" + etpProc.size());
                        for (Etape etp : etpProc) {

                            CloneEtape ce = new CloneEtape();

                            if (etp.getNumero() == 1) {
                                Calendar c = Calendar.getInstance();
                                c.setTime(new Date());
                                c.add(Calendar.DATE, etp.getDelai());
                                ce.setDateDebut(new Date());
                                ce.setDateFin(c.getTime());
                            }

                            else {

                                ce.setDateDebut(null);

                                ce.setDateFin(null);

                            }
                            f.setFinalise(null);
                            ce.setQuality(etp.getQuality());
                            ce.setDelai(etp.getDelai());
                            ce.setDelaiRet(etp.getDelaiRet());
                            ce.setCommentaire(null);
                            ce.setCourrier(f);
                            ce.setSignataire(etp.getSignataire());
                            ce.setName(etp.getName());
                            ce.setEtat(0);
                            ce.setIsBack(0);
                            ce.setNumero(etp.getNumero());
                            // ce.setUsers(etp.getUsers());
                            a = ctr.saveAndFlush(ce);
                            if (ce.getNumero() == nature.getProcess().getEtapes().size()) {
                                ce.setIsLast(etp.getNumero());
                            }

                            if (ft.getCat().equals(DEPART)) {

                                ctr.addUserEtape(connectedUser().getUserId(), a.getId());

                            } else if (ft.getCat().equals(ARRIVE)) {

                                for (User us : etp.getUsers()) {

                                    ctr.addUserEtape(us.getUserId(), a.getId());
                                }
                            }
                        }

                    }
                }
                if (Objects.isNull(n1) && Objects.nonNull(folder.getNature())) {
                    CloneEtape a = new CloneEtape();
                    List<Etape> etpProc = er.findEtapeByProcessusIdOrderByNumeroAsc(nature.getProcess().getId());

                    for (Etape etp : etpProc) {

                        CloneEtape ce = new CloneEtape();

                        if (etp.getNumero() == 1) {
                            Calendar c = Calendar.getInstance();
                            c.setTime(new Date());
                            c.add(Calendar.DATE, etp.getDelai());
                            ce.setDateDebut(new Date());
                            ce.setDateFin(c.getTime());
                        }

                        else {
                            Calendar c = Calendar.getInstance();
                            c.setTime(a.getDateFin());
                            c.add(Calendar.DATE, 1);
                            ce.setDateDebut(null);
                            Calendar c2 = Calendar.getInstance();
                            c2.setTime(c.getTime());
                            c2.add(Calendar.DATE, etp.getDelai() - 1);
                            ce.setDateFin(null);

                        }
                        f.setFinalise(null);
                        ce.setQuality(etp.getQuality());
                        ce.setDelai(etp.getDelai());
                        ce.setDelaiRet(etp.getDelaiRet());
                        ce.setCommentaire(null);
                        ce.setCourrier(f);
                        ce.setSignataire(etp.getSignataire());
                        ce.setName(etp.getName());
                        ce.setEtat(0);
                        ce.setIsBack(0);
                        ce.setNumero(etp.getNumero());
                        // ce.setUsers(etp.getUsers());
                        a = ctr.saveAndFlush(ce);
                        if (ce.getNumero() == nature.getProcess().getEtapes().size()) {
                            ce.setIsLast(etp.getNumero());
                        }
                        if (ft.getCat().equals(DEPART)) {

                            ctr.addUserEtape(connectedUser().getUserId(), a.getId());

                        } else if (ft.getCat().equals(ARRIVE)) {
                            a = ctr.saveAndFlush(ce);
                            for (User us : etp.getUsers()) {

                                ctr.addUserEtape(us.getUserId(), a.getId());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // //.println();
                e.printStackTrace();
            }
        }
        return null;
    }

    /*
     *************************************************************************************************************************
     **************************************************************************************************************************
     ***************************************** 
     * 
     * deleting/
     * Region*******************************************************************
     **************************************************************************************************************************
     **************************************************************************************************************************
     * 
     */
    @Autowired
    FolderFavoriteRepo ffr;

    
    public void deleteFolderWithoutAccess(UUID id, String secondary) {

        Folder f;
        List<SharedFolder> sharedFolders = new ArrayList<>();
        List<SharedWith> sharedWithFolders = new ArrayList<>();

        sharedFolders = sharedFolderRepo.findByFolderId(id);

        f = fr.findByID(id);
    
            {
                ffr.deleteByIdFolder(f);
                // ffr.deleteByFolder(id.toString());
                for (DocumentFolder df : f.getDocuments()) {

                    documentService.delete(df.getDocument().getId(), secondary);
                }
                for (FoldersRelations frel : f.getChilds()) {
                    foldersRelationsRepo.delete(frel);

                }
                for (FoldersRelations frel : f.getParents()) {
                    foldersRelationsRepo.delete(frel);

                }
                for (Track track : f.getTrackedEmails()) {
                    trackRepo.delete(track);

                }

                for (SharedFolder sharedFolder : sharedFolders) {

                    sharedWithFolders = sharedWithRepo.findByMessageId(sharedFolder.getId());

                    for (SharedWith sharedWithFolder : sharedWithFolders) {
                        sharedWithRepo.delete(sharedWithFolder);
                    }

                }
                sharedFolderRepo.deleteAll(sharedFolders);

                for (int i = 0; i < f.getEtapes().size(); i++) {
                    for (EtapeDetail ed : f.getEtapes().get(i).getDetails()) {
                        edr.delete(ed);
                    }
                    ctr.deleteSteps(f.getEtapes().get(i).getId());
                    ctr.delete(f.getEtapes().get(i));
                }
                if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() >= 1) {
                    Journal j = new Journal();

                    j.setUser(connectedUser());
                    j.setDate(new Date());
                    j.setComposante("Courrier");
                    j.setMode("S");
                    if (secondary.equals("true")) {

                        User user = ur.getSecondaryUser(connectedUser().getUserId());

                        j.setTypeEv("Utilisateur/Suppression/Secondaire Profil");

                        if (Objects.nonNull(user)) {
                            j.setConnectedSacondaryName(user.getFullName());
                        }

                    } else {
                        j.setTypeEv("Utilisateur/Suppression");
                    }
                    j.setAction(" Type :" + f.getType().getName() + "| Réference : " + f.getReference() + "| Date : "
                            + f.getDate() + " | Destinataire :" + f.getReceiver() + " | Nature :"
                            + f.getNature().getName() + " | Objet : " + f.getObjet());
                    j.setAction("Suppression de courrier '" + f.getReference() + " de type '" + f.getType().getName()
                            + "'");
                    j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

                    jr.save(j);
                }
                fr.delete(f);
            }
        
    }
    // DELETE FOLDER (DOCS AND RELATIONS)
    public void deleteFolder(UUID id, String secondary) {

        Folder f;
        List<SharedFolder> sharedFolders = new ArrayList<>();
        List<SharedWith> sharedWithFolders = new ArrayList<>();

        sharedFolders = sharedFolderRepo.findByFolderId(id);

        f = fr.findByID(id);
        if (us.hasAccessToNat(f.getNature().getId(), "D") == 1) {
            {
                ffr.deleteByIdFolder(f);
                // ffr.deleteByFolder(id.toString());
                for (DocumentFolder df : f.getDocuments()) {

                    documentService.delete(df.getDocument().getId(), secondary);
                }
                for (FoldersRelations frel : f.getChilds()) {
                    foldersRelationsRepo.delete(frel);

                }
                for (FoldersRelations frel : f.getParents()) {
                    foldersRelationsRepo.delete(frel);

                }
                for (Track track : f.getTrackedEmails()) {
                    trackRepo.delete(track);

                }

                for (SharedFolder sharedFolder : sharedFolders) {

                    sharedWithFolders = sharedWithRepo.findByMessageId(sharedFolder.getId());

                    for (SharedWith sharedWithFolder : sharedWithFolders) {
                        sharedWithRepo.delete(sharedWithFolder);
                    }

                }
                sharedFolderRepo.deleteAll(sharedFolders);

                for (int i = 0; i < f.getEtapes().size(); i++) {
                    for (EtapeDetail ed : f.getEtapes().get(i).getDetails()) {
                        edr.delete(ed);
                    }
                    ctr.deleteSteps(f.getEtapes().get(i).getId());
                    ctr.delete(f.getEtapes().get(i));
                }
                if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() >= 1) {
                    Journal j = new Journal();

                    j.setUser(connectedUser());
                    j.setDate(new Date());
                    j.setComposante("Courrier");
                    j.setMode("S");
                    if (secondary.equals("true")) {

                        User user = ur.getSecondaryUser(connectedUser().getUserId());

                        j.setTypeEv("Utilisateur/Suppression/Secondaire Profil");

                        if (Objects.nonNull(user)) {
                            j.setConnectedSacondaryName(user.getFullName());
                        }

                    } else {
                        j.setTypeEv("Utilisateur/Suppression");
                    }
                    j.setAction(" Type :" + f.getType().getName() + "| Réference : " + f.getReference() + "| Date : "
                            + f.getDate() + " | Destinataire :" + f.getReceiver() + " | Nature :"
                            + f.getNature().getName() + " | Objet : " + f.getObjet());
                    j.setAction("Suppression de courrier '" + f.getReference() + " de type '" + f.getType().getName()
                            + "'");
                    j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

                    jr.save(j);
                }
                fr.delete(f);
            }
        }
    }

    // GET DOCS COURRIER (doc etape)
    public Page<DocumentFolder> getDocEtape(UUID id, Pageable pageable) {

        // List<DocumentFolder> ds = documentFolderRepo.findDocumentsByFolderUUID(id);
        // int start = (int) pageable.getOffset();
        // int end = (int) (start + pageable.getPageSize()) > ds.size() ? ds.size() :
        // (start + pageable.getPageSize());
        // Page<DocumentFolder> pages = new PageImpl<DocumentFolder>(ds.subList(start,
        // end), pageable, ds.size());
        // return pages;

        // List<Groupe> groups =
        // grpR.findGroupeByUsersUserId(connectedUser().getUserId());
        // List<DocumentFolder> ds = documentFolderRepo.findDocumentsByFolderUUID(id);
        // List<DocumentFolder> dsend = new ArrayList<DocumentFolder>();
        // if (connectedUser().getMaster() == null) {
        // int start = (int) pageable.getOffset();
        // int end = (int) (start + pageable.getPageSize()) > ds.size() ? ds.size() :
        // (start + pageable.getPageSize());
        // Page<DocumentFolder> pages = new PageImpl<DocumentFolder>(ds.subList(start,
        // end), pageable, ds.size());
        // return pages;
        // }
        // List<DocumentType> documentTypes = new ArrayList<DocumentType>();
        // for (Groupe groupe : groups) {

        // List<PermissionGroup> permissionGroups = pgr.findByGroup(groupe);

        // for (PermissionGroup pd : permissionGroups) {

        // for (PermissionDocumentType pdt :
        // pd.getPermissionDocument().getPermissionDocumentTypes()) {
        // documentTypes.add(pdt.getDocumentType());

        // }
        // }

        // }
        // for (DocumentFolder documentFolder : ds) {
        // if(documentTypes.indexOf( documentFolder.getDocument().getType())!=-1){
        // dsend.add(documentFolder);

        // }

        // }

        // int start = (int) pageable.getOffset();
        // int end = (int) (start + pageable.getPageSize()) > dsend.size() ?
        // dsend.size()
        // : (start + pageable.getPageSize());
        // Page<DocumentFolder> pages = new
        // PageImpl<DocumentFolder>(dsend.subList(start, end), pageable, dsend.size());
        // return pages;

        List<Groupe> groups = grpR.findGroupeByUsersUserId(connectedUser().getUserId());
        List<DocumentFolder> ds = documentFolderRepo.findDocumentsByFolderUUID(id);
        List<DocumentFolder> dsend = new ArrayList<DocumentFolder>();
        if (connectedUser().getMaster() == null) {
            int start = (int) pageable.getOffset();
            int end = (int) (start + pageable.getPageSize()) > ds.size() ? ds.size() : (start + pageable.getPageSize());
            Page<DocumentFolder> pages = new PageImpl<DocumentFolder>(ds.subList(start, end), pageable, ds.size());
            return pages;
        }
        List<DocumentType> documentTypes = new ArrayList<DocumentType>();
        for (Groupe groupe : groups) {

            List<PermissionGroup> permissionGroups = pgr.findByGroup(groupe);

            for (PermissionGroup pd : permissionGroups) {

                for (PermissionDocumentType pdt : pd.getPermissionDocument().getPermissionDocumentTypes()) {
                    documentTypes.add(pdt.getDocumentType());

                }
            }

        }
        for (DocumentFolder documentFolder : ds) {
            if (documentTypes.indexOf(documentFolder.getDocument().getType()) != -1) {
                dsend.add(documentFolder);

            }

        }

        int start = (int) pageable.getOffset();
        int end = (int) (start + pageable.getPageSize()) > dsend.size() ? dsend.size()
                : (start + pageable.getPageSize());
        Page<DocumentFolder> pages = new PageImpl<DocumentFolder>(dsend.subList(start, end), pageable, dsend.size());
        return pages;
    }

    // FIND DOCS BY FOLDER
    public ResponseEntity<Page<DocumentFolder>> getFolderDocuments(UUID id, Pageable pageable) {

        List<Groupe> groups = grpR.findGroupeByUsersUserId(connectedUser().getUserId());
        List<DocumentFolder> ds = documentFolderRepo.findDocumentsByFolderUUID(id);
        List<DocumentFolder> dsend = new ArrayList<DocumentFolder>();

        List<DocumentType> documentTypes = new ArrayList<DocumentType>();
        for (Groupe groupe : groups) {

            List<PermissionGroup> permissionGroups = pgr.findByGroup(groupe);

            for (PermissionGroup pd : permissionGroups) {

                for (PermissionDocumentType pdt : pd.getPermissionDocument().getPermissionDocumentTypes()) {
                    documentTypes.add(pdt.getDocumentType());

                }
            }

        }

        if (connectedUser().getMaster() == null) {

            for (DocumentFolder documentFolder : ds) {
                if (documentTypes.indexOf(documentFolder.getDocument().getType()) != -1) {

                    DocumentType Doctype = dtr.findByNameAndMaster(documentFolder.getDocument().getType().getName(),
                            ur.findById(connectedUserMaster(connectedUser().getUserId())).get());

                    List<Object> lst = dtr.finDocTypeAttrRequiredClasses(Doctype.getId());

                    List<Attribute> ls = new ArrayList<Attribute>();

                    for (Object obj : lst) {

                        Long var = new Long(((Object[]) obj)[0].toString());
                        int var2 = new Integer(((Object[]) obj)[2].toString());
                        int var1 = new Integer(((Object[]) obj)[3].toString());
                        int var3 = new Integer(((Object[]) obj)[4].toString());
                        System.out.println(var1);
                        Attribute atr = attr.findById(var).orElse(null);
                        atr.setLibelle(atr.getLibelle());
                        atr.setRequired(var2);
                        atr.setVisib(var3);
                        atr.setRep(var1);

                        for (DocumentAttributeValue attribute : documentFolder.getDocument().getAttributeValues()) {
                            if (attribute.getAttribute().getId() == atr.getId()) {
                                attribute.setAttribute(atr);
                            }
                        }

                    }
                }

            }
            int start = (int) pageable.getOffset();
            int end = (int) (start + pageable.getPageSize()) > ds.size() ? ds.size() : (start + pageable.getPageSize());
            Page<DocumentFolder> pages = new PageImpl<DocumentFolder>(ds.subList(start, end), pageable, ds.size());

            return new ResponseEntity<Page<DocumentFolder>>(pages, HttpStatus.OK);
        }

        for (DocumentFolder documentFolder : ds) {
            if (documentTypes.indexOf(documentFolder.getDocument().getType()) != -1) {
                dsend.add(documentFolder);

                DocumentType Doctype = dtr.findByNameAndMaster(documentFolder.getDocument().getType().getName(),
                        ur.findById(connectedUserMaster(connectedUser().getUserId())).get());

                List<Object> lst = dtr.finDocTypeAttrRequiredClasses(Doctype.getId());

                List<Attribute> ls = new ArrayList<Attribute>();

                for (Object obj : lst) {

                    Long var = new Long(((Object[]) obj)[0].toString());
                    int var2 = new Integer(((Object[]) obj)[2].toString());
                    int var1 = new Integer(((Object[]) obj)[3].toString());
                    int var3 = new Integer(((Object[]) obj)[4].toString());
                    System.out.println(var1);
                    Attribute atr = attr.findById(var).orElse(null);
                    atr.setLibelle(atr.getLibelle());
                    atr.setRequired(var2);
                    atr.setVisib(var3);
                    atr.setRep(var1);
                    for (DocumentAttributeValue attribute : documentFolder.getDocument().getAttributeValues()) {
                        if (attribute.getAttribute().getId() == atr.getId()) {
                            attribute.setAttribute(atr);
                        }
                    }

                }

            }

        }

        int start = (int) pageable.getOffset();
        int end = (int) (start + pageable.getPageSize()) > dsend.size() ? dsend.size()
                : (start + pageable.getPageSize());
        Page<DocumentFolder> pages = new PageImpl<DocumentFolder>(dsend.subList(start, end), pageable, dsend.size());
        return new ResponseEntity<Page<DocumentFolder>>(pages, HttpStatus.OK);

    }
    
    public ResponseEntity<List<DocumentFolder>> getFolderDocumentsList(UUID id) {

        List<Groupe> groups = grpR.findGroupeByUsersUserId(connectedUser().getUserId());
        List<DocumentFolder> ds = documentFolderRepo.findDocumentsByFolderUUID(id);
        List<DocumentFolder> dsend = new ArrayList<DocumentFolder>();

        List<DocumentType> documentTypes = new ArrayList<DocumentType>();
        for (Groupe groupe : groups) {

            List<PermissionGroup> permissionGroups = pgr.findByGroup(groupe);

            for (PermissionGroup pd : permissionGroups) {

                for (PermissionDocumentType pdt : pd.getPermissionDocument().getPermissionDocumentTypes()) {
                    documentTypes.add(pdt.getDocumentType());

                }
            }

        }

        if (connectedUser().getMaster() == null) {

            for (DocumentFolder documentFolder : ds) {
                if (documentTypes.indexOf(documentFolder.getDocument().getType()) != -1) {

                    DocumentType Doctype = dtr.findByNameAndMaster(documentFolder.getDocument().getType().getName(),
                            ur.findById(connectedUserMaster(connectedUser().getUserId())).get());

                    List<Object> lst = dtr.finDocTypeAttrRequiredClasses(Doctype.getId());

                    List<Attribute> ls = new ArrayList<Attribute>();

                    for (Object obj : lst) {

                        Long var = new Long(((Object[]) obj)[0].toString());
                        int var2 = new Integer(((Object[]) obj)[2].toString());
                        int var1 = new Integer(((Object[]) obj)[3].toString());
                        int var3 = new Integer(((Object[]) obj)[4].toString());
                        System.out.println(var1);
                        Attribute atr = attr.findById(var).orElse(null);
                        atr.setLibelle(atr.getLibelle());
                        atr.setRequired(var2);
                        atr.setVisib(var3);
                        atr.setRep(var1);

                        for (DocumentAttributeValue attribute : documentFolder.getDocument().getAttributeValues()) {
                            if (attribute.getAttribute().getId() == atr.getId()) {
                                attribute.setAttribute(atr);
                            }
                        }

                    }
                }

            }
       

            return new ResponseEntity<List<DocumentFolder>>(ds, HttpStatus.OK);
        }

        for (DocumentFolder documentFolder : ds) {
            if (documentTypes.indexOf(documentFolder.getDocument().getType()) != -1) {
                dsend.add(documentFolder);

                DocumentType Doctype = dtr.findByNameAndMaster(documentFolder.getDocument().getType().getName(),
                        ur.findById(connectedUserMaster(connectedUser().getUserId())).get());

                List<Object> lst = dtr.finDocTypeAttrRequiredClasses(Doctype.getId());

                List<Attribute> ls = new ArrayList<Attribute>();

                for (Object obj : lst) {

                    Long var = new Long(((Object[]) obj)[0].toString());
                    int var2 = new Integer(((Object[]) obj)[2].toString());
                    int var1 = new Integer(((Object[]) obj)[3].toString());
                    int var3 = new Integer(((Object[]) obj)[4].toString());
                    System.out.println(var1);
                    Attribute atr = attr.findById(var).orElse(null);
                    atr.setLibelle(atr.getLibelle());
                    atr.setRequired(var2);
                    atr.setVisib(var3);
                    atr.setRep(var1);
                    for (DocumentAttributeValue attribute : documentFolder.getDocument().getAttributeValues()) {
                        if (attribute.getAttribute().getId() == atr.getId()) {
                            attribute.setAttribute(atr);
                        }
                    }

                }

            }

        }

        return new ResponseEntity<List<DocumentFolder>>(ds, HttpStatus.OK);

    }

    // UNLINK DOC TO FOLDER
    public void unlinkDocumetToFolders(UUID did, UUID fid) {

        DocumentFolder link = documentFolderRepo.getOne(new DocumentFolderPk(did, fid));

        documentFolderRepo.delete(link);

    }

    // UNLINK DOC TO FOLDERS
    public void unlinkDocumetToFolder(String did, List<String> fid) {

        for (String uuid : fid) {

            // DocumentFolder link = documentFolderRepo.getOne(new
            // DocumentFolderPk(did,uuid));

            documentFolderRepo.deleteDocsLink(did, uuid);
            // //.println(uuid);
            // .delete(link);
        }

    }

    public List<Object> linkDocumetToAvilableFolders(UUID id) {
        // TODO Auto-generated method stub

        return fr.findAvailableFolders(id.toString());
    }

    // ADD DOC BY CLIENT
    public Map<String, String> addDocsByClient(DocumnentClass dc) {
        Document doc = new Document();
        doc.setClient(cltr.getOne(connectedUser().getIsClient()));
        doc.setId(UUID.randomUUID());
        doc.setUpload_date((new Date()));
        doc.setIs_deleted(false);
        doc.setLast_edit_date(null);
        doc.setFileName(dc.getFileName());
        doc.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));
        doc.setFolders(null);
        Date date1 = doc.getUpload_date();
        LocalDate localDate = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        String path = "/"
                + Base64.getEncoder().encodeToString((String.valueOf(doc.getUpload_date().getYear())).getBytes()) + "/"
                + Base64.getEncoder().encodeToString((String.valueOf(doc.getUpload_date().getMonth())).getBytes()) + "/"
                + Base64.getEncoder().encodeToString((String.valueOf(localDate.getDayOfMonth())).getBytes());

        doc.setPathServer(path);
        // .println(path);
        doc = dr.saveAndFlush(doc);

        Map<String, String> idMap = new HashMap<>();
        idMap.put("id", doc.getId().toString());
        return idMap;
    }
    // EDIT DOC ADDED BY CLIENT

    public ResponseEntity ConfirmeDocClient(DocumnentClass dc, UUID id) throws SQLException {

        List<String> listAttrs = new ArrayList();
        Document doc = dr.getOne(id);
        doc.setClientDoc(null);
        doc.setType(dtr.getOne(dc.type));
        doc.setOwner(connectedUser());
        AttributeValue av;
        DocumentAttributeValue dav;
        doc = dr.saveAndFlush(doc);
        for (AttributeClass a : dc.getAttrs()) {
            // .println(a.val);
            av = new AttributeValue();
            av.setValue(a.val);
            av.setDocumenAttribute(null);
            av = avr.saveAndFlush(av);
            Long valId = av.getId();
            dav = new DocumentAttributeValue();
            dav.setId(new DocumentAttributeValuePK(id, av.getId()));
            dav.setAttribute(attr.getOne(a.id));
            dav.setDocument(doc);
            dav.setValue(avr.getOne(valId));
            davr.save(dav);
        }
        String query = "insert into d" + doc.getType().getId()
                + " (id,upload_date,last_edit_date,owner,master,content_type,file_name,type_name";

        for (AttributeClass attrsClass : dc.attrs) {

            query += "," + attrsClass.name.replace(' ', '_');
        }
        SimpleDateFormat format11 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date11 = format11.format(new Date());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date());
        query += ") values ( ? , ? ,null, ? ,  ? , ? , ? , ? ";
        for (AttributeClass attrsClass : dc.attrs) {
            // if(attr.findByName("Fichier").getId()!=6)
            // {
            listAttrs.add(attrsClass.val.replace("'", "''"));
            query += ", ? ";
            // }
        }

        query += ")";
        Connection conn = DriverManager.getConnection(db, dbus, dbpw);

        System.out.println(query);

        PreparedStatement pe = conn.prepareStatement(query);
        pe.setString(1, id.toString());
        pe.setString(2, date11);
        pe.setLong(3, connectedUser().getUserId());
        pe.setLong(4, connectedUserMaster(connectedUser().getUserId()));
        pe.setString(5, dc.content);
        pe.setString(6, dc.getFileName());
        pe.setString(7, dtr.findById(dc.type).get().getLibelle());
        int i = 8;
        for (String string : listAttrs) {
            System.out.println(string);
            pe.setString(i, string);
            i++;
        }

        pe.executeUpdate();

        conn.close();
        return new ResponseEntity(HttpStatus.OK);
    }

    // GET ALL NATURES
    public List<Nature> gNatures(String action) {

        List<Nature> lst = new ArrayList<Nature>();
        List<Nature> Filtred = new ArrayList<Nature>();

        System.out.println("dqsgdsgdg" + action);

        lst = nr.findByM(getMaster().getUserId());

        if (action.equals("all")) {
            return lst;
        }

        User u = connectedUser();
        int i = 0;
        for (Nature nature : lst) {
            Nature n = nr.findById(nature.getId()).orElse(null);
            if (Objects.nonNull(n)) {
                List<Groupe> groups = grpR.findGroupeByUsersUserId(u.getUserId());
                for (Groupe groupe : groups) {
                    List<PermissionGroupN> permissionGroups = pgnr.findByGroup(groupe);
                    for (PermissionGroupN pd : permissionGroups) {

                        for (PermissionNatureCourrier pdt : pd.getPermissionCourrier().getPermissionNature()) {

                            if (n.getId() == pdt.getNature().getId()
                                    && (pdt.getPermissionCourrier().getAcces().contains(action))) {
                                i = 1;
                            }
                        }
                    }
                }
            }
            if (i == 1) {
                Filtred.add(n);
                i = 0;
            }

        }

        return Filtred;
    }

    @Autowired
    private ProcessusRepo pr;
    @Autowired
    private EtapeRepo er;

    public List<Etape> findStepsByProcess(Long id) {
        List<Etape> lst = new ArrayList<Etape>();
        for (Long d : pr.findByPro(id)) {
            List<User> us = new ArrayList<User>();

            Etape e = er.findById(d).orElse(null);
            e.setUsers(e.getUsers().stream().distinct().collect(Collectors.toList()));

            lst.add(e);
        }

        return lst;
    }

    // VALIDATE STEP N AND GO TO STEP N+1
    /*
     * public ResponseEntity editStep(Long id, EditEtapeClass editEtape) throws
     * ParseException, MessagingException, IOException {
     * 
     * CloneEtape e = ctr.findById(id).orElse(null);
     * 
     * // Ayoub Add this Script
     * List<User> users = new ArrayList();
     * 
     * for (UserClassEtape user2 : editEtape.getUsers()) {
     * 
     * users.add(ur.findById(user2.getUserId()).get());
     * }
     * 
     * 
     * /// Ayoub Add this Script
     * if (e.getEtat() == 0) {
     * Folder f = e.getCourrier();
     * User tr = connectedUser();
     * e.setTraitant(tr);
     * 
     * CloneEtape a = new CloneEtape();
     * 
     * if (e.getDateFin().compareTo(new Date()) > 0) {
     * long diff = e.getDateFin().getTime() - (new Date()).getTime();
     * int decalage = (int) Math.abs(diff / (24 * 60 * 60 * 1000));
     * long ONE_MINUTE_IN_MILLIS = 60000;// millisecs
     * Calendar date = Calendar.getInstance();
     * long t = date.getTimeInMillis();
     * 
     * e.setDateFin(new Date(t + (ONE_MINUTE_IN_MILLIS)));
     * ctr.save(e);
     * List<CloneEtape> steps = ctr.findByCourrierIdOrderByNumeroAsc(f.getId());
     * 
     * for (CloneEtape cc : steps) {
     * 
     * if (cc.getNumero() - e.getNumero() == 1 && e.getIsBack() == 0) {
     * 
     * Calendar c2 = Calendar.getInstance();
     * c2.setTime(new Date());
     * c2.add(Calendar.DATE, cc.getDelai());
     * cc.setDateDebut(new Date());
     * cc.setDateFin(c2.getTime());
     * // Ayoub add This
     * ctr.deleteSteps(cc.getId());
     * a = ctr.save(cc);
     * for (User us : users) {
     * 
     * ctr.addUserEtape(us.getUserId(), a.getId());
     * }
     * try {
     * // SEND MAIL TO NEXT ONE
     * 
     * mailservice.processMail("Annotation : " + editEtape.getComm(), true,
     * tr.getFullName(), users,
     * mailservice.getProp());
     * } catch (Exception e2) {
     * System.out.println("erroooooooooooooooooooooooooooooooor");
     * e2.printStackTrace();
     * 
     * }
     * }
     * if (e.getIsBack() == 1 && cc.getNumero() - e.getNumero() == 1) {
     * 
     * Calendar c = Calendar.getInstance();
     * e.setIsBack(0);
     * // cc.setIsBack(0);
     * cc.setDateDebut(new Date());
     * c.setTime(cc.getDateDebut());
     * c.add(Calendar.DATE, cc.getDelai());
     * cc.setDateFin(c.getTime());
     * cc.setDateTraitement(null);
     * cc.setEtat(0);
     * // Ayoub add This
     * ctr.deleteSteps(cc.getId());
     * a = ctr.save(cc);
     * for (User us : users) {
     * 
     * ctr.addUserEtape(us.getUserId(), a.getId());
     * }
     * 
     * try {
     * // SEND MAIL TO NEXT ONE
     * 
     * mailservice.processMail("Annotation : " + editEtape.getComm(), true,
     * tr.getFullName(), users,
     * mailservice.getProp());
     * } catch (Exception e2) {
     * System.out.println("erroooooooooooooooooooooooooooooooor");
     * e2.printStackTrace();
     * 
     * }
     * }
     * 
     * }
     * 
     * e.setCommentaire(editEtape.getComm());
     * e.setEtat(1);
     * EtapeDetail ed = new EtapeDetail();
     * 
     * User u = connectedUser();
     * ed.setUser(u);
     * int indx = -1;
     * 
     * System.out.println("sqdqsdsqdsq" + e.getUsers().get(0).getFullName());
     * 
     * for (User us : e.getUsers()) {
     * if (us.getUserId().equals(u.getUserId())) {
     * indx = 0;
     * }
     * }
     * if (indx == -1) {
     * 
     * ed.setSuppName(u.getFullName());
     * e.setSuppName(u.getFullName());
     * for (User us : e.getUsers()) {
     * 
     * Long rep = absenceRepo.findBySuppAndDate(u.getUserId(), us.getUserId());
     * if (rep != null) {
     * 
     * User repl = ur.getOne(rep);
     * ed.setUser(repl);
     * e.setTraitant(repl);
     * }
     * }
     * }
     * ed.setDateAvance(new Date());
     * ed.setInstruction(editEtape.getComm());
     * e.setDateTraitement(new Date());
     * 
     * ed.setMotif(null);
     * ed.setDateRet(null);
     * 
     * ed.setEtape(e);
     * edr.save(ed);
     * ctr.save(e);
     * 
     * } else {
     * e.setDateTraitement(new Date());
     * e.setEtat(1);
     * e.setCommentaire(editEtape.getComm());
     * EtapeDetail ed = new EtapeDetail();
     * User u = connectedUser();
     * ed.setUser(u);
     * int indx = -1;
     * for (User us : e.getUsers()) {
     * if (us.getUserId().equals(u.getUserId())) {
     * indx = 0;
     * }
     * }
     * if (indx == -1) {
     * ed.setSuppName(u.getFullName());
     * for (User us : e.getUsers()) {
     * 
     * Long rep = absenceRepo.findBySuppAndDate(u.getUserId(), us.getUserId());
     * if (rep != null) {
     * User repl = ur.getOne(rep);
     * ed.setUser(repl);
     * e.setTraitant(repl);
     * }
     * // .println(us.getUserId() + "ddddd" + u.getUserId());
     * 
     * // //.println(repl.getUsername());
     * }
     * }
     * ed.setDateAvance(new Date());
     * ed.setInstruction(editEtape.getComm());
     * e.setDateTraitement(new Date());
     * e.setTraitant(connectedUser());
     * ed.setMotif(null);
     * ed.setDateRet(null);
     * ed.setEtape(e);
     * ed.setUser(connectedUser());
     * e.setTraitant(connectedUser());
     * edr.save(ed);
     * ctr.save(e);
     * Date d = new Date();
     * List<CloneEtape> steps = ctr.findByCourrierIdOrderByNumeroAsc(f.getId());
     * for (CloneEtape cc : steps) {
     * if (cc.getNumero() - e.getNumero() == 1)
     * 
     * {
     * 
     * int dl = cc.getDelai();
     * 
     * Calendar c = Calendar.getInstance();
     * c.setTime(d);
     * c.add(Calendar.DATE, 1);
     * Calendar c2 = Calendar.getInstance();
     * c2.setTime(d);
     * c2.add(Calendar.DATE, cc.getDelai());
     * 
     * cc.setDateDebut(new Date());
     * cc.setDateFin(c2.getTime());
     * // Ayoub add This
     * 
     * d.setTime(cc.getDateFin().getTime());
     * 
     * // //.println(c2.getTime());
     * a = ctr.save(cc);
     * for (User us : users) {
     * 
     * ctr.addUserEtape(us.getUserId(), a.getId());
     * }
     * 
     * 
     * ;
     * 
     * }
     * 
     * }
     * }
     * return new ResponseEntity(HttpStatus.OK);
     * }
     * 
     * return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
     * 
     * }
     */

    @Transactional
    public ResponseEntity editStep(Long id, EditEtapeClass editEtape, int directbo, int normal, int arrskip,
            String secondary)
            throws ParseException, MessagingException, IOException {

        CloneEtape e = ctr.findById(id).orElse(null);

        /* Ayoub Add this Script */
        List<User> users = new ArrayList<User>();
        List<User> Notifiedusers = new ArrayList<User>();
        List<User> NotifiedusersReal = new ArrayList<User>();
        List<ProfilsAbsence> AbsNotified = new ArrayList<ProfilsAbsence>();

        for (UserClassEtape user2 : editEtape.getUsers()) {
            List<ProfilsAbsence> AbsUserNotifie = absenceRepo.findByUserUserId(user2.getUserId());
            Date dateNow = new Date() ;
            for (ProfilsAbsence user : AbsUserNotifie) {
                
                Date date1 =new SimpleDateFormat("yyyy-MM-dd").parse(user.getDateFin()); 
                if(date1.after(dateNow)) {
                    AbsNotified.add(user); 
                }
               
            }
            users.add(ur.findById(user2.getUserId()).get());
            NotifiedusersReal.add(ur.findById(user2.getUserId()).get());
        }
        

        /* Ayoub Add this Script */
        if (e.getEtat() == 0) {
            Folder f = e.getCourrier();

            /* for mail info */
            CourrierInfo info = new CourrierInfo();
            info.setDest(f.getDest());
            info.setEmet__(f.getEmet__());
            info.setObjet(f.getObjet());
            info.setReference(f.getReference());

            User tr = connectedUser();
            e.setTraitant(tr);

            CloneEtape a = new CloneEtape();

            FolderType ft = ftr.getOne(e.getCourrier().getType().getId());

            if (ft.getCat().equals(ARRIVE)) {

                List<Quality> qualities = qualityRepo.findAll();

                for (User user : users) {
                    User parent = ur.findByUserChild(user.getParent());

                    for (Quality quality : qualities) {
                        if (quality.getRef_bo() == 1) {
                            if (connectedUser().getTitle().equals(quality.getCode())) {
                                while (Objects.nonNull(parent)) {
                                    for (Quality quality1 : qualities) {

                                        if (quality1.getCode().equals(parent.getTitle())) {
                                            if (quality1.getNotifier() == 1) {
                                                Notifiedusers.add(parent);
                                            }
                                        }

                                    }

                                    parent = ur.findByUserChild(parent.getParent());
                                }
                            }
                        }
                    }

                }

            }

            if (e.getDateFin().compareTo(new Date()) > 0) {
                long diff = e.getDateFin().getTime() - (new Date()).getTime();
                int decalage = (int) Math.abs(diff / (24 * 60 * 60 * 1000));
                long ONE_MINUTE_IN_MILLIS = 60000;// millisecs
                Calendar date = Calendar.getInstance();
                long t = date.getTimeInMillis();

                e.setDateFin(new Date(t + (ONE_MINUTE_IN_MILLIS)));
                ctr.save(e);
                List<CloneEtape> steps = ctr.findByCourrierIdOrderByNumeroAsc(f.getId());

                for (CloneEtape cc : steps) {

                    if (normal == 1) {

                        CloneEtape boStep = steps.get(steps.size() - 1);
                        boStep.setReturnBo(0);
                        ctr.save(boStep);

                        if (cc.getNumero() - e.getNumero() == 1 && e.getIsBack() == 0) {

                            Calendar c2 = Calendar.getInstance();
                            c2.setTime(new Date());
                            c2.add(Calendar.DATE, cc.getDelai());
                            cc.setDateDebut(new Date());
                            cc.setDateFin(c2.getTime());
                            cc.setStatusPassed(0);
                            cc.setIsBack(0);
                            cc.setEtat(0);// Ayoub add This
                            ctr.deleteSteps(cc.getId());
                            a = ctr.save(cc);
                            for (User us : users) {

                                ctr.addUserEtape(us.getUserId(), a.getId());
                            }

                            List<Quality> qualities = qualityRepo.findAll();

                            for (Quality quality : qualities) {
                                if (quality.getRef_bo() == 1) {
                                    if (cc.getQuality().equals(quality.getCode()) && e.getIsBack() == 0) {
                                        if (f.getType().getCat().equals(DEPART)) {

                                            if (f.getReference().equals("")) {
                                                Seq seq = seqRepo.findByType(DEPART);

                                                DateFormat dateFormat = new SimpleDateFormat("yyyy");

                                                String str = String.format("%0" + seq.getSeq().length() + "d",
                                                        Long.parseLong(seq.getSeq()) + 1);

                                                seq.setSeq(str);

                                                f.setReference(dateFormat.format(new Date()) + "16" + seq.getSeq());

                                                seqRepo.save(seq);
                                                folderRepo.save(f);
                                            }

                                        }
                                    }
                                }
                            }

                            try {
                                // SEND MAIL TO NEXT ONE
                                if (ft.getCat().equals(ARRIVE)) {
                                    mailservice.processMail("Annotation : " + editEtape.getComm(), true,
                                            tr.getFullName(),
                                            Notifiedusers,
                                            mailservice.getProp(), info, ARRIVE, NotifiedusersReal);
                                } else if (ft.getCat().equals(DEPART)) {
                                    mailservice.processMail("Annotation : " + editEtape.getComm(), true,
                                            tr.getFullName(),
                                            Notifiedusers,
                                            mailservice.getProp(), info, DEPART, NotifiedusersReal);
                                }

                                if (!AbsNotified.isEmpty()) {

                                    if (ft.getCat().equals(ARRIVE)) {

                                        mailservice.proccessMailAbsence(mailservice.getProp(),"Annotation : " + editEtape.getComm(),
                                                f.getId(), AbsNotified, ARRIVE,tr.getFullName());
                                    } else if (ft.getCat().equals(DEPART)) {

                                        mailservice.proccessMailAbsence(mailservice.getProp(),"Annotation : " + editEtape.getComm(),
                                                f.getId(), AbsNotified, DEPART,tr.getFullName());
                                    }
                                }

                            } catch (Exception e2) {
                                System.out.println("erroooooooooooooooooooooooooooooooor");
                                e2.printStackTrace();

                            }
                        }
                        if (e.getIsBack() == 1 && cc.getNumero() - e.getNumero() == 1) {

                            Calendar c = Calendar.getInstance();
                            e.setIsBack(0);
                            // cc.setIsBack(0);
                            cc.setDateDebut(new Date());
                            c.setTime(cc.getDateDebut());
                            c.add(Calendar.DATE, cc.getDelai());
                            cc.setDateFin(c.getTime());
                            cc.setDateTraitement(null);
                            cc.setEtat(0);
                            cc.setStatusPassed(0);
                            cc.setIsBack(0);
                            // Ayoub add This
                            ctr.deleteSteps(cc.getId());
                            a = ctr.save(cc);
                            for (User us : users) {

                                ctr.addUserEtape(us.getUserId(), a.getId());
                            }

                            try {
                                // SEND MAIL TO NEXT ONE

                                if (ft.getCat().equals(ARRIVE)) {
                                    mailservice.processMail("Annotation : " + editEtape.getComm(), true,
                                            tr.getFullName(),
                                            Notifiedusers,
                                            mailservice.getProp(), info, ARRIVE, NotifiedusersReal);
                                } else if (ft.getCat().equals(DEPART)) {
                                    mailservice.processMail("Annotation : " + editEtape.getComm(), true,
                                            tr.getFullName(),
                                            Notifiedusers,
                                            mailservice.getProp(), info, DEPART, NotifiedusersReal);
                                }
                                if (!AbsNotified.isEmpty()) {

                                    if (ft.getCat().equals(ARRIVE)) {

                                        mailservice.proccessMailAbsence(mailservice.getProp(),"Annotation : " + editEtape.getComm(),
                                                f.getId(), AbsNotified, ARRIVE,tr.getFullName());
                                    } else if (ft.getCat().equals(DEPART)) {

                                        mailservice.proccessMailAbsence(mailservice.getProp(),"Annotation : " + editEtape.getComm(),
                                                f.getId(), AbsNotified, DEPART,tr.getFullName());
                                    }
                                }
                            } catch (Exception e2) {
                                System.out.println("erroooooooooooooooooooooooooooooooor");
                                e2.printStackTrace();

                            }
                        }
                    }

                    if (directbo == 1) {

                        List<Quality> qualities = qualityRepo.findAll();

                        for (Quality quality : qualities) {
                            if (quality.getRef_bo() == 1) {
                                if (cc.getQuality().equals(quality.getCode()) && e.getIsBack() == 0) {
                                    Calendar c2 = Calendar.getInstance();
                                    c2.setTime(new Date());
                                    c2.add(Calendar.DATE, cc.getDelai());
                                    cc.setDateDebut(new Date());
                                    cc.setDateFin(c2.getTime());
                                    cc.setPassed(e.getId());
                                    cc.setStatusPassed(0);
                                    cc.setEtat(0);
                                    e.setIsBack(0);
                                    // Ayoub add This
                                    ctr.deleteSteps(cc.getId());
                                    a = ctr.save(cc);
                                    for (User us : users) {

                                        ctr.addUserEtape(us.getUserId(), a.getId());
                                    }

                                    if (f.getType().getCat().equals(DEPART)) {

                                        if (f.getReference().equals("")) {
                                            Seq seq = seqRepo.findByType(DEPART);

                                            DateFormat dateFormat = new SimpleDateFormat("yyyy");

                                            String str = String.format("%0" + seq.getSeq().length() + "d",
                                                    Long.parseLong(seq.getSeq()) + 1);

                                            seq.setSeq(str);

                                            f.setReference(dateFormat.format(new Date()) + "16" + seq.getSeq());

                                            seqRepo.save(seq);
                                            folderRepo.save(f);
                                        }

                                    }

                                    try {
                                        // SEND MAIL TO NEXT ONE

                                        if (ft.getCat().equals(ARRIVE)) {
                                            mailservice.processMail("Annotation : " + editEtape.getComm(), true,
                                                    tr.getFullName(),
                                                    Notifiedusers,
                                                    mailservice.getProp(), info, ARRIVE, NotifiedusersReal);
                                        } else if (ft.getCat().equals(DEPART)) {
                                            mailservice.processMail("Annotation : " + editEtape.getComm(), true,
                                                    tr.getFullName(),
                                                    Notifiedusers,
                                                    mailservice.getProp(), info, DEPART, NotifiedusersReal);
                                        }

                                        if (!AbsNotified.isEmpty()) {

                                            if (ft.getCat().equals(ARRIVE)) {

                                                mailservice.proccessMailAbsence(mailservice.getProp(),"Annotation : " + editEtape.getComm(),
                                                        f.getId(), AbsNotified, ARRIVE,tr.getFullName());
                                            } else if (ft.getCat().equals(DEPART)) {

                                                mailservice.proccessMailAbsence(mailservice.getProp(),"Annotation : " + editEtape.getComm(),
                                                        f.getId(), AbsNotified, DEPART,tr.getFullName());
                                            }
                                        }
                                    } catch (Exception e2) {
                                        System.out.println("erroooooooooooooooooooooooooooooooor");
                                        e2.printStackTrace();

                                    }
                                } else if (cc.getQuality().equals(quality.getCode()) && e.getIsBack() == 1) {
                                    Calendar c = Calendar.getInstance();
                                    e.setIsBack(0);
                                    // cc.setIsBack(0);
                                    cc.setDateDebut(new Date());
                                    c.setTime(cc.getDateDebut());
                                    c.add(Calendar.DATE, cc.getDelai());
                                    cc.setDateFin(c.getTime());
                                    cc.setDateTraitement(null);
                                    cc.setEtat(0);
                                    cc.setPassed(e.getId());
                                    cc.setStatusPassed(0);
                                    // Ayoub add This
                                    ctr.deleteSteps(cc.getId());
                                    a = ctr.save(cc);
                                    for (User us : users) {

                                        ctr.addUserEtape(us.getUserId(), a.getId());
                                    }

                                    try {
                                        // SEND MAIL TO NEXT ONE

                                        if (ft.getCat().equals(ARRIVE)) {
                                            mailservice.processMail("Annotation : " + editEtape.getComm(), true,
                                                    tr.getFullName(),
                                                    Notifiedusers,
                                                    mailservice.getProp(), info, ARRIVE, NotifiedusersReal);
                                        } else if (ft.getCat().equals(DEPART)) {
                                            mailservice.processMail("Annotation : " + editEtape.getComm(), true,
                                                    tr.getFullName(),
                                                    Notifiedusers,
                                                    mailservice.getProp(), info, DEPART, NotifiedusersReal);
                                        }
                                        if (!AbsNotified.isEmpty()) {

                                            if (ft.getCat().equals(ARRIVE)) {

                                                mailservice.proccessMailAbsence(mailservice.getProp(),"Annotation : " + editEtape.getComm(),
                                                        f.getId(), AbsNotified, ARRIVE,tr.getFullName());
                                            } else if (ft.getCat().equals(DEPART)) {

                                                mailservice.proccessMailAbsence(mailservice.getProp(),"Annotation : " + editEtape.getComm(),
                                                        f.getId(), AbsNotified, DEPART,tr.getFullName());
                                            }
                                        }
                                    } catch (Exception e2) {
                                        System.out.println("erroooooooooooooooooooooooooooooooor");
                                        e2.printStackTrace();

                                    }
                                }

                                if (!cc.getQuality().equals(quality.getCode()) && e.getId() != cc.getId()
                                        && cc.getEtat() != 1) {
                                    Calendar c = Calendar.getInstance();
                                    // cc.setIsBack(0);
                                    cc.setDateDebut(new Date());
                                    c.setTime(cc.getDateDebut());
                                    c.add(Calendar.DATE, cc.getDelai());
                                    cc.setDateFin(c.getTime());
                                    cc.setDateTraitement(null);
                                    cc.setStatusPassed(1);
                                    cc.setEtat(1);
                                    cc.setIsBack(1);
                                    ctr.save(cc);
                                }
                            }

                        }

                    }

                    if (arrskip == 1) {

                        if (cc.getId().equals(editEtape.getIdSkiped()) && e.getIsBack() == 0) {
                            Calendar c2 = Calendar.getInstance();
                            c2.setTime(new Date());
                            c2.add(Calendar.DATE, cc.getDelai());
                            cc.setDateDebut(new Date());
                            cc.setDateFin(c2.getTime());
                            cc.setPassed(e.getId());
                            cc.setStatusPassed(0);
                            cc.setEtat(0);
                            cc.setIsBack(0);
                            // Ayoub add This
                            ctr.deleteSteps(cc.getId());
                            a = ctr.save(cc);
                            for (User us : users) {

                                ctr.addUserEtape(us.getUserId(), a.getId());
                            }
                            try {
                                // SEND MAIL TO NEXT ONE

                                if (ft.getCat().equals(ARRIVE)) {
                                    mailservice.processMail("Annotation : " + editEtape.getComm(), true,
                                            tr.getFullName(),
                                            Notifiedusers,
                                            mailservice.getProp(), info, ARRIVE, NotifiedusersReal);
                                } else if (ft.getCat().equals(DEPART)) {
                                    mailservice.processMail("Annotation : " + editEtape.getComm(), true,
                                            tr.getFullName(),
                                            Notifiedusers,
                                            mailservice.getProp(), info, DEPART, NotifiedusersReal);
                                }
                                if (!AbsNotified.isEmpty()) {

                                    if (ft.getCat().equals(ARRIVE)) {

                                        mailservice.proccessMailAbsence(mailservice.getProp(),"Annotation : " + editEtape.getComm(),
                                                f.getId(), AbsNotified, ARRIVE,tr.getFullName());
                                    } else if (ft.getCat().equals(DEPART)) {

                                        mailservice.proccessMailAbsence(mailservice.getProp(),"Annotation : " + editEtape.getComm(),
                                                f.getId(), AbsNotified, DEPART,tr.getFullName());
                                    }
                                }
                            } catch (Exception e2) {
                                System.out.println("erroooooooooooooooooooooooooooooooor");
                                e2.printStackTrace();

                            }
                        } else if (cc.getId().equals(editEtape.getIdSkiped()) && e.getIsBack() == 1) {
                            Calendar c = Calendar.getInstance();
                            e.setIsBack(0);
                            // cc.setIsBack(0);
                            cc.setDateDebut(new Date());
                            c.setTime(cc.getDateDebut());
                            c.add(Calendar.DATE, cc.getDelai());
                            cc.setDateFin(c.getTime());
                            cc.setDateTraitement(null);
                            cc.setEtat(0);
                            cc.setPassed(e.getId());
                            cc.setStatusPassed(0);
                            cc.setIsBack(0);
                            // Ayoub add This
                            ctr.deleteSteps(cc.getId());
                            a = ctr.save(cc);
                            for (User us : users) {

                                ctr.addUserEtape(us.getUserId(), a.getId());
                            }

                            try {
                                // SEND MAIL TO NEXT ONE

                                if (ft.getCat().equals(ARRIVE)) {
                                    mailservice.processMail("Annotation : " + editEtape.getComm(), true,
                                            tr.getFullName(),
                                            Notifiedusers,
                                            mailservice.getProp(), info, ARRIVE, NotifiedusersReal);
                                } else if (ft.getCat().equals(DEPART)) {
                                    mailservice.processMail("Annotation : " + editEtape.getComm(), true,
                                            tr.getFullName(),
                                            Notifiedusers,
                                            mailservice.getProp(), info, DEPART, NotifiedusersReal);
                                }

                                if (!AbsNotified.isEmpty()) {

                                    if (ft.getCat().equals(ARRIVE)) {

                                        mailservice.proccessMailAbsence(mailservice.getProp(),"Annotation : " + editEtape.getComm(),
                                                f.getId(), AbsNotified, ARRIVE,tr.getFullName());
                                    } else if (ft.getCat().equals(DEPART)) {

                                        mailservice.proccessMailAbsence(mailservice.getProp(),"Annotation : " + editEtape.getComm(),
                                                f.getId(), AbsNotified, DEPART,tr.getFullName());
                                    }
                                }
                            } catch (Exception e2) {
                                System.out.println("erroooooooooooooooooooooooooooooooor");
                                e2.printStackTrace();

                            }
                        }

                        if (!cc.getId().equals(editEtape.getIdSkiped()) && e.getId() != cc.getId()
                                && cc.getEtat() != 1) {
                            Calendar c = Calendar.getInstance();
                            // cc.setIsBack(0);
                            cc.setDateDebut(new Date());
                            c.setTime(cc.getDateDebut());
                            c.add(Calendar.DATE, cc.getDelai());
                            cc.setDateFin(c.getTime());
                            cc.setDateTraitement(null);
                            cc.setStatusPassed(1);
                            cc.setEtat(1);
                            cc.setIsBack(1);
                            ctr.save(cc);
                        }
                    }

                }

                e.setCommentaire(editEtape.getComm());
                e.setEtat(1);
                EtapeDetail ed = new EtapeDetail();

                User u = connectedUser();
                ed.setUser(u);
                int indx = -1;

                for (User us : e.getUsers()) {
                    if (us.getUserId().equals(u.getUserId())) {
                        indx = 0;
                    }
                }
                if (indx == -1) {

                    ed.setSuppName(u.getFullName());
                    e.setSuppName(u.getFullName());
                    for (User us : e.getUsers()) {

                        Long rep = absenceRepo.findBySuppAndDate(u.getUserId(), us.getUserId());
                        if (rep != null) {

                            User repl = ur.getOne(rep);
                            ed.setUser(repl);
                            e.setTraitant(repl);
                        }
                    }
                }
                ed.setDateAvance(new Date());
                ed.setInstruction(editEtape.getComm());
                e.setDateTraitement(new Date());

                ed.setMotif(null);
                ed.setDateRet(null);

                ed.setEtape(e);
                edr.save(ed);
                ctr.save(e);

            } else {
                e.setDateTraitement(new Date());
                e.setEtat(1);
                e.setCommentaire(editEtape.getComm());
                EtapeDetail ed = new EtapeDetail();
                User u = connectedUser();
                ed.setUser(u);
                int indx = -1;
                for (User us : e.getUsers()) {
                    if (us.getUserId().equals(u.getUserId())) {
                        indx = 0;
                    }
                }
                if (indx == -1) {
                    ed.setSuppName(u.getFullName());
                    for (User us : e.getUsers()) {

                        Long rep = absenceRepo.findBySuppAndDate(u.getUserId(), us.getUserId());
                        if (rep != null) {
                            User repl = ur.getOne(rep);
                            ed.setUser(repl);
                            e.setTraitant(repl);
                        }
                        // .println(us.getUserId() + "ddddd" + u.getUserId());

                        // //.println(repl.getUsername());
                    }
                }
                ed.setDateAvance(new Date());
                ed.setInstruction(editEtape.getComm());
                e.setDateTraitement(new Date());
                e.setTraitant(connectedUser());
                ed.setMotif(null);
                ed.setDateRet(null);
                ed.setEtape(e);
                ed.setUser(connectedUser());
                e.setTraitant(connectedUser());
                edr.save(ed);
                ctr.save(e);
                Date d = new Date();
                List<CloneEtape> steps = ctr.findByCourrierIdOrderByNumeroAsc(f.getId());
                for (CloneEtape cc : steps) {

                    if (normal == 1) {

                        CloneEtape boStep = steps.get(steps.size() - 1);
                        boStep.setReturnBo(0);
                        ctr.save(boStep);

                        if (cc.getNumero() - e.getNumero() == 1) {

                            int dl = cc.getDelai();

                            Calendar c = Calendar.getInstance();
                            c.setTime(d);
                            c.add(Calendar.DATE, 1);
                            Calendar c2 = Calendar.getInstance();
                            c2.setTime(d);
                            c2.add(Calendar.DATE, cc.getDelai());

                            cc.setDateDebut(new Date());
                            cc.setDateFin(c2.getTime());
                            // Ayoub add This

                            d.setTime(cc.getDateFin().getTime());

                            cc.setStatusPassed(0);
                            cc.setIsBack(0);
                            cc.setEtat(0);// Ayoub add This
                            ctr.deleteSteps(cc.getId());
                            a = ctr.saveAndFlush(cc);
                            for (User us : users) {

                                ctr.addUserEtape(us.getUserId(), a.getId());
                            }

                            List<Quality> qualities = qualityRepo.findAll();

                            for (Quality quality : qualities) {
                                if (quality.getRef_bo() == 1) {
                                    if (cc.getQuality().equals(quality.getCode()) && e.getIsBack() == 0) {
                                        if (f.getType().getCat().equals(DEPART)) {
                                            if (f.getReference().equals("")) {
                                                Seq seq = seqRepo.findByType(DEPART);

                                                DateFormat dateFormat = new SimpleDateFormat("yyyy");

                                                String str = String.format("%0" + seq.getSeq().length() + "d",
                                                        Long.parseLong(seq.getSeq()) + 1);

                                                seq.setSeq(str);

                                                f.setReference(dateFormat.format(new Date()) + "16" + seq.getSeq());

                                                seqRepo.save(seq);
                                                folderRepo.save(f);
                                            }
                                        }
                                    }
                                }
                            }

                            try {
                                // SEND MAIL TO NEXT ONE

                                if (ft.getCat().equals(ARRIVE)) {
                                    mailservice.processMail("Annotation : " + editEtape.getComm(), true,
                                            tr.getFullName(),
                                            Notifiedusers,
                                            mailservice.getProp(), info, ARRIVE, NotifiedusersReal);
                                } else if (ft.getCat().equals(DEPART)) {
                                    mailservice.processMail("Annotation : " + editEtape.getComm(), true,
                                            tr.getFullName(),
                                            Notifiedusers,
                                            mailservice.getProp(), info, DEPART, NotifiedusersReal);
                                }

                                if (!AbsNotified.isEmpty()) {

                                    if (ft.getCat().equals(ARRIVE)) {

                                        mailservice.proccessMailAbsence(mailservice.getProp(),"Annotation : " + editEtape.getComm(),
                                                f.getId(), AbsNotified, ARRIVE,tr.getFullName());
                                    } else if (ft.getCat().equals(DEPART)) {

                                        mailservice.proccessMailAbsence(mailservice.getProp(),"Annotation : " + editEtape.getComm(),
                                                f.getId(), AbsNotified, DEPART,tr.getFullName());
                                    }
                                }
                            } catch (Exception e2) {
                                System.out.println("erroooooooooooooooooooooooooooooooor");
                                e2.printStackTrace();

                            }

                        }
                    }

                    if (directbo == 1) {

                        List<Quality> qualities = qualityRepo.findAll();

                        for (Quality quality : qualities) {
                            if (quality.getRef_bo() == 1) {

                                if (cc.getQuality().equals(quality.getCode())) {

                                    int dl = cc.getDelai();

                                    Calendar c = Calendar.getInstance();
                                    c.setTime(d);
                                    c.add(Calendar.DATE, 1);
                                    Calendar c2 = Calendar.getInstance();
                                    c2.setTime(d);
                                    c2.add(Calendar.DATE, cc.getDelai());

                                    cc.setDateDebut(new Date());
                                    cc.setDateFin(c2.getTime());
                                    // Ayoub add This

                                    d.setTime(cc.getDateFin().getTime());

                                    cc.setPassed(e.getId());
                                    cc.setStatusPassed(0);
                                    cc.setEtat(0);
                                    e.setIsBack(0);
                                    // Ayoub add This
                                    ctr.deleteSteps(cc.getId());
                                    a = ctr.saveAndFlush(cc);
                                    for (User us : users) {

                                        ctr.addUserEtape(us.getUserId(), a.getId());
                                    }

                                    if (f.getReference().equals("")) {
                                        Seq seq = seqRepo.findByType(DEPART);

                                        DateFormat dateFormat = new SimpleDateFormat("yyyy");

                                        String str = String.format("%0" + seq.getSeq().length() + "d",
                                                Long.parseLong(seq.getSeq()) + 1);

                                        seq.setSeq(str);

                                        f.setReference(dateFormat.format(new Date()) + "16" + seq.getSeq());

                                        seqRepo.save(seq);
                                        folderRepo.save(f);
                                    }

                                    try {
                                        // SEND MAIL TO NEXT ONE

                                        if (ft.getCat().equals(ARRIVE)) {
                                            mailservice.processMail("Annotation : " + editEtape.getComm(), true,
                                                    tr.getFullName(),
                                                    Notifiedusers,
                                                    mailservice.getProp(), info, ARRIVE, NotifiedusersReal);
                                        } else if (ft.getCat().equals(DEPART)) {
                                            mailservice.processMail("Annotation : " + editEtape.getComm(), true,
                                                    tr.getFullName(),
                                                    Notifiedusers,
                                                    mailservice.getProp(), info, DEPART, NotifiedusersReal);
                                        }

                                        if (!AbsNotified.isEmpty()) {

                                            if (ft.getCat().equals(ARRIVE)) {

                                                mailservice.proccessMailAbsence(mailservice.getProp(),"Annotation : " + editEtape.getComm(),
                                                        f.getId(), AbsNotified, ARRIVE,tr.getFullName());
                                            } else if (ft.getCat().equals(DEPART)) {

                                                mailservice.proccessMailAbsence(mailservice.getProp(),"Annotation : " + editEtape.getComm(),
                                                        f.getId(), AbsNotified, DEPART,tr.getFullName());
                                            }
                                        }
                                    } catch (Exception e2) {
                                        System.out.println("erroooooooooooooooooooooooooooooooor");
                                        e2.printStackTrace();

                                    }
                                }

                                if (!cc.getQuality().equals(quality.getCode()) && e.getId() != cc.getId()
                                        && cc.getEtat() != 1) {
                                    Calendar c = Calendar.getInstance();
                                    // cc.setIsBack(0);
                                    cc.setDateDebut(new Date());
                                    c.setTime(cc.getDateDebut());
                                    c.add(Calendar.DATE, cc.getDelai());
                                    cc.setDateFin(c.getTime());
                                    cc.setDateTraitement(null);
                                    cc.setStatusPassed(1);
                                    cc.setEtat(1);
                                    cc.setIsBack(1);
                                    ctr.save(cc);
                                }
                            }

                        }

                    }

                    if (arrskip == 1) {

                        if (cc.getId().equals(editEtape.getIdSkiped()) && e.getIsBack() == 0) {

                            int dl = cc.getDelai();

                            Calendar c = Calendar.getInstance();
                            c.setTime(d);
                            c.add(Calendar.DATE, 1);
                            Calendar c2 = Calendar.getInstance();
                            c2.setTime(d);
                            c2.add(Calendar.DATE, cc.getDelai());

                            cc.setDateDebut(new Date());
                            cc.setDateFin(c2.getTime());
                            // Ayoub add This

                            d.setTime(cc.getDateFin().getTime());

                            cc.setPassed(e.getId());
                            cc.setStatusPassed(0);
                            cc.setEtat(0);
                            cc.setIsBack(0);
                            // Ayoub add This
                            ctr.deleteSteps(cc.getId());
                            a = ctr.save(cc);
                            for (User us : users) {

                                ctr.addUserEtape(us.getUserId(), a.getId());
                            }

                            try {
                                // SEND MAIL TO NEXT ONE

                                if (ft.getCat().equals(ARRIVE)) {
                                    mailservice.processMail("Annotation : " + editEtape.getComm(), true,
                                            tr.getFullName(),
                                            Notifiedusers,
                                            mailservice.getProp(), info, ARRIVE, NotifiedusersReal);
                                } else if (ft.getCat().equals(DEPART)) {
                                    mailservice.processMail("Annotation : " + editEtape.getComm(), true,
                                            tr.getFullName(),
                                            Notifiedusers,
                                            mailservice.getProp(), info, DEPART, NotifiedusersReal);
                                }

                                if (!AbsNotified.isEmpty()) {

                                    if (ft.getCat().equals(ARRIVE)) {

                                        mailservice.proccessMailAbsence(mailservice.getProp(),"Annotation : " + editEtape.getComm(),
                                                f.getId(), AbsNotified, ARRIVE,tr.getFullName());
                                    } else if (ft.getCat().equals(DEPART)) {

                                        mailservice.proccessMailAbsence(mailservice.getProp(),"Annotation : " + editEtape.getComm(),
                                                f.getId(), AbsNotified, DEPART,tr.getFullName());
                                    }
                                }
                            } catch (Exception e2) {
                                System.out.println("erroooooooooooooooooooooooooooooooor");
                                e2.printStackTrace();

                            }

                        }

                        if (!cc.getId().equals(editEtape.getIdSkiped()) && e.getId() != cc.getId()
                                && cc.getEtat() != 1) {
                            Calendar c = Calendar.getInstance();
                            // cc.setIsBack(0);
                            cc.setDateDebut(new Date());
                            c.setTime(cc.getDateDebut());
                            c.add(Calendar.DATE, cc.getDelai());
                            cc.setDateFin(c.getTime());
                            cc.setDateTraitement(null);
                            cc.setStatusPassed(1);
                            cc.setEtat(1);
                            cc.setIsBack(1);
                            ctr.save(cc);
                        }
                    }

                }
            }

            if (ur.findById(connectedUserMaster(connectedUser().getUserId())).orElse(null).getSecLevel() == 3) {
                Journal j = new Journal();

                j.setUser(connectedUser());
                j.setDate(new Date());

                j.setMode("A");
                j.setComposante("Valider l'étape");
                if (secondary.equals("true")) {

                    User user = ur.getSecondaryUser(connectedUser().getUserId());

                    j.setTypeEv("Processus/Etape/Secondaire");

                    if (Objects.nonNull(user)) {
                        j.setConnectedSacondaryName(user.getFullName());
                    }

                } else {
                    j.setTypeEv("Processus/Etape");
                }

                j.setAction(" ID COURRIER :" + f.getId() + "| Type :" + f.getType().getName() + "| Réference : "
                        + f.getReference() + "| Date : "
                        + f.getDate() + " | Nature :"
                        + f.getNature().getName() + " | Objet : " + f.getObjet() + " | l'étape a Valider par :   "
                        + connectedUser().getFullName());
                j.setMaster(ur.findById(connectedUserMaster(connectedUser().getUserId())).orElse(null));

                jr.save(j);
            }

            return new ResponseEntity(HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);

    }

    // VALIDATE PROCESS (LAST STEP)
    public ResponseEntity lastStepValidate(Long id, int abondonne, int clot, String commentaire, String secondary)
            throws ParseException {

        CloneEtape e = ctr.getOne(id);

        if (abondonne == 1) {

            if (e.getEtat() == 0) {
                Folder f = e.getCourrier();
                User tr = connectedUser();

                List<CloneEtape> steps = ctr.findByCourrierIdOrderByNumeroAsc(f.getId());

                for (CloneEtape cloneEtape : steps) {

                    if (cloneEtape.getNumero() > e.getNumero()) {
                        cloneEtape.setEtat(1);
                        cloneEtape.setStatusPassed(2);
                        ctr.save(cloneEtape);
                    }

                }
                e.setEtat(1);
                e.setTraitant(connectedUser());

                System.out.println("teruv " + commentaire);
                e.setCommentaire(commentaire);

                EtapeDetail ed = new EtapeDetail();
                User u = connectedUser();
                ed.setUser(u);
                ed.setInstruction(commentaire);
                int indx = -1;
                for (User us : e.getUsers()) {
                    if (us.getUserId().equals(u.getUserId())) {
                        indx = 0;
                    }
                }
                if (indx == -1) {
                    ed.setSuppName(u.getFullName());
                    for (User us : e.getUsers()) {

                        Long rep = absenceRepo.findBySuppAndDate(u.getUserId(), us.getUserId());
                        if (rep != null) {
                            User repl = ur.getOne(rep);
                            ed.setUser(repl);
                            e.setTraitant(repl);
                        }

                        // //.println(repl.getUsername());
                    }
                }

                e.setDateTraitement(new Date());
                ed.setMotif(null);
                ed.setDateRet(null);
                ed.setUser(connectedUser());
                ed.setEtape(e);
                edr.save(ed);
                ctr.save(e);
                // && (f.getAddedAcces().equals("") || f.getAddedAcces().equals(null))

                f.setFinalise("abondonne");

                fr.save(f);

                User parent = ur.findByUserChild(connectedUser().getParent());

                try {

                    mailservice.notifyParentAbonndoner(parent, connectedUser().getFullName(), f.getReference());

                } catch (Exception e2) {
                    System.out.println("erroooooooooooooooooooooooooooooooor");
                    e2.printStackTrace();

                }

                if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
                    Journal j = new Journal();

                    j.setUser(connectedUser());
                    j.setDate(new Date());

                    j.setMode("A");
                    j.setComposante("Abandonné Courrier");
                    if (secondary.equals("true")) {

                        User user = ur.getSecondaryUser(connectedUser().getUserId());

                        j.setTypeEv("Processus/Etape/Secondaire");

                        if (Objects.nonNull(user)) {
                            j.setConnectedSacondaryName(user.getFullName());
                        }

                    } else {
                        j.setTypeEv("Processus/Etape");
                    }
                    j.setAction(" ID COURRIER :" + f.getId() + "| Type :" + f.getType().getName() + "| Réference : "
                            + f.getReference() + "| Date : "
                            + f.getDate() + " | Nature :"
                            + f.getNature().getName() + " | Objet : " + f.getObjet() + " | Courrier a Abandonné  par :   "
                            + connectedUser().getFullName());
                    
                    j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

                    jr.save(j);
                }

                return new ResponseEntity(HttpStatus.OK);
            }
            return new ResponseEntity(HttpStatus.CONFLICT);

        } else if (clot == 1) {

            if (e.getEtat() == 0) {
                Folder f = e.getCourrier();
                User tr = connectedUser();

                List<CloneEtape> steps = ctr.findByCourrierIdOrderByNumeroAsc(f.getId());

                for (CloneEtape cloneEtape : steps) {

                    if (cloneEtape.getNumero() > e.getNumero()) {
                        cloneEtape.setEtat(1);
                        cloneEtape.setStatusPassed(3);
                        ctr.save(cloneEtape);
                    }

                }
                e.setEtat(1);
                e.setTraitant(connectedUser());

                EtapeDetail ed = new EtapeDetail();
                User u = connectedUser();
                ed.setUser(u);
                int indx = -1;
                for (User us : e.getUsers()) {
                    if (us.getUserId().equals(u.getUserId())) {
                        indx = 0;
                    }
                }
                if (indx == -1) {
                    ed.setSuppName(u.getFullName());
                    for (User us : e.getUsers()) {

                        Long rep = absenceRepo.findBySuppAndDate(u.getUserId(), us.getUserId());
                        if (rep != null) {
                            User repl = ur.getOne(rep);
                            ed.setUser(repl);
                            e.setTraitant(repl);
                        }

                        // //.println(repl.getUsername());
                    }
                }

                e.setDateTraitement(new Date());
                ed.setMotif(null);
                ed.setDateRet(null);
                ed.setUser(connectedUser());
                ed.setEtape(e);
                edr.save(ed);
                ctr.save(e);
                // && (f.getAddedAcces().equals("") || f.getAddedAcces().equals(null))

                try {
                    integrationService.exportDocuments(e.getCourrier().getId());
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                f.setFinalise("fini");
                // f.setFinalise("fini");
                fr.save(f);

                if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
                    Journal j = new Journal();

                    j.setUser(connectedUser());
                    j.setDate(new Date());

                    j.setMode("A");
                    j.setComposante("Clôturé Courrier");
                    if (secondary.equals("true")) {

                        User user = ur.getSecondaryUser(connectedUser().getUserId());

                        j.setTypeEv("Processus/Etape/Secondaire");

                        if (Objects.nonNull(user)) {
                            j.setConnectedSacondaryName(user.getFullName());
                        }

                    } else {
                        j.setTypeEv("Processus/Etape");
                    }
                    j.setAction(" ID COURRIER :" + f.getId() + "| Type :" + f.getType().getName() + "| Réference : "
                            + f.getReference() + "| Date : "
                            + f.getDate() + " | Nature :"
                            + f.getNature().getName() + " | Objet : " + f.getObjet() + " | Courrier a Clôturé  par :   "
                            + connectedUser().getFullName());
                    
                    j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

                    jr.save(j);
                }

                return new ResponseEntity(HttpStatus.OK);
            }
            return new ResponseEntity(HttpStatus.CONFLICT);

        } else {

            if (e.getEtat() == 0) {
                Folder f = e.getCourrier();
                e.setEtat(1);
                e.setTraitant(connectedUser());

                EtapeDetail ed = new EtapeDetail();
                User u = connectedUser();
                ed.setUser(u);
                int indx = -1;
                for (User us : e.getUsers()) {
                    if (us.getUserId().equals(u.getUserId())) {
                        indx = 0;
                    }
                }
                if (indx == -1) {
                    ed.setSuppName(u.getFullName());
                    for (User us : e.getUsers()) {

                        Long rep = absenceRepo.findBySuppAndDate(u.getUserId(), us.getUserId());
                        if (rep != null) {
                            User repl = ur.getOne(rep);
                            ed.setUser(repl);
                            e.setTraitant(repl);
                        }

                        // //.println(repl.getUsername());
                    }
                }

                e.setDateTraitement(new Date());
                ed.setMotif(null);
                ed.setDateRet(null);
                ed.setUser(connectedUser());
                ed.setEtape(e);
                edr.save(ed);
                ctr.save(e);
                // && (f.getAddedAcces().equals("") || f.getAddedAcces().equals(null))

                if (f.getAccuse() == 1) {

                    f.setFinalise("accusation");
                } else {
                    f.setFinalise("fini");
                    try {
                        integrationService.exportDocuments(e.getCourrier().getId());
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }

                // f.setFinalise("fini");
                fr.save(f);

                if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
                    Journal j = new Journal();

                    j.setUser(connectedUser());
                    j.setDate(new Date());

                    j.setMode("A");
                    j.setComposante("Courrier a Finalisé");
                    if (secondary.equals("true")) {

                        User user = ur.getSecondaryUser(connectedUser().getUserId());

                        j.setTypeEv("Processus/Etape/Secondaire");

                        if (Objects.nonNull(user)) {
                            j.setConnectedSacondaryName(user.getFullName());
                        }

                    } else {
                        j.setTypeEv("Processus/Etape");
                    }
                    j.setAction(" ID COURRIER :" + f.getId() + "| Type :" + f.getType().getName() + "| Réference : "
                            + f.getReference() + "| Date : "
                            + f.getDate() + " | Nature :"
                            + f.getNature().getName() + " | Objet : " + f.getObjet() + " | Courrier a Finalisé  par :   "
                            + connectedUser().getFullName());
                    
                    j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

                    jr.save(j);
                }

                return new ResponseEntity(HttpStatus.OK);
            }
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
    }
//
//    Pattern NUMBER = Pattern.compile("\\d+");
//
//    public String increment(String input) {
//        return NUMBER.matcher(input)
//            .replaceFirst(s -> String.format(
//                "%0" + s.group().length() + "d",
//                Integer.parseInt(s.group()) + 1));
//    }

    // GO TO PREVIOUS STEP
    public ResponseEntity returnCourrier(Long idStep, String motifRetour) throws MessagingException, IOException {
        CloneEtape e = ctr.findById(idStep).orElse(null);
        User tr = connectedUser();
        if (e.getEtat() == 0) {
            Folder f = e.getCourrier();

            CourrierInfo info = new CourrierInfo();
            info.setDest(f.getDest());
            info.setEmet__(f.getEmet__());
            info.setObjet(f.getObjet());
            info.setReference(f.getReference());

            if (e.getDateFin().compareTo(new Date()) > 0) {
                long ONE_MINUTE_IN_MILLIS = 60000;// millisecs
                Calendar date = Calendar.getInstance();
                long t = date.getTimeInMillis();

                e.setDateFin(new Date(t + (ONE_MINUTE_IN_MILLIS)));

                ctr.save(e);

            }

            CloneEtape ce = getpreviousStep(idStep);
            try {
                System.out.println("entred!!");
                mailservice.processMail("Motif de retour : " + motifRetour, false, tr.getFullName(), ce.getUsers(),
                        mailservice.getProp(), info, ARRIVE, null);
            } catch (Exception e2) {
                // on back
                e2.printStackTrace();
            }
            CloneEtape ct = e;
            ct.setMotifDeRetour(motifRetour);
            ct.setEtat(1);
            e.setTraitant(tr);
            ct.setTraitant(tr);
            // ct.setDateFin(new Date());
            ct.setDateTraitement(new Date());
            ce.setIsBack(1);
            ce.setEtat(0);
            ce.setDateTraitement(null);
            EtapeDetail ed = new EtapeDetail();

            User u = connectedUser();
            ed.setUser(u);
            int indx = -1;
            for (User us : e.getUsers()) {
                if (us.getUserId().equals(u.getUserId())) {
                    indx = 0;
                }
            }
            if (indx == -1) {
                ed.setSuppName(u.getFullName());
                e.setSuppName(u.getFullName());
                for (User us : e.getUsers()) {

                    Long rep = absenceRepo.findBySuppAndDate(u.getUserId(), us.getUserId());
                    if (rep != null) {
                        User repl = ur.getOne(rep);
                        ed.setUser(repl);
                        e.setTraitant(repl);
                    }
                }
            }
            ed.setDateAvance(null);
            ed.setInstruction(null);

            e.setDateTraitement(new Date());

            ed.setMotif(motifRetour);
            ed.setDateRet(new Date());
            ed.setEtape(e);
            edr.save(ed);
            ce.setDateDebut(new Date());
            int decalage = ce.getDelaiRet();
            Calendar dc = Calendar.getInstance();
            dc.setTime(new Date());
            dc.add(Calendar.DATE, decalage);
            ce.setDateFin(dc.getTime());
            ctr.save(ce);
            ctr.save(ct);
            return new ResponseEntity(HttpStatus.OK);

        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity returnCourrier1(Long idStep, String motifRetour, String secondary)
            throws MessagingException, IOException {
        CloneEtape e = ctr.findById(idStep).orElse(null);
        User tr = connectedUser();

        if (Objects.nonNull(e.getPassed())) {
            if (e.getEtat() == 0) {
                Folder f = e.getCourrier();

                CourrierInfo info = new CourrierInfo();
                info.setDest(f.getDest());
                info.setEmet__(f.getEmet__());
                info.setObjet(f.getObjet());
                info.setReference(f.getReference());

                if (e.getDateFin().compareTo(new Date()) > 0) {
                    long ONE_MINUTE_IN_MILLIS = 60000;// millisecs
                    Calendar date = Calendar.getInstance();
                    long t = date.getTimeInMillis();

                    e.setDateFin(new Date(t + (ONE_MINUTE_IN_MILLIS)));

                    ctr.save(e);

                }

                CloneEtape ce;

                ce = ctr.findById(e.getPassed()).get();

                try {
                    System.out.println("entred!!");
                    mailservice.processMail("Motif de retour : " + motifRetour, false, tr.getFullName(), ce.getUsers(),
                            mailservice.getProp(), info, ARRIVE, null);
                } catch (Exception e2) {
                    // on back
                    e2.printStackTrace();
                }
                CloneEtape ct = e;
                ct.setMotifDeRetour(motifRetour);
                ct.setEtat(1);
                e.setTraitant(tr);
                ct.setTraitant(tr);
                // ct.setDateFin(new Date());
                ct.setDateTraitement(new Date());
                ce.setIsBack(1);
                ce.setEtat(0);
                ce.setDateTraitement(null);
                ct.setPassed(null);
                ct.setReturnBo(1);
                EtapeDetail ed = new EtapeDetail();

                User u = connectedUser();
                ed.setUser(u);
                int indx = -1;
                for (User us : e.getUsers()) {
                    if (us.getUserId().equals(u.getUserId())) {
                        indx = 0;
                    }
                }
                if (indx == -1) {
                    ed.setSuppName(u.getFullName());
                    e.setSuppName(u.getFullName());
                    for (User us : e.getUsers()) {

                        Long rep = absenceRepo.findBySuppAndDate(u.getUserId(), us.getUserId());
                        if (rep != null) {
                            User repl = ur.getOne(rep);
                            ed.setUser(repl);
                            e.setTraitant(repl);
                        }
                    }
                }
                ed.setDateAvance(null);
                ed.setInstruction(null);

                e.setDateTraitement(new Date());

                ed.setMotif(motifRetour);
                ed.setDateRet(new Date());
                ed.setEtape(e);
                edr.save(ed);
                ce.setDateDebut(new Date());
                int decalage = ce.getDelaiRet();
                Calendar dc = Calendar.getInstance();
                dc.setTime(new Date());
                dc.add(Calendar.DATE, decalage);
                ce.setDateFin(dc.getTime());
                ctr.save(ce);
                ctr.save(ct);

                if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
                    Journal j = new Journal();

                    j.setUser(connectedUser());
                    j.setDate(new Date());

                    j.setMode("A");
                    j.setComposante("Retourner l'étape");
                    if (secondary.equals("true")) {

                        User user = ur.getSecondaryUser(connectedUser().getUserId());

                        j.setTypeEv("Processus/Etape/Secondaire");

                        if (Objects.nonNull(user)) {
                            j.setConnectedSacondaryName(user.getFullName());
                        }

                    } else {
                        j.setTypeEv("Processus/Etape");
                    }
                    j.setAction(" ID COURRIER :" + f.getId() + "| Type :" + f.getType().getName() + "| Réference : "
                            + f.getReference() + "| Date : "
                            + f.getDate() + " | Nature :"
                            + f.getNature().getName() + " | Objet : " + f.getObjet() + " | l'étape a retourné par :   "
                            + connectedUser().getFullName());
                    j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

                    jr.save(j);
                }

                return new ResponseEntity(HttpStatus.OK);

            }

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            if (e.getEtat() == 0) {
                Folder f = e.getCourrier();

                CourrierInfo info = new CourrierInfo();
                info.setDest(f.getDest());
                info.setEmet__(f.getEmet__());
                info.setObjet(f.getObjet());
                info.setReference(f.getReference());

                if (e.getDateFin().compareTo(new Date()) > 0) {
                    long ONE_MINUTE_IN_MILLIS = 60000;// millisecs
                    Calendar date = Calendar.getInstance();
                    long t = date.getTimeInMillis();

                    e.setDateFin(new Date(t + (ONE_MINUTE_IN_MILLIS)));

                    ctr.save(e);

                }

                CloneEtape ce = getpreviousStep(idStep);
                try {
                    System.out.println("entred!!");
                    mailservice.processMail("Motif de retour : " + motifRetour, false, tr.getFullName(), ce.getUsers(),
                            mailservice.getProp(), info, ARRIVE, null);
                } catch (Exception e2) {
                    // on back
                    e2.printStackTrace();
                }
                CloneEtape ct = e;
                ct.setMotifDeRetour(motifRetour);
                ct.setEtat(1);
                e.setTraitant(tr);
                ct.setTraitant(tr);
                // ct.setDateFin(new Date());
                ct.setDateTraitement(new Date());
                ce.setIsBack(1);
                ce.setEtat(0);
                ce.setDateTraitement(null);
                EtapeDetail ed = new EtapeDetail();

                User u = connectedUser();
                ed.setUser(u);
                int indx = -1;
                for (User us : e.getUsers()) {
                    if (us.getUserId().equals(u.getUserId())) {
                        indx = 0;
                    }
                }
                if (indx == -1) {
                    ed.setSuppName(u.getFullName());
                    e.setSuppName(u.getFullName());
                    for (User us : e.getUsers()) {

                        Long rep = absenceRepo.findBySuppAndDate(u.getUserId(), us.getUserId());
                        if (rep != null) {
                            User repl = ur.getOne(rep);
                            ed.setUser(repl);
                            e.setTraitant(repl);
                        }
                    }
                }
                ed.setDateAvance(null);
                ed.setInstruction(null);

                e.setDateTraitement(new Date());

                ed.setMotif(motifRetour);
                ed.setDateRet(new Date());
                ed.setEtape(e);
                edr.save(ed);
                ce.setDateDebut(new Date());
                int decalage = ce.getDelaiRet();
                Calendar dc = Calendar.getInstance();
                dc.setTime(new Date());
                dc.add(Calendar.DATE, decalage);
                ce.setDateFin(dc.getTime());
                ctr.save(ce);
                ctr.save(ct);

                if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
                    Journal j = new Journal();

                    j.setUser(connectedUser());
                    j.setDate(new Date());

                    j.setMode("A");
                    j.setComposante("Retourner l'étape");
                    if (secondary.equals("true")) {

                        User user = ur.getSecondaryUser(connectedUser().getUserId());

                        j.setTypeEv("Processus/Etape/Secondaire");

                        if (Objects.nonNull(user)) {
                            j.setConnectedSacondaryName(user.getFullName());
                        }

                    } else {
                        j.setTypeEv("Processus/Etape");
                    }
                    j.setAction(" ID COURRIER :" + f.getId() + "| Type :" + f.getType().getName() + "| Réference : "
                            + f.getReference() + "| Date : "
                            + f.getDate() + " | Nature :"
                            + f.getNature().getName() + " | Objet : " + f.getObjet() + " | l'étape a retourné par :   "
                            + connectedUser().getFullName());
                    j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

                    jr.save(j);
                }

                return new ResponseEntity(HttpStatus.OK);

            }

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // get Comment (Step-1)
    public CloneEtape getComment(UUID id, int Step, Long idStep) {
        Folder c = fr.findByID(id);
        CloneEtape cc = ctr.findById(idStep).get();
        for (CloneEtape ce : c.getEtapes()) {

            if (ce.getNumero() == Step - 1 && ce.getStatusPassed() == 0) {
                return ce;
            }

            System.out.println("test : " + ce.getId() + " Vs " + cc.getPassed());
            if (ce.getId().equals(cc.getPassed())) {
                return ce;
            }

        }
        return null;
    }

    // get MOTIF (Step+1)
    public CloneEtape getMotif(UUID id, int Step, Long idStep) {
        Folder c = fr.findByID(id);
        CloneEtape cc = ctr.findById(idStep).get();
        for (CloneEtape ce : c.getEtapes()) {

            if (ce.getNumero() == Step + 1 && ce.getStatusPassed() == 0) {
                return ce;
            }

            if (Objects.nonNull(ce.getPassed())) {
                if (ce.getPassed().equals(cc.getId())) {
                    return ce;
                }
            }

            if (Objects.nonNull(ce.getReturnBo())) {
                if (ce.getReturnBo() == 1) {
                    return c.getEtapes().get(c.getEtapes().size() - 1);
                }
            }

        }
        return null;
    }

    // get courrier steps
    public List<CloneEtape> courrierSteps(UUID id) {
        // Folder c = fr.findByID(id);
        return ctr.findByCourrierIdOrderByNumeroAsc(id);

    }

    // FIND STEP-1
    public CloneEtape getpreviousStep(Long idStep) {
        CloneEtape ce = ctr.findById(idStep).orElse(null);
        Folder f = ce.getCourrier();
        for (CloneEtape ct : ctr.findByCourrierIdOrderByNumeroAsc(f.getId())) {
            if (ce.getNumero() - ct.getNumero() == 1) {
                return ct;
            }
        }
        return null;
    }

    // ADD NEW EMETTEUR (FORMULAIRE)
    public ClientDoc addClientDoc(Long mst, String mail, String message, String nom, String prenom, String num,
            String titre, String objet) {

        ClientDoc cd = new ClientDoc();

        cd.setEmail(mail);
        cd.setMessage(message);
        cd.setMaster(mst);
        cd.setDocuments(null);
        cd.setNom(nom);
        cd.setPrenom(prenom);
        cd.setNum(num);
        cd.setTitre(titre);
        cd.setObjet(objet);
        cd.setEmetteur_id(null);
        cd.setSentDate(new Date());
        cd = cdr.saveAndFlush(cd);
        // .println("xxxxxxxxxxxxxxxxxxxxxxxxxxx");

        return cd;

    }

    // ADD DOCS FORMULAIRE
    public Map<String, String> addDocByClient(String fileName, Long mst, Long clt) {
        Document doc = new Document();
        // doc.setClient(cltr.getOne(connectedUser().getIsClient()));
        // doc.setId(UUID.randomUUID());
        doc.setUpload_date((new Date()));
        doc.setIs_deleted(false);
        doc.setLast_edit_date(null);
        doc.setFileName(fileName);
        doc.setMaster(ur.getOne(mst));
        doc.setFolders(null);
        doc.setClientDoc(cdr.getOne(clt));
        Date date1 = doc.getUpload_date();
        LocalDate localDate = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        String path = masterConfigService.findActivePathByMaster(mst) + "\\upload" + "\\"
                + Base64.getEncoder().encodeToString((String.valueOf(doc.getUpload_date().getYear())).getBytes()) + "\\"
                + Base64.getEncoder().encodeToString((String.valueOf(doc.getUpload_date().getMonth())).getBytes())
                + "\\" + Base64.getEncoder().encodeToString((String.valueOf(localDate.getDayOfMonth())).getBytes())
                + "\\";
        doc.setPathServer(path);
        doc = dr.saveAndFlush(doc);

        Map<String, String> idMap = new HashMap<>();
        idMap.put("id", doc.getId().toString());
        // .println("-------------------here");
        return idMap;
    }

    public final Path root = Paths.get("uploads");
    public Path path = null;
    public static final String UploadRoot = System.getProperty("user.dir") + "/upload";

    // ADD DOCS FORMULAIRE (FILE)
    public void saveClientDoc(UUID id, MultipartFile file) throws IOException {
        final Document doc = dr.getOne(id);

        final File saveInRootPath = new File(doc.getPathServer());
        // doc.setContentType(file.getContentType());
        // // doc.setFileName(file.getOriginalFilename());
        // // .println(file.getOriginalFilename());
        // if (!saveInRootPath.exists()) {
        // saveInRootPath.mkdirs();
        // }
        // path = Paths.get(saveInRootPath.toString());
        // final java.io.InputStream is = file.getInputStream();

        // Files.copy(is, this.path.resolve(id.toString() + "_" +
        // file.getOriginalFilename()));
        // // .println(id.toString() + "_" + file.getOriginalFilename());
        // dr.save(doc);
        byte[] data = null;
        try {
            doc.setContentType(file.getContentType());

            if (!saveInRootPath.exists()) {
                saveInRootPath.mkdirs();
            }
            path = Paths.get(saveInRootPath.toString());
            // final java.io.InputStream is = file.getInputStream();
            data = file.getBytes();
            // Files.copy(is, this.path.resolve(id.toString() + "_" +
            // file.getOriginalFilename()));
            // .println(id.toString() + "_" + file.getOriginalFilename());
            dr.save(doc);
        } catch (final Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
        String datab64 = new String(Base64.getEncoder().encode(data));
        final String pathServ = doc.getPathServer();
        String pathName = pathServ + id.toString() + "_" + file.getOriginalFilename().split("[.]")[0] + ".documania";
        doc.setPathServer(pathName);
        dr.save(doc);
        // try
        // {
        File f = new File(pathName);
        f.createNewFile();
        FileWriter myWriter = new FileWriter(pathName);
        myWriter.write(datab64);
        myWriter.close();
    }

    public ResponseEntity deleteClientDoc(Long id) {
        cdr.delete(cdr.getOne(id));
        return new ResponseEntity(HttpStatus.OK);
    }

    @Autowired
    zipMailRepo zmr;

    // CHECK IF ZIP OF FILES EXIST
    public String checkifexist(UUID id) {
        zipMail z = zmr.findById(id).orElse(null);
        if (z == null) {
            return "notExist";
        }
        if (z.getHasPassword() == 0) {
            return "nopw";
        }
        return "pw";
    }

    // CHECK PASSWORD AND DOWNLOAD ZIP
    public File64 checkPwAndGetFile(UUID id, String pw) throws IOException {
        zipMail z = zmr.findById(id).orElse(null);
        // .println(pw);
        if ((z.getHasPassword() == 1 && z.getPassowrd().equals(pw)) || z.getHasPassword() == 0) {

            return fileStorageServiceImpl.downloadZip(z);
        }
        // .println(z.getPassowrd().equals(pw));
        return null;
    }

    // GET PROCESS BY NATURE ID
    public List<Etape> getStepsByNature(Long id) {
        Nature n = nr.findById(id).orElse(null);
        return er.findEtapeByProcessusIdOrderByNumeroAsc(n.getProcess().getId());
    }

    public List<Etape> getStepsByProc(Long id) {

        return er.findEtapeByProcessusIdOrderByNumeroAsc(id);
    }

    // GET ALL SENDERS
    public List<Sender> getAllSenders() {
        return senderRepo.findSenderByMasterUserId(connectedUserMaster(connectedUser().getUserId()));
    }
    // GET ALL RECEIVERS

    public List<receiver> getAllReceivers() {
        return receiverRepo.findReceiverByMasterUserId(connectedUserMaster(connectedUser().getUserId()));
    }

    public CloneEtape findCurrentStepByFolder(UUID id) {
        return ctr.findByCourrierIdAndEtatAndDateFinIsNotNull(id, 0);
    }

    // User has access to
    public int hasAccessTo(Long id, String action) {

        User u = connectedUser();
        DocumentType dt = dtr.findById(id).orElse(null);
        if (Objects.nonNull(dt)) {
            List<Groupe> groups = grpR.findGroupeByUsersUserId(u.getUserId());
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
}
