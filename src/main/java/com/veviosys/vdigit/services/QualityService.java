package com.veviosys.vdigit.services;
import java.util.List;
import java.util.Optional;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.veviosys.vdigit.classes.CloneEtapeClass;
import com.veviosys.vdigit.classes.QualityClass;
import com.veviosys.vdigit.models.CloneEtape;
import com.veviosys.vdigit.models.Etape;
import com.veviosys.vdigit.models.Quality;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.repositories.CloneEtapeRepo;
import com.veviosys.vdigit.repositories.EtapeRepo;
import com.veviosys.vdigit.repositories.QualityRepo;
import com.veviosys.vdigit.repositories.UserRepository;



@Service
public class QualityService {
 
    @Autowired 
    QualityRepo qualityRepo;
    
    @Autowired 
    UserRepository userRepo;
    @Autowired 
    CloneEtapeRepo cloneEtapeRep;
    @Autowired 
    EtapeRepo etapeRepo;
    
    @Autowired
    private CloneEtapeRepo ctr;
    
    public User connectedUser() {
        return ((CostumUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }
    
    public List<Quality> getAllQualityNoPage() {
        return qualityRepo.getQualityByMaster(Objects.nonNull(connectedUser().getMaster()) ? connectedUser().getMaster() : connectedUser());
    }
    
    public Page<Quality> getAllQualityPage(Pageable pageable,String q) {
        
        List<Quality> list = qualityRepo.findQualityByNameContainingIgnoreCaseAndMaster(q,connectedUser());
        
        int start = (int) pageable.getOffset();
        int end = (int) (start + pageable.getPageSize()) > list.size() ? list.size() : (start + pageable.getPageSize());
        Page<Quality> pages = new PageImpl<Quality>(list.subList(start, end), pageable, list.size());

        return pages;
    }
    
    @SuppressWarnings("rawtypes")
    public ResponseEntity EditQuality(QualityClass qualityClass,Long id) {
        
        try {
            
        Quality quality = qualityRepo.findById(id).get();
        List<User> users = userRepo.findByTitle(quality.getCode());
        List<CloneEtape> cloneEtapes = cloneEtapeRep.findByQuality(quality.getCode());
        List<Etape> etapes = etapeRepo.findByQuality(quality.getCode());
        
        if(Objects.nonNull(quality)) {

            quality.setCode(qualityClass.getCode());
            quality.setName(qualityClass.getName());   
            quality.setSubordonnee(qualityClass.getSubordonnee()); 
            quality.setAccessBo(qualityClass.getAccessBo());
            quality.setNotifier(qualityClass.getNotifier());
            qualityRepo.save(quality);
            
            for (User user : users) {
                user.setTitle(qualityClass.getCode());
            }
            userRepo.saveAll(users);
            for (Etape etape : etapes) {
                etape.setQuality(qualityClass.getCode());
            }
            etapeRepo.saveAll(etapes);
            for (CloneEtape cloneEtape : cloneEtapes) {
                cloneEtape.setQuality(qualityClass.getCode());
            } 
            cloneEtapeRep.saveAll(cloneEtapes);
            
          return new ResponseEntity(HttpStatus.OK);
          
        }else {
            
            return new ResponseEntity(HttpStatus.CONFLICT);
            
        }
        
        }catch(Exception e ) {
            
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            
        }
        
    }
    
    @SuppressWarnings("rawtypes")
    public ResponseEntity addQuality(QualityClass qualityClass) {
            
     
        try{
            System.out.println(qualityClass);
               Quality newQuality = new Quality();
               
               newQuality.setCode(qualityClass.getCode());  
               newQuality.setName(qualityClass.getName());
               newQuality.setSubordonnee(qualityClass.getSubordonnee());
               newQuality.setAccessBo(qualityClass.getAccessBo());
               newQuality.setNotifier(qualityClass.getNotifier());
               newQuality.setRef_bo(0);
               newQuality.setMaster(connectedUser());
               qualityRepo.save(newQuality);
               
              return new ResponseEntity(HttpStatus.OK);
        
        }catch(Exception e ) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }
    
    @SuppressWarnings("rawtypes")
    public ResponseEntity deleteQuality(Long id) {
        
        try {
            
            qualityRepo.delete(qualityRepo.findById(id).get());;
            
            return new ResponseEntity(HttpStatus.OK);
            
        }catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
       
        
    }
    


 

    
    //++++++++++++++++++++++++++++++++ Add Quality +++++++++++++++++++++++++++++++++++++++
    @SuppressWarnings("rawtypes")
    public ResponseEntity<Void> refBo(@RequestBody List<Long> ids) {
        try {
            for (Long id : ids) {
                Optional<Quality> optionalQuality = qualityRepo.findById(id);
                if (optionalQuality.isPresent()) {
                    Quality quality = optionalQuality.get();
                    quality.setRef_bo(1);
                    qualityRepo.save(quality);
                } else {
                    // You can log this, throw an exception, or return an error response
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //++++++++++++++++++++++++++++++++ End Quality +++++++++++++++++++++++++++++++++++++++
    
    //++++++++++++++++++++++++++++++++ Romove Quality: +++++++++++++++++++++++++++++++++++++++
    public void removeRefFromothers(List<Long> ids) {
        List<Quality> qualities = qualityRepo.findAll();
        
        for (Quality quality : qualities) {
            if (!ids.contains(quality.getId())) {
                quality.setRef_bo(0);
                qualityRepo.save(quality);
            }
        }
    }
  //++++++++++++++++++++++++++++++++ End Romove Quality: +++++++++++++++++++++++++++++++++++++++
    
   public List<User> getUserByTitle(String title){
       return userRepo.findByTitle(title);
   }
   public List<User> getUserByparent(Long id){
       return userRepo.findByParent(id);
   } 
   public List<User> getUserByChild(Long id){
       
       User user = userRepo.findById(id).orElse(null);
       if(Objects.nonNull(user)) {
           return userRepo.findByChild(user.getParent());
       } else {
           return null;
       }
      
   }
   
   public CloneEtape getNextEtape(CloneEtapeClass cloneetape){
       
       List<CloneEtape> steps = ctr.findByCourrierIdOrderByNumeroAsc(cloneetape.getIdCourrier());
       
       
       for (CloneEtape cc : steps) {
           if (cc.getNumero() - cloneetape.getNumero() == 1 ) {
               return cc;
           }
       }
       
       return null;
   }
}
