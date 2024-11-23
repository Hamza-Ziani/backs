package com.veviosys.vdigit.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.Gson;
import com.veviosys.vdigit.classes.AttributeClass;
import com.veviosys.vdigit.classes.DocumnentClass;
import com.veviosys.vdigit.classes.FolderClass;
import com.veviosys.vdigit.classes.NativeQueriesUtils;
import com.veviosys.vdigit.classes.Search;
import com.veviosys.vdigit.classes.SearchAttrs;
import com.veviosys.vdigit.classes.SearchResultColumn;
import com.veviosys.vdigit.classes.SubSearchClass;
import com.veviosys.vdigit.classes.UserClassEtape;
import com.veviosys.vdigit.classes.mapSearch;
import com.veviosys.vdigit.models.Attribute;
import com.veviosys.vdigit.models.CloneEtape;
import com.veviosys.vdigit.models.Document;
import com.veviosys.vdigit.models.DocumentAttributeValue;
import com.veviosys.vdigit.models.DocumentFullText;
import com.veviosys.vdigit.models.DocumentType;
import com.veviosys.vdigit.models.DocumentVersion;
import com.veviosys.vdigit.models.Folder;
import com.veviosys.vdigit.models.FrequencySearch;
import com.veviosys.vdigit.models.Groupe;
import com.veviosys.vdigit.models.Journal;
import com.veviosys.vdigit.models.PermissionDocumentType;
import com.veviosys.vdigit.models.PermissionGroup;
import com.veviosys.vdigit.models.PermissionGroupN;
import com.veviosys.vdigit.models.PermissionNatureCourrier;
import com.veviosys.vdigit.models.ProfilsAbsence;
import com.veviosys.vdigit.models.SearchAttributeValue;
import com.veviosys.vdigit.models.Sender;
import com.veviosys.vdigit.models.Track;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.models.receiver;
import com.veviosys.vdigit.models.pk.SearchAttributeValuePk;
import com.veviosys.vdigit.reposietories.AttributeRepo;
import com.veviosys.vdigit.reposietories.AttributeValueRepo;
import com.veviosys.vdigit.repositories.ClientRepo;
import com.veviosys.vdigit.repositories.DocumentAttributeValueRepo;
import com.veviosys.vdigit.repositories.DocumentFulltextRepository;
import com.veviosys.vdigit.repositories.DocumentRepo;
import com.veviosys.vdigit.repositories.DocumentTypeRepo;
import com.veviosys.vdigit.repositories.DocumentVersionRepo;
import com.veviosys.vdigit.repositories.FolderRepo;
import com.veviosys.vdigit.repositories.FolderTypeRepo;
import com.veviosys.vdigit.repositories.FoldersRelationsRepo;
import com.veviosys.vdigit.repositories.FrequencySearchRepo;
import com.veviosys.vdigit.repositories.GroupRepo;
import com.veviosys.vdigit.repositories.JournalRepo;
import com.veviosys.vdigit.repositories.NatureRepo;
import com.veviosys.vdigit.repositories.PermissionGroupNRepo;
import com.veviosys.vdigit.repositories.PermissionGroupRepo;
import com.veviosys.vdigit.repositories.ReceiverRepo;
import com.veviosys.vdigit.repositories.SearchAttrValRepo;
import com.veviosys.vdigit.repositories.SearchAttributeRepo;
import com.veviosys.vdigit.repositories.SenderRepo;
import com.veviosys.vdigit.repositories.TrackRepo;
import com.veviosys.vdigit.repositories.UserRepository;
import com.veviosys.vdigit.repositories.profilsAbsenceRepo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class SearchService {

    @Autowired
    private UserRepository ur;
    @Autowired
    private FolderTypeRepo ftr;

    @Autowired
    private FolderRepo fr;
    @Autowired
    private TrackRepo trackRepo;
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
    private SearchAttributeRepo sattr;
    @Autowired
    private FrequencySearchRepo fsr;
    @Autowired
    private SearchAttrValRepo savr;
    @Autowired
    private FoldersRelationsRepo foldersRelationsRepo;
    @Autowired
    private DocumentVersionRepo dvr;
    @Autowired
    private GroupRepo gr;
    @Autowired
    private PermissionGroupRepo pgr;
    @Autowired
    NatureRepo nr;
    @Autowired
    private PermissionGroupNRepo pgnr;
    @Autowired
    private JournalRepo jr;
    @Autowired
    DocumentFulltextRepository dfr;
    @Autowired
    ReceiverRepo receiverRepo;
    @Autowired
    profilsAbsenceRepo absenceRepo;
    @Autowired
    SenderRepo senderRepo;
    @Value("${documania.datasource.select}")
    private String selectedDb;
    @Value("${db.name}")
    private String db;
    @Value("${db.user}")
    private String dbus;
    @Value("${db.pw}")
    private String dbpw;

    public SearchService() {

    }

    public User getMaster() {
        return ur.getOne(connectedUserMaster(connectedUser().getUserId()));
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

    // Methode to search folders normal

    // Search and remove parent
    public Page<Folder> searchFolders(FolderClass fc, Pageable pageable, UUID id, String secondary) {

        return fr.findAll(new Specification<Folder>() {

            @Override
            public Predicate toPredicate(Root<Folder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                // SQLStatementCountValidator.reset();
                Predicate p = criteriaBuilder.conjunction();
                User mst = getMaster();
                p = criteriaBuilder.equal(root.get("master"), mst);
                p = criteriaBuilder.and(p, criteriaBuilder.notEqual(root.get("id"), id));
                String a = "";
                if (Objects.nonNull(fc.getType())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("type").get("id"), fc.getType()));
                    if (a.equals("")) {
                        a += " Type : " + ftr.findById(fc.getType()).orElse(null).getName();

                    } else {
                        a += "| Type : " + ftr.findById(fc.getType()).orElse(null).getName();

                    }

                }

                if (fc.getMode() == 1 && fc.getDest().size() > 0) {

                    /*
                     * for (String name : fc.getDest()) {
                     * p = criteriaBuilder.and(p,
                     * criteriaBuilder.like(criteriaBuilder.upper(root.get("dest__")),
                     * "%" + name.toUpperCase() + "%"));
                     * }
                     */

                    p = criteriaBuilder.and(p, root.get("id").in(fr.findFolderDest(fc.getDest())));

                }
                if (fc.getMode() == 2 && Objects.nonNull(fc.getSender()) && !fc.getSender().equals("")) {
                    p = criteriaBuilder.and(p,
                            criteriaBuilder.like(criteriaBuilder.upper(root.get("emet__")),
                                    "%" + fc.getSender().toUpperCase() + "%"));
                    // p = criteriaBuilder.and(p,
                    // criteriaBuilder.equal(root.get("sender").get("name"), fc.getSender()));
                    // sssss
                }

                if (Objects.nonNull(fc.getReference()) && !StringUtils.isEmpty(fc.getReference())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(criteriaBuilder.upper(root.get("reference")),
                            "%" + fc.getReference().toUpperCase() + "%"));
                    if (a.equals(""))
                        a += " Référence : " + fc.getReference();
                    else
                        a += " | Référence : " + fc.getReference();

                }
                if (Objects.nonNull(fc.getRefAuto()) && !StringUtils.isEmpty(fc.getRefAuto())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("autoRef"), "%" + fc.getRefAuto() + "%"));
                    if (a.equals(""))
                        a += " Référence automatique : " + fc.getRefAuto();
                    else
                        a += " | Référence automatique: " + fc.getRefAuto();
                }
                if (Objects.nonNull(fc.getObjet()) && !StringUtils.isEmpty(fc.getObjet())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(criteriaBuilder.upper(root.get("objet")),
                            "%" + fc.getObjet().toUpperCase() + "%"));
                    if (a.equals(""))
                        a += " Objet : " + fc.getObjet();
                    else
                        a += " | Objet : " + fc.getObjet();

                }
                if (Objects.nonNull(fc.getNature())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("nature").get("id"), fc.getNature()));
                    if (a.equals(""))
                        a += " Nature : " + nr.findById(fc.getNature()).get().getName();
                    else
                        a += " | Nature : " + nr.findById(fc.getNature()).get().getName();

                }
                if (fc.getMotif() != "" && Objects.nonNull(fc.getMotif())) {
                    Join join = root.join("etapes");
                    Join j2 = join.join("details");
                    p = criteriaBuilder.and(p, criteriaBuilder.like(criteriaBuilder.upper(j2.get("motif")),
                            "%" + fc.getMotif().toUpperCase() + "%"));
                    if (a.equals(""))
                        a += " Motif : " + fc.getMotif();
                    else
                        a += " | Motif : " + fc.getMotif();

                }
                if (fc.getInstru() != "" && Objects.nonNull(fc.getInstru()))

                {
                    Join join1 = root.join("etapes");
                    Join j2 = join1.join("details");

                    p = criteriaBuilder.and(p, criteriaBuilder.like(criteriaBuilder.upper(j2.get("instruction")),
                            "%" + fc.getInstru().toUpperCase() + "%"));
                    if (a.equals(""))
                        a += " Annotation : " + fc.getInstru();
                    else
                        a += " | Annotation : " + fc.getInstru();

                }
                if (fc.getFini() != "" && Objects.nonNull(fc.getFini())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("finalise"), fc.getFini()));
                }

                if (fc.getFinalise() != "" && Objects.nonNull(fc.getFinalise())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("finalise"), fc.getFinalise()));
                }
                if (fc.getAccuse() == 1) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("accuse"), 1));
                    if (a.equals("")) {
                        a += " Accusé de réception : oui ";
                    } else
                        a += " | Accusé de réception : oui ";

                }
                // if (Objects.nonNull(fc.getDate()) && !StringUtils.isEmpty(fc.getDate()))

                // p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("date"),
                // fc.getDate()));
                if (Objects.nonNull(fc.getDeDate()) && Objects.nonNull(fc.getToDate())
                        && !StringUtils.isEmpty(fc.getDeDate()) && !StringUtils.isEmpty(fc.getToDate())) {
                    p = criteriaBuilder.and(p,
                            criteriaBuilder.between(root.get("date"), fc.getDeDate(), fc.getToDate()));
                    log.debug(fc.getDeDate() + "----------------" + fc.getToDate());
                }
                User user = connectedUser();

                if (Objects.nonNull(user.getMaster())) {

                    List<Groupe> groups = gr.findGroupeByUsersUserId(user.getUserId());
                    List<Long> acces = new ArrayList<Long>();
                    for (Groupe groupe : groups) {

                        List<PermissionGroupN> permissionGroups = pgnr.findByGroup(groupe);
                        for (PermissionGroupN pd : permissionGroups) {

                            for (PermissionNatureCourrier pdt : pd.getPermissionCourrier().getPermissionNature()) {

                                if (pdt.getPermissionCourrier().getAcces().contains("R")

                                        && connectedUser().getMaster() != null) {
                                    // .println(pdt.getNature().getId());
                                    acces.add(pdt.getNature().getId());
                                }
                            }
                        }

                    }
                    p = criteriaBuilder.and(p, criteriaBuilder.in(root.get("nature").get("id")).value(acces));
                }

                query.distinct(true);
                if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() >= 2) {
                    Journal j = new Journal();
                    j.setUser(connectedUser());
                    j.setDate(new Date());
                    j.setAction(a);
                    j.setMode("R");
                    j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));
                    j.setComposante("Courrier");
                    if (secondary.equals("true")) {

                        User user1 = ur.getSecondaryUser(connectedUser().getUserId());

                        j.setTypeEv("Utilisateur/Recherche(Déplacer)/Secondaire Profil");

                        if (Objects.nonNull(user1)) {
                            j.setConnectedSacondaryName(user1.getFullName());
                        }

                    } else {
                        j.setTypeEv("Utilisateur/Recherche(Déplacer)");
                    }
                    jr.save(j);
                }

                return p;
            }
        }, pageable);
    }

    public Page<Document> fullTextSearch(String text, Pageable pageable) {
        Page<DocumentFullText> dft = dfr.findAll(new Specification<DocumentFullText>() {
            @Override
            public Predicate toPredicate(Root<DocumentFullText> root, CriteriaQuery<?> query,
                    CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.conjunction();
                User mst = getMaster();
                p = criteriaBuilder.equal(root.get("masterId"), mst.getUserId());
                if (Objects.nonNull(text) && !StringUtils.isEmpty(text)) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(criteriaBuilder.upper(root.get("fullText")),
                            "%" + text.toUpperCase() + "%"));
                }
                User user = connectedUser();
                if (Objects.nonNull(user.getMaster())) {
                    List<Groupe> groups = gr.findGroupeByUsersUserId(user.getUserId());
                    List<Long> acces = new ArrayList<Long>();
                    for (Groupe groupe : groups) {
                        List<PermissionGroup> permissionGroups = pgr.findByGroup(groupe);
                        for (PermissionGroup pd : permissionGroups) {
                            for (PermissionDocumentType pdt : pd.getPermissionDocument().getPermissionDocumentTypes()) {
                                if (pdt.getPermissionDocument().getAcces().contains("R")) {
                                    acces.add(pdt.getDocumentType().getId());
                                }
                            }
                        }
                    }
                    p = criteriaBuilder.and(p, criteriaBuilder.in(root.get("typeId")).value(acces));

                }
                return p;
            }
        }, pageable);

        List<Document> list = new ArrayList<Document>();
        for (DocumentFullText documentFullText : dft.getContent()) {
            list.add(dr.findById(documentFullText.getId()).orElse(null));

        }
        int start = (int) pageable.getOffset();
        int end = (int) (start + pageable.getPageSize()) > list.size() ? list.size() : (start + pageable.getPageSize());
        Page<Document> pages = new PageImpl<Document>(list.subList(start, end), pageable, list.size());

        return pages;
    }

    // available folders to link with
    public Page<Folder> availableFoldersToLinkSearch(FolderClass fc, UUID id, Pageable pageable) {

        List<Folder> folders = fr.findLinkedFolders(id.toString());
        List<Folder> currentPage = fr.findAll(new Specification<Folder>() {

            @Override
            public Predicate toPredicate(Root<Folder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                Predicate p = criteriaBuilder.conjunction();
                p = criteriaBuilder.equal(root.get("master"), getMaster());
                p = criteriaBuilder.and(p, criteriaBuilder.notEqual(root.get("id"), id));
                String a = "";
                // .println(fc.toString());
                if (Objects.nonNull(fc.getType())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("type").get("id"), fc.getType()));
                    if (a.equals("")) {
                        a += " Type : " + ftr.findById(fc.getType()).orElse(null).getName();

                    } else {
                        a += "| Type : " + ftr.findById(fc.getType()).orElse(null).getName();

                    }

                }

                if (fc.getMode() == 1 && fc.getDest().size() > 0) {

                    /*
                     * Join<Folder, receiver> receiverJoin = root.join("receivers");
                     * p = criteriaBuilder.and(p,
                     * criteriaBuilder.and(receiverJoin.get("name").in(fc.getDest())));
                     */
                    p = criteriaBuilder.and(p, root.get("id").in(fr.findFolderDest(fc.getDest())));
                }
                if (fc.getMode() == 2 && Objects.nonNull(fc.getSender())) {

                    p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("emet__"), "%" + fc.getSender() + "%"));

                }
                if (Objects.nonNull(fc.getReceiver())) {
                    p = criteriaBuilder.and(p,
                            criteriaBuilder.like(root.get("receiver"), "%" + fc.getReceiver() + "%"));
                    if (a.equals(""))
                        a += " Destinataire : " + fc.getReceiver();
                    else
                        a += " | Destinataire : " + fc.getReceiver();

                }

                if (Objects.nonNull(fc.getReference()) && !StringUtils.isEmpty(fc.getReference())) {
                    p = criteriaBuilder.and(p,
                            criteriaBuilder.like(root.get("reference"), "%" + fc.getReference() + "%"));
                    if (a.equals(""))
                        a += " Référence : " + fc.getReference();
                    else
                        a += " | Référence : " + fc.getReference();

                }
                if (Objects.nonNull(fc.getObjet()) && !StringUtils.isEmpty(fc.getObjet())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("objet"), "%" + fc.getObjet() + "%"));
                    if (a.equals(""))
                        a += " Objet : " + fc.getObjet();
                    else
                        a += " | Objet : " + fc.getObjet();

                }
                if (Objects.nonNull(fc.getNature())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("nature").get("id"), fc.getNature()));
                    if (a.equals(""))
                        a += " Nature : " + nr.findById(fc.getNature()).get().getName();
                    else
                        a += " | Nature : " + nr.findById(fc.getNature()).get().getName();

                }
                if (fc.getMotif() != "" && Objects.nonNull(fc.getMotif())) {
                    Join join = root.join("etapes");
                    Join j2 = join.join("details");
                    p = criteriaBuilder.and(p, criteriaBuilder.like(j2.get("motif"), "%" + fc.getMotif() + "%"));
                    if (a.equals(""))
                        a += " Motif : " + fc.getMotif();
                    else
                        a += " | Motif : " + fc.getMotif();

                }
                if (fc.getInstru() != "" && Objects.nonNull(fc.getInstru()))

                {
                    Join join1 = root.join("etapes");
                    Join j2 = join1.join("details");

                    p = criteriaBuilder.and(p, criteriaBuilder.like(j2.get("instruction"), "%" + fc.getInstru() + "%"));
                    if (a.equals(""))
                        a += " Annotation : " + fc.getInstru();
                    else
                        a += " | Annotation : " + fc.getInstru();

                }
                if (fc.getFinalise() != "" && Objects.nonNull(fc.getFinalise())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("finalise"), fc.getFinalise()));
                }

