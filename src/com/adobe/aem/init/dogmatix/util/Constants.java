package com.adobe.aem.init.dogmatix.util;

public interface Constants {

	String NEW_LINE = System.lineSeparator();

	String SERVER_HTTP_VERSION = "HTTP/1.1";
	
	int MAX_HEADER_FIELD_NAME_LENGTH = 500;

	int MAX_HEADER_FIELD_VALUE_LENGTH = 500;


	interface HEADERS {
		
		String CONTENT_TYPE = "Content-Type";
		
		String ACCEPT = "Accept";
		
		String CONTENT_LENGTH = "Content-Length";
		
		String CONNECTION = "Connection";
		
		String CONTENT_DISPOSITION = "Content-Disposition";
	}
}
