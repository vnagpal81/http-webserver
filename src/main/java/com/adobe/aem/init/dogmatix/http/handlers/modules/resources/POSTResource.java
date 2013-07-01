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

package com.adobe.aem.init.dogmatix.http.handlers.modules.resources;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.exceptions.HttpError;
import com.adobe.aem.init.dogmatix.http.handlers.modules.resources.storage.RepositoryNotAccessibleException;
import com.adobe.aem.init.dogmatix.http.request.FileUpload;
import com.adobe.aem.init.dogmatix.http.request.HttpRequest;
import com.adobe.aem.init.dogmatix.http.response.HttpResponse;
import com.adobe.aem.init.dogmatix.util.Constants;
import com.adobe.aem.init.dogmatix.util.IOUtils;

/**
 * A resource request processor which processes POST requests for a resources
 * module.
 * 
 * Delegates the actual CRUD operations to a repository as configured in the
 * module level settings.
 * 
 * @author vnagpal
 * 
 */
public class POSTResource extends ResourcesRequestProcessor {

	private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

	private static final int BUFFER_SIZE = 65535;

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

			FileUpload fileUpload = getFileUpload(request);
			String fileName = fileUpload.getFileName();

			if (repository.exists(fileName)) {
				logger.error("File {} already exists in repository", fileName);
				throw new HttpError(400, String.format(
						"File with same name (%s) already exists", fileName));
			}

			long uploadLimit = Long.parseLong(settings
					.getProperty(ResourcesModule.SETTINGS.UPLOAD_LIMIT));
			if (fileUpload.getSize() > uploadLimit) {
				throw new HttpError(413, "(Max Upload Limit is " + uploadLimit + " bytes)");
			}

			repository.create(fileUpload);

			// Write response
			response.status(201).append(fileName + " saved successfully");

		} catch (RepositoryNotAccessibleException e) {
			logger.error("Could not connect to repository");
			throw new HttpError(404, "(Could not connect to repository)");
		} catch (IOException e) {
			logger.error("Error while POSTing resource", e);
			throw new HttpError(500);
		}
	}

	/**
	 * Reads the byte buffer and constructs an Upload object.
	 * Separates out file upload bytes into a temp file to be saved in the repository later.
	 * 
	 * Code borrowed from <a href="http://www.prasannatech.net/2009/03/java-http-post-file-upload-server.html">
	 * http://www.prasannatech.net/2009/03/java-http-post-file-upload-server.html</a>
	 * 
	 * @param request HTTP Request object
	 * @return FileUpload object containing the metadata for the file to be created and path 
	 * of the temp file which contains the actual data
	 * @throws IOException if any error while reading from request or writing to temp file
	 * 		   HttpError if content type is wrongly set
	 */
	private FileUpload getFileUpload(HttpRequest request) throws IOException, HttpError {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(request.getEntity());
		//BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		
		//read boundary from multipart/form-data
		String contentType = request.getHeader(Constants.HEADERS.CONTENT_TYPE);
		logger.debug("{}: {}", Constants.HEADERS.CONTENT_TYPE, contentType);
		if(contentType.indexOf("multipart/form-data") == -1) {
			throw new HttpError(400, "(Content Type should be 'multipart/form-data' for file uploads)");
		}
		if(contentType.indexOf("boundary=") == -1) {
			throw new HttpError(400, "(Content Type should include 'boundary' for file uploads)");
		}
		String boundary = "--" + contentType.split("boundary=")[1];

		//read filename from content-disposition
		String fileName = null;
		while(true) {
			String currentLine = IOUtils.readLine(inputStream);//ignore any encoded form data
			if (currentLine.indexOf(boundary) != -1) {
				currentLine = IOUtils.readLine(inputStream);
				if(currentLine.indexOf(Constants.HEADERS.CONTENT_DISPOSITION) != -1 && currentLine.indexOf("filename=\"") != -1) {
					String fileNameOnClient = currentLine.split("filename=")[1].replaceAll("\"", "");
					String[] filePath = fileNameOnClient.split("\\"	+ File.separator);
					fileName = filePath[filePath.length - 1];
					break;					
				}
			}
		}
		
		if(fileName == null) {
			logger.error("Filename missing in POST request entity");
			throw new HttpError(400, "(filename missing)");
		}
		
		logger.debug("File to be uploaded = {}", fileName);

		//read content type from next line
		String fileContentType = IOUtils.readLine(inputStream).split(" ")[1];
		logger.debug("Uploaded file type {}", fileContentType);

		//skip a line
		IOUtils.readLine(inputStream); 

		String tempFile = getTempFileName();
		logger.debug("Saving the file temporarily at {}", tempFile);
		FileOutputStream tempFileOutputStream = new FileOutputStream(tempFile);
		int fileSize = 0;

		byte[] buffer = new byte[BUFFER_SIZE];
		byte[] extendedArray;
        byte[] endFlag = (boundary + "--").getBytes();
                
        int bytesRead, bytesAvailable;   
		
        try {
        	// Here we upload the actual file contents
			while ((bytesAvailable = inputStream.available()) > 0) {

				bytesRead = inputStream.read(buffer, 0, BUFFER_SIZE);
				extendedArray = buffer;
				int endBytePos = 0;

				// When number of bytes to be read in the stream < BUFFER_SIZE
				if (bytesAvailable < BUFFER_SIZE) {

					// Case where part of POST Boundary comes in the last buffer
					if (bytesAvailable < endFlag.length) {
						extendedArray = new byte[BUFFER_SIZE + bytesAvailable];
						System.arraycopy(buffer, 0, extendedArray, 0, bytesRead);
						bytesRead = inputStream.read(extendedArray,	BUFFER_SIZE, bytesAvailable);
					}

				}

				endBytePos = sub_array(extendedArray, endFlag);

				if (endBytePos == -1) {
					tempFileOutputStream.write(buffer, 0, bytesRead);
					fileSize += bytesRead;
				} else {
					tempFileOutputStream.write(extendedArray, 0, endBytePos - 2);
					fileSize += endBytePos - 2;
				}
			}// while
		}
        finally {
        	tempFileOutputStream.close();
        }
        
		return new FileUpload(fileName, fileContentType, tempFile, fileSize);
	}
	
	/**
	 * Generates a file location where the uploaded file will be temporary stored
	 *  
	 * @return the temp file path
	 */
	private String getTempFileName() {
		return TEMP_DIR + File.separator + Thread.currentThread().getName()
				+ System.currentTimeMillis();
	}

	/**
	 * A routine to find the array2 within array1
	 * 
	 * @param array1 array of bytes to search in
	 * @param array2 array of bytes to search for
	 * @return index of array1 in array2 if found, else -1
	 */
	private int sub_array(byte[] array1, byte[] array2) {
		try {
			int i = array1.length - 1;
			int j = array2.length - 1;
			boolean found = false;

			for (int k = i; k >= 0; k--) {
				if (array1[k] == array2[j]) {
					found = true;
					for (int l = j - 1; l >= 0; l--) {
						k = k - 1;
						if (k < 0)
							return -1;
						if (array1[k] == array2[l])
							continue;
						else {
							found = false;
							break;
						}
					}
					if (found == true)
						return k;
				}
			}
		}
		catch(Exception e) {
			logger.error("Error in sub_array", e);
		}
		
		return -1;
	}

}