//                if (fc.getFinalise() != "" && Objects.nonNull(fc.getFinalise())) {
//                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("finalise"), fc.getFinalise()));
//                }
                if (fc.getFini() != "" && Objects.nonNull(fc.getFini())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("finalise"), fc.getFini()));
                }
                if (fc.getAccuse() == 1) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("accuse"), 1));
                    if (a.equals("")) {
                        a += " Accusé de réception : oui ";
                    } else
                        a += " | Accusé de réception : oui ";

                }
                // if (Objects.nonNull(fc.getDate()) && !StringUtils.isEmpty(fc.getDate()))

                // p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("date"),
                // fc.getDate()));
                if (Objects.nonNull(fc.getDeDate()) && Objects.nonNull(fc.getToDate())
                        && !StringUtils.isEmpty(fc.getDeDate()) && !StringUtils.isEmpty(fc.getToDate())) {
                    p = criteriaBuilder.and(p,
                            criteriaBuilder.between(root.get("date"), fc.getDeDate(), fc.getToDate()));
                }

                String[] order = org.apache.commons.lang3.StringUtils.chop(fc.getOrder()).split(" ");

                if (fc.getOrder().equals("")) {
                    query.orderBy(criteriaBuilder.desc(root.get("creation_date")));
                } else {
                    if (order[1].equals("desc")) {
                        log.info("order by : {}", order[1]);
                        query.orderBy(criteriaBuilder.desc(root.get(order[0])));
                    } else {
                        query.orderBy(criteriaBuilder.asc(root.get(order[0])));
                    }

                }

                User user = connectedUser();

                if (Objects.nonNull(user.getMaster())) {

                    List<Groupe> groups = gr.findGroupeByUsersUserId(user.getUserId());
                    List<Long> acces = new ArrayList<Long>();
                    for (Groupe groupe : groups) {

                        List<PermissionGroupN> permissionGroups = pgnr.findByGroup(groupe);
                        for (PermissionGroupN pd : permissionGroups) {

                            for (PermissionNatureCourrier pdt : pd.getPermissionCourrier().getPermissionNature()) {

                                if (pdt.getPermissionCourrier().getAcces().contains("R")

                                        && connectedUser().getMaster() != null) {
                                    // .println(pdt.getNature().getId());
                                    acces.add(pdt.getNature().getId());
                                }
                            }
                        }

                    }
                    p = criteriaBuilder.and(p, criteriaBuilder.in(root.get("nature").get("id")).value(acces));
                }

                return p;
            }
        });

        List<Folder> returned = new ArrayList<Folder>();

        for (Folder folder : currentPage) {
            if (folders.contains(folder)) {

                folder.setField1(1);
                returned.add(0, folder);
            } else {
                returned.add(folder);
            }

        }

        // .println(id.toString());

        int start = (int) pageable.getOffset();
        int end = (int) (start + pageable.getPageSize()) > returned.size() ? returned.size()
                : (start + pageable.getPageSize());
        Page<Folder> pages = new PageImpl<Folder>(returned.subList(start, end), pageable, returned.size());

        for (Folder folder : pages.getContent()) {
            // .println(folder.getField1());
        }
        return pages;

    }

    // AVAILABLE FOLDERS TO LINK WITH DOC
    public Page<Folder> availableFoldersToLinkDocument(FolderClass fc, UUID id, Pageable pageable) {

        List<Folder> folders = fr.findLinkedFoldersOfDocument(id.toString());
        List<Folder> currentPage = fr.findAll(new Specification<Folder>() {

            @Override
            public Predicate toPredicate(Root<Folder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                Predicate p = criteriaBuilder.conjunction();
                p = criteriaBuilder.equal(root.get("master"), getMaster());
                if (Objects.nonNull(fc.getType()))
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("type").get("id"), fc.getType()));

                if (Objects.nonNull(fc.getClient()))
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("client").get("id"), fc.getClient()));
                if (Objects.nonNull(fc.getReceiver())) {
                    p = criteriaBuilder.and(p,
                            criteriaBuilder.like(root.get("receiver"), "%" + fc.getReceiver() + "%"));
                }
                if (fc.getMode() == 1 && fc.getDest().size() > 0) {

                    /*
                     * Join<Folder, receiver> receiverJoin = root.join("receivers");
                     * p = criteriaBuilder.and(p,
                     * criteriaBuilder.and(receiverJoin.get("name").in(fc.getDest())));
                     */
                    p = criteriaBuilder.and(p, root.get("id").in(fr.findFolderDest(fc.getDest())));
                }
                if (fc.getMode() == 2 && Objects.nonNull(fc.getSender())) {

                    p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("emet__"), "%" + fc.getSender() + "%"));

                }
                if (Objects.nonNull(fc.getNature())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("nature").get("id"), fc.getNature()));

                }
                if (Objects.nonNull(fc.getReference()) && !StringUtils.isEmpty(fc.getReference()))
                    p = criteriaBuilder.and(p,
                            criteriaBuilder.like(root.get("reference"), "%" + fc.getReference() + "%"));
                if (fc.getFinalise() != "" && Objects.nonNull(fc.getFinalise())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("finalise"), fc.getFinalise()));
                }
                if (fc.getFini() != "" && Objects.nonNull(fc.getFini())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("finalise"), fc.getFini()));
                }
                if (fc.getAccuse() == 1) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("accuse"), 1));
                }
                if (Objects.nonNull(fc.getNumber()))
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("number"), fc.getNumber()));
                // if (Objects.nonNull(fc.getDate()) && !StringUtils.isEmpty(fc.getDate()))
                // p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("date"),
                // fc.getDate()));
                if (Objects.nonNull(fc.getDeDate()) && Objects.nonNull(fc.getToDate())
                        && !StringUtils.isEmpty(fc.getDeDate()) && !StringUtils.isEmpty(fc.getToDate())) {
                    p = criteriaBuilder.and(p,
                            criteriaBuilder.between(root.get("date"), fc.getDeDate(), fc.getToDate()));
                }

                String[] order = org.apache.commons.lang3.StringUtils.chop(fc.getOrder()).split(" ");

                if (fc.getOrder().equals("")) {
                    query.orderBy(criteriaBuilder.desc(root.get("creation_date")));
                } else {
                    if (order[1].equals("desc")) {
                        log.info("order by : {}", order[1]);
                        query.orderBy(criteriaBuilder.desc(root.get(order[0])));
                    } else {
                        query.orderBy(criteriaBuilder.asc(root.get(order[0])));
                    }

                }
                return p;
            }
        });

        List<Folder> returned = new ArrayList<Folder>();

        for (Folder folder : currentPage) {
            if (folders.contains(folder)) {

                folder.setField1(1);
                returned.add(0, folder);
            } else {
                returned.add(folder);
            }

        }

        // .println(id.toString());

        int start = (int) pageable.getOffset();
        int end = (int) (start + pageable.getPageSize()) > returned.size() ? returned.size()
                : (start + pageable.getPageSize());
        Page<Folder> pages = new PageImpl<Folder>(returned.subList(start, end), pageable, returned.size());

        for (Folder folder : pages.getContent()) {
            // .println(folder.getField1());
        }
        return pages;

    }

    public Page<Folder> foo(FolderClass fc, Pageable page) {

        Folder pattern = new Folder();

        return null;
    }

    @Transactional(readOnly = false)
    // SEARCH FOLDER CRITERIA BUILDER
    public Page<Folder> searchFolders(FolderClass fc, String secondary, Pageable pageable) {
        return fr.findAll(new Specification<Folder>() {

            @Override
            public Predicate toPredicate(Root<Folder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.conjunction();
                User mst = getMaster();
                p = criteriaBuilder.equal(root.get("master"), mst);

                String a = "";
                // .println(fc.toString());
                if (Objects.nonNull(fc.getType())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("type").get("id"), fc.getType()));
                    if (a.equals("")) {
                        a += " Type : " + ftr.findById(fc.getType()).orElse(null).getName();

                    } else {
                        a += "| Type : " + ftr.findById(fc.getType()).orElse(null).getName();

                    }

                }

                if (fc.getMode() == 1 && fc.getDest().size() > 0) {
                    /*
                     * for (String name : fc.getDest()) {
                     * p = criteriaBuilder.and(p,
                     * criteriaBuilder.like(criteriaBuilder.upper(root.get("dest__")),
                     * "%" + name.toUpperCase() + "%"));
                     * }
                     */

                    // Join<Folder, receiver> receiverJoin = root.join("receivers");
                    // p = criteriaBuilder.and(p,
                    // criteriaBuilder.and(receiverJoin.get("name").in(fc.getDest())));

                    p = criteriaBuilder.and(p, root.get("id").in(fr.findFolderDest(fc.getDest())));

                }
                if (fc.getMode() == 2 && Objects.nonNull(fc.getSender())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("emet__"), "%" + fc.getSender() + "%"));
                }

                if (Objects.nonNull(fc.getReference()) && !StringUtils.isEmpty(fc.getReference())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(criteriaBuilder.upper(root.get("reference")),
                            "%" + fc.getReference().toUpperCase() + "%"));
                    if (a.equals(""))
                        a += " Référence : " + fc.getReference();
                    else
                        a += " | Référence : " + fc.getReference();

                }
                if (Objects.nonNull(fc.getRefAuto()) && !StringUtils.isEmpty(fc.getRefAuto())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("autoRef"), "%" + fc.getRefAuto() + "%"));
                    if (a.equals(""))
                        a += " Référence automatique : " + fc.getRefAuto();
                    else
                        a += " | Référence automatique: " + fc.getRefAuto();
                }
                if (Objects.nonNull(fc.getObjet()) && !StringUtils.isEmpty(fc.getObjet())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(criteriaBuilder.upper(root.get("objet")),
                            "%" + fc.getObjet().toUpperCase() + "%"));
                    if (a.equals(""))
                        a += " Objet : " + fc.getObjet();
                    else
                        a += " | Objet : " + fc.getObjet();

                }
                if (Objects.nonNull(fc.getNature())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("nature").get("id"), fc.getNature()));
                    if (a.equals(""))
                        a += " Nature : " + nr.findById(fc.getNature()).get().getName();
                    else
                        a += " | Nature : " + nr.findById(fc.getNature()).get().getName();

                }
                if (fc.getMotif() != "" && Objects.nonNull(fc.getMotif())) {
                    Join join = root.join("etapes");
                    Join j2 = join.join("details");
                    p = criteriaBuilder.and(p, criteriaBuilder.like(criteriaBuilder.upper(j2.get("motif")),
                            "%" + fc.getMotif().toUpperCase() + "%"));
                    if (a.equals(""))
                        a += " Motif : " + fc.getMotif();
                    else
                        a += " | Motif : " + fc.getMotif();

                }
                if (fc.getInstru() != "" && Objects.nonNull(fc.getInstru()))

                {
                    Join join1 = root.join("etapes");
                    Join j2 = join1.join("details");

                    p = criteriaBuilder.and(p, criteriaBuilder.like(criteriaBuilder.upper(j2.get("instruction")),
                            "%" + fc.getInstru().toUpperCase() + "%"));
                    if (a.equals(""))
                        a += " Annotation : " + fc.getInstru();
                    else
                        a += " | Annotation : " + fc.getInstru();

                }

