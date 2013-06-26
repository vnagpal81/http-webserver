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

package com.adobe.aem.init.dogmatix.exceptions;

/**
 * Thrown if the server detects a malformed header in the request.
 * 
 * Validation rules are header specific, although, some rules apply to all
 * headers like, max header name length and max header value length.
 * 
 * These are, according to HTTP specs, server dependent.
 * 
 * @author vnagpal
 *
 */
@SuppressWarnings("serial")
public class InvalidHeaderException extends Exception {
	
	private String headerName;
	private String headerValue;

	public InvalidHeaderException(String headerName, String headerValue, String message) {
		super(message);
		this.headerName = headerName;
		this.headerValue = headerValue;
	}

	public InvalidHeaderException() {}

	public String getHeaderName() {
		return headerName;
	}

	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}

	public String getHeaderValue() {
		return headerValue;
	}

	public void setHeaderValue(String headerValue) {
		this.headerValue = headerValue;
	}
	
}