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
 * Represents an HTTP Error response. Thrown at any point while serving the
 * request. Usually when an HTTPError is thrown it signals to the server to stop
 * processing that request any further and send the response back. Comprises of
 * a HTTP Response status code and any extra informational message to be
 * returned to the user-agent.
 * 
 * @author vnagpal
 * 
 */
@SuppressWarnings("serial")
public class HttpError extends Exception {

	private int status;
	private String message;

	public HttpError() {
	}

	public HttpError(int status) {
		this.status = status;
	}

	public HttpError(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
