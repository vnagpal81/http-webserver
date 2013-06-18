package com.adobe.aem.init.dogmatix.util;

import java.util.Arrays;
import java.util.List;

public interface Constants {

	String NEW_LINE = System.lineSeparator();

	String SERVER_HTTP_VERSION = "HTTP/1.1";
	
	int MAX_HEADER_FIELD_NAME_LENGTH = 500;

	int MAX_HEADER_FIELD_VALUE_LENGTH = 500;

	public static final List<String> SUPPORTED_OPTIONS = Arrays.asList("-a", "--action", "-d", "--debug", "-f", "--file", "-h", "--help", "-v", "--version");

	public static final String VERSION = "1.0";


	interface HEADERS {
		
		String CONTENT_TYPE = "Content-Type";
		
		String ACCEPT = "Accept";
		
		String CONTENT_LENGTH = "Content-Length";
		
		String CONNECTION = "Connection";
		
		String CONTENT_DISPOSITION = "Content-Disposition";

		String KEEP_ALIVE = "Keep-Alive";
	}
}
