package com.adobe.aem.init.dogmatix.http.handlers.modules.resources;

import com.adobe.aem.init.dogmatix.http.handlers.modules.RequestProcessor;
import com.adobe.aem.init.dogmatix.http.handlers.modules.resources.storage.Repository;

public abstract class ResourcesRequestProcessor implements RequestProcessor {
	
	protected Repository repository;
	
	protected String urlPattern;

	public ResourcesRequestProcessor setRepository(Repository repository) {
		this.repository = repository;
		return this;
	}
	
	public ResourcesRequestProcessor setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
		return this;
	}
}
