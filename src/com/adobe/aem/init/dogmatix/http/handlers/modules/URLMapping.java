package com.adobe.aem.init.dogmatix.http.handlers.modules;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.config.ModuleConfig;
import com.adobe.aem.init.dogmatix.util.Matcher;

public class URLMapping {
	
	private static final Logger logger = LoggerFactory
			.getLogger(URLMapping.class);
	
	private static final int MAX_URL_CACHE_SIZE = 5000;
	private static final int URL_CACHE_CLEAN_CHUNK = 100;

	private static Map<String, ModuleConfig> urlMapping;
	private static Map<String, Long> lastUsed;
	private static Map<String, Long> countUsed;
	
	public static ModuleConfig getModuleConfig(String url) {
		logger.debug("Trying to lookup module for {}", url);
		if(urlMapping == null) {
			logger.error("urlMapping is null");
			return null;//throw uninitialized error?
		}
		if(!urlMapping.containsKey(url)) {
			for(ModuleConfig config : urlMapping.values()) {
				logger.debug("Check if {} matches {}", url, config.getUrl());
				if(Matcher.matches(url, config.getUrl())) {
					if(urlMapping.size() == MAX_URL_CACHE_SIZE) {
						//cleanUpCache();
					}
					urlMapping.put(url, config);
					lastUsed.put(url, System.currentTimeMillis());
					countUsed.put(url, (countUsed.containsKey(url)?countUsed.get(url):0L)+1);
					break;
				}
			}
		}
		ModuleConfig config = urlMapping.get(url);
		if(config == null) {
			logger.error("No module fit to consume {}", url);
			return null;
		}
		logger.debug("Found {} for {}", config.getClassName(), config.getUrl());
		return config;
	}

	public static void map(ModuleConfig moduleConfig) {
		logger.debug("Mapping URL {} to {}", moduleConfig.getUrl(), moduleConfig.getClassName());
		if(urlMapping == null) {
			urlMapping = Collections.synchronizedMap(new HashMap<String, ModuleConfig>());
			lastUsed = Collections.synchronizedMap(new HashMap<String, Long>());
			countUsed = Collections.synchronizedMap(new HashMap<String, Long>());
		}
		urlMapping.put(moduleConfig.getUrl(), moduleConfig);
	}
	
	public static void map(List<ModuleConfig> configs) {
		for(ModuleConfig config : configs) {
			map(config);
		}
	}
	
	private static void cleanUpCache() {
		//if policy is DiscardLastUsed
		synchronized(lastUsed) {
			int count = 0;
			for(int i = 0; i < urlMapping.size() && count < URL_CACHE_CLEAN_CHUNK; i++) {
				for(String url : urlMapping.keySet()) {
					
				}
			}
		}
		//else if policy is DiscardLeastUsed
		synchronized(countUsed) {
			
		}
	}

}