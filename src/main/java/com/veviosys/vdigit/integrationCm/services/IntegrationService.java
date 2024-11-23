package com.veviosys.vdigit.integrationCm.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;
import com.veviosys.vdigit.classes.AttributeIntegration;
import com.veviosys.vdigit.classes.DocTypeIntegration;
import com.veviosys.vdigit.classes.mapType;
import com.veviosys.vdigit.integrationCm.classes.Attrs;
import com.veviosys.vdigit.integrationCm.classes.DocumentExport;
import com.veviosys.vdigit.integrationCm.models.OredrIntegration;
import com.veviosys.vdigit.integrationCm.repository.OrderIntegretionRepo;
import com.veviosys.vdigit.models.Attribute;
import com.veviosys.vdigit.models.DbAttrsConfig;
import com.veviosys.vdigit.models.Document;
import com.veviosys.vdigit.models.DocumentAttributeValue;
import com.veviosys.vdigit.models.DocumentFolder;
import com.veviosys.vdigit.models.DocumentType;
import com.veviosys.vdigit.models.Folder;
import com.veviosys.vdigit.models.Journal;
import com.veviosys.vdigit.models.MasterConfig;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.models.receiver;
import com.veviosys.vdigit.reposietories.AttributeRepo;
import com.veviosys.vdigit.repositories.DbAttrsConfigRepo;
import com.veviosys.vdigit.repositories.DocumentRepo;
import com.veviosys.vdigit.repositories.DocumentTypeRepo;
import com.veviosys.vdigit.repositories.FolderRepo;
import com.veviosys.vdigit.repositories.JournalRepo;
import com.veviosys.vdigit.repositories.UserRepository;
import com.veviosys.vdigit.services.AlimentationService;
import com.veviosys.vdigit.services.CostumUserDetails;
import com.veviosys.vdigit.services.FileStorageServiceImpl;
import com.veviosys.vdigit.services.MasterConfigService;

@Service
public class IntegrationService {

    @Autowired
    private OrderIntegretionRepo orderIntegretionRepo;
    @Autowired
    FolderRepo folderRepo;
    @Autowired
    DocumentTypeRepo DTR;
    @Autowired
    FileStorageServiceImpl fileStorageServiceImpl;
    @Autowired
    AlimentationService alimentationService;
    @Autowired
    MasterConfigService masterConfigService;
    @Autowired
    DbAttrsConfigRepo dbAttrsConfigRepo;
    @Autowired
    AttributeRepo attributeRepo;
    @Autowired
    UserRepository ur;
    @Autowired
    JournalRepo jr;
    @Autowired
    DocumentTypeRepo dtr;
    @Autowired
    DocumentRepo dtR;

    @Value("${db.name}")
    private String db;
    @Value("${db.user}")
    private String dbus;
    @Value("${db.pw}")
    private String dbpw;
    @Value("${database.datatypes.longtext}")
    private String longTextType;
    @Value("${documania.datasource.select}")
    private String dbSelected;

    Logger logger = LoggerFactory.getLogger("ApiIntegration");

