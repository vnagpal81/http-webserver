package com.adobe.aem.init.dogmatix.http.handlers.modules.rest;

import com.adobe.aem.init.dogmatix.exceptions.HttpError;
import com.adobe.aem.init.dogmatix.http.request.HttpRequest;
import com.adobe.aem.init.dogmatix.http.response.HttpResponse;

public interface Handler {

	void handle(HttpRequest request, HttpResponse response) throws HttpError;
	
	void GET(HttpRequest request, HttpResponse response) throws HttpError;
	
	void PUT(HttpRequest request, HttpResponse response) throws HttpError;
	
	void POST(HttpRequest request, HttpResponse response) throws HttpError;
	
	void DELETE(HttpRequest request, HttpResponse response) throws HttpError;
	
	void TRACE(HttpRequest request, HttpResponse response) throws HttpError;
	
	void OPTIONS(HttpRequest request, HttpResponse response) throws HttpError;
	
	void CONNECT(HttpRequest request, HttpResponse response) throws HttpError;
	
	void HEAD(HttpRequest request, HttpResponse response) throws HttpError;
}
