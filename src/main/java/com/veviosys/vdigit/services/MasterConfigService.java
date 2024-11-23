package com.veviosys.vdigit.services;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.Store;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.veviosys.vdigit.classes.FluxElectro;
import com.veviosys.vdigit.classes.StorageDetail;
import com.veviosys.vdigit.models.MasterConfig;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.repositories.MasterConfigRepository;
import com.veviosys.vdigit.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

@Service
@Slf4j
public class MasterConfigService {

	@Autowired
	private MasterConfigRepository configRepository;
	@Autowired
	private UserRepository userRepository;
	@Lazy
	@Autowired
	masterService masterService;

	public User getMaster() {
		return userRepository.findById(connectedUserMaster(connectedUser().getUserId())).orElse(null);
	}

	public User connectedUser() {
		CostumUserDetails user = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		return user.getUser();
	}

	public Long connectedUserMaster(Long userid) {
		Long id;
		id = userRepository.findUserMaster(userid);
		if (id != null) {
			return id;
		}
		return userid;
	}
	
	public MasterConfig getMasterConfigByNameCapture(String name) {
	   return configRepository.findByMasterUserIdAndConfigName(1L, name);
	}


	// @Cacheable("masterconfigs")
	public List<MasterConfig> getMasterConfigs() {
           
	    if(Objects.isNull(connectedUser().getMaster())) {
	        List<MasterConfig> list = configRepository.findByMasterUserId(connectedUser().getUserId());
            for (MasterConfig masterConfig : list) {
                if(masterConfig.getConfigName().equals("LEDGER")) {
                     String[] value = masterConfig.getConfigValue().split("\\|");                     
                    for (String name : value) {
                        System.out.println(name);
                    }
                    value[2] = "*******";
                     
                    String newConfig = String.join("|", value);
                    masterConfig.setConfigValue(newConfig);
                }
            }
	        return list; 
	    }else {
	        List<MasterConfig> list = configRepository.findByMasterUserIdAndHasAccess(getMaster().getUserId(),1);  
	        for (MasterConfig masterConfig : list) {
                if(masterConfig.getConfigName().equals("LEDGER")) {
                     String[] value = masterConfig.getConfigValue().split("\\|");

                    value[2] = "*******";
                     
                    String newConfig = String.join("|", value);
                    masterConfig.setConfigValue(newConfig);
                }
            }
	        return list;
	    }
		
	}


    public void testReadEmails(String host,String email,String password) throws Exception {
        
        log.info("ana dkhalt ");
        try {
        //create properties field
        Properties properties = new Properties();

        properties.put("mail.pop3.host", host);
        properties.put("mail.pop3.port", "143");
        properties.put("mail.pop3.starttls.enable", "true");
        Session emailSession = Session.getInstance(properties);
    
        //create the POP3 store object and connect with the pop server
        Store store = emailSession.getStore("imap");

        store.connect(host, email , password );
        
 
     
        }catch (Exception e) {
             System.err.println(e.getMessage().toString());
             throw new Exception("Ex");
         }
    }
	
	// CREATE NEW CONFIG
	public void addNewConfig(String configName, String configValue) throws Exception {
 
	    
	   
	  
	
	    if(configName.equals("FLUX-ELECTRO")){
	        log.info("{}",configName);

	        FluxElectro convertedObject = new FluxElectro();

	        Type listType = new TypeToken<FluxElectro>() {
	        }.getType();

	        convertedObject = new Gson().fromJson(configValue, listType);

	        
	        testReadEmails(convertedObject.getHost(),convertedObject.getEmail(),convertedObject.getPassword());
	        
	       
	   
	       
	    }else if(configName.equals("USER_INREGRATION_INFO")) {
	          
	         configValue = Base64.getEncoder().encodeToString(configValue.getBytes());
	        
	    }
	    
		MasterConfig config = configRepository.findByMasterUserIdAndConfigName(getMaster().getUserId(), configName);

        
		if (Objects.isNull(config))
			config = new MasterConfig();
		if (configName.toLowerCase().equals("ledger")) {

			try {

				WebClient.create().post().uri(configValue.split("\\|")[1]).accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Basic " + configValue.split("\\|")[2])
						.body(BodyInserters.fromValue("{}")).retrieve().onStatus(HttpStatus::isError, res -> {

							if (res.statusCode().equals(HttpStatus.BAD_REQUEST)) {

								return Mono.error(new Exception("IGN"));
							}
							return Mono.error(new Exception("GOE"));

						}).bodyToMono(JsonNode.class).block();

			} catch (WebClientException e) {

				// TODO: handle exception

			} catch (Exception e) {
				System.err.println(e.getMessage().toString());
				if (e.getMessage().contains("IGN")) {
					config.setMaster(getMaster());
					config.setConfigName(configName);
					config.setConfigValue(configValue);
					configRepository.save(config);
				} else {
					throw new Exception("Ex");
				}

			}

		} else {
			config.setMaster(getMaster());
			config.setConfigName(configName);
			config.setConfigValue(configValue);
			configRepository.save(config);

		}

		//
	}

