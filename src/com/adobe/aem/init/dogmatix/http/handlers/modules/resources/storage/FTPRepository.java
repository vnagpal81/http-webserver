package com.adobe.aem.init.dogmatix.http.handlers.modules.resources.storage;

import java.io.FileInputStream;
import java.io.IOException;

import com.adobe.aem.init.dogmatix.http.request.FileUpload;

public class FTPRepository implements Repository {

	@Override
	public FileInputStream lookup(String uri) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void create(FileUpload fileUpload) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean exists(String uri) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void delete(String uri) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Metadata getInfo(String uri) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileInputStream lookin(String parentPath, String fileName)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
