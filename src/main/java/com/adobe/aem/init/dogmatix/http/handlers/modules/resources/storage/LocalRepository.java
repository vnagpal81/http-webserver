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

package com.adobe.aem.init.dogmatix.http.handlers.modules.resources.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;

import net.sf.jmimemagic.Magic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.http.request.FileUpload;
import com.adobe.aem.init.dogmatix.http.response.Mime;

/**
 * 
 * A file repository implementation with an underlying storage mechanism on the local server disk.
 * 
 * Parameters required to connect to a Local Repository:
 * Base Directory 	:	The path to a directory on the local disk where files are to be read/written
 * 						May be specified as absolute or relative to server root/lib/
 * 
 * @author vnagpal
 *
 */
public class LocalRepository implements Repository {
	
	private static final Logger logger = LoggerFactory.getLogger(LocalRepository.class);

	private String baseDir;

	/**
	 * Initialize a file repository on the local server disk storage.
	 * Base directory path could be relative to server root as well as absolute.
	 * 
	 * @param baseDir Base directory path
	 * @throws RepositoryNotAccessibleException thrown if path specified is not found or is not a directory
	 */
	public LocalRepository(String baseDir) throws RepositoryNotAccessibleException {
		try {
			//try locating base dir as an absolute path
			File f = new File(baseDir);
			if(!f.isDirectory()) {
				throw new RepositoryNotAccessibleException(String.format("%s is not a directory", baseDir));
			}
			this.baseDir = f.getPath() + File.separator;
			logger.debug("Registered a Local repository at {}", this.baseDir);
		}
		catch(Exception e) {
			try {
				URL dirURL = this.getClass().getResource(baseDir);
				File f = new File(dirURL.toURI());
				if(!f.isDirectory()) {
					throw new RepositoryNotAccessibleException(String.format("%s is not a directory", baseDir));
				}
			}
			catch(Exception ex) {
				logger.error("Failure while registering local repository at {} : {}", baseDir, e.getMessage());
				throw new RepositoryNotAccessibleException(e);
			}
		}
	}

	@Override
	public FileInputStream lookup(String uri) throws IOException {
		String[] paths = uri.split("/");
		String path = baseDir;
		for(String s : paths) {
			path += s + File.separator;
		}
		if(path.endsWith(File.separator)) {
			path = path.substring(0, path.length()-1);
		}
		logger.debug("Looking up {}", path);
		
		try {
			return new FileInputStream(path);
		} catch (IOException e) {
			logger.error("Error while looking up {} : {}", path, e.getMessage());
			throw e;
		}
	}

	@Override
	public void create(FileUpload fileUpload) throws IOException {
		if(!exists(fileUpload.getFileName())) {
			FileInputStream temp = new FileInputStream(fileUpload.getTempFilePath());
			FileOutputStream newFile = new FileOutputStream(baseDir + fileUpload.getFileName());
			
			while(temp.available() > 0) {
				newFile.write(temp.read());
			}
			
			newFile.close();
			temp.close();
		}
		else {
			throw new FileAlreadyExistsException(fileUpload.getFileName());
		}
	}

	@Override
	public boolean exists(String uri) throws IOException {
		File f = new File(baseDir + uri);
		return f.exists();
	}

	@Override
	public void delete(String uri) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Metadata getInfo(String uri) throws IOException {
		if(!exists(uri)) {
			throw new FileNotFoundException();
		}
		File f = new File(baseDir + uri);
		Metadata metadata = new Metadata();
		metadata.setSize(f.length());
		metadata.setCreatedOn(f.lastModified());
		metadata.setLastModifiedOn(f.lastModified());
		if(f.isFile()) {
			try {
				String type = null;
				if(uri.lastIndexOf('.') != -1) {
					String[] parts = uri.split("\\.");
					String ext = parts[parts.length - 1];
					type = Mime.Mapping.get(ext);
				}
				if(type == null) {
					type = Magic.getMagicMatch(f, true).getMimeType();
				}
				
				metadata.setType(type);
			} catch (Exception e) {
				logger.error("Error determining type", e);
			}
		}
			
		metadata.setName(f.getName());
		metadata.setLeafNode(f.isFile());
		return metadata;
	}

	@Override
	public FileInputStream lookin(String parentPath, String fileName)
			throws IOException {
		String[] paths = parentPath.split("/");
		String path = baseDir;
		for(String s : paths) {
			path += s + File.separator;
		}
		path += fileName;
		logger.debug("Looking up {}", path);
		
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(path);
		} catch (Exception e) {
			logger.error("Error while looking up {} : {}", path, e.getMessage());
		}

		return fis;
	}

}
