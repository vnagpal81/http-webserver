package com.adobe.aem.init.dogmatix.http.handlers.modules;

import java.util.Properties;

import com.adobe.aem.init.dogmatix.config.ModuleConfig;
import com.adobe.aem.init.dogmatix.exceptions.InvalidModuleConfigException;
import com.adobe.aem.init.dogmatix.http.handlers.HttpContext;
import com.adobe.aem.init.dogmatix.http.handlers.modules.resources.ResourcesModule;
import com.adobe.aem.init.dogmatix.http.handlers.modules.rest.RESTModule;

/**
 * Abstract base class representing a Module which consumes requests. All
 * concrete implementations <b>must</b> extend this class.
 * 
 * @author vnagpal
 * @see ResourcesModule
 * @see EchoModule
 * @see ProxyServerModule
 * @see RESTModule
 */
public abstract class AbstractHttpRequestHandlerModule {
	
	protected static Properties DEFAULTS = new Properties();

	/**
	 * Each module is configured via XML or class-level Annotation. It is the
	 * responsibility of the creator of a module instance to set the
	 * corresponding config object without which the functioning of the module
	 * would be impaired.
	 */
	protected ModuleConfig config;

	public ModuleConfig getConfig() {
		return config;
	}

	public void setConfig(ModuleConfig config) {
		this.config = config;
	}

	/**
	 * Initialization hook. Can be invoked by child classes to initialize
	 * resources using the module config, which is guaranteed to be set by this
	 * time.
	 * 
	 * Different from the constructor because resources might need the
	 * moduleConfig.
	 * 
	 * @throws Exception
	 */
	protected void init() throws InvalidModuleConfigException {
		assert (config != null);
		// initialize child instance using module config
		Properties moduleSettings = new Properties(DEFAULTS);
		moduleSettings.putAll(config.getSettings());
		config.setSettings(moduleSettings);
	}

	/**
	 * abstract method. to be implemented by child classes of modules wishing to
	 * consume the requests wrapped in context.
	 * 
	 * @param context
	 *            Object wrapper for request and response
	 * @return
	 */
	public abstract boolean consume(HttpContext context);

}
