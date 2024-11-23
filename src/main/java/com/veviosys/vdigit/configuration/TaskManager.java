package com.veviosys.vdigit.configuration;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.FlagTerm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.veviosys.vdigit.classes.FluxElectro;
import com.veviosys.vdigit.classes.LicenseToBeEncode;
import com.veviosys.vdigit.license.DcryptClass;
import com.veviosys.vdigit.models.ClientDoc;
import com.veviosys.vdigit.models.CloneEtape;
import com.veviosys.vdigit.models.Document;
import com.veviosys.vdigit.models.MasterConfig;
import com.veviosys.vdigit.models.NotifMail;
import com.veviosys.vdigit.models.ProfilsAbsence;
import com.veviosys.vdigit.models.Sender;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.models.delayMail;
import com.veviosys.vdigit.repositories.ClientDocRepo;
import com.veviosys.vdigit.repositories.CloneEtapeRepo;
import com.veviosys.vdigit.repositories.DocumentRepo;
import com.veviosys.vdigit.repositories.MasterConfigRepository;
import com.veviosys.vdigit.repositories.NotifMailRepo;
import com.veviosys.vdigit.repositories.SenderRepo;
import com.veviosys.vdigit.repositories.UserRepository;
import com.veviosys.vdigit.repositories.delayMailRepo;
import com.veviosys.vdigit.repositories.profilsAbsenceRepo;
import com.veviosys.vdigit.services.AlimentationService;
import com.veviosys.vdigit.services.mailService;
import com.veviosys.vdigit.services.userService;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableAsync
@EnableScheduling
@Slf4j
public class TaskManager {
    @Autowired
    UserRepository ur;
    @Autowired
    AlimentationService alimentationService;
    @Autowired
    userService us;
    @Autowired
    SenderRepo senderRepo;
    @Autowired 
    @Lazy
    mailService ms;
    @Autowired
    MasterConfigRepository configRepository;
    @Autowired
    delayMailRepo nmr;
    @Autowired
    NotifMailRepo nm;
    @Autowired
    profilsAbsenceRepo absenceRepo ;
    @Autowired
    private DocumentRepo dr;
    @Autowired
    ClientDocRepo cdr;
    @Autowired
    DcryptClass dcryptClass;
    
    @Value("${spring.mail.username}")
    private String email;
    @Value("${spring.mail.password}")
    private String password;
    
    @Value("${license.url}")
    private String url;
    @Value("${license.file.name}")
    private String fileName;
    @Value("${license.secretKey}")
    private String secretKey;
    @Value("${license.iv}")
    private String iv;
    
    public User connectedUserMaster(Long userid) {
       User u= ur.findById(userid).orElse(null);
        if (u.getMaster()!= null) {
            return u.getMaster();
        }
        return u;
    }
    
    public Path path = null;
    
	@Autowired
	CloneEtapeRepo cer;
	
