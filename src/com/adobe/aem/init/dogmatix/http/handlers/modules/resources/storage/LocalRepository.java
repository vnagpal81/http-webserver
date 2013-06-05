package com.adobe.aem.init.dogmatix.http.handlers.modules.resources.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

public class LocalRepository implements Repository {

	private String baseDir;

	public LocalRepository(String baseDir) {
		this.baseDir = baseDir;
	}

	@Override
	public FileInputStream lookup(String uri) throws IOException {
		FileInputStream fis = null;

		try {
			new FileInputStream(baseDir+uri);
		} catch (Exception e) {
			// log
		}

		return fis;
	}

	@Override
	public void create(File file) throws IOException {
		
	}

	@Override
	public boolean exists(String uri) throws IOException {
		return false;
	}

	@Override
	public void delete(String uri) throws IOException {
		
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
		return metadata;
	}

}
