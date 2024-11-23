package com.veviosys.vdigit.controllers;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList; 
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.CertificateUtil;
import com.itextpdf.text.pdf.security.CrlClient;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import com.veviosys.vdigit.classes.File64;
import com.veviosys.vdigit.models.MasterConfig;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.services.CertificateService;
import com.veviosys.vdigit.services.MasterConfigService;
import com.veviosys.vdigit.services.SignService;
import com.veviosys.vdigit.services.masterService;

import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;



@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/signing")
public class SigningController {
	
	@Autowired private MasterConfigService masterService;
	@Autowired private  SignService signaturService;
	@Autowired private CertificateService certificateService;
	public  final String KEYSTORE = "src/main/resources/certif.pfx";
	public  final char[] PASSWORD = "".toCharArray();
	public  final String SRC = "results/out.pdf";
	public  final String DEST = "results/sample_signe.pdf";
    

	
	@Value("${documania.certify.crl.url}")
	String URL;
	@Value("${documania.certify.crl.path}")
    String CRL;
	
	@PostMapping("{did}")
	public Map<String, String> visibleExternalSignature( @RequestParam("sign") MultipartFile sign,@RequestParam("cert") MultipartFile certa,@PathVariable UUID did  , float x, float y,int page,float h,float w) throws FileNotFoundException, IOException, GeneralSecurityException, DocumentException {
		
		String TMP = masterService.findActivePath()+"\\";
		String pass = certificateService.getCertPass();
		//.println(pass+ "" + "pss");
		char[] unCryptedPass = new String(Base64.decodeBase64(pass.getBytes())).toCharArray();
		//.println(new String(Base64.decodeBase64(pass.getBytes()))+ "" + "pss");
		System.out.println(unCryptedPass.toString());
		// Map<String, String> obj = new HashMap<String, String>();
		// float xP, yP;
		//.println("is sign null" + Objects.isNull(sign));
		try {
			BouncyCastleProvider provider = new BouncyCastleProvider();
			 Security.addProvider(provider);
			 KeyStore ks = KeyStore.getInstance("pkcs12", provider.getName());
			 ks.load(certa.getInputStream(), unCryptedPass);
			//ks.load(new FileInputStream("R:\\certificatename.pfx"), unCryptedPass);
			// R:\Projects\NA.pfx
			 
			 
			 String alias = (String)ks.aliases().nextElement();
			 PrivateKey pk = (PrivateKey) ks.getKey(alias, unCryptedPass);
			 //.println("all "+alias);
			 Certificate[] chain = ks.getCertificateChain(alias);
			 java.util.List<CrlClient> crlList = new ArrayList<CrlClient>();
			//  CrlClient cerCrlClient = new CrlClientOnline(URL);
			//  crlList.add(cerCrlClient);
			 try(	FileInputStream is = new FileInputStream(CRL)){
				 
				 ByteArrayOutputStream baos = new ByteArrayOutputStream();
				 byte[] buf = new byte[1024];
				 while (is.read(buf) != -1) baos.write(buf);
				 CrlClient crlClient = new com.itextpdf.text.pdf.security.CrlClientOffline(baos.toByteArray()) ;
				 
				 crlList.add(crlClient);
               	//  for (CrlClient b  :crlClient) {
						// CR de la présentation du 29-12-2020 à 14h CNOPS
					//.println(crlList.size());
					// }
			 } 
			 
			 //.println("page "+page);
			 if(Objects.nonNull(pk))
			 ////.println("count " + crlList.size()+ " len ch "+ chain.toString());
			 for (int i = 0; i < chain.length; i++) {
				 X509Certificate cert = (X509Certificate)chain[i];
				 //.println(String.format("[%s] %s", i, cert.getSubjectDN()));
				 //.println(CertificateUtil.getCRLURL(cert));
				}
			 if(Objects.nonNull(pk))
			 signaturService.sign(did, chain,
					 pk, 
					 DigestAlgorithms.SHA256,
					 provider.getName(), 
					 CryptoStandard.CMS,
					 "Reason", 
					 "Casablanca",
					 TMP,
					 sign.getBytes(),
					 x,
					 y,
					 page,
					 h,
					 w,
					 crlList);
			
			
			 //.println("passed");
		} catch (Exception e) {
            e.printStackTrace();
			//.println("ex");
			
			// TODO: handle exception
		}
				
		
		return null;
	}
	
	
	

	
	public void sign(String src, String name, String dest, Certificate[] chain,
			 PrivateKey pk, String digestAlgorithm, String provider,
			 CryptoStandard subfilter, String reason, String location)
			 throws GeneralSecurityException, IOException, DocumentException {
			 // Creating the reader and the stamper
			 PdfReader reader = new PdfReader(src);
			 FileOutputStream os = new FileOutputStream(dest);
			 PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0');
			 // Creating the appearance
			 PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
			 //appearance.setReason(reason);
			 appearance.setLocation(location);
			 appearance.setVisibleSignature(name);
			 // Creating the signature
			 ExternalSignature pks = new PrivateKeySignature(pk, digestAlgorithm, provider);
			 ExternalDigest digest = new BouncyCastleDigest();
			 MakeSignature.signDetached(appearance, digest, pks, chain,
			 null, null, null, 0, subfilter);
		
	}
	@PostMapping("add")
	public void addSignature( String intitule,@RequestParam("sigmag") MultipartFile sigmag) {
		
		//.println(intitule);
		//System.err.println(intitule);
		//.println(Objects.isNull(sigmag) +"");
		signaturService.setUserSignature(getCurrentUser(),
				sigmag,
				intitule);
	}
	
	
	@GetMapping("usign")
	public File64 getSignature() {
		
		
		
		return signaturService.getUserSignature(getCurrentUser());
	}
	
	@GetMapping("usign/delete")
	public void deleteSignature() {
		
		
		
		signaturService.deleteUserSignImage(getCurrentUser());
	}
	public User getCurrentUser() {
		
		
		
		return signaturService.getCurrentUser();
	}
	
	

}
