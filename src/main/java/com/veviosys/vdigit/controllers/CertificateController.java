package com.veviosys.vdigit.controllers;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import com.veviosys.vdigit.classes.Cert;
import com.veviosys.vdigit.models.Certaficate;
import com.veviosys.vdigit.services.CertificateService;

import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/certificate")
public class CertificateController {

	
	@Autowired CertificateService certificateService; 
	
	
	@GetMapping
	public ResponseEntity<Certaficate> GetUserCertificate()
	{
		Certaficate cert = new Certaficate();
		try {
			cert = certificateService.GetUserCertificate();
			
			return new ResponseEntity<Certaficate>(cert, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Certaficate>(cert, HttpStatus.EXPECTATION_FAILED);
			// TODO: handle exception
		}
		
	}
	
	
	@PostMapping
	public ResponseEntity AddUserCertificate(@RequestBody Cert certif)
	{
		Certaficate cert = new Certaficate();
		try {
			
			//.println(""+ certif.date);
			certificateService.AddCertificare(certif.date, certif.path);
			
			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity( HttpStatus.CONFLICT);
			// TODO: handle exception
		}
		
	}
	
	@PostMapping("check")
	public ResponseEntity checkPass(String pass,MultipartFile crt) throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException
	{   //.println(crt.getOriginalFilename() + " " + crt.getContentType());
		Certaficate cert = new Certaficate();
		
		
		
		//String unCryptedPass = new String(Base64.decodeBase64(pass.getBytes()));
		try {
			
			
			
			
			String unCryptedPass = new String(Base64.decodeBase64(pass.getBytes()));
			BouncyCastleProvider provider = new BouncyCastleProvider();
			 Security.addProvider(provider);
			 KeyStore ks = KeyStore.getInstance("pkcs12", provider.getName());
			 ks.load(crt.getInputStream(), unCryptedPass.toCharArray());
			 String alias = (String)ks.aliases().nextElement();
			//PrivateKey pk = (PrivateKey) ks.getKey(alias, unCryptedPass.toCharArray());
			
			Certificate certificate = ks.getCertificate(alias);
			
			PublicKey pk = certificate.getPublicKey();
			X509Certificate certa = (X509Certificate)certificate;
			String publicKeyString = Base64.encodeBase64String(pk
                    .getEncoded());
			 certificateService.setUserCerPass(pass,publicKeyString);
		
			 
			return new ResponseEntity(HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity( HttpStatus.CONFLICT);
			// TODO: handle exception
		}

//		try {
			
			
			
			
			
			//BouncyCastleProvider provider = new BouncyCastleProvider();
			 //Security.addProvider(provider);
			 ////.println("" + unCryptedPass); 
			 //KeyStore ks = KeyStore.getInstance("pkcs12", provider.getName());
			 //ks.load(crt.getInputStream(), unCryptedPass.toCharArray());
			 //String alias = (String)ks.aliases().nextElement();
			 //rivateKey pk = (PrivateKey) ks.getKey(alias, unCryptedPass.toCharArray());
			// PublicKey puk = (PublicKey) ks.getKey(alias, unCryptedPass.toCharArray());
			 //certificateService.setUserCerPass(pass);
			 
			//return new ResponseEntity(HttpStatus.CREATED);
	/*	} catch (Exception e) {
			e.fillInStackTrace();
			return new ResponseEntity( HttpStatus.CONFLICT);
			// TODO: handle exception
		}*/
		
	}
	
	
	
	@PutMapping("updatepath")
	public ResponseEntity<Certaficate> updatePath(@RequestBody String certPath)
	{		Certaficate cert;
	//.println(certPath);
			cert = certificateService.updateCertPath(certPath);
			return ResponseEntity.ok(cert);
		
		
		
	}
	
	@PostMapping("checkvalidity")
	public ResponseEntity<Certaficate> checkValidity(MultipartFile crt)
	{
	 
		
		
	try {
		return ResponseEntity.ok(certificateService.checkValidity(crt.getInputStream()));
	} catch (KeyStoreException | NoSuchProviderException | IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;	
	}
	@Data
	class CertPath {
		
		private String newPath;
	}
	
	

	
	
}
 
