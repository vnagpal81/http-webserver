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

package com.adobe.aem.init.dogmatix.util;

import java.util.Arrays;
import java.util.List;

/**
 * Globally accessible Application-level constants
 * 
 * @author vnagpal
 *
 */
public interface Constants {

	String NEW_LINE = System.lineSeparator();

	String SERVER_HTTP_VERSION = "HTTP/1.1";
	
	int MAX_HEADER_FIELD_NAME_LENGTH = 500;

	int MAX_HEADER_FIELD_VALUE_LENGTH = 500;

	List<String> SUPPORTED_OPTIONS = Arrays.asList("-a", "--action", "-l", "--level", "-f", "--file", "-h", "--help", "-v", "--version");

	String VERSION = "1.0";


	interface HEADERS {
		
		String CONTENT_TYPE = "Content-Type";
		
		String ACCEPT = "Accept";
		
		String CONTENT_LENGTH = "Content-Length";
		
		String CONNECTION = "Connection";
		
		String CONTENT_DISPOSITION = "Content-Disposition";

		String KEEP_ALIVE = "Keep-Alive";

		String LOCATION = "Location";

		String HOST = "Host";
	}
	
	String REDIRECT_HTML = "<html><head><title>Moved</title></head><body><h1>Moved</h1><p>This page has moved to <a href=\"%s\">%s</a>.</p></body></html>";
	
}
