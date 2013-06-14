package com.adobe.aem.init.dogmatix.http.handlers.modules.resources;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.exceptions.HttpError;
import com.adobe.aem.init.dogmatix.http.handlers.modules.resources.storage.Metadata;
import com.adobe.aem.init.dogmatix.http.handlers.modules.resources.storage.RepositoryNotAccessibleException;
import com.adobe.aem.init.dogmatix.http.request.HttpRequest;
import com.adobe.aem.init.dogmatix.http.response.HttpResponse;
import com.adobe.aem.init.dogmatix.util.Constants;
import com.adobe.aem.init.dogmatix.util.Matcher;

public class GETResource extends ResourcesRequestProcessor {

	Logger logger = LoggerFactory.getLogger(GETResource.class);

	private Properties settings;

	public GETResource(Properties settings) {
		super();
		this.settings = settings;
	}

	@Override
	public void processRequest(HttpRequest request, HttpResponse response)
			throws HttpError {
		logger.debug("Processing GET Resource request");
		String uri = request.getURI();
		try {
			logger.debug("GET URI : {}", uri);

			uri = Matcher.extract(uri, urlPattern);

			logger.debug("GET URI after removing context path : {}", uri);

			if (!repository.exists(uri)) {
				logger.error("URI {} is not found in repository", uri);
				if (settings.containsKey(ResourcesModule.SETTINGS.NOT_FOUND)) {
					// Either redirect or GET notFound resource instead
				} else {
					throw new HttpError(404, String.format("(%s)", uri));
				}
			}
			Metadata info = repository.getInfo(uri);
			if (!info.isLeafNode()) {
				logger.debug("We are dealing with a directory here");
				if (settings
						.containsKey(ResourcesModule.SETTINGS.DEFAULT_DIR_PATH)) {
					// Either redirect or GET defaultDirPath resource instead
				}
			} else {
				FileInputStream fis = repository.lookup(uri);

				// Write file to response stream
				response.append(fis);
				fis.close();

				if (Boolean.parseBoolean(request.getParam(settings
						.getProperty(ResourcesModule.SETTINGS.DOWNLOAD_PARAM)))) {
					response.addHeader(Constants.HEADERS.CONTENT_DISPOSITION,
							"attachment; filename=\"" + info.getName() + "\"");
				}

				response.addHeader(Constants.HEADERS.CONTENT_TYPE,
						info.getType());
			}
		} catch (RepositoryNotAccessibleException e) {
			throw new HttpError(404,
					String.format("(Could not connect to repository)"));
		} catch (IOException e) {
			logger.error("Error while GET resource " + uri, e);
			throw new HttpError(404, String.format("(%s)", uri));
		}
	}

}
