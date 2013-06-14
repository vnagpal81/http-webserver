package com.adobe.aem.init.dogmatix.http.handlers;

import java.util.HashMap;

import com.adobe.aem.init.dogmatix.http.request.HttpRequest;
import com.adobe.aem.init.dogmatix.http.response.HttpResponse;

@SuppressWarnings("serial")
public class HttpContext extends HashMap<String, Object> {
	
	private HttpRequest request;
	private HttpResponse response;
	
	public HttpRequest getRequest() {
		return request;
	}
	public void setRequest(HttpRequest request) {
		this.request = request;
	}
	public HttpResponse getResponse() {
		return response;
	}
	public void setResponse(HttpResponse response) {
		this.response = response;
	}
	
	public HttpContext(HttpRequest request, HttpResponse response) {
		super();
		this.request = request;
		this.response = response;
	}

	public HttpContext() {}
}
