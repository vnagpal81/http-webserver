package com.adobe.aem.init.dogmatix.http.handlers.modules.resources;

import java.io.FileInputStream;
import java.io.IOException;

import com.adobe.aem.init.dogmatix.exceptions.HttpError;
import com.adobe.aem.init.dogmatix.http.handlers.modules.resources.storage.Metadata;
import com.adobe.aem.init.dogmatix.http.handlers.modules.resources.storage.RepositoryNotAccessibleException;
import com.adobe.aem.init.dogmatix.http.request.HttpRequest;
import com.adobe.aem.init.dogmatix.http.response.HttpResponse;
import com.adobe.aem.init.dogmatix.util.Constants;

public class GETResource extends ResourcesRequestProcessor {

	@Override
	public void processRequest(HttpRequest request, HttpResponse response) throws HttpError {
		String uri = request.getURI();
		try {
		
			if(!repository.exists(uri)) {
				throw new HttpError(404, String.format("(%s)", uri));
			}
			Metadata info = repository.getInfo(uri);
			String accept = request.getHeader(Constants.HEADERS.ACCEPT);
			if(accept != null) {
				//find out acceptable types
				//try to match metadata.getType
				//if not found throw 406
				//TODO
			}
			
			FileInputStream fis = repository.lookup(uri);
			// Write file to response stream
			response.append(fis);

		} catch (RepositoryNotAccessibleException e) {
			throw new HttpError(404, String.format("(Could not connect to repository)"));
		} catch (IOException e) {
			throw new HttpError(404, String.format("(%s)", uri));
		}
	}

}
