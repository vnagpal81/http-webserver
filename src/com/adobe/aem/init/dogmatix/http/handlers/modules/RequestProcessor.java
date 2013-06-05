package com.adobe.aem.init.dogmatix.http.handlers.modules;

import com.adobe.aem.init.dogmatix.exceptions.HttpError;
import com.adobe.aem.init.dogmatix.http.request.HttpRequest;
import com.adobe.aem.init.dogmatix.http.response.HttpResponse;

public interface RequestProcessor {
	
	void processRequest(HttpRequest request, HttpResponse response) throws HttpError;

}
