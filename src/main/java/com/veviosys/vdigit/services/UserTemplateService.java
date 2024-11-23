package com.veviosys.vdigit.services;

import java.awt.print.Pageable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.veviosys.vdigit.models.Document;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.models.UserTemplates;
import com.veviosys.vdigit.repositories.UserTemplateRepository;
@Service
public class UserTemplateService {



@Autowired
MasterConfigService masterConfigService;
@Autowired
UserTemplateRepository repository;

public User connectedUser() {
return ((CostumUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
}


String createNewPathTemplateFilePath(String data,String vuuid) {
 
  Date n = new Date();
  String path = masterConfigService.findActivePath() + "\\templates" + "\\"
+ Base64.getEncoder().encodeToString((String.valueOf(n.getYear())).getBytes())
+ "\\"
+ Base64.getEncoder().encodeToString((String.valueOf(n.getMonth())).getBytes())
+ "\\" + Base64.getEncoder().encodeToString((String.valueOf(n.getDay())).getBytes())
;
 
  File f = new File(path);
  if(!f.exists())
  f.mkdirs();
 

     
     try {
    FileWriter myWriter = new FileWriter(path+ "\\" + vuuid + ".documania");
myWriter.write(data);
myWriter.close();
} catch (Exception e) {
// TODO Auto-generated catch block
e.printStackTrace();
}
   


 
 
  return path+ "\\" + vuuid + ".documania";
 
  }


 public UserTemplates createUserTemplate( Map<String, String> userTemplate ) {
 
 UserTemplates templates = new UserTemplates();
 
 templates.setCreationDate(new Date());
 templates.setModificationDate(new Date());
 templates.setName(userTemplate.get("modelName"));
 templates.setDesc(userTemplate.get("modelDesc"));
 templates.setHasFooter(userTemplate.get("hasFooter").equals("1"));
 templates.setHasHeader(userTemplate.get("hasHeader").equals("1"));
 
 
 templates.setUserId(connectedUser().getUserId());
 templates = repository.saveAndFlush(templates);
 templates.setTemplatePath(createNewPathTemplateFilePath(userTemplate.get("data"), templates.getId().toString()));
 templates = repository.saveAndFlush(templates);
 
 return templates;

 }
 
 public UserTemplates editUserTemplate(UUID templateId, Map<String, String> userTemplate ) {
 
 UserTemplates templates = repository.findById(templateId).orElse(null);
 
 templates.setModificationDate(new Date());
 templates.setName(userTemplate.get("name"));
 templates.setDesc(userTemplate.get("desc"));
 
 templates.setHasFooter(userTemplate.get("hasFooter").equals("1"));
 
 templates.setHasHeader(userTemplate.get("hasHeader").equals("1"));
 FileWriter myWriter2 = null;
 try {
myWriter2 = new FileWriter(templates.getTemplatePath());
myWriter2.write(userTemplate.get("content"));
myWriter2.close();
} catch (IOException e) {
// TODO Auto-generated catch block
e.printStackTrace();
try {
myWriter2.close();
} catch (IOException e1) {
// TODO Auto-generated catch block
e1.printStackTrace();
}
}
 


 templates = repository.saveAndFlush(templates);
 
 return templates;
 
 
 }
 
 public UserTemplates getUserTemplate(UUID templateId ) {
 
 UserTemplates ut = repository.findById(templateId).orElse(null);
 try {
ut.setContent(getDocEditor(ut));
} catch (FileNotFoundException e) {
// TODO Auto-generated catch block
e.printStackTrace();
} catch (IOException e) {
// TODO Auto-generated catch block
e.printStackTrace();
}
 return ut;
 
 
 
 }
 
 public void deleeteUserTemplate(UUID templateId ) {

 UserTemplates templates = repository.getOne(templateId);
 File f = new File(templates.getTemplatePath());
 f.delete();
 repository.delete(templates);
 
 }
 
 public Page<UserTemplates> getUserTemplates(String q,org.springframework.data.domain.Pageable page){
 
 
 System.err.println(repository.findByUserIdAndNameContainingIgnoreCaseOrderByCreationDateDesc(connectedUser().getUserId(),q,page).getTotalElements());
 
 return repository.findByUserIdAndNameContainingIgnoreCaseOrderByCreationDateDesc(connectedUser().getUserId(),q,page);
 
 
 }
 
 
public String getDocEditor(UserTemplates templates) throws FileNotFoundException, IOException {
// TODO Auto-generated method stub

StringBuilder resultStringBuilder = new StringBuilder();
File file = new File(templates.getTemplatePath());


       BufferedReader br = new BufferedReader(new FileReader(file));

       String st;

     
       while ((st = br.readLine()) != null) {
           resultStringBuilder.append(st).append("\n");
       }


       return resultStringBuilder.toString();
}
 
 
}
