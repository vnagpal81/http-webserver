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

package com.adobe.aem.init.dogmatix.http.handlers.modules.resources;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.exceptions.HttpError;
import com.adobe.aem.init.dogmatix.exceptions.InvalidModuleConfigException;
import com.adobe.aem.init.dogmatix.http.handlers.HttpContext;
import com.adobe.aem.init.dogmatix.http.handlers.modules.AbstractHttpRequestHandlerModule;
import com.adobe.aem.init.dogmatix.http.handlers.modules.resources.storage.Repository;
import com.adobe.aem.init.dogmatix.http.handlers.modules.resources.storage.RepositoryFactory;
import com.adobe.aem.init.dogmatix.http.handlers.modules.resources.storage.RepositoryNotAccessibleException;
import com.adobe.aem.init.dogmatix.http.request.HttpRequest;
import com.adobe.aem.init.dogmatix.http.request.Method;
import com.adobe.aem.init.dogmatix.http.response.HttpResponse;

/**
 * A Resources module is a server module which acts like a file server
 * and serves requests for fetching, adding, removing, and updating files 
 * in a {@link Repository}
 * 
 * Depending on the settings in the XML or via {@link Module} annotation,
 * the module repository is configured to serve the requests by delegating 
 * the crud operations to the repository.
 * 
 * Taking advantage of the module design, a user running the server has the
 * ability to configure more than one {@link ResourceModule} simultaneously,
 * the only condition being the url mapping should be different.
 * 
 * Hence, this module may be used to serve files via a local repo and web 
 * resources via a s3 repo by configuring two modules with class {@link ResourceModule}
 * and url "files/**" and "web/**" respectively.
 */
/*@Module(url="files/**", settings={
		@Setting(name = ResourcesModule.SETTINGS.REPOSITORY, value = "local"),
		@Setting(name = ResourcesModule.SETTINGS.BASE_DIR, value = "C:\\temp"),
		@Setting(name = ResourcesModule.SETTINGS.UPLOAD_LIMIT, value = "5000"),
		@Setting(name = ResourcesModule.SETTINGS.TRACK, value = "false"),
		@Setting(name = ResourcesModule.SETTINGS.DOWNLOAD_PARAM, value = "download"),
		@Setting(name = ResourcesModule.SETTINGS.DEFAULT_DIR_PATH, value = "index.html"),
		@Setting(name = ResourcesModule.SETTINGS.REDIRECT_TO_DEFAULT, value = "true"),
		@Setting(name = ResourcesModule.SETTINGS.NOT_FOUND, value = "404.html"),
		@Setting(name = ResourcesModule.SETTINGS.REDIRECT_TO_NOT_FOUND, value = "true"),
})*/
public class ResourcesModule extends AbstractHttpRequestHandlerModule {
	
	private static final Logger logger = LoggerFactory.getLogger(ResourcesModule.class);
	
	public interface SETTINGS {
		/**
		 * Underlying storage strategy. 
		 * One of local, remote, ftp, s3. 
		 * May need extra settings depending on the repository value
		 * 
		 */
		String REPOSITORY = "repository"; 
		/**
		 * Base directory path in case local storage is used.
		 */
		String BASE_DIR = "baseDir"; 
		/**
		 * Max upload size in case of POST requests. Units are bytes.
		 */
		String UPLOAD_LIMIT = "uploadLimit";
		/**
		 * Tracks GET count if true.
		 */
		String TRACK = "track";
		/**
		 * Query param to check in case response is to be sent as an attachment.
		 */
		String DOWNLOAD_PARAM = "downloadParam";
		/**
		 * The file to default to in case requested resource is a directory.
		 * Usually file servers have a default index.html in each folder.
		 */
		String DEFAULT_DIR_PATH = "defaultDirPath";
		/**
		 * The strategy to employ in case requested resource is a directory.
		 * If true, send a redirect to the user-agent.
		 * If false, fall back to response publishing the defaultDirPath.
		 */
		String REDIRECT_TO_DEFAULT = "redirectToDefault";
		/**
		 * The file to default to in case requested resource is not found.
		 * Usually file servers have a default 404.html to be served globally.
		 */
		String NOT_FOUND = "notFound";
		/**
		 * The strategy to employ in case requested resource is not found.
		 * If true, send a redirect to the user-agent.
		 * If false, fall back to response publishing the notFound.
		 */
		String REDIRECT_TO_NOT_FOUND = "redirectToNotFound";
	}
	
	static {
		DEFAULTS = new Properties();
		DEFAULTS.setProperty(SETTINGS.REPOSITORY, "local");
		DEFAULTS.setProperty(SETTINGS.BASE_DIR, ".." + File.separator + "resources");
		DEFAULTS.setProperty(SETTINGS.UPLOAD_LIMIT, "5242880");
		DEFAULTS.setProperty(SETTINGS.TRACK, "false");
		DEFAULTS.setProperty(SETTINGS.DOWNLOAD_PARAM, "download");
		//DEFAULTS.setProperty(SETTINGS.DEFAULT_DIR_PATH, "index.html"); Optional parameter
		DEFAULTS.setProperty(SETTINGS.REDIRECT_TO_DEFAULT, "true");
		//DEFAULTS.setProperty(SETTINGS.NOT_FOUND, "404.html"); Optional parameter
		DEFAULTS.setProperty(SETTINGS.REDIRECT_TO_NOT_FOUND, "true");
	}
	
	/**
	 * A method resolver is just a routing mechanism to route {@link HttpRequest}s to
	 * the specific {@link ResourcesRequestProcessor} based on HTTP {@link Method}
	 */
	private Map<Method, ResourcesRequestProcessor> methodResolver;
	
	/**
	 * Initializes a Resources module by
	 * 
	 * (1) Creating the underlying repository connection
	 * (2) Populating the method resolver with {@link ResourcesRequestProcessor}s
	 * 	   for each HTTP {@link Method} using the module level {@link Setting}s
	 */
	protected void init() throws InvalidModuleConfigException {
		super.init();
		
		//populate repository from module config
		Repository repository;
		try {
			repository = RepositoryFactory.getRepository(config.getSettings());
		} catch (RepositoryNotAccessibleException e) {
			logger.error("Not able to connect to repository", e);
			throw new InvalidModuleConfigException(e);
		}
		
		methodResolver = new HashMap<Method, ResourcesRequestProcessor>();
		methodResolver.put(Method.GET, 
				new GETResource(config.getSettings())
					.setRepository(repository)
					.setUrlPattern(config.getUrl()));
		methodResolver.put(Method.POST, 
				new POSTResource(config.getSettings())
					.setRepository(repository)
					.setUrlPattern(config.getUrl()));
	}

	/**
	 * Consumes the HTTP request.
	 * Delegates the actual processing to a request processor determined by resolving the
	 * HTTP method.
	 * 
	 * If no processor is bound to the HTTP method, a 404 is raised.
	 * 
	 * @param ctx the HTTP context to consume
	 * @return Always return true, signifying to continue further processing in the chain
	 */
	@Override
	public boolean consume(HttpContext ctx) {
		HttpResponse response = ctx.getResponse();
		
		try {
			HttpRequest request = ctx.getRequest();
			
			// get request processor
			ResourcesRequestProcessor processor = methodResolver.get(request.getMethod());
			
			if (processor != null) {
				
				processor.processRequest(request, response);
				
			} else {
				// raise 404
				response.err(new HttpError(404, String.format("Unsupported HTTP method %s", request.getMethod())));
			}			
		}
		catch(HttpError e) {
			response.err(e);
		}
		
		return false;
	}

}
