package com.adobe.aem.init.dogmatix.http.handlers.modules.resources;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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

/**
 * A resource request processor which processes GET requests for a 
 * resources module.
 * 
 * Delegates the actual CRUD operations to a repository as configured in
 * the module level settings.
 * 
 * A URL is as follows:
 * 
 * http://www.example.com:80/assets/menu/submenu/images/1.png?type=icon
 *   ^           ^        ^  |____________^____________| ^      ^
 *   |           |        |               |              |      |
 * protocol   hostname   port            path           file   query
 * 
 * Depending on user agents, the GET request URI will contain the whole URL
 * or just the path+file+query.
 * 
 * In case of latter, the former part of the URL has to be extracted from the 
 * request headers. But this is only needed in case of a redirect.
 * 
 * If serving the GET request, we need to follow the logic below:
 * 
 * 1) Separate Path, File and Query from the request URI using String split
 * 2) From Path, extract the URL pattern which is used to map the Module 
 * 	  serving the request. The pattern is only a mapping key and is not part 
 * 	  of the actual file path
 * 3) Construct the actual file path from the extracted path above + filename 
 */
public class GETResource extends ResourcesRequestProcessor {

	Logger logger = LoggerFactory.getLogger(GETResource.class);

	private Properties settings;

	/**
	 * Constructs a Resource Request Processor for GET requests
	 * using the module level configuration settings.
	 * 
	 * @param settings
	 */
	public GETResource(Properties settings) {
		super();
		this.settings = settings;
	}

	/**
	 * Processes the GET request.
	 * Looks for the URI in the repository.
	 * 
	 * If the URI points to a directory, either
	 * (a) redirects to the defaultDirPath within that directory OR
	 * (b) fetches the defaultDirPath within that directory
	 * 
	 * If the URI is not existing in the repository, either
	 * (a) redirects to the notFound file path
	 * (b) fetches the notFound file path
	 * 
	 * If the repository is not connecting, raises a 404
	 * 
	 * @param request HTTPRequest object to read from
	 * @param response HTTPResponse object to write to
	 * @throws HttpError 40 Not Found if repo is not accessible OR if resource not found
	 */
	@Override
	public void processRequest(HttpRequest request, HttpResponse response)
			throws HttpError {
		
		
		logger.debug("Processing GET Resource request");
		String uri = new String(request.getURI());
		try {
			logger.debug("GET URI : {}", uri);

			String actualUri = Matcher.extract(uri, urlPattern);
			String contextPath = uri;
			if(uri.length() > actualUri.length()) {
				contextPath = uri.substring(0, uri.length()-actualUri.length());
			}

			logger.debug("GET URI after removing context path : {}", actualUri);
			logger.debug("Context Path : {}", contextPath);

			if (!repository.exists(actualUri)) {
				logger.error("URI {} is not found in repository", actualUri);
				String notFound = settings.getProperty(ResourcesModule.SETTINGS.NOT_FOUND);
				
				if (notFound != null) {
					// Either redirect or GET notFound resource instead
					if(Boolean.parseBoolean(settings.getProperty(ResourcesModule.SETTINGS.REDIRECT_TO_NOT_FOUND))) {
						redirectTo(request, response, contextPath+notFound);
					}
					else {
						try {
							write(response, notFound);
						}
						catch(IOException e) {
							throw new HttpError(404, String.format("(%s) Not found", notFound));
						}
					}
				} else {
					throw new HttpError(404, String.format("(%s) Not found", actualUri));
				}
				
			}
			else {
				Metadata info = repository.getInfo(actualUri);
				if (!info.isLeafNode()) {
					
					logger.debug("We are dealing with a directory here");
					String defaultDirPath = settings.getProperty(ResourcesModule.SETTINGS.DEFAULT_DIR_PATH);
					
					if (defaultDirPath != null) {
					
						// Either redirect or GET defaultDirPath resource instead
						if(Boolean.parseBoolean(settings.getProperty(ResourcesModule.SETTINGS.REDIRECT_TO_NOT_FOUND))) {
							String newLocation = request.getURI() + "/" + defaultDirPath;
							redirectTo(request, response, newLocation);
						}
						else {
							try {
								write(response, actualUri + "/" + defaultDirPath);
							}
							catch(IOException e) {
								throw new HttpError(404, String.format("(%s) Not found", actualUri + "/" + defaultDirPath));
							}
						}
					}
					else {
						throw new HttpError(404, String.format("(%s) Not found", actualUri));
					}
				} else {
					write(response, actualUri);

					//send as attachment if query param for download is true
					if (Boolean.parseBoolean(request.getParam(settings
							.getProperty(ResourcesModule.SETTINGS.DOWNLOAD_PARAM)))) {
						response.addHeader(Constants.HEADERS.CONTENT_DISPOSITION,
								"attachment; filename=\"" + info.getName() + "\"");
					}
				}
			}
		} catch (RepositoryNotAccessibleException e) {
			throw new HttpError(404,
					String.format("(Could not connect to repository)"));
		} catch (IOException e) {
			logger.error("Error while GET resource " + uri, e);
			throw new HttpError(404, String.format("(%s) Not found", uri));
		}
	}
	
	/**
	 * Utility method to fetch a file from the repo and write it to the response.
	 * 
	 * @param response HttpResponse object to write to
	 * @param file Full path of the file to look for in the repo
	 * @throws IOException Raised when any error encountered while accessing the repo
	 * OR looking up the file
	 */
	private void write(HttpResponse response, String file) throws IOException {
		Metadata info = repository.getInfo(file);
		FileInputStream fis = repository.lookup(file);

		// Write file to response stream
		response.append(fis);
		fis.close();
		if(info.getType() != null) {
			response.addHeader(Constants.HEADERS.CONTENT_TYPE, info.getType());	
		}
	}
	
	/**
	 * Private utility method to send a 301 redirect back to the user agent.
	 * Sends the appropriate headers in the response to accomplish that.
	 * 
	 * @param request HttpRequest object to read from
	 * @param response HttpResponse object to write to
	 * @param relativeLocation New location to redirect to relative to the current
	 * request URI.
	 * @throws HttpError throws a 500 Internal Server error if unable to construct
	 * a redirect construct.
	 */
	private void redirectTo(HttpRequest request, HttpResponse response, String relativeLocation) throws HttpError {
		String host = request.getHeader(Constants.HEADERS.HOST);
		String newLocation = "";
		try {
			new URL(host);
			newLocation = host + "/" + relativeLocation;
		} catch (MalformedURLException e) {
			host = "http://" + host;
			try {
				new URL(host);
				newLocation = host + "/" + relativeLocation;
			} catch (MalformedURLException ex) {
				newLocation = "http://" + relativeLocation;
			}
		}
		
		try {
			new URL(newLocation);
		} catch(MalformedURLException e) {
			logger.error("Unable to redirect. Host unavailable ro construct redirect URL.");
			throw new HttpError(500, "Unable to redirect. Host unavailable ro construct redirect URL.");
		}
		
		logger.debug("Redirecting to {}", newLocation);
		response.status(301)
		.addHeader(Constants.HEADERS.LOCATION, newLocation)
		.addHeader(Constants.HEADERS.CONTENT_TYPE, "text/html")
		.append(String.format(Constants.REDIRECT_HTML, newLocation, newLocation));
	}

}
