package com.veviosys.vdigit.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import com.veviosys.vdigit.models.Certaficate;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.repositories.CertificateRepository;

import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CertificateService {

	@Autowired
	private CertificateRepository certificateRepository;

	public User connectedUser() {
		return ((CostumUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
	}

	public void AddCertificare(String date, String path) {

		Certaficate cert = new Certaficate();
		cert.setClientPath(path);
		cert.setUser(connectedUser());
		cert.setValidityDate(date);
		certificateRepository.save(cert);

	}

	public Certaficate GetUserCertificate() {

		return certificateRepository.findByUserUserId(connectedUser().getUserId());
	}

	public void setUserCerPass(String pass, String pk) {
		Certaficate cert = certificateRepository.findByUserUserId(connectedUser().getUserId());
		cert.setPwd(pass);
		cert.setPk(pk);

		certificateRepository.save(cert);

	}

	public String getCertPass() {

		Certaficate cert = certificateRepository.findByUserUserId(connectedUser().getUserId());
		return cert.getPwd2();

	}

	public Certaficate updateCertPath(String newPath) {

		Certaficate cert = certificateRepository.findByUserUserId(connectedUser().getUserId());
		try {
			cert.setClientPath(newPath);
			certificateRepository.save(cert);
		} catch (Exception e) {
			// TODO: handle exception
			return cert;
		}

		return cert;
	}
	@Value("${documania.certify.crl.path}")
    String CRL;
	public Certaficate checkValidity(InputStream inputStream) throws KeyStoreException, NoSuchProviderException {
		// TODO Auto-generated method stub
		int validLevel = 0;
		String reasons = "";
		String valid = "";
		Certaficate certificate = GetUserCertificate();
		String pass = certificate.getPwd2();
		String unCryptedPass = new String(Base64.decodeBase64(pass.getBytes()));
		BouncyCastleProvider provider = new BouncyCastleProvider();
		Security.addProvider(provider);
		KeyStore ks = KeyStore.getInstance("pkcs12", "BC");
		
		
		Certificate certificateX;
		
		
		//test mot de passe
		try {
			ks.load(inputStream, unCryptedPass.toCharArray());

			validLevel++;
			System.err.println("1 pass");

		} catch (Exception e) {

			reasons += "Mot de passe incorrect|";
      
		}
		
		String alias = (String) ks.aliases().nextElement();
		//test clé public
		try {
			certificateX = ks.getCertificate(alias);
			PublicKey pk = certificateX.getPublicKey();
			
			
			PublicKey pubKey = (PublicKey) loadPublicKey(certificate.getPk());
			//System.err.println("pk 1 " + pk);
			
			String publicKeyString = Base64.encodeBase64String(pubKey.getEncoded());
			//.println("pk2  " + pubKey);
			if (pk.equals(pubKey)) {

				validLevel++;
				//.println("2 pass");
			} else {

				reasons += "La clé public n'est authentique|";
			}
		} catch (Exception e) {
			// TODO: handle exception
			reasons += "La clé public n'est authentique|";

		}
		//test clé privée
		
		try {
			certificateX = ks.getCertificate(alias);
			PublicKey pk = certificateX.getPublicKey();
			Key pubKey = loadPublicKey(certificate.getPk());
			byte[] encrypted = encrypt(pubKey,"AchrafWithMehdi");
			PrivateKey privatekey = (PrivateKey)ks.getKey(alias, unCryptedPass.toCharArray());
			String decrypted = decrypt(privatekey,encrypted);
			if (decrypted.equals("AchrafWithMehdi")) {
				
				validLevel ++;
				System.out.println("3 pass");
				
			}else {
				
				
				reasons += "La clé privée n'est authentique|";
				
				
				
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
				certificateX = ks.getCertificate(alias);
			    CertificateFactory cf = CertificateFactory.getInstance("X.509");
			    X509CRL crl = (X509CRL)cf.generateCRL(new FileInputStream(CRL));
			    if(crl.isRevoked(certificateX))
			    {
			    	reasons += "Certificat révoqueé|";
			    	
			    }
			    else
			    {
			    	validLevel ++;
			    	System.out.println("4 pass");
			    }

		} catch (Exception e) {
			
			e.printStackTrace();
			// TODO: handle exception
		}

		if(validLevel ==4)
		{
		
			certificate.setValid(true);
			certificate.setValidationReasons(reasons);
			certificate = certificateRepository.saveAndFlush(certificate);
		}
		else {
			certificate.setValid(false);
			certificate.setValidationReasons(reasons);
			certificate = certificateRepository.saveAndFlush(certificate);
		}
		return certificate;
	}

	public static Key loadPublicKey(String stored) throws GeneralSecurityException, IOException {
		byte[] data = Base64.decodeBase64((stored.getBytes()));
		X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
		KeyFactory fact = KeyFactory.getInstance("RSA", "BC");
		return fact.generatePublic(spec);

	}

	public byte[] encrypt(Key key, String message) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] cipherData = cipher.doFinal(message.getBytes());
		return cipherData;
	}

	public String decrypt(Key key, byte[] message) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] cipherData = cipher.doFinal(message);
		return new String(cipherData);
	}

}
