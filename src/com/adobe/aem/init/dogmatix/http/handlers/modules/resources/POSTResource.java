package com.adobe.aem.init.dogmatix.http.handlers.modules.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.exceptions.HttpError;
import com.adobe.aem.init.dogmatix.http.handlers.modules.resources.storage.RepositoryNotAccessibleException;
import com.adobe.aem.init.dogmatix.http.request.HttpRequest;
import com.adobe.aem.init.dogmatix.http.response.HttpResponse;
import com.adobe.aem.init.dogmatix.util.Matcher;

public class POSTResource extends ResourcesRequestProcessor {

	Logger logger = LoggerFactory.getLogger(POSTResource.class);

	private Properties settings;

	public POSTResource(Properties settings) {
		super();
		this.settings = settings;
	}
	
	@Override
	public void processRequest(HttpRequest request, HttpResponse response)
			throws HttpError {

		logger.debug("Processing POST Resource request");
		String uri = request.getURI();
		
		try {
			logger.debug("POST URI : {}", uri);

			uri = Matcher.extract(uri, urlPattern);

			logger.debug("POST URI after removing context path : {}", uri);

			if (repository.exists(uri)) {
				logger.error("File {} already exists in repository", uri);
				throw new HttpError(4, String.format("File with same name (%s) already exists", uri));
			}
			
			long uploadLimit = Long.parseLong(settings.getProperty(ResourcesModule.SETTINGS.UPLOAD_LIMIT, "5000"));
			if(request.getEntity().length > uploadLimit) {
				throw new HttpError();
			}
			
			//create temp file
			FileOutputStream temp = new FileOutputStream(uri);
			temp.write(request.getEntity());
			temp.close();
			
			repository.create(new File(uri));

			// Write response
			response.append(null);

			
		} catch (RepositoryNotAccessibleException e) {
			throw new HttpError(404,
					String.format("(Could not connect to repository)"));
		} catch (IOException e) {
			logger.error("Error while POST resource " + uri, e);
			throw new HttpError(404, String.format("(%s)", uri));
		}
	}

}
