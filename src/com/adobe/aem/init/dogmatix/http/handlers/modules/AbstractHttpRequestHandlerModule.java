package com.adobe.aem.init.dogmatix.http.handlers.modules;

import com.adobe.aem.init.dogmatix.config.ModuleConfig;
import com.adobe.aem.init.dogmatix.http.handlers.HttpContext;

public abstract class AbstractHttpRequestHandlerModule {

	protected int status;

	protected ModuleConfig config;

	public ModuleConfig getConfig() {
		return config;
	}

	public void setConfig(ModuleConfig config) {
		this.config = config;
	}

	protected void init() throws Exception {
		assert (config!=null);
		// initialize child instance using module config
	}

	/**
	 * abstract method. to be implemented by child classes of modules wishing to
	 * consume the requests wrapped in context.
	 * 
	 * @param context Object wrapper for request and response
	 */
	public abstract void consume(HttpContext context);

}
