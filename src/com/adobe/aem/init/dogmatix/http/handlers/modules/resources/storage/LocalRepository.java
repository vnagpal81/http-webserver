package com.adobe.aem.init.dogmatix.http.handlers.modules.resources.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * A file repository implementation with an underlying storage mechanism on the local server disk.
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
		
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(path);
		} catch (IOException e) {
			logger.error("Error while looking up {} : {}", path, e.getMessage());
			throw e;
		}

		return fis;
	}

	@Override
	public void create(File file) throws IOException {
		throw new UnsupportedOperationException();
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
				metadata.setType(Magic.getMagicMatch(f, false).getMimeType());
			} catch (MagicParseException | MagicMatchNotFoundException
					| MagicException e) {
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
