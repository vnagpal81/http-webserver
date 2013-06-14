package com.adobe.aem.init.dogmatix.http.handlers.modules.resources;

import java.util.HashMap;
import java.util.Map;

import com.adobe.aem.init.dogmatix.exceptions.HttpError;
import com.adobe.aem.init.dogmatix.http.handlers.HttpContext;
import com.adobe.aem.init.dogmatix.http.handlers.modules.AbstractHttpRequestHandlerModule;
import com.adobe.aem.init.dogmatix.http.handlers.modules.Module;
import com.adobe.aem.init.dogmatix.http.handlers.modules.Setting;
import com.adobe.aem.init.dogmatix.http.handlers.modules.resources.storage.Repository;
import com.adobe.aem.init.dogmatix.http.handlers.modules.resources.storage.RepositoryFactory;
import com.adobe.aem.init.dogmatix.http.request.HttpRequest;
import com.adobe.aem.init.dogmatix.http.request.Method;
import com.adobe.aem.init.dogmatix.http.response.HttpResponse;

@Module(url="files/**", settings={
		@Setting(name = ResourcesModule.SETTINGS.REPOSITORY, value = "local"),
		@Setting(name = ResourcesModule.SETTINGS.BASE_DIR, value = "C:\\temp"),
		@Setting(name = ResourcesModule.SETTINGS.UPLOAD_LIMIT, value = "5000"),
		@Setting(name = ResourcesModule.SETTINGS.TRACK, value = "false"),
		@Setting(name = ResourcesModule.SETTINGS.DOWNLOAD_PARAM, value = "download"),
		@Setting(name = ResourcesModule.SETTINGS.DEFAULT_DIR_PATH, value = "index.html"),
		@Setting(name = ResourcesModule.SETTINGS.REDIRECT_TO_DEFAULT, value = "true"),
		@Setting(name = ResourcesModule.SETTINGS.NOT_FOUND, value = "404.html"),
		@Setting(name = ResourcesModule.SETTINGS.REDIRECT_TO_NOT_FOUND, value = "true"),
})
public class ResourcesModule extends AbstractHttpRequestHandlerModule {
	
	public interface SETTINGS {
		String REPOSITORY = "repository";
		String BASE_DIR = "baseDir";
		String UPLOAD_LIMIT = "uploadLimit";
		String TRACK = "track";
		String DOWNLOAD_PARAM = "downloadParam";
		String DEFAULT_DIR_PATH = "defaultDirPath";
		String REDIRECT_TO_DEFAULT = "redirectToDefault";
		String NOT_FOUND = "notFound";
		String REDIRECT_TO_NOT_FOUND = "redirectToNotFound";
	}

	private Map<Method, ResourcesRequestProcessor> methodResolver;
	
	protected void init() throws Exception {
		super.init();

		//populate repository from module config
		Repository repository = RepositoryFactory.getRepository(config);
		
		methodResolver = new HashMap<Method, ResourcesRequestProcessor>();
		methodResolver.put(Method.GET, 
				new GETResource(config.getSettings())
					.setRepository(repository)
					.setUrlPattern(config.getUrl()));
	}

	@Override
	public void consume(HttpContext ctx) {
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
	}

}
