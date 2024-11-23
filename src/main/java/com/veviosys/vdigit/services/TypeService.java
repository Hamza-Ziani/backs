package com.veviosys.vdigit.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import com.mysql.cj.jdbc.DatabaseMetaData;
import com.veviosys.vdigit.classes.ArborescenceClass;
import com.veviosys.vdigit.classes.AttributeClass;
import com.veviosys.vdigit.classes.KeyValueFk;
import com.veviosys.vdigit.classes.mapClass;
import com.veviosys.vdigit.classes.mapSearch;
import com.veviosys.vdigit.classes.mapType;
import com.veviosys.vdigit.models.Attribute;
import com.veviosys.vdigit.models.AttributeType;
import com.veviosys.vdigit.models.Document;
import com.veviosys.vdigit.models.DocumentType;
import com.veviosys.vdigit.models.FolderType;
import com.veviosys.vdigit.models.Journal;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.models.arborescence;
import com.veviosys.vdigit.reposietories.AttributeRepo;
import com.veviosys.vdigit.reposietories.AttributeTypeRepo;
import com.veviosys.vdigit.repositories.DocumentRepo;
import com.veviosys.vdigit.repositories.DocumentTypeRepo;
import com.veviosys.vdigit.repositories.FolderRepo;
import com.veviosys.vdigit.repositories.FolderTypeRepo;
import com.veviosys.vdigit.repositories.JournalRepo;
import com.veviosys.vdigit.repositories.UserRepository;
import com.veviosys.vdigit.repositories.arborescenceRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TypeService {

	@Autowired
	AttributeRepo ar;
	@Autowired
	UserRepository ur;
	@Autowired
	AttributeTypeRepo atr;
	@Autowired
	DocumentTypeRepo dtr;
	@Autowired
	FolderTypeRepo ftr;
	@Autowired
	DocumentRepo dr;
	@Autowired
	arborescenceRepository arbr;
	@Autowired
    DbAttrsConfigService dbAttrsConfigService;
	@Autowired
	FolderRepo fr;
	@Autowired
	AlimentationService alimentationService;
	@Autowired
	JournalRepo jr;

	@Value("${db.name}")
	private String db;
	@Value("${db.user}")
	private String dbus;
	@Value("${db.pw}")
	private String dbpw;
	@Value("${database.datatypes.longtext}")
	private String longTextType;
	@Value("${documania.datasource.select}")
	private String dbSelected;

	public User getUser() {
		CostumUserDetails user = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		return user.getUser();
	}

	public ResponseEntity addAttr(AttributeClass att,String secondary) {
		try {
			User u = getUser();
			Attribute at = ar.findByNameAndMaster(att.name, u);
			if (Objects.isNull(at)) {
			    at = new Attribute();
                at.setVisib(1);
                at.setType(atr.findByName(att.type));
                at.setName(att.name);
                at.setLibelle(att.libelle);
                at.setLabelar(! Objects.isNull(att.labelar) ? att.labelar:"*");
                at.setLabelfr(! Objects.isNull(att.labelfr) ? att.labelfr:"*");
                at.setLabeleng(!Objects.isNull(att.labeleng)? att.labeleng:"*");
                at.setMaster(u);
                if (att.getType().equals("List")) {
                    at.setDefaultValue(! Objects.isNull(att.defaultValue) ?att.defaultValue:"*");
                } else if (att.getType().equals("ListDep")){
                      if(att.getSource().equals("manuale")) {
                          at.setDefaultValue(! Objects.isNull(att.defaultValue) ?att.defaultValue:"*");
                          at.setListDep(att.getListDep());
                      }
                }
                ar.save(at);
                if(att.getType().equals("ListDep")) {
                    at.setListDep(att.getListDep());
                    if(att.getSource().equals("db")) {
                        at.setConfigId(att.getConfigId());
                        at.setTableConfig(att.getTableConfig()); 
                        
                        dbAttrsConfigService.loadDataSourceByAttr(at);                          
                    }
                }
                else if(att.getType().equals("listDb")) {
                    at.setConfigId(att.getConfigId());
                    at.setTableConfig(att.getTableConfig()); 
                    
                    List<KeyValueFk>  data =  dbAttrsConfigService.loadDataSourceByAttr(at);
                }

                
                if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
                    Journal j = new Journal();
                    String a = "";
                    a = " Libelle : " + att.libelle + " |   Type : " + atr.findByName(att.type).getName()
                            + " | Name : " + att.name ;
                    j.setUser(connectedUser());
                    j.setDate(new Date());
                    j.setMode("A");
                    if (secondary.equals("true")) {

                        User user = ur.getSecondaryUser(connectedUser().getUserId());

                        j.setTypeEv("Système/Ajout/Secondaire Profil");
                        if (Objects.nonNull(user)) {
                            j.setConnectedSacondaryName(user.getFullName());
                        }

                    } else {
                        j.setTypeEv("Système/Ajout");
                    }
                    j.setComposante("Ajouter Attribute");

                    j.setAction(a);
                    j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

                    jr.save(j);
                }
                
                
				return new ResponseEntity<>(HttpStatus.OK);
			}
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		} catch (Exception e) {

		   e.printStackTrace();
		   return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public List<AttributeType> getTypes() {
		return atr.findAll();
	}

	public Page<DocumentType> getDocsType(org.springframework.data.domain.Pageable pageable) {
		User u = getUser();
		Long idM;
		if (u.getMaster() == null) {
			idM = u.getUserId();
		} else {
			idM = u.getMaster().getUserId();
		}
		List<DocumentType> dt = dtr.findByUser(getUser().getUserId(), idM);
		int start = (int) pageable.getOffset();
		int end = (int) (start + pageable.getPageSize()) > dt.size() ? dt.size() : (start + pageable.getPageSize());
		Page<DocumentType> pages = new PageImpl<DocumentType>(dt.subList(start, end), pageable, dt.size());

		return pages;

	}

	public Page<DocumentType> getAll(org.springframework.data.domain.Pageable pageable) {

		User u = getUser();
		Long idM;
		if (u.getMaster() == null) {
			idM = u.getUserId();
		} else {
			idM = u.getMaster().getUserId();
		}
		List<DocumentType> dt = dtr.findOthers(getUser().getUserId(), idM);

		int start = (int) pageable.getOffset();
		int end = (int) (start + pageable.getPageSize()) > dt.size() ? dt.size() : (start + pageable.getPageSize());
		Page<DocumentType> pages = new PageImpl<DocumentType>(dt.subList(start, end), pageable, dt.size());

		return pages;
	}

	public Page<DocumentType> getListofType(org.springframework.data.domain.Pageable pageable,String query) {
		User u = getUser();
		Long idM;
		if (u.getMaster() == null) {
			idM = u.getUserId();
		} else {
			idM = u.getMaster().getUserId();
		}
		List<DocumentType> dt = dtr.findByNameContainingIgnoreCase(getUser().getUserId(), idM,query);
		for (int i = 0; i < dt.size(); i++) {

			dt.get(i).setSelected(1);
		}
		int pos = dt.size();

		dt.addAll(dtr.findByNameContainingIgnoreCaseOthers(getUser().getUserId(), idM,query));// =
																// dtr.findByUser(getUser().getUserId());
		for (int i = pos; i < dt.size(); i++) {
			dt.get(i).setSelected(0);
		}

		int start = (int) pageable.getOffset();
		int end = (int) (start + pageable.getPageSize()) > dt.size() ? dt.size() : (start + pageable.getPageSize());
		Page<DocumentType> pages = new PageImpl<DocumentType>(dt.subList(start, end), pageable, dt.size());

		return pages;

	}

	public List<Attribute> getAttr() {

		return ar.findByMasterOrderByIdAsc(getUser());

	}

	/* EDIT DOCTYPE */
	@Transactional(rollbackOn = Exception.class)
	public ResponseEntity editDocumentType(String name,String libelle, List<List<mapType>> lsAttr, Long id,String secondary) throws SQLException {
		String query;
		DocumentType documentType = dtr.findId(id);
		if (!documentType.getName().equals("Accusé de réception")
				|| (documentType.getName().equals("Accusé de réception") && name.equals("Accusé de réception"))) {
			// String name, List<mapClass> lsAttr

			List<mapType> lstOld = lsAttr.get(0);
			List<mapType> lstNews = lsAttr.get(1);

			String dt = documentType.getName();
			String ch = " Type : " + dt;
			String nw = " Type : " + name;
			int b = 0;

			Connection conn = DriverManager.getConnection(db, dbus, dbpw);
			for (Attribute atr : documentType.getAttributes()) {
				if (!atr.getName().equals("Fichier"))
					// { if (b == 0) {
					// ch += " (" + atr.getName() + ")";
					// b = 554;

					// } else {
					ch += " | " + atr.getName() + " : " + atr.getType().getName();
				nw += " | " + atr.getName() + " : " + atr.getType().getName();

				// }}
			}
			// nw=ch;
			// if (!documentType.getName().equals(name)) {

			// query = "RENAME " + (dbSelected.equals("orcl") ? "" : "TABLE ") +
			// documentType.getName().replace(' ', '_').replace("'", "_")
			// + documentType.getId() + " TO " + name.replace(' ', '_').replace("'", "_")
			// + documentType.getId();

			documentType.setName(name);
			documentType.setLibelle(libelle);
			// conn.createStatement().execute(query);
			// query = "";
			// }
			query = "";
			int i = 0;
			for (mapType attribute : lstOld) {
				dtr.editTypeAttr(documentType.getId(), ar.findByNameAndMaster(attribute.key, getUser()).getId(),
						attribute.value, attribute.rep ,attribute.visible);

			}
			b = 0;
			// name.replace(' ', '_').replace("'", "_") +

			if(dbSelected.equals("msql")) {
			    query = " alter table d" + documentType.getId();
			}else {
			    query = " alter table d" + documentType.getId()+" add "+(dbSelected.equals("mssql")? " ( " :"");  
			}			
			for (mapType attribute : lstNews) {
				i += 1;
				dtr.typeAttr(documentType.getId(), ar.findByNameAndMaster(attribute.key, getUser()).getId(),
						attribute.value,attribute.rep,attribute.visible);
				if(dbSelected.equals("msql")) {
				    query +=" add "+(dbSelected.equals("mssql")? " ( " :"");
				}
				query += (dbSelected.equals("msql") ?"Column "  :"" )
						+ attribute.key.replace(' ', '_').replace("'", "_") + " " + longTextType ;

				if (!attribute.key.equals("Fichier"))
				 
					nw += " | " + attribute.key + " : "
							+ ar.findByNameAndMaster(attribute.key, getUser()).getType().getName();
			 
				if (i != lstNews.size()) {
					query += " , ";
				}
			}
			DocumentType d = dtr.saveAndFlush(documentType);
			b = 0;
		 
			System.err.println(query);
			if (lstNews.size() > 0) {
				
				query+=" "+(dbSelected.equals("mssql")?" );":" ");
				System.out.println(query);
				conn.createStatement().execute(query);
				conn.close();
			} else {
				System.out.println(lstNews.size() + " // " + lstOld.size() + "--------out test");

			}
			if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
				Journal j = new Journal();

				j.setUser(connectedUser());
				j.setDate(new Date());
				
				j.setMode("MD");
				// j.setAction("Modification de type de document (" +
				// dt + ") Attributs : " + ch + ". Nouvelle valeur nom ("
				// + documentType.getName() + ") Attributs :" + nw);
				j.setAction(ch + " --> " + nw);
				if (secondary.equals("true")) {

					User user = ur.getSecondaryUser(connectedUser().getUserId());

					j.setTypeEv("Système/Modification/Secondaire Profil");

					if (Objects.nonNull(user)) {
						j.setConnectedSacondaryName(user.getFullName());
					}

				} else {
					j.setTypeEv("Système/Modification");
				}
				j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));
				j.setComposante("Type document");
				jr.save(j);
			}
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}

	}

	private void connectdb() throws SQLException {
		Connection conn = DriverManager.getConnection(db, dbus, dbpw);
	}

	// ADD DOCUMENT TYPE
	public ResponseEntity addType(String name, String libelle ,List<mapType> lsAttr,String secondary) throws SQLException {

		DocumentType type = dtr.findByNameAndMaster(name, connectedUser());
		if (Objects.isNull(type)) {
			User us = getUser();

			List<Attribute> lstA = new ArrayList<Attribute>();
			DocumentType dt = new DocumentType();
			dt.setAttributes(lstA);
			dt.setName(name);
			dt.setLibelle(libelle);
			dt.setMaster(getUser());
			dt = dtr.saveAndFlush(dt);
			Long a = Long.valueOf(6);
			dtr.typeAttr(dtr.findByNameAndMaster(name, us).getId(), a, 1,0, 1);
			// dt.getName().replace(' ', '_').replace("'", "_") +
			String str = "create table d" + dt.getId()
					+ "(id varchar(100) primary key not null ,upload_date VARCHAR(100),last_edit_date date,"
					+ "owner int,master int, content_type  VARCHAR(100), file_name  VARCHAR(100) ";
			int b = 0;
			String ch = "";
			for (int i = 0; i < lsAttr.size(); i++) {
				Attribute atr = ar.findByNameAndMaster(lsAttr.get(i).key, getUser());

				
				String tempName = "";
				if (atr.getName().equals("Date"))
					tempName = "Date_";
				
				else 
					tempName = atr.getName();
				str += ", " + tempName.replace(' ', '_').replace("'", "_") + " " + longTextType;
				if (!atr.getName().equals("Fichier") ) {
					// System.out.println(atr.getName());
					ch += " | " + atr.getName() + " : " + atr.getType().getName();

				}
				
			}
			str += ",Fichier VARCHAR(100), type_name VARCHAR(1200) )";

			// System.out.println(str);
			for (mapType attribute : lsAttr) {
				dtr.typeAttr(dtr.findByNameAndMaster(name, us).getId(),
						ar.findByNameAndMaster(attribute.key, us).getId(), attribute.value, attribute.rep ,attribute.visible);

			}
			// dtr.typeAttr(dtr.findByNameAndMaster(name, getUser()).getId(),
			// ar.findByNameAndMaster("HTML_CONTENT", getUser()).getId(), 0);
			System.err.println("------------s" + str);
			Connection conn = DriverManager.getConnection(db, dbus, dbpw);
			conn.createStatement().executeUpdate(str);
			conn.close();
			if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
				Journal j = new Journal();

				j.setUser(connectedUser());
				j.setDate(new Date());
				
				j.setMode("A");
				j.setComposante("Type document");
				if (secondary.equals("true")) {

					User user = ur.getSecondaryUser(connectedUser().getUserId());

					j.setTypeEv("Système/Ajout/Secondaire Profil");

					if (Objects.nonNull(user)) {
						j.setConnectedSacondaryName(user.getFullName());
					}

				} else {
					j.setTypeEv("Système/Ajout");
				}
				j.setAction(" Type : " + dt.getName() + ch);
				j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

				jr.save(j);
			}
			// //System.out.println(ch);
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}

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

	public List<AttributeClass> getAttributsOfType(Long id) {
		DocumentType dt = dtr.findId(id);// .orElse(null);

		List<AttributeClass> ac = new ArrayList<AttributeClass>();
		for (Attribute at : dt.getAttributes()) {
			ac.add((new AttributeClass(at.getId(), at.getType().getName(), at.getName(), at.getLibelle(), "",at.getLabelfr(),at.getLabelar(),at.getLabeleng(),at.getDefaultValue() )));
			/*	private String labelfr;
	private String labelar;
	private String labeleng;
			 * */
		}
		return ac;
	}

	// ADD FOLDERS TYPE

	public ResponseEntity addFolderType(FolderType ft,String secondary) {
		User master = getUser();
		FolderType f = ftr.findByname(ft.getName(), master.getUserId());

		if (f == null) {
			ft.setSeq(new Long(1));
			ft.setConfigText("text");
			ft.setSeparatorRef("-");
			ft.setOrderRef("dt-ref-txt");
			ft.setSeqIsActive(0);
			ft.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));
			ftr.save(ft);
			if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
				Journal j = new Journal();

				j.setUser(connectedUser());
				j.setDate(new Date());
				j.setTypeEv("Ajout");
				j.setComposante("Type courrier");
				j.setMode("A");
				if (secondary.equals("true")) {

					User user = ur.getSecondaryUser(connectedUser().getUserId());

					j.setTypeEv("Système/Ajout/Secondaire Profil");

					if (Objects.nonNull(user)) {
						j.setConnectedSacondaryName(user.getFullName());
					}

				} else {
					j.setTypeEv("Système/Ajout");
				}
				j.setAction("	Type : " + ft.getName());
				j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

				jr.save(j);
			}
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);

	}

	/*
	 * DELETE DOCUMENT TYPE
	 */

	public ResponseEntity deleteDocumentType(Long id,String secondary) throws SQLException {
		DocumentType dt = dtr.findById(id).orElse(null);
		if (!dt.getName().equals("Accusé de réception")) {
			List<User> users = ur.findUsersByMaster(getUser());
			List<Document> documents = dr.findByType(id, getUser().getUserId());
			int b = 0;
			String ch = "";

			for (Attribute atr : dt.getAttributes()) {
				if (!atr.getName().equals("Fichier")) {

					// if (b == 0) {
					// ch += " (" + atr.getName() + ")";
					// b = 554;

					// } else {
					// ch += ", (" + atr.getName() + ")";
					// }
					ch += " | " + atr.getName() + " : " + atr.getName();

				}

			}

			if (documents.size() == 0) {
				dtr.delType(getUser().getUserId(), id);
				for (User user : users) {
					dtr.delType(user.getUserId(), id);
				}
				//String name = dtr.findById(id).orElse(null).getName().replace(' ', '_').replace("'", "_");
				dtr.deleteDocumentType(id);

				dtr.delete(dtr.findId(id));
				// + name
				
				
				Connection conn = DriverManager.getConnection(db, dbus, dbpw);
				
                String str = "drop table d" + id;
               
				conn.createStatement().executeUpdate(str);
				conn.close();
				if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
					Journal j = new Journal();

					j.setUser(connectedUser());
					j.setDate(new Date());
					// j.setTypeEv("Suppression");
					
					j.setMode("S");
					if (secondary.equals("true")) {

						User user = ur.getSecondaryUser(connectedUser().getUserId());

						j.setTypeEv("Système/Suppression/Secondaire Profil");

						if (Objects.nonNull(user)) {
							j.setConnectedSacondaryName(user.getFullName());
						}

					} else {
						j.setTypeEv("Système/Suppression");
					}
					j.setComposante("Type document");
					j.setAction("Type : " + dt.getName() + "  " + ch);
					j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

					jr.save(j);
				}

				return new ResponseEntity<>(HttpStatus.OK);
			} else {

				return new ResponseEntity<>(HttpStatus.CONFLICT);
			}

		} else {
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);

		}
	}

	/*
	 * EDIT FOLDER TYPE
	 */

	public ResponseEntity editFolderType(FolderType ft) {
		try {
			FolderType f = ftr.getOne(ft.getId());
			f.setName(ft.getName());
			f.setCat(ft.getCat());
			// if (!f.getName().equals("Courrier arrivé") && !f.getName().equals("Courrier départ")) {
				// ft.setMaster(getUser());
				// ft.setSelected(0);
			 ftr.save(f );
				// ft.setConfigText(configText);
				return new ResponseEntity<>(HttpStatus.OK);
			// }
			// return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);

		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}

	}

	/*
	 * delete folder type
	 */

	public ResponseEntity deleteFolderType(Long id,String secondary) {
		FolderType f = ftr.getOne(id);
		if (!f.getName().equals("Courrier arrivé") && !f.getName().equals("Courrier départ")) {
			List<User> users = ur.findUsersByMaster(getUser());

			int folders = ftr.findByType(id, getUser().getUserId());

			if (folders == 0) {
				// System.out.println(id);
				ftr.delType(getUser().getUserId(), id);
				for (User user : users) {
					ftr.delType(user.getUserId(), id);
				}
				FolderType ft = ftr.getOne(id);
				if (ur.getOne(connectedUserMaster(connectedUser().getUserId())).getSecLevel() == 3) {
					Journal j = new Journal();

					j.setUser(connectedUser());
					j.setDate(new Date());
					// j.setTypeEv("Suppression");
					
					j.setMode("S");
					if (secondary.equals("true")) {

						User user = ur.getSecondaryUser(connectedUser().getUserId());

						j.setTypeEv("Système/Suppression/Secondaire Profil");
						if (Objects.nonNull(user)) {
							j.setConnectedSacondaryName(user.getFullName());
						}

					} else {
						j.setTypeEv("Système/Suppression");
					}
					j.setComposante("Type courrier");
					j.setAction("Type : " + ft.getName());
					j.setMaster(ur.getOne(connectedUserMaster(connectedUser().getUserId())));

					jr.save(j);
				}
				ftr.delete(ft);
				return new ResponseEntity<>(HttpStatus.OK);
			} else {

				return new ResponseEntity<>(HttpStatus.CONFLICT);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);

		}
	}

	/*
	 * SELECTED FOLDERS TYPE/ALL FOLDERS TYPE PAGINATION
	 */

	public Page<FolderType> getLisFolderType(org.springframework.data.domain.Pageable pageable) {
		User u = getUser();
		Long idM;
		if (u.getMaster() == null) {
			idM = u.getUserId();
		} else {
			idM = u.getMaster().getUserId();
		}
		List<FolderType> ft = ftr.findFoldersType(idM, u.getUserId());
		for (int i = 0; i < ft.size(); i++) {

			ft.get(i).setSelected(1);
		}
		int pos = ft.size();

		ft.addAll(ftr.findOthers(getUser().getUserId(), idM));// = dtr.findByUser(getUser().getUserId());
		for (int i = pos; i < ft.size(); i++) {
			ft.get(i).setSelected(0);
		}
		int start = (int) pageable.getOffset();
		int end = (int) (start + pageable.getPageSize()) > ft.size() ? ft.size() : (start + pageable.getPageSize());
		Page<FolderType> pages = new PageImpl<FolderType>(ft.subList(start, end), pageable, ft.size());

		return pages;

	}

	// IMPL FIRST LEVEL
	public ResponseEntity isFirstLevel(Long id, int etat) {

		FolderType ft = ftr.findById(id).orElse(null);
		if (etat == 0) {

			ft.setPremierNiveau(0);
		} else {
			ft.setPremierNiveau(1);
		}
		ftr.save(ft);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	// ***********************************ARBORESCENCE
	// PART********************************************///

	// GET ARBO OF USER

	public Page<arborescence> getArborescence(org.springframework.data.domain.Pageable pageable) {

		List<arborescence> ft = arbr.findByMaster(getUser());
		int start = (int) pageable.getOffset();
		int end = (int) (start + pageable.getPageSize()) > ft.size() ? ft.size() : (start + pageable.getPageSize());
		Page<arborescence> pages = new PageImpl<arborescence>(ft.subList(start, end), pageable, ft.size());

		return pages;
	}

	// ADD ARBO
	public ResponseEntity addArbo(String name) {
		try {
			// System.out.println("name");
			arborescence arb = new arborescence();
			arb.setMaster(getUser());
			arb.setName(name);
			arbr.save(arb);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// ADD ARBO MODELS
	public ResponseEntity AddArboConfig(List<ArborescenceClass> list) {
		// List<Folder> lstAdd = new ArrayList<Folder>();
		// UUID parent = new UUID(0, 0);
		// List<UUID> lstIdChilds = new ArrayList<UUID>();
		// Folder fd = null;
		// for (ArborescenceClass folder : list) {
		// // //System.out.println(arbr.findByName(folder.arboName).getName());
		// Folder f = new Folder();
		// f.setMaster(getUser());
		// f.setOwner(getUser());
		// f.setReference(folder.name);
		// f.setArbo(arbr.findByName(folder.arboName));

		// fd = fr.saveAndFlush(f);
		// lstAdd.add(fd);

		// }
		// for (ArborescenceClass folder : list) {
		// lstIdChilds = new ArrayList<UUID>();
		// if (folder.parent != null) {
		// for (Folder f : lstAdd) {
		// if (f.getReference().equals(folder.parent)) {
		// parent = f.getId();
		// }
		// }
		// for (Folder f : lstAdd) {
		// if (f.getReference().equals(folder.name)) {
		// lstIdChilds.add(f.getId());

		// }
		// }
		// alimentationService.linkFolderToFolder(parent, lstIdChilds);
		// }

		// }

		return new ResponseEntity<>(HttpStatus.OK);
	}

	public ResponseEntity linkArboFolderType(Long idArbo, Long idFtType) {
		FolderType f = ftr.findById(idFtType).orElse(null);
		f.setArbo(arbr.findById(idArbo).orElse(null));
		ftr.save(f);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	public ResponseEntity editVersion(Long idDocType, int etat) {
		DocumentType dt = dtr.findId(idDocType);
		dt.setIsVersionable(etat);
		dtr.save(dt);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	public ResponseEntity configNumerotation(mapSearch m, Long id, String order) {

		FolderType ft = ftr.findById(id).orElse(null);
		if (Objects.nonNull(ft)) {

			ft.setOrderRef(order);
			ft.setSeparatorRef(m.key);
			ft.setOrderRef(order);
			ft.setConfigText(m.value);
			ft.setSeqIsActive(1);
			;
			ftr.save(ft);
			// private Long seq;
			// private int seqIsActive;
			// private String orderRef;
			// private String separatorRef;
			// private String configText;
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	// ACTIVE / DESACTIVE LA SEQUENCE
	public ResponseEntity editSeqState(Long id, int state) {

		FolderType ft = ftr.findById(id).orElse(null);
		if (Objects.nonNull(ft)) {
			ft.setSeqIsActive(state);
			ftr.save(ft);
			return new ResponseEntity<>(HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);

	}

	// INIT SEQ a 0
	public ResponseEntity initSeq(Long id) {

		FolderType ft = ftr.findById(id).orElse(null);
		if (Objects.nonNull(ft)) {
			ft.setSeq(new Long(0));
			ftr.save(ft);
			return new ResponseEntity<>(HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);

	}

	public void resetDocTypes() throws SQLException {
		List<String> names = new ArrayList<String>();
		List<String> ref = new ArrayList<String>();

		List<DocumentType> docs = dtr.findAll();
		for (DocumentType documentType : docs) {
			for (Attribute attr : documentType.getAttributes()) {
				ref.add(attr.getName());
			}
			String query = "select * from d" + documentType.getId() + " where 0=1";
			Connection conn = DriverManager.getConnection(db, dbus, dbpw);
			ResultSet resultSet = conn.createStatement().executeQuery(query);
			ResultSetMetaData metaData = resultSet.getMetaData();
		 
			for (int i = 1; i < metaData.getColumnCount(); i++) {
				if (metaData.getColumnTypeName(i).equals("CLOB")) {

					names.add(metaData.getColumnName(i));
				}

			}

			List<String> data = new ArrayList<String>();
			for (String name : ref) {
				if (names.indexOf(name.replace(' ', '_').replace("'", "_") ) != -1) {
					data.add(name);
 }
			}
 for (String name : ref) {
				names.add(name);
			}
			
 if (data.size() > 0) {
				System.out.println(data.size());
 String qr = " alter table d" + documentType.getId();
				for (String name : names) {
					qr += " add " + name + " CLOB ";
				}
				System.out.println(qr);
    //  conn.createStatement().executeUpdate(qr);
			}
			 conn.close();
			data = new ArrayList<String>();
			names = new ArrayList<String>();
			ref = new ArrayList<String>();
		}
	}
	public void resetDocs() throws SQLException{
		
		List<DocumentType> docs = dtr.findAll();
		for (DocumentType documentType : docs) {
			Connection con  = DriverManager.getConnection(db, dbus, dbpw); 
			try {
			con.createStatement().executeUpdate("drop table d"+documentType.getId());
				
			} catch (Exception e) {
				//TODO: handle exception
			}
		String str = "create table d" + documentType.getId()
			+ "(id varchar(100) primary key not null ,upload_date VARCHAR(100),last_edit_date date,"
			+ "owner int,master int, content_type  VARCHAR(100), file_name  VARCHAR(100) ";
			for (Attribute a : documentType.getAttributes()) {
				String tempName = "";
				if (a.getName().equals("Date"))
				tempName = "Date_";
			else
				tempName = a.getName();
				if (!a.getName().equals("Fichier"))
			str += ", " + tempName.replace(' ', '_').replace("'", "_") + " " + longTextType;
			}
			str += ",Fichier VARCHAR(100), type_name VARCHAR(100))";
			con.createStatement().executeUpdate(str);
			con.close();
		}
	}
 
} 