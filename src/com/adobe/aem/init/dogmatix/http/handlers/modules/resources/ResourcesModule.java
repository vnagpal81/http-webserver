package com.adobe.aem.init.dogmatix.http.handlers.modules.resources;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.adobe.aem.init.dogmatix.exceptions.HttpError;
import com.adobe.aem.init.dogmatix.http.handlers.modules.AbstractHttpRequestHandlerModule;
import com.adobe.aem.init.dogmatix.http.handlers.modules.Module;
import com.adobe.aem.init.dogmatix.http.handlers.modules.resources.storage.Repository;
import com.adobe.aem.init.dogmatix.http.request.HttpRequest;
import com.adobe.aem.init.dogmatix.http.request.Method;
import com.adobe.aem.init.dogmatix.http.response.HttpResponse;

@Module
public class ResourcesModule extends AbstractHttpRequestHandlerModule {

	private Map<Method, ResourcesRequestProcessor> methodResolver;
	
	protected void init() throws Exception {
		super.init();

		//populate repository from module config
		Repository repository = null;
		
		methodResolver = new HashMap<Method, ResourcesRequestProcessor>();
		methodResolver.put(Method.GET, new GETResource().setRepository(repository));
	}

	@Override
	public void consume(InputStream in, OutputStream out) {
		HttpResponse response = new HttpResponse();
		
		try {
			HttpRequest request = parseRequest(in);
			
			// get request processor
			ResourcesRequestProcessor processor = methodResolver.get(request.getMethod());
			
			if (processor != null) {
				
				processor.processRequest(request, response);
				
			} else {
				// raise 404
				response.err(new HttpError(404));
			}			
		}
		catch(HttpError e) {
			response.err(e);
		}
		finally {
			sendResponse(out, response);
		}
	}

}
