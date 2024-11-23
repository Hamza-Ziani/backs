package com.veviosys.vdigit.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Objects;

import com.veviosys.vdigit.models.MasterConfig;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.repositories.MasterConfigRepository;

import com.veviosys.vdigit.repositories.UserRepository;
import com.veviosys.vdigit.services.CostumUserDetailService;

import com.veviosys.vdigit.services.IldapService;

@Component
class AuthFilter extends OncePerRequestFilter {

    @Autowired(required=true)
    private CostumUserDetailService cuds;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    IldapService serviceLdap;
    @Autowired
    MasterConfigRepository configRepository;
    
    private MasterConfig getMasterConfig(User r) {
        return configRepository.findByMasterUserIdAndConfigName(r.getUserId(), "LDAP_CONFIG");
    }
    Logger logger = LoggerFactory.getLogger("ApiIntegration");
   
    @Override
    @Transactional
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            if (request.getServletPath().contains("/secondary/userlogin")) {

                if (request.getParameter("secondary").equals("true")) {

                    String auth1 = request.getParameter("code");
                    byte[] decodedBytes = Base64.getDecoder().decode(auth1);
                    String decodedString = new String(decodedBytes);
                    String username = decodedString.split(":")[0];
                    String password = decodedString.split(":")[1];
 
                   // System.out.println("password" + pw);
                    if (Objects.nonNull(username) && Objects.nonNull(password)) {

                        User user = userRepo.findByUsernameIgnoreCase(username);
                      
                      
                        if(user.getFromLdap() == 1) {
                            
                           // if (serviceLdap.logToLdap(username, password, getMasterConfig(user.getMaster()))) {
                                
                            
                                
                           

                                User SecondaryUser = userRepo.getUserSecondary(user.getUserId());
                              
                                if (Objects.nonNull(SecondaryUser)) {

                                    UserDetails cud = cuds.loadUserByUsername(SecondaryUser.getUsername());
                                    
                                    User u = cuds.getUser();
                                    
                              
                                        List<GrantedAuthority> authorities = new ArrayList<>();
                                        u.getRoles().forEach(r -> {

                                            GrantedAuthority auth = new SimpleGrantedAuthority("ROLE_" + r.getRole());
                                            authorities.add(auth);

                                        });
                                        
                                         
                                           
                                        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(
                                                cud,password, authorities);

                                       ;
                                        SecurityContext sc = SecurityContextHolder.getContext();
                                        sc.setAuthentication(authReq);  
                                        
                                       

                            //   }
                            }
                        }else {
                            
                            if (passwordEncoder.matches(password.toString(), user.getPassword())) {

                                User SecondaryUser = userRepo.getUserSecondary(user.getUserId());

                                
                                if (SecondaryUser != null) {

                                    UserDetails cud = cuds.loadUserByUsername(SecondaryUser.getUsername());

                                    User u = cuds.getUser();
                                    
                              
                                        List<GrantedAuthority> authorities = new ArrayList<>();
                                        u.getRoles().forEach(r -> {

                                            GrantedAuthority auth = new SimpleGrantedAuthority("ROLE_" + r.getRole());
                                            authorities.add(auth);

                                        });
                                        
                                       
                                        
                                 
                                        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(
                                                cud,u.getPassword(), authorities);
                                        
                                        SecurityContext sc = SecurityContextHolder.getContext();
                                        sc.setAuthentication(authReq); 
                                       
                                       
                                        
                                    }
                                 
                                

                            }
                        }
                           
                               
                        }
                        
                       }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);

    }

}
