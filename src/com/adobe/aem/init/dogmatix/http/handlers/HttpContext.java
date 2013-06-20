package com.adobe.aem.init.dogmatix.http.handlers;

import java.util.HashMap;

import com.adobe.aem.init.dogmatix.core.ReusableSocket;
import com.adobe.aem.init.dogmatix.http.request.HttpRequest;
import com.adobe.aem.init.dogmatix.http.response.HttpResponse;

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
