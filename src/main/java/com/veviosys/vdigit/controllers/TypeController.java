package com.veviosys.vdigit.controllers;

 

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.veviosys.vdigit.classes.ArborescenceClass;
import com.veviosys.vdigit.classes.AttributeClass; 
import com.veviosys.vdigit.classes.mapClass;
import com.veviosys.vdigit.classes.mapSearch;
import com.veviosys.vdigit.classes.mapType;
import com.veviosys.vdigit.models.Attribute;
import com.veviosys.vdigit.models.AttributeType;
import com.veviosys.vdigit.models.DocumentType;
import com.veviosys.vdigit.models.FolderType;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.models.arborescence;
import com.veviosys.vdigit.repositories.DocumentTypeRepo;
import com.veviosys.vdigit.repositories.FolderTypeRepo;
import com.veviosys.vdigit.services.AlimentationService;
import com.veviosys.vdigit.services.CostumUserDetails;
import com.veviosys.vdigit.services.ElementTypeGroupService;
import com.veviosys.vdigit.services.TypeService;

@RestController
@RequestMapping("api/v1/")
public class TypeController {
    @Autowired
    FolderTypeRepo FTR;
    @Autowired
    DocumentTypeRepo DTR;
    @Autowired
    AlimentationService alimentationService;
    @Autowired
    TypeService ts;
    @Autowired
    ElementTypeGroupService elemeGrService;

    @RequestMapping("folderType")
    public String addTypeDossier(@RequestBody FolderType ft) {
        CostumUserDetails user = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        /*
         * Long mst; if(user.getUser().getMaster()==null) { mst
         * =user.getUser().getUserId(); } else mst
         * =user.getUser().getMaster().getUserId();
         */
        FTR.addType(user.getUser().getUserId(), ft.getId());

        return "Added successfully !!";
    }

    @RequestMapping("/foldersType")
    public List<FolderType> getFoldersType() {
        CostumUserDetails user = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return FTR.findByUser(user.getUser().getUserId());
    }

    @RequestMapping("/allfolderstype")
    public List<FolderType> getFoldersTypes() {
        CostumUserDetails user = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        /*
         * Long mst; if(user.getUser().getMaster()==null) { mst
         * =user.getUser().getUserId(); } else mst
         * =user.getUser().getMaster().getUserId();
         */
        return FTR.findOthers(user.getUser().getUserId());
    }

    @DeleteMapping("/delete/{id}")

    public String deleteFType(@PathVariable Long id) {
        FTR.delType(getprinc().getUserId(), id);
        return "Done.";
    }

    public User getprinc() {
        CostumUserDetails user = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        return user.getUser();
    }

    @RequestMapping("/doc-types-byGrAccess/{access}/{gr}")
    public List<DocumentType> getAllDocsTypesAclFilterAndGroup(@PathVariable String access,@PathVariable Long gr) {
    Long idM;
    if (getprinc().getMaster() == null) {
    idM = getprinc().getUserId();
    } else
    idM = getprinc().getMaster().getUserId();
     
     
     
    List<DocumentType> dtsACL = new ArrayList<>();
     
    List<DocumentType> dts =  elemeGrService.getOne(gr).getDocumentTypes();
     
     
    for (DocumentType documentType : dts) {
     
    if(alimentationService.hasAccessTo(documentType.getId(), access)== 1)
    dtsACL.add(documentType);
    }
     
     
    return  dtsACL;
    }

    
    @RequestMapping("/docType")
    public Page<DocumentType> getDocType(Pageable page, @RequestParam(name = "q") String q) {
        return ts.getListofType(page,q);
    }
    
    @RequestMapping("/docstypenogroup")
    public List<DocumentType> getDocsTypeNoGroup() {
    Long idM;
    if (getprinc().getMaster() == null) {
    idM = getprinc().getUserId();
    } else
    idM = getprinc().getMaster().getUserId();
    System.err.println("qsd");
    return DTR.findByUserNonGroup(idM, idM);
    }
    
    public User getUser() {
        CostumUserDetails user = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return user.getUser();
    }

    @RequestMapping("/docstype")
    public List<DocumentType> getDocsTypePermission(@RequestParam(required = false,defaultValue = "all") String action) {
        Long idM;
        if (getprinc().getMaster() == null) {
            idM = getprinc().getUserId();
        } else
            idM = getprinc().getMaster().getUserId();
        
        if(action.equals("all")) {
            
             return DTR.findByUser(idM, idM);
             
        }
           // List<DocumentType> FiltredDocType = new ArrayList<DocumentType>();
            
            List<DocumentType> noFiltredDocType = DTR.getDocAccessList(action,idM, getUser().getUserId());
//            
//            for (DocumentType documentType : noFiltredDocType) {
//                
//                int condition =   alimentationService.hasAccessTo(documentType.getId(),action);
//                
//                
//                if(condition == 1) {
//                    FiltredDocType.add(documentType);
//                }
//                
//            }
//            
            return noFiltredDocType;
       
        
    }
    
    



    @RequestMapping("/alldoctype")
    public Page<DocumentType> getall(Pageable page) {
        return ts.getAll(page);
    }

    @RequestMapping("/adddoctype")
    public String addTypeDoc(@RequestBody DocumentType dt) {
        System.out.println(getprinc().getUserId()+"---"+ dt.getId());
        DTR.addType(getprinc().getUserId(), dt.getId());
        return "Added successfully !!";
    }

    @DeleteMapping("/deleteD/{id}")
    public String deleteDocType(@PathVariable Long id) {

        DTR.delType(getprinc().getUserId(), id);
        return "Done.";
    }

