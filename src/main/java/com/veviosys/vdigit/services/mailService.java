package com.veviosys.vdigit.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;
import org.apache.tomcat.util.buf.StringUtils;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.transaction.Transactional;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import com.veviosys.vdigit.classes.CourrierInfo;
import com.veviosys.vdigit.classes.ScheduleEmailRequest;
import com.veviosys.vdigit.classes.ScheduleEmailResponse;
import com.veviosys.vdigit.classes.mailClass;
import com.veviosys.vdigit.emailConf.Genpw;
import com.veviosys.vdigit.models.Document;
import com.veviosys.vdigit.models.DocumentAttributeValue;
import com.veviosys.vdigit.models.DocumentFolder;
import com.veviosys.vdigit.models.Folder;
import com.veviosys.vdigit.models.Journal;
import com.veviosys.vdigit.models.MasterConfig;
import com.veviosys.vdigit.models.NotifMail;
import com.veviosys.vdigit.models.ProfilsAbsence;
import com.veviosys.vdigit.models.Track;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.models.receiver;
import com.veviosys.vdigit.models.zipMail;
import com.veviosys.vdigit.repositories.DocumentRepo;
import com.veviosys.vdigit.repositories.FolderRepo;
import com.veviosys.vdigit.repositories.JournalRepo;
import com.veviosys.vdigit.repositories.MasterConfigRepository;
import com.veviosys.vdigit.repositories.NotifMailRepo;
import com.veviosys.vdigit.repositories.ReceiverRepo;
import com.veviosys.vdigit.repositories.TrackRepo;
import com.veviosys.vdigit.repositories.UserRepository;
import com.veviosys.vdigit.repositories.zipMailRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class mailService {
    private static final Logger logger = LoggerFactory.getLogger(mailService.class);

    @Autowired
    FileStorageServiceImpl fileStorageService;
    @Value("${config.mail.mail}")
    private String username;
    @Value("${mail.courrier.depart.fin}")
    private String CourrierFinaliseDepart;
    @Value("${mail.courrier.notif.absence}")
    private String NotifyAbsence;
    @Value("${mail.courrier.notif.abandonne}")
    private String CourrierAbandonne;
    @Value("${mail.courrier.notif.absence.create}")
    private String NotifyAbsenceCreate;
    @Value("${mail.courrier.notif.return}")
    private String CourrierReturn;
    @Value("${mail.courrier.notif.traite}")
    private String CourrierTraiter;
    @Value("${config.mail.password}")
    private String password;
    @Value("${front.url}")
    private String frontUrl;
    @Value("${back.url}")
    private String backUrl;
    @Value("${test.config.notify}")
    private String email;
    @Autowired
    private Scheduler scheduler;
// 	/// MAIL CONFIG 
// 	config.mail.host=mail.cnops.org.ma
// config.mail.port=25
// config.mail.mail=documania.courrier@cnops.org.ma
// config.mail.password=Admin@123
    @Value("${config.mail.host}")
    private String hostConfig;
    @Value("${config.mail.port}")
    private String portConfig;
    @Value("${config.cert.path}")
    private String certPath;
    @Value("${mail.path}")
    private String url;

    @Autowired
    NotifMailRepo nmr;

    @Autowired
    TrackRepo trackedRepo;

    static final String ARRIVE = "arr";
    static final String DEPART = "dep";

    @Autowired
    UserRepository ur;
    @Autowired
    MasterConfigRepository configRepository;
    @Autowired
    DocumentRepo dr;
    @Autowired
    JournalRepo jr;
    @Autowired
    userService us;
    @Autowired
    ReceiverRepo rc;

    @Lazy
    @Autowired
    private FileStorageServiceImpl fileStorageServiceImpl;

    public User getmst() {
        CostumUserDetails user = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if (user.getUser().getMaster() == null) {
            return user.getUser();
        } else {
            return user.getUser().getMaster();
        }
    }

    // GET MAIL PROPRETIES
    public Properties getProp() {
        User u = getmst();
        NotifMail nm = nmr.findByUserUserId(u.getUserId());
        Properties props = new Properties();

        if (Objects.isNull(nm)) {
            props.put("mail.smtp.host", hostConfig);
            props.put("mail.smtp.port", portConfig);
            props.put("mail.smtp.socketFactory.port", portConfig);
            props.put("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");

        } else {
            props.put("mail.smtp.host", nm.getHost());
            props.put("mail.smtp.port", nm.getPort());
            props.put("mail.smtp.socketFactory.port", nm.getPort());
            props.put("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");

            username = nm.getMail();
            password = nm.getPassword();

        }

        return props;
    }

    private Properties getPropByUser(User master) {

        NotifMail nm = nmr.findByUserUserId(master.getUserId());
        Properties props = new Properties();

        if (Objects.isNull(nm)) {
            props.put("mail.smtp.host", hostConfig);
            props.put("mail.smtp.port", portConfig);
            props.put("mail.smtp.socketFactory.port", portConfig);
            props.put("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");

        } else {
            props.put("mail.smtp.host", nm.getHost());
            props.put("mail.smtp.port", nm.getPort());
            props.put("mail.smtp.socketFactory.port", nm.getPort());
            props.put("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");

            username = nm.getMail();
            password = nm.getPassword();

        }
        System.out.println("password :" + password);
        return props;
    }

    // SEND REGISTRATION MAIL (Credentials)
    public void send(Genpw gen, String gpw, String uname, String sx, String name)
            throws AddressException, MessagingException, IOException {
        Properties props = getProp();
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username, false));
        System.setProperty("mail.mime.charset", "UTF-8");
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(gen.getEmailTo()));
        msg.setSubject(gen.getSubject());
        String sexe;
        if (sx.equals("M")) {
            sexe = "Mr " + name;
        } else {
            sexe = "Mme " + name;

        }
        Path path = Paths.get(root + "/user.txt");
        Charset charset = Charset.forName("Windows-1252");
        String content = "", line;

        int year = Calendar.getInstance().get(Calendar.YEAR);
        String timeStamp = new SimpleDateFormat("EEEE dd MMMM yyyy | hh:mm:ss ", Locale.FRANCE).format(new Date());

        try (BufferedReader in = Files.newBufferedReader(path, charset)) {
            ;
            while ((line = in.readLine()) != null) {
                line = line.replace("xx#748__", gpw);
                line = line.replace("xx#44z__", sexe);
                line = line.replace("xx#74z__", uname);
                line = line.replace("#date#", timeStamp);
                line = line.replace("#year#", String.valueOf(year));
                line = line.replace("#url#", url);
                // .println(line);

                content += line;
            }
        }

        // msg.setContent("Bonjour "+sexe+",<br/><br/> Nous sommes ravis de vous
        // accueillir sur Documania Courrier. <br/><br/> "
        // +"Prière de trouver ci-dessous vos informations de connexion : <br/> Login :
        // "+uname+"<br/> Mot de passe : "
        // +gpw+"<br/><br/><br/>Cordialement.<br/> Documania Courrier","text/html");
        msg.setContent(content, "text/html");
        msg.setSentDate(new Date());
        Transport.send(msg);
        // .println(content);
    }

    public String root = System.getProperty("user.dir") + "/src/main/resources/mails";

    // SEND RECOVERY MAIL (Password)
    public void resetpw(Genpw gen, String gpw, String uname, String sx, String name)
            throws AddressException, MessagingException, IOException {
        User u = ur.findByEmailIgnoreCase(gen.getEmailTo());
        if (u.getMaster() != null) {
            u = u.getMaster();
        }
        System.setProperty("mail.mime.charset", "UTF-8");
        Properties props = getPropByUser(u);

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username, false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(gen.getEmailTo()));
        msg.setSubject(gen.getSubject());
        String sexe;
        if (sx.equals("M")) {
            sexe = "Mr " + name;
        } else {
            sexe = "Mme " + name;

        }
        Path path = Paths.get(root + "/respw.txt");
        Charset charset = Charset.forName("Windows-1252");
        String content = "", line;

        int year = Calendar.getInstance().get(Calendar.YEAR);
        String timeStamp = new SimpleDateFormat("EEEE dd MMMM yyyy | hh:mm:ss ", Locale.FRANCE).format(new Date());

        try (BufferedReader in = Files.newBufferedReader(path, charset)) {
            ;
            while ((line = in.readLine()) != null) {
                line = line.replace("xx#748__", gpw);
                line = line.replace("xx#799__", sexe);
                // line = line.replace("xx#74z__", uname);
                line = line.replace("#date#", timeStamp);
                line = line.replace("#year#", String.valueOf(year));
                line = line.replace("#url#", url);
                // .println(line);

                content += line;
            }
        }
        msg.setContent(content, "text/html");
        msg.setSentDate(new Date());
        Transport.send(msg);
    }

    public void sendCredentialToClient(String cltName, String gnUserName, Genpw gen, String gpw)
            throws AddressException, MessagingException {

        Properties props = getProp();
        System.setProperty("mail.mime.charset", "UTF-8");
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username, false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(gen.getEmailTo()));
        msg.setSubject(gen.getSubject());
        msg.setContent("Bonjour Monsieur " + cltName + ", <br/> Votre nom d'utilisateur : " + gnUserName
                + "<br/> Votre mot de passe : " + gpw, "text/html");
        msg.setSentDate(new Date());
        Transport.send(msg);
    }

    // SEND EMAIL WITH ATT
    public void sendwithAtt(mailClass mail) throws AddressException, MessagingException, IOException {

        Properties props = getProp();
        System.setProperty("mail.mime.charset", "UTF-8");
        String[] lst = mail.mailTo.split("\n");
        String[] lstC = mail.cc.split("\n");
        // .println(mail.cc);
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        MimeMessage msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(username));
        // msg.addRecipient(Message.RecipientType.TO, new InternetAddress(mail.mailTo));

        for (String email : lst) {
            // .println(email);
            msg.addRecipient(RecipientType.TO, new InternetAddress(email));

        }
        if (lstC.length > 0 && Objects.nonNull(mail.cc) && !mail.cc.equals("")) {
            for (String email : lstC) {
                msg.addRecipient(RecipientType.CC, new InternetAddress(email));
            }
        }

        msg.setSubject(mail.objet);

        Path path = Paths.get(root + "/sendmail2.txt");

        Multipart emailContent = new MimeMultipart();
        Charset charset = Charset.forName("Windows-1252");
        String content = "", line;
        String nomUtil = ((CostumUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUser().getFullName();

        int year = Calendar.getInstance().get(Calendar.YEAR);
        String timeStamp = new SimpleDateFormat("EEEE dd MMMM yyyy | hh:mm:ss ", Locale.FRANCE).format(new Date());

        try (BufferedReader in = Files.newBufferedReader(path, charset)) {
            ;
            while ((line = in.readLine()) != null) {
                line = line.replace("#messageBody#", mail.body);
                line = line.replace("#date#", timeStamp);
                line = line.replace("#year#", String.valueOf(year));
                line = line.replace("#url#", url);
                line = line.replace("#senderName#", nomUtil);
                content += line;
            }
        }

        // Text body part
        MimeBodyPart textBodyPart = new MimeBodyPart();
        // textBodyPart.setText(content);
        textBodyPart.setContent(content, "text/html");

        // Attachment body part.
        MasterConfig mc = configRepository.findByMasterUserIdAndConfigName(getmst().getUserId(), "ACTIVE_PATH");
        Document d = dr.getOne(mail.documentId);
        String pathTemp = mc.getConfigValue() + "\\upload\\tempfiles\\";
        final File saveInRootPath = new File(pathTemp);
        if (!saveInRootPath.exists()) {
            saveInRootPath.mkdirs();
        }
        pathTemp += d.getFileName();
        Resource r = fileStorageService.load(d, pathTemp);

        MimeBodyPart Attachment = new MimeBodyPart();
        Attachment.attachFile(r.getFile());
        // String[] filenametable = r.getFilename().split("_");
        String filename = r.getFilename().replace("%20", " ");
        // .println(filename);
        // for (int i = 0; i < filenametable.length; i++) {

        // if (i != 0) {
        // filename += filenametable[i];
        // }

        // }
        Attachment.setFileName(filename);
        emailContent.addBodyPart(textBodyPart);
        emailContent.addBodyPart(Attachment);
        msg.setContent(emailContent);
        Transport.send(msg);
        // .println("Sent message");

        User u = fileStorageService.connectedUser();
        User mst = us.GetMaster();
        Document doc = dr.findById(mail.documentId).orElse(null);
        if (mst.getSecLevel() >= 2 && doc.getClientDoc() == null) {
            final Journal j = new Journal();
            j.setUser(u);
            j.setDate(new Date());

            String ev = " Type : " + doc.getType().getName();

            final int a = 0;
            ev += " | " + "à " + " : " + StringUtils.join(Arrays.copyOf(lst, lst.length));
            for (final DocumentAttributeValue av : doc.getAttributeValues()) {
                if (!av.getAttribute().getName().equals("Fichier")) {
                    if (a == 0) {
                        ev += " | " + av.getAttribute().getName() + " : " + av.getValue().getValue();

                    }
                }
            }
            j.setAction(ev);
            j.setComposante("Document");
            j.setMode("C");
            j.setTypeEv("Utilisateur/Envoi par mail");
            j.setMaster(mst);

            jr.save(j);
        }
        File f = new File(pathTemp);
        boolean b = f.delete();
    }

    // SEND MAIL TO USER(STEP)
    public void alertUserStep(String userMail, String courrier, String delai, int sentPerDay, String name, NotifMail nm)
            throws MessagingException, IOException {
        // Properties props = getProp();
        // Properties props = new Properties();
        // if (Objects.isNull(nm)){
        // props.put("mail.smtp.host", hostConfig);
        // props.put("mail.smtp.port", portConfig);
        // props.put("mail.smtp.socketFactory.port",portConfig);
        // props.put("mail.smtp.socketFactory.class",
        // "javax.net.ssl.SSLSocketFactory");
        // props.put("mail.smtp.auth", "true");
        // } else {
        // props.put("mail.smtp.host", nm.getHost());
        // props.put("mail.smtp.port", nm.getPort());
        // props.put("mail.smtp.socketFactory.port", nm.getPort());
        // props.put("mail.smtp.socketFactory.class",
        // "javax.net.ssl.SSLSocketFactory");
        // props.put("mail.smtp.auth", "true");

        // username = nm.getMail();
        // password = nm.getPassword();
        // }

        // Session session = Session.getInstance(props, new javax.mail.Authenticator() {
        // protected PasswordAuthentication getPasswordAuthentication() {
        // return new PasswordAuthentication(username, password);
        // }
        // });
        Date d = new Date();
        Calendar calenda = Calendar.getInstance();
        calenda.setTime(d);
        calenda.add(Calendar.HOUR_OF_DAY, 7);
        d = calenda.getTime();
        int diffS = (int) 10 * 60 / sentPerDay;
        System.setProperty("mail.mime.charset", "UTF-8");
        for (int i = 0; i < sentPerDay; i++) {
            // Message msg = new MimeMessage(session);
            // msg.setFrom(new InternetAddress(username, false));

            // msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userMail));
            // msg.setSubject("Traitement de courrier : " + courrier);
            // msg.setSentDate(date);
            Path path = Paths.get(root + "/step.txt");
            Charset charset = Charset.forName("Windows-1252");
            String content = "", line;

            int year = Calendar.getInstance().get(Calendar.YEAR);
            String timeStamp = new SimpleDateFormat("EEEE dd MMMM yyyy | hh:mm:ss ", Locale.FRANCE).format(new Date());

            try (BufferedReader in = Files.newBufferedReader(path, charset)) {
                ;
                while ((line = in.readLine()) != null) {
                    line = line.replace("reffold#_", courrier);
                    line = line.replace("xx#799__", name);
                    line = line.replace("#date#", timeStamp);
                    line = line.replace("#year#", String.valueOf(year));
                    line = line.replace("#url#", url);
                    line = line.replace("datetr#_", delai);

                    content += line;
                }
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            calendar.add(Calendar.MINUTE, diffS);
            // d = calendar.getTime();
            // msg.setSentDate(d);
            ScheduleEmailRequest sem = new ScheduleEmailRequest();
            ZoneId zid = ZoneId.of("Africa/Casablanca");
            sem.setTimeZone(zid);
            sem.setBody(content);
            
            sem.setSubject("[DOCUMANIA] Traitement de courrier : " + courrier);
            sem.setDateTime(LocalDateTime.ofInstant(calendar.toInstant(), zid));
            sem.setEmail(userMail);
            scheduleEmail(sem);
        }

    }

    // TEST VALIDITY OF USER MAIL (SEND MAIL TO TEST)
    public void mailNotifTest(String mail, String passwordM, String host, String port) throws MessagingException {
        Properties props = new Properties();
        // .println(mail + passwordM + host + port);
        System.setProperty("mail.mime.charset", "UTF-8");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        // props.put("mail.smtp.port", port);
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mail, passwordM);
            }
        });
        System.setProperty("mail.mime.charset", "UTF-8");
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(mail, false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        msg.setSubject("Test de configuration");
        msg.setContent("Email configuré avec succés.", "text/html");
        Transport.send(msg);

    }

    @Autowired
    zipMailRepo zmr;

    // SEND MAIL DOWNLOAD ZIP (Link and password)
    public void sendLinkZip(mailClass mail, UUID id) throws AddressException, MessagingException, IOException {
        Properties props = getProp();
        zipMail z = zmr.findById(id).orElse(null);

        String[] lst = mail.mailTo.split("\n");
        String[] lstC = mail.cc.split("\n");
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message msg = new MimeMessage(session);
        System.setProperty("mail.mime.charset", "UTF-8");
        msg.setFrom(new InternetAddress(username));
        msg.setSubject(mail.objet);
        if (!lst[0].equals(""))

            for (String email : lst) {
                msg.addRecipient(RecipientType.TO, new InternetAddress(email));

            }
        if (!lstC[0].equals("")) {
            for (String email : lstC) {
                msg.addRecipient(RecipientType.CC, new InternetAddress(email));

            }
        }

        Path path;
        if (z.getHasPassword() == 0) {
            path = Paths.get(root + "/zip.txt");
        } else {
            path = Paths.get(root + "/zippw.txt");
        }

        Charset charset = Charset.forName("Windows-1252");
        String content = "", line;

        int year = Calendar.getInstance().get(Calendar.YEAR);
        String timeStamp = new SimpleDateFormat("EEEE dd MMMM yyyy | hh:mm:ss ", Locale.FRANCE).format(new Date());

        try (BufferedReader in = Files.newBufferedReader(path, charset)) {
            ;
            String nomUtil = ((CostumUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                    .getUser().getFullName();
            while ((line = in.readLine()) != null) {
                line = line.replace("#messageBody#", mail.body);
                line = line.replace("{{myLink}}", frontUrl + "/fichier/" + z.getId());
                if (Objects.nonNull(z.getPassowrd()))
                    line = line.replace("xx#748__", z.getPassowrd());
                line = line.replace("#senderName#", nomUtil);
                line = line.replace("#date#", timeStamp);
                line = line.replace("#year#", String.valueOf(year));
                line = line.replace("#url#", url);
                content += line;
            }
        }
        msg.setContent(content, "text/html");
        Transport.send(msg);

    }

    public void notifSupport(Long id, String email, String genKey)
            throws AddressException, MessagingException, IOException {
        System.out.println("here");
        Properties props = getProp();
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        System.setProperty("mail.mime.charset", "UTF-8");
        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(username));

        Charset charset = Charset.forName("Windows-1252");
        String content = "", line;
        msg.addRecipient(RecipientType.TO, new InternetAddress(email));
        msg.setSubject("Changement d'emplacement de stockage.");
        Path path = Paths.get(root + "/changepath.txt");

        int year = Calendar.getInstance().get(Calendar.YEAR);
        String timeStamp = new SimpleDateFormat("EEEE dd MMMM yyyy | hh:mm:ss ", Locale.FRANCE).format(new Date());

        try (BufferedReader in = Files.newBufferedReader(path, charset)) {

            while ((line = in.readLine()) != null) {

                line = line.replace("{{myLink}}", frontUrl + "/storage/" + id + "/" + genKey);
                line = line.replace("#date#", timeStamp);
                line = line.replace("#year#", String.valueOf(year));
                line = line.replace("#url#", url);
                // line = line.replace("#senderName#",((CostumUserDetails)
                // SecurityContextHolder.getContext().getAuthentication()
                // .getPrincipal()).getUser().getFullName());
                // //.println(line);
                content += line;
            }
        }
        msg.setContent(content, "text/html");
        // .println("done");
        Transport.send(msg);

    }
    // -----JOBS for mail notif

    public void scheduleEmail(ScheduleEmailRequest scheduleEmailRequest) {
        try {
            ZonedDateTime dateTime = ZonedDateTime.of(scheduleEmailRequest.getDateTime(),
                    scheduleEmailRequest.getTimeZone());

            JobDetail jobDetail = buildJobDetail(scheduleEmailRequest);
            Trigger trigger = buildJobTrigger(jobDetail, dateTime);
            scheduler.scheduleJob(jobDetail, trigger);

            ScheduleEmailResponse scheduleEmailResponse = new ScheduleEmailResponse(true,
                    jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), "Email Scheduled Successfully!");

        } catch (SchedulerException ex) {
            logger.error("Error scheduling email", ex);

        }
    }

    private JobDetail buildJobDetail(ScheduleEmailRequest scheduleEmailRequest) {
        
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("email", scheduleEmailRequest.getEmail());
        jobDataMap.put("subject", scheduleEmailRequest.getSubject());
        jobDataMap.put("body", scheduleEmailRequest.getBody());
        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(UUID.randomUUID().toString(), "email-jobs")
                .withDescription("Send Email Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "email-triggers")
                .withDescription("Send Email Trigger")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }

    @Autowired
    FolderRepo fr;

    public void sendCourrierDocs(UUID id, List<String> receivers)
            throws AddressException, MessagingException, IOException {

        Folder f = fr.findById(id).orElse(null);

        List<DocumentFolder> docsFolder = f.getDocuments();
        List<UUID> docs = new ArrayList<>();
        for (DocumentFolder document : docsFolder) {
            docs.add(document.getDocument().getId());
        }

        zipMail zMail = new zipMail();
        System.setProperty("mail.mime.charset", "UTF-8");
        fileStorageServiceImpl.saveZip(docs, zMail);

        for (String receiver : receivers) {

            receiver rec = rc.findReceiverByEmail(receiver);

            Properties props = getProp();
            // .println(mail.cc);
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            MimeMessage msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(username));
            // msg.addRecipient(Message.RecipientType.TO, new InternetAddress(mail.mailTo));
            msg.setSubject(new String(CourrierFinaliseDepart.getBytes("ISO-8859-15"), "UTF-8"));
            msg.addRecipient(RecipientType.TO, new InternetAddress(receiver));

            // Text body part
            MimeBodyPart textBodyPart = new MimeBodyPart();

            msg.setFrom(new InternetAddress(username));

            // msg.addRecipient(RecipientType.CC, new InternetAddress(email));

            Path path;

            path = Paths.get(root + "/zip1.txt");

            Charset charset = Charset.forName("Windows-1252");
            String content = "", line;

            Track emailTracked = new Track();
            emailTracked.setEmail(receiver);
            emailTracked.setFolder(f);
            emailTracked.setRead(0);
            emailTracked.setClicked(0);
            trackedRepo.saveAndFlush(emailTracked);

            int year = Calendar.getInstance().get(Calendar.YEAR);
            String timeStamp = new SimpleDateFormat("EEEE dd MMMM yyyy | hh:mm:ss ", Locale.FRANCE).format(new Date());

            try (BufferedReader in = Files.newBufferedReader(path, charset)) {
                ;
                String nomUtil = ((CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
                        .getPrincipal())
                                .getUser().getFullName();
                while ((line = in.readLine()) != null) {
                    line = line.replace("#pixelTracked#", backUrl + "readEmail/" + emailTracked.getId());
//                    line = line.replace("#owner#",  f.getOwner().getFullName());
//                    if(Objects.nonNull(rec)) {
//                        line = line.replace("#dest#", "vers le destinataire " + "<strong>"+rec.getName()+"</strong>"); 
//                    }else {
//                        line = line.replace("#dest#", ""); 
//                    }
//                    line = line.replace("#object#",  f.getObjet());
                    line = line.replace("#messageBody#", f.getReference());
                    line = line.replace("{{myLink}}",
                            frontUrl + "/fichier/" + zMail.getId() + "/" + emailTracked.getId());
                    if (Objects.nonNull(zMail.getPassowrd()))
                        line = line.replace("xx#748__", zMail.getPassowrd());
                    // line = line.replace("#senderName#", nomUtil);
                    line = line.replace("#date#", timeStamp);
                    line = line.replace("#year#", String.valueOf(year));
                    line = line.replace("#url#", url);
                    content += line;
                }
            }
            msg.setContent(content, "text/html");
            Transport.send(msg);

        }

    }


    
    public void proccessMailAbsence(Properties props,String annotation,UUID id,List<ProfilsAbsence> receivers,String type,String nomUtil)
            throws AddressException, MessagingException, IOException {

        Folder f = fr.findById(id).orElse(null);



        for (ProfilsAbsence user : receivers) {



            // .println(mail.cc);
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            MimeMessage msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(username));
       
            msg.addRecipient(RecipientType.TO, new InternetAddress(user.getSupleant().getEmail()));
            
            String name = user.getSupleant().getSexe().equals("M") ? "M " +" "+ user.getUser().getFullName() : "Mme " +" "+ user.getSupleant().getFullName();
            if(user.getSupleant().getSexe().equals("")) {
                name = "M/Mme";
            }
            
            msg.setSubject(new String(NotifyAbsence.getBytes("ISO-8859-15"), "UTF-8") );
            
           
                Path path = null;

                if(type.equals(ARRIVE)) {

                 path = Paths.get(root + "/sendmail1Abs.txt");
                }else if(type.equals(DEPART)) {

                path = Paths.get(root + "/sendmailAbs.txt");
                }
                
               

                Multipart emailContent = new MimeMultipart();
                Charset charset = Charset.forName("UTF-8");
                String content = "", line;
                // String nomUtil= ((CostumUserDetails)
                // SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getFullName();

                int year = Calendar.getInstance().get(Calendar.YEAR); 
                String timeStamp = new SimpleDateFormat("EEEE dd MMMM yyyy | hh:mm:ss ",Locale.FRANCE).format(new Date());
                String dests = "";
                   
                try {
                    
                  for (receiver dest : f.getDest()) {
                    dests += dest.getName()+",";
                  }
                
                }catch(Exception e) {
                    e.printStackTrace();
                }
                try (BufferedReader in = Files.newBufferedReader(path, charset)) {
                    ;
                    while ((line = in.readLine()) != null) {
                        if(type.equals(ARRIVE)) {

                            line = line.replace("#emet#",f.getEmet__());
                            line = line.replace("#objet#", f.getObjet());
                            
                            line = line.replace("#ref#", f.getReference());
                            line = line.replace("#senderName#", user.getUser().getFullName());
                            line = line.replace("#profile#", name);
                           }else if(type.equals(DEPART)) {

                               line = line.replace("#dest#",dests );
                               line = line.replace("#objet#", f.getObjet());
                               line = line.replace("#senderName#", nomUtil);
                               line = line.replace("#profile#", name);
                           }
                       
                        line = line.replace("#messageBody#", annotation);
                        line = line.replace("#date#", timeStamp);
                        line = line.replace("#year#", String.valueOf(year));
                        line = line.replace("#url#", url);
                        
                        // .println(line);
                        content += line;
                    }
                }
                MimeBodyPart textBodyPart = new MimeBodyPart();
                textBodyPart.setContent(content, "text/html; charset=UTF-8");
                emailContent.addBodyPart(textBodyPart);
                msg.setContent(emailContent);
                Transport.send(msg);
            }
        

    }
    
    
    // SEND MAIL PROCESS
    //@Async
    @Transactional
    public void processMail(String annotation, boolean next, String nomUtil, List<User> receivers, Properties props,CourrierInfo f,String type, List<User> receiversReal)
            throws MessagingException, IOException {
        System.out.println("from mail !!!!!!!!!!");
       
      try {
        System.setProperty("mail.mime.charset","UTF-8");

        // Properties props = getProp();
        // .println(mail.cc);
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        MimeMessage msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(username));
        // msg.addRecipient(Message.RecipientType.TO, new InternetAddress(mail.mailTo));
        if(next == true){
        for (User u : receiversReal) {
            // .println(email);
            if(type.equals(ARRIVE)) {
                for (User user : receivers) {
                    msg.addRecipient(RecipientType.TO, new InternetAddress(user.getEmail()));
                }  
            }
           
            msg.addRecipient(RecipientType.TO, new InternetAddress(u.getEmail()));
            System.out.println(CourrierTraiter);
            msg.setSubject(next ? new String(CourrierTraiter.getBytes("ISO-8859-15"), "UTF-8") :  new String(CourrierReturn.getBytes("ISO-8859-15"), "UTF-8"));
            
           
                Path path = null;
                if(type.equals(ARRIVE)) {

                 path = Paths.get(root + "/sendmail1.txt");
                }else if(type.equals(DEPART)) {

                path = Paths.get(root + "/sendmail.txt");
                }
               

                Multipart emailContent = new MimeMultipart();
                Charset charset = Charset.forName("UTF-8");
                String content = "", line;
                // String nomUtil= ((CostumUserDetails)
                // SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getFullName();

                int year = Calendar.getInstance().get(Calendar.YEAR); 
                String timeStamp = new SimpleDateFormat("EEEE dd MMMM yyyy | hh:mm:ss ",Locale.FRANCE).format(new Date());
                String dests = "";
                   
                try {
                    
                  for (receiver dest : f.getDest()) {
                    dests += dest.getName()+",";
                  }
                
                }catch(Exception e) {
                    e.printStackTrace();
                }
                try (BufferedReader in = Files.newBufferedReader(path, charset)) {
                    ;
                    while ((line = in.readLine()) != null) {
                        if(type.equals(ARRIVE)) {

                            line = line.replace("#emet#",f.getEmet__());
                            line = line.replace("#objet#", f.getObjet());
                            
                            line = line.replace("#ref#", f.getReference());
                            line = line.replace("#senderName#", u.getFullName());
                            
                           }else if(type.equals(DEPART)) {

                               line = line.replace("#dest#",dests );
                               line = line.replace("#objet#", f.getObjet());
                               line = line.replace("#senderName#", nomUtil);
                           }
                       
                        line = line.replace("#messageBody#", annotation);
                        line = line.replace("#date#", timeStamp);
                        line = line.replace("#year#", String.valueOf(year));
                        line = line.replace("#url#", url);
                        
                        // .println(line);
                        content += line;
                    }
                }
                MimeBodyPart textBodyPart = new MimeBodyPart();
                textBodyPart.setContent(content, "text/html; charset=UTF-8");
                emailContent.addBodyPart(textBodyPart);
                msg.setContent(emailContent);
                System.out.println("Pre send");
                Transport.send(msg);
                System.out.println("Sent !");  
            }
        
            
        }
        else {
            for (User u : receivers) { 
                
            Path path = null;
           
            msg.addRecipient(RecipientType.TO, new InternetAddress(u.getEmail()));
             
            path = Paths.get(root + "/retouner.txt");
           
            System.out.println(CourrierTraiter);
            msg.setSubject(next ? new String(CourrierTraiter.getBytes("ISO-8859-15"), "UTF-8") :  new String(CourrierReturn.getBytes("ISO-8859-15"), "UTF-8"));
             
            Multipart emailContent = new MimeMultipart();
            Charset charset = Charset.forName("UTF-8");
            String content = "", line;
            // String nomUtil= ((CostumUserDetails)
            // SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getFullName();

            int year = Calendar.getInstance().get(Calendar.YEAR); 
            String timeStamp = new SimpleDateFormat("EEEE dd MMMM yyyy | hh:mm:ss ",Locale.FRANCE).format(new Date());
            String dests = "";
                    
            for (receiver dest : f.getDest()) {
                dests += dest.getName()+",";
            }
                    
            try (BufferedReader in = Files.newBufferedReader(path, charset)) {
                ;
                while ((line = in.readLine()) != null) {
               
                    line = line.replace("#messageBody#", annotation);
                    line = line.replace("#date#", timeStamp);
                    line = line.replace("#year#", String.valueOf(year));
                    line = line.replace("#url#", url);
                    line = line.replace("#sendername#", nomUtil);
                    // .println(line);
                    content += line;
                }
            }
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setContent(content, "text/html;charset=UTF-8");
            emailContent.addBodyPart(textBodyPart);
            msg.setContent(emailContent);
            System.out.println("Pre send");
            Transport.send(msg);
            System.out.println("Sent !");  
            }
        }
        }catch(Exception e) {
            e.printStackTrace();
        }
        
    }

    public boolean emetteurEmail(String nomMaster, String code, String email)
            throws MessagingException, IOException {
        System.out.println("from mail !!!!!!!!!!");
        System.setProperty("mail.mime.charset", "UTF-8");
        try {
            // Properties props = getProp();
            // .println(mail.cc);
            Session session = Session.getInstance(getProp(), new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            MimeMessage msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(username));
            // msg.addRecipient(Message.RecipientType.TO, new InternetAddress(mail.mailTo));

            msg.addRecipient(RecipientType.TO, new InternetAddress(email));

            msg.setSubject("Code d'accès de l'espace émetteur");
            Path path = Paths.get(root + "/sendmail.txt");

            Multipart emailContent = new MimeMultipart();
            Charset charset = Charset.forName("UTF-8");
            String content = "", line;
            // String nomUtil= ((CostumUserDetails)
            // SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getFullName();

            int year = Calendar.getInstance().get(Calendar.YEAR);
            String timeStamp = new SimpleDateFormat("EEEE dd MMMM yyyy | hh:mm:ss ", Locale.FRANCE).format(new Date());

            try (BufferedReader in = Files.newBufferedReader(path, charset)) {
                ;
                while ((line = in.readLine()) != null) {
                    line = line.replace("#messageBody#", "Votre code est : " + code);
                    line = line.replace("#date#", timeStamp);
                    line = line.replace("#year#", String.valueOf(year));
                    line = line.replace("#url#", url);
                    line = line.replace("#senderName#", nomMaster);
                    // .println(line);

                    content += line;
                }
            }
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setContent(content, "text/html;charset=UTF-8");
            emailContent.addBodyPart(textBodyPart);
            msg.setContent(emailContent);
            Transport.send(msg);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void sendSignReqeust(String singerName, String singerEmail, String code, int validationTime,
            String documentName) throws AddressException, MessagingException, IOException {

        Properties props = getProp();
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        System.setProperty("mail.mime.charset", "UTF-8");
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username, false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(singerEmail));
        msg.setSubject("Code E-Sign");
        String sexe;
        Path path = Paths.get(root + "/sigreq.txt");
        Charset charset = Charset.forName("Windows-1252");
        String content = "", line;

        int year = Calendar.getInstance().get(Calendar.YEAR);
        String timeStamp = new SimpleDateFormat("EEEE dd MMMM yyyy | hh:mm:ss ", Locale.FRANCE).format(new Date());

        try (BufferedReader in = Files.newBufferedReader(path, charset)) {
            ;
            while ((line = in.readLine()) != null) {

                line = line.replace("#reqName#", singerName);
                line = line.replace("xx#748__", code);
                line = line.replace("#docName#", documentName);
                line = line.replace("x#125", validationTime + "");
                line = line.replace("#date#", timeStamp);
                line = line.replace("#year#", String.valueOf(year));
                line = line.replace("#url#", url);
                content += line;
            }
        }
        msg.setContent(content, "text/html; charset=UTF-8");
        msg.setSentDate(new Date());
        Transport.send(msg);
    }

    public void storageVolumeRequest(String name, String email, String code, int validationTime, String pathToEdit)
            throws AddressException, MessagingException, IOException {

        Properties props = getProp();
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        System.setProperty("mail.mime.charset", "UTF-8");
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username, false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        msg.setSubject("Suppression de volume de stockage");

        Path path = Paths.get(root + "/storagereq.txt");
        Charset charset = Charset.forName("Windows-1252");
        String content = "", line;

        int year = Calendar.getInstance().get(Calendar.YEAR);
        String timeStamp = new SimpleDateFormat("EEEE dd MMMM yyyy | hh:mm:ss ", Locale.FRANCE).format(new Date());

        try (BufferedReader in = Files.newBufferedReader(path, charset)) {
            ;
            while ((line = in.readLine()) != null) {

                line = line.replace("#reqName#", name);
                line = line.replace("xx#748__", code);
                line = line.replace("#path#", pathToEdit);
                line = line.replace("x#125", validationTime + "");
                line = line.replace("#date#", timeStamp);
                line = line.replace("#year#", String.valueOf(year));
                line = line.replace("#url#", url);
                content += line;
            }
        }
        msg.setContent(content, "text/html; charset=UTF-8");
        msg.setSentDate(new Date());
        Transport.send(msg);
    }

    public void notifyProfilAbs(String name, String suppleant, String mail, String dateDe, String dateA)
            throws AddressException, MessagingException, IOException {
        Properties props = getProp();
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        System.setProperty("mail.mime.charset", "UTF-8");
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username, false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail));
        msg.setSubject(new String(NotifyAbsenceCreate.getBytes("ISO-8859-15"), "UTF-8"));
        String sexe;
        Path path = Paths.get(root + "/notifabs.txt");
        Charset charset = Charset.forName("Windows-1252");
        String content = "", line;

        int year = Calendar.getInstance().get(Calendar.YEAR);
        String timeStamp = new SimpleDateFormat("EEEE dd MMMM yyyy | hh:mm:ss ", Locale.FRANCE).format(new Date());

        try (BufferedReader in = Files.newBufferedReader(path, charset)) {
            ;
            while ((line = in.readLine()) != null) {

                //line = line.replace("name#125", name);
                line = line.replace("xsupp#125", name);
                line = line.replace("xdated#125", dateDe);
                line = line.replace("xdatef#125", dateA);
                line = line.replace("#date#", timeStamp);
                line = line.replace("#year#", String.valueOf(year));
                line = line.replace("#url#", url);
                content += line;
            }
        }
        msg.setContent(content, "text/html; charset=UTF-8");

        msg.setSentDate(new Date());
        Transport.send(msg);
    }

    public void notifyProfilAbsParent(String name, String suppleant,String parent, String mail, String dateDe, String dateA)
            throws AddressException, MessagingException, IOException {
        Properties props = getProp();
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        System.setProperty("mail.mime.charset", "UTF-8");
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username, false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail));
        msg.setSubject(new String(NotifyAbsenceCreate.getBytes("ISO-8859-15"), "UTF-8"));
        String sexe;
        Path path = Paths.get(root + "/notifabsParent.txt");
        Charset charset = Charset.forName("Windows-1252");
        String content = "", line;

        int year = Calendar.getInstance().get(Calendar.YEAR);
        String timeStamp = new SimpleDateFormat("EEEE dd MMMM yyyy | hh:mm:ss ", Locale.FRANCE).format(new Date());

        try (BufferedReader in = Files.newBufferedReader(path, charset)) {
            ;
            while ((line = in.readLine()) != null) {

                //line = line.replace("name#125", name);
                line = line.replace("xsupp#125", name);
                line = line.replace("xparent#125", parent);    
                line = line.replace("xdated#125", dateDe);
                line = line.replace("xdatef#125", dateA);
                line = line.replace("#date#", timeStamp);
                line = line.replace("#year#", String.valueOf(year));
                line = line.replace("#url#", url);
                content += line;
            }
        }
        msg.setContent(content, "text/html; charset=UTF-8");

        msg.setSentDate(new Date());
        Transport.send(msg);
    }
    public void notifyProfilSec(String name, String suppleant, String mail, String mode, String action)
            throws AddressException, MessagingException, IOException {
        Properties props = getProp();
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        System.setProperty("mail.mime.charset", "UTF-8");
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username, false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail));
        msg.setSubject(mode + " du profil secondaire");
        String sexe;
        Path path = Paths.get(root + "/notifSec.txt");
        Charset charset = Charset.forName("Windows-1252");
        String content = "", line;

        int year = Calendar.getInstance().get(Calendar.YEAR);
        String timeStamp = new SimpleDateFormat("EEEE dd MMMM yyyy | hh:mm:ss ", Locale.FRANCE).format(new Date());

        try (BufferedReader in = Files.newBufferedReader(path, charset)) {
            ;
            while ((line = in.readLine()) != null) {

                line = line.replace("#mode#", mode);
                // line = line.replace("#action#", action);
                line = line.replace("xname#125", name);
                // line = line.replace("xsupp#125", suppleant);
                line = line.replace("#date#", timeStamp);
                line = line.replace("#year#", String.valueOf(year));
                line = line.replace("#url#", url);
                content += line;
            }
        }
        msg.setContent(content, "text/html; charset=UTF-8");

        msg.setSentDate(new Date());
        Transport.send(msg);
    }

    public void notifyProfilSecSupp(String name, String suppleant, String mail, String mode, String action)
            throws AddressException, MessagingException, IOException {
        Properties props = getProp();
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        System.setProperty("mail.mime.charset", "UTF-8");
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username, false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail));
        msg.setSubject(mode + " du profil secondaire");
        String sexe;
        Path path = Paths.get(root + "/notifSecSupp.txt");
        Charset charset = Charset.forName("Windows-1252");
        String content = "", line;

        int year = Calendar.getInstance().get(Calendar.YEAR);
        String timeStamp = new SimpleDateFormat("EEEE dd MMMM yyyy | hh:mm:ss ", Locale.FRANCE).format(new Date());

        try (BufferedReader in = Files.newBufferedReader(path, charset)) {
            ;
            while ((line = in.readLine()) != null) {

                line = line.replace("#mode#", mode);
                // line = line.replace("#action#", action);
                line = line.replace("xname#125", name);
                // line = line.replace("xsupp#125", suppleant);
                line = line.replace("#date#", timeStamp);
                line = line.replace("#year#", String.valueOf(year));
                line = line.replace("#url#", url);
                content += line;
            }
        }
        msg.setContent(content, "text/html; charset=UTF-8");

        msg.setSentDate(new Date());
        Transport.send(msg);
    }
    
    public void notifyParentAbonndoner(User parent,String currentUserName,String reference)
            throws AddressException, MessagingException, IOException {
        Properties props = getProp();
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        System.setProperty("mail.mime.charset", "UTF-8");
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username, false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(parent.getEmail()));
        msg.setSubject(new String(CourrierAbandonne.getBytes("ISO-8859-15"), "UTF-8"));
        
        Path path = Paths.get(root + "/notifParentAbonndoner.txt");
        Charset charset = Charset.forName("Windows-1252");
        String content = "", line;

        int year = Calendar.getInstance().get(Calendar.YEAR);
        String timeStamp = new SimpleDateFormat("EEEE dd MMMM yyyy | hh:mm:ss ", Locale.FRANCE).format(new Date());

        try (BufferedReader in = Files.newBufferedReader(path, charset)) {
            ;
            while ((line = in.readLine()) != null) {

                line = line.replace("xname#125", currentUserName);
                line = line.replace("xname#124", Objects.nonNull(reference) ? reference : "N/A");
                line = line.replace("#date#", timeStamp);
                line = line.replace("#year#", String.valueOf(year));
                line = line.replace("#url#", url);
                content += line;
            }
        }
        msg.setContent(content, "text/html; charset=UTF-8");

        msg.setSentDate(new Date());
        Transport.send(msg);
    }
}
