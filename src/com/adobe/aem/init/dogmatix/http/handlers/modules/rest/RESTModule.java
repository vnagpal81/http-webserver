package com.adobe.aem.init.dogmatix.http.handlers.modules.rest;

import java.util.Map;

import com.adobe.aem.init.dogmatix.exceptions.HttpError;
import com.adobe.aem.init.dogmatix.http.handlers.HttpContext;
import com.adobe.aem.init.dogmatix.http.handlers.modules.AbstractHttpRequestHandlerModule;
import com.adobe.aem.init.dogmatix.http.handlers.modules.Module;
import com.adobe.aem.init.dogmatix.http.request.HttpRequest;
import com.adobe.aem.init.dogmatix.http.request.Method;
import com.adobe.aem.init.dogmatix.http.response.HttpResponse;
import com.adobe.aem.init.dogmatix.util.Matcher;
import com.adobe.aem.init.dogmatix.util.ReflectionUtils;

@Module(url="rest/*")
public class RESTModule extends AbstractHttpRequestHandlerModule {

	private Map<String, Handler> urlMapping;

	public RESTModule() {
		try {
			init();
		} catch (Exception e) {
		}
		//load url mapping from module config
	}
	
	@Override
	public void consume(HttpContext ctx) {
		try {
			HttpRequest request = ctx.getRequest();
			HttpResponse response = ctx.getResponse();

			// Deduce HTTP Method
			Method httpMethod = request.getMethod();

			// Deduce Handler from URLMapping
			Handler handler = getHandler(request, response);

			if (handler != null) {
				// Invoke handler.method() if found, else invoke handler.handle()
				try {
					java.lang.reflect.Method target = ReflectionUtils
							.getMethodWithExactName(handler.getClass(),
									request.getMethod().name());
					try {
						target.invoke(handler, request, response);
					} catch (Exception e) {
						// raise 500 Internal Server Error
						throw new HttpError(500);
					}
				} catch (NoSuchMethodException e) {
					handler.handle(request, response);
				}
			} else {
				// raise 404
				throw new HttpError(404);
			}			
		}
		catch(HttpError e) {
			
		}
	}

	protected Handler getHandler(HttpRequest request, HttpResponse response) {
		for (String pattern : urlMapping.keySet()) {
			boolean matchFound = Matcher.matches(request.getURI(), pattern);
			if (matchFound) {
				return urlMapping.get(pattern);
			}
		}
		return null;
	}

}