    public Map<String, String> checkOrder(String name) {

        Map<String, String> map = new HashMap<String, String>();

        OredrIntegration order = orderIntegretionRepo.findByName(name);
        List<OredrIntegration> orders = orderIntegretionRepo.findAll();

        for (OredrIntegration oredrIntegration : orders) {

            if (order.getName().equals("chaine") && order.getProcessed() == 0) {

                map.put("state", "yes");

            } else if (order.getName().equals("chaine") && order.getProcessed() == 1) {

                map.put("state", "imported");

            }
            if (oredrIntegration.getName().equals("chaine") && oredrIntegration.getProcessed() == 1) {

                if (order.getName().equals("attribute") && order.getProcessed() == 1) {

                    map.put("state", "imported");

                } else if (order.getName().equals("attribute") && order.getProcessed() == 0) {

                    map.put("state", "yes");

                }

            }
            if (oredrIntegration.getName().equals("chaine") && oredrIntegration.getProcessed() == 0) {

                map.put("state", "no");
            }

            if (oredrIntegration.getName().equals("attribute") && oredrIntegration.getProcessed() == 1) {

                if (order.getName().equals("docType") && order.getProcessed() == 1) {

                    map.put("state", "imported");

                } else if (order.getName().equals("attribute") && order.getProcessed() == 0) {

                    map.put("state", "yes");

                } else if (order.getName().equals("docType") && order.getProcessed() == 0) {

                    map.put("state", "yes");

                }

            }
            if (order.getName().equals("docType") && oredrIntegration.getName().equals("attribute")
                    && oredrIntegration.getProcessed() == 0) {

                map.put("state", "no");

            }

        }

        return map;
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

    public String Login(String username, String password) {

        WebClient client = WebClient.create();

        WebClient.ResponseSpec responseSpec = client.get()
                .uri("https://jsonplaceholder.typicode.com/posts")
                .header("Authorization",
                        "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
                .retrieve();

        String responseBody = responseSpec.bodyToMono(String.class).block();

        for (int i = 0; i < 145800; i++) {
            logger.info("complete with success");

        }

        return responseBody;

    }

    public boolean getChaineConnexion(String secondary) {

        try {

            Connection conn = DriverManager.getConnection(db, dbus, dbpw);

            MasterConfig user1 = masterConfigService.getCmIntegration("USER_INREGRATION_INFO");
            MasterConfig url = masterConfigService.getCmIntegration("CM_INTEGRATION");
            byte[] decodedBytes = Base64.getDecoder().decode(user1.getConfigValue());
            String decodedString = new String(decodedBytes);
            com.veviosys.vdigit.classes.Login userInfo = new Gson().fromJson(decodedString,
                    com.veviosys.vdigit.classes.Login.class);

            WebClient client = WebClient.create();

            WebClient.ResponseSpec responseSpec = client.mutate().codecs(configurer -> configurer
                    .defaultCodecs()
                    .maxInMemorySize(16 * 1024 * 1024)).build().get()
                    .uri(url.getConfigValue() + "/cm-connector/connection-links/list")
                    .header("Authorization",
                            "Basic " + Base64.getEncoder()
                                    .encodeToString((userInfo.getUsername() + ":" + userInfo.getPassword()).getBytes()))
                    .retrieve().onStatus(
                            HttpStatus.UNAUTHORIZED::equals,
                            response -> response.bodyToMono(String.class).map(Exception::new));

            List<DbAttrsConfig> responseBody = Arrays.stream(responseSpec.bodyToMono(DbAttrsConfig[].class).block())
                    .collect(Collectors.toList());

            for (DbAttrsConfig dbAttrsConfig : responseBody) {

                DbAttrsConfig config = dbAttrsConfigRepo.findById(dbAttrsConfig.getId()).orElse(null);

                if (Objects.isNull(config)) {
                    String str = "insert into db_attrs_config" +
                            "(id,app,config_name,db_name,host,pass,table_name,port,name,master)" +
                            "VALUES" + "(?,?,?,?,?,?,?,?,?,?);";

                    PreparedStatement pe = conn.prepareStatement(str);

                    pe.setLong(1, dbAttrsConfig.getId());
                    pe.setString(2, dbAttrsConfig.getApp());
                    pe.setString(3, dbAttrsConfig.getConfigName());
                    pe.setString(4, dbAttrsConfig.getDbName());
                    pe.setString(5, dbAttrsConfig.getHost());
                    pe.setString(6, dbAttrsConfig.getPass());
                    pe.setString(7, dbAttrsConfig.getTable());
                    pe.setString(8, dbAttrsConfig.getPort());
                    pe.setString(9, dbAttrsConfig.getUser());
                    pe.setLong(10, connectedUser().getUserId());

                    pe.executeUpdate();

                    str = "";
                    logger.info("insert successfylly");

                    if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
                        Journal j = new Journal();
                        String a = "";
                        a = " Name : " + dbAttrsConfig.getDbName();
                        j.setUser(connectedUser());
                        j.setDate(new Date());
                        j.setMode("A");
                        if (secondary.equals("true")) {

                            User user = ur.getSecondaryUser(connectedUser().getUserId());

                            j.setTypeEv("Système/Ajout/Secondaire Profil");
                            if (Objects.nonNull(user)) {
                                j.setConnectedSacondaryName(user.getFullName());
                            }

                        } else {
                            j.setTypeEv("Système/Ajout");
                        }
                        j.setComposante("Ajouter Chaine connexion Integration Import");

                        j.setAction(a);
                        j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

                        jr.save(j);
                    }

                }
            }
            conn.close();
            OredrIntegration chaine = orderIntegretionRepo.findByName("chaine");
            chaine.setProcessed(1);
            orderIntegretionRepo.save(chaine);
        } catch (Exception e) {

            logger.info(e.getMessage());
            return false;
        }

        return true;
    }

    public void insertAttrs(AttributeIntegration attribute, String secondary, Connection conn, int i) {
        try {

            if (attribute.getRep() == null) {
                attribute.setRep("0");
            }
            if (attribute.getRequired() == null) {
                attribute.setRequired("0");
            }
            if (attribute.getVisib() == null) {
                attribute.setVisib("1");
            }

            String str = "insert into attribute" +
                    "(id,config_id,default_value,labelar,labeleng,labelfr,libelle,list_dep,name,rep,required,table_config,visib,master,attribute_type)"
                    +
                    "VALUES" +
                    "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

            PreparedStatement pe = conn.prepareStatement(str);

            pe.setLong(1, attribute.getId());
            pe.setString(2, attribute.getConfigId());
            pe.setString(3, attribute.getDefaultValue());
            pe.setString(4, attribute.getLibelle());
            pe.setString(5, attribute.getLibelle());
            pe.setString(6, attribute.getLibelle());
            pe.setString(7, attribute.getLibelle());
            pe.setString(8, attribute.getListDep());
            pe.setString(9, attribute.getName());
            pe.setString(10, attribute.getRep());
            pe.setString(11, attribute.getRequired());
            pe.setString(12, attribute.getTableConfig());
            pe.setString(13, attribute.getVisib());
            pe.setLong(14, connectedUser().getUserId());
            pe.setLong(15, attribute.getType().getId());

            pe.executeUpdate();

            str = "";
            logger.info("insert successfylly" + i);

        } catch (Exception e) {
            logger.info(e.getMessage());
            logger.info("Aleardy Exist : " + attribute.getName());
        }

        if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
            Journal j = new Journal();
            String a = "";
            a = " Libelle : " + attribute.getLibelle() + " |   Type : " + attribute.getType().getName()
                    + " | Name : " + attribute.getName();
            j.setUser(connectedUser());
            j.setDate(new Date());
            j.setMode("A");
            if (secondary.equals("true")) {

                User user = ur.getSecondaryUser(connectedUser().getUserId());

                j.setTypeEv("Système/Ajout/Secondaire Profil");
                if (Objects.nonNull(user)) {
                    j.setConnectedSacondaryName(user.getFullName());
                }

            } else {
                j.setTypeEv("Système/Ajout");
            }
            j.setComposante("Ajouter Attribute Integration Import");

            j.setAction(a);
            j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

            jr.save(j);
        }

    }

