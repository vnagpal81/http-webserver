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

package com.adobe.aem.init.dogmatix.http.request;

import java.util.Hashtable;

/**
 * Represents an HTTP Request.
 * 
 * Structure of a request as defined in the rfc is as follows:
 * 
 * ( initial line ) Method URI HTTP/Version
 * Header1: value1
 * Header2: value2
 * Header3: value3
 * 
 * ( optional message body goes here, like file contents or query data; 
 * it can be many lines long, or even binary data $&*%@!^$@ )
 * 
 * @author vnagpal
 *
 */
public class HttpRequest {
	
	private Hashtable<String, String> headers;
	private Hashtable<String, String> params;
	private byte[] entity;

	private byte[] raw;
	private long length;
	
	private Method method;
	private String URI;
	private String protocol;
	
	public String getHeader(String name) {
		if(headers != null) {
			return headers.get(name.toLowerCase());
		}
		return null;
	}
	public Hashtable<String, String> getHeaders() {
		return headers;
	}
	public void setHeaders(Hashtable<String, String> headers) {
		this.headers = headers;
	}
	
	public Hashtable<String, String> getParams() {
		return params;
	}
	public void setParams(Hashtable<String, String> params) {
		this.params = params;
	}
	public byte[] getEntity() {
		return entity;
	}
	public void setEntity(byte[] entity) {
		this.entity = entity;
	}
	public byte[] getRaw() {
		return raw;
	}
	public void setRaw(byte[] raw) {
		this.raw = raw;
	}
	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public String getURI() {
		return URI;
	}
	public void setURI(String uRI) {
		this.URI = uRI;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getParam(String name) {
		if(params == null) {
			return null;
		}
		return params.get(name.toLowerCase());
	}
}
