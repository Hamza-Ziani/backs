package com.veviosys.vdigit.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.itextpdf.kernel.geom.Path;
import com.veviosys.vdigit.classes.ElementDraft;
import com.veviosys.vdigit.classes.TypeDraft;
import com.veviosys.vdigit.models.Element;
import com.veviosys.vdigit.models.TypeElement;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.repositories.ElementRepository;
import com.veviosys.vdigit.repositories.ElementTypeRepo;

@Service
public class ElementService {
	
	@Autowired
	ElementRepository elementRepo;

    

	@Autowired
	ElementTypeRepo typeElementRepo;
	@Autowired
	userService us;
	@Autowired MasterConfigService configService;
	
    public List getTreeData(){
        List l = elementRepo.findElementByMaster(us.connectedUserMaster(us.connectedUser().getUserId()));
        
        return l;
    }

    public Page<Element> getElements(Long idEspVers, Pageable pageable){
        return elementRepo.getElmByEspVers(idEspVers,
                us.connectedUser().getMaster() != null ? us.connectedUser().getMaster().getUserId() : us.connectedUser().getUserId(),
                pageable);
    }

    public List<Element> getTreeDataByLevel(Long level){
        Long firstId = -1l;

        if(level == 1)
            firstId = typeElementRepo.findTypeElementByMasterOrderByIdTypeElementAsc(null,PageRequest.of(0,1)).get(0).getIdTypeElement();

        List<Element> elms = elementRepo.getElementsByTypeElementIdTypeElement(firstId);
        return elms;
    }

    public List<Element> getDescendance(String idParent){
        if(idParent.equals("0")){
            return getTreeDataByLevel(1l);
        }else
        return elementRepo.getElementsByIdParent(UUID.fromString(idParent));
    }
    
    

    public ResponseEntity addElement(ElementDraft elm) throws IOException {

        User u = us.connectedUser();
        Element e = new Element(elm);

        if(u.getMaster() == null)
            e.setMaster(u.getUserId());
        else
            e.setMaster(u.getMaster().getUserId());

        e.setDateCreation(LocalDate.now());

        TypeElement type = typeElementRepo.getOne(elm.getIdTypeElement());

        e.setTypeElement(type);

        e.setIsReserved(0);
        e.setIsVerser(0);
        e.setIsRonger(0);

//        List<Long> l = typeElementRepo.getLastType(2l).stream().map(ee -> ee.getIdTypeElement()).collect(Collectors.toList());

        List<Long> l = typeElementRepo.findTypeElementByMasterOrderByIdTypeElementDesc(2l,PageRequest.of(0, 2)).stream().map(ee -> ee.getIdTypeElement()).collect(Collectors.toList());

        if(l.contains(type.getIdTypeElement()))
        e.setLast(true);

        e = elementRepo.save(e);
        
        return ResponseEntity.ok(e);

    }

    public List<TypeElement> updateType(TypeDraft draft, Long idType) throws IOException {

        if(draft.getFileModel() == null)//No photo
        {

            TypeElement t = typeElementRepo.getOne(idType);
            if(draft.getNomTypeElement() != null && draft.getNomTypeElement() != "")
                t.setIntituleTypeElement(draft.getNomTypeElement());
            typeElementRepo.saveAndFlush(t);

            return typeElementRepo.findAll();

        }else {//With photo

            TypeElement t = typeElementRepo.getOne(idType);
            if(draft.getNomTypeElement() != null && draft.getNomTypeElement() != "")
                t.setIntituleTypeElement(draft.getNomTypeElement());

            String newName = saveFile(t.getIdTypeElement() + "-" +
                    draft.getFileModel().getFileName() +
                    ".documania", draft.getFileModel().getFileBase64());

            t.setPicPath(newName);

            t = typeElementRepo.saveAndFlush(t);

            return  typeElementRepo.findAll();

        }

    }




    public TypeElement addType(TypeDraft draft) throws IOException {


        TypeElement t = new TypeElement(draft);
        t = typeElementRepo.saveAndFlush(t);
        Long idNew = t.getIdTypeElement();

 
        if(draft.getIdParent() == null){
            String newName = saveFile(t.getIdTypeElement() + "-" +
                    draft.getFileModel().getFileName() +
                    ".documania", draft.getFileModel().getFileBase64());

            t.setPicPath(newName);
            
//            t.setMaster(us.GetMaster().getUserId());

            t = typeElementRepo.saveAndFlush(t);
            
            return t;
        }


        TypeElement oldChild = typeElementRepo.findByMasterAndIdTypeElementNotLike(draft.getIdParent(), idNew);
        
        if (oldChild == null){
           
            String newName = saveFile(t.getIdTypeElement() + "-" +
                    draft.getFileModel().getFileName() +
                    ".documania", draft.getFileModel().getFileBase64());

            t.setPicPath(newName);

            t = typeElementRepo.saveAndFlush(t);
            return t;
        }
        
        oldChild.setMaster(idNew);
        
        typeElementRepo.saveAndFlush(oldChild);
        String newName = saveFile(t.getIdTypeElement() + draft.getFileModel().getFileName() + ".documania", draft.getFileModel().getFileBase64());
        t.setPicPath(newName);

        typeElementRepo.saveAndFlush(t);

        return t;
    }
    
    public Map loadFile(String path) throws IOException {
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
        Map<String, String> hash = new HashMap();
        hash.put("data", data);
        return hash;
    }
    
    public String saveFile(String pathName, String base64) throws IOException {

        //String path = System.getProperty("user.dir") + "\\upload";
        String path =configService.findActivePath()+"\\upload\\typesImg";
        

        System.out.println(path);

        
        File f = new File(path);
        
        if(!f.exists())
        f.mkdir();
        //D:\DocumaniaCourrier
    	System.out.println(path);
        File pathSave = new File(path);
        if (!pathSave.exists()) {
            pathSave.mkdir();
        }

        String finalPath = path + "\\" + pathName;
        File ff = new File(finalPath);
        ff.createNewFile();
        FileWriter myWriter = new FileWriter(finalPath);
        myWriter.write(base64);


        myWriter.close();
        return finalPath;
    }
}