    public boolean getAttribute(String secondary) {

        try {

            Connection conn = DriverManager.getConnection(db, dbus, dbpw);

            WebClient client = WebClient.create();
            MasterConfig user = masterConfigService.getCmIntegration("USER_INREGRATION_INFO");
            MasterConfig url = masterConfigService.getCmIntegration("CM_INTEGRATION");
            byte[] decodedBytes = Base64.getDecoder().decode(user.getConfigValue());
            String decodedString = new String(decodedBytes);
            com.veviosys.vdigit.classes.Login userInfo = new Gson().fromJson(decodedString,
                    com.veviosys.vdigit.classes.Login.class);
            WebClient.ResponseSpec responseSpec = client.mutate().codecs(configurer -> configurer
                    .defaultCodecs()
                    .maxInMemorySize(16 * 1024 * 1024)).build().get()
                    .uri(url.getConfigValue() + "/cm-connector/attributes-list")
                    .header("Authorization",
                            "Basic " + Base64.getEncoder()
                                    .encodeToString((userInfo.getUsername() + ":" + userInfo.getPassword()).getBytes()))
                    .retrieve().onStatus(
                            HttpStatus.UNAUTHORIZED::equals,
                            response -> response.bodyToMono(String.class).map(Exception::new));

            List<AttributeIntegration> responseBody = Arrays
                    .stream(responseSpec.bodyToMono(AttributeIntegration[].class).block()).collect(Collectors.toList());

            List<Attribute> list = attributeRepo.findAll();

            System.out.println(list.toString());

            int i = 0;
            for (AttributeIntegration attribute : responseBody) {

                if (list.isEmpty()) {
                    insertAttrs(attribute, secondary, conn, i);
                } else {
                    int check = 0;
                    for (Attribute attribute2 : list) {
                        if (attribute.getName().equals(attribute2.getName())) {
                            check = 1;
                        }
                    }
                    if (check == 0) {
                        insertAttrs(attribute, secondary, conn, i);
                    }
                    check = 0;
                }

            }

            conn.close();
            OredrIntegration attribute11 = orderIntegretionRepo.findByName("attribute");
            attribute11.setProcessed(1);
            orderIntegretionRepo.save(attribute11);

        } catch (Exception e) {
            logger.info(e.getMessage());
            return false;
        }

        return true;
    }

