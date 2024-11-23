package com.veviosys.vdigit.services;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.barcodes.Barcode39;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImage;
import com.itextpdf.text.pdf.PdfIndirectObject;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.codec.TiffImage;
import com.veviosys.vdigit.classes.File64;
import com.veviosys.vdigit.models.Document;
import com.veviosys.vdigit.models.DocumentAttributeValue;
import com.veviosys.vdigit.models.DocumentVersion;
import com.veviosys.vdigit.models.DocumentVersionAttributeValue;
import com.veviosys.vdigit.models.Journal;
import com.veviosys.vdigit.models.Signature;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.models.zipMail;
import com.veviosys.vdigit.repositories.DocumentRepo;
import com.veviosys.vdigit.repositories.DocumentVersionRepo;
import com.veviosys.vdigit.repositories.JournalRepo;
import com.veviosys.vdigit.repositories.UserRepository;
import com.veviosys.vdigit.repositories.zipMailRepo;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import lombok.var;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

	@Autowired
	private DocumentRepo dr;
	public final Path root = Paths.get("uploads");
	public Path path = null;
	@Autowired
	UserRepository userRepository;
	@Autowired
	DocumentVersionRepo dvr;
	@Autowired
	JournalRepo jr;
	@Autowired
	UserRepository ur;
	public static final String UploadRoot = System.getProperty("user.dir") + "/upload";
	@Autowired
	zipMailRepo zmr;
	@Autowired
	MasterConfigService masterConfigService;
 
	
	
	@Override
	public void init() {

	}

	@Override
	public void save(final MultipartFile file, final UUID id, final int convert, final int codebar, final float x,
			final float y, final float h, final float w, final String val, final int type, final MultipartFile img)
			throws IOException, DocumentException {
		final Document doc = dr.getOne(id);
		final String pathServ = doc.getPathServer();
		byte[] data = null;
		final File saveInRootPath = new File(pathServ);

		if (convert == 0) {

			try {
				doc.setContentType(file.getContentType());

				if (!saveInRootPath.exists()) {
					saveInRootPath.mkdirs();
				}
				path = Paths.get(saveInRootPath.toString());
				final java.io.InputStream is = file.getInputStream();

				if (type == 1) {
					// Files.copy(,
					// this.path.resolve(id.toString() + "_" + file.getOriginalFilename()));
					ByteArrayInputStream bais = addQrCodeToPdf(is, id.toString(), codebar, x, y, h, w, val, img);
					int available = bais.available();
					data = new byte[available];
					bais.read(data);

					// data = addQrCodeToPdf(is, id.toString(), codebar, x, y, h, w,
					// val).readAllBytes();

				} else {
					System.err.println("enterrrrrrrr");
					ByteArrayInputStream bais = addBarCodeToPdf(is, id.toString(), codebar, x, y, h, w, val, img);
					int available = bais.available();
					data = new byte[available];
					bais.read(data);
					// data = addBarCodeToPdf(is, id.toString(), codebar, x, y, h, w,
					// val).readAllBytes();

				}
				// Files.copy(addBarCodeToPdf(is, id.toString(), codebar, x, y, h, w, val),
				// this.path.resolve(id.toString() + "_" + file.getOriginalFilename()));

				// .println(id.toString() + "_" + file.getOriginalFilename());
				dr.save(doc);
			} catch (final Exception e) {
				e.printStackTrace();
			}

		} else if (convert == 1) {
			if (!Objects.isNull(file)) {
				final ByteArrayOutputStream outBs = new ByteArrayOutputStream();
				// TIFF CONVERT
				// .println(file.getOriginalFilename().split("[.]")[1]);
				if (file.getOriginalFilename().split("[.]")[1].equals("tiff")||file.getOriginalFilename().split("[.]")[1].equals("tif")) {

					final String UploadRoot = System.getProperty("user.dir") + "/upload" + pathServ + "/";
					final com.itextpdf.text.pdf.RandomAccessFileOrArray myTiffFile = new com.itextpdf.text.pdf.RandomAccessFileOrArray(
							file.getBytes());
					// Find number of images in Tiff file
					final int numberOfPages = TiffImage.getNumberOfPages(myTiffFile);

					// .println("pages" + numberOfPages);
					final com.itextpdf.kernel.pdf.PdfDocument TifftoPDF = new com.itextpdf.kernel.pdf.PdfDocument(
							new com.itextpdf.kernel.pdf.PdfWriter(new FileOutputStream(UploadRoot + id.toString() + "_"
									+ file.getOriginalFilename().split("[.]")[0] + ".pdf".toString())));
					// //.println(" " + UploadRoot +
					// id.toString()+"_"+file.getOriginalFilename().toString());
					myTiffFile.close();
					// PdfWriter.getInstance(TifftoPDF, new FileOutputStream(UploadRoot +
					// id.toString()+"_"+file.getOriginalFilename().split("[.]")[0]+".pdf".toString()));

					// Run a for loop to extract images from Tiff file
					// into a Image object and add to PDF recursively
					// for(int i=1;i<=numberOfPages;i++){
					// Image tempImage=TiffImage.getTiffImage(myTiffFile, i);
					// Rectangle tiffPage = new Rectangle(tempImage.getWidth(),
					// tempImage.getHeight());
					// TifftoPDF.add(tiffPage);
					// }
					for (int tiffImageCounter = 1; tiffImageCounter <= numberOfPages; tiffImageCounter++) {

						final ImageData tiffImage = ImageDataFactory.createTiff(file.getBytes(), true, tiffImageCounter,
								true);
						final com.itextpdf.kernel.geom.Rectangle tiffPageSize = new com.itextpdf.kernel.geom.Rectangle(
								tiffImage.getWidth(), tiffImage.getHeight());
						final com.itextpdf.kernel.pdf.PdfPage newPage = TifftoPDF
								.addNewPage(new com.itextpdf.kernel.geom.PageSize(tiffPageSize));

						final PdfCanvas canvas = new PdfCanvas(newPage);
						canvas.addImage(tiffImage, tiffPageSize, false);

					}
					doc.setContentType("application/pdf");
					dr.save(doc);

					TifftoPDF.close();
				}

				else if (file.getOriginalFilename().split("[.]")[1].equals("doc")
						|| file.getOriginalFilename().split("[.]")[1].equals("docx")) {
				}

				try {
					final java.io.InputStream docxInputStream = file.getInputStream();
					final OutputStream outputStream = new FileOutputStream(UploadRoot + id.toString() + "_"
							+ file.getOriginalFilename().split("[.]")[0] + ".pdf".toString());
					final IConverter converter = LocalConverter.builder().build();
					converter.convert(docxInputStream).as(DocumentType.DOCX).to(outputStream).as(DocumentType.PDF)
							.execute();
					outputStream.close();
					doc.setContentType("application/pdf");
					dr.save(doc);

					// .println("success");
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		} else if (convert == 3) {

			try {
				doc.setContentType(file.getContentType());

				if (!saveInRootPath.exists()) {
					saveInRootPath.mkdirs();
				}
				path = Paths.get(saveInRootPath.toString());
				// final java.io.InputStream is = file.getInputStream();
				data = file.getBytes();
				// Files.copy(is, this.path.resolve(id.toString() + "_" +
				// file.getOriginalFilename()));
				// .println(id.toString() + "_" + file.getOriginalFilename());
				dr.save(doc);
			} catch (final Exception e) {
				throw new RuntimeException("Error: " + e.getMessage());
			}

		}
		String datab64 = new String(Base64.getEncoder().encode(data));

		if (!saveInRootPath.exists()) {
			saveInRootPath.mkdirs();
		}
		String pathName = pathServ + id.toString() + "_" + file.getOriginalFilename().split("[.]")[0] + ".documania";
		doc.setPathServer(pathName);
		dr.save(doc);
		// try
		// {
		File f = new File(pathName);
		f.createNewFile();
		FileWriter myWriter = new FileWriter(pathName);
		myWriter.write(datab64);
		myWriter.close();
		// }
		// catch (Exception e) {
		// //System.err.println(e.);
		// }
	}

	public User connectedUser() {
		return ((CostumUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
	}

	public Long connectedUserMaster(final Long userid) {
		Long id;
		id = ur.findUserMaster(userid);
		if (id != null) {
			return id;
		}
		return userid;
	}

	// SAVE PATH IS TEMP

	public Resource load(Document doc, String savePath) {
		/* try { */

		try {
			String data = binaryFileToBase64(doc.getPathServer());

			FileOutputStream fos = new FileOutputStream(savePath);
			data = data.split("==")[0];
			data = data.split("=")[0];
			byte[] decoder = Base64.getMimeDecoder().decode(new String(data));

			fos.write(decoder);
			fos.close();

			final Path file = Paths.get(savePath);
			final Resource resource = new UrlResource(file.toUri());
			return resource;

		} catch (Exception e) {
			// .println("------------heeeeeeeeeeere ");
			System.err.println(e.getMessage());
			return null;
		}

	}

	public String binaryFileToBase64(String path) throws IOException {
		// .println(path);
		BufferedReader br = new BufferedReader(new FileReader(path));

		StringBuilder sb = new StringBuilder();
		String line = br.readLine();

		while (line != null) {
			sb.append(line);
			sb.append(System.lineSeparator());
			line = br.readLine();
		}
		String data = sb.toString();
		br.close();
		return data;
	}

	public File64 loadBase64String(final UUID id,String secondary) throws IOException {

		final Document doc = dr.getOne(id);
		final String contentType = doc.getContentType();
		final String FILE_NAME = doc.getFileName();

		final File64 file64 = new File64();
		file64.setId(id);
		file64.setFileName(FILE_NAME);
		file64.setContentType(contentType);
		// Files.readAllBytes(saveInRootPath);
		BufferedReader br = new BufferedReader(new FileReader(doc.getPathServer()));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();

		while (line != null) {
			sb.append(line);
			sb.append(System.lineSeparator());
			line = br.readLine();
		}
		String data = sb.toString();
		br.close();
		file64.setFileData("data:" + contentType + ";base64," + data);
		if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() >= 2
				&& doc.getClientDoc() == null) {
			final Journal j = new Journal();
			j.setUser(connectedUser());
			j.setDate(new Date());

			String ev = " Type : " + doc.getType().getName();

			final int a = 0;
			for (final DocumentAttributeValue av : doc.getAttributeValues()) {
				if (!av.getAttribute().getName().equals("Fichier")) {
					if (a == 0) {
						ev += " | " + av.getAttribute().getName() + " : " + av.getValue().getValue();
						// a=546;
						// }
						// else{
						// ev+=", "+av.getAttribute().getName()+" ("+av.getValue().getValue()+")";

						// }
					}
				}
				j.setAction(ev);
				j.setComposante("Document");
				j.setMode("C");
				j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));
				if (secondary.equals("true")) {

					User user = ur.getSecondaryUser(connectedUser().getUserId());

					j.setTypeEv("Utilisateur/Consultation/Secondaire Profil");
					if (Objects.nonNull(user)) {
						j.setConnectedSacondaryName(user.getFullName());
					}

				} else {
					j.setTypeEv("Utilisateur/Consultation ");
				}
				jr.save(j);
				// .println(ev);
			}

		}
		return file64;

	}

	public File64 loadBase64StringVersion(final Long id,String secondary) throws IOException {

		final DocumentVersion doc = dvr.findById(id).get();
		final String contentType = doc.getDocument().getContentType();
		final String FILE_NAME = doc.getDocument().getFileName();
		String data64 = binaryFileToBase64(doc.getPathServer());
		final File64 file64 = new File64();
		file64.setFileName(FILE_NAME);
		file64.setContentType(contentType);
		file64.setFileData("data:" + contentType + ";base64," + data64);
		if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() >= 2) {
			final Journal j = new Journal();
			j.setUser(connectedUser());
			j.setDate(new Date());
			String ev = "";
			ev = " Type : " + doc.getDocument().getType().getName();

			final int a = 0;
			for (final DocumentVersionAttributeValue av : doc.getAttributeValues()) {
				if (!av.getAttribute().getName().equals("Fichier")) {
					// if(a==0 )
					// {
					ev += " | " + av.getAttribute().getName() + " : " + av.getValue().getValue();
					// a=546;
					// }
					// else{
					// ev+=", "+av.getAttribute().getName()+" ("+av.getValue().getValue()+")";

					// }
				}
			}
			j.setAction("Utilisateur/Consultation");
			j.setComposante("Version document");
			j.setMode("C");
			
			j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));
			if (secondary.equals("true")) {

				User user = ur.getSecondaryUser(connectedUser().getUserId());

				j.setTypeEv(ev +"/Secondaire Profil");
				if (Objects.nonNull(user)) {
					j.setConnectedSacondaryName(user.getFullName());
				}

			} else {
				j.setTypeEv(ev);
			}
			jr.save(j);

		}

		return file64;

	}

	@Override
	public void delete(final String filename, final UUID id) {
		// TODO Auto-generated method stub

	}

	public String getDocumentContentType(final UUID id) {

		return dr.findContentTypeById(id);
	}

	public Path getDocumentFilePath(final UUID id) {

		final Document doc = dr.getOne(id);

		// final String FILE_NAME = doc.getFileName();

		// final File saveInRootPath = new File(doc.getPathServer());
		// path = Paths.get(saveInRootPath.toString());

		final Path file = Paths.get(doc.getPathServer());

		return file;

	}

	public String getDocumentSaveFilePath(final UUID id) {
		final Document doc = dr.getOne(id);
		final String contentType = doc.getContentType();
		final String MONTH = String.valueOf(doc.getUpload_date().getMonth());
		final String YEAR = String.valueOf(doc.getUpload_date().getYear());
		final String FILE_NAME = doc.getFileName();
		// .println(doc.getPathServer());
		return this.UploadRoot + doc.getPathServer() + "/" + id + "_" + FILE_NAME;

	}

	public String getDocumentVersionSaveFilePath(final UUID id, final int Ver) {
		final Document doc = dr.getOne(id);
		final String FILE_NAME = doc.getFileName();
		String activePath = masterConfigService.findActivePath();

		Date date1 = new Date();
		LocalDate localDate = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		String path = activePath + "\\upload\\versions" + "\\"
				+ Base64.getEncoder().encodeToString((String.valueOf(localDate.getYear())).getBytes()) + "\\"
				+ Base64.getEncoder().encodeToString((String.valueOf(localDate.getMonth())).getBytes()) + "\\"
				+ Base64.getEncoder().encodeToString((String.valueOf(localDate.getDayOfMonth())).getBytes()) + "\\";
		final File saveInRootPath = new File(path);
		if (!saveInRootPath.exists()) {
			saveInRootPath.mkdirs();
		}
		// //.println(FILE_NAME°.
		return path + String.valueOf(Ver) + "_" + FILE_NAME.split("[.]")[0] + ".documania";
	}

	public String saveSignature(final Long userId, final MultipartFile file) {

		String pt = masterConfigService.findActivePath() + "\\upload\\signatures\\" + userId;
		final File saveInRootPath = new File(pt);
		try {
			if (!saveInRootPath.exists()) {
				saveInRootPath.mkdirs();
			}
			
			
			path = Paths.get(saveInRootPath.toString());
			
            
		    File filee = new File(pt + "\\" + file.getOriginalFilename());
		    if(filee.exists()) {
		        return pt + "\\" + file.getOriginalFilename();
		    }else {
		        Files.copy(file.getInputStream(), this.path.resolve(file.getOriginalFilename()));
		    }
			return pt + "\\" + file.getOriginalFilename();

		} catch (final Exception e) {
			throw new RuntimeException("Error: " + e.getMessage());
		}

	}

	public File64 getSignature(final Long userId, final Signature sign) throws IOException {
		File f = new File(sign.getSignGraphicPath());
		if (f.exists()) {
			final String contentType = sign.getContentType();
			path = Paths.get(sign.getSignGraphicPath());

			final File64 file64 = new File64();
			file64.setContentType(contentType);
			file64.setFileData(
					"data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(Files.readAllBytes(path)));

			return file64;
		} else
			return null;

	}

	public ByteArrayInputStream addBarCodeToPdf(final java.io.InputStream is, final String id, final int isBarcode,
			final float x, final float y, final float pageH, final float pageW, final String val, MultipartFile img)
			throws IOException, DocumentException {
//		final ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// create input stream from baos

//		final PdfDocument pdfDoc = new PdfDocument(new PdfReader(inputStream), new PdfWriter(baos));
		// *************************** Add Bacrcode in the first page *****************
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(is);
		final PdfStamper stamper = new PdfStamper(reader, baos);
		if (Objects.nonNull(img) && isBarcode==1) {

			float offset = 0;
			final com.itextpdf.text.Rectangle pagesize = reader.getPageSize(1);
			final float h = pagesize.getHeight();
			final float w = pagesize.getWidth();
			offset = ((h / pageH) + (w / pageW)) / 2;
			final com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(img.getBytes());
			// final PdfImage stream = new PdfImage(image, "QrCode", null);
			final float llx = (x * offset);
			final float lly = (h - (y * offset) - (image.getHeight() * offset));
			final float urx = llx + (image.getWidth() * offset);
			final float ury = lly + (image.getHeight() * offset);
			image.scaleAbsoluteHeight(image.getHeight() * offset);
			image.scaleAbsoluteWidth(image.getWidth() * offset);
			image.setAbsolutePosition(llx, lly);
			final PdfContentByte over = stamper.getOverContent(1);
			over.addImage(image);

		}

		stamper.close();
		reader.close();
		final ByteArrayInputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
		return isFromFirstData;
		// if (isBarcode == 1) {
//
//			final PdfPage pdfPage = pdfDoc.getPage(1);
//
//			final Rectangle pageSize = pdfPage.getPageSize();
//
//			final float PDFheight = pageSize.getHeight();
//			final float PDFWidth = pageSize.getWidth();
//			final float offset = ((PDFheight / h) + (PDFWidth / w)) / 2;
//			final float llx = (x * offset);
//			final float lly = (PDFheight - (y * offset) - 50f);
//
//			/*
//			 * float x = pageSize.getLeft() + 10; float y = pageSize.getBottom() + 10;
//			 */
//			final Barcode39 barcode = new Barcode39(pdfDoc);
//			barcode.setCodeType(Barcode39.ALIGN_LEFT);
//			barcode.setCode(createBarcodeNumberFirstPage(val));
//			//.println();
//			barcode.setBarHeight(50f);
//			// set the text size
//			barcode.setSize(13F);
//			barcode.setBaseline(11F);
//
//			// barcode.setFont();
//
//			final PdfFormXObject barcodeXObject = barcode.createFormXObject(
//					com.itextpdf.kernel.colors.ColorConstants.BLACK, com.itextpdf.kernel.colors.ColorConstants.BLACK,
//					pdfDoc);
//			final PdfCanvas over = new PdfCanvas(pdfPage);
//			over.addXObject(barcodeXObject, llx, lly);
//			// *************************** Add Bacrcode in all the pages *****************
//			/*
//			 * for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) { PdfPage pdfPage =
//			 * pdfDoc.getPage(i); Rectangle pageSize = pdfPage.getPageSize(); float x =
//			 * pageSize.getLeft() + 10; float y = pageSize.getBottom() + 10; Barcode39
//			 * barcode = new Barcode39(pdfDoc); barcode.setCodeType(Barcode39.ALIGN_LEFT);
//			 * barcode.setCode(createBarcodeNumberFirstPage(id));
//			 * 
//			 * //set the text size barcode.setSize(12);
//			 * 
//			 * // barcode.setFont();
//			 * 
//			 * PdfFormXObject barcodeXObject =
//			 * barcode.createFormXObject(com.itextpdf.kernel.color.Color.BLACK,
//			 * com.itextpdf.kernel.color.Color.BLACK, pdfDoc); PdfCanvas over = new
//			 * PdfCanvas(pdfPage); over.addXObject(barcodeXObject, x, y);
//			 * 
//			 * }
//			 */
//
//		}
//
//		pdfDoc.close();
//		final ByteArrayInputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
//		return isFromFirstData;

	}

	public ByteArrayInputStream addQrCodeToPdf(final java.io.InputStream is, final String id, final int isBarcode,
			final float x, final float y, final float pageH, final float pageW, final String val, MultipartFile img)
			throws IOException, WriterException, DocumentException {

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(is);
		final PdfStamper stamper = new PdfStamper(reader, baos);
		float offset = 0;
		final com.itextpdf.text.Rectangle pagesize = reader.getPageSize(1);
		final float h = pagesize.getHeight();
		final float w = pagesize.getWidth();
		offset = ((h / pageH) + (w / pageW)) / 2;
		final com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(img.getBytes());
		// final PdfImage stream = new PdfImage(image, "QrCode", null);
		final float llx = (x * offset);
		final float lly = (h - (y * offset) - (image.getHeight() * offset));
		final float urx = llx + (image.getWidth() * offset);
		final float ury = lly + (image.getHeight() * offset);
		image.scaleAbsoluteHeight(image.getHeight() * offset);
		image.scaleAbsoluteWidth(image.getWidth() * offset);
		image.setAbsolutePosition(llx, lly);
		final PdfContentByte over = stamper.getOverContent(1);
		over.addImage(image);
		stamper.close();
		reader.close();
		final ByteArrayInputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
		return isFromFirstData;

	}

	private String createBarcodeNumberAllPage(final int i, final String id) {
		// TODO Auto-generated method stub
		return id.toUpperCase().concat(String.valueOf(i));
	}

	private String createBarcodeNumberFirstPage(final String id) {
		// TODO Auto-generated method stub
		return id.toUpperCase();
	}

	// @Autowired UserRepository ur;

	public ResponseEntity addImage(final MultipartFile file) throws IOException {
		String activePath = masterConfigService.findActivePath();
		final File saveInRootPath = new File(
				activePath + "\\upload\\photo\\" + connectedUserMaster(connectedUser().getUserId()) + "\\");

		if (!saveInRootPath.exists()) {
			saveInRootPath.mkdirs();
		}

		final User u = connectedUser();

		final File delpath = new File(
				activePath + "\\upload\\photo\\" + connectedUserMaster(connectedUser().getUserId()) + "/"
						+ connectedUser().getUserId().toString() + ".png");
		final boolean result = Files.deleteIfExists(delpath.toPath());

		u.setContetType(file.getContentType());
		ur.save(u);
		path = Paths.get(saveInRootPath.toString());
		final java.io.InputStream is = file.getInputStream();

		// Tika tika = new Tika();
		// String detectedType = tika.detect(file.getBytes());

		Path finalPath = this.path.resolve(connectedUser().getUserId().toString() + ".png");

		Files.copy(is, finalPath);

		final User currentuser = connectedUser();

		currentuser.setPicPath(finalPath.toString());
		ur.save(currentuser);
		// ));
		return new ResponseEntity(HttpStatus.OK);
	}

	public Map<String, String> getImg() throws IOException {
		final User u = connectedUser();
		String activePath = masterConfigService.findActivePath();
		final File saveInRootPath = new File(
				activePath + "\\upload\\photo\\" + connectedUserMaster(u.getUserId()) + "\\");
		path = Paths.get(saveInRootPath.toString());
		// if(path.resolve("other"))
		try {
			final Path file = path.resolve(u.getUserId().toString() + ".png");

			final Map<String, String> ret = new HashMap<String, String>();
			ret.put("img", "data:" + u.getContetType() + ";base64,"
					+ Base64.getEncoder().encodeToString(Files.readAllBytes(file)));
			return ret;

		} catch (Exception e) {
			return null;
		}
	}

	String rootlogo = System.getProperty("user.dir") + "/applogo";

	public Resource loadLogoBrut(String name) throws MalformedURLException {
		final File saveInRootPath = new File(rootlogo);
		path = Paths.get(saveInRootPath.toString());
		final Path file = path.resolve(name);
		final Resource resource = new UrlResource(file.toUri());
		return resource;
	}

	public Boolean deleteImage() throws IOException {
		// TODO Auto-generated method stub
		final User u = connectedUser();
		String activePath = masterConfigService.findActivePath();
		final File delpath = new File(
				activePath + "\\upload\\photo\\" + connectedUserMaster(connectedUser().getUserId()) + "\\"
						+ connectedUser().getUserId().toString() + ".png");
		final boolean result = Files.deleteIfExists(delpath.toPath());
		if (result) {
			u.setPicPath(null);
			userRepository.save(u);

		}
		return result;

	}

	public Boolean deleteUserSignature(Long userId, Signature sign) throws IOException {
		// TODO Auto-generated method stub
		Path pathSig = Paths.get(sign.getSignGraphicPath());

		final boolean result = Files.deleteIfExists(pathSig);

		return result; 
	}

	public zipMail saveZip(List<UUID> docs, zipMail zMail) throws IOException {
		// ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destZipFile));
	    
        String mc = masterConfigService.findActivePath();
        
        final File saveInRootPath = new File(mc + "\\upload\\tempfiles\\");
        
        if (!saveInRootPath.exists()) {
         saveInRootPath.mkdirs();
         }
         
        Path pathe = Paths.get(saveInRootPath.toString());
        
		Date d = new Date();
		LocalDate localDate = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		String pathS = "\\" + Base64.getEncoder().encodeToString((String.valueOf(localDate.getYear())).getBytes())
				+ "\\" + Base64.getEncoder().encodeToString((String.valueOf(localDate.getMonth())).getBytes()) + "\\"
				+ Base64.getEncoder().encodeToString((String.valueOf(localDate.getDayOfMonth())).getBytes());
		if (zMail.getHasPassword() == 0) {
			zMail.setPassowrd("");
		}
		zMail = zmr.saveAndFlush(zMail); 
		// z.s
		String cheminTemp = mc + "\\upload\\tempfiles\\";
		List<File> files = new ArrayList<File>(); 
		for (UUID id : docs) {
			Document doc = dr.findById(id).orElse(null);
			String path = doc.getPathServer();
			String savePath = cheminTemp + id + "_" + doc.getFileName();
			// .println(savePath);
			String data = binaryFileToBase64(doc.getPathServer()).split("==")[0];
			FileOutputStream fos = new FileOutputStream(savePath);

			byte[] decoder = Base64.getMimeDecoder().decode(data.getBytes());

			fos.write(decoder);
			fos.close();
			File file = new File(cheminTemp + doc.getId() + "_" + doc.getFileName());
 
			
			files.add(file);
		}

		String chemin = mc + "\\upload\\zip" + pathS + "\\";
		File saveIn = new File(chemin);
		if (!saveIn.exists()) {
			saveIn.mkdirs();
		}
		File saveInP = new File(chemin + zMail.getId() + ".zip");

		FileOutputStream fos = new FileOutputStream(saveInP);

		ZipOutputStream zipOutputStream = new ZipOutputStream(fos);

		for (int i = 0; i < files.size(); i++) {
			File file = files.get(i);
			zipOutputStream.putNextEntry(new ZipEntry(docs.get(i) + "_" + file.getName()));
			FileInputStream fileInputStream = new FileInputStream(file);
			IOUtils.copy(fileInputStream, zipOutputStream);
			fileInputStream.close();
			zipOutputStream.closeEntry();
			file.delete();
		}

		zMail.setPath(chemin + zMail.getId() + ".zip");
		zMail = zmr.saveAndFlush(zMail);

		zipOutputStream.close();
		return zMail;
	}
	
	public Path pathe = null;

	public ResponseEntity<StreamingResponseBody> zipFiles(List<UUID> docs) {
		return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=\"test.zip\"").body(out -> {
			var zipOutputStream = new ZipOutputStream(out);
			String mc = masterConfigService.findActivePath();
			String cheminTemp = mc + "\\upload\\tempfiles\\";
		
			final File saveInRootPath = new File(cheminTemp);
            
            if (!saveInRootPath.exists()) {
               saveInRootPath.mkdirs();
            }
            
            pathe = Paths.get(saveInRootPath.toString());
			ArrayList<File> files = new ArrayList<File>();
			for (UUID id : docs) {
				Document doc = dr.findById(id).orElse(null);
				  
                
                 
				String savePath = cheminTemp + id + "_" + doc.getFileName();
				// .println(savePath);
				String data = binaryFileToBase64(doc.getPathServer()).split("==")[0].replaceAll("\\n", "").replaceAll("\\r", "");
				FileOutputStream fos = new FileOutputStream(savePath);
				 
				byte[] decoder = Base64.getDecoder().decode(new String(data));
				fos.write(decoder);
				fos.close();
				File file = new File(cheminTemp + doc.getId() + "_" + doc.getFileName());
				files.add(file);
			} 

			// package files
			for (int i = 0; i < files.size(); i++) {
				File file = files.get(i);
				zipOutputStream.putNextEntry(new ZipEntry(docs.get(i) + "_" + file.getName()));
				FileInputStream fileInputStream = new FileInputStream(file);
				IOUtils.copy(fileInputStream, zipOutputStream);
				fileInputStream.close();
				zipOutputStream.closeEntry();
				file.delete();
			}

			zipOutputStream.close();
		});
	}

	public File64 downloadZip(zipMail z) throws IOException {

		final String pathServ = z.getPath();
		final Path file = Paths.get(pathServ);
		final File64 file64 = new File64();

		file64.setContentType("application/zip");
		file64.setFileData(
				"data:application/zip" + ";base64," + Base64.getEncoder().encodeToString(Files.readAllBytes(file)));

		return file64;
	}

	public String getActivePath() {
		return masterConfigService.findActivePath();
	}

	@Override
	public void saveFile(MultipartFile file, UUID id, int convert, int codebar, float x, float y, float h, float w,
			String val, int type, MultipartFile img)
			throws com.itextpdf.io.IOException, DocumentException, IOException {

		final Document doc = dr.findById(id).orElse(null);
		final String pathServ = doc.getPathServer();
		byte[] data = null;
		final File saveInRootPath = new File(pathServ);
		InputStream is = file.getInputStream();
		
		
		
		doc.setContentType(file.getContentType());
		if (!saveInRootPath.exists()) {
			saveInRootPath.mkdirs();
		}
		path = Paths.get(saveInRootPath.toString());
		
		
		
		if (file.getContentType().equals("application/pdf")) {

			ByteArrayInputStream bais = addBarCodeToPdf(is, id.toString(), codebar, x, y, h, w, val, img);
			int available = bais.available();
			data = new byte[available];
			bais.read(data);
      
		}else {
			
			data = file.getBytes();
			
		}
		
		
		
		String datab64 = new String(Base64.getEncoder().encode(data));

		if (!saveInRootPath.exists()) {
			saveInRootPath.mkdirs();
		}
		String pathName = pathServ + id.toString() + "_" + file.getOriginalFilename().split("[.]")[0] + ".documania";
		doc.setPathServer(pathName);
		dr.save(doc);
		
		File f = new File(pathName);
		f.createNewFile();
		FileWriter myWriter = new FileWriter(pathName);
		myWriter.write(datab64);
		myWriter.close();
		
		

	}
	public String zipDocs(List<Document> docs) throws IOException {
		// ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destZipFile));
		Date d = new Date();
		LocalDate localDate = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		String pathS = "\\" + Base64.getEncoder().encodeToString((String.valueOf(localDate.getYear())).getBytes())
				+ "\\" + Base64.getEncoder().encodeToString((String.valueOf(localDate.getMonth())).getBytes()) + "\\"
				+ Base64.getEncoder().encodeToString((String.valueOf(localDate.getDayOfMonth())).getBytes());
 
		// z.s
		String mc = masterConfigService.findActivePath();
		String cheminTemp = mc + "\\upload\\tempfiles\\";
		List<File> files = new ArrayList<File>();
		for (Document doc : docs) {
			 
			String path = doc.getPathServer();
			String savePath = cheminTemp + doc.getId() + "_" + doc.getFileName();
			// .println(savePath);
			String data = binaryFileToBase64(doc.getPathServer()).split("==")[0];
			FileOutputStream fos = new FileOutputStream(savePath);
		 
			byte[] decoder = Base64.getMimeDecoder().decode(data.getBytes());

			fos.write(decoder);
			fos.close();
			File file = new File(cheminTemp + doc.getId() + "_" + doc.getFileName());

			files.add(file);
		}

		String chemin = mc + "\\upload\\zip" + pathS + "\\";
		File saveIn = new File(chemin);
		if (!saveIn.exists()) {
			saveIn.mkdirs();
		}
	String	zipPath=chemin +UUID.randomUUID() + ".zip";
		File saveInP = new File(zipPath);

		FileOutputStream fos = new FileOutputStream(saveInP);

		ZipOutputStream zipOutputStream = new ZipOutputStream(fos);

		for (int i = 0; i < files.size(); i++) {
			File file = files.get(i);
			zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
			FileInputStream fileInputStream = new FileInputStream(file);
			IOUtils.copy(fileInputStream, zipOutputStream);
			fileInputStream.close();
			zipOutputStream.closeEntry();
			file.delete();
		}
 

		zipOutputStream.close();
		return zipPath;
		 
	}
	
	
	
	
	public File64 downfile(UUID id,String secondary) throws IOException {
		File64 data=loadBase64String(id,secondary);
		Document doc=dr.findById(id).orElse(null);
		if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() >= 2
				&& doc.getClientDoc() == null) {
			final Journal j = new Journal();
			j.setUser(connectedUser());
			j.setDate(new Date());

			String ev = " Type : " + doc.getType().getName();

			final int a = 0;
			for (final DocumentAttributeValue av : doc.getAttributeValues()) {
				if (!av.getAttribute().getName().equals("Fichier")) {
					if (a == 0) {
						ev += " | " + av.getAttribute().getName() + " : " + av.getValue().getValue();
					 
					}
				}
				j.setAction(ev);
				j.setComposante("Document");
				j.setMode("C");
				j.setTypeEv("Utilisateur/Télécharger");
				j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));
				if (secondary.equals("true")) {

					User user = ur.getSecondaryUser(connectedUser().getUserId());

					j.setTypeEv("Utilisateur/Télécharger/Secondaire Profil");
					if (Objects.nonNull(user)) {
						j.setConnectedSacondaryName(user.getFullName());
					}

				} else {
					j.setTypeEv("Utilisateur/Télécharger");
				}
				jr.save(j);
				// .println(ev);
			}
			}
		return data;
	}
	
	public String createTempConvertFile(UUID id,String activePath,Document doc) throws IOException {
		 
		
		String tempFolder = activePath + "\\upload\\tempfiles\\";
		File saveIn=new File(tempFolder);
		if (!saveIn.exists()) {
			saveIn.mkdirs();
		}
		
		String path = doc.getPathServer();
		String savePath = tempFolder + id + "_" + doc.getFileName();
		// .println(savePath);
		String data = binaryFileToBase64(doc.getPathServer()).split("==")[0];
		FileOutputStream fos = new FileOutputStream(savePath);

		byte[] decoder = Base64.getMimeDecoder().decode(data.getBytes());

		fos.write(decoder);
		fos.close();
		return savePath;
	}
	public void deleteTempFiles(List<String>files) {
		for (String path : files) {
			File f=new File(path);
			if(f.exists()) {
				f.delete();
			}
		}
	}
	


}
