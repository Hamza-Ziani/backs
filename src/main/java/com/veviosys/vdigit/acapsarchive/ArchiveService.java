package com.veviosys.vdigit.acapsarchive;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;
import com.veviosys.vdigit.classes.ArchiveClass;
import com.veviosys.vdigit.classes.ArchiveCourrier;
import com.veviosys.vdigit.classes.ArchiveEtapes;
import com.veviosys.vdigit.classes.ArchiveFormatResponse;
import com.veviosys.vdigit.classes.ArriveFormatRequest;
import com.veviosys.vdigit.classes.Fichier;
import com.veviosys.vdigit.classes.File64;
import com.veviosys.vdigit.models.DbAttrsConfig;
import com.veviosys.vdigit.models.MasterConfig;
import com.veviosys.vdigit.models.Sender;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.repositories.ReceiverRepo;
import com.veviosys.vdigit.repositories.SenderRepo;
import com.veviosys.vdigit.repositories.UserRepository;
import com.veviosys.vdigit.services.CostumUserDetails;
import com.veviosys.vdigit.services.MasterConfigService;

@Service
public class ArchiveService {

    @Value("${acaps.archivedb.name}")
    private String dbName;
    @Value("${db.user}")
    private String user;
    @Value("${db.pw}")
    private String pw;
    @Value("${acaps.archivedb.fileStorage}")
    private String path;

    @Autowired
    SenderRepo senderRepo;
    @Autowired
    ReceiverRepo receiverRepo;
    
    @Lazy
    @Autowired
    MasterConfigService masterConfigService;

    @Autowired
    UserRepository userRepo;