    public User getprinc() {
        CostumUserDetails user = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        return user.getUser();
    }

    public void insertTypeDocument(DocTypeIntegration documentType, Connection conn, WebClient client,
            com.veviosys.vdigit.classes.Login userInfo, MasterConfig url, String secondary) {

        try {

            String str1 = "insert into document_type" +
                    "(id,is_versionable,name,selected,group_goup_id,master,libelle)" +
                    "VALUES" +
                    "(?,?,?,?,?,?,?);";

            PreparedStatement pe;

            pe = conn.prepareStatement(str1);

            pe.setLong(1, documentType.getId());
            pe.setInt(2, documentType.getIsVersionable());
            pe.setString(3, documentType.getName());
            pe.setInt(4, documentType.getSelected());
            pe.setString(5, null);
            pe.setLong(6, connectedUser().getUserId());
            pe.setString(7, documentType.getLabel());
            pe.executeUpdate();
            if (documentType.getSelected() == 1) {
                DTR.addType(getprinc().getUserId(), documentType.getId());
            }

            logger.info("Type Instance Created");

            str1 = "";

            List<Attribute> lstA = new ArrayList<Attribute>();
            DocumentType dt = dtr.findByNameAndMaster(documentType.getName(), connectedUser());
            dt.setAttributes(lstA);

            dtr.saveAndFlush(dt);

            WebClient.ResponseSpec response = client.mutate().codecs(configurer -> configurer
                    .defaultCodecs()
                    .maxInMemorySize(16 * 1024 * 1024)).build().get()
                    .uri(url.getConfigValue() + "/cm-connector/type/" + dt.getId() + "/attributes")
                    .header("Authorization",
                            "Basic " + Base64.getEncoder()
                                    .encodeToString((userInfo.getUsername() + ":" + userInfo.getPassword()).getBytes()))
                    .retrieve().onStatus(
                            HttpStatus.UNAUTHORIZED::equals,
                            response1 -> response1.bodyToMono(String.class).map(Exception::new));

            List<AttributeIntegration> atrributes = Arrays
                    .stream(response.bodyToMono(AttributeIntegration[].class).block()).collect(Collectors.toList());

            List<mapType> lsAttr = new ArrayList();
            for (AttributeIntegration attribute : atrributes) {
                mapType map = new mapType();
                map.setKey(attribute.getName());
                map.setValue(Integer.parseInt(attribute.getRequired()));
                map.setRep(Integer.parseInt(attribute.getRep()));
                map.setVisible(Integer.parseInt(attribute.getVisib()));
                lsAttr.add(map);
                map = new mapType();
            }
            User us = connectedUser();

            Long a = Long.valueOf(6);
            // dt.getName().replace(' ', '_').replace("'", "_") +
            String str = "create table d" + dt.getId()
                    + "(id varchar(100) primary key not null ,upload_date VARCHAR(100),last_edit_date date,"
                    + "owner int,master int, content_type  VARCHAR(100), file_name  VARCHAR(100) ";
            int b = 0;
            String ch = "";

            for (int i = 0; i < lsAttr.size(); i++) {

                Attribute atr = attributeRepo.findByNameAndMaster(lsAttr.get(i).key, connectedUser());

                String tempName = "";
                if (atr.getName().equals("Date"))
                    tempName = "Date_";

                else
                    tempName = atr.getName();
                str += ", " + tempName.replace(' ', '_').replace("'", "_") + " " + longTextType;

                // System.out.println(atr.getName());
                ch += " | " + atr.getName() + " : " + atr.getType().getName();

            }
            str += ", type_name VARCHAR(1200) )";

            for (mapType attribute1 : lsAttr) {
                dtr.typeAttr(dtr.findByNameAndMaster(dt.getName(), us).getId(),
                        attributeRepo.findByNameAndMaster(attribute1.key, us).getId(), attribute1.value,
                        attribute1.rep, attribute1.visible);

            }
            logger.info("Query + " + str);
            try {
                conn.createStatement().executeUpdate(str);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                logger.info("Error executioin" + str);
            }

            if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
                Journal j = new Journal();

                j.setUser(connectedUser());
                j.setDate(new Date());

                j.setMode("A");
                j.setComposante("Type document Integration Import");
                if (secondary.equals("true")) {

                    User user = ur.getSecondaryUser(connectedUser().getUserId());

                    j.setTypeEv("Système/Ajout/Secondaire Profil");

                    if (Objects.nonNull(user)) {
                        j.setConnectedSacondaryName(user.getFullName());
                    }

                } else {
                    j.setTypeEv("Système/Ajout");
                }
                j.setAction(" Type : " + dt.getName() + ch);
                j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

                jr.save(j);
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean getTypeDocument(String secondary) {

        try {

            WebClient client = WebClient.create();
            Connection conn = DriverManager.getConnection(db, dbus, dbpw);
            MasterConfig user1 = masterConfigService.getCmIntegration("USER_INREGRATION_INFO");
            MasterConfig url = masterConfigService.getCmIntegration("CM_INTEGRATION");

            byte[] decodedBytes = Base64.getDecoder().decode(user1.getConfigValue());
            String decodedString = new String(decodedBytes);
            com.veviosys.vdigit.classes.Login userInfo = new Gson().fromJson(decodedString,
                    com.veviosys.vdigit.classes.Login.class);

            WebClient.ResponseSpec responseSpec = client.mutate().codecs(configurer -> configurer
                    .defaultCodecs()
                    .maxInMemorySize(16 * 1024 * 1024)).build().get()
                    .uri(url.getConfigValue() + "/cm-connector/doctypes-list")
                    .header("Authorization",
                            "Basic " + Base64.getEncoder()
                                    .encodeToString((userInfo.getUsername() + ":" + userInfo.getPassword()).getBytes()))
                    .retrieve().onStatus(
                            HttpStatus.UNAUTHORIZED::equals,
                            response -> response.bodyToMono(String.class).map(Exception::new));

            List<DocTypeIntegration> responseBody = Arrays
                    .stream(responseSpec.bodyToMono(DocTypeIntegration[].class).block()).collect(Collectors.toList());
            int count = 0;
            logger.info("size is " + responseBody.size());
            List<DocumentType> alldocType = dtr.findAll();
            logger.info("size is in " + alldocType.size());

            for (DocTypeIntegration documentType : responseBody) {

                DocumentType doctype = dtr.findById(documentType.getId()).orElse(null);

                if (Objects.isNull(doctype)) {

                    if (documentType.getSelected() == 1) {
                        insertTypeDocument(documentType, conn, client, userInfo, url, secondary);
                        count++;
                        logger.info("insert successfylly " + count);
                    }

                }

            }

            conn.close();
            OredrIntegration docType = orderIntegretionRepo.findByName("docType");
            docType.setProcessed(1);
            orderIntegretionRepo.save(docType);

        } catch (Exception e) {
            logger.info(e.getMessage());
            return false;
        }

        return true;
    }

    public boolean exportDocuments(UUID folderId) throws IOException {

        try {
            Folder folder = folderRepo.findById(folderId).orElse(null);

            MasterConfig user = masterConfigService.getCmIntegration("USER_INREGRATION_INFO");
            MasterConfig url = masterConfigService.getCmIntegration("CM_INTEGRATION");

            byte[] decodedBytes = Base64.getDecoder().decode(user.getConfigValue());
            String decodedString = new String(decodedBytes);
            com.veviosys.vdigit.classes.Login userInfo = new Gson().fromJson(decodedString,
                    com.veviosys.vdigit.classes.Login.class);

            String mc = masterConfigService.findActivePath();
            String cheminTemp = mc + "\\upload\\tempfiles\\";

            for (DocumentFolder documentFolder : folder.getDocuments()) {

                DocumentExport document = new DocumentExport();

                document.setName(documentFolder.getDocument().getType().getName());
                document.setType(documentFolder.getDocument().getType().getId());
                document.setContent(documentFolder.getDocument().getContentType());
                document.setFileName(documentFolder.getDocument().getFileName());
                document.setGenerated(false);

                Attrs attr = new Attrs();
                List<Attribute> attributes = alimentationService
                        .getAttrsByType(documentFolder.getDocument().getType().getId());
                List<Attrs> attrs = new ArrayList<Attrs>();
                for (DocumentAttributeValue attribute : documentFolder.getDocument().getAttributeValues()) {

                    attr.setId(attribute.getAttribute().getId());
                    attr.setName(attribute.getAttribute().getName());
                    attr.setType(attribute.getAttribute().getType().getName());
                    attr.setVal(attribute.getValue().getValue());

                    attrs.add(attr);
                    attr = new Attrs();

                }

                for (Attribute attribute : attributes) {

                    if (attribute.getName().equals("DateCourrier")) {
                        attr.setId(attribute.getId());
                        attr.setName(attribute.getName());
                        attr.setType(attribute.getType().getName());
                        attr.setVal(folder.getDate());
                        attrs.add(attr);
                        attr = new Attrs();
                    } else if (attribute.getName().equals("DateEnregistrement")) {
                        attr.setId(attribute.getId());
                        attr.setName(attribute.getName());
                        attr.setType(attribute.getType().getName());
                        attr.setVal(documentFolder.getDocument().getUpload_date().toString());
                        attrs.add(attr);
                        attr = new Attrs();
                    } else if (attribute.getName().equals("Destinataire")) {
                        attr.setId(attribute.getId());
                        attr.setName(attribute.getName());
                        attr.setType(attribute.getType().getName());
                        String dests = "";
                        for (receiver dest : folder.getDest()) {
                            dests += dest.getName() + ",";
                        }
                        attr.setVal(dests);
                        attrs.add(attr);
                        attr = new Attrs();
                    } else if (attribute.getName().equals("Expediteur")) {
                        attr.setId(attribute.getId());
                        attr.setName(attribute.getName());
                        attr.setType(attribute.getType().getName());
                        attr.setVal(folder.getEmet__());
                        attrs.add(attr);
                        attr = new Attrs();
                    } else if (attribute.getName().equals("Nature_Document")) {
                        attr.setId(attribute.getId());
                        attr.setName(attribute.getName());
                        attr.setType(attribute.getType().getName());
                        attr.setVal(folder.getNatureName());
                        attrs.add(attr);
                        attr = new Attrs();
                    } else if (attribute.getName().equals("Num_Courrier")) {
                        attr.setId(attribute.getId());
                        attr.setName(attribute.getName());
                        attr.setType(attribute.getType().getName());
                        attr.setVal(folder.getReference());
                        attrs.add(attr);
                        attr = new Attrs();
                    } else if (attribute.getName().equals("ObjetCourrier")) {
                        attr.setId(attribute.getId());
                        attr.setName(attribute.getName());
                        attr.setType(attribute.getType().getName());
                        attr.setVal(folder.getObjet());
                        attrs.add(attr);
                        attr = new Attrs();
                    } else if (attribute.getName().equals("Nom_Dossier")) {
                        attr.setId(attribute.getId());
                        attr.setName(attribute.getName());
                        attr.setType(attribute.getType().getName());
                        attr.setVal(null);
                        attrs.add(attr);
                        attr = new Attrs();
                    }

                }

                document.setAttrs(attrs);

                WebClient client = WebClient.create();

                WebClient.ResponseSpec responseSpec = client.post()
                        .uri(url.getConfigValue() + "/cm-connector/documents")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)

                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(
                                                (userInfo.getUsername() + ":" + userInfo.getPassword()).getBytes()))
                        .body(BodyInserters.fromValue(document)).retrieve().onStatus(
                                HttpStatus.UNAUTHORIZED::equals,
                                response -> response.bodyToMono(String.class).map(Exception::new));

                HashMap<String, String> DocId = responseSpec.bodyToMono(HashMap.class).timeout(Duration.ofSeconds(30))
                        .block();

                logger.info(DocId.get("id"));

                if (documentFolder.getDocument().getFileName() != null  && documentFolder.getDocument().getContentType() != null) {
                    String savePath = cheminTemp + documentFolder.getDocument().getId() + "_"
                            + documentFolder.getDocument().getFileName();
                    // .println(savePath);
                    String data = fileStorageServiceImpl
                            .binaryFileToBase64(documentFolder.getDocument().getPathServer())
                            .split("==")[0].replaceAll("\\n", "").replaceAll("\\r", "");
                    FileOutputStream fos = new FileOutputStream(savePath);

                    byte[] decoder = Base64.getDecoder().decode(new String(data));
                    fos.write(decoder);
                    fos.close();

                    Path path = Paths.get(savePath);
                    String name = document.getFileName();
                    String originalFileName = document.getFileName();
                    String contentType = document.getContent();
                    byte[] content = null;

                    content = Files.readAllBytes(path);

                    MultipartFile multipartFile = new MockMultipartFile(name,
                            originalFileName, contentType, content);

                    MultipartBodyBuilder builder = new MultipartBodyBuilder();
                    builder.part("file", multipartFile.getResource());

                    WebClient.ResponseSpec responseSpec1 = client.post()
                            .uri(url.getConfigValue() + "/cm-connector/documentfile/" + DocId.get("id")
                                    + "/0/0/44?x=0&y=0&h=0&w=0")
                            .header("Authorization",
                                    "Basic " + Base64.getEncoder()
                                            .encodeToString(
                                                    (userInfo.getUsername() + ":" + userInfo.getPassword()).getBytes()))
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .body(BodyInserters.fromMultipartData(builder.build())).retrieve();

                    ResponseEntity DocIds = responseSpec1.bodyToMono(ResponseEntity.class).block();

                }

                logger.info("document insert succesfuly : " + document.getFileName());
                Document documentCourrier = dtR.findById(documentFolder.getDocument().getId()).get();
                documentCourrier.setArchiveCm(1);
                dtR.save(documentCourrier);
            }

        } catch (Exception e) {
            logger.info(e.getMessage());
            return false;
        }
        return true;

    }