//                if (fc.getFinalise() != "" && Objects.nonNull(fc.getFinalise())) {
//                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("finalise"), fc.getFinalise()));
//                }
                if (fc.getFini() != "" && Objects.nonNull(fc.getFini())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("finalise"), fc.getFini()));
                }
                if (fc.getAccuse() == 1) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("accuse"), 1));
                    if (a.equals("")) {
                        a += " Accusé de réception : oui ";
                    } else
                        a += " | Accusé de réception : oui ";

                }
                // if (Objects.nonNull(fc.getDate()) && !StringUtils.isEmpty(fc.getDate()))

                // p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("date"),
                // fc.getDate()));
                if (Objects.isNull(fc.getDeDate()) || fc.getDeDate().equals("")) {
                    fc.setDeDate("2015-01-01");
                }
                if (Objects.nonNull(fc.getDeDate()) && Objects.nonNull(fc.getToDate())
                        && !StringUtils.isEmpty(fc.getDeDate()) && !StringUtils.isEmpty(fc.getToDate())) {
                    p = criteriaBuilder.and(p,
                            criteriaBuilder.between(root.get("date"), fc.getDeDate(), fc.getToDate()));
                }

                String[] order = org.apache.commons.lang3.StringUtils.chop(fc.getOrder()).split(" ");

                if (fc.getOrder().equals("")) {
                    query.orderBy(criteriaBuilder.desc(root.get("creation_date")));
                } else {
                    if (order[1].equals("desc")) {
                        // log.info("order by : {}", order[0]);
                        query.orderBy(criteriaBuilder.desc(root.get(order[0])));
                    } else {
                        query.orderBy(criteriaBuilder.asc(root.get(order[0])));
                    }

                }

                User user = connectedUser();

                if (Objects.nonNull(user.getMaster())) {

                    List<Groupe> groups = gr.findGroupeByUsersUserId(user.getUserId());
                    List<Long> acces = new ArrayList<Long>();
                    for (Groupe groupe : groups) {

                        List<PermissionGroupN> permissionGroups = pgnr.findByGroup(groupe);
                        for (PermissionGroupN pd : permissionGroups) {

                            for (PermissionNatureCourrier pdt : pd.getPermissionCourrier().getPermissionNature()) {

                                if (pdt.getPermissionCourrier().getAcces().contains("R")

                                        && connectedUser().getMaster() != null) {
                                    // .println(pdt.getNature().getId());
                                    acces.add(pdt.getNature().getId());
                                }
                            }
                        }

                    }
                    p = criteriaBuilder.and(p, criteriaBuilder.in(root.get("nature").get("id")).value(acces));
                }

                query.distinct(true);
                if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() >= 2) {
                    Journal j = new Journal();
                    j.setUser(connectedUser());
                    j.setDate(new Date());
                    j.setAction(a);
                    j.setMode("R");
                    j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));
                    j.setComposante("Courrier");
                    if (secondary.equals("true")) {

                        User userSeco = ur.getSecondaryUser(connectedUser().getUserId());

                        j.setTypeEv("Utilisateur/Recherche/Secondaire Profil");

                        if (Objects.nonNull(userSeco)) {
                            j.setConnectedSacondaryName(userSeco.getFullName());
                        }

                    } else {
                        j.setTypeEv("Utilisateur/Recherche");
                    }
                    jr.save(j);
                }

                return p;
            }
        }, pageable);

    }

    @Transactional(readOnly = false)
    // SEARCH FOLDER CRITERIA BUILDER WITHOUT PAGINATION
    public List<Folder> searchFoldersAll(FolderClass fc, String secondary) {
        return fr.findAll(new Specification<Folder>() {

            @Override
            public Predicate toPredicate(Root<Folder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.conjunction();
                User mst = getMaster();
                p = criteriaBuilder.equal(root.get("master"), mst);

                String a = "";
                // .println(fc.toString());
                if (Objects.nonNull(fc.getType())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("type").get("id"), fc.getType()));
                    if (a.equals("")) {
                        a += " Type : " + ftr.findById(fc.getType()).orElse(null).getName();

                    } else {
                        a += "| Type : " + ftr.findById(fc.getType()).orElse(null).getName();

                    }

                }

                if (fc.getMode() == 1 && fc.getDest().size() > 0) {
                    p = criteriaBuilder.and(p, root.get("id").in(fr.findFolderDest(fc.getDest())));
                }
                if (fc.getMode() == 2 && Objects.nonNull(fc.getSender()) && !fc.getSender().equals("")) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("emet__"), "%" + fc.getSender() + "%"));
                    // p = criteriaBuilder.and(p,
                    // criteriaBuilder.equal(root.get("sender").get("name"), fc.getSender()));

                }

                if (Objects.nonNull(fc.getReference()) && !StringUtils.isEmpty(fc.getReference())) {
                    p = criteriaBuilder.and(p,
                            criteriaBuilder.like(root.get("reference"), "%" + fc.getReference() + "%"));
                    if (a.equals(""))
                        a += " Référence : " + fc.getReference();
                    else
                        a += " | Référence : " + fc.getReference();

                }
                if (Objects.nonNull(fc.getRefAuto()) && !StringUtils.isEmpty(fc.getRefAuto())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("autoRef"), "%" + fc.getRefAuto() + "%"));
                    if (a.equals(""))
                        a += " Référence automatique : " + fc.getRefAuto();
                    else
                        a += " | Référence automatique: " + fc.getRefAuto();
                }
                if (Objects.nonNull(fc.getObjet()) && !StringUtils.isEmpty(fc.getObjet())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("objet"), "%" + fc.getObjet() + "%"));
                    if (a.equals(""))
                        a += " Objet : " + fc.getObjet();
                    else
                        a += " | Objet : " + fc.getObjet();

                }
                if (Objects.nonNull(fc.getNature())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("nature").get("id"), fc.getNature()));
                    if (a.equals(""))
                        a += " Nature : " + nr.findById(fc.getNature()).get().getName();
                    else
                        a += " | Nature : " + nr.findById(fc.getNature()).get().getName();

                }
                if (fc.getMotif() != "" && Objects.nonNull(fc.getMotif())) {
                    Join join = root.join("etapes");
                    Join j2 = join.join("details");
                    p = criteriaBuilder.and(p, criteriaBuilder.like(j2.get("motif"), "%" + fc.getMotif() + "%"));
                    if (a.equals(""))
                        a += " Motif : " + fc.getMotif();
                    else
                        a += " | Motif : " + fc.getMotif();

                }
                if (fc.getInstru() != "" && Objects.nonNull(fc.getInstru()))

                {
                    Join join1 = root.join("etapes");
                    Join j2 = join1.join("details");

                    p = criteriaBuilder.and(p, criteriaBuilder.like(j2.get("instruction"), "%" + fc.getInstru() + "%"));
                    if (a.equals(""))
                        a += " Annotation : " + fc.getInstru();
                    else
                        a += " | Annotation : " + fc.getInstru();

                }
                if (fc.getFinalise() != "" && Objects.nonNull(fc.getFinalise())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("finalise"), fc.getFinalise()));
                }
                if (fc.getFini() != "" && Objects.nonNull(fc.getFini())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("finalise"), fc.getFini()));
                }
                if (fc.getAccuse() == 1) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("accuse"), 1));
                    if (a.equals("")) {
                        a += " Accusé de réception : oui ";
                    } else
                        a += " | Accusé de réception : oui ";

                }
                // if (Objects.nonNull(fc.getDate()) && !StringUtils.isEmpty(fc.getDate()))

                // p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("date"),
                // fc.getDate()));
                if (Objects.nonNull(fc.getDeDate()) && Objects.nonNull(fc.getToDate())
                        && !StringUtils.isEmpty(fc.getDeDate()) && !StringUtils.isEmpty(fc.getToDate())) {
                    p = criteriaBuilder.and(p,
                            criteriaBuilder.between(root.get("date"), fc.getDeDate(), fc.getToDate()));
                }
                User user = connectedUser();

                if (Objects.nonNull(user.getMaster())) {

                    List<Groupe> groups = gr.findGroupeByUsersUserId(user.getUserId());
                    List<Long> acces = new ArrayList<Long>();
                    for (Groupe groupe : groups) {

                        List<PermissionGroupN> permissionGroups = pgnr.findByGroup(groupe);
                        for (PermissionGroupN pd : permissionGroups) {

                            for (PermissionNatureCourrier pdt : pd.getPermissionCourrier().getPermissionNature()) {

                                if (pdt.getPermissionCourrier().getAcces().contains("R")

                                        && connectedUser().getMaster() != null) {
                                    // .println(pdt.getNature().getId());
                                    acces.add(pdt.getNature().getId());
                                }
                            }
                        }

                    }
                    p = criteriaBuilder.and(p, criteriaBuilder.in(root.get("nature").get("id")).value(acces));
                }

                query.orderBy(criteriaBuilder.desc(root.get("creation_date")));

                query.distinct(true);
                if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() >= 2) {
                    Journal j = new Journal();
                    j.setUser(connectedUser());
                    j.setDate(new Date());
                    j.setTypeEv("Utilisateur/Recherche");
                    j.setAction(a);
                    j.setMode("R");
                    if (secondary.equals("true")) {

                        User user1 = ur.getSecondaryUser(connectedUser().getUserId());

                        j.setTypeEv("Utilisateur/Recherche/Secondaire Profil");

                        if (Objects.nonNull(user1)) {
                            j.setConnectedSacondaryName(user1.getFullName());
                        }

                    } else {
                        j.setTypeEv("Utilisateur/Recherche");
                    }
                    j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));
                    j.setComposante("Courrier");

                    jr.save(j);
                }

                return p;
            }
        });

    }

    public void saveSearch(Search s, String secondary) throws Exception {
        Long count = fsr.searchCount(connectedUser(), s.getName());
        // .println("" + count);

        if (count == 0) {
            try {
                FrequencySearch search = new FrequencySearch();
                search.setName(s.getName());
                search.setSearch_date(LocalDate.now());
                search.setUser(connectedUser());
                search.setSearchForm(s.searchForm);
                search = fsr.saveAndFlush(search);

                final Long searchId = search.getId();

                for (SearchAttrs sa : s.attributes) {
                    if (!(StringUtils.isEmpty(sa.value) || !Objects.nonNull(sa.value))) {
                        // .println(sa.value);
                        savr.save(new SearchAttributeValue(new SearchAttributeValuePk(searchId, sa.id), search,
                                sattr.getOne(sa.id), sa.value, null));
                    }
                }

                if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() >= 1) {
                    Journal j = new Journal();
                    j.setUser(connectedUser());
                    j.setDate(new Date());

                    j.setComposante("Recherche Fréquente");
                    j.setMode("txt");
                    j.setAction("A enregistré la recherche :" + s.name);
                    j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));
                    if (secondary.equals("true")) {

                        User userSeco = ur.getSecondaryUser(connectedUser().getUserId());

                        j.setTypeEv("Utilisateur/Enregitrement/Secondaire Profil");

                        if (Objects.nonNull(userSeco)) {
                            j.setConnectedSacondaryName(userSeco.getFullName());
                        }

                    } else {
                        j.setTypeEv("Utilisateur/Enregitrement");
                    }
                    jr.save(j);
                }
            } catch (Exception e) {
                // TODO: handle exception
                // System.out.println(e.getMessage());
            }

        } else {
            throw new Exception("Duplicate name");
        }

    }

    public void editSearch(Search s) {
        FrequencySearch search;
        search = fsr.getOne(s.id);
        search.setLast_edit_date(LocalDate.now());
        search.setName(s.getName());
        final Long searchId = search.getId();

        for (SearchAttrs sa : s.attributes) {
            // .println(sa.value);
            SearchAttributeValue sAttributeValue = savr.findById(new SearchAttributeValuePk(s.id, sa.id)).orElse(null);
            if (sAttributeValue == null && !(StringUtils.isEmpty(sa.value) || !Objects.nonNull(sa.value)))
                savr.save(new SearchAttributeValue(new SearchAttributeValuePk(searchId, sa.id), search,
                        sattr.getOne(sa.id), sa.value, null));
            else if (sAttributeValue != null) {
                sAttributeValue.setValue(sa.value);
                savr.save(sAttributeValue);
            }

            // savr.save(new SearchAttributeValue(new
            // SearchAttributeValuePk(searchId,sa.id),search,sattr.getOne(sa.id),sa.value,null));

        }
        search = fsr.save(search);
    }

    public void deleteSearch(Long id) {
        FrequencySearch search = fsr.getOne(id);
        savr.deleteBySearchId(search.getId());
        fsr.delete(search);

    }

    public List<SearchAttributeValue> getSearchAttributes(Long id) {

        FrequencySearch fs = fsr.getOne(id);
        return savr.findBySearch(fs);

    }

    public Page<FrequencySearch> getMostSearches(Pageable pageable) {
        return fsr.findByUserUserId(connectedUser().getUserId(), pageable);
    }

    // DOCUMENT SEARCH (TYPE TABLE)

    public List<List<SearchResultColumn>> rechMulti2(DocumnentClass dc, String secondary,
            org.springframework.data.domain.Pageable pageable) throws SQLException {
        DocumentType dt = dtr.getOne(dc.type);
        int acces = 0;
        User con = connectedUser();
        // List<Groupe> groupsd = gr.findGroupeByUsersUserId(con.getUserId());
        if (con.getMaster() != null) {
            List<Groupe> groups = gr.findGroupeByUsersUserId(con.getUserId());
            for (Groupe groupe : groups) {
                // .println(groupe.getGroupName());
                List<PermissionGroup> permissionGroups = pgr.findByGroup(groupe);
                for (PermissionGroup pd : permissionGroups) {

                    for (PermissionDocumentType pdt : pd.getPermissionDocument().getPermissionDocumentTypes()) {

                        if (dt.getId() == pdt.getDocumentType().getId()
                                && (pdt.getPermissionDocument().getAcces().contains("R"))

                                && connectedUser().getMaster() != null) {
                            acces = 1;
                        }
                    }
                }
            }
            // .println(acces);
        }
        // .println(acces);

        if (acces == 0 && con.getMaster() != null) {
            return null;
        }
        Document doc = new Document();
        // final Long masterId = getMaster().getUserId();
        doc.setType(dt);
        String query = "";
        String queryIds = "";
        if (selectedDb.equals("msql")) {
            // dt.getName().replace(' ', '_').replace("'", "_")
            query = "select * ,count(*)  over() as nbelem from d" + +dt.getId();
            queryIds = "select * ,count(*)  over() as nbelem from d" + +dt.getId();
        } else if (selectedDb.equals("orcl")) {
            // + dt.getName().replace(' ', '_').replace("'", "_")
            query = "select * from d" + dt.getId();
            queryIds = "select * from d" + dt.getId();
        }
        if (selectedDb.equals("mssql")) {
            // dt.getName().replace(' ', '_').replace("'", "_")
            query = "select * ,count(*)  over() nbelem from d" + +dt.getId();
            queryIds = "select * ,count(*)  over() nbelem from d" + +dt.getId();
        }

        int i = 0;

        String conditions = "";
        String a = "Type : " + dt.getName();

        List<String> listValues = new ArrayList<String>();

        for (AttributeClass attClass : dc.attrs) {

            if (Objects.nonNull(attClass.val) && attClass.val != "") {

                if (i != 0) {
                    conditions += " and ";
                    a += " | " + (attClass.name.equals("Date") ? "Date_" : attClass.name) + " : " + attClass.val;

                }
                if (i == 0) {
                    conditions += " where ";
                    i = 1;
                    a += " | " + (attClass.name.equals("Date") ? "Date_" : attClass.name) + " : " + attClass.val;

                }
                if (attClass.getType().equals("List") || attClass.getType().equals("listDb")
                        || attClass.getType().equals("ListDep")) {
                    conditions += "UPPER("
                            + attClass.name
                            + ")" + " = " + " " + " ? " + " " + " ";
                    listValues.add(attClass.val);
                } else {
                    conditions += "UPPER("
                            + (attClass.name.equals("Date") ? "Date_" : attClass.name).replace(' ', '_').replace("'",
                                    "_")
                            + ")" + " like ? ";
                    listValues.add(NativeQueriesUtils.wildCard(attClass.val));
                }

            }
            // %'''%
        }

        query += conditions;
        System.out.println(query);

        if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() >= 2) {
            Journal j = new Journal();
            j.setUser(connectedUser());
            j.setDate(new Date());
            j.setTypeEv("Utilisateur/Recherche");
            j.setAction(a);
            j.setMode("R");
            j.setComposante("Document");
            j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));
            if (secondary.equals("true")) {

                User userSeco = ur.getSecondaryUser(connectedUser().getUserId());

                j.setTypeEv("Utilisateur/Recherche/Secondaire Profil");

                if (Objects.nonNull(userSeco)) {
                    j.setConnectedSacondaryName(userSeco.getFullName());
                }

            } else {
                j.setTypeEv("Utilisateur/Recherche");
            }
            // //.println(a);
            jr.save(j);
        }

        if (Objects.nonNull(dc.order)) {
            query += " order by ?";
            listValues.add(org.apache.commons.lang3.StringUtils.chop(dc.order));
        } else {
            query += " order by " + "upload_date desc";
        }

        if (selectedDb.equals("msql")) {
            query += " LIMIT " + pageable.getPageSize() + " offset "
                    + pageable.getPageNumber() * pageable.getPageSize();
        } else if (selectedDb.equals("orcl")) {
            int pageNumber = pageable.getPageNumber() + 1;

            query = "SELECT * FROM (SELECT tab.*,rownum r__ , (select count(*) from (" + query + " )) nbelem from ( "
                    + query + " ) tab WHERE rownum <" + ((pageNumber * pageable.getPageSize()) + 1) + " ) WHERE r__ >="
                    + (((pageNumber - 1) * pageable.getPageSize()) + 1);
        } else if (selectedDb.equals("mssql")) {

            query += " ORDER BY upload_date desc OFFSET " + pageable.getPageNumber() * pageable.getPageSize()
                    + " ROWS FETCH NEXT " + pageable.getPageSize() + " ROWS ONLY ";
        }
        System.out.println("queryyyyyyyyyyy " + dbus + dbpw);
        System.out.println("queryyyyyyyyyyy " + query);
        Connection conn = DriverManager.getConnection(db, dbus, dbpw);

        PreparedStatement pe = conn.prepareStatement(query);

        int ii = 1;
        for (String value : listValues) {
            NativeQueriesUtils.setParamToPreparedStatement(pe, ii, value);
            ii++;
        }

        ResultSet rs = pe.executeQuery();

        ResultSet rsIds = conn.createStatement().executeQuery(queryIds);

        ArrayList<String> Ids = new ArrayList<String>();

        while (rsIds.next()) {

            Ids.add(rsIds.getString("id"));

        }

        Gson json = new Gson();

        List<SearchResultColumn> array = new ArrayList<SearchResultColumn>();
        List<List<SearchResultColumn>> array2 = new ArrayList<List<SearchResultColumn>>();
        i = 0;
        String count = "";

        while (rs.next()) {

            List<Object> lst = dtr.finDocTypeAttrRequiredClasses(dc.type);

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
                ls.add(atr);

            }

            array.add(new SearchResultColumn("id", rs.getString("id"), "id", "id", "id", "id", "id"));
            array.add(new SearchResultColumn("fileName", rs.getString("file_name"), "file", "file", "file", "file",
                    "file"));
            array.add(new SearchResultColumn("typeName", rs.getString("type_name"), "type", "type", "type", "type",
                    "type"));
            array.add(new SearchResultColumn("content", rs.getString("content_type"), "content", "content", "content",
                    "content", "content"));
            array.add(new SearchResultColumn("owner", ur.findById(rs.getLong("owner")).get().getFullName(), "owner",
                    "owner", "owner", "owner", "owner"));
            for (Attribute iterable_element : dt.getAttributes()) {
                if (i == 0) {
                    count = rs.getString("nbelem");

                    i = 1;
                }

                if (!iterable_element.getName().equals("HTML_CONTENT") && !iterable_element.getName().equals("Fichier")
                        && !iterable_element.getName().equals("fileName")) {

                    for (Attribute attri : ls) {

                        if (attri.getName().equals(iterable_element.getName())) {
                            array.add(new SearchResultColumn(iterable_element.getName(),
                                    rs.getString(
                                            (iterable_element.getName().equals("Date") ? "Date_"
                                                    : iterable_element.getName())
                                                            .replace(' ', '_')),
                                    iterable_element.getLabelfr(), iterable_element.getLabelar(),
                                    iterable_element.getLabeleng(), iterable_element.getDefaultValue(), count,
                                    iterable_element.getRep(), iterable_element.getType()));

                        }

                    }

                }
            }
            array.add(new SearchResultColumn("Date d'enregistrement", rs.getString("upload_date"),
                    "Date d'enregistrement", "تاريخ التسجيل", "Date d'enregistrement", "Date d'enregistrement", ""));

            array2.add(array);
            array = new ArrayList<SearchResultColumn>();
        }
        if (!StringUtils.isEmpty(count)) {
            int ct = Integer.parseInt(count);
            array.add(new SearchResultColumn("ids", json.toJson(Ids), "ids", "ids", "ids", "ids", "ids"));
            array.add(new SearchResultColumn("count", count, "count", "count", "count", "count", "count"));
        }

        array2.add(array);
        conn.close();

        return array2;

    }

    public Page<DocumnentClass> rechMulti(DocumnentClass dc, org.springframework.data.domain.Pageable pageable) {
        DocumentType dt = dtr.getOne(dc.type);
        int acces = 0;
        List<Groupe> groups = gr.findGroupeByUsersUserId(connectedUser().getUserId());
        for (Groupe groupe : groups) {

            List<PermissionGroup> permissionGroups = pgr.findByGroup(groupe);
            for (PermissionGroup pd : permissionGroups) {

                for (PermissionDocumentType pdt : pd.getPermissionDocument().getPermissionDocumentTypes()) {

                    if (dt.getId() == pdt.getDocumentType().getId()
                            && (pdt.getPermissionDocument().getAcces().contains("R"))

                            && connectedUser().getMaster() != null) {
                        acces = 1;
                    }
                }
            }
        }
        if (acces == 0 && connectedUser().getMaster() != null) {
            return null;
        }
        Document doc = new Document();
        final Long masterId = getMaster().getUserId();
        doc.setType(dt);
        List<UUID> ls = new ArrayList<UUID>();
        List<UUID> clean = new ArrayList<UUID>();
        clean.clear();
        List<DocumnentClass> ds = new ArrayList<DocumnentClass>();
        for (AttributeClass a : dc.getAttrs()) {
            ls.addAll(davr.findDocs(a.name, a.val, dc.getType(), masterId));
        }
        int ext = 0;
        for (int i = 0; i < ls.size(); i++) {

            for (int j = 0; j < ls.size(); j++) {
                if (ls.get(i).equals(ls.get(j))) {
                    ext++;
                }
            }
            if (ext == dc.getAttrs().size()) {
                if (!clean.contains(ls.get(i)))
                    clean.add(ls.get(i));
            }
            ext = 0;
        }

        ls.clear();

        for (int i = 0; i < clean.size(); i++) {
            List<AttributeClass> lsAttr = new ArrayList<AttributeClass>();
            doc = new Document();
            doc = dr.getOne(clean.get(i));
            DocumnentClass dcc = new DocumnentClass();
            dcc.fileName = doc.getFileName();
            dcc.id = doc.getId();
            dcc.type = dc.type;
            for (DocumentAttributeValue a : davr.findAll()) {
                if (a.getDocument().getId().equals(clean.get(i))) {
                    lsAttr.add(new AttributeClass(a.getAttribute().getId(), a.getAttribute().getType().getName(),
                            a.getAttribute().getName(), a.getValue().getValue(), a.getAttribute().getLibelle(),
                            a.getAttribute().getLabelfr(), a.getAttribute().getLabelar(),
                            a.getAttribute().getLabeleng(), a.getAttribute().getDefaultValue()));
                }
            }
            dcc.setAttrs(lsAttr);
            ds.add(dcc);
        }
        int start = (int) pageable.getOffset();
        int end = (int) (start + pageable.getPageSize()) > ds.size() ? ds.size() : (start + pageable.getPageSize());
        Page<DocumnentClass> pages = new PageImpl<DocumnentClass>(ds.subList(start, end), pageable, ds.size());
        for (DocumnentClass documnentClass : pages) {
            AttributeClass ac = null;
            for (int i = documnentClass.attrs.size() - 1; i >= 1; i--) {

                for (int j = 0; j < i; j++) {
                    if (documnentClass.attrs.get(j).getId() > documnentClass.attrs.get(j + 1).id) {
                        ac = documnentClass.attrs.get(j + 1);
                        documnentClass.attrs.set(j + 1, documnentClass.attrs.get(j));
                        documnentClass.attrs.set(j, ac);

                    }
                }

            }
        }

        return pages;
    }

    // SEARCH DOCS BY CLIENT
    public Page<Document> getDocs(Pageable pageable) {
        User u = getMaster();
        if (u.getMaster() != null) {
            u = u.getMaster();
        }
        List<Document> ds = dr.findDocsClient(u.getUserId());

        int start = (int) pageable.getOffset();
        int end = (int) (start + pageable.getPageSize()) > ds.size() ? ds.size() : (start + pageable.getPageSize());
        Page<Document> pages = new PageImpl<Document>(ds.subList(start, end), pageable, ds.size());
        return pages;
    }

    public int getDocNumber() {

        User u = new User();
        u = getMaster();

        return dr.docsNumberByClients(u.getUserId());
    }

    public Page<DocumentVersion> getVersionDocs(UUID id, Pageable pageable) {
        Document doc = dr.findById(id).orElse(null);
        List<DocumentVersion> ds = dvr.findByDocument(doc);
        int start = (int) pageable.getOffset();
        int end = (int) (start + pageable.getPageSize()) > ds.size() ? ds.size() : (start + pageable.getPageSize());
        Page<DocumentVersion> pages = new PageImpl<DocumentVersion>(ds.subList(start, end), pageable, ds.size());
        return pages;
    }

    public Page<Track> getEmailTracked(UUID id, Pageable pageable) {
        Folder folder = fr.findById(id).orElse(null);
        List<Track> trackedEmails = trackRepo.findByFolder(folder);
        int start = (int) pageable.getOffset();
        int end = (int) (start + pageable.getPageSize()) > trackedEmails.size() ? trackedEmails.size()
                : (start + pageable.getPageSize());
        Page<Track> pages = new PageImpl<Track>(trackedEmails.subList(start, end), pageable, trackedEmails.size());
        return pages;
    }

    public Document findDoc(UUID id) {
        return dr.findById(id).orElse(null);
    }

    public Page<Folder> searchFoldersAccusation(Pageable pageable) {
        return fr.findAll(new Specification<Folder>() {

            @Override
            public Predicate toPredicate(Root<Folder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.conjunction();
                p = criteriaBuilder.equal(root.get("master"), getMaster());

                // .println(1);
                p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("accuse"), 1));
                // .println(2);
                p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("finalise"), "accusation"));

                p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("addedAcces"), ""));
                // FolderType t=ftr.findFolderTypeByNameAndMaster("Courrier
                // départ",getMaster());
                // // //.println(---------1233213);

                // //.println(t.getName());
                // .println(1233213);

                // p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("type").get("id"),
                // t.getId()));

                return p;
            }
        }, pageable);
    }

    public Page<Folder> findFoldersBySteps(Pageable pageable, String type, int filter, int myOnly, String sort) {
        return fr.findAll(new Specification<Folder>() {

            @SuppressWarnings("unchecked")
            @Override
            public Predicate toPredicate(Root<Folder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                User mst = getMaster();
                User u = connectedUser();
                String datedif = "datediffinvers";
                datedif = "datediff";
                if (selectedDb.equals("msql")) {
                    datedif = "datediff";
                }
                Predicate p = criteriaBuilder.conjunction();

                p = criteriaBuilder.equal(root.get("master"), mst);
                // List<Predicate> predicates = new ArrayList<>();

                // o.add( criteriaBuilder.desc(root.get("numerotation")));
                if (!type.equals("-1")) {
                    String tp = "%" + type + "%";
                    // predicates.add(criteriaBuilder.equal(root.get("type").get("id"),type));
                    p = criteriaBuilder.and(p, criteriaBuilder.or(
                            criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.upper(root.get("type").get("name")),
                                    tp.toUpperCase())),
                            criteriaBuilder.or(criteriaBuilder
                                    .like(criteriaBuilder.upper(root.get("reference")), tp.toUpperCase())),
                            criteriaBuilder.or(criteriaBuilder.like(root.get("date"), tp)),
                            criteriaBuilder.or(criteriaBuilder
                                    .like(criteriaBuilder.upper(root.get("nature").get("name")), tp.toUpperCase())),
                            criteriaBuilder.or(
                                    criteriaBuilder.like(criteriaBuilder.upper(root.get("emet__")), tp.toUpperCase())),
                            criteriaBuilder.or(
                                    criteriaBuilder.like(criteriaBuilder.upper(root.get("dest__")), tp.toUpperCase())),
                            criteriaBuilder.or(
                                    criteriaBuilder.like(criteriaBuilder.upper(root.get("reference")),
                                            tp.toUpperCase())),
                            criteriaBuilder.or(
                                    criteriaBuilder.like(criteriaBuilder.upper(root.get("owner").get("fullName")),
                                            tp.toUpperCase()))

                    // ,criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.upper(root.get("entity")),tp.toUpperCase()))
                    , criteriaBuilder
                            .or(criteriaBuilder.like(criteriaBuilder.upper(root.get("objet")), tp.toUpperCase())),
                            criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.upper(root.get("autoRef")),
                                    tp.toUpperCase()))));

                }

                String[] order = sort.split(",");

                if (order.equals("")) {
                    query.orderBy(criteriaBuilder.desc(root.get("creation_date")));
                } else {
                    if (order[1].equals("desc")) {
                        log.info("order by : {}", order[1]);

                        query.orderBy(criteriaBuilder.desc(root.get(order[0])));

                    } else {

                        query.orderBy(criteriaBuilder.asc(root.get(order[0])));

                    }

                }
                Join joinSteps = root.join("etapes");
                Join usersSteps = joinSteps.join("users");
                List<User> currentUser = new ArrayList<>();
                currentUser.add(u);
                // currentUser.add(u);currentUser.add(u);currentUser.add(u);
                String dt = (new Date()).toString();
                List<ProfilsAbsence> pa = absenceRepo.findPaByUser(u.getUserId());
                // absenceRepo.findBySupleantAndDateFinLessThanEqualAndDateDebutGreaterThanEqual(u,dt,dt);
                Date dateNow = new Date();
                for (ProfilsAbsence profilsAbsence : pa) {
                    Date date1;
                    try {
                        date1 = new SimpleDateFormat("yyyy-MM-dd").parse(profilsAbsence.getDateDebut());
                        if (date1.before(dateNow)) {
                            currentUser.add(profilsAbsence.getUser());
                        }
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                Date d = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                /*
                 * c.add(Calendar.DATE,-1); Calendar c1 = Calendar.getInstance(); c1.setTime(d);
                 * c1.add(Calendar.DATE,1); p = criteriaBuilder.and(p,
                 * criteriaBuilder.between(root.get("creation_date"),c.getTime(),c1.getTime()));
                 */

                // builder.lessThanOrEqualTo(root.<Date>get("dateCreated"), param)

                if (myOnly == 1) {
                    p = criteriaBuilder.and(p,
                            criteriaBuilder.and(
                                    criteriaBuilder.lessThanOrEqualTo(joinSteps.get("dateDebut"), new Date()),
                                    criteriaBuilder.equal(joinSteps.get("etat"), 0)),

                            criteriaBuilder.and(criteriaBuilder.equal(root.get("owner"), u)),

                            criteriaBuilder.and(criteriaBuilder.in(usersSteps).value(u)));

                } else if (myOnly == -1) {
                    p = criteriaBuilder.and(p,
                            criteriaBuilder.and(
                                    criteriaBuilder.lessThanOrEqualTo(joinSteps.get("dateDebut"), new Date()),
                                    criteriaBuilder.equal(joinSteps.get("etat"), 0)),

                            criteriaBuilder.and(criteriaBuilder.in(usersSteps).value(currentUser)));
                }

                return p;
            }
        }, pageable);
    }

    public Page<Folder> findFoldersUsersSub(Pageable pageable, String type, int filter, String sort,
            SubSearchClass search) {
        return fr.findAll(new Specification<Folder>() {

            @Override
            public Predicate toPredicate(Root<Folder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                User mst = getMaster();
                User u = connectedUser();
                String datedif = "datediffinvers";
                datedif = "datediff";
                if (selectedDb.equals("msql")) {
                    datedif = "datediff";
                }
                Predicate p = criteriaBuilder.conjunction();

                p = criteriaBuilder.equal(root.get("master"), mst);
                // List<Predicate> predicates = new ArrayList<>();

                // o.add( criteriaBuilder.desc(root.get("numerotation")));
                if (!type.equals("-1")) {
                    String tp = "%" + type + "%";
                    // predicates.add(criteriaBuilder.equal(root.get("type").get("id"),type));
                    p = criteriaBuilder.and(p, criteriaBuilder.or(
                            criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.upper(root.get("type").get("name")),
                                    tp.toUpperCase())),
                            criteriaBuilder.or(criteriaBuilder
                                    .like(criteriaBuilder.upper(root.get("reference")), tp.toUpperCase())),
                            criteriaBuilder.or(criteriaBuilder.like(root.get("date"), tp)),
                            criteriaBuilder.or(criteriaBuilder
                                    .like(criteriaBuilder.upper(root.get("nature").get("name")), tp.toUpperCase())),
                            criteriaBuilder.or(
                                    criteriaBuilder.like(criteriaBuilder.upper(root.get("emet__")), tp.toUpperCase())),
                            criteriaBuilder.or(
                                    criteriaBuilder.like(criteriaBuilder.upper(root.get("dest__")), tp.toUpperCase()))
                    // ,criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.upper(root.get("entity")),tp.toUpperCase()))
                    , criteriaBuilder
                            .or(criteriaBuilder.like(criteriaBuilder.upper(root.get("objet")), tp.toUpperCase())),
                            criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.upper(root.get("autoRef")),
                                    tp.toUpperCase()))));

                }

                String[] order = sort.split(",");

                if (order.equals("")) {
                    query.orderBy(criteriaBuilder.desc(root.get("creation_date")));
                } else {
                    if (order[1].equals("desc")) {
                        log.info("order by : {}", order[1]);

                        query.orderBy(criteriaBuilder.desc(root.get(order[0])));

                    } else {

                        query.orderBy(criteriaBuilder.asc(root.get(order[0])));

                    }

                }
                Join joinSteps = root.join("etapes");
                Join usersSteps = joinSteps.join("users");
                List<User> currentUser = new ArrayList<>();
                for (Long long1 : search.getIds()) {
                    currentUser.add(ur.findById(long1).get());
                }

                if (search.getStatus() != "" && Objects.nonNull(search.getStatus())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("finalise"), search.getStatus()));
                }

                p = criteriaBuilder.and(p,

                        criteriaBuilder.and(criteriaBuilder.in(usersSteps).value(currentUser)));

                query.distinct(true);

                return p;
            }
        }, pageable);
    }

    public Page<Folder> findMyFolders(Pageable pageable, FolderClass fc) {
        return fr.findAll(new Specification<Folder>() {

            @Override
            public Predicate toPredicate(Root<Folder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                User mst = getMaster();
                User u = connectedUser();
                String datedif = "datediffinvers";
                datedif = "datediff";
                if (selectedDb.equals("msql")) {
                    datedif = "datediff";
                }
                Predicate p = criteriaBuilder.conjunction();

                p = criteriaBuilder.equal(root.get("master"), mst);
                // List<Predicate> predicates = new ArrayList<>();

                if (Objects.nonNull(fc.getReference()) && !fc.getReference().equals("")) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("reference"), fc.getReference()));

                }

                if (Objects.nonNull(fc.getType())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("type").get("id"), fc.getType()));

                }

                if (fc.getMode() == 1 && fc.getDest().size() > 0) {
                    /*
                     * for (String name : fc.getDest()) {
                     * p = criteriaBuilder.and(p,
                     * criteriaBuilder.like(criteriaBuilder.upper(root.get("dest__")),
                     * "%" + name.toUpperCase() + "%"));
                     * }
                     */

                    // Join<Folder, receiver> receiverJoin = root.join("receivers");
                    // p = criteriaBuilder.and(p,
                    // criteriaBuilder.and(receiverJoin.get("name").in(fc.getDest())));

                    p = criteriaBuilder.and(p, root.get("id").in(fr.findFolderDest(fc.getDest())));

                }
                if (fc.getFini() != "" && Objects.nonNull(fc.getFini())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("finalise"), fc.getFini()));
                }
                if (fc.getMode() == 2 && Objects.nonNull(fc.getSender())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("emet__"), "%" + fc.getSender() + "%"));
                }

                // o.add( criteriaBuilder.desc(root.get("numerotation")));

                String[] order = org.apache.commons.lang3.StringUtils.chop(fc.getOrder()).split(" ");

                if (fc.getOrder().equals("")) {
                    query.orderBy(criteriaBuilder.desc(root.get("creation_date")));
                } else {
                    if (order[1].equals("desc")) {
                        log.info("order by : {}", order[1]);
                        query.orderBy(criteriaBuilder.desc(root.get(order[0])));
                    } else {
                        query.orderBy(criteriaBuilder.asc(root.get(order[0])));
                    }

                }

                if (Objects.nonNull(fc.getObjet()) && !StringUtils.isEmpty(fc.getObjet())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(criteriaBuilder.upper(root.get("objet")),
                            "%" + fc.getObjet().toUpperCase() + "%"));

                }

                if (Objects.nonNull(fc.getObjet()) && !StringUtils.isEmpty(fc.getObjet())) {
                    p = criteriaBuilder.and(p, criteriaBuilder.like(criteriaBuilder.upper(root.get("objet")),
                            "%" + fc.getObjet().toUpperCase() + "%"));

                }

                if (Objects.nonNull(fc.getDeDate()) && Objects.nonNull(fc.getToDate())
                        && !StringUtils.isEmpty(fc.getDeDate()) && !StringUtils.isEmpty(fc.getToDate())) {
                    p = criteriaBuilder.and(p,
                            criteriaBuilder.between(root.get("date"), fc.getDeDate(), fc.getToDate()));
                }

                Join joinSteps = root.join("etapes");
                // Join usersSteps = joinSteps.join("users");
                // List<User> currentUser = new ArrayList<>();

                /*
                 * c.add(Calendar.DATE,-1); Calendar c1 = Calendar.getInstance(); c1.setTime(d);
                 * c1.add(Calendar.DATE,1); p = criteriaBuilder.and(p,
                 * criteriaBuilder.between(root.get("creation_date"),c.getTime(),c1.getTime()));
                 */

                // builder.lessThanOrEqualTo(root.<Date>get("dateCreated"), param)

                p = criteriaBuilder.and(p,
                        criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(joinSteps.get("dateDebut"), new Date()),
                                criteriaBuilder.equal(joinSteps.get("traitant"), connectedUser().getUserId())));

                /*
                 * 
                 * p= criteriaBuilder.and(p,
                 * criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.
                 * function(datedif, Integer.class, criteriaBuilder.literal(new Date()),
                 * joinSteps.get("dateDebut")), 0) ,criteriaBuilder.equal(joinSteps.get("etat"),
                 * 0)),
                 * 
                 * criteriaBuilder.and(criteriaBuilder.in(usersSteps).value(currentUser)));
                 */

                return p;
            }
        }, pageable);
    }
}
