package com.veviosys.vdigit.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfSignatureAppearance.RenderingMode;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.CrlClient;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import com.veviosys.vdigit.classes.File64;
import com.veviosys.vdigit.models.Document;
import com.veviosys.vdigit.models.DocumentAttributeValue;
import com.veviosys.vdigit.models.DocumentVersion;
import com.veviosys.vdigit.models.DocumentVersionAttributeValue;
import com.veviosys.vdigit.models.Signature;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.models.ValueVersion;
import com.veviosys.vdigit.models.pk.DocumentVesionAttributeValuePK;
import com.veviosys.vdigit.repositories.DocumentRepo;
import com.veviosys.vdigit.repositories.DocumentVersionAttributeValueRepo;
import com.veviosys.vdigit.repositories.DocumentVersionRepo;
import com.veviosys.vdigit.repositories.SignatureRepository;
import com.veviosys.vdigit.repositories.ValueVersionRepo;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SignService {

	@Autowired
	private SignatureRepository signatureRepository;

	@Autowired
	private AlimentationService alimService;

	@Autowired
	private FileStorageServiceImpl fileStorageServiceImpl;

	@Autowired
	DocumentRepo dr;
	@Autowired
	DocumentVersionRepo dvr;
	@Autowired
	private DocumentVersionAttributeValueRepo dvavr;
	@Autowired
	private ValueVersionRepo vvr;
	public static final String UploadRoot = System.getProperty("user.dir") + "/upload/versions";

	// private float offset=0;
	public User getUser() {
		CostumUserDetails user = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		return user.getUser();
	}

	public void saveV(UUID docId, Path path) throws IOException {
		Document dd = dr.findById(docId).orElse(null);
		int lastV = 0;

		List<DocumentVersion> listVersion = dvr.findByDocument(dd);
		DocumentVersion dv = new DocumentVersion();
		if (listVersion != null) {
			for (DocumentVersion documentVersion : listVersion) {
				if (documentVersion.getNumVersion() > lastV) {
					lastV = documentVersion.getNumVersion();
				}
			}
		}
		lastV++;
		dv.setDocument(dd);
		dv.setEdit_date(new Date());
		dv.setContentType(dd.getContentType());
		dv.setEditedBy(getUser());
		dv.setNumVersion(lastV);
		dv = dvr.saveAndFlush(dv);
		for (DocumentAttributeValue doc : dd.getAttributeValues()) {

			DocumentVesionAttributeValuePK pkId = new DocumentVesionAttributeValuePK(dv.getId(),
					doc.getAttribute().getId());
			DocumentVersionAttributeValue dvav = new DocumentVersionAttributeValue();

			ValueVersion vv = new ValueVersion();
			vv.setValue(doc.getValue().getValue());
			dvav.setId(pkId);
			vv = vvr.saveAndFlush(vv);
			dvav.setValue(vv);
			dvav.setDocument(dv);

			dvav.setAttribute(doc.getAttribute());

			dvavr.save(dvav);
		}
		Document doc = dr.getOne(docId);
		String SavePath = fileStorageServiceImpl.getDocumentVersionSaveFilePath(docId, lastV);

		FileOutputStream os = new FileOutputStream(SavePath);
		os.close();
	}

	public void sign(UUID docId, Certificate[] chain, PrivateKey pk, String digestAlgorithm,

			String provider, CryptoStandard subfilter, String reason, String location, String tmp, byte[] img,
			float imgX, float imgY, int page, float pageH, float pageW, List<CrlClient> crlClients)
			throws GeneralSecurityException, IOException, DocumentException {
		// Creating the reader and the stamper
		/*
		 * int lastV=0;
		 * 
		 * List<DocumentVersion> listVersion=dvr.findByDocument(dd); DocumentVersion
		 * dv=new DocumentVersion(); if(listVersion!=null) { for (DocumentVersion
		 * documentVersion : listVersion) { if(documentVersion.getNumVersion()>lastV) {
		 * lastV=documentVersion.getNumVersion(); } } } lastV++;
		 */
		Path docPath = fileStorageServiceImpl.getDocumentFilePath(docId);
		Document dd = dr.findById(docId).orElse(null);
		BufferedReader br = new BufferedReader(new FileReader(dd.getPathServer()));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();

		while (line != null) {
			sb.append(line);
			sb.append(System.lineSeparator());
			line = br.readLine();
		}
		String data = sb.toString();
		br.close();

		String SavePath = dd.getPathServer();
		// fileStorageServiceImpl.getDocumentSaveFilePath(dd);
		//.println(data);
		PdfReader reader = new PdfReader(com.itextpdf.text.pdf.codec.Base64.decode(data));
		// PdfReader reader = new PdfReader(Files.readAllBytes(docPath));
		int lastV = 0;

		List<DocumentVersion> listVersion = dvr.findByDocument(dd);
		DocumentVersion dv = new DocumentVersion();
		if (listVersion != null) {
			for (DocumentVersion documentVersion : listVersion) {
				if (documentVersion.getNumVersion() > lastV) {
					lastV = documentVersion.getNumVersion();
				}
			}
		}
		lastV++;
		dv.setDocument(dd);
		dv.setEdit_date(new Date());
		dv.setContentType(dd.getContentType());
		dv.setEditedBy(getUser());
		dv.setNumVersion(lastV);
		dv.setEditType("Document"); 
		String SavePath1 = fileStorageServiceImpl.getDocumentVersionSaveFilePath(docId, lastV);
		dv.setPathServer(SavePath1);
		dv = dvr.saveAndFlush(dv);

		for (DocumentAttributeValue doc : dd.getAttributeValues()) {

			DocumentVesionAttributeValuePK pkId = new DocumentVesionAttributeValuePK(dv.getId(),
					doc.getAttribute().getId());
			DocumentVersionAttributeValue dvav = new DocumentVersionAttributeValue();

			ValueVersion vv = new ValueVersion();
			vv.setValue(doc.getValue().getValue());
			dvav.setId(pkId);
			vv = vvr.saveAndFlush(vv);
			dvav.setValue(vv);
			dvav.setDocument(dv);

			dvav.setAttribute(doc.getAttribute());

			dvavr.save(dvav);
		}
		Document doc = dr.getOne(docId);

		File f = docPath.toFile();
		Files.copy(docPath, new FileOutputStream(SavePath1));
		// String data= fileStorageServiceImpl.binaryFileToBase64(SavePath);
		String clonePath=SavePath.split("[.]")[0]+".pdf";
		File fclone=new File(clonePath);
		fclone.createNewFile();
		FileOutputStream os = new FileOutputStream(clonePath);
		PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0', new File(tmp + docPath.getFileName()), true);
		// get pdf Hiegth and Width
		float offset = 0;
		Rectangle pagesize = reader.getPageSize(1);

		float h = pagesize.getHeight();
		float w = pagesize.getWidth();

		offset = ((h / pageH) + (w / pageW)) / 2;

		// image

		com.itextpdf.text.Image signImg = com.itextpdf.text.Image.getInstance(img);
		// Creating the appearance
		PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
		// appearance.setReason(reason);
		appearance.setLocation(location);
		//.println(" " + pagesize.getBottom());
		float llx = (imgX * offset);
		float lly = (h - (imgY * offset) - (signImg.getHeight() * offset));
		float urx = llx + (signImg.getWidth() * offset);
		float ury = lly + (signImg.getHeight() * offset);

		Rectangle react = new Rectangle(llx, lly, urx + 100, ury, 0);
	

		appearance.setVisibleSignature(react, page, appearance.getNewSigName());
		appearance.setRenderingMode(RenderingMode.GRAPHIC_AND_DESCRIPTION);
	
		appearance.setSignatureGraphic(Image.getInstance(signImg));
		X509Certificate cert = (X509Certificate)chain[0];
		X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();
		RDN cn = x500name.getRDNs(BCStyle.CN)[0];
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
		Calendar now = Calendar.getInstance();
	
		appearance.setLayer2Text("Signé numériquement par "+cn.getFirst().getValue()+"\nLe "+dateFormat.format(now.getTime()));

		ExternalDigest digest = new BouncyCastleDigest();
		ExternalSignature signature = new PrivateKeySignature(pk, digestAlgorithm, provider);
		MakeSignature.signDetached(appearance, digest, signature, chain, crlClients, null, null, 0, subfilter);
		//System.out.println(SavePath);
		// String clonePath=SavePath.split("[.]")[0]+".pdf";
		// File fclone=new File(clonePath);
		// fclone.createNewFile();

		 FileInputStream is = new FileInputStream(clonePath);
		
		
		 
		byte[] fileContent = new byte[is.available()];
		
		is.read(fileContent);
		is.close();	
		String datab64 = new String(Base64.getEncoder().encode(fileContent));
		FileWriter myWriter = new FileWriter(doc.getPathServer());
	 
		myWriter.write(datab64);
		myWriter.close();
		fclone.delete();
	}

	public float fixSize(float pxSize) {
		return pxSize - (pxSize / 75);
	}

	public float calulateLly(float percentage, float height) {
		return percentage * height / 100;
	}

	public void setUserSignature(User user, MultipartFile file, String intitule) {

		Signature sign = signatureRepository.findByUserUserId(user.getUserId());
		String path = fileStorageServiceImpl.saveSignature(user.getUserId(), file);
		
		if(Objects.isNull(sign))
		{
			sign=new Signature();sign.setUser(user);sign.setIntitule(intitule);
		}
		
		sign.setSignGraphicPath(path);
		
		sign.setContentType(file.getContentType());
		signatureRepository.save(sign);
	}

	public File64 getUserSignature(User user) {

		if (Objects.isNull(user)) {
			user = getCurrentUser();
		}
		Signature sign = signatureRepository.findByUserUserId(getCurrentUser().getUserId());

		try {
			if(Objects.nonNull(sign))
		{	File64 file = fileStorageServiceImpl.getSignature(user.getUserId(), sign);
			 
			return file;}
		} catch (IOException e) {
 
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public User getCurrentUser() {
		return alimService.connectedUser();

	}

	public File64 deleteUserSignImage(User currentUser) {

		User user = getCurrentUser();
		Signature sign = signatureRepository.findByUserUserId(user.getUserId());

		try {
			Boolean res = fileStorageServiceImpl.deleteUserSignature(user.getUserId(), sign);
			if (res) {

				signatureRepository.delete(sign);

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