    public boolean exportDocument(UUID docId, UUID folderId) throws IOException {

        try {

            Folder folder = folderRepo.findById(folderId).orElse(null);

            Document documentCourrier = dtR.getOne(docId);
            MasterConfig user = masterConfigService.getCmIntegration("USER_INREGRATION_INFO");
            MasterConfig url = masterConfigService.getCmIntegration("CM_INTEGRATION");
            String mc = masterConfigService.findActivePath();
            String cheminTemp = mc + "\\upload\\tempfiles\\";

            byte[] decodedBytes = Base64.getDecoder().decode(user.getConfigValue());
            String decodedString = new String(decodedBytes);
            com.veviosys.vdigit.classes.Login userInfo = new Gson().fromJson(decodedString,
                    com.veviosys.vdigit.classes.Login.class);

            DocumentExport document = new DocumentExport();

            document.setName(documentCourrier.getType().getName());
            document.setType(documentCourrier.getType().getId());
            document.setContent(documentCourrier.getContentType());
            document.setFileName(documentCourrier.getFileName());
            document.setGenerated(false);

            Attrs attr = new Attrs();
            List<Attribute> attributes = alimentationService.getAttrsByType(documentCourrier.getType().getId());
            List<Attrs> attrs = new ArrayList<Attrs>();
            for (DocumentAttributeValue attribute : documentCourrier.getAttributeValues()) {

                attr.setId(attribute.getAttribute().getId());
                attr.setName(attribute.getAttribute().getName());
                attr.setType(attribute.getAttribute().getType().getName());
                attr.setVal(attribute.getValue().getValue().length() == 0 ? null : attribute.getValue().getValue());
                attrs.add(attr);
                attr = new Attrs();

            }

            for (Attribute attribute : attributes) {

                if (attribute.getName().equals("DateCourrier")) {
                    attr.setId(attribute.getId());
                    attr.setName(attribute.getName());
                    attr.setType(attribute.getType().getName());
                    attr.setVal(folder.getDate());
                    attrs.add(attr);
                    attr = new Attrs();
                } else if (attribute.getName().equals("DateEnregistrement")) {
                    attr.setId(attribute.getId());
                    attr.setName(attribute.getName());
                    attr.setType(attribute.getType().getName());
                    attr.setVal(documentCourrier.getUpload_date().toString());
                    attrs.add(attr);
                    attr = new Attrs();
                } else if (attribute.getName().equals("Destinataire")) {
                    attr.setId(attribute.getId());
                    attr.setName(attribute.getName());
                    attr.setType(attribute.getType().getName());
                    String dests = "";
                    for (receiver dest : folder.getDest()) {
                        dests += dest.getName() + ",";
                    }
                    attr.setVal(dests);
                    attrs.add(attr);
                    attr = new Attrs();
                } else if (attribute.getName().equals("Expediteur")) {
                    attr.setId(attribute.getId());
                    attr.setName(attribute.getName());
                    attr.setType(attribute.getType().getName());
                    attr.setVal(folder.getEmet__());
                    attrs.add(attr);
                    attr = new Attrs();
                } else if (attribute.getName().equals("Nature_Document")) {
                    attr.setId(attribute.getId());
                    attr.setName(attribute.getName());
                    attr.setType(attribute.getType().getName());
                    attr.setVal(folder.getNatureName());
                    attrs.add(attr);
                    attr = new Attrs();
                } else if (attribute.getName().equals("Num_Courrier")) {
                    attr.setId(attribute.getId());
                    attr.setName(attribute.getName());
                    attr.setType(attribute.getType().getName());
                    attr.setVal(folder.getReference());
                    attrs.add(attr);
                    attr = new Attrs();
                } else if (attribute.getName().equals("ObjetCourrier")) {
                    attr.setId(attribute.getId());
                    attr.setName(attribute.getName());
                    attr.setType(attribute.getType().getName());
                    attr.setVal(folder.getObjet());
                    attrs.add(attr);
                    attr = new Attrs();
                } else if (attribute.getName().equals("Nom_Dossier")) {
                    attr.setId(attribute.getId());
                    attr.setName(attribute.getName());
                    attr.setType(attribute.getType().getName());
                    attr.setVal(null);
                    attrs.add(attr);
                    attr = new Attrs();
                }

            }

            document.setAttrs(attrs);

            WebClient client = WebClient.create();

            WebClient.ResponseSpec responseSpec = client.post().uri(url.getConfigValue() + "/cm-connector/documents")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)

                    .header("Authorization",
                            "Basic " + Base64.getEncoder()
                                    .encodeToString((userInfo.getUsername() + ":" + userInfo.getPassword()).getBytes()))
                    .body(BodyInserters.fromValue(document)).retrieve().onStatus(
                            HttpStatus.UNAUTHORIZED::equals,
                            response -> response.bodyToMono(String.class).map(Exception::new));

            HashMap<String, String> DocId = responseSpec.bodyToMono(HashMap.class).timeout(Duration.ofSeconds(30))
                    .block();

            logger.info(DocId.get("id"));

            if (documentCourrier.getFileName() != null  && documentCourrier.getContentType() != null) {
                String savePath = cheminTemp + documentCourrier.getId() + "_"
                        + documentCourrier.getFileName();
                // .println(savePath);
                String data = fileStorageServiceImpl.binaryFileToBase64(documentCourrier.getPathServer())
                        .split("==")[0].replaceAll("\\n", "").replaceAll("\\r", "");
                FileOutputStream fos = new FileOutputStream(savePath);

                byte[] decoder = Base64.getDecoder().decode(new String(data));
                fos.write(decoder);
                fos.close();

                Path path = Paths.get(savePath);
                String name = document.getFileName();
                String originalFileName = document.getFileName();
                String contentType = document.getContent();
                byte[] content = null;

                content = Files.readAllBytes(path);

                MultipartFile multipartFile = new MockMultipartFile(name,
                        originalFileName, contentType, content);

                MultipartBodyBuilder builder = new MultipartBodyBuilder();
                builder.part("file", multipartFile.getResource());

                WebClient.ResponseSpec responseSpec1 = client.post()
                        .uri(url.getConfigValue() + "/cm-connector/documentfile/" + DocId.get("id")
                                + "/0/0/44?x=0&y=0&h=0&w=0")
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(
                                                (userInfo.getUsername() + ":" + userInfo.getPassword()).getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .body(BodyInserters.fromMultipartData(builder.build())).retrieve();

                ResponseEntity DocIds = responseSpec1.bodyToMono(ResponseEntity.class).block();

            }

            logger.info("document insert succesfuly : " + document.getFileName());

            documentCourrier.setArchiveCm(1);
            dtR.save(documentCourrier);

        } catch (Exception e) {
            logger.info(e.getMessage());
            return false;
        }
        return true;

    }
}
