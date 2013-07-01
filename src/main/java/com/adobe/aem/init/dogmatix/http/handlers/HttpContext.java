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

package com.adobe.aem.init.dogmatix.http.handlers;

import java.util.HashMap;

import com.adobe.aem.init.dogmatix.core.ReusableSocket;
import com.adobe.aem.init.dogmatix.http.request.HttpRequest;
import com.adobe.aem.init.dogmatix.http.response.HttpResponse;

/**
 * A context map representing the HTTP state within the server at any time.
 * This is passed into a server module to be consumed and may also be used
 * to relay any information back to the {@link HTTPRequestHandler} including 
 * any post-processing instructions.
 * 
 * Has convenience methods to get/set {@link HttpRequest}, {@link HttpResponse} and {@link ReusableSocket}
 * 
 * @author vnagpal
 *
 */
@SuppressWarnings("serial")
public class HttpContext extends HashMap<String, Object> {
	
	public static final String SOCKET_HANDLE = "SOCKET_HANDLE";
	public static final String HTTP_REQUEST = "HTTP_REQUEST";
	public static final String HTTP_RESPONSE = "HTTP_RESPONSE";
	
	public HttpRequest getRequest() {
		if(!containsKey(HTTP_REQUEST)) {
			return null;
		}
		return (HttpRequest) get(HTTP_REQUEST);
	}
	public void setRequest(HttpRequest request) {
		put(HTTP_REQUEST,  request);
	}
	public HttpResponse getResponse() {
		if(!containsKey(HTTP_RESPONSE)) {
			return null;
		}
		return (HttpResponse) get(HTTP_RESPONSE);
	}
	public void setResponse(HttpResponse response) {
		put(HTTP_RESPONSE, response);
	}
	public ReusableSocket getSocket() {
		if(!containsKey(SOCKET_HANDLE)) {
			return null;
		}
		return (ReusableSocket) get(SOCKET_HANDLE);
	}
	public void setSocket(ReusableSocket socket) {
		put(SOCKET_HANDLE, socket);
	}
	
	public HttpContext(HttpRequest request, HttpResponse response) {
		super();
		setRequest(request);
		setResponse(response);
	}

	public HttpContext() {}
}
