package com.adobe.aem.init.dogmatix.http.handlers.modules.resources.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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

	public LocalRepository(String baseDir) {
		try {
			File f = new File(baseDir);
			if(!f.isDirectory()) {
				throw new IllegalArgumentException();
			}
			this.baseDir = f.getPath() + File.separator;
			logger.debug("Registered a Local repository at {}", this.baseDir);
		}
		catch(Exception e) {
			logger.error("Failure while registering local repository at {} : {}", baseDir, e.getMessage());
			throw new IllegalArgumentException(e);
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
		} catch (Exception e) {
			logger.error("Error while looking up {} : {}", path, e.getMessage());
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
		File f = new File(baseDir + uri);
		Metadata metadata = new Metadata();
		metadata.setSize(f.length());
		metadata.setCreatedOn(f.lastModified());
		metadata.setLastModifiedOn(f.lastModified());
		try {
			metadata.setType(Magic.getMagicMatch(f, false).getMimeType());
		} catch (MagicParseException | MagicMatchNotFoundException
				| MagicException e) {
			//log
		}
		metadata.setName(f.getName());
		metadata.setLeafNode(f.isFile());
		return metadata;
	}

}