    public User connectedUser() {
        return ((CostumUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

    public File64 getArriveFiles(Fichier fichier) {
        File64 file64 = null;

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
                .uri(url.getConfigValue() + "/cm-connector/files/view/" + fichier.getId())
                .header("Authorization",
                        "Basic " + Base64.getEncoder()
                                .encodeToString((userInfo.getUsername() + ":" + userInfo.getPassword()).getBytes()))
                .retrieve().onStatus(
                        HttpStatus.UNAUTHORIZED::equals,
                        response1 -> response1.bodyToMono(String.class).map(Exception::new));

        File64 responseBody = responseSpec.bodyToMono(File64.class).block();

        return responseBody;
    }

    public Page<ArchiveCourrier> getSearchDepart(ArchiveClass searchOptions, Pageable pageable, List<String> ids,
            Connection conn) {
        List<ArchiveCourrier> archiveList = new ArrayList<ArchiveCourrier>();
        String query = "";
        ArchiveCourrier courrier = new ArchiveCourrier();

        query = "select * from courrier_depart,expediteur where  FOLDERID in ("
                + ids.toString().replace("]", "'").replace("[", "'").replace(",", "','").replace(" ", "")
                + ") and expediteur.ID_EXPEDITEUR = courrier_depart.ID_EXPEDITEUR_ACAPS";

        List<String> preparedValue = new ArrayList<String>();

        if (Objects.nonNull(searchOptions.getStatut())) {
            if (!searchOptions.getStatut().equals("")) {
                query += " and STATUS = ? ";
                preparedValue.add(searchOptions.getStatut().replace("'", "''"));
            }

        }
        if (Objects.nonNull(searchOptions.getReference())) {
            if (!searchOptions.getReference().equals("")) {
                query += " and REF_BO_COURRIER_DEPART = ? ";
                preparedValue.add(searchOptions.getReference().replace("'", "''"));
            }
        }
        if (Objects.nonNull(searchOptions.getObjet())) {
            if (!searchOptions.getObjet().equals("")) {
                query += " and OBJET like ? ";
                preparedValue.add("%" + searchOptions.getObjet().replace("'", "''") + "%");
            }
        }
        if (Objects.nonNull(searchOptions.getDest())) {
            if (!searchOptions.getDest().equals("")) {
                query += " and ID_DESTINATAIRE = ? ";
                preparedValue.add(searchOptions.getDest().replace("'", "''"));
            }
        }
        if (Objects.nonNull(searchOptions.getEmetteur())) {
            if (!searchOptions.getEmetteur().equals("")) {
                query += " and ID_EXPEDITEUR_ACAPS = ? ";
                preparedValue.add(searchOptions.getEmetteur().replace("'", "''"));
            }
        }

        if (Objects.nonNull(searchOptions.getDateFin())) {
            if (!searchOptions.getDateFin().equals("")) {

                query += " and DATE_ENREGISTREMENT between ? and ?";
                if (Objects.nonNull(searchOptions.getDateDebut())) {
                    if (!searchOptions.getDateDebut().equals("")) {
                        preparedValue.add(searchOptions.getDateDebut().replace("'", "''"));
                    }
                } else {
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String strDate = formatter.format(date);
                    preparedValue.add(strDate.replace("'", "''"));
                }

                preparedValue.add(searchOptions.getDateFin().replace("'", "''"));

            }
        }

        PreparedStatement pe;
        try {

            pe = conn.prepareStatement(query);

            int i = 1;
            for (String value : preparedValue) {
                pe.setString(i, value);
                i++;
            }

            ResultSet result = pe.executeQuery();
            while (result.next()) {
                courrier.setId(result.getString("ID_COURRIER_DEPART"));
                courrier.setType("Courrier Dèpart");
                courrier.setObjet(result.getString("OBJET"));
                courrier.setDate(result.getString("DATE_ENREGISTREMENT"));
                courrier.setDestinateur(result.getString("ID_DESTINATAIRE"));
                courrier.setRef(result.getString("REF_BO_COURRIER_DEPART"));
                courrier.setEmetteur(result.getString("EXPEDITEUR"));
                courrier.setStatus(result.getString("STATUS"));
                courrier.setFolderId(result.getString("FOLDERID"));
                archiveList.add(courrier);
                courrier = new ArchiveCourrier();
            }

            if (Objects.nonNull(searchOptions.getOrder())) {
                String[] order = searchOptions.getOrder().split(",");
                if (Objects.nonNull(order)) {
                    if (order[0].equals("date")) {
                        if (order[1].equals("desc")) {
                            archiveList.sort(Comparator.comparing(ArchiveCourrier::getDate).reversed());
                        } else if (order[1].equals("asc")) {
                            archiveList.sort(Comparator.comparing(ArchiveCourrier::getDate));
                        }
                    }
                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int start = (int) pageable.getOffset();
        int end = (int) (start + pageable.getPageSize()) > archiveList.size() ? archiveList.size()
                : (start + pageable.getPageSize());

        Page<ArchiveCourrier> pages = new PageImpl<ArchiveCourrier>(archiveList.subList(start, end), pageable,
                archiveList.size());
        return pages;

    }

    public Page<ArchiveCourrier> getSearchArrive(ArchiveClass searchOptions, Pageable pageable, List<String> ids,
            Connection conn) {

        List<ArchiveCourrier> archiveList = new ArrayList<ArchiveCourrier>();
        String query = "";
        ArchiveCourrier courrier = new ArchiveCourrier();

        query = "select * from courrier_arrive,expediteur where  FOLDER_ID in ("
                + ids.toString().replace("]", "'").replace("[", "'").replace(",", "','").replace(" ", "")
                + ") and expediteur.ID_EXPEDITEUR = courrier_arrive.ID_EXPEDITEUR";

        List<String> preparedValue = new ArrayList<String>();

        if (Objects.nonNull(searchOptions.getStatut())) {
            if (!searchOptions.getStatut().equals("")) {
                query += " and STATUS = ? ";
                preparedValue.add(searchOptions.getStatut().replace("'", "''"));
            }

        }
        if (Objects.nonNull(searchOptions.getReference())) {
            if (!searchOptions.getReference().equals("")) {
                query += " and REF_BO_COURRIER_ARRIVE = ? ";
                preparedValue.add(searchOptions.getReference().replace("'", "''"));
            }
        }
        if (Objects.nonNull(searchOptions.getObjet())) {
            if (!searchOptions.getObjet().equals("")) {
                query += " and OBJET_COURRIER like ? ";
                preparedValue.add("%" + searchOptions.getObjet().replace("'", "''") + "%");
            }
        }
        if (Objects.nonNull(searchOptions.getDest())) {
            if (!searchOptions.getDest().equals("")) {
                query += " and ID_DESTINATAIRE_ACAPS = ? ";
                preparedValue.add(searchOptions.getDest().replace("'", "''"));
            }
        }
        if (Objects.nonNull(searchOptions.getEmetteur())) {
            if (!searchOptions.getEmetteur().equals("")) {
                query += " and ID_EXPEDITEUR = ? ";
                preparedValue.add(searchOptions.getEmetteur().replace("'", "''"));
            }
        }

        if (Objects.nonNull(searchOptions.getDateFin())) {
            if (!searchOptions.getDateFin().equals("")) {

                query += " and DATE_ENREGISTREMENT between ? and ?";
                if (Objects.nonNull(searchOptions.getDateDebut())) {
                    if (!searchOptions.getDateDebut().equals("")) {
                        preparedValue.add(searchOptions.getDateDebut().replace("'", "''"));
                    }
                } else {
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String strDate = formatter.format(date);
                    preparedValue.add(strDate.replace("'", "''"));
                }

                preparedValue.add(searchOptions.getDateFin().replace("'", "''"));

            }
        }

        try {
            PreparedStatement pe = conn.prepareStatement(query);
            int i = 1;
            for (String value : preparedValue) {
                pe.setString(i, value);
                i++;
            }

            ResultSet result = pe.executeQuery();
            while (result.next()) {

                courrier.setId(result.getString("ID_COURRIER_ARRIVE"));
                courrier.setType("Courrier Arrivé");
                courrier.setObjet(result.getString("OBJET_COURRIER"));
                courrier.setDate(result.getString("DATE_ENREGISTREMENT"));
                courrier.setDestinateur(result.getString("ID_DESTINATAIRE_ACAPS"));
                courrier.setRef(result.getString("REF_BO_COURRIER_ARRIVE"));
                courrier.setStatus(result.getString("STATUS"));
                courrier.setFolderId(result.getString("FOLDER_ID"));
                courrier.setEmetteur(result.getString("EXPEDITEUR"));
                archiveList.add(courrier);
                courrier = new ArchiveCourrier();
            }

            if (Objects.nonNull(searchOptions.getOrder())) {
                String[] order = searchOptions.getOrder().split(",");
                if (Objects.nonNull(order)) {
                    if (order[0].equals("date")) {
                        if (order[1].equals("desc")) {
                            archiveList.sort(Comparator.comparing(ArchiveCourrier::getDate).reversed());
                        } else if (order[1].equals("asc")) {
                            archiveList.sort(Comparator.comparing(ArchiveCourrier::getDate));
                        }
                    }
                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int start = (int) pageable.getOffset();
        int end = (int) (start + pageable.getPageSize()) > archiveList.size() ? archiveList.size()
                : (start + pageable.getPageSize());
        Page<ArchiveCourrier> pages = new PageImpl<ArchiveCourrier>(archiveList.subList(start, end), pageable,
                archiveList.size());
        return pages;
    }

    public Page<ArchiveCourrier> getSearchInterne(ArchiveClass searchOptions, Pageable pageable, List<String> ids,
            Connection conn) {

        List<ArchiveCourrier> archiveList = new ArrayList<ArchiveCourrier>();
        String query = "";
        ArchiveCourrier courrier = new ArchiveCourrier();

        query = "select * from courrier_interne,expediteur where  FOLDERID in ("
                + ids.toString().replace("]", "'").replace("[", "'").replace(",", "','").replace(" ", "")
                + ") and expediteur.ID_EXPEDITEUR = courrier_interne.ID_EXPEDITEUR";

        List<String> preparedValue = new ArrayList<String>();

        if (Objects.nonNull(searchOptions.getStatut())) {
            if (!searchOptions.getStatut().equals("")) {
                query += " and STATUS = ? ";
                preparedValue.add(searchOptions.getStatut().replace("'", "''"));
            }

        }
        if (Objects.nonNull(searchOptions.getReference())) {
            if (!searchOptions.getReference().equals("")) {
                query += " and REFERENCE = ? ";
                preparedValue.add(searchOptions.getReference().replace("'", "''"));
            }
        }
        if (Objects.nonNull(searchOptions.getObjet())) {
            if (!searchOptions.getObjet().equals("")) {
                query += " and OBJET like ? ";
                preparedValue.add("%" + searchOptions.getObjet().replace("'", "''") + "%");
            }
        }
        if (Objects.nonNull(searchOptions.getDest())) {
            if (!searchOptions.getDest().equals("")) {
                query += " and ID_DESTINATAIRE = ? ";
                preparedValue.add(searchOptions.getDest().replace("'", "''"));
            }
        }
        if (Objects.nonNull(searchOptions.getEmetteur())) {
            if (!searchOptions.getEmetteur().equals("")) {
                query += " and ID_EXPEDITEUR = ? ";
                preparedValue.add(searchOptions.getEmetteur().replace("'", "''"));
            }
        }

        if (Objects.nonNull(searchOptions.getDateFin())) {
            if (!searchOptions.getDateFin().equals("")) {

                query += " and DATE_ENREGISTREMENT between ? and ?";
                if (Objects.nonNull(searchOptions.getDateDebut())) {
                    if (!searchOptions.getDateDebut().equals("")) {
                        preparedValue.add(searchOptions.getDateDebut().replace("'", "''"));
                    }
                } else {
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String strDate = formatter.format(date);
                    preparedValue.add(strDate.replace("'", "''"));
                }

                preparedValue.add(searchOptions.getDateFin().replace("'", "''"));

            }
        }

        PreparedStatement pe;
        try {
            pe = conn.prepareStatement(query);

            int i = 1;
            for (String value : preparedValue) {
                pe.setString(i, value);
                i++;
            }

            ResultSet result = pe.executeQuery();
            while (result.next()) {
                courrier.setId(result.getString("ID_COURRIER_INTERNE"));
                courrier.setType("Courrier Interne");
                courrier.setObjet(result.getString("OBJET"));
                courrier.setDate(result.getString("DATE_ENREGISTREMENT"));
                courrier.setDestinateur(result.getString("ID_DESTINATAIRE"));
                courrier.setRef(result.getString("REFERENCE"));
                courrier.setEmetteur(result.getString("EXPEDITEUR"));
                courrier.setStatus(result.getString("STATUS"));
                courrier.setFolderId(result.getString("FOLDERID"));
                archiveList.add(courrier);
                courrier = new ArchiveCourrier();
            }

            if (Objects.nonNull(searchOptions.getOrder())) {
                String[] order = searchOptions.getOrder().split(",");
                if (Objects.nonNull(order)) {
                    if (order[0].equals("date")) {
                        if (order[1].equals("desc")) {
                            archiveList.sort(Comparator.comparing(ArchiveCourrier::getDate).reversed());
                        } else if (order[1].equals("asc")) {
                            archiveList.sort(Comparator.comparing(ArchiveCourrier::getDate));
                        }
                    }
                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int start = (int) pageable.getOffset();
        int end = (int) (start + pageable.getPageSize()) > archiveList.size() ? archiveList.size()
                : (start + pageable.getPageSize());
        Page<ArchiveCourrier> pages = new PageImpl<ArchiveCourrier>(archiveList.subList(start, end), pageable,
                archiveList.size());
        return pages;

    }

    public Page<ArchiveCourrier> getSearchExamn(ArchiveClass searchOptions, Pageable pageable, List<String> ids,
            Connection conn) {

        List<ArchiveCourrier> archiveList = new ArrayList<ArchiveCourrier>();
        String query = "";
        ArchiveCourrier courrier = new ArchiveCourrier();

        query = "select * from examen where  FOLDER_ID in ("
                + ids.toString().replace("]", "'").replace("[", "'").replace(",", "','").replace(" ", "") + ")";

        List<String> preparedValue = new ArrayList<String>();

        if (Objects.nonNull(searchOptions.getStatut())) {
            if (!searchOptions.getStatut().equals("")) {
                query += " and STATUS = ? ";
                preparedValue.add(searchOptions.getStatut().replace("'", "''"));
            }

        }
        if (Objects.nonNull(searchOptions.getReference())) {
            if (!searchOptions.getReference().equals("")) {
                query += " and REFERENCE_BO = ? ";
                preparedValue.add(searchOptions.getReference().replace("'", "''"));
            }
        }
        if (Objects.nonNull(searchOptions.getObjet())) {
            if (!searchOptions.getObjet().equals("")) {
                query += " and OBJET like ? ";
                preparedValue.add("%" + searchOptions.getObjet().replace("'", "''") + "%");
            }
        }

        if (Objects.nonNull(searchOptions.getDateFin())) {
            if (!searchOptions.getDateFin().equals("")) {

                query += " and DATE_ENREGISTREMENT between ? and ?";
                if (Objects.nonNull(searchOptions.getDateDebut())) {
                    if (!searchOptions.getDateDebut().equals("")) {
                        preparedValue.add(searchOptions.getDateDebut().replace("'", "''"));
                    }
                } else {
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String strDate = formatter.format(date);
                    preparedValue.add(strDate.replace("'", "''"));
                }

                preparedValue.add(searchOptions.getDateFin().replace("'", "''"));

            }
        }

        PreparedStatement pe;
        try {
            pe = conn.prepareStatement(query);

            int i = 1;
            for (String value : preparedValue) {
                pe.setString(i, value);
                i++;
            }

            ResultSet result = pe.executeQuery();
            while (result.next()) {
                courrier.setId(result.getString("ID_EXAMEN"));
                courrier.setType("Examen");
                courrier.setObjet(result.getString("OBJET"));
                courrier.setDate(result.getString("DATE_ENREGISTREMENT"));
                courrier.setRef(result.getString("REFERENCE_BO"));
                courrier.setStatus(result.getString("STATUS"));
                courrier.setFolderId(result.getString("FOLDER_ID"));
                courrier.setEmetteur("");
                archiveList.add(courrier);
                courrier = new ArchiveCourrier();
            }

            if (Objects.nonNull(searchOptions.getOrder())) {
                String[] order = searchOptions.getOrder().split(",");
                if (Objects.nonNull(order)) {
                    if (order[0].equals("date")) {
                        if (order[1].equals("desc")) {
                            archiveList.sort(Comparator.comparing(ArchiveCourrier::getDate).reversed());
                        } else if (order[1].equals("asc")) {
                            archiveList.sort(Comparator.comparing(ArchiveCourrier::getDate));
                        }
                    }
                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int start = (int) pageable.getOffset();
        int end = (int) (start + pageable.getPageSize()) > archiveList.size() ? archiveList.size()
                : (start + pageable.getPageSize());
        Page<ArchiveCourrier> pages = new PageImpl<ArchiveCourrier>(archiveList.subList(start, end), pageable,
                archiveList.size());
        return pages;
    }

    public Page<ArchiveCourrier> getSearchPiece(ArchiveClass searchOptions, Pageable pageable, List<String> ids,
            Connection conn) {

        List<ArchiveCourrier> archiveList = new ArrayList<ArchiveCourrier>();
        String query = "";
        ArchiveCourrier courrier = new ArchiveCourrier();

        query = "select * from piece_justificatives where  FOLDER_ID in ("
                + ids.toString().replace("]", "'").replace("[", "'").replace(",", "','").replace(" ", "") + ")";

        List<String> preparedValue = new ArrayList<String>();

        if (Objects.nonNull(searchOptions.getStatut())) {
            if (!searchOptions.getStatut().equals("")) {
                query += " and STATUS = ? ";
                preparedValue.add(searchOptions.getStatut().replace("'", "''"));
            }

        }
        if (Objects.nonNull(searchOptions.getReference())) {
            if (!searchOptions.getReference().equals("")) {
                query += " and REFERENCE_BO = ? ";
                preparedValue.add(searchOptions.getReference().replace("'", "''"));
            }
        }
        if (Objects.nonNull(searchOptions.getObjet())) {
            if (!searchOptions.getObjet().equals("")) {
                query += " and OBJET like ? ";
                preparedValue.add("%" + searchOptions.getObjet().replace("'", "''") + "%");
            }
        }

        if (Objects.nonNull(searchOptions.getDateFin())) {
            if (!searchOptions.getDateFin().equals("")) {

                query += " and DATE_ENREGISTREMENT between ? and ?";
                if (Objects.nonNull(searchOptions.getDateDebut())) {
                    if (!searchOptions.getDateDebut().equals("")) {
                        preparedValue.add(searchOptions.getDateDebut().replace("'", "''"));
                    }
                } else {
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String strDate = formatter.format(date);
                    preparedValue.add(strDate.replace("'", "''"));
                }

                preparedValue.add(searchOptions.getDateFin().replace("'", "''"));

            }
        }

        PreparedStatement pe;
        try {
            pe = conn.prepareStatement(query);

            int i = 1;
            for (String value : preparedValue) {
                pe.setString(i, value);
                i++;
            }

            ResultSet result = pe.executeQuery();
            while (result.next()) {
                courrier.setId(result.getString("ID_PIECE_JUSTIFICATIVES"));
                courrier.setType("Piece Justificatives");
                courrier.setObjet(result.getString("OBJET"));
                courrier.setDate(result.getString("DATE_ENREGISTREMENT"));
                courrier.setRef(result.getString("REFERENCE_BO"));
                courrier.setStatus(result.getString("STATUS"));
                courrier.setFolderId(result.getString("FOLDER_ID"));
                archiveList.add(courrier);
                courrier = new ArchiveCourrier();
            }

            if (Objects.nonNull(searchOptions.getOrder())) {
                String[] order = searchOptions.getOrder().split(",");
                if (Objects.nonNull(order)) {
                    if (order[0].equals("date")) {
                        if (order[1].equals("desc")) {
                            archiveList.sort(Comparator.comparing(ArchiveCourrier::getDate).reversed());
                        } else if (order[1].equals("asc")) {
                            archiveList.sort(Comparator.comparing(ArchiveCourrier::getDate));
                        }
                    }
                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int start = (int) pageable.getOffset();
        int end = (int) (start + pageable.getPageSize()) > archiveList.size() ? archiveList.size()
                : (start + pageable.getPageSize());
        Page<ArchiveCourrier> pages = new PageImpl<ArchiveCourrier>(archiveList.subList(start, end), pageable,
                archiveList.size());
        return pages;

    }

    public List<ArchiveCourrier> getSearchDepartList(ArchiveClass searchOptions, Pageable pageable, List<String> ids,
            Connection conn) {
        List<ArchiveCourrier> archiveList = new ArrayList<ArchiveCourrier>();
        String query = "";
        ArchiveCourrier courrier = new ArchiveCourrier();

        query = "select * from courrier_depart,expediteur where  FOLDERID in ("
                + ids.toString().replace("]", "'").replace("[", "'").replace(",", "','").replace(" ", "")
                + ") and expediteur.ID_EXPEDITEUR = courrier_depart.ID_EXPEDITEUR_ACAPS";

        List<String> preparedValue = new ArrayList<String>();

        if (Objects.nonNull(searchOptions.getStatut())) {
            if (!searchOptions.getStatut().equals("")) {
                query += " and STATUS = ? ";
                preparedValue.add(searchOptions.getStatut().replace("'", "''"));
            }

        }
        if (Objects.nonNull(searchOptions.getReference())) {
            if (!searchOptions.getReference().equals("")) {
                query += " and REF_BO_COURRIER_DEPART = ? ";
                preparedValue.add(searchOptions.getReference().replace("'", "''"));
            }
        }
        if (Objects.nonNull(searchOptions.getObjet())) {
            if (!searchOptions.getObjet().equals("")) {
                query += " and OBJET like ? ";
                preparedValue.add("%" + searchOptions.getObjet().replace("'", "''") + "%");
            }
        }
        if (Objects.nonNull(searchOptions.getDest())) {
            if (!searchOptions.getDest().equals("")) {
                query += " and ID_DESTINATAIRE = ? ";
                preparedValue.add(searchOptions.getDest().replace("'", "''"));
            }
        }
        if (Objects.nonNull(searchOptions.getEmetteur())) {
            if (!searchOptions.getEmetteur().equals("")) {
                query += " and ID_EXPEDITEUR_ACAPS = ? ";
                preparedValue.add(searchOptions.getEmetteur().replace("'", "''"));
            }
        }

        if (Objects.nonNull(searchOptions.getDateFin())) {
            if (!searchOptions.getDateFin().equals("")) {

                query += " and DATE_ENREGISTREMENT between ? and ?";
                if (Objects.nonNull(searchOptions.getDateDebut())) {
                    if (!searchOptions.getDateDebut().equals("")) {
                        preparedValue.add(searchOptions.getDateDebut().replace("'", "''"));
                    }
                } else {
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String strDate = formatter.format(date);
                    preparedValue.add(strDate.replace("'", "''"));
                }

                preparedValue.add(searchOptions.getDateFin().replace("'", "''"));

            }
        }

        PreparedStatement pe;
        try {
            pe = conn.prepareStatement(query);

            int i = 1;
            for (String value : preparedValue) {
                pe.setString(i, value);
                i++;
            }

            ResultSet result = pe.executeQuery();
            while (result.next()) {
                courrier.setId(result.getString("ID_COURRIER_DEPART"));
                courrier.setType("Courrier Dèpart");
                courrier.setObjet(result.getString("OBJET"));
                courrier.setDate(result.getString("DATE_ENREGISTREMENT"));
                courrier.setDestinateur(result.getString("ID_DESTINATAIRE"));
                courrier.setRef(result.getString("REF_BO_COURRIER_DEPART"));
                courrier.setEmetteur(result.getString("EXPEDITEUR"));
                courrier.setStatus(result.getString("STATUS"));
                courrier.setFolderId(result.getString("FOLDERID"));
                archiveList.add(courrier);
                courrier = new ArchiveCourrier();
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return archiveList;

    }

    public List<ArchiveCourrier> getSearchArriveList(ArchiveClass searchOptions, Pageable pageable, List<String> ids,
            Connection conn) {

        List<ArchiveCourrier> archiveList = new ArrayList<ArchiveCourrier>();
        String query = "";
        ArchiveCourrier courrier = new ArchiveCourrier();

        query = "select * from courrier_arrive,expediteur where  FOLDER_ID in ("
                + ids.toString().replace("]", "'").replace("[", "'").replace(",", "','").replace(" ", "")
                + ") and expediteur.ID_EXPEDITEUR = courrier_arrive.ID_EXPEDITEUR";

        List<String> preparedValue = new ArrayList<String>();

        if (Objects.nonNull(searchOptions.getStatut())) {
            if (!searchOptions.getStatut().equals("")) {
                query += " and STATUS = ? ";
                preparedValue.add(searchOptions.getStatut().replace("'", "''"));
            }

        }
        if (Objects.nonNull(searchOptions.getReference())) {
            if (!searchOptions.getReference().equals("")) {
                query += " and REF_BO_COURRIER_ARRIVE = ? ";
                preparedValue.add(searchOptions.getReference().replace("'", "''"));
            }
        }
        if (Objects.nonNull(searchOptions.getObjet())) {
            if (!searchOptions.getObjet().equals("")) {
                query += " and OBJET_COURRIER like ? ";
                preparedValue.add("%" + searchOptions.getObjet().replace("'", "''") + "%");
            }
        }
        if (Objects.nonNull(searchOptions.getDest())) {
            if (!searchOptions.getDest().equals("")) {
                query += " and ID_DESTINATAIRE_ACAPS = ? ";
                preparedValue.add(searchOptions.getDest().replace("'", "''"));
            }
        }
        if (Objects.nonNull(searchOptions.getEmetteur())) {
            if (!searchOptions.getEmetteur().equals("")) {
                query += " and ID_EXPEDITEUR = ? ";
                preparedValue.add(searchOptions.getEmetteur().replace("'", "''"));
            }
        }

        if (Objects.nonNull(searchOptions.getDateFin())) {
            if (!searchOptions.getDateFin().equals("")) {

                query += " and DATE_ENREGISTREMENT between ? and ?";
                if (Objects.nonNull(searchOptions.getDateDebut())) {
                    if (!searchOptions.getDateDebut().equals("")) {
                        preparedValue.add(searchOptions.getDateDebut().replace("'", "''"));
                    }
                } else {
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String strDate = formatter.format(date);
                    preparedValue.add(strDate.replace("'", "''"));
                }

                preparedValue.add(searchOptions.getDateFin().replace("'", "''"));

            }
        }

        try {
            PreparedStatement pe = conn.prepareStatement(query);
            int i = 1;
            for (String value : preparedValue) {
                pe.setString(i, value);
                i++;
            }

            ResultSet result = pe.executeQuery();
            while (result.next()) {
                courrier.setId(result.getString("ID_COURRIER_ARRIVE"));
                courrier.setType("Courrier Arrivé");
                courrier.setObjet(result.getString("OBJET_COURRIER"));
                courrier.setDate(result.getString("DATE_ENREGISTREMENT"));
                courrier.setDestinateur(result.getString("ID_DESTINATAIRE_ACAPS"));
                courrier.setRef(result.getString("REF_BO_COURRIER_ARRIVE"));
                courrier.setEmetteur(result.getString("EXPEDITEUR"));
                courrier.setStatus(result.getString("STATUS"));
                courrier.setFolderId(result.getString("FOLDER_ID"));
                archiveList.add(courrier);
                courrier = new ArchiveCourrier();
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return archiveList;
    }

    public List<ArchiveCourrier> getSearchInterneList(ArchiveClass searchOptions, Pageable pageable, List<String> ids,
            Connection conn) {

        List<ArchiveCourrier> archiveList = new ArrayList<ArchiveCourrier>();
        String query = "";
        ArchiveCourrier courrier = new ArchiveCourrier();

        query = "select * from courrier_interne,expediteur where  FOLDERID in ("
                + ids.toString().replace("]", "'").replace("[", "'").replace(",", "','").replace(" ", "")
                + ") and expediteur.ID_EXPEDITEUR = courrier_interne.ID_EXPEDITEUR";

        List<String> preparedValue = new ArrayList<String>();

        if (Objects.nonNull(searchOptions.getStatut())) {
            if (!searchOptions.getStatut().equals("")) {
                query += " and STATUS = ? ";
                preparedValue.add(searchOptions.getStatut().replace("'", "''"));
            }

        }
        if (Objects.nonNull(searchOptions.getReference())) {
            if (!searchOptions.getReference().equals("")) {
                query += " and REFERENCE = ? ";
                preparedValue.add(searchOptions.getReference().replace("'", "''"));
            }
        }
        if (Objects.nonNull(searchOptions.getObjet())) {
            if (!searchOptions.getObjet().equals("")) {
                query += " and OBJET like ? ";
                preparedValue.add("%" + searchOptions.getObjet().replace("'", "''") + "%");
            }
        }
        if (Objects.nonNull(searchOptions.getDest())) {
            if (!searchOptions.getDest().equals("")) {
                query += " and ID_DESTINATAIRE = ? ";
                preparedValue.add(searchOptions.getDest().replace("'", "''"));
            }
        }
        if (Objects.nonNull(searchOptions.getEmetteur())) {
            if (!searchOptions.getEmetteur().equals("")) {
                query += " and ID_EXPEDITEUR = ? ";
                preparedValue.add(searchOptions.getEmetteur().replace("'", "''"));
            }
        }

        if (Objects.nonNull(searchOptions.getDateFin())) {
            if (!searchOptions.getDateFin().equals("")) {

                query += " and DATE_ENREGISTREMENT between ? and ?";
                if (Objects.nonNull(searchOptions.getDateDebut())) {
                    if (!searchOptions.getDateDebut().equals("")) {
                        preparedValue.add(searchOptions.getDateDebut().replace("'", "''"));
                    }
                } else {
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String strDate = formatter.format(date);
                    preparedValue.add(strDate.replace("'", "''"));
                }

                preparedValue.add(searchOptions.getDateFin().replace("'", "''"));

            }
        }

        PreparedStatement pe;
        try {
            pe = conn.prepareStatement(query);

            int i = 1;
            for (String value : preparedValue) {
                pe.setString(i, value);
                i++;
            }

            ResultSet result = pe.executeQuery();
            while (result.next()) {
                courrier.setId(result.getString("ID_COURRIER_INTERNE"));
                courrier.setType("Courrier Interne");
                courrier.setObjet(result.getString("OBJET"));
                courrier.setDate(result.getString("DATE_ENREGISTREMENT"));
                courrier.setDestinateur(result.getString("ID_DESTINATAIRE"));
                courrier.setRef(result.getString("REFERENCE"));
                courrier.setEmetteur(result.getString("EXPEDITEUR"));
                courrier.setStatus(result.getString("STATUS"));
                courrier.setFolderId(result.getString("FOLDERID"));
                archiveList.add(courrier);
                courrier = new ArchiveCourrier();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return archiveList;

    }

    public List<ArchiveCourrier> getSearchExamnList(ArchiveClass searchOptions, Pageable pageable, List<String> ids,
            Connection conn) {

        List<ArchiveCourrier> archiveList = new ArrayList<ArchiveCourrier>();
        String query = "";
        ArchiveCourrier courrier = new ArchiveCourrier();

        query = "select * from examen where  FOLDER_ID in ("
                + ids.toString().replace("]", "'").replace("[", "'").replace(",", "','").replace(" ", "") + ")";

        List<String> preparedValue = new ArrayList<String>();

        if (Objects.nonNull(searchOptions.getStatut())) {
            if (!searchOptions.getStatut().equals("")) {
                query += " and STATUS = ? ";
                preparedValue.add(searchOptions.getStatut().replace("'", "''"));
            }

        }
        if (Objects.nonNull(searchOptions.getReference())) {
            if (!searchOptions.getReference().equals("")) {
                query += " and REFERENCE_BO = ? ";
                preparedValue.add(searchOptions.getReference().replace("'", "''"));
            }
        }
        if (Objects.nonNull(searchOptions.getObjet())) {
            if (!searchOptions.getObjet().equals("")) {
                query += " and OBJET like ? ";
                preparedValue.add("%" + searchOptions.getObjet().replace("'", "''") + "%");
            }
        }

        if (Objects.nonNull(searchOptions.getDateFin())) {
            if (!searchOptions.getDateFin().equals("")) {

                query += " and DATE_ENREGISTREMENT between ? and ?";
                if (Objects.nonNull(searchOptions.getDateDebut())) {
                    if (!searchOptions.getDateDebut().equals("")) {
                        preparedValue.add(searchOptions.getDateDebut().replace("'", "''"));
                    }
                } else {
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String strDate = formatter.format(date);
                    preparedValue.add(strDate.replace("'", "''"));
                }

                preparedValue.add(searchOptions.getDateFin().replace("'", "''"));

            }
        }

        PreparedStatement pe;
        try {
            pe = conn.prepareStatement(query);

            int i = 1;
            for (String value : preparedValue) {
                pe.setString(i, value);
                i++;
            }

            ResultSet result = pe.executeQuery();
            while (result.next()) {
                courrier.setId(result.getString("ID_EXAMEN"));
                courrier.setType("Examen");
                courrier.setObjet(result.getString("OBJET"));
                courrier.setDate(result.getString("DATE_ENREGISTREMENT"));
                courrier.setRef(result.getString("REFERENCE_BO"));
                courrier.setStatus(result.getString("STATUS"));
                courrier.setFolderId(result.getString("FOLDER_ID"));
                archiveList.add(courrier);
                courrier = new ArchiveCourrier();
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return archiveList;
    }

    public List<ArchiveCourrier> getSearchPieceList(ArchiveClass searchOptions, Pageable pageable, List<String> ids,
            Connection conn) {

        List<ArchiveCourrier> archiveList = new ArrayList<ArchiveCourrier>();
        String query = "";
        ArchiveCourrier courrier = new ArchiveCourrier();

        query = "select * from piece_justificatives where  FOLDER_ID in ("
                + ids.toString().replace("]", "'").replace("[", "'").replace(",", "','").replace(" ", "") + ")";

        List<String> preparedValue = new ArrayList<String>();

        if (Objects.nonNull(searchOptions.getStatut())) {
            if (!searchOptions.getStatut().equals("")) {
                query += " and STATUS = ? ";
                preparedValue.add(searchOptions.getStatut().replace("'", "''"));
            }

        }
        if (Objects.nonNull(searchOptions.getReference())) {
            if (!searchOptions.getReference().equals("")) {
                query += " and REFERENCE_BO = ? ";
                preparedValue.add(searchOptions.getReference().replace("'", "''"));
            }
        }
        if (Objects.nonNull(searchOptions.getObjet())) {
            if (!searchOptions.getObjet().equals("")) {
                query += " and OBJET like ? ";
                preparedValue.add("%" + searchOptions.getObjet().replace("'", "''") + "%");
            }
        }

        if (Objects.nonNull(searchOptions.getDateFin())) {
            if (!searchOptions.getDateFin().equals("")) {

                query += " and DATE_ENREGISTREMENT between ? and ?";
                if (Objects.nonNull(searchOptions.getDateDebut())) {
                    if (!searchOptions.getDateDebut().equals("")) {
                        preparedValue.add(searchOptions.getDateDebut().replace("'", "''"));
                    }
                } else {
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String strDate = formatter.format(date);
                    preparedValue.add(strDate.replace("'", "''"));
                }

                preparedValue.add(searchOptions.getDateFin().replace("'", "''"));

            }
        }

        PreparedStatement pe;
        try {
            pe = conn.prepareStatement(query);

            int i = 1;
            for (String value : preparedValue) {
                pe.setString(i, value);
                i++;
            }

            ResultSet result = pe.executeQuery();
            while (result.next()) {
                courrier.setId(result.getString("ID_PIECE_JUSTIFICATIVES"));
                courrier.setType("Piece Justificatives");
                courrier.setObjet(result.getString("OBJET"));
                courrier.setDate(result.getString("DATE_ENREGISTREMENT"));
                courrier.setRef(result.getString("REFERENCE_BO"));
                courrier.setStatus(result.getString("STATUS"));
                courrier.setFolderId(result.getString("FOLDER_ID"));
                archiveList.add(courrier);
                courrier = new ArchiveCourrier();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return archiveList;

    }

    public List<Sender> getSenders() {
        
        List<Sender> senders = new ArrayList<Sender>();

        Sender sender = new Sender();

        try {
 
            Connection conn = DriverManager.getConnection(dbName, user, pw);

            String sql = "SELECT * FROM expediteur";

            PreparedStatement pee = conn.prepareStatement(sql);

            ResultSet result1 = pee.executeQuery();

            while (result1.next()) {

                sender.setName(result1.getString("EXPEDITEUR"));

                sender.setId(result1.getLong("ID_EXPEDITEUR"));
                
                senders.add(sender);

                sender = new Sender();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return senders;
    }

    public Page<ArchiveCourrier> searchCourrier(ArchiveClass searchOptions, Pageable pageable) {

        try {

            if (connectedUser().getArchiviste() == 0) {
                List<User> users = userRepo.findByParent(connectedUser().getUserId());

                Connection conn = DriverManager.getConnection(dbName, user, pw);

                List<String> ids = new ArrayList<String>();

                String sql = "SELECT DISTINCT FOLDER_ID FROM journal jr,user u where u.ID_USER = jr.ID_USER and  u.name = ?";

                PreparedStatement pee = conn.prepareStatement(sql);

                pee.setString(1, connectedUser().getUsername());

                ResultSet result1 = pee.executeQuery();
                while (result1.next()) {
                    ids.add(result1.getString("FOLDER_ID"));
                }

                for (User user : users) {

                    PreparedStatement peee = conn.prepareStatement(sql);

                    peee.setString(1, user.getUsername());

                    ResultSet result2 = peee.executeQuery();
                    while (result2.next()) {
                        ids.add(result2.getString("FOLDER_ID"));
                    }
                }

                if (searchOptions.getType().equals("all")) {

                    String[] all = { "depart", "arrive", "interne", "examen", "piece" };

                    List<ArchiveCourrier> archiveList = new ArrayList<ArchiveCourrier>();

                    List<ArchiveCourrier> archiveListArrive = new ArrayList<ArchiveCourrier>();
                    for (String type : all) {

                        if (type.equals("arrive")) {

                            archiveListArrive = getSearchArriveList(searchOptions, pageable, ids, conn);

                            for (ArchiveCourrier archiveCourrier : archiveListArrive) {
                                archiveList.add(archiveCourrier);
                            }

                            archiveListArrive = new ArrayList<ArchiveCourrier>();

                        }
                        if (type.equals("depart")) {

                            archiveListArrive = getSearchDepartList(searchOptions, pageable, ids, conn);

                            for (ArchiveCourrier archiveCourrier : archiveListArrive) {
                                archiveList.add(archiveCourrier);
                            }

                            archiveListArrive = new ArrayList<ArchiveCourrier>();

                        } else if (type.equals("interne")) {

                            archiveListArrive = getSearchInterneList(searchOptions, pageable, ids, conn);

                            for (ArchiveCourrier archiveCourrier : archiveListArrive) {
                                archiveList.add(archiveCourrier);
                            }

                            archiveListArrive = new ArrayList<ArchiveCourrier>();

                        } else if (type.equals("examen")) {

                            archiveListArrive = getSearchExamnList(searchOptions, pageable, ids, conn);

                            for (ArchiveCourrier archiveCourrier : archiveListArrive) {
                                archiveList.add(archiveCourrier);
                            }

                            archiveListArrive = new ArrayList<ArchiveCourrier>();

                        } else if (type.equals("piece")) {

                            archiveListArrive = getSearchPieceList(searchOptions, pageable, ids, conn);

                            for (ArchiveCourrier archiveCourrier : archiveListArrive) {
                                archiveList.add(archiveCourrier);
                            }

                            archiveListArrive = new ArrayList<ArchiveCourrier>();

                        }
                    }

                    String[] order = searchOptions.getOrder().split(",");
                    if (Objects.nonNull(order)) {
                        if (order[0].equals("date")) {
                            if (order[1].equals("desc")) {
                                archiveList.sort(Comparator.comparing(ArchiveCourrier::getDate).reversed());
                            } else if (order[1].equals("asc")) {
                                archiveList.sort(Comparator.comparing(ArchiveCourrier::getDate));
                            }
                        }
                    }

                    int start = (int) pageable.getOffset();
                    int end = (int) (start + pageable.getPageSize()) > archiveList.size() ? archiveList.size()
                            : (start + pageable.getPageSize());

                    System.out.println("Start : " + start);
                    System.out.println("End : " + end);

                    Page<ArchiveCourrier> pages = new PageImpl<ArchiveCourrier>(archiveList.subList(start, end),
                            pageable, archiveList.size());
                    return pages;

                } else if (searchOptions.getType().equals("arrive")) {

                    return getSearchArrive(searchOptions, pageable, ids, conn);

                }
                if (searchOptions.getType().equals("depart")) {

                    return getSearchDepart(searchOptions, pageable, ids, conn);

                } else if (searchOptions.getType().equals("interne")) {

                    return getSearchInterne(searchOptions, pageable, ids, conn);

                } else if (searchOptions.getType().equals("examen")) {

                    return getSearchExamn(searchOptions, pageable, ids, conn);

                } else if (searchOptions.getType().equals("piece")) {

                    return getSearchPiece(searchOptions, pageable, ids, conn);

                }

                return null;
            } else {

                Connection conn = DriverManager.getConnection(dbName, user, pw);

                List<String> ids = new ArrayList<String>();

                String sql = "SELECT DISTINCT FOLDER_ID FROM journal";

                PreparedStatement pee = conn.prepareStatement(sql);

                ResultSet result1 = pee.executeQuery();

                while (result1.next()) {
                    ids.add(result1.getString("FOLDER_ID"));
                }

                if (searchOptions.getType().equals("all")) {

                    String[] all = { "depart", "arrive", "interne", "examen", "piece" };

                    List<ArchiveCourrier> archiveList = new ArrayList<ArchiveCourrier>();

                    List<ArchiveCourrier> archiveListArrive = new ArrayList<ArchiveCourrier>();
                    for (String type : all) {

                        if (type.equals("arrive")) {

                            archiveListArrive = getSearchArriveList(searchOptions, pageable, ids, conn);

                            for (ArchiveCourrier archiveCourrier : archiveListArrive) {
                                archiveList.add(archiveCourrier);
                            }

                            archiveListArrive = new ArrayList<ArchiveCourrier>();

                        }
                        if (type.equals("depart")) {

                            archiveListArrive = getSearchDepartList(searchOptions, pageable, ids, conn);

                            for (ArchiveCourrier archiveCourrier : archiveListArrive) {
                                archiveList.add(archiveCourrier);
                            }

                            archiveListArrive = new ArrayList<ArchiveCourrier>();

                        } else if (type.equals("interne")) {

                            archiveListArrive = getSearchInterneList(searchOptions, pageable, ids, conn);

                            for (ArchiveCourrier archiveCourrier : archiveListArrive) {
                                archiveList.add(archiveCourrier);
                            }

                            archiveListArrive = new ArrayList<ArchiveCourrier>();

                        } else if (type.equals("examen")) {

                            archiveListArrive = getSearchExamnList(searchOptions, pageable, ids, conn);

                            for (ArchiveCourrier archiveCourrier : archiveListArrive) {
                                archiveList.add(archiveCourrier);
                            }

                            archiveListArrive = new ArrayList<ArchiveCourrier>();

                        } else if (type.equals("piece")) {

                            archiveListArrive = getSearchPieceList(searchOptions, pageable, ids, conn);

                            for (ArchiveCourrier archiveCourrier : archiveListArrive) {
                                archiveList.add(archiveCourrier);
                            }

                            archiveListArrive = new ArrayList<ArchiveCourrier>();

                        }
                    }

                    if (Objects.nonNull(searchOptions.getOrder())) {
                        String[] order = searchOptions.getOrder().split(",");
                        if (Objects.nonNull(order)) {
                            if (order[0].equals("date")) {
                                if (order[1].equals("desc")) {
                                    archiveList.sort(Comparator.comparing(ArchiveCourrier::getDate).reversed());
                                } else if (order[1].equals("asc")) {
                                    archiveList.sort(Comparator.comparing(ArchiveCourrier::getDate));
                                }
                            }
                        }
                    }

                    int start = (int) pageable.getOffset();
                    int end = (int) (start + pageable.getPageSize()) > archiveList.size() ? archiveList.size()
                            : (start + pageable.getPageSize());

                    System.out.println("Start : " + start);
                    System.out.println("End : " + end);

                    Page<ArchiveCourrier> pages = new PageImpl<ArchiveCourrier>(archiveList.subList(start, end),
                            pageable, archiveList.size());
                    return pages;

                } else if (searchOptions.getType().equals("arrive")) {

                    return getSearchArrive(searchOptions, pageable, ids, conn);

                }
                if (searchOptions.getType().equals("depart")) {

                    return getSearchDepart(searchOptions, pageable, ids, conn);

                } else if (searchOptions.getType().equals("interne")) {

                    return getSearchInterne(searchOptions, pageable, ids, conn);

                } else if (searchOptions.getType().equals("examen")) {

                    return getSearchExamn(searchOptions, pageable, ids, conn);

                } else if (searchOptions.getType().equals("piece")) {

                    return getSearchPiece(searchOptions, pageable, ids, conn);

                }

                return null;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    public Page<ArchiveEtapes> getEtapes(String folderid, Pageable pageable) {

        try {

            Connection conn = DriverManager.getConnection(dbName, user, pw);

            String query = "select * from journal where FOLDER_ID = ? Order By DATE_ACTION";

            PreparedStatement pe = conn.prepareStatement(query);

            pe.setString(1, folderid);

            ArchiveEtapes etape = new ArchiveEtapes();
            List<ArchiveEtapes> etapes = new ArrayList<ArchiveEtapes>();

            ResultSet result = pe.executeQuery();
            while (result.next()) {

                String query1 = "select * from user where ID_USER = ? ";

                PreparedStatement pee = conn.prepareStatement(query1);

                pee.setString(1, result.getString("ID_USER"));

                ResultSet result1 = pee.executeQuery();

                while (result1.next()) {
                    etape.setUser(result1.getString("NAME"));
                }
                etape.setId_journal(result.getString("ID_JOURNAL"));

                etape.setFolder_id(result.getString("FOLDER_ID"));
                etape.setType(result.getString("TYPE_COURRIER"));
                etape.setAction(result.getString("ACTION"));
                etape.setEtape(result.getString("ETAPE"));
                etape.setDate_action(result.getString("DATE_ACTION"));

                etapes.add(etape);

                etape = new ArchiveEtapes();
            }

            int start = (int) pageable.getOffset();
            int end = (int) (start + pageable.getPageSize()) > etapes.size() ? etapes.size()
                    : (start + pageable.getPageSize());
            Page<ArchiveEtapes> pages = new PageImpl<ArchiveEtapes>(etapes.subList(start, end), pageable,
                    etapes.size());
            return pages;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public Page<Fichier> getDepartFiles(String IdCourrier, Pageable pageable) {

        try {

            Connection conn = DriverManager.getConnection(dbName, user, pw);

            String query = "select * from fichier where ID_COURRIER_DEPART = ? ";

            PreparedStatement pe = conn.prepareStatement(query);

            pe.setString(1, IdCourrier);

            Fichier file = new Fichier();
            List<Fichier> files = new ArrayList<Fichier>();

            ResultSet result = pe.executeQuery();
            while (result.next()) {

                file.setId(result.getString("ID_FICHIER"));
                file.setId_Courrier(result.getString("ID_COURRIER_DEPART"));
                file.setTypeCourrier("Courrier Départ");
                if (Objects.nonNull(result.getString("NAME"))) {
                    if (!result.getString("NAME").equals("")) {
                        if (result.getString("NAME").contains("pdf") || result.getString("NAME").contains("PDF")) {
                            file.setContentType("application/pdf");
                        } else if (result.getString("NAME").contains("doc")
                                || result.getString("NAME").contains("DOC")) {
                            file.setContentType(
                                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                        } else if (result.getString("NAME").contains("docx")
                                || result.getString("NAME").contains("DOC")) {
                            file.setContentType(
                                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                        } else if (result.getString("NAME").contains("xlsx")
                                || result.getString("NAME").contains("XLSX")) {
                            file.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                        } else if (result.getString("NAME").contains("csv")
                                || result.getString("NAME").contains("CSV")) {
                            file.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                        } else if (result.getString("NAME").contains("jpg")
                                || result.getString("NAME").contains("JPG")) {
                            file.setContentType("image/jpeg");
                        } else if (result.getString("NAME").contains("png")
                                || result.getString("NAME").contains("PNG")) {
                            file.setContentType("image/png");
                        } else if (result.getString("NAME").contains("tiff")
                                || result.getString("NAME").contains("TIFF")) {
                            file.setContentType("image/tiff");
                        } else if (result.getString("NAME").contains("gif")
                                || result.getString("NAME").contains("GIF")) {
                            file.setContentType("image/gif");
                        } else if (result.getString("NAME").contains("bmp")
                                || result.getString("NAME").contains("BMP")) {
                            file.setContentType("image/bmp");
                        } else if (result.getString("NAME").contains("apng")
                                || result.getString("NAME").contains("APNG")) {
                            file.setContentType("image/apng");
                        }

                    }
                }

                file.setName(result.getString("NAME"));
                file.setGuid(result.getString("GUID"));
                file.setVersion(result.getString("VERSION"));
                file.setAnnexe(result.getString("ANNEXE"));
                files.add(file);

                file = new Fichier();
            }

            int start = (int) pageable.getOffset();
            int end = (int) (start + pageable.getPageSize()) > files.size() ? files.size()
                    : (start + pageable.getPageSize());
            Page<Fichier> pages = new PageImpl<Fichier>(files.subList(start, end), pageable, files.size());
            return pages;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public Page<Fichier> getInterneFiles(String IdCourrier, Pageable pageable) {

        try {

            Connection conn = DriverManager.getConnection(dbName, user, pw);

            String query = "select * from fichier_ci where ID_COURRIER_INTERNE = ? ";

            PreparedStatement pe = conn.prepareStatement(query);

            pe.setString(1, IdCourrier);

            Fichier file = new Fichier();
            List<Fichier> files = new ArrayList<Fichier>();

            ResultSet result = pe.executeQuery();
            while (result.next()) {

                file.setId(result.getString("ID_FICHIER"));
                file.setId_Courrier(result.getString("ID_COURRIER_INTERNE"));
                file.setTypeCourrier("Courrier Interne");
                if (Objects.nonNull(result.getString("NAME"))) {
                    if (!result.getString("NAME").equals("")) {
                        if (result.getString("NAME").contains("pdf") || result.getString("NAME").contains("PDF")) {
                            file.setContentType("application/pdf");
                        } else if (result.getString("NAME").contains("doc")
                                || result.getString("NAME").contains("DOC")) {
                            file.setContentType(
                                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                        } else if (result.getString("NAME").contains("docx")
                                || result.getString("NAME").contains("DOC")) {
                            file.setContentType(
                                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                        } else if (result.getString("NAME").contains("xlsx")
                                || result.getString("NAME").contains("XLSX")) {
                            file.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                        } else if (result.getString("NAME").contains("csv")
                                || result.getString("NAME").contains("CSV")) {
                            file.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                        } else if (result.getString("NAME").contains("jpg")
                                || result.getString("NAME").contains("JPG")) {
                            file.setContentType("image/jpeg");
                        } else if (result.getString("NAME").contains("png")
                                || result.getString("NAME").contains("PNG")) {
                            file.setContentType("image/png");
                        } else if (result.getString("NAME").contains("tiff")
                                || result.getString("NAME").contains("TIFF")) {
                            file.setContentType("image/tiff");
                        } else if (result.getString("NAME").contains("gif")
                                || result.getString("NAME").contains("GIF")) {
                            file.setContentType("image/gif");
                        } else if (result.getString("NAME").contains("bmp")
                                || result.getString("NAME").contains("BMP")) {
                            file.setContentType("image/bmp");
                        } else if (result.getString("NAME").contains("apng")
                                || result.getString("NAME").contains("APNG")) {
                            file.setContentType("image/apng");
                        }

                    }
                }
                file.setName(result.getString("NAME"));
                file.setGuid(result.getString("GUID"));
                file.setVersion(result.getString("VERSION"));
                files.add(file);

                file = new Fichier();
            }

            int start = (int) pageable.getOffset();
            int end = (int) (start + pageable.getPageSize()) > files.size() ? files.size()
                    : (start + pageable.getPageSize());
            Page<Fichier> pages = new PageImpl<Fichier>(files.subList(start, end), pageable, files.size());
            return pages;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public Page<Fichier> getPieceFiles(String IdCourrier, Pageable pageable) {

        try {

            Connection conn = DriverManager.getConnection(dbName, user, pw);

            String query = "select * from fichier_pj where ID_PIECE = ? ";

            PreparedStatement pe = conn.prepareStatement(query);

            pe.setString(1, IdCourrier);

            Fichier file = new Fichier();
            List<Fichier> files = new ArrayList<Fichier>();

            ResultSet result = pe.executeQuery();
            while (result.next()) {

                file.setId(result.getString("ID_FICHIER"));
                file.setId_Courrier(result.getString("ID_PIECE"));
                file.setTypeCourrier("Piece Justificatives");
                if (Objects.nonNull(result.getString("NAME"))) {
                    if (!result.getString("NAME").equals("")) {
                        if (result.getString("NAME").contains("pdf") || result.getString("NAME").contains("PDF")) {
                            file.setContentType("application/pdf");
                        } else if (result.getString("NAME").contains("doc")
                                || result.getString("NAME").contains("DOC")) {
                            file.setContentType(
                                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                        } else if (result.getString("NAME").contains("docx")
                                || result.getString("NAME").contains("DOC")) {
                            file.setContentType(
                                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                        } else if (result.getString("NAME").contains("xlsx")
                                || result.getString("NAME").contains("XLSX")) {
                            file.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                        } else if (result.getString("NAME").contains("csv")
                                || result.getString("NAME").contains("CSV")) {
                            file.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                        } else if (result.getString("NAME").contains("jpg")
                                || result.getString("NAME").contains("JPG")) {
                            file.setContentType("image/jpeg");
                        } else if (result.getString("NAME").contains("png")
                                || result.getString("NAME").contains("PNG")) {
                            file.setContentType("image/png");
                        } else if (result.getString("NAME").contains("tiff")
                                || result.getString("NAME").contains("TIFF")) {
                            file.setContentType("image/tiff");
                        } else if (result.getString("NAME").contains("gif")
                                || result.getString("NAME").contains("GIF")) {
                            file.setContentType("image/gif");
                        } else if (result.getString("NAME").contains("bmp")
                                || result.getString("NAME").contains("BMP")) {
                            file.setContentType("image/bmp");
                        } else if (result.getString("NAME").contains("apng")
                                || result.getString("NAME").contains("APNG")) {
                            file.setContentType("image/apng");
                        }

                    }
                }
                file.setName(result.getString("NAME"));
                file.setGuid(result.getString("GUID"));
                file.setVersion(result.getString("VERSION"));
                files.add(file);

                file = new Fichier();
            }

            int start = (int) pageable.getOffset();
            int end = (int) (start + pageable.getPageSize()) > files.size() ? files.size()
                    : (start + pageable.getPageSize());
            Page<Fichier> pages = new PageImpl<Fichier>(files.subList(start, end), pageable, files.size());
            return pages;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public Page<Fichier> getExamenFiles(String IdCourrier, Pageable pageable) {

        try {

            Connection conn = DriverManager.getConnection(dbName, user, pw);

            String query = "select * from fichier_ex where ID_EXAMEN = ? ";

            PreparedStatement pe = conn.prepareStatement(query);

            pe.setString(1, IdCourrier);

            Fichier file = new Fichier();
            List<Fichier> files = new ArrayList<Fichier>();

            ResultSet result = pe.executeQuery();
            while (result.next()) {

                file.setId(result.getString("ID_FICHIER"));
                file.setId_Courrier(result.getString("ID_PIECE"));
                file.setTypeCourrier("Examen");
                if (Objects.nonNull(result.getString("NAME"))) {
                    if (!result.getString("NAME").equals("")) {
                        if (result.getString("NAME").contains("pdf") || result.getString("NAME").contains("PDF")) {
                            file.setContentType("application/pdf");
                        } else if (result.getString("NAME").contains("doc")
                                || result.getString("NAME").contains("DOC")) {
                            file.setContentType(
                                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                        } else if (result.getString("NAME").contains("docx")
                                || result.getString("NAME").contains("DOC")) {
                            file.setContentType(
                                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                        } else if (result.getString("NAME").contains("xlsx")
                                || result.getString("NAME").contains("XLSX")) {
                            file.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                        } else if (result.getString("NAME").contains("csv")
                                || result.getString("NAME").contains("CSV")) {
                            file.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                        } else if (result.getString("NAME").contains("jpg")
                                || result.getString("NAME").contains("JPG")) {
                            file.setContentType("image/jpeg");
                        } else if (result.getString("NAME").contains("png")
                                || result.getString("NAME").contains("PNG")) {
                            file.setContentType("image/png");
                        } else if (result.getString("NAME").contains("tiff")
                                || result.getString("NAME").contains("TIFF")) {
                            file.setContentType("image/tiff");
                        } else if (result.getString("NAME").contains("gif")
                                || result.getString("NAME").contains("GIF")) {
                            file.setContentType("image/gif");
                        } else if (result.getString("NAME").contains("bmp")
                                || result.getString("NAME").contains("BMP")) {
                            file.setContentType("image/bmp");
                        } else if (result.getString("NAME").contains("apng")
                                || result.getString("NAME").contains("APNG")) {
                            file.setContentType("image/apng");
                        }

                    }
                }
                file.setName(result.getString("NAME"));
                file.setGuid(result.getString("GUID"));
                file.setVersion(result.getString("VERSION"));
                files.add(file);

                file = new Fichier();
            }

            int start = (int) pageable.getOffset();
            int end = (int) (start + pageable.getPageSize()) > files.size() ? files.size()
                    : (start + pageable.getPageSize());
            Page<Fichier> pages = new PageImpl<Fichier>(files.subList(start, end), pageable, files.size());
            return pages;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public Page<ArchiveFormatResponse> getArriveFiles(String IdCourrier, Pageable pageable) {

        try {

            Connection conn = DriverManager.getConnection(dbName, user, pw);

            String query = "select * from courrier_arrive where ID_COURRIER_ARRIVE = ?";

            PreparedStatement pe = conn.prepareStatement(query);

            pe.setString(1, IdCourrier);

            ArchiveCourrier courrier = new ArchiveCourrier();

            ResultSet result = pe.executeQuery();
            while (result.next()) {
                courrier.setId(result.getString("ID_COURRIER_ARRIVE"));
                courrier.setType("Courrier Arrivé");
                courrier.setObjet(result.getString("OBJET_COURRIER"));
                courrier.setDate(result.getString("DATE_ENREGISTREMENT"));
                courrier.setDestinateur(result.getString("ID_DESTINATAIRE_ACAPS"));
                courrier.setRef(result.getString("REF_BO_COURRIER_ARRIVE"));
                courrier.setEmetteur(result.getString("ID_EXPEDITEUR"));
                courrier.setStatus(result.getString("STATUS"));
                courrier.setFolderId(result.getString("FOLDER_ID"));
                courrier.setTypeAttribut(result.getString("TYPEATTRIBUT"));
                courrier.setTypeFamilles(result.getString("TYPEFAMILLES"));
            }

            if (Objects.nonNull(courrier.getTypeFamilles())) {

                List<String> types = Arrays.stream(courrier.getTypeFamilles().split(";")).collect(Collectors.toList());

                ArriveFormatRequest requestData = new ArriveFormatRequest();
                requestData.setDocTypes(types);
                requestData.setFieldName(courrier.getTypeAttribut());
                requestData.setFieldValue(courrier.getRef());

                MasterConfig user1 = masterConfigService.getCmIntegration("USER_INREGRATION_INFO");
                MasterConfig url = masterConfigService.getCmIntegration("CM_INTEGRATION");
                byte[] decodedBytes = Base64.getDecoder().decode(user1.getConfigValue());
                String decodedString = new String(decodedBytes);
                com.veviosys.vdigit.classes.Login userInfo = new Gson().fromJson(decodedString,
                        com.veviosys.vdigit.classes.Login.class);

                WebClient client = WebClient.create();

                WebClient.ResponseSpec responseSpec = client.mutate().codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024)).build().post()
                        .uri(url.getConfigValue() + "/cm-connector/search-document")
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(
                                                (userInfo.getUsername() + ":" + userInfo.getPassword()).getBytes()))
                        .body(BodyInserters.fromValue(requestData)).retrieve().onStatus(
                                HttpStatus.UNAUTHORIZED::equals,
                                response -> response.bodyToMono(String.class).map(Exception::new));

                List<ArchiveFormatResponse> responseBody = Arrays
                        .stream(responseSpec.bodyToMono(ArchiveFormatResponse[].class).block())
                        .collect(Collectors.toList());

                int start = (int) pageable.getOffset();
                int end = (int) (start + pageable.getPageSize()) > responseBody.size() ? responseBody.size()
                        : (start + pageable.getPageSize());
                Page<ArchiveFormatResponse> pages = new PageImpl<ArchiveFormatResponse>(
                        responseBody.subList(start, end), pageable, responseBody.size());
                return pages;

            } else {

                return null;

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

}
