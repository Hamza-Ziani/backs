package com.veviosys.vdigit.dashboard.serivces;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.persistence.EntityManager;

import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;
import com.veviosys.vdigit.classes.StatisticUser;
import com.veviosys.vdigit.dashboard.models.UserDashboardSettings;
import com.veviosys.vdigit.dashboard.models.UserSettingsRepository;
import com.veviosys.vdigit.models.DocumentType;
import com.veviosys.vdigit.models.ElementTypeGroup;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.repositories.UserRepository;
import com.veviosys.vdigit.services.ElementTypeGroupService;


import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KPIs {

    @Autowired
    private EntityManager em;

    @Value("${documania.datasource.select}")
    private String selectedDb;

    @Autowired
    ElementTypeGroupService elementTypeGroupService;

    @Autowired
    UserSettingsRepository settingsRepository;

    @Autowired
    UserRepository userRepository;

    public HashMap<String, List<String>> getCurrentFlowNormalByName(Long userID) {

        List<Object[]> data = em.createNativeQuery(
                "SELECT count(*) as count ,f.nature_name  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                        + userID + " and   CURRENT_TIMESTAMP < ce.date_fin and ce.etat=0 group by f.nature_name")
                .getResultList();

        List<String> counts = new ArrayList<>();
        List<String> names = new ArrayList<>();
        for (Object[] object : data) {

            counts.add(String.valueOf(object[0]));
            names.add(String.valueOf(object[1]));
        }

        HashMap<String, List<String>> dataRet = new HashMap<>();

        dataRet.put("counts", counts);
        dataRet.put("names", names);

        return dataRet;
    }

    public HashMap<String, List<String>> getCurrentFlowNormalByNameRed(Long userID) {

        List<Object[]> data = null;
        List<Object[]> data2 = null;
        if (selectedDb.equals("orcl")) {
            data = em.createNativeQuery(
                    "SELECT count(*) as count ,f.nature_name  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID
                            + " and   CURRENT_TIMESTAMP > ce.date_fin  and ce.date_debut is not null and ce.etat=0 group by f.nature_name")
                    .getResultList();

            data2 = em.createNativeQuery(
                    "SELECT count(*) as count ,f.nature_name  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID
                            + " and   CURRENT_TIMESTAMP > ce.date_fin and ce.late_relance=1 and ce.date_debut is not null and ce.etat=0 group by f.nature_name")
                    .getResultList();

        } else if (selectedDb.equals("mssql")) {

            data = em.createNativeQuery(
                    "SELECT count(*) as count ,f.nature_name  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID
                            + " and   CURRENT_TIMESTAMP > ce.date_fin  and ce.date_debut is not null and ce.etat=0 group by f.nature_name")
                    .getResultList();

            data2 = em.createNativeQuery(
                    "SELECT count(*) as count ,f.nature_name  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID
                            + " and   CURRENT_TIMESTAMP > ce.date_fin and ce.late_relance=1 and ce.date_debut is not null and ce.etat=0 group by f.nature_name")
                    .getResultList();

        } else if (selectedDb.equals("pgsql")) {

            data = em.createNativeQuery(
                    "SELECT count(*) as count ,f.nature_name  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID
                            + " and   DATE_PART('day', CURRENT_TIMESTAMP - ce.date_fin)>=0  and ce.date_debut is not null and ce.etat=0 group by f.nature_name")
                    .getResultList();

            data2 = em.createNativeQuery(
                    "SELECT count(*) as count ,f.nature_name  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID
                            + " and   DATE_PART('day', CURRENT_TIMESTAMP - ce.date_fin)>=0 and ce.late_relance=true and ce.date_debut is not null and ce.etat=0 group by f.nature_name")
                    .getResultList();

        } else if (selectedDb.equals("msql")) {

            data = em.createNativeQuery(
                    "SELECT count(*) as count ,f.nature_name  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID
                            + " and   CURRENT_TIMESTAMP - ce.date_fin >=0  and ce.date_debut is not null and ce.etat=0 group by f.nature_name")
                    .getResultList();

            data2 = em.createNativeQuery(
                    "SELECT count(*) as count ,f.nature_name  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID
                            + " and   CURRENT_TIMESTAMP - ce.date_fin >=0 and ce.late_relance=true and ce.date_debut is not null and ce.etat=0 group by f.nature_name")
                    .getResultList();

        }

        List<String> counts = new ArrayList<>();
        List<String> names = new ArrayList<>();
        for (Object[] object : data) {

            counts.add(String.valueOf(object[0]));
            names.add(String.valueOf(object[1]));
        }

        HashMap<String, List<String>> dataRet = new HashMap<>();

        dataRet.put("counts", counts);
        dataRet.put("countsRelance", counts);
        dataRet.put("names", names);

        return dataRet;
    }

    public HashMap<String, Long> getCurrentFlowCounts(Long userID) {
        List<Object[]> data2 = null;

        List<Object[]> data = null;
        if (selectedDb.equals("orcl")) {
            data2 = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where     f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID
                            + " and   CURRENT_TIMESTAMP > ce.date_fin and ce.date_debut is not null and ce.etat=0")
                    .getResultList();

            data = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where       f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and   CURRENT_TIMESTAMP < ce.date_fin and ce.etat=0")
                    .getResultList();

        } else if (selectedDb.equals("mssql")) {

            data2 = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where     f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and   GETDATE() > ce.date_fin and ce.date_debut is not null and ce.etat=0")
                    .getResultList();

            data = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where       f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and   GETDATE() < ce.date_fin and ce.etat=0")
                    .getResultList();

        } else if (selectedDb.equals("pgsql")) {

            data2 = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID
                            + " and   DATE_PART('day', CURRENT_TIMESTAMP - ce.date_fin)>=0 and ce.date_debut is not null and ce.etat=0")
                    .getResultList();

            data = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and   DATE_PART('day', CURRENT_TIMESTAMP - ce.date_fin)<=0 and ce.etat=0")
                    .getResultList();

        } else if (selectedDb.equals("msql")) {

            data2 = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID
                            + " and   CURRENT_TIMESTAMP - ce.date_fin >=0 and ce.date_debut is not null and ce.etat=0")
                    .getResultList();

            data = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and   CURRENT_TIMESTAMP - ce.date_fin <=0 and ce.etat=0")
                    .getResultList();

        }

        HashMap<String, Long> dataRet = new HashMap<>();

        System.out.println(data.get(0) + "");
        dataRet.put("normal", Long.valueOf(String.valueOf(data.get(0))));
        dataRet.put("late", Long.valueOf(String.valueOf(data2.get(0))));
        dataRet.put("total", Long.valueOf(String.valueOf(data.get(0))) + Long.valueOf(String.valueOf(data2.get(0))));

        return dataRet;
    }

    public HashMap<String, Long> getCurrentFlowCounts(Long userID, Long type,LocalDate yearStart,LocalDate yearEnd) {
        List<Object[]> data2 = null;

        List<Object[]> data = null;
        if (selectedDb.equals("orcl")) {
            data2 = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where     f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + "  and  folder_type = " + type
                            + " and   CURRENT_TIMESTAMP > ce.date_fin and ce.date_debut is not null and ce.etat=0"
                            +"and ce.DATE_DEBUT between TO_DATE('" + yearStart.toString()
                            + " 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('" + yearEnd.toString()
                            + "','YYYY-MM-DD HH24:MI:SS')")
                    .getResultList();

            data = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where       f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and   folder_type = " + type
                            + " and   CURRENT_TIMESTAMP < ce.date_fin and ce.etat=0"
                            +"and ce.DATE_DEBUT between TO_DATE('" + yearStart.toString()
                            + " 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('" + yearEnd.toString()
                            + "','YYYY-MM-DD HH24:MI:SS')")
                    .getResultList();

        } else if (selectedDb.equals("mssql")) {

            data2 = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where     f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and  folder_type = " + type
                            + " and   GETDATE() > ce.date_fin and ce.date_debut is not null and ce.etat=0"
                            +"and ce.DATE_DEBUT between CAST('" + yearStart.toString()
                            + "' AS DATE) and CAST ('" + yearEnd.toString()
                            + "' AS DATE)")
                    .getResultList();

            data = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where       f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + "  and   folder_type = " + type + " and   GETDATE() < ce.date_fin and ce.etat=0"
                            +"and ce.DATE_DEBUT between CAST('" + yearStart.toString()
                            + "' AS DATE) and CAST ('" + yearEnd.toString()
                            + "' AS DATE)")
                    .getResultList();

        } else if (selectedDb.equals("pgsql")) {

            data2 = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and  folder_type = " + type
                            + " and   DATE_PART('day', CURRENT_TIMESTAMP - ce.date_fin)>=0 and ce.date_debut is not null and ce.etat=0"
                            + "and ce.DATE_TRAITEMENT >= TO_DATE('"
                            + yearStart.toString() + "','yyyy-mm-dd') and ce.DATE_TRAITEMENT < TO_DATE('"
                            + yearEnd.toString() + "' ,'yyyy-mm-dd')")
                    .getResultList();

            data = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and   folder_type = " + type
                            + " and   DATE_PART('day', CURRENT_TIMESTAMP - ce.date_fin)<=0 and ce.etat=0"
                            + "and ce.DATE_TRAITEMENT >= TO_DATE('"
                            + yearStart.toString() + "','yyyy-mm-dd') and ce.DATE_TRAITEMENT < TO_DATE('"
                            + yearEnd.toString() + "' ,'yyyy-mm-dd')")
                    .getResultList();

        } else if (selectedDb.equals("msql")) {

            data2 = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and  folder_type = " + type
                            + "  and   CURRENT_TIMESTAMP - ce.date_fin >=0 and ce.date_debut is not null and ce.etat=0"
                            + " and ce.DATE_DEBUT >= STR_TO_DATE('" + yearStart.toString()+ "', '%Y-%m-%d') and ce.DATE_DEBUT <= STR_TO_DATE('" + yearEnd.toString()+ "', '%Y-%m-%d')"        
                    )
                    .getResultList();

            data = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and     folder_type = " + type
                            + " and CURRENT_TIMESTAMP - ce.date_fin <=0 and ce.etat=0"
                            + " and ce.DATE_DEBUT >= STR_TO_DATE('" + yearStart.toString()+ "', '%Y-%m-%d') and ce.DATE_DEBUT <= STR_TO_DATE('" + yearEnd.toString()+ "', '%Y-%m-%d')"
                            )
                    .getResultList();

        }

        HashMap<String, Long> dataRet = new HashMap<>();

        System.out.println(data.get(0) + "");
        dataRet.put("normal", Long.valueOf(String.valueOf(data.get(0))));
        dataRet.put("late", Long.valueOf(String.valueOf(data2.get(0))));
        dataRet.put("total", Long.valueOf(String.valueOf(data.get(0))) + Long.valueOf(String.valueOf(data2.get(0))));

        return dataRet;
    }

    public HashMap<String, Long> getDoneFlowCounts(Long userID) {

        List<Object[]> data2 = null;
        List<Object[]> data = null;

        if (selectedDb.equals("orcl")) {

            data2 = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and f.finalise='fini'  and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID
                            + " and   ce.DATE_TRAITEMENT  > ce.date_fin and ce.date_debut is not null and ce.etat=1")
                    .getResultList();
            data = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and f.finalise='fini' and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and  ce.DATE_TRAITEMENT <= ce.date_fin  and ce.etat=1")
                    .getResultList();

        } else if (selectedDb.equals("mssql")) {

            data2 = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and f.finalise='fini'  and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID
                            + " and   ce.DATE_TRAITEMENT  > ce.date_fin and ce.date_debut is not null and ce.etat=1")
                    .getResultList();
            data = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and f.finalise='fini' and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and  ce.DATE_TRAITEMENT <= ce.date_fin  and ce.etat=1")
                    .getResultList();
        } else if (selectedDb.equals("pgsql")) {

            data2 = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and f.finalise='fini'  and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID
                            + " and  DATE_PART('day', ce.DATE_TRAITEMENT - ce.date_fin)>=0 and  ce.date_debut is not null and ce.etat=1")
                    .getResultList();
            data = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and f.finalise='fini' and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and  DATE_PART('day', ce.DATE_TRAITEMENT - ce.date_fin)<=0 and ce.etat=1")
                    .getResultList();
        } else if (selectedDb.equals("msql")) {

            data2 = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and f.finalise='fini'  and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID
                            + " and  ce.DATE_TRAITEMENT - ce.date_fin >=0 and  ce.date_debut is not null and ce.etat=1")
                    .getResultList();
            data = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and f.finalise='fini' and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and  ce.DATE_TRAITEMENT - ce.date_fin <=0 and ce.etat=1")
                    .getResultList();
        }

        HashMap<String, Long> dataRet = new HashMap<>();
        System.out.println(data.get(0) + "");
        dataRet.put("normal", Long.valueOf(String.valueOf(data.get(0))));
        dataRet.put("late", Long.valueOf(String.valueOf(data2.get(0))));
        dataRet.put("total", Long.valueOf(String.valueOf(data.get(0))) + Long.valueOf(String.valueOf(data2.get(0))));

        return dataRet;
    }

    public HashMap<String, Long> getDoneFlowCounts(Long userID, Long type,LocalDate yearStart,LocalDate yearEnd) {

        List<Object[]> data2 = null;
        List<Object[]> data = null;

        if (selectedDb.equals("orcl")) {

            data2 = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and f.finalise='fini'  and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and  folder_type = " + type
                            + " and   ce.DATE_TRAITEMENT  > ce.date_fin and ce.date_debut is not null and ce.etat=1"
                            +"and ce.DATE_DEBUT between TO_DATE('" + yearStart.toString()
                                    + " 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('" + yearEnd.toString()
                                    + "','YYYY-MM-DD HH24:MI:SS')")
                    .getResultList();
            data = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and f.finalise='fini' and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and  folder_type = " + type
                            + " and  ce.DATE_TRAITEMENT <= ce.date_fin  and ce.etat=1"
                            +"and ce.DATE_DEBUT between TO_DATE('" + yearStart.toString()
                                    + " 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('" + yearEnd.toString()
                                    + "','YYYY-MM-DD HH24:MI:SS')")
                    .getResultList();

        } else if (selectedDb.equals("mssql")) {

            data2 = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and f.finalise='fini'  and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and  folder_type = " + type
                            + " and   ce.DATE_TRAITEMENT  > ce.date_fin and ce.date_debut is not null and ce.etat=1"
                            +"and ce.DATE_DEBUT between CAST('" + yearStart.toString()
                                    + "' AS DATE) and CAST ('" + yearEnd.toString()
                                    + "' AS DATE)")
                    .getResultList();
            data = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and f.finalise='fini' and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and  folder_type = " + type
                            + "  and  ce.DATE_TRAITEMENT <= ce.date_fin  and ce.etat=1"
                            +"and ce.DATE_DEBUT between CAST('" + yearStart.toString()
                                    + "' AS DATE) and CAST ('" + yearEnd.toString()
                                    + "' AS DATE)")
                    .getResultList();
        } else if (selectedDb.equals("pgsql")) {

            data2 = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and f.finalise='fini'  and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and  folder_type = " + type
                            + " and  DATE_PART('day', ce.DATE_TRAITEMENT - ce.date_fin)>=0 and  ce.date_debut is not null and ce.etat=1"
                            + "and ce.DATE_TRAITEMENT >= TO_DATE('"
                            + yearStart.toString() + "','yyyy-mm-dd') and ce.DATE_TRAITEMENT < TO_DATE('"
                            + yearEnd.toString() + "' ,'yyyy-mm-dd')")
                    .getResultList();
            data = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and f.finalise='fini' and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and  folder_type = " + type
                            + " and  DATE_PART('day', ce.DATE_TRAITEMENT - ce.date_fin)<=0 and ce.etat=1"
                            + "and ce.DATE_TRAITEMENT >= TO_DATE('"
                                    + yearStart.toString() + "','yyyy-mm-dd') and ce.DATE_TRAITEMENT < TO_DATE('"
                                    + yearEnd.toString() + "' ,'yyyy-mm-dd')")
                    .getResultList();
        } else if (selectedDb.equals("msql")) {

            data2 = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and f.finalise='fini'  and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and  folder_type = " + type
                            + " and  ce.DATE_TRAITEMENT - ce.date_fin >=0 and  ce.date_debut is not null and ce.etat=1"
                            + " and ce.DATE_DEBUT >= STR_TO_DATE('" + yearStart.toString()+ "', '%Y-%m-%d') and ce.DATE_DEBUT <= STR_TO_DATE('" + yearEnd.toString()+ "', '%Y-%m-%d')")
                    .getResultList();
            data = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and f.finalise='fini' and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and  folder_type = " + type
                            + " and  ce.DATE_TRAITEMENT - ce.date_fin <=0 and ce.etat=1"
                            + " and ce.DATE_DEBUT >= STR_TO_DATE('" + yearStart.toString()+ "', '%Y-%m-%d') and ce.DATE_DEBUT <= STR_TO_DATE('" + yearEnd.toString()+ "', '%Y-%m-%d')")
                    .getResultList();
        }

        HashMap<String, Long> dataRet = new HashMap<>();
        System.out.println(data.get(0) + "");
        dataRet.put("normal", Long.valueOf(String.valueOf(data.get(0))));
        dataRet.put("late", Long.valueOf(String.valueOf(data2.get(0))));
        dataRet.put("total", Long.valueOf(String.valueOf(data.get(0))) + Long.valueOf(String.valueOf(data2.get(0))));

        return dataRet;
    }
    
    
    
    public HashMap<String, Double> getDoneFlowsAvg(Long userID, Long type,LocalDate yearStart,LocalDate yearEnd,Long unit) {

        List<Object[]> data = null;

        if (selectedDb.equals("orcl")) {

            data = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and f.finalise='fini' and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and  folder_type = " + type
                            + " and  ce.DATE_TRAITEMENT <= ce.date_fin  and ce.etat=1"
                            +"and ce.DATE_DEBUT between TO_DATE('" + yearStart.toString()
                                    + " 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('" + yearEnd.toString()
                                    + "','YYYY-MM-DD HH24:MI:SS')")
                    .getResultList();

        } else if (selectedDb.equals("mssql")) {

            data = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and f.finalise='fini' and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and  folder_type = " + type
                            + "  and  ce.DATE_TRAITEMENT <= ce.date_fin  and ce.etat=1"
                            +"and ce.DATE_DEBUT between CAST('" + yearStart.toString()
                                    + "' AS DATE) and CAST ('" + yearEnd.toString()
                                    + "' AS DATE)")
                    .getResultList();
        } else if (selectedDb.equals("pgsql")) {         
            data = em.createNativeQuery(
                    "SELECT count(*) as count  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and f.finalise='fini' and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and  folder_type = " + type
                            + " and  DATE_PART('day', ce.DATE_TRAITEMENT - ce.date_fin)<=0 and ce.etat=1"
                            + "and ce.DATE_TRAITEMENT >= TO_DATE('"
                                    + yearStart.toString() + "','yyyy-mm-dd') and ce.DATE_TRAITEMENT < TO_DATE('"
                                    + yearEnd.toString() + "' ,'yyyy-mm-dd')")
                    .getResultList();
        } else if (selectedDb.equals("msql")) {       
            data = em.createNativeQuery(
                    "SELECT IFNULL(AVG((UNIX_TIMESTAMP(ce.DATE_TRAITEMENT) - UNIX_TIMESTAMP(ce.DATE_DEBUT)) / "+unit+" ) , 0) as count  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and f.finalise='fini' and ce.id = uge.clone_etape_id and uge.user_id="
                            + userID + " and  folder_type = " + type
                            + " and ce.etat=1"
                            + " and ce.DATE_DEBUT >= STR_TO_DATE('" + yearStart.toString()+ "', '%Y-%m-%d') and ce.DATE_DEBUT <= STR_TO_DATE('" + yearEnd.toString()+ "', '%Y-%m-%d')")
                    .getResultList();
        }

        HashMap<String, Double> dataRet = new HashMap<>();
        
        dataRet.put("total", Double.valueOf(String.valueOf(data.get(0))) );

        return dataRet;
    }

    public HashMap<String, List<String>> getItemsByType(Long typeId, String showBy, LocalDate yearStart,
            LocalDate yearEnd) {

        List<String> counts = new ArrayList<>();
        List<String> names = new ArrayList<>();
        if (showBy.equals("year")) {

            List<Object[]> data = null;
            if (selectedDb.equals("orcl")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (select to_char(TO_DATE (UPLOAD_DATE,'YYYY-MM-DD HH24:MI:SS'),'YYYY') AS xAxe from d"
                                + typeId + " where TO_DATE (UPLOAD_DATE,'YYYY-MM-DD HH24:MI:SS') between TO_DATE('"
                                + yearStart.toString() + " 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('"
                                + yearEnd.toString() + "','YYYY-MM-DD HH24:MI:SS') ) group by xAxe  order by xAxe")
                        .getResultList();
            } else if (selectedDb.equals("mssql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (select FORMAT(CAST(UPLOAD_DATE AS DATE),'yyyy') AS xAxe from d"
                                + typeId + " where CAST(UPLOAD_DATE as date) between CAST('" + yearStart.toString()
                                + "' as date) and CAST ('" + yearEnd.toString()
                                + "' as date )) as t group by xAxe  order by xAxe")
                        .getResultList();
            } else if (selectedDb.equals("pgsql")) {

                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (select FORMAT(CAST(UPLOAD_DATE AS DATE),'yyyy') AS xAxe from d"
                                + typeId + " where CAST(UPLOAD_DATE as date) between CAST('" + yearStart.toString()
                                + "' as date) and CAST ('" + yearEnd.toString()
                                + "' as date )) as t group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("msql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (select DATE_FORMAT(UPLOAD_DATE,'%Y') AS xAxe from d" + typeId
                                + " where UPLOAD_DATE between STR_TO_DATE('" + yearStart.toString()
                                + "','%Y-%m-%d') and  STR_TO_DATE('" + yearEnd.toString()
                                + "','%Y-%m-%d')) as t group by xAxe  order by xAxe")
                        .getResultList();
            }
            System.out.println("getItemsByType -- year  : " + data);

            int yEnd = yearEnd.getYear();
            int yStart = yearStart.getYear();
            names.add(yStart + "");
            counts.add("0");
            while (yEnd != yStart) {

                yearStart = yearStart.plusYears(1L);
                yStart = yearStart.getYear();
                System.out.println("" + yStart);
                names.add(String.valueOf(yStart));
                counts.add(String.valueOf("0"));

            }

            for (Object[] object : data) {

                for (int i = 0; i < names.size(); i++) {

                    if (names.get(i).equals(object[0])) {

                        counts.set(i, String.valueOf(object[1]));

                    }

                }

//				 names.add(String.valueOf(object[0]));
//				 counts.add(String.valueOf(object[1]));

            }

        } else if (showBy.equals("month")) {
            List<Object[]> data = null;
            try {
                data = null;

                if (selectedDb.equals("orcl")) {

                    data = em.createNativeQuery("WITH data1 as(select xAxe, count(xAxe) ct  from"
                            + "(select to_char(TO_DATE (UPLOAD_DATE,'YYYY-MM-DD HH24:MI:SS'),'MM')|| '/'  || to_char(TO_DATE (UPLOAD_DATE,'YYYY-MM-DD HH24:MI:SS'),'YY')  AS xAxe from d"
                            + typeId + " )"
                            + "group by xAxe  order by xAxe"
                            + "),"
                            + "data2 as (SELECT LEVEL as num, TO_CHAR ( ADD_MONTHS (TO_DATE ('" + yearEnd.toString()
                            + "','YYYY-MM-DD HH24:MI:SS') , LEVEL - 1 - MONTHS_BETWEEN( TO_DATE ('" + yearEnd.toString()
                            + "','YYYY-MM-DD HH24:MI:SS'),TO_DATE('" + yearStart.toString()
                            + " 00:00:00','YYYY-MM-DD HH24:MI:SS')) ) ,'MM/YY' ) AS str  FROM dual CONNECT BY LEVEL <= MONTHS_BETWEEN(sysdate,TO_DATE('"
                            + yearStart.toString() + " 00:00:00','YYYY-MM-DD HH24:MI:SS'))  )"
                            + "select ct,str from data1 RIGHT JOIN data2 on data1.xAxe = data2.str  order by num desc ")
                            .getResultList();

                } else if (selectedDb.equals("mssql")) {

                    data = em.createNativeQuery(
                            " WITH data1 as( select xAxe, count(xAxe) ct  from (select FORMAT(CAST(UPLOAD_DATE AS DATE ),'MM/yy') AS xAxe from d"
                                    + typeId
                                    + " )as t group by xAxe ),data2 as (SELECT 0 as num , FORMAT(DATEADD(MONTH,DATEDIFF(month,CAST('"
                                    + yearEnd.toString() + "' as date),CAST('" + yearEnd.toString()
                                    + "' as date)),CAST('" + yearStart.toString()
                                    + "' as date)),'MM/yy') as str UNION ALL SELECT num+1 , FORMAT(DATEADD(MONTH,num+1+DATEDIFF(month,CAST('"
                                    + yearEnd.toString() + "' as date),CAST('" + yearEnd.toString()
                                    + "' as date)),CAST('" + yearStart.toString()
                                    + "' as date)),'MM/yy') as strr FROM data2 WHERE num < DATEDIFF(month,CAST('"
                                    + yearStart.toString() + "' as date),CAST('" + yearEnd.toString()
                                    + "' as date))) select ct , str from data1 RIGHT JOIN data2 on data1.xAxe = data2.str  order by num desc"
                                    +
                                    "")
                            .getResultList();

                } else if (selectedDb.equals("pgsql")) {

                    data = em.createNativeQuery(
                            " WITH data1 as( select xAxe, count(xAxe) ct  from (select FORMAT(CAST(UPLOAD_DATE AS DATE ),'MM/yy') AS xAxe from d"
                                    + typeId
                                    + " )as t group by xAxe ),data2 as (SELECT 0 as num , FORMAT(DATEADD(MONTH,DATEDIFF(month,CAST('"
                                    + yearEnd.toString() + "' as date),CAST('" + yearEnd.toString()
                                    + "' as date)),CAST('" + yearStart.toString()
                                    + "' as date)),'MM/yy') as str UNION ALL SELECT num+1 , FORMAT(DATEADD(MONTH,num+1+DATEDIFF(month,CAST('"
                                    + yearEnd.toString() + "' as date),CAST('" + yearEnd.toString()
                                    + "' as date)),CAST('" + yearStart.toString()
                                    + "' as date)),'MM/yy') as strr FROM data2 WHERE num < DATEDIFF(month,CAST('"
                                    + yearStart.toString() + "' as date),CAST('" + yearEnd.toString()
                                    + "' as date))) select ct , str from data1 RIGHT JOIN data2 on data1.xAxe = data2.str  order by num desc"
                                    +
                                    "")
                            .getResultList();

                } else if (selectedDb.equals("msql")) {
                    data = em.createNativeQuery(
                            "WITH RECURSIVE  data1 as( select xAxe, count(xAxe) ct  from (select  DATE_FORMAT(UPLOAD_DATE,'%m/%y')  AS xAxe from d"
                                    + typeId
                                    + " )as t group by xAxe ), data2 as (SELECT 0 as num , DATE_FORMAT( DATE_ADD( DATE_FORMAT('"
                                    + yearEnd.toString() + "','%Y-%m-%d') , INTERVAL  TIMESTAMPDIFF(MONTH,DATE_FORMAT('"
                                    + yearEnd.toString() + "','%Y-%m-%d'),DATE_FORMAT('" + yearStart.toString()
                                    + "','%Y-%m-%d')) MONTH ) ,'%m/%y' ) as str  UNION ALL  SELECT num + 1 , DATE_FORMAT( DATE_ADD( DATE_FORMAT('"
                                    + yearEnd.toString()
                                    + "','%Y-%m-%d') , INTERVAL num + 1 + TIMESTAMPDIFF(MONTH,DATE_FORMAT('"
                                    + yearEnd.toString() + "','%Y-%m-%d'),DATE_FORMAT('" + yearStart.toString()
                                    + "','%Y-%m-%d')) MONTH ) ,'%m/%y' )  as strr FROM data2 WHERE num < TIMESTAMPDIFF(month,DATE_FORMAT('"
                                    + yearStart.toString() + "','%Y-%m-%d'),DATE_FORMAT('" + yearEnd.toString()
                                    + "','%Y-%m-%d')) ) select ct , str from data1 RIGHT JOIN data2 on data1.xAxe = data2.str  order by num desc;"
                                    + "")
                            .getResultList();
                }

                System.out.println("getItemsByType -- month  : " + data);

            } catch (Exception e) {
                // TODO: handle exception

                e.printStackTrace();
            }

            for (Object[] object : data) {

                counts.add(String.valueOf(Objects.isNull(object[0]) ? 0 : object[0]));
                names.add(String.valueOf(object[1]));
            }

            counts = Lists.reverse(counts);
            names = Lists.reverse(names);

        }

        else if (showBy.equals("week")) {

            List<Object[]> data = null;

            if (selectedDb.equals("orcl")) {

                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) ct  from (select to_char(TO_DATE (UPLOAD_DATE,'YYYY-MM-DD HH24:MI:SS'),'WW')|| '/'  || to_char(TO_DATE (UPLOAD_DATE,'YYYY-MM-DD HH24:MI:SS'),'YYYY')  AS xAxe from d"
                                + typeId + "   where TO_DATE (UPLOAD_DATE,'YYYY-MM-DD HH24:MI:SS') between TO_DATE('"
                                + yearStart.toString() + " 00:00:00','YYYY-MM-DD HH24:MI:SS')  and TO_DATE('"
                                + yearEnd.toString()
                                + " 00:00:00','YYYY-MM-DD HH24:MI:SS')   ) group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("mssql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) ct  from (select   CONCAT(DATENAME (WEEK, UPLOAD_DATE)-1,'/', FORMAT(CAST(UPLOAD_DATE AS DATE),'yyyy'))   AS xAxe from d"
                                + typeId + "   where CAST(UPLOAD_DATE AS DATE) between CAST('" + yearStart.toString()
                                + "' AS DATE)  and CAST('" + yearEnd.toString()
                                + "' as date)   ) as r group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("msql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) ct  from (select   CONCAT(WEEK(UPLOAD_DATE),'/', DATE_FORMAT(UPLOAD_DATE,'%Y'))   AS xAxe from d"
                                + typeId + "   where UPLOAD_DATE  between DATE_FORMAT('" + yearStart.toString()
                                + "' ,'%Y-%m-%d' )  and DATE_FORMAT('" + yearEnd.toString()
                                + "' ,'%Y-%m-%d' )   ) as r group by xAxe  order by xAxe")
                        .getResultList();
            }
            System.out.println("getItemsByType -- week  : " + data);

            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int yEnd = yearEnd.getYear();
            int weekEnd = yearEnd.get(weekFields.weekOfWeekBasedYear());
            int yStart = yearStart.getYear();
            int weekStart = yearStart.get(weekFields.weekOfWeekBasedYear());
            names.add((weekStart < 10 ? "0" : "") + weekStart + "/" + yStart);
            counts.add("0");
            System.out.println(weekStart + "/" + yStart);
            ;
            while (yEnd != yStart || weekEnd != weekStart) {
                System.out.println(weekStart + "/" + yStart);
                ;
                yearStart = yearStart.plusWeeks(1L);
                yStart = yearStart.getYear();
                weekStart = yearStart.get(weekFields.weekOfWeekBasedYear());
                System.out.println("" + weekStart + "/" + yStart);
                names.add(String.valueOf(weekStart < 10 ? "0" : "") + weekStart + "/" + yStart);
                counts.add(String.valueOf("0"));

            }

            for (Object[] object : data) {
                System.out.println(object[0] + " ============= ");
                for (int i = 0; i < names.size(); i++) {

                    if (names.get(i).equals(object[0])) {

                        counts.set(i, String.valueOf(object[1]));

                    }

                }

            }

        }

        else if (showBy.equals("day")) {

            List<Object[]> data = null;

            if (selectedDb.equals("orcl")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) ct  from (select to_char(TO_DATE (UPLOAD_DATE,'YYYY-MM-DD HH24:MI:SS'),'DD/MM/YYYY')  AS xAxe from d"
                                + typeId + "  where TO_DATE (UPLOAD_DATE,'YYYY-MM-DD HH24:MI:SS') between TO_DATE('"
                                + yearStart.toString() + " 00:00:00','YYYY-MM-DD HH24:MI:SS')  and TO_DATE('"
                                + yearEnd.toString()
                                + " 00:00:00','YYYY-MM-DD HH24:MI:SS')   ) group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("mssql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) ct  from (select FORMAT(CAST(UPLOAD_DATE AS DATE),'dd/MM/yyyy')  AS xAxe from d"
                                + typeId + "  where CAST(UPLOAD_DATE AS DATE) between CAST('" + yearStart.toString()
                                + " ' AS DATE)  and CAST('" + yearEnd.toString()
                                + "' AS DATE)   ) as r group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("msql")) {

                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) ct  from (select DATE_FORMAT(UPLOAD_DATE,'%d/%m/%Y')   AS xAxe from d"
                                + typeId + " where STR_TO_DATE(UPLOAD_DATE,'%Y-%m-%d %T' ) between STR_TO_DATE('"
                                + yearStart.toString() + " ' ,'%Y-%m-%d %T' )  and  STR_TO_DATE('" + yearEnd.toString()
                                + "','%Y-%m-%d  %T')   ) as r group by xAxe  order by xAxe ")
                        .getResultList();
            }
            System.out.println("getItemsByType -- day  : " + data);
            int yEnd = yearEnd.getYear();
            int weekEnd = yearEnd.getMonthValue();
            int dayEnd = yearEnd.getDayOfMonth();
            int yStart = yearStart.getYear();
            int weekStart = yearStart.getMonthValue();
            int dayStart = yearStart.getDayOfMonth();
            names.add((dayStart < 10 ? "0" : "") + dayStart + "/" + (weekStart < 10 ? "0" : "") + weekStart + "/"
                    + yStart);
            counts.add("0");
            System.out.println(weekStart + "/" + yStart);
            ;
            while (yEnd != yStart || weekEnd != weekStart || dayEnd != dayStart) {
                System.out.println((dayStart < 10 ? "0" : "") + dayStart + "/" + (weekStart < 10 ? "0" : "") + weekStart
                        + "/" + yStart);
                yearStart = yearStart.plusDays(1L);
                yStart = yearStart.getYear();
                weekStart = yearStart.getMonthValue();
                dayStart = yearStart.getDayOfMonth();
                names.add((dayStart < 10 ? "0" : "") + dayStart + "/" + (weekStart < 10 ? "0" : "") + weekStart + "/"
                        + yStart);
                counts.add(String.valueOf("0"));

            }

            for (Object[] object : data) {
                System.out.println(object[0] + " ============= ");
                for (int i = 0; i < names.size(); i++) {

                    if (names.get(i).equals(object[0])) {

                        counts.set(i, String.valueOf(object[1]));

                    }

                }

            }

        }

        HashMap<String, List<String>> dataRet = new HashMap<>();

        dataRet.put("counts", counts);
        dataRet.put("names", names);

        return dataRet;
    }

    public HashMap<String, List<String>> getItemsByTypeGroup(Long typeId, String showBy, LocalDate yearStart,
            LocalDate yearEnd) {
        HashMap<String, List<String>> data = new HashMap<>();
        List<HashMap<String, List<String>>> hashMap = new ArrayList<HashMap<String, List<String>>>();

        ElementTypeGroup gr;
        if (Objects.nonNull(typeId)) {

            log.info("fvnvkmcbm ù :{}", typeId);

            gr = elementTypeGroupService.getOne(typeId);

            int i = 0;
            for (DocumentType dc : gr.getDocumentTypes()) {

                hashMap.add(getItemsByType(dc.getId(), showBy, yearStart, yearEnd));
                i++;

            }

        } else {

            Page<DocumentType> dt = elementTypeGroupService.getAll(PageRequest.of(0, 1000000000));
            ;
            int i = 0;
            for (DocumentType dc : dt.getContent()) {

                hashMap.add(getItemsByType(dc.getId(), showBy, yearStart, yearEnd));
                i++;

            }

            // System.out.println(i);

        }

        List<Long> counts = new ArrayList<Long>();
        List<String> names = hashMap.get(0).get("names");
        data.put("names", hashMap.get(0).get("names"));

        System.out.println("siiiiiize 1 ! !! " + names.size());
        for (String string : names) {

            counts.add(0L);

        }

        for (HashMap<String, List<String>> hashMap2 : hashMap) {

            int i = 0;
            System.out.println("siiiiiize  ! !! " + hashMap2.get("counts").size());
            for (String string : hashMap2.get("counts")) {

                counts.set(i, counts.get(i) + Long.valueOf(string));
                i++;
            }

        }

        List<String> cStr = new ArrayList<String>();

        for (Long string : counts) {

            cStr.add(string + "");
        }
        data.put("counts", cStr);

        return data;

    }

    public HashMap<String, List<String>> getLastQuaData() {

        LocalDate sDate = LocalDate.now().minusMonths(4);

        LocalDate eDate = LocalDate.now();

        HashMap<String, List<String>> lastData = getItemsByTypeGroup(null, "month", sDate, eDate);

        List<String> counts = lastData.get("counts");
        List<String> xhanges = new ArrayList<String>();
        xhanges.add("0");
        Long v1 = null;
        for (String string : counts) {

            System.out.println(string + "     " + v1);
            System.out.println(string + " 	 " + v1);
            if (Objects.nonNull(v1)) {

                // float r = (float) ((Math.abs((v1 - Long.valueOf(string))) / ((v1 +
                // Long.valueOf(string) )/2 ))*100L);
                Long r;
                if (v1 != 0)
                    r = 100 - (Long.valueOf(string) * 100) / v1;
                else
                    r = Long.valueOf(string) * 100L;
                xhanges.add((v1 > Long.valueOf(string) ? "-" : (r == 0 ? "" : "+")) + String.valueOf(Math.abs(r)));
                System.out.println(String.valueOf(r));

            }

            v1 = Long.valueOf(string);

        }

        lastData.put("changes", xhanges);

        return lastData;

    }

    public HashMap<String, List<String>> getFluxTraiter(Long userId, String showBy, LocalDate yearStart,
            LocalDate yearEnd, Long type) {

        List<String> counts = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> counts2 = new ArrayList<>();

        if (showBy.equals("year")) {

            List<Object[]> data = null;
            List<Object[]> data2 = null;
            List<Object[]> data3 = null;
            if (selectedDb.equals("orcl")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT to_char(ce.DATE_TRAITEMENT,'YYYY') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId + " and  folder_type = " + type
                                + "   and ce.etat=1 and ce.DATE_TRAITEMENT between TO_DATE('" + yearStart.toString()
                                + " 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('" + yearEnd.toString()
                                + "','YYYY-MM-DD HH24:MI:SS'))   group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT to_char(ce.DATE_TRAITEMENT,'YYYY') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini'and uge.user_id="
                                + userId
                                + " and  ce.DATE_TRAITEMENT > ce.date_fin  and ce.etat=1 and ce.DATE_TRAITEMENT between TO_DATE('"
                                + yearStart.toString() + " 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('"
                                + yearEnd.toString() + "','YYYY-MM-DD HH24:MI:SS') )   group by xAxe  order by xAxe")
                        .getResultList();

                data3 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT to_char(ce.DATE_TRAITEMENT,'YYYY') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini'and uge.user_id="
                                + userId
                                + " and  ce.DATE_TRAITEMENT > ce.date_fin and ce.date_debut is not null and ce.etat=0and ce.DATE_TRAITEMENT between TO_DATE('"
                                + yearStart.toString() + " 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('"
                                + yearEnd.toString() + "','YYYY-MM-DD HH24:MI:SS') )   group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("mssql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT FORMAT(ce.DATE_TRAITEMENT,'yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId + "  and  folder_type = " + type
                                + " and ce.etat=1 and ce.DATE_TRAITEMENT between CAST('" + yearStart.toString()
                                + "' AS DATE) and CAST ('" + yearEnd.toString()
                                + "' AS DATE)) as t   group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT FORMAT(ce.DATE_TRAITEMENT,'yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini'and uge.user_id="
                                + userId
                                + " and  ce.DATE_TRAITEMENT > ce.date_fin  and ce.etat=1 and ce.DATE_TRAITEMENT between CAST('"
                                + yearStart.toString() + "' AS DATE) and CAST ('" + yearEnd.toString()
                                + "' AS DATE) ) as t  group by xAxe  order by xAxe")
                        .getResultList();
                data3 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT FORMAT(ce.DATE_TRAITEMENT,'yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini'and uge.user_id="
                                + userId
                                + " and  ce.DATE_TRAITEMENT > ce.date_fin and ce.date_debut is not null and ce.etat=0 and ce.DATE_TRAITEMENT between CAST('"
                                + yearStart.toString() + "' AS DATE) and CAST ('" + yearEnd.toString()
                                + "' AS DATE) ) as t  group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("pgsql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT TO_CHAR(ce.DATE_TRAITEMENT,'yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId + " and  folder_type = " + type
                                + "  and ce.etat=1 and ce.DATE_TRAITEMENT >= TO_DATE('" + yearStart.toString()
                                + "','yyyy-mm-dd') and ce.DATE_TRAITEMENT <= TO_DATE('" + yearEnd.toString()
                                + "','yyyy-mm-dd')) as t   group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT TO_CHAR(ce.DATE_TRAITEMENT,'yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini'and uge.user_id="
                                + userId
                                + " and  ce.DATE_TRAITEMENT > ce.date_fin  and ce.etat=1 and ce.DATE_TRAITEMENT >= TO_DATE('"
                                + yearStart.toString() + "','yyyy-mm-dd') and ce.DATE_TRAITEMENT <= TO_DATE('"
                                + yearEnd.toString() + "','yyyy-mm-dd') ) as t  group by xAxe  order by xAxe")
                        .getResultList();
                data3 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT TO_CHAR(ce.DATE_TRAITEMENT,'yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini'and uge.user_id="
                                + userId
                                + " and  ce.DATE_TRAITEMENT > ce.date_fin and ce.date_debut is not null and ce.etat=0 and ce.DATE_TRAITEMENT >= TO_DATE('"
                                + yearStart.toString() + "','yyyy-mm-dd') and ce.DATE_TRAITEMENT <= TO_DATE('"
                                + yearEnd.toString() + "','yyyy-mm-dd') ) as t  group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("msql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT DATE_FORMAT(ce.DATE_TRAITEMENT, '%Y') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId + " and  folder_type = " + type
                                + "  and ce.etat=1 and ce.DATE_TRAITEMENT >= STR_TO_DATE('" + yearStart.toString()
                                + "', '%Y-%m-%d')  and ce.DATE_TRAITEMENT <= STR_TO_DATE('" + yearEnd.toString()
                                + "', '%Y-%m-%d') )as t   group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT DATE_FORMAT(ce.DATE_TRAITEMENT, '%Y')  xAxe from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini'and uge.user_id="
                                + userId
                                + " and  ce.DATE_TRAITEMENT > ce.date_fin  and ce.etat=1 and ce.DATE_TRAITEMENT >= STR_TO_DATE('"
                                + yearStart.toString() + "', '%Y-%m-%d')and ce.DATE_TRAITEMENT <= STR_TO_DATE('"
                                + yearEnd.toString() + "', '%Y-%m-%d') )as t  group by xAxe  order by xAxe")
                        .getResultList();
                data3 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT DATE_FORMAT(ce.DATE_TRAITEMENT, '%Y') xAxe  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini'and uge.user_id="
                                + userId
                                + " and  ce.DATE_TRAITEMENT > ce.date_fin and ce.date_debut is not null and ce.etat=0 and ce.DATE_TRAITEMENT >= STR_TO_DATE('"
                                + yearStart.toString() + "', '%Y-%m-%d') and ce.DATE_TRAITEMENT <= STR_TO_DATE('"
                                + yearEnd.toString() + "', '%Y-%m-%d') )as t  group by xAxe  order by xAxe")
                        .getResultList();
            }

            int yEnd = yearEnd.getYear();
            int yStart = yearStart.getYear();
            names.add(yStart + "");
            counts.add("0");
            counts2.add("0");
            while (yEnd != yStart) {

                yearStart = yearStart.plusYears(1L);
                yStart = yearStart.getYear();
                System.out.println("" + yStart);
                names.add(String.valueOf(yStart));
                counts.add(String.valueOf("0"));
                counts2.add(String.valueOf("0"));

            }

            for (Object[] object : data) {

                for (int i = 0; i < names.size(); i++) {

                    if (names.get(i).equals(object[0])) {

                        counts.set(i, String.valueOf(object[1]));

                    }

                }

            }

            for (Object[] object : data2) {

                for (int i = 0; i < names.size(); i++) {

                    if (names.get(i).equals(object[0])) {

                        counts2.set(i, String.valueOf(object[1]));

                    }

                }

            }
        } else if (showBy.equals("week")) {

            List<Object[]> data = null;
            List<Object[]> data2 = null;

            if (selectedDb.equals("orcl")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT to_char(ce.DATE_TRAITEMENT,'WW/YYYY') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId + " and  folder_type = " + type
                                + "   and ce.etat=1 and ce.DATE_TRAITEMENT between TO_DATE('" + yearStart.toString()
                                + " 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('" + yearEnd.toString()
                                + "','YYYY-MM-DD HH24:MI:SS')    ) group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT to_char(ce.DATE_TRAITEMENT,'WW/YYYY') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId + " and  folder_type = " + type
                                + "  and  ce.DATE_TRAITEMENT > ce.date_fin  and ce.etat=1 and ce.DATE_TRAITEMENT between TO_DATE('"
                                + yearStart.toString() + " 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('"
                                + yearEnd.toString() + "','YYYY-MM-DD HH24:MI:SS')    ) group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("mssql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT  CONCAT(DATENAME (WEEK, ce.DATE_TRAITEMENT)-1,'/', FORMAT(ce.DATE_TRAITEMENT,'yyyy')) xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId + " and  folder_type = " + type
                                + "    and ce.etat=1 and ce.DATE_TRAITEMENT between CAST('" + yearStart.toString()
                                + "' AS DATE) and CAST ('" + yearEnd.toString()
                                + "' AS DATE)    ) as t group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT CONCAT(DATENAME (WEEK, ce.DATE_TRAITEMENT)-1,'/', FORMAT(ce.DATE_TRAITEMENT,'yyyy'))  xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId
                                + " and  ce.DATE_TRAITEMENT > ce.date_fin  and ce.etat=1 and ce.DATE_TRAITEMENT between CAST('"
                                + yearStart.toString() + "' AS DATE) and CAST ('" + yearEnd.toString()
                                + "' AS DATE)    ) as t group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("pgsql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT  CONCAT(DATE_PART('WEEK', ce.DATE_TRAITEMENT),'/', TO_CHAR(ce.DATE_TRAITEMENT,'yyyy')) xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId + " and  folder_type = " + type
                                + "   and ce.etat=1 and ce.DATE_TRAITEMENT >= TO_DATE('" + yearStart.toString()
                                + "','yyyy-mm-dd') and  ce.DATE_TRAITEMENT < TO_DATE('" + yearEnd.toString()
                                + "' ,'yyyy-mm-dd')) as t group by xAxe order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT  CONCAT(DATE_PART('WEEK', ce.DATE_TRAITEMENT),'/', TO_CHAR(ce.DATE_TRAITEMENT,'yyyy')) xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId
                                + " and  DATE_PART('day',ce.DATE_TRAITEMENT - ce.date_fin)>0  and ce.etat=1 and ce.DATE_TRAITEMENT >= TO_DATE('"
                                + yearStart.toString() + "','yyyy-mm-dd') and ce.DATE_TRAITEMENT < TO_DATE('"
                                + yearEnd.toString() + "' ,'yyyy-mm-dd')) as t group by xAxe order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("msql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT  CONCAT(WEEK(ce.DATE_TRAITEMENT),'/', DATE_FORMAT(ce.DATE_TRAITEMENT,'%Y')) xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId + " and  folder_type = " + type
                                + "  and ce.etat=1 and ce.DATE_TRAITEMENT >= STR_TO_DATE('" + yearStart.toString()
                                + "', '%Y-%m-%d') and  ce.DATE_TRAITEMENT < STR_TO_DATE('" + yearEnd.toString()
                                + "', '%Y-%m-%d') )as t group by xAxe order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT  CONCAT(WEEK(ce.DATE_TRAITEMENT),'/', DATE_FORMAT(ce.DATE_TRAITEMENT,'%Y')) xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId + "   and ce.etat=1 and ce.DATE_TRAITEMENT >= STR_TO_DATE('"
                                + yearStart.toString() + "', '%Y-%m-%d') and ce.DATE_TRAITEMENT < STR_TO_DATE('"
                                + yearEnd.toString() + "', '%Y-%m-%d') )as t group by xAxe order by xAxe")
                        .getResultList();
            }
//			 List<Object[]>  data3 =  em.createNativeQuery("select xAxe, count(xAxe) from (SELECT to_char(ce.DATE_TRAITEMENT,'WW/YYYY') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="+userId+" and  ce.DATE_TRAITEMENT > ce.date_fin and ce.date_debut is not null and ce.etat=0and ce.DATE_TRAITEMENT between TO_DATE('"+ yearStart.toString() +" 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('"+yearEnd.toString()+"','YYYY-MM-DD HH24:MI:SS') )   group by xAxe  order by xAxe").getResultList();

            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int yEnd = yearEnd.getYear();
            int weekEnd = yearEnd.get(weekFields.weekOfWeekBasedYear());
            int yStart = yearStart.getYear();
            int weekStart = yearStart.get(weekFields.weekOfWeekBasedYear());
            names.add((weekStart < 10 ? "0" : "") + weekStart + "/" + yStart);
            counts.add("0");
            counts2.add("0");
            System.out.println(weekStart + "/" + yStart);
            ;
            while (yEnd != yStart || weekEnd != weekStart) {
                System.out.println(weekStart + "/" + yStart);
                ;
                yearStart = yearStart.plusWeeks(1L);
                yStart = yearStart.getYear();
                weekStart = yearStart.get(weekFields.weekOfWeekBasedYear());
                System.out.println("" + weekStart + "/" + yStart);
                names.add(String.valueOf(weekStart < 10 ? "0" : "") + weekStart + "/" + yStart);
                counts.add(String.valueOf("0"));
                counts2.add(String.valueOf("0"));

            }

            for (Object[] object : data) {
                System.out.println(object[0] + " ============= ");
                for (int i = 0; i < names.size(); i++) {

                    if (names.get(i).equals(object[0])) {

                        counts.set(i, String.valueOf(object[1]));

                    }

                }

            }

            for (Object[] object : data2) {
                System.out.println(object[0] + " ============= ");
                for (int i = 0; i < names.size(); i++) {

                    if (names.get(i).equals(object[0])) {

                        counts2.set(i, String.valueOf(object[1]));

                    }

                }

            }

        } else if (showBy.equals("day")) {

            List<Object[]> data = null;
            List<Object[]> data2 = null;

            if (selectedDb.equals("orcl")) {

                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT to_char(ce.DATE_TRAITEMENT,'DD/MM/YYYY') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId + " and  folder_type = " + type
                                + "   and ce.etat=1 and ce.DATE_TRAITEMENT between TO_DATE('" + yearStart.toString()
                                + " 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('" + yearEnd.toString()
                                + "','YYYY-MM-DD HH24:MI:SS')    ) group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT to_char(ce.DATE_TRAITEMENT,'DD/MM/YYYY') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId + "   and ce.etat=1 and ce.DATE_TRAITEMENT between TO_DATE('"
                                + yearStart.toString() + " 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('"
                                + yearEnd.toString() + "','YYYY-MM-DD HH24:MI:SS')    ) group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("mssql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT FORMAT(ce.DATE_TRAITEMENT,'dd/MM/yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId + "  and  folder_type = " + type
                                + " and ce.etat=1 and ce.DATE_TRAITEMENT between CAST('" + yearStart.toString()
                                + " ' AS DATE) and CAST('" + yearEnd.toString()
                                + "' AS DATE)    ) as t group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT FORMAT(ce.DATE_TRAITEMENT,'dd/MM/yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId + "   and ce.etat=1 and ce.DATE_TRAITEMENT between CAST('"
                                + yearStart.toString() + " ' AS DATE) and CAST('" + yearEnd.toString()
                                + "' AS DATE)     ) as t group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("pgsql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT TO_CHAR(ce.DATE_TRAITEMENT,'dd/MM/yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId + " and  folder_type = " + type
                                + "  and ce.etat=1 and ce.DATE_TRAITEMENT between CAST('" + yearStart.toString()
                                + " ' AS DATE) and CAST('" + yearEnd.toString()
                                + "' AS DATE)    ) as t group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT TO_CHAR(ce.DATE_TRAITEMENT,'dd/MM/yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId
                                + " and  DATE_PART('day',ce.DATE_TRAITEMENT - ce.date_fin )>0  and ce.etat=1 and ce.DATE_TRAITEMENT >= TO_DATE('"
                                + yearStart.toString() + " ','yyyy-mm-dd') and ce.DATE_TRAITEMENT <= TO_DATE('"
                                + yearEnd.toString() + "','yyyy-mm-dd')   ) as t group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("msql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT DATE_FORMAT(ce.DATE_TRAITEMENT, '%d/%m/%Y') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId + "  and  folder_type = " + type
                                + "  and ce.etat=1 and ce.DATE_TRAITEMENT between STR_TO_DATE('" + yearStart.toString()
                                + "', '%Y-%m-%d')  and STR_TO_DATE('" + yearEnd.toString()
                                + "', '%Y-%m-%d') )as t group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT DATE_FORMAT(ce.DATE_TRAITEMENT, '%d/%m/%Y') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId
                                + " and  ce.DATE_TRAITEMENT - ce.date_fin >0  and ce.etat=1 and ce.DATE_TRAITEMENT >= STR_TO_DATE('"
                                + yearStart.toString() + "', '%Y-%m-%d') and ce.DATE_TRAITEMENT <= STR_TO_DATE('"
                                + yearEnd.toString() + "', '%Y-%m-%d') )as t group by xAxe  order by xAxe")
                        .getResultList();
            }

            int yEnd = yearEnd.getYear();
            int weekEnd = yearEnd.getMonthValue();
            int dayEnd = yearEnd.getDayOfMonth();
            int yStart = yearStart.getYear();
            int weekStart = yearStart.getMonthValue();
            int dayStart = yearStart.getDayOfMonth();
            names.add((dayStart < 10 ? "0" : "") + dayStart + "/" + (weekStart < 10 ? "0" : "") + weekStart + "/"
                    + yStart);
            counts.add("0");
            counts2.add("0");
            System.out.println(weekStart + "/" + yStart);
            ;
            while (yEnd != yStart || weekEnd != weekStart || dayEnd != dayStart) {
                System.out.println((dayStart < 10 ? "0" : "") + dayStart + "/" + (weekStart < 10 ? "0" : "") + weekStart
                        + "/" + yStart);
                yearStart = yearStart.plusDays(1L);
                yStart = yearStart.getYear();
                weekStart = yearStart.getMonthValue();
                dayStart = yearStart.getDayOfMonth();
                names.add((dayStart < 10 ? "0" : "") + dayStart + "/" + (weekStart < 10 ? "0" : "") + weekStart + "/"
                        + yStart);
                counts.add(String.valueOf("0"));

                counts2.add(String.valueOf("0"));

            }

            for (Object[] object : data) {
                System.out.println(object[0] + " ============= ");
                for (int i = 0; i < names.size(); i++) {

                    if (names.get(i).equals(object[0])) {

                        counts.set(i, String.valueOf(object[1]));

                    }

                }

            }
            for (Object[] object : data2) {
                System.out.println(object[0] + " ============= ");
                for (int i = 0; i < names.size(); i++) {

                    if (names.get(i).equals(object[0])) {

                        counts2.set(i, String.valueOf(object[1]));

                    }

                }

            }

        } else if (showBy.equals("month")) {

            List<Object[]> data = null;
            List<Object[]> data2 = null;

            if (selectedDb.equals("orcl")) {

                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT to_char(ce.DATE_TRAITEMENT,'MM/YYYY') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId + "  and  folder_type = " + type
                                + " and ce.etat=1 and ce.DATE_TRAITEMENT between TO_DATE('" + yearStart.toString()
                                + " 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('" + yearEnd.toString()
                                + "','YYYY-MM-DD HH24:MI:SS')    ) group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT to_char(ce.DATE_TRAITEMENT,'MM/YYYY') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId
                                + " and  ce.DATE_TRAITEMENT > ce.date_fin  and ce.etat=1 and ce.DATE_TRAITEMENT between TO_DATE('"
                                + yearStart.toString() + " 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('"
                                + yearEnd.toString() + "','YYYY-MM-DD HH24:MI:SS')    ) group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("mssql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT FORMAT(ce.DATE_TRAITEMENT,'MM/yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId + "  and  folder_type = " + type
                                + " and ce.etat=1 and ce.DATE_TRAITEMENT between CAST('" + yearStart.toString()
                                + " ' AS DATE) and CAST('" + yearEnd.toString()
                                + "' AS DATE)    ) as t group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT FORMAT(ce.DATE_TRAITEMENT,'MM/yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId
                                + " and  ce.DATE_TRAITEMENT > ce.date_fin  and ce.etat=1 and ce.DATE_TRAITEMENT between CAST('"
                                + yearStart.toString() + " ' AS DATE) and CAST('" + yearEnd.toString()
                                + "' AS DATE)     ) as t group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("pgsql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT TO_CHAR(ce.DATE_TRAITEMENT,'MM/yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId + " and  folder_type = " + type
                                + " and ce.etat=1 and ce.DATE_TRAITEMENT >= TO_DATE('" + yearStart.toString()
                                + " ','yyyy-mm-dd') and ce.DATE_TRAITEMENT <= TO_DATE('" + yearEnd.toString()
                                + "','yyyy-mm-dd')   ) as t group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT TO_CHAR(ce.DATE_TRAITEMENT,'MM/yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId
                                + " and  DATE_PART('day',ce.DATE_TRAITEMENT - ce.date_fin)>0  and ce.etat=1 and ce.DATE_TRAITEMENT >= TO_DATE('"
                                + yearStart.toString() + " ','yyyy-mm-dd') and ce.DATE_TRAITEMENT <= TO_DATE('"
                                + yearEnd.toString() + "','yyyy-mm-dd')   ) as t group by xAxe  order by xAxe")
                        .getResultList();

                System.out.println(data);
            } else if (selectedDb.equals("msql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT DATE_FORMAT(ce.DATE_TRAITEMENT, '%m/%Y') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId + " and  folder_type = " + type
                                + "  and ce.etat=1 and ce.DATE_TRAITEMENT >= STR_TO_DATE('" + yearStart.toString()
                                + "', '%Y-%m-%d') and ce.DATE_TRAITEMENT <= STR_TO_DATE('" + yearEnd.toString()
                                + "', '%Y-%m-%d')   ) as t group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT DATE_FORMAT(ce.DATE_TRAITEMENT, '%m/%Y') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId
                                + " and ce.DATE_TRAITEMENT - ce.date_fin >0  and ce.etat=1 and ce.DATE_TRAITEMENT >= STR_TO_DATE('"
                                + yearStart.toString() + "', '%Y-%m-%d') and ce.DATE_TRAITEMENT <= STR_TO_DATE('"
                                + yearEnd.toString() + "', '%Y-%m-%d') ) as t group by xAxe  order by xAxe")
                        .getResultList();
            }

            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int yEnd = yearEnd.getYear();
            int weekEnd = yearEnd.getMonthValue();
            int yStart = yearStart.getYear();
            int weekStart = yearStart.getMonthValue();
            names.add((weekStart < 10 ? "0" : "") + weekStart + "/" + yStart);
            counts.add("0");
            counts2.add("0");
            System.out.println(weekStart + "/" + yStart);
            ;
            while (yEnd != yStart || weekEnd != weekStart) {
                System.out.println(weekStart + "/" + yStart);
                ;
                yearStart = yearStart.plusMonths(1L);
                yStart = yearStart.getYear();
                weekStart = yearStart.getMonthValue();
                System.out.println("" + weekStart + "/" + yStart);
                names.add(String.valueOf(weekStart < 10 ? "0" : "") + weekStart + "/" + yStart);
                counts.add(String.valueOf("0"));
                counts2.add(String.valueOf("0"));

            }

            for (Object[] object : data) {
                System.out.println(object[0] + " ============= ");
                for (int i = 0; i < names.size(); i++) {

                    if (names.get(i).equals(object[0])) {

                        counts.set(i, String.valueOf(object[1]));

                    }

                }

            }

            for (Object[] object : data2) {
                System.out.println(object[0] + " ============= ");
                for (int i = 0; i < names.size(); i++) {

                    if (names.get(i).equals(object[0])) {

                        counts2.set(i, String.valueOf(object[1]));

                    }

                }

            }
        }

        HashMap<String, List<String>> dataRet = new HashMap<>();

        dataRet.put("counts", counts);
        dataRet.put("counts2", counts2);
        dataRet.put("names", names);

        return dataRet;

    }

    public UserDashboardSettings updateCreateUserSettings(User user, UserDashboardSettings dashboardSettings) {

        UserDashboardSettings uDS = settingsRepository.findById(user.getUserId())
                .orElse(new UserDashboardSettings(user.getUserId(), 3L, null, "month", "month", 3L, "month", "month",
                        3L, "month", "month", 3L, "month", "month"));

        if (Objects.nonNull(dashboardSettings)) {

            if (Objects.nonNull(dashboardSettings.getHierarchPeriod())) {
                uDS.setHierarchPeriod(dashboardSettings.getHierarchPeriod());

            }
            if (Objects.nonNull(dashboardSettings.getHierarchUser())) {
                uDS.setHierarchUser(dashboardSettings.getHierarchUser());

            }
            if (Objects.nonNull(dashboardSettings.getHierarchUnit())) {
                uDS.setHierarchUnit(dashboardSettings.getHierarchUnit());

            }

            if (Objects.nonNull(dashboardSettings.getHierarchShowBy())) {
                uDS.setHierarchShowBy(dashboardSettings.getHierarchShowBy());

            }

            if (Objects.nonNull(dashboardSettings.getDonePeriod())) {
                uDS.setDonePeriod(dashboardSettings.getDonePeriod());

            }
            if (Objects.nonNull(dashboardSettings.getDoneUnit())) {
                uDS.setDoneUnit(dashboardSettings.getDoneUnit());

            }

            if (Objects.nonNull(dashboardSettings.getDoneShowBy())) {
                uDS.setDoneShowBy(dashboardSettings.getDoneShowBy());

            }

            if (Objects.nonNull(dashboardSettings.getItemsPeriod())) {
                uDS.setItemsPeriod(dashboardSettings.getItemsPeriod());

            }
            if (Objects.nonNull(dashboardSettings.getItemsUnit())) {
                uDS.setItemsUnit(dashboardSettings.getItemsUnit());

            }

            if (Objects.nonNull(dashboardSettings.getItemsShowBy())) {
                uDS.setItemsShowBy(dashboardSettings.getItemsShowBy());

            }

            if (Objects.nonNull(dashboardSettings.getGrPeriod())) {
                uDS.setGrPeriod(dashboardSettings.getGrPeriod());

            }
            if (Objects.nonNull(dashboardSettings.getGrUnit())) {
                uDS.setGrUnit(dashboardSettings.getGrUnit());

            }

            if (Objects.nonNull(dashboardSettings.getGrShowBy())) {
                uDS.setGrShowBy(dashboardSettings.getGrShowBy());

            }

            uDS = settingsRepository.saveAndFlush(uDS);
        }

        return uDS;
    }

    public HashMap<String, List<String>> getFluxStill(Long userId, String showBy, LocalDate yearStart,
            LocalDate yearEnd, Long type) {

        List<String> counts = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> counts2 = new ArrayList<>();

        if (showBy.equals("year")) {

            List<Object[]> data = null;
            List<Object[]> data2 = null;
            List<Object[]> data3 = null;
            if (selectedDb.equals("orcl")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT to_char(ce.DATE_DEBUT,'YYYY') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                                + userId + " and  folder_type = " + type
                                + "   and ce.etat=0 and ce.DATE_DEBUT between TO_DATE('" + yearStart.toString()
                                + " 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('" + yearEnd.toString()
                                + "','YYYY-MM-DD HH24:MI:SS'))   group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT to_char(ce.DATE_DEBUT,'YYYY') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                                + userId
                                + " and  ce.DATE_DEBUT > ce.date_fin  and ce.etat=1 and ce.DATE_DEBUT between TO_DATE('"
                                + yearStart.toString() + " 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('"
                                + yearEnd.toString() + "','YYYY-MM-DD HH24:MI:SS') )   group by xAxe  order by xAxe")
                        .getResultList();

                data3 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT to_char(ce.DATE_TRAITEMENT,'YYYY') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini'and uge.user_id="
                                + userId
                                + " and  ce.DATE_TRAITEMENT > ce.date_fin and ce.date_debut is not null and ce.etat=0and ce.DATE_TRAITEMENT between TO_DATE('"
                                + yearStart.toString() + " 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('"
                                + yearEnd.toString() + "','YYYY-MM-DD HH24:MI:SS') )   group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("mssql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT FORMAT(ce.DATE_DEBUT,'yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id  and uge.user_id="
                                + userId + "  and  folder_type = " + type
                                + " and ce.etat=0 and ce.DATE_DEBUT between CAST('" + yearStart.toString()
                                + "' AS DATE) and CAST ('" + yearEnd.toString()
                                + "' AS DATE)) as t   group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT FORMAT(ce.DATE_TRAITEMENT,'yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini'and uge.user_id="
                                + userId
                                + " and  ce.DATE_TRAITEMENT > ce.date_fin  and ce.etat=1 and ce.DATE_TRAITEMENT between CAST('"
                                + yearStart.toString() + "' AS DATE) and CAST ('" + yearEnd.toString()
                                + "' AS DATE) ) as t  group by xAxe  order by xAxe")
                        .getResultList();
                data3 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT FORMAT(ce.DATE_TRAITEMENT,'yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini'and uge.user_id="
                                + userId
                                + " and  ce.DATE_TRAITEMENT > ce.date_fin and ce.date_debut is not null and ce.etat=0 and ce.DATE_TRAITEMENT between CAST('"
                                + yearStart.toString() + "' AS DATE) and CAST ('" + yearEnd.toString()
                                + "' AS DATE) ) as t  group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("pgsql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT TO_CHAR(ce.DATE_DEBUT,'yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                                + userId + " and  folder_type = " + type
                                + "  and ce.etat=0 and ce.DATE_DEBUT >= TO_DATE('" + yearStart.toString()
                                + "','yyyy-mm-dd') and ce.DATE_DEBUT <= TO_DATE('" + yearEnd.toString()
                                + "','yyyy-mm-dd')) as t   group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT TO_CHAR(ce.DATE_TRAITEMENT,'yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini'and uge.user_id="
                                + userId
                                + " and  ce.DATE_TRAITEMENT > ce.date_fin  and ce.etat=1 and ce.DATE_TRAITEMENT >= TO_DATE('"
                                + yearStart.toString() + "','yyyy-mm-dd') and ce.DATE_TRAITEMENT <= TO_DATE('"
                                + yearEnd.toString() + "','yyyy-mm-dd') ) as t  group by xAxe  order by xAxe")
                        .getResultList();
                data3 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT TO_CHAR(ce.DATE_TRAITEMENT,'yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini'and uge.user_id="
                                + userId
                                + " and  ce.DATE_TRAITEMENT > ce.date_fin and ce.date_debut is not null and ce.etat=0 and ce.DATE_TRAITEMENT >= TO_DATE('"
                                + yearStart.toString() + "','yyyy-mm-dd') and ce.DATE_TRAITEMENT <= TO_DATE('"
                                + yearEnd.toString() + "','yyyy-mm-dd') ) as t  group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("msql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT DATE_FORMAT(ce.DATE_DEBUT, '%Y') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id  and uge.user_id="
                                + userId + " and  folder_type = " + type
                                + "  and ce.etat=0 and ce.DATE_DEBUT >= STR_TO_DATE('" + yearStart.toString()
                                + "', '%Y-%m-%d')  and ce.DATE_DEBUT <= STR_TO_DATE('" + yearEnd.toString()
                                + "', '%Y-%m-%d') )as t   group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT DATE_FORMAT(ce.DATE_TRAITEMENT, '%Y')  xAxe from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini'and uge.user_id="
                                + userId
                                + " and  ce.DATE_TRAITEMENT > ce.date_fin  and ce.etat=1 and ce.DATE_TRAITEMENT >= STR_TO_DATE('"
                                + yearStart.toString() + "', '%Y-%m-%d')and ce.DATE_TRAITEMENT <= STR_TO_DATE('"
                                + yearEnd.toString() + "', '%Y-%m-%d') )as t  group by xAxe  order by xAxe")
                        .getResultList();
                data3 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT DATE_FORMAT(ce.DATE_TRAITEMENT, '%Y') xAxe  from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini'and uge.user_id="
                                + userId
                                + " and  ce.DATE_TRAITEMENT > ce.date_fin and ce.date_debut is not null and ce.etat=0 and ce.DATE_TRAITEMENT >= STR_TO_DATE('"
                                + yearStart.toString() + "', '%Y-%m-%d') and ce.DATE_TRAITEMENT <= STR_TO_DATE('"
                                + yearEnd.toString() + "', '%Y-%m-%d') )as t  group by xAxe  order by xAxe")
                        .getResultList();
            }

            int yEnd = yearEnd.getYear();
            int yStart = yearStart.getYear();
            names.add(yStart + "");
            counts.add("0");
            counts2.add("0");
            while (yEnd != yStart) {

                yearStart = yearStart.plusYears(1L);
                yStart = yearStart.getYear();
                System.out.println("" + yStart);
                names.add(String.valueOf(yStart));
                counts.add(String.valueOf("0"));
                counts2.add(String.valueOf("0"));

            }

            for (Object[] object : data) {

                for (int i = 0; i < names.size(); i++) {

                    if (names.get(i).equals(object[0])) {

                        counts.set(i, String.valueOf(object[1]));

                    }

                }

            }

            for (Object[] object : data2) {

                for (int i = 0; i < names.size(); i++) {

                    if (names.get(i).equals(object[0])) {

                        counts2.set(i, String.valueOf(object[1]));

                    }

                }

            }
        } else if (showBy.equals("week")) {

            List<Object[]> data = null;
            List<Object[]> data2 = null;

            if (selectedDb.equals("orcl")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT to_char(ce.DATE_DEBUT,'WW/YYYY') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id  and uge.user_id="
                                + userId + " and  folder_type = " + type
                                + "   and ce.etat=0 and ce.DATE_DEBUT between TO_DATE('" + yearStart.toString()
                                + " 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('" + yearEnd.toString()
                                + "','YYYY-MM-DD HH24:MI:SS')    ) group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT to_char(ce.DATE_TRAITEMENT,'WW/YYYY') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId + " and  folder_type = " + type
                                + "  and  ce.DATE_TRAITEMENT > ce.date_fin  and ce.etat=1 and ce.DATE_TRAITEMENT between TO_DATE('"
                                + yearStart.toString() + " 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('"
                                + yearEnd.toString() + "','YYYY-MM-DD HH24:MI:SS')    ) group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("mssql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT  CONCAT(DATENAME (WEEK, ce.DATE_DEBUT)-1,'/', FORMAT(ce.DATE_TRAITEMENT,'yyyy')) xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id  and uge.user_id="
                                + userId + " and  folder_type = " + type
                                + "    and ce.etat=0 and ce.DATE_DEBUT between CAST('" + yearStart.toString()
                                + "' AS DATE) and CAST ('" + yearEnd.toString()
                                + "' AS DATE)    ) as t group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT CONCAT(DATENAME (WEEK, ce.DATE_TRAITEMENT)-1,'/', FORMAT(ce.DATE_TRAITEMENT,'yyyy'))  xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId
                                + " and  ce.DATE_TRAITEMENT > ce.date_fin  and ce.etat=1 and ce.DATE_TRAITEMENT between CAST('"
                                + yearStart.toString() + "' AS DATE) and CAST ('" + yearEnd.toString()
                                + "' AS DATE)    ) as t group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("pgsql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT  CONCAT(DATE_PART('WEEK', ce.DATE_DEBUT),'/', TO_CHAR(ce.DATE_DEBUT,'yyyy')) xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="
                                + userId + " and  folder_type = " + type
                                + "   and ce.etat=0 and ce.DATE_DEBUT >= TO_DATE('" + yearStart.toString()
                                + "','yyyy-mm-dd') and  ce.DATE_DEBUT < TO_DATE('" + yearEnd.toString()
                                + "' ,'yyyy-mm-dd')) as t group by xAxe order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT  CONCAT(DATE_PART('WEEK', ce.DATE_TRAITEMENT),'/', TO_CHAR(ce.DATE_TRAITEMENT,'yyyy')) xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId
                                + " and  DATE_PART('day',ce.DATE_TRAITEMENT - ce.date_fin)>0  and ce.etat=1 and ce.DATE_TRAITEMENT >= TO_DATE('"
                                + yearStart.toString() + "','yyyy-mm-dd') and ce.DATE_TRAITEMENT < TO_DATE('"
                                + yearEnd.toString() + "' ,'yyyy-mm-dd')) as t group by xAxe order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("msql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT  CONCAT(WEEK(ce.DATE_DEBUT),'/', DATE_FORMAT(ce.DATE_DEBUT,'%Y')) xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id  and uge.user_id="
                                + userId + " and  folder_type = " + type
                                + "  and ce.etat=0 and ce.DATE_DEBUT >= STR_TO_DATE('" + yearStart.toString()
                                + "', '%Y-%m-%d') and  ce.DATE_DEBUT < STR_TO_DATE('" + yearEnd.toString()
                                + "', '%Y-%m-%d') )as t group by xAxe order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT  CONCAT(WEEK(ce.DATE_TRAITEMENT),'/', DATE_FORMAT(ce.DATE_TRAITEMENT,'%Y')) xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId + "   and ce.etat=1 and ce.DATE_TRAITEMENT >= STR_TO_DATE('"
                                + yearStart.toString() + "', '%Y-%m-%d') and ce.DATE_TRAITEMENT < STR_TO_DATE('"
                                + yearEnd.toString() + "', '%Y-%m-%d') )as t group by xAxe order by xAxe")
                        .getResultList();
            }
//        List<Object[]>  data3 =  em.createNativeQuery("select xAxe, count(xAxe) from (SELECT to_char(ce.DATE_TRAITEMENT,'WW/YYYY') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and uge.user_id="+userId+" and  ce.DATE_TRAITEMENT > ce.date_fin and ce.date_debut is not null and ce.etat=0and ce.DATE_TRAITEMENT between TO_DATE('"+ yearStart.toString() +" 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('"+yearEnd.toString()+"','YYYY-MM-DD HH24:MI:SS') )   group by xAxe  order by xAxe").getResultList();

            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int yEnd = yearEnd.getYear();
            int weekEnd = yearEnd.get(weekFields.weekOfWeekBasedYear());
            int yStart = yearStart.getYear();
            int weekStart = yearStart.get(weekFields.weekOfWeekBasedYear());
            names.add((weekStart < 10 ? "0" : "") + weekStart + "/" + yStart);
            counts.add("0");
            counts2.add("0");
            System.out.println(weekStart + "/" + yStart);
            ;
            while (yEnd != yStart || weekEnd != weekStart) {
                System.out.println(weekStart + "/" + yStart);
                ;
                yearStart = yearStart.plusWeeks(1L);
                yStart = yearStart.getYear();
                weekStart = yearStart.get(weekFields.weekOfWeekBasedYear());
                System.out.println("" + weekStart + "/" + yStart);
                names.add(String.valueOf(weekStart < 10 ? "0" : "") + weekStart + "/" + yStart);
                counts.add(String.valueOf("0"));
                counts2.add(String.valueOf("0"));

            }

            for (Object[] object : data) {
                System.out.println(object[0] + " ============= ");
                for (int i = 0; i < names.size(); i++) {

                    if (names.get(i).equals(object[0])) {

                        counts.set(i, String.valueOf(object[1]));

                    }

                }

            }

            for (Object[] object : data2) {
                System.out.println(object[0] + " ============= ");
                for (int i = 0; i < names.size(); i++) {

                    if (names.get(i).equals(object[0])) {

                        counts2.set(i, String.valueOf(object[1]));

                    }

                }

            }

        } else if (showBy.equals("day")) {

            List<Object[]> data = null;
            List<Object[]> data2 = null;

            if (selectedDb.equals("orcl")) {

                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT to_char(ce.DATE_DEBUT,'DD/MM/YYYY') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id  and uge.user_id="
                                + userId + " and  folder_type = " + type
                                + "   and ce.etat=0 and ce.DATE_DEBUT between TO_DATE('" + yearStart.toString()
                                + " 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('" + yearEnd.toString()
                                + "','YYYY-MM-DD HH24:MI:SS')    ) group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT to_char(ce.DATE_TRAITEMENT,'DD/MM/YYYY') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId + "   and ce.etat=1 and ce.DATE_TRAITEMENT between TO_DATE('"
                                + yearStart.toString() + " 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('"
                                + yearEnd.toString() + "','YYYY-MM-DD HH24:MI:SS')    ) group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("mssql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT FORMAT(ce.DATE_DEBUT,'dd/MM/yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id  and uge.user_id="
                                + userId + "  and  folder_type = " + type
                                + " and ce.etat=0 and ce.DATE_DEBUT between CAST('" + yearStart.toString()
                                + " ' AS DATE) and CAST('" + yearEnd.toString()
                                + "' AS DATE)    ) as t group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT FORMAT(ce.DATE_TRAITEMENT,'dd/MM/yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId + "   and ce.etat=1 and ce.DATE_TRAITEMENT between CAST('"
                                + yearStart.toString() + " ' AS DATE) and CAST('" + yearEnd.toString()
                                + "' AS DATE)     ) as t group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("pgsql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT TO_CHAR(ce.DATE_DEBUT,'dd/MM/yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id  and uge.user_id="
                                + userId + " and  folder_type = " + type
                                + "  and ce.etat=0 and ce.DATE_DEBUT between CAST('" + yearStart.toString()
                                + " ' AS DATE) and CAST('" + yearEnd.toString()
                                + "' AS DATE)    ) as t group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT TO_CHAR(ce.DATE_TRAITEMENT,'dd/MM/yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId
                                + " and  DATE_PART('day',ce.DATE_TRAITEMENT - ce.date_fin )>0  and ce.etat=1 and ce.DATE_TRAITEMENT >= TO_DATE('"
                                + yearStart.toString() + " ','yyyy-mm-dd') and ce.DATE_TRAITEMENT <= TO_DATE('"
                                + yearEnd.toString() + "','yyyy-mm-dd')   ) as t group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("msql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT DATE_FORMAT(ce.DATE_DEBUT, '%d/%m/%Y') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id  and uge.user_id="
                                + userId + "  and  folder_type = " + type
                                + "  and ce.etat=0 and ce.DATE_DEBUT between STR_TO_DATE('" + yearStart.toString()
                                + "', '%Y-%m-%d')  and STR_TO_DATE('" + yearEnd.toString()
                                + "', '%Y-%m-%d') )as t group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT DATE_FORMAT(ce.DATE_TRAITEMENT, '%d/%m/%Y') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId
                                + " and  ce.DATE_TRAITEMENT - ce.date_fin >0  and ce.etat=1 and ce.DATE_TRAITEMENT >= STR_TO_DATE('"
                                + yearStart.toString() + "', '%Y-%m-%d') and ce.DATE_TRAITEMENT <= STR_TO_DATE('"
                                + yearEnd.toString() + "', '%Y-%m-%d') )as t group by xAxe  order by xAxe")
                        .getResultList();
            }

            int yEnd = yearEnd.getYear();
            int weekEnd = yearEnd.getMonthValue();
            int dayEnd = yearEnd.getDayOfMonth();
            int yStart = yearStart.getYear();
            int weekStart = yearStart.getMonthValue();
            int dayStart = yearStart.getDayOfMonth();
            names.add((dayStart < 10 ? "0" : "") + dayStart + "/" + (weekStart < 10 ? "0" : "") + weekStart + "/"
                    + yStart);
            counts.add("0");
            counts2.add("0");
            System.out.println(weekStart + "/" + yStart);
            ;
            while (yEnd != yStart || weekEnd != weekStart || dayEnd != dayStart) {
                System.out.println((dayStart < 10 ? "0" : "") + dayStart + "/" + (weekStart < 10 ? "0" : "") + weekStart
                        + "/" + yStart);
                yearStart = yearStart.plusDays(1L);
                yStart = yearStart.getYear();
                weekStart = yearStart.getMonthValue();
                dayStart = yearStart.getDayOfMonth();
                names.add((dayStart < 10 ? "0" : "") + dayStart + "/" + (weekStart < 10 ? "0" : "") + weekStart + "/"
                        + yStart);
                counts.add(String.valueOf("0"));

                counts2.add(String.valueOf("0"));

            }

            for (Object[] object : data) {
                System.out.println(object[0] + " ============= ");
                for (int i = 0; i < names.size(); i++) {

                    if (names.get(i).equals(object[0])) {

                        counts.set(i, String.valueOf(object[1]));

                    }

                }

            }
            for (Object[] object : data2) {
                System.out.println(object[0] + " ============= ");
                for (int i = 0; i < names.size(); i++) {

                    if (names.get(i).equals(object[0])) {

                        counts2.set(i, String.valueOf(object[1]));

                    }

                }

            }

        } else if (showBy.equals("month")) {

            List<Object[]> data = null;
            List<Object[]> data2 = null;

            if (selectedDb.equals("orcl")) {

                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT to_char(ce.DATE_DEBUT,'MM/YYYY') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id  and uge.user_id="
                                + userId + "  and  folder_type = " + type
                                + " and ce.etat=0 and ce.DATE_DEBUT between TO_DATE('" + yearStart.toString()
                                + " 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('" + yearEnd.toString()
                                + "','YYYY-MM-DD HH24:MI:SS')    ) group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT to_char(ce.DATE_TRAITEMENT,'MM/YYYY') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId
                                + " and  ce.DATE_TRAITEMENT > ce.date_fin  and ce.etat=1 and ce.DATE_TRAITEMENT between TO_DATE('"
                                + yearStart.toString() + " 00:00:00','YYYY-MM-DD HH24:MI:SS') and TO_DATE ('"
                                + yearEnd.toString() + "','YYYY-MM-DD HH24:MI:SS')    ) group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("mssql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT FORMAT(ce.DATE_DEBUT,'MM/yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id  and uge.user_id="
                                + userId + "  and  folder_type = " + type
                                + " and ce.etat=0 and ce.DATE_DEBUT between CAST('" + yearStart.toString()
                                + " ' AS DATE) and CAST('" + yearEnd.toString()
                                + "' AS DATE)    ) as t group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT FORMAT(ce.DATE_TRAITEMENT,'MM/yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId
                                + " and  ce.DATE_TRAITEMENT > ce.date_fin  and ce.etat=1 and ce.DATE_TRAITEMENT between CAST('"
                                + yearStart.toString() + " ' AS DATE) and CAST('" + yearEnd.toString()
                                + "' AS DATE)     ) as t group by xAxe  order by xAxe")
                        .getResultList();

            } else if (selectedDb.equals("pgsql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT TO_CHAR(ce.DATE_DEBUT,'MM/yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id  and uge.user_id="
                                + userId + " and  folder_type = " + type
                                + " and ce.etat=0 and ce.DATE_DEBUT >= TO_DATE('" + yearStart.toString()
                                + " ','yyyy-mm-dd') and ce.DATE_DEBUT <= TO_DATE('" + yearEnd.toString()
                                + "','yyyy-mm-dd')   ) as t group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT TO_CHAR(ce.DATE_TRAITEMENT,'MM/yyyy') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId
                                + " and  DATE_PART('day',ce.DATE_TRAITEMENT - ce.date_fin)>0  and ce.etat=1 and ce.DATE_TRAITEMENT >= TO_DATE('"
                                + yearStart.toString() + " ','yyyy-mm-dd') and ce.DATE_TRAITEMENT <= TO_DATE('"
                                + yearEnd.toString() + "','yyyy-mm-dd')   ) as t group by xAxe  order by xAxe")
                        .getResultList();

                System.out.println(data);
            } else if (selectedDb.equals("msql")) {
                data = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT DATE_FORMAT(ce.DATE_DEBUT, '%m/%Y') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id  and uge.user_id="
                                + userId + " and  folder_type = " + type
                                + "  and ce.etat=0 and ce.DATE_DEBUT >= STR_TO_DATE('" + yearStart.toString()
                                + "', '%Y-%m-%d') and ce.DATE_DEBUT <= STR_TO_DATE('" + yearEnd.toString()
                                + "', '%Y-%m-%d')   ) as t group by xAxe  order by xAxe")
                        .getResultList();
                data2 = em.createNativeQuery(
                        "select xAxe, count(xAxe) from (SELECT DATE_FORMAT(ce.DATE_TRAITEMENT, '%m/%Y') xAxe   from folder f, clone_etape ce, user_gestion_etape uge where  f.id = ce.courrier and ce.id = uge.clone_etape_id and f.finalise='fini' and uge.user_id="
                                + userId
                                + " and ce.DATE_TRAITEMENT - ce.date_fin >0  and ce.etat=1 and ce.DATE_TRAITEMENT >= STR_TO_DATE('"
                                + yearStart.toString() + "', '%Y-%m-%d') and ce.DATE_TRAITEMENT <= STR_TO_DATE('"
                                + yearEnd.toString() + "', '%Y-%m-%d') ) as t group by xAxe  order by xAxe")
                        .getResultList();
            }

            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int yEnd = yearEnd.getYear();
            int weekEnd = yearEnd.getMonthValue();
            int yStart = yearStart.getYear();
            int weekStart = yearStart.getMonthValue();
            names.add((weekStart < 10 ? "0" : "") + weekStart + "/" + yStart);
            counts.add("0");
            counts2.add("0");
            System.out.println(weekStart + "/" + yStart);
            ;
            while (yEnd != yStart || weekEnd != weekStart) {
                System.out.println(weekStart + "/" + yStart);
                ;
                yearStart = yearStart.plusMonths(1L);
                yStart = yearStart.getYear();
                weekStart = yearStart.getMonthValue();
                System.out.println("" + weekStart + "/" + yStart);
                names.add(String.valueOf(weekStart < 10 ? "0" : "") + weekStart + "/" + yStart);
                counts.add(String.valueOf("0"));
                counts2.add(String.valueOf("0"));

            }

            for (Object[] object : data) {
                System.out.println(object[0] + " ============= ");
                for (int i = 0; i < names.size(); i++) {

                    if (names.get(i).equals(object[0])) {

                        counts.set(i, String.valueOf(object[1]));

                    }

                }

            }

            for (Object[] object : data2) {
                System.out.println(object[0] + " ============= ");
                for (int i = 0; i < names.size(); i++) {

                    if (names.get(i).equals(object[0])) {

                        counts2.set(i, String.valueOf(object[1]));

                    }

                }

            }
        }

        HashMap<String, List<String>> dataRet = new HashMap<>();

        dataRet.put("counts", counts);
        dataRet.put("counts2", counts2);
        dataRet.put("names", names);

        return dataRet;

    }

    public  List<StatisticUser> getFluxTable(Long userId, String showBy, LocalDate yearStart,
            LocalDate yearEnd, Long type,Long unit) {

        List<User> users = userRepository.findByParent(userId);
        List<StatisticUser> result = new ArrayList<StatisticUser>();
        StatisticUser stats = new StatisticUser();       
        long totalDone = 0;
        long totalStill = 0;
        Double totalAvg = 0.0;
        long done = 0;
        long still = 0;
        Double avg = 0.0;

        for (User user : users) { 
            done = getDoneFlowCounts(user.getUserId(), type,yearStart,yearEnd).get("total");
            still = getCurrentFlowCounts(user.getUserId(), type,yearStart,yearEnd).get("total"); 
            avg = getDoneFlowsAvg(user.getUserId(), type,yearStart,yearEnd,unit).get("total");
            stats.setName(user.getFullName());
            stats.setDone(done);
            stats.setStill(still);
            stats.setAvg(avg);
            result.add(stats);
            stats = new StatisticUser();
            totalDone += done;
            totalStill += still;
            totalAvg += avg;
        }

        stats.setName("TOTAL");
        stats.setDone(totalDone);
        stats.setStill(totalStill);
        stats.setAvg(totalAvg);
        result.add(stats);
        stats = new StatisticUser();
        
        return result;
    }

}
