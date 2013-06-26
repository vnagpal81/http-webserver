/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. The ASF licenses this file to You 
 * under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.adobe.aem.init.dogmatix.http.handlers.modules;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.config.ModuleConfig;
import com.adobe.aem.init.dogmatix.util.Matcher;

/**
 * A utility class which maintains the URL mapping of url patterns against modules.
 * It maintains an internal cache of mappings so as not to do run a match every time.
 * Since, the cache is in memory, a max size is enforced on it.
 * Once the max size is reached, the cache is purged using a LRU or LFU algorithm 
 * as per the declared cache policy
 * 
 * @author vnagpal
 *
 */
public class URLMapping {
	
	private static final Logger logger = LoggerFactory
			.getLogger(URLMapping.class);
	
	/**
	 * Threshold for cache size exceeding which a purge is done
	 */
	private static final int MAX_URL_CACHE_SIZE = 5000;
	
	/**
	 * Maximum number of cache entries to invalidate at one go
	 */
	private static final int URL_CACHE_CLEAN_CHUNK = 100;

	/**
	 * The URL Mapping cache
	 */
	private static Map<String, ModuleConfig> urlMapping;
	
	/**
	 * Record of the last time a cache entry was used
	 */
	private static Map<String, Long> lastUsed;

	/**
	 * Record of number of times a cache entry is used
	 */
	private static Map<String, Long> countUsed;
	
	/**
	 * Looks up a module config object against a url.
	 * 
	 * @param url request URI
	 * @return the module config object if found, else null
	 */
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

	/**
	 * Generates a url mapping for a url pattern against a module config
	 * Should be called at server startup time to initialize the mapping cache.
	 * 
	 * @param moduleConfig The module config to map
	 */
	public static void map(ModuleConfig moduleConfig) {
		logger.debug("Mapping URL {} to {}", moduleConfig.getUrl(), moduleConfig.getClassName());
		if(urlMapping == null) {
			urlMapping = Collections.synchronizedMap(new HashMap<String, ModuleConfig>());
			lastUsed = Collections.synchronizedMap(new HashMap<String, Long>());
			countUsed = Collections.synchronizedMap(new HashMap<String, Long>());
		}
		urlMapping.put(moduleConfig.getUrl(), moduleConfig);
	}
	
	/**
	 * Convenience method to map multiple modules at once.
	 * 
	 * @param configs The module configs to map
	 */
	public static void map(List<ModuleConfig> configs) {
		for(ModuleConfig config : configs) {
			map(config);
		}
	}
	
	/**
	 * Purges the mapping cache according to purge policy.
	 * Policies supported are LRU and LFU
	 */
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