package com.veviosys.vdigit.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer; 
import org.springframework.cache.concurrent.ConcurrentMapCacheManager; 
import org.springframework.stereotype.Component;


@Component
public class CachingConfig implements CacheManagerCustomizer<ConcurrentMapCacheManager> {
 
    @Override
    public void customize(ConcurrentMapCacheManager cacheManager) {
    	List<String> names = new ArrayList<String>();
    	names.add("masterconfig");
        cacheManager.setCacheNames(names);
        

    }


}
