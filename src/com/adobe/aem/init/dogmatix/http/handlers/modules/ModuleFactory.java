package com.adobe.aem.init.dogmatix.http.handlers.modules;

import java.util.HashMap;
import java.util.List;

import com.adobe.aem.init.dogmatix.exceptions.InvalidModuleException;
import com.adobe.aem.init.dogmatix.util.ReflectionUtils;

/**
 * A Factory which provides module instances on demand. Maintains an internal pool of module instances for re-use.
 * Loads the modules at server startup.
 * 
 * @author vnagpal
 *
 */
public class ModuleFactory {
	
	private static HashMap<String, ModuleInstancePool> moduleInstanceCache;
	
	public static AbstractHttpRequestHandlerModule getModule(String className) {
		return moduleInstanceCache.get(className).checkOut();
	}

	/**
	 * Loads the modules instances. Must be called at server startup *before* the socket starts listening.
	 * May be called again if we wish to support config reload without server restart in future.
	 * @throws InvalidModuleException 
	 */
	public static void load(List<String> modules) throws InvalidModuleException {
		moduleInstanceCache = new HashMap<String, ModuleInstancePool>();			
		for(String module: modules) {
			ModuleInstancePool moduleInstancePool = new ModuleInstancePool(module);
			moduleInstanceCache.put(module, moduleInstancePool);
		}
	}
	
	/**
	 * Auto-detects and loads the modules in a specific path. Must be called at server startup *before* the socket starts listening. 
	 * Convenience method to load all modules in a package path by annotating a class with @Module.
	 * Inspired from Spring's Component Scan.
	 * @param path package path under which to scan the modules for
	 * @throws InvalidModuleException
	 */
	public static void annotatedLoad(String path) throws InvalidModuleException {
		load(ReflectionUtils.getClassNamesInPackageWithAnnotation(path, Module.class));
	}
	
}