    @PutMapping("/add/attribute")
    public ResponseEntity addType(@RequestBody AttributeClass a,@RequestHeader(name = "secondary") String secondary) {

        return ts.addAttr(a,secondary);
    }

    @GetMapping("/attributes")
    public List<Attribute> getAttribute() {
        return ts.getAttr();
    }

    @GetMapping("/typeattr")
    public List<AttributeType> getTypes() {
        return ts.getTypes();
    }

    // add doc Type
    // @PutMapping("/addTypeD")
    // public ResponseEntity addTypeD(@RequestBody DocumentTypeClass dt)
    // {HashMap<String,Integer>lsAttr
    // //.println(dt.attr);
    // return ts.addType(dt.name,dt.attr);
    // }
    @PutMapping("/addTypeD")
    public ResponseEntity addTypeD(@RequestBody List<mapType> lsAttr ,@RequestHeader(name = "secondary") String secondary) throws SQLException {
       mapType typeName=lsAttr.get(lsAttr.size()-1);
        String name=typeName.key;
        String libelle = typeName.libelle;
        lsAttr.remove(typeName);
        return ts.addType(name,libelle,lsAttr,secondary);
    }

    @GetMapping("/getAttrsType/{id}")
    public List<AttributeClass> getAttrs(@PathVariable Long id) {
        return ts.getAttributsOfType(id);
    }

    @GetMapping("/getTypeName/{id}")
    public DocumentType getName(@PathVariable Long id) {
        return DTR.findId(id);
    }

    // ADD FOLDER TYPE
    @PutMapping("/addTypeF")
    public ResponseEntity addTypeF(@RequestBody FolderType ft,@RequestHeader(name = "secondary") String secondary) {
        return ts.addFolderType(ft,secondary);
    }

    // DELETE Doctype TYPE
    @DeleteMapping("/deletedocumenttype/{id}")
    public ResponseEntity deletedocumentType(@PathVariable Long id,@RequestHeader(name = "secondary") String secondary) throws SQLException {
        return ts.deleteDocumentType(id,secondary);
    }

    // GET SELELECTED
    @GetMapping("getFol")
    public Page<FolderType> getFoldersType(Pageable pageable) {
        return ts.getLisFolderType(pageable);
    }

    // DELETE FOLDER TYPE
    @DeleteMapping("/deletefoldertype/{id}")

    public ResponseEntity deleteFolderType(@PathVariable Long id,@RequestHeader(name = "secondary") String secondary ) {
        return ts.deleteFolderType(id,secondary);
    }

    // edit FOLDER TYPE
    @PutMapping("/editfoldertype")

    public ResponseEntity EditFolderType(@RequestBody FolderType ft) {
        return ts.editFolderType(ft);
    }

    // edit DocumentType
    @PutMapping("/editDocumenttype/{id}/{name}/{libelle}")

    public ResponseEntity EditDocumentType(@PathVariable String name,@PathVariable String libelle, @RequestBody List<List<mapType>> lsAttr,
            @PathVariable Long id,@RequestHeader(name = "secondary") String secondary)  
{
    try {
        
return ts.editDocumentType(name,libelle, lsAttr, id,secondary );
    } catch (Exception e) {
      System.err.println(e.getMessage());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }	
}
@PutMapping("editlevel/{id}")
public ResponseEntity EditFirstLevel(@PathVariable(name="id") Long id,@RequestBody int etat)
{
	return ts.isFirstLevel(id,etat);
}

@GetMapping("/arborescence/getall")
public Page<arborescence>getAll(Pageable pageable)
{
	return ts.getArborescence(pageable);
}
//CREATE ARBO
@PostMapping("/arborescence/add/")
public ResponseEntity addArbo( @RequestBody String name)
{
	return ts.addArbo(name);
}
//CONFIG ARBO
@PostMapping("/arborescence/structure/add")
public ResponseEntity configArbo(@RequestBody List<ArborescenceClass> arbo)
{
	return ts.AddArboConfig(arbo);
	}


//LINK FOLDER WITH ARBO
@PostMapping("/arborescence/linkarboft/{id1}")
public ResponseEntity linkFolderArbo(@PathVariable(name="id1") Long idArbo,@RequestBody Long idFtType)
{
	return ts.linkArboFolderType(idArbo, idFtType);
}
// ADD VERSION
@PostMapping("/documenttype/setversion/{id}/{etat}")
public ResponseEntity editVesrion(@PathVariable(name = "id")Long id,@PathVariable(name="etat")int etat)
{
	return ts.editVersion(id, etat);
}
//  CONFIG AUTO REF

@PostMapping("type/autoref/{id}")
public ResponseEntity configAutoRef(@PathVariable Long id,@RequestBody List<mapSearch> ms)
{
    return ts.configNumerotation(ms.get(0), id, ms.get(1).value);
}
@GetMapping("autoref/state/{id}/{etat}")
public ResponseEntity editStateSeq(@PathVariable Long id,@PathVariable int etat )
{
    return ts.editSeqState(id, etat);
}
@GetMapping("autoref/init/{id}")
public ResponseEntity initSeq(@PathVariable Long id)
{
    return ts.initSeq(id);
}

@GetMapping("doctypes/reset")
public ResponseEntity resetDocTypes() throws SQLException
{
    ts.resetDocs();
    // try {
       
    // } catch (Exception e) {
    //    System.out.println(e.getMessage());
    // }
     
     return null;
}



 
}