    @Scheduled(cron = " 0 0 0 * * ?")
   // @Scheduled( fixedRate = 1000 * 60 )
    public void TimeStap() throws MessagingException, ParseException, IOException {

        List<User> lst = ur.findAll();
        for (User user : lst) {
            List<CloneEtape> etapes = cer.findAllStepsTodo(user.getUserId(),-1l,1l);  
        	String dt=(new Date()).toString();
            List<ProfilsAbsence>abs=absenceRepo.findByUserUserIdAndDateFinLessThanEqualAndDateDebutGreaterThanEqual(user.getUserId(), dt, dt);
            for (CloneEtape etape : etapes) {
                List<User> uList = ur.findUsersByEtape(etape.getId());
                for (User user2 : uList) {
                    if (user.getUserId().equals(user2.getUserId())) {
                        int delaiAlertSup = 1;
                        int delaiUs = 1;
                        int sentPerDay = 1;
                        User master = connectedUserMaster(user.getUserId());
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
                            	if(abs.size()>0) {
                            		for (ProfilsAbsence pa : abs) {
                            			  ms.alertUserStep(pa.getSupleant().getEmail(), etape.getCourrier().getReference(), date, sentPerDay,
                            					  (pa.getSupleant().getSexe().equals("M")?"Mr ":"Mme ")+ pa.getSupleant().getFullName(), notif);
									}
                            	}
                            	else {
                            	     ms.alertUserStep(user.getEmail(), etape.getCourrier().getReference(), date, sentPerDay,
                                             name, notif);
                            	}
                           
                                
                            } catch (Exception e) {
                                // TODO: handle exception
                            }

                        }
                        if (delaiAlertSup >= diffDays && etape.getDateFin().compareTo(new Date()) < 0
                                && Objects.nonNull(user.getParent()))
                        {
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

    @Value("${documania.certify.crl.url}")
    String URL;
    @Value("${documania.certify.crl.path}")
    String CRL;

  
    //@Scheduled(fixedRate = 1000 *60*60*24)
    public void CRLupdate() throws MalformedURLException, IOException {

        FileUtils.copyURLToFile(new URL(URL), new File(CRL), 500, 500);
        
       
    }
    
   
    //@Scheduled(fixedDelay = 60000)
    public boolean readFromMails() {
        
        try {
  
            List<User> masters = new ArrayList<>();
            
            masters = ur.findMasters();
            
            for (User master : masters) {
                
                
            MasterConfig config = configRepository.findByMasterUserIdAndConfigName(master.getUserId(),"FLUX-ELECTRO");;
                
            Store store ;
            
            if(config != null) {
                
                log.info("config no null");
                FluxElectro convertedObject = new FluxElectro();

                Type listType = new TypeToken<FluxElectro>() {
                }.getType();

                convertedObject = new Gson().fromJson(config.getConfigValue(), listType);
                //create properties field
                Properties properties = new Properties();

                properties.put("mail.imap.host", convertedObject.getHost());
                properties.put("mail.imap.port", convertedObject.getPort());
                properties.put("mail.imap.starttls.enable", "true");
                properties.put("mail.smtp.auth", "true"); 
                properties.put("mail.smtp.starttls.enable","true"); 
                properties.put("mail.smtp.EnableSSL.enable","true");
                properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");   
                properties.setProperty("mail.smtp.socketFactory.fallback", "false");   
                properties.setProperty("mail.smtp.port", "465");   
                properties.setProperty("mail.smtp.socketFactory.port", "465"); 
                Session emailSession = Session.getInstance(properties);
            
                //create the POP3 store object and connect with the pop server
                store = emailSession.getStore("imap");

                store.connect(convertedObject.getHost(), convertedObject.getEmail() , convertedObject.getPassword());
                
            }else {
                
                log.info("config is null");
                
                String host = "mail.veviosys.ma";
                //create properties field
                Properties properties = new Properties();

                properties.put("mail.imap.host", host);
                properties.put("mail.imap.port", "143");
                properties.put("mail.smtp.auth", "true"); 
                properties.put("mail.smtp.starttls.enable","true"); 
                properties.put("mail.smtp.EnableSSL.enable","true");
                properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");   
                properties.setProperty("mail.smtp.socketFactory.fallback", "false");   
                properties.setProperty("mail.smtp.port", "465");   
                properties.setProperty("mail.smtp.socketFactory.port", "465"); 
                Session emailSession = Session.getInstance(properties);
            
                //create the POP3 store object and connect with the pop server
                store = emailSession.getStore("imap");

                store.connect(host, email , password );
            }
         

            //create the folder object and open it
            javax.mail.Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(javax.mail.Folder.READ_WRITE);
             
            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm unseenFlagTerm = new FlagTerm(seen,false);
            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.search(unseenFlagTerm);
            
            if(messages.length == 0) {
                return false;
            }
            
            System.out.println("messages.length---" + messages.length);

            ClientDoc cd = new ClientDoc();

            List<Sender> senders = senderRepo.findAll();

  
            for (Message message : messages) {
                
                
                boolean exist = false;
                Sender sender1 = new Sender();
                
                String from = message.getHeader("Return-Path")[0];
                
                String[] array = message.getFrom()[0].toString().split(" ");
                
                String email = from.substring(1, from.length() - 1);
                
                for (Sender sender : senders) {
                  
                    if(Objects.nonNull(sender.getEmail())) {
                        if (sender.getEmail().equals(email)) {
                            exist = true;
                            sender1 = sender;
                            message.setFlag(Flags.Flag.SEEN, true);
                      }
                    }
                   
                }
                
                if(exist == true ) {
                    
                  
                    Object content =  message.getContent();  


                     if (content instanceof Multipart)  
                     {  
                         Multipart multipart  = (Multipart) content;  
    
                         String file = new String();
                         

                         cd.setEmail(from.substring(1, from.length() - 1));
                         cd.setMessage(null);
                         cd.setMaster((long) 1);
                         cd.setDocuments(null);
                         cd.setNom(array[0]);
                         cd.setPrenom(array[1]);
                         cd.setNum(null);
                         cd.setTitre(null);
                         cd.setEmetteur_id(sender1);
                         cd.setObjet(message.getSubject());
                         cd.setSentDate(message.getSentDate());
                         cd = cdr.saveAndFlush(cd);
   
                         for(int k = 0; k < multipart.getCount(); k++){
                             
                             MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(k);

                             if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {


                                  file = part.getFileName();
                                 
                                
                                 Map<String, String> idC = alimentationService.addDocByClient(file, (long) 1 , cd.getId());
                                 
                                 UUID id = UUID .fromString(idC.get("id")); 
                                                 
                                 Optional<Document> doc = dr.findById(id);
                                 
                                 final File saveInRootPath = new File(doc.get().getPathServer());
                                  
                                 if (!saveInRootPath.exists()) {
                                  saveInRootPath.mkdirs();
                                  }
                                  
                                 path = Paths.get(saveInRootPath.toString());
                               
                                 final String pathServ = doc.get().getPathServer();
                                 
                                 String pathName = pathServ + id.toString() + "_" + file.split("[.]")[0] + ".documania";
                                 
                                 doc.get().setPathServer(pathName);
         
                                 part.saveFile(pathName  + file);
                                 
                                 Path path = new File(pathName + file).toPath();
                                 String mimeType = Files.probeContentType(path);
                                 
                                 doc.get().setContentType(mimeType);
                                 
                                 dr.save(doc.get());


                                 byte[] byteData = Files.readAllBytes(Paths.get(pathName + file));
                                 String base64String = Base64.getEncoder().encodeToString(byteData);
                                 
                                
                                 
                                 File f = new File( pathName + file);
                                 f.delete();
                                 FileWriter myWriter = new FileWriter(pathName);
                                 myWriter.write(base64String);
                                 myWriter.close();
                                 
                             } 
                         
                          
                         }
                         
                     }
                }
                cd = new ClientDoc();
                   
                }
                
                 

              emailFolder.close(false);
              store.close();
            }
            } catch (NoSuchProviderException e) {
               e.printStackTrace();
            } catch (MessagingException e) {
               e.printStackTrace();
            } catch (Exception e) {
               e.printStackTrace();
            }
        return true;
    }
    
    @Scheduled(cron = " 0 0 0 * * ?")
    // @Scheduled( fixedRate = 1000 * 60 )
    public void EditStoragePath() {
        List<MasterConfig> dataToEdit = configRepository.findByConfigName("PATH_CONFIRMED");
        if (Objects.nonNull(dataToEdit)) {
            for (MasterConfig masterConfig : dataToEdit) {
                MasterConfig m = configRepository.findByMasterUserIdAndConfigName(masterConfig.getMaster().getUserId(),
                        "ACTIVE_PATH");
                m.setConfigValue(masterConfig.getConfigValue());
                configRepository.save(m);
                configRepository.delete(masterConfig);
            }
        }
    }
    
    // @Scheduled( fixedRate = 6000 )
    public void checkLicence() {
        
        LicenseToBeEncode exist = dcryptClass.getFileContent();

        WebClient client = WebClient.create();

        WebClient.ResponseSpec response = client.mutate().codecs(configurer -> configurer
                .defaultCodecs()
                .maxInMemorySize(16 * 1024 * 1024)).build().get()
                .uri(url + "/" + exist.getId() )     
                .retrieve().onStatus(
                        HttpStatus.UNAUTHORIZED::equals,
                        response1 -> response1.bodyToMono(String.class).map(Exception::new));

        LicenseToBeEncode licenseToBeEncode = response.bodyToMono(LicenseToBeEncode.class).block();
        
        
        if(!licenseToBeEncode.equals(exist)) {
            try {
                
                dcryptClass.initExist();
  
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String json = ow.writeValueAsString(licenseToBeEncode);
                
                String encode = dcryptClass.encrypt(json);
                
                
                File myFile = new File(fileName);
                myFile.delete();
                myFile.createNewFile();
                
                PrintWriter wr = new PrintWriter(myFile);
                wr.write(encode); 
                wr.close();
                
            } catch ( Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
           
        }
    }
}