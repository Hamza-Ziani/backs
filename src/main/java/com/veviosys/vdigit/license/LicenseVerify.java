package com.veviosys.vdigit.license;

//import com.company.product.keygen.LicenseManager;



import java.io.File;
import java.io.FileReader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.prefs.Preferences;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.google.gson.Gson;



public class LicenseVerify {
   



    public void install(){




    }

    public boolean verify(String lic){
            
        String osName = System.getProperty("os.name");
        
        AbstractServerInfos serverInfos = null;
        
        
        
        
        
        	
        
        
        
        
        
        
        
       
        try {
            Path path = Paths.get(lic);

            String read = Files.readAllLines(path).get(0);
            System.out.println(read);
        	String uc = UnCipherText(Base64.getMimeDecoder().decode(read), readPublicKey());
        	Gson gson = new Gson();
        	LicenseModel nl = gson.fromJson(uc, LicenseModel.class);
        	
        	System.out.println(osName);
        	
        	if(osName.contains("indows"))
            	serverInfos = new WindowsServerInfos();
            else if(osName.contains("inux"))
            	serverInfos = new LinuxServerInfos();
            
        	
        	
        	if(!"COURRIER".equals(nl.getProduct()))
        		return false;
        	if((new Date()).after(nl.getNotBefore()))
        		return false;
        	if(!serverInfos.getIpAddress().contains(nl.getIpAddress().get(0)))
        		return false;
        	if(!serverInfos.getMacAddress().contains(nl.getMacAddress().get(0)))
        		return false;
//        	if(!serverInfos.getMainBoardSerial().equals(nl.getMainBoardSerial()))
//        		return false;
        	
            return true;
        }catch (Exception e){
          e.printStackTrace();
            return false;
        }
    }
    
	  private  String UnCipherText(byte[] secretMessage,  Key key) throws InvalidKeyException, NoSuchAlgorithmException,  NoSuchPaddingException,  IllegalBlockSizeException,  BadPaddingException {
		  
		  Cipher encryptCipher = Cipher.getInstance("RSA");
		  encryptCipher.init(Cipher.DECRYPT_MODE,key);
		 
		  byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessage);
		  
		  return new String(encryptedMessageBytes, StandardCharsets.UTF_8);
		  
	  }
	  
	  private RSAPublicKey readPublicKey() throws Exception {

		    byte[] encoded = { (byte) 48, (byte)-126, (byte)1, (byte)38, (byte)48, (byte)13, (byte)6, (byte)9, (byte)42, (byte)-122, (byte)72, (byte)-122, (byte)-9, (byte)13, (byte)1, (byte)1, (byte)1, (byte)5, (byte)0, (byte)3, (byte)-126, (byte)1, (byte)19, (byte)0, (byte)48, (byte)-126, (byte)1, (byte)14, (byte)2, (byte)-126, (byte)1, (byte)5, (byte)10, (byte)-28, (byte)65, (byte)30, (byte)111, (byte)-106, (byte)108, (byte)-7, (byte)47, (byte)86, (byte)43, (byte)18, (byte)-1, (byte)-64, (byte)-68, (byte)-105, (byte)112, (byte)113, (byte)122, (byte)15, (byte)-92, (byte)-22, (byte)-15, (byte)125, (byte)-104, (byte)-30, (byte)65, (byte)113, (byte)-25, (byte)-75, (byte)120, (byte)-47, (byte)-103, (byte)93, (byte)-100, (byte)-1, (byte)-58, (byte)8, (byte)32, (byte)45, (byte)-119, (byte)87, (byte)94, (byte)-38, (byte)24, (byte)-19, (byte)16, (byte)-54, (byte)-80, (byte)-101, (byte)127, (byte)-80, (byte)25, (byte)93, (byte)13, (byte)82, (byte)-54, (byte)-9, (byte)125, (byte)-53, (byte)77, (byte)14, (byte)-111, (byte)13, (byte)-86, (byte)118, (byte)-30, (byte)-40, (byte)29, (byte)-57, (byte)-75, (byte)127, (byte)-69, (byte)-97, (byte)-52, (byte)88, (byte)-69, (byte)-74, (byte)111, (byte)127, (byte)55, (byte)48, (byte)111, (byte)-69, (byte)-66, (byte)60, (byte)-124, (byte)43, (byte)-125, (byte)59, (byte)20, (byte)72, (byte)-83, (byte)14, (byte)-36, (byte)57, (byte)-16, (byte)43, (byte)84, (byte)-86, (byte)-84, (byte)-4, (byte)18, (byte)2, (byte)-106, (byte)77, (byte)106, (byte)27, (byte)-102, (byte)86, (byte)-82, (byte)-86, (byte)-128, (byte)-30, (byte)73, (byte)13, (byte)-116, (byte)-100, (byte)83, (byte)-66, (byte)94, (byte)-26, (byte)10, (byte)-42, (byte)92, (byte)102, (byte)84, (byte)1, (byte)-93, (byte)57, (byte)55, (byte)-107, (byte)49, (byte)84, (byte)-106, (byte)-127, (byte)84, (byte)-11, (byte)23, (byte)-104, (byte)31, (byte)40, (byte)-109, (byte)-72, (byte)38, (byte)70, (byte)-117, (byte)33, (byte)-71, (byte)86, (byte)-77, (byte)59, (byte)91, (byte)-67, (byte)63, (byte)-49, (byte)-40, (byte)76, (byte)84, (byte)-113, (byte)-19, (byte)34, (byte)64, (byte)42, (byte)-42, (byte)122, (byte)93, (byte)-15, (byte)-5, (byte)33, (byte)41, (byte)-92, (byte)117, (byte)-93, (byte)93, (byte)87, (byte)41, (byte)69, (byte)-17, (byte)114, (byte)-60, (byte)-71, (byte)-26, (byte)-90, (byte)-119, (byte)-122, (byte)87, (byte)-86, (byte)114, (byte)-84, (byte)-105, (byte)75, (byte)113, (byte)82, (byte)-26, (byte)30, (byte)110, (byte)-10, (byte)-56, (byte)110, (byte)-69, (byte)-49, (byte)23, (byte)-25, (byte)-31, (byte)-91, (byte)88, (byte)-51, (byte)51, (byte)95, (byte)103, (byte)-97, (byte)-43, (byte)-7, (byte)5, (byte)-106, (byte)-7, (byte)3, (byte)-97, (byte)91, (byte)-73, (byte)8, (byte)-32, (byte)27, (byte)64, (byte)-57, (byte)50, (byte)-17, (byte)21, (byte)-72, (byte)116, (byte)-20, (byte)-115, (byte)-100, (byte)8, (byte)-94, (byte)80, (byte)-35, (byte)-113, (byte)18, (byte)-30, (byte)-120, (byte)106, (byte)-69, (byte)32, (byte)15, (byte)17, (byte)22, (byte)117, (byte)16, (byte)13, (byte)-102, (byte)-48, (byte)-44, (byte)59, (byte)-108, (byte)-101, (byte)-116, (byte)123, (byte)11, (byte)33, (byte)2, (byte)3, (byte)1, (byte)0, (byte)1};
		    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
		    return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		}
   
}
