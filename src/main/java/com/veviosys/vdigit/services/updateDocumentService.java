package com.veviosys.vdigit.services;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.veviosys.vdigit.classes.AttributeClass;
import com.veviosys.vdigit.classes.DocumnentClass;
import com.veviosys.vdigit.classes.NativeQueriesUtils;
import com.veviosys.vdigit.classes.mapSearch;
import com.veviosys.vdigit.models.AttributeValue;
import com.veviosys.vdigit.models.ClientDoc;
import com.veviosys.vdigit.models.Document;
import com.veviosys.vdigit.models.DocumentAttributeValue;
import com.veviosys.vdigit.models.DocumentFolder;
import com.veviosys.vdigit.models.DocumentFullText;
import com.veviosys.vdigit.models.DocumentType;
import com.veviosys.vdigit.models.DocumentVersion;
import com.veviosys.vdigit.models.DocumentVersionAttributeValue;
import com.veviosys.vdigit.models.Folder;
import com.veviosys.vdigit.models.Groupe;
import com.veviosys.vdigit.models.InterneMessage;
import com.veviosys.vdigit.models.Journal;
import com.veviosys.vdigit.models.Nature;
import com.veviosys.vdigit.models.PermissionDocumentType;
import com.veviosys.vdigit.models.PermissionGroup;
import com.veviosys.vdigit.models.PermissionGroupN;
import com.veviosys.vdigit.models.PermissionNatureCourrier;
import com.veviosys.vdigit.models.RecieveMessage;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.models.ValueVersion;
import com.veviosys.vdigit.models.pk.DocumentAttributeValuePK;
import com.veviosys.vdigit.models.pk.DocumentVesionAttributeValuePK;
import com.veviosys.vdigit.reposietories.AttributeRepo;
import com.veviosys.vdigit.reposietories.AttributeValueRepo;
import com.veviosys.vdigit.repositories.ClientDocRepo;
import com.veviosys.vdigit.repositories.ClientRepo;
import com.veviosys.vdigit.repositories.DocumentAttributeValueRepo;
import com.veviosys.vdigit.repositories.DocumentFolderRepo;
import com.veviosys.vdigit.repositories.DocumentFulltextRepository;
import com.veviosys.vdigit.repositories.DocumentRepo;
import com.veviosys.vdigit.repositories.DocumentTypeRepo;
import com.veviosys.vdigit.repositories.DocumentVersionAttributeValueRepo;
import com.veviosys.vdigit.repositories.DocumentVersionRepo;
import com.veviosys.vdigit.repositories.FolderRepo;
import com.veviosys.vdigit.repositories.FolderTypeRepo;
import com.veviosys.vdigit.repositories.GroupRepo;
import com.veviosys.vdigit.repositories.InterneMessageRepo;
import com.veviosys.vdigit.repositories.JournalRepo;
import com.veviosys.vdigit.repositories.NatureRepo;
import com.veviosys.vdigit.repositories.PermissionGroupNRepo;
import com.veviosys.vdigit.repositories.PermissionGroupRepo;
import com.veviosys.vdigit.repositories.ReceiveMessageRepo;
import com.veviosys.vdigit.repositories.UserRepository;
import com.veviosys.vdigit.repositories.ValueVersionRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class updateDocumentService {
	@Autowired
	private UserRepository ur;
	@Autowired
	private FolderRepo fr;
	@Autowired
	private DocumentFolderRepo dfr; 
	@Autowired
	private DocumentRepo dr;
	@Autowired
	private AttributeValueRepo avr;
	@Autowired
	private DocumentAttributeValueRepo davr;
 	@Autowired
	private DocumentVersionRepo dvr;
	@Autowired
	private DocumentVersionAttributeValueRepo dvavr;
	@Autowired
	private ValueVersionRepo vvr;
	@Autowired
	private PermissionGroupRepo pgr;
	@Autowired
	private PermissionGroupNRepo pgnr;
 	@Autowired
	private InterneMessageRepo imr;
	@Autowired
	private ReceiveMessageRepo rmr;
	@Autowired
	GroupRepo gr;
	@Autowired
	JournalRepo jr;
@Autowired
DocumentFulltextRepository documentFulltextRepository;
	public User getUser() {
		CostumUserDetails user = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		return user.getUser();
	}

	public User connectedUser() {
		return ((CostumUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
	}

	public Long connectedUserMaster(Long userid) {
		Long id;
		id = ur.findUserMaster(userid);
		if (id != null) {
			return id;
		}
		return userid;
	}
	public ResponseEntity editDocProcess(DocumnentClass dc) throws SQLException {
		Document dd = dr.findById(dc.id).orElse(null);
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
		dv.setContentType(dd.getContentType());
		dv.setDocument(dd);
		dv.setEdit_date(new Date());
		dv.setEditedBy(getUser());
		dv.setNumVersion(lastV);
		dv.setPathServer(dd.getPathServer());
		dv.setEditType("Fiche signalétique");
		dv.setHTML_CONTENT(dd.getHTML_CONTENT());
		dv = dvr.saveAndFlush(dv);
		for (DocumentAttributeValue doc : dd.getAttributeValues()) {
		
		 DocumentVesionAttributeValuePK pk = new DocumentVesionAttributeValuePK(dv.getId(),
		doc.getAttribute().getId());
		DocumentVersionAttributeValue dvav = new DocumentVersionAttributeValue();
		
		 ValueVersion vv = new ValueVersion();
		
		 vv.setValue(doc.getValue().getValue());
		dvav.setId(pk);
		vv = vvr.saveAndFlush(vv);
		dvav.setValue(vv);
		dvav.setDocument(dv);
		
		 dvav.setAttribute(doc.getAttribute());
		
		 dvavr.save(dvav);
		}
		for (AttributeClass attr : dc.attrs) {
		DocumentAttributeValuePK pk = new DocumentAttributeValuePK(dc.id, attr.id);
		AttributeValue av = davr.findById(pk).orElse(null).getValue();
		av.setValue(attr.val);
		avr.save(av);
		
		 }
		List<String> listAtrrs = new ArrayList();
		
		//  dd.getType().getName().replace(' ', '_') + 
		String query = "update d" + dd.getType().getId() + " set";
		int index = 0;
		for (AttributeClass attrsClass : dc.attrs) {
		if (!attrsClass.name.equals("HTML_CONTENT")) {
		if (index == 0&&Objects.nonNull(attrsClass.val)) {
		query += " " + (attrsClass.name.equals("Date") ? "Date_" : attrsClass.name).replace(' ', '_');
		listAtrrs.add(attrsClass.val.replace("'", "''"));
		query += "= ?";
		index = 14;
		} else if(Objects.nonNull(attrsClass.val)) {
		//System.out.println(attrsClass.name + "---" + attrsClass.val);
		query += "," + (attrsClass.name.equals("Date") ? "Date_" : attrsClass.name).replace(' ', '_');
		listAtrrs.add(attrsClass.val.replace("'", "''"));
		query += "= ?";
		}
		}
		}
		
		 query += " where id= ? ";
		
		 dd.setLast_edit_date((new Date()));
		dr.save(dd);
		//System.out.println(query);
		Connection conn = DriverManager.getConnection(db, dbus, dbpw);
		PreparedStatement pe = conn.prepareStatement(query);
		int i = 1;
		for (String string : listAtrrs) {
            pe.setString(i, string);
            i++;
        }
		NativeQueriesUtils.setParamToPreparedStatement(pe, i++,  dc.id.toString());
		pe.executeUpdate();
		conn.close();
		return new ResponseEntity(HttpStatus.OK);
		}
	 
	public ResponseEntity updateDoc(DocumnentClass dc,String secondary) throws SQLException {
		Document dd = dr.findById(dc.id).orElse(null);
		List<mapSearch> lsOld = new ArrayList<mapSearch>();
		List<mapSearch> lsNew = new ArrayList<mapSearch>();

		for (DocumentAttributeValue v : dd.getAttributeValues()) {
			lsOld.add(new mapSearch(v.getAttribute().getName(), v.getValue().getValue(),""));
		}
		if (hasAccessToEdit(dc.id) == 1) {
			if (dd.getType().getIsVersionable() == 1) {
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
				dv.setContentType(dd.getContentType());
				dv.setDocument(dd);
				dv.setEdit_date(new Date());
				dv.setEditedBy(getUser());
				dv.setNumVersion(lastV);
				 
				dv.setEditType("Fiche signalétique");
				dv.setHTML_CONTENT(dd.getHTML_CONTENT());
				dv = dvr.saveAndFlush(dv);
				for (DocumentAttributeValue doc : dd.getAttributeValues()) {
					if (!doc.getAttribute().getName().equals("HTML_CONTENT")) {
						DocumentVesionAttributeValuePK pk = new DocumentVesionAttributeValuePK(dv.getId(),
								doc.getAttribute().getId());
						DocumentVersionAttributeValue dvav = new DocumentVersionAttributeValue();

						ValueVersion vv = new ValueVersion();
						vv.setValue(doc.getValue().getValue());
						dvav.setId(pk);
						//System.out.println(vv.getValue());
						vv = vvr.saveAndFlush(vv);
						dvav.setValue(vv);
						dvav.setDocument(dv);

						dvav.setAttribute(doc.getAttribute());

						dvavr.save(dvav);
					}
				}
			}
			for (AttributeClass attr : dc.attrs) {
				DocumentAttributeValuePK pk = new DocumentAttributeValuePK(dc.id, attr.id);
				AttributeValue av = davr.findById(pk).orElse(null).getValue();
				av.setValue(attr.val);
				lsNew.add(new mapSearch(attr.name, attr.val,""));
				avr.save(av);

			}
			// dd.getType().getName().replace(' ', '_') +
			 List<String> listAtrrs = new ArrayList();
			 
			String query = "update d" + dd.getType().getId() + " set";
			int index = 0;
			String ref = "";
			for (AttributeClass attrsClass : dc.attrs) {
				if (Objects.nonNull(attrsClass.val)) {
					if (index == 0) {
						query += "  " + (attrsClass.name.equals("Date") ? "Date_" : attrsClass.name).replace(' ', '_');
						listAtrrs.add(attrsClass.val.replace("'", "''"));
						query += "= ?";
						index = 14;
						if (attrsClass.name.equals("Réference"))
							ref = attrsClass.val;
					} else {
						query += "," + (attrsClass.name.equals("Date") ? "Date_" : attrsClass.name).replace(' ', '_');
						listAtrrs.add(attrsClass.val.replace("'", "''"));
						query += "= ?";
					}

				}
			}

			query += " where id= ?";
			System.out.println(query);
			dd.setLast_edit_date((new Date()));
			dr.save(dd);
			//System.out.println(query);
			Connection conn = DriverManager.getConnection(db, dbus, dbpw);
			PreparedStatement pee = conn.prepareStatement(query);
	        int ji = 1;
	        for (String string : listAtrrs) {
	            pee.setString(ji, string);
	            ji++;
	        }
	        pee.setString(ji++, dc.id.toString());
	        pee.executeUpdate();
			conn.close();
			if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() >= 1) {
				Journal j = new Journal();

				j.setUser(connectedUser());
				j.setDate(new Date());
				
				j.setMode("M");
				j.setComposante("Document");
				if (secondary.equals("true")) {

                    User user = ur.getSecondaryUser(connectedUser().getUserId());

                    j.setTypeEv("Utilisateur/Modification/Secondaire Profil");

                    if (Objects.nonNull(user)) {
                        j.setConnectedSacondaryName(user.getFullName());
                    }

                }else {
                    j.setTypeEv("Utilisateur/Modification");
                }
				String ch = "";
				int pe = 0;
				for (int i = 0; i < lsOld.size(); i++) {
					mapSearch ms = lsOld.get(i);
					for (mapSearch mapSearch : lsNew) {
						if(ms.key.equals(mapSearch.key) &&!ms.key.equals("Fichier")) {
							if (pe == 0) {
								ch += " " + ms.key + " : " + ms.value + " --> " + mapSearch.value;
								pe = 123456;
							} else {
								ch += "| " + ms.key + " : " + ms.value + " --> " + mapSearch.value;

							
						}
						}
					}
						
			/*		if (!ms.key.equals("Fichier") && false) {
						if (pe == 0) {
							ch += " " + ms.key + " : " + ms.value + " --> " + lsNew.get(i).value;
							pe = 123456;
						} else {
							ch += "| " + ms.key + " : " + ms.value + " --> " + lsNew.get(i).value;

						}
					}*/

				}
				j.setAction(ch);
				j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

				jr.save(j);
			}
			return new ResponseEntity(HttpStatus.OK);
		}
		return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public int hasAccessToDelete(UUID id) {
		DocumentType dt = dr.findById(id).orElse(null).getType();

		/*if (getUser().getMaster() == null) {
			return 1;
		}*/
		List<Groupe> groups = gr.findGroupeByUsersUserId(connectedUser().getUserId());
		for (Groupe groupe : groups) {
			List<PermissionGroup> permissionGroups = pgr.findByGroup(groupe);
			for (PermissionGroup pd : permissionGroups) {

				for (PermissionDocumentType pdt : pd.getPermissionDocument().getPermissionDocumentTypes()) {

					if (dt.getId() == pdt.getDocumentType().getId()
							&& pdt.getPermissionDocument().getAcces().contains("D")

							 ) {
						return 1;
					}
				}
			}
		}
		return -1;

	}

	public int hasAccessToDeleteN(UUID id) {

		/*if (getUser().getMaster() == null) {
			return 1;
		}*/
		Folder f = fr.findById(id).orElse(null);
		Nature n = f.getNature();
		List<Groupe> groups = gr.findGroupeByUsersUserId(connectedUser().getUserId());
		for (Groupe groupe : groups) {
			List<PermissionGroupN> permissionGroups = pgnr.findByGroup(groupe);
			for (PermissionGroupN pd : permissionGroups) {

				for (PermissionNatureCourrier pdt : pd.getPermissionCourrier().getPermissionNature()) {

					if (n.getId() == pdt.getNature().getId() && (pdt.getPermissionCourrier().getAcces().contains("D"))

							 ) {
						return 1;
					}
				}
			}
		}
		return -1;

	}

	public int hasAccessToEdit(UUID id) {
		DocumentType dt = dr.findById(id).orElse(null).getType();

	/*	if (getUser().getMaster() == null) {
			return 1;
		}*/
		List<Groupe> groups = gr.findGroupeByUsersUserId(connectedUser().getUserId());
		for (Groupe groupe : groups) {
			List<PermissionGroup> permissionGroups = pgr.findByGroup(groupe);
			for (PermissionGroup pd : permissionGroups) {

				for (PermissionDocumentType pdt : pd.getPermissionDocument().getPermissionDocumentTypes()) {

					if (dt.getId() == pdt.getDocumentType().getId()
							&& pdt.getPermissionDocument().getAcces().contains("W")

							) {
						return 1;
					}
				}
			}
		}
		return -1;

	}

	public int hasAccessToEditN(UUID id) {
	/*	if (getUser().getMaster() == null) {
			return 1;
		}*/
		Folder f = fr.findById(id).orElse(null);
		Nature n = f.getNature();
		List<Groupe> groups = gr.findGroupeByUsersUserId(connectedUser().getUserId());
		for (Groupe groupe : groups) {
			List<PermissionGroupN> permissionGroups = pgnr.findByGroup(groupe);
			for (PermissionGroupN pd : permissionGroups) {

				for (PermissionNatureCourrier pdt : pd.getPermissionCourrier().getPermissionNature()) {

					if (n.getId() == pdt.getNature().getId() && (pdt.getPermissionCourrier().getAcces().contains("W"))) {
						return 1;
					}
				}
			}
		}
		return -1;

	}

	@Autowired
	ClientDocRepo cdr;

	public ResponseEntity deleteDocClt(Long id) {
		ClientDoc cd = cdr.getOne(id);
		for (Document d : cd.getDocuments()) {
			for (DocumentAttributeValue attr : d.getAttributeValues()) {
				AttributeValue av = attr.getValue();
				davr.delete(attr);
				avr.delete(av);
			}
			dr.delete(d);
		}
		cdr.delete(cd);
		return new ResponseEntity(HttpStatus.OK);
	}

	public ResponseEntity delete(UUID dc,String secondary) {
		Document document = dr.findById(dc).orElse(null);

		try {

			List<DocumentFolder> folders = document.getFolders();
			for (DocumentFolder folder : folders) {
				dfr.delete(folder);
			}
			List<DocumentAttributeValue> documentAttributeValues = document.getAttributeValues();
			for (DocumentAttributeValue attr : documentAttributeValues) {
				AttributeValue av = attr.getValue();
				davr.delete(attr);
				avr.delete(av);
			}

			for (DocumentVersion dv : document.getVersions()) {
				deleteDocVers(dv.getId());
			}
			for (InterneMessage interneMessage : document.getMessage()) {
				for (RecieveMessage msg : interneMessage.getReceivers()) {
					rmr.delete(msg);
				}
				imr.delete(interneMessage);
			}
			Connection conn = DriverManager.getConnection(db, dbus, dbpw);
			
			// document.getType().getName().replace(' ', '_') +
			String query = "delete from d"+document.getType().getId()+" where id=? ";
			
			PreparedStatement pe = conn.prepareStatement(query);
			NativeQueriesUtils.setParamToPreparedStatement(pe, 1, dc.toString());
			pe.executeUpdate();
			
			conn.close();
			DocumentFullText documentFulltext= documentFulltextRepository.findById(dc).orElse(null);
			if(Objects.nonNull(documentFulltext))
				documentFulltextRepository.delete(documentFulltext);
				
			final String FILE_NAME = document.getFileName();

			try {
				File f = new File(document.getPathServer());
				//System.out.println(f.exists());
				boolean b = f.delete();

			} catch (Exception e) {
				//System.err.println(e.getMessage());
				return null;
			}
			
			dr.delete(document);
			
			 if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() >= 1) {
                 Journal j = new Journal();

                 j.setUser(connectedUser());
                 j.setDate(new Date());
                 j.setComposante("Document");
                 j.setMode("S");
                 if (secondary.equals("true")) {

                     User user = ur.getSecondaryUser(connectedUser().getUserId());

                     j.setTypeEv("Utilisateur/Suppression/Secondaire Profil");

                     if (Objects.nonNull(user)) {
                         j.setConnectedSacondaryName(user.getFullName());
                     }

                 } else {
                     j.setTypeEv("Utilisateur/Suppression");
                 }
                 j.setAction(" Type :" + document.getType().getName());
                 j.setAction("Suppression de courrier '" + document.getFileName() + " de type '" + document.getType().getName()
                         + "'");
                 j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

                 jr.save(j);
             }
			return new ResponseEntity(HttpStatus.OK);
 
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public ResponseEntity deleteDocs(List<UUID> docs,String secondary) {
		for (UUID uuid : docs) {
			delete(uuid,secondary);
		}
		return new ResponseEntity<>(HttpStatus.OK);

	}

	public static final String UploadRoot = System.getProperty("user.dir") + "/upload";

	// SUPP DOC VERSION
	public ResponseEntity deleteDocVers(Long id) {
		DocumentVersion dv = dvr.findById(id).orElse(null);
		// List<DocumentAttributeValue>documentAttributeValues=document.getAttributeValues();
		// for(DocumentAttributeValue attr : documentAttributeValues)
		// { AttributeValue av=attr.getValue();
		// davr.delete(attr);
		// avr.delete(av);
		// }
		String ref = "";
		Document d = dv.getDocument();
		for (DocumentAttributeValue av : d.getAttributeValues()) {
			if (av.getAttribute().getName().equals("Réference"))
				;
			ref = av.getValue().getValue();
		}
		if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() >= 1) {
			Journal j = new Journal();
			j.setUser(connectedUser());
			j.setDate(new Date());
			j.setTypeEv("Suppression");
			j.setAction("Suppression d'une version de document '" + ref + "' de type '"
					+ dv.getDocument().getType().getName() + "'");
			j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));
			jr.save(j);
		}
		List<DocumentVersionAttributeValue> lstd = dv.getAttributeValues();
		for (DocumentVersionAttributeValue documentVersionAttributeValue : lstd) {
			dvavr.delete(documentVersionAttributeValue);
		}
		dvr.delete(dv);
		return new ResponseEntity(HttpStatus.OK);
	}

	@Value("${db.name}")
	private String db;
	@Value("${db.user}")
	private String dbus;
	@Value("${db.pw}")
	private String dbpw;

	public DocumnentClass FindById(UUID id) {
		Document d = dr.findById(id).orElse(null);

		DocumnentClass dc = new DocumnentClass();
		dc.type = d.getType().getId();
		dc.fileName = d.getFileName();

		List<DocumentAttributeValue> documentAttributeValues = d.getAttributeValues();
		dc.attrs = new ArrayList<AttributeClass>();
		for (DocumentAttributeValue dav : documentAttributeValues) {

			AttributeClass a = new AttributeClass();
			a.id = dav.getAttribute().getId();

			a.type = dav.getAttribute().getType().getName();
			a.name = dav.getAttribute().getName();
			a.val = dav.getValue().getValue();

			dc.attrs.add(a);
		}
		return dc;

	}

}