	public void configStoragePath(MasterConfig ms) throws Exception {

		MasterConfig config = configRepository.findByMasterUserIdAndConfigName(getMaster().getUserId(),
				ms.getConfigName());

		masterService.addDirectory(ms.getConfigValue().split(",")[0] + "\\upload");
		if (Objects.isNull(config)) {
			config = new MasterConfig();
			config.setMaster(getMaster());
			config.setConfigName(ms.getConfigName());
			config.setConfigValue(ms.getConfigValue());
		} else {
			config.setConfigValue(config.getConfigValue() + "|" + ms.getConfigValue());
		}
		configRepository.save(config);
	}

	public void removeConfig(MasterConfig ms, String pathToRemove) {
		String newConfig = "";
		String volumes[] = ms.getConfigValue().split("\\|");
		for (int i = 0; i < volumes.length; i++) {
			String volume = volumes[i];
			System.out.println(volume);
			String path = volume.split(",")[0];
			if (!path.equals(pathToRemove)) {
				newConfig += volume;
				System.out.println("TEEEEEEEEEST"+(i + 1 < volumes.length));
				if (i + 2 < volumes.length) {
					newConfig += "|";
				}
			}
		}
		ms.setConfigValue(newConfig);
		configRepository.save(ms);

		// for (String volume : ms.getConfigValue().split("//|")) {
		// String path=volume.split(",")[0];
		// if(!path.equals(pathToRemove)){
		// newConfig+=volume+"|";
		// }
		// }

	}

	public MasterConfig getStragey2Value() {
		// TODO Auto-generated method stub
		return configRepository.findByMasterUserIdAndConfigName(getMaster().getUserId(), "BARCODE_STRATEGY_2");
	}

	public List<StorageDetail> findConfigByName(String name) {
		List<StorageDetail> disks = new ArrayList<StorageDetail>();
		MasterConfig config = configRepository.findByMasterUserIdAndConfigName(getMaster().getUserId(), name);
		if (Objects.nonNull(config)) {
			for (String data : config.getConfigValue().split("\\|")) {
				StorageDetail sd = new StorageDetail();
				String nickname = data.split(",")[1];
				String path = data.split(",")[0];
				sd.setPath(path);
				sd.setName(nickname);
				List<Long> detail = masterService.testConnectToDisk(path);
				if (Objects.nonNull(detail)) {
					sd.setAccess(1);
					sd.setFreeSpace(detail.get(1));
					sd.setSize(detail.get(0));

				} else {
					sd.setAccess(0);
				}
				disks.add(sd);
			}
			return disks;
		}
		return null;
	}
  
	public FluxElectro findConfigByNameFlux(String name) {
	    
   
        MasterConfig config = configRepository.findByMasterUserIdAndConfigName(connectedUserMaster(connectedUser().getUserId()), name);
     

        FluxElectro convertedObject = new FluxElectro();

        Type listType = new TypeToken<FluxElectro>() {
        }.getType();

        convertedObject = new Gson().fromJson(config.getConfigValue(), listType);
        
        
        return convertedObject;
    }
	
	public MasterConfig getCmIntegration(String name) {
        
	    
        MasterConfig config = configRepository.findByMasterUserIdAndConfigName(connectedUserMaster(connectedUser().getUserId()), name);
             
        return config;
    }

	
	public String findActivePath() {
		return configRepository.findByMasterUserIdAndConfigName(getMaster().getUserId(), "ACTIVE_PATH")
				.getConfigValue();
	}

	public String findActivePathByMaster(Long id) {
		return configRepository.findByMasterUserIdAndConfigName(id, "ACTIVE_PATH").getConfigValue();
	}

	public MasterConfig getMasterConfigByName(String name) {

		return configRepository.findByMasterUserIdAndConfigName(getMaster().getUserId(), name);
	}
}
