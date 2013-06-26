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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.config.ModuleConfig;
import com.adobe.aem.init.dogmatix.exceptions.InvalidModuleException;
import com.adobe.aem.init.dogmatix.util.ObjectPool;

/**
 * An object pool implementation of {@link AbstractHttpRequestHandlerModule}
 * Each request to this module will be served by a re-usable instance in the
 * pool. Since instances can be used by multiple requests, they must be
 * stateless. If no instances are free in the pool, new ones are created as
 * required. Objects in the pool are also expired after a default idle time 
 * of 30 seconds so as to free memory.
 * 
 * @author vnagpal
 * 
 */
public class ModuleInstancePool extends
		ObjectPool<AbstractHttpRequestHandlerModule> {

	private static final Logger logger = LoggerFactory
			.getLogger(ModuleInstancePool.class);

	private ModuleConfig moduleConfig;

	public ModuleInstancePool(ModuleConfig moduleConfig)
			throws InvalidModuleException {
		super();
		try {
			Class.forName(moduleConfig.getClassName()).newInstance();
		} catch (Exception e) {
			logger.error("Invalid class name", e);
			throw new InvalidModuleException(moduleConfig.getClassName());
		} finally {

		}

		this.moduleConfig = moduleConfig;
		logger.debug("Created object pool for {}", moduleConfig.getClassName());
	}

	@Override
	protected AbstractHttpRequestHandlerModule create() throws InvalidModuleException {
		try {
			logger.debug("Creating instance of {}",	this.moduleConfig.getClassName());
			AbstractHttpRequestHandlerModule moduleObj = 
					(AbstractHttpRequestHandlerModule) Class.forName(moduleConfig.getClassName()).newInstance();
			
			logger.debug("Setting the config {}", moduleConfig);
			moduleObj.setConfig(moduleConfig);
			
			logger.debug("Initializing module instance");
			moduleObj.init();
			
			logger.debug("Adding new object to pool for {}", this.moduleConfig.getClassName());
			return moduleObj;
		} catch (Exception e) {
			logger.error("Error adding new object to pool for {} : {}",
					this.moduleConfig.getClassName(), e.getMessage());
			throw new InvalidModuleException(moduleConfig.getClassName(), e);
		}
	}

	@Override
	public boolean validate(AbstractHttpRequestHandlerModule o) {
		return true;
	}

	@Override
	public void expire(AbstractHttpRequestHandlerModule o) {
		//no-op
	}

}
