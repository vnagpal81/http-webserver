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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.config.ModuleConfig;
import com.adobe.aem.init.dogmatix.exceptions.InvalidModuleException;
import com.adobe.aem.init.dogmatix.util.ReflectionUtils;

/**
 * A Factory which provides module instances on demand. Maintains an internal
 * pool of module instances for re-use, which saves module construction and
 * destruction time. Loads the modules at server startup.
 * 
 * @author vnagpal
 * 
 */
public class ModuleFactory {

	private static final Logger logger = LoggerFactory
			.getLogger(ModuleFactory.class);

	private static HashMap<ModuleConfig, ModuleInstancePool> moduleInstanceCache;

	public static AbstractHttpRequestHandlerModule getModule(
			ModuleConfig moduleConfig) throws Exception {
		AbstractHttpRequestHandlerModule module = moduleInstanceCache.get(
				moduleConfig).checkOut();
		return module;
	}

	/**
	 * Loads the modules instances. Must be called at server startup
	 * <b>before</b> the server starts listening. May be called again if we wish
	 * to support config reload without server restart in future.
	 * 
	 * @throws InvalidModuleException
	 */
	public static void load(List<ModuleConfig> moduleConfigs)
			throws InvalidModuleException {
		logger.debug("Begin loading server modules");
		if (moduleInstanceCache == null) {
			moduleInstanceCache = new HashMap<ModuleConfig, ModuleInstancePool>();
		}
		for (ModuleConfig moduleConfig : moduleConfigs) {
			if (!moduleInstanceCache.containsKey(moduleConfig)) {
				logger.debug("Loading module {}", moduleConfig.getClassName());
				moduleInstanceCache.put(moduleConfig, new ModuleInstancePool(
						moduleConfig));
			}
		}
		logger.debug("Finish loading server modules");
		logger.debug("Generate URL mappings for all modules");
		URLMapping.map(moduleConfigs);
	}

	/**
	 * Auto-detects and loads the modules in a specific path. Must be called at
	 * server startup <b>before</b> the server starts listening. Convenience
	 * method to load all modules in a package path by annotating a class with
	 * {@link @Module}. Inspired from Spring's Component Scan.
	 * 
	 * @param path
	 *            package path under which to scan the modules for
	 * @throws InvalidModuleException
	 */
	public static void annotatedLoad(String path) throws InvalidModuleException {
		List<Class<?>> moduleClasses = ReflectionUtils
				.getClassesWithAnnotation(path, Module.class);
		List<ModuleConfig> moduleConfigs = new ArrayList<ModuleConfig>();
		for (Class<?> clazz : moduleClasses) {
			String className = clazz.getName();
			Module annotation = clazz.getAnnotation(Module.class);
			String url = annotation.url();
			ModuleConfig moduleConfig = new ModuleConfig(className, url);

			Setting[] settings = annotation.settings();
			for (Setting setting : settings) {
				moduleConfig.addSetting(setting.name(), setting.value());
			}
			moduleConfigs.add(moduleConfig);
		}
		load(moduleConfigs);
	}

}