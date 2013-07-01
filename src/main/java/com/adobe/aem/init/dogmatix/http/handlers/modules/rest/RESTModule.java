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

/**
 * A module which acts like a front controller and delegates the actual
 * request handling to an appropriate {@link Handler} determined by matching
 * the request URI against a url mapping specified via the module settings.
 * 
 * Once a {@link Handler} is determined, the request method determines which
 * method of the class is to be invoked and that is done reflectively.
 * 
 * This module offers an extension point where users can plugin their REST
 * handlers. This is modeled on the SpringMVC framework.
 * 
 * @author vnagpal
 *
 */
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
	
	/**
	 * Consumes the HTTP request.
	 * 
	 * (1) Gets a handler class based on url mapping.
	 * (2) Determines the handler class method based on HTTP request method.
	 * (3) Invokes the method reflectively.
	 * (4) If no method found, invokes the default method handle()
	 * (5) If no handler found, raise a 404.
	 */
	@Override
	public boolean consume(HttpContext ctx) {
		try {
			HttpRequest request = ctx.getRequest();
			HttpResponse response = ctx.getResponse();

			// Deduce HTTP Method
			Method httpMethod = request.getMethod();

			// Deduce Handler from URLMapping
			Handler handler = getHandler(request.getURI());

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
		return false;
	}

	/**
	 * Determines a handler to handle a HTTP REST request using the URL mapping rules
	 * 
	 * @param uri the request URI to match against
	 * @return the Handler class instance if found, else null
	 */
	protected Handler getHandler(String uri) {
		for (String pattern : urlMapping.keySet()) {
			boolean matchFound = Matcher.matches(uri, pattern);
			if (matchFound) {
				return urlMapping.get(pattern);
			}
		}
		return null;
	}

}
