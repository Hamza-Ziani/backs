package com.veviosys.vdigit.license;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;
import com.veviosys.vdigit.classes.LicenseToBeEncode;
import com.veviosys.vdigit.configuration.MySessionRegistry;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.services.CostumUserDetails;

@Service
public class DcryptClass {

    private SecretKey key;
    private int T_LEN = 128;
    private byte[] IV;

    @Value("${license.secretKey}")
    private String secretKey;
    @Value("${license.iv}")
    private String iv;
    @Value("${license.file.name}")
    private String fileName;

    @Autowired
    private MySessionRegistry sessionRegistry;

    public boolean readfile() {

        initExist();

        LocalDate dateNow = LocalDate.now();

        List<User> users = listLoggedInUsers();

        Path path = Paths.get(fileName);
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            return true;
        }
        String data = "";
        try {
            data = decrypt(new String(bytes));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Gson json = new Gson();
        LicenseToBeEncode request = json.fromJson(data, LicenseToBeEncode.class);
        LocalDate dateFin = LocalDate.parse(request.getDateFin());

        
        if (request.getNumberUtilisateur().equals("ILIMITE")) {

  
            
            if (dateFin.isAfter(dateNow)) {
               
                return true;
                
            } else {
              
                return false;
                
            }

        } else {


            if (dateFin.isAfter(dateNow) && users.size() <= Integer.parseInt(request.getNumberUtilisateur())) {
                
                return true;
                
            } else {
                
                return false;
                
            }

        }

    }
    
    public LicenseToBeEncode getFileContent() {

        initExist();

        Path path = Paths.get(fileName);
        
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            return null;
        }
        String data = "";
        try {
            data = decrypt(new String(bytes));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        Gson json = new Gson();
        LicenseToBeEncode request = json.fromJson(data, LicenseToBeEncode.class);
     
        return request;

    }

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

    public void initExist() {
        key = new SecretKeySpec(decode(secretKey), "AES");
        this.IV = decode(iv);
    }

    public String decrypt(String encryptedMessage) throws Exception {
        byte[] messageInBytes = decode(encryptedMessage);
        Cipher decryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, this.IV);
        decryptionCipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] decryptedBytes = decryptionCipher.doFinal(messageInBytes);
        return new String(decryptedBytes);
    }
    
    public String encrypt(String message) throws Exception {
        byte[] messageInBytes = message.getBytes();
        Cipher encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, this.IV);
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] encryptedBytes = encryptionCipher.doFinal(messageInBytes);
        return encode(encryptedBytes);
    }
    
    private String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

}
