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

package com.adobe.aem.init.dogmatix.http.response;

import static com.adobe.aem.init.dogmatix.util.Constants.NEW_LINE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

import net.sf.jmimemagic.Magic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.config.ServerConfig;
import com.adobe.aem.init.dogmatix.exceptions.HttpError;
import com.adobe.aem.init.dogmatix.util.Constants;

/**
 * Represents a HTTP response.
 * 
 * Structure of a request as defined in the rfc is as follows:
 * 
 * ( initial line ) HTTP/Version StatusCode StatusMsg
 * Header1: value1
 * Header2: value2
 * Header3: value3
 * 
 * ( optional message body goes here, like file contents or query data; 
 * it can be many lines long, or even binary data $&*%@!^$@ )
 * 
 * 
 * @author vnagpal
 *
 */
public class HttpResponse {

	private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
	
	private int status = 200;
	private Hashtable<String, String> headers = new Hashtable<String, String>();
	private ByteArrayOutputStream body = new ByteArrayOutputStream();

	private OutputStream outputStream;
	
	private boolean flushed = false;

	/**
	 * Force construction of HTTPResponse with an underlying OutputStream This
	 * will ensure that the stream is never un-initialized
	 * 
	 * @param out
	 */
	public HttpResponse(OutputStream out) {
		assert (out != null);
		this.outputStream = out;
	}

	public Hashtable<String, String> getHeaders() {
		return headers;
	}

	public HttpResponse headers(Hashtable<String, String> headers) {
		this.headers.putAll(headers);
		return this;
	}

	public HttpResponse append(Object o) {
		body.write(o.toString().getBytes(), 0, o.toString().length());
		return this;
	}

	public HttpResponse addHeader(String name, String value) {
		headers.put(name, value);
		return this;
	}

	public HttpResponse write(byte[] chunk) {
		body.write(chunk, 0, chunk.length);
		return this;
	}

	public HttpResponse status(int code) {
		status = code;
		return this;
	}

	public HttpResponse append(InputStream in) {
		byte[] buffer = new byte[8192];
	    int bytesRead = -1;
	    try {
			while ((bytesRead = in.read(buffer)) != -1) {
				body.write(buffer, 0, bytesRead);
			}
		} catch (IOException e) {
			//log
		}
		
		return this;
	}

	public HttpResponse err(HttpError error) {
		HttpResponse response = status(error.getStatus());
		if(error.getMessage() != null) {
			response = response.append(error.getMessage());
		}
		return response;
	}
	
	public HttpResponse err(int code) {
		return err(new HttpError(code));
	}
	
	/**
	 * Gets the String representation of the response.
	 * Includes only the status line and the headers that follow.
	 */
	public String toString() {
		StringBuffer stringResponse = new StringBuffer();
		String statusLine = "HTTP/" + ServerConfig.getInstance().httpVersion()
				+ " " + status + " " + Status.HttpReplies.get(status);
		
		stringResponse.append(statusLine);
		stringResponse.append(NEW_LINE);

		if (!headers.containsKey(Constants.HEADERS.CONTENT_LENGTH)) {
			headers.put(Constants.HEADERS.CONTENT_LENGTH,
					String.valueOf(body.size()));
		}

		if (body.size()>0 && !headers.containsKey(Constants.HEADERS.CONTENT_TYPE)) {
			try {
				headers.put(Constants.HEADERS.CONTENT_TYPE, Magic
						.getMagicMatch(body.toString().getBytes())
						.getMimeType());
			} catch (Exception e) {
				logger.error("Error while determining content type");
			}
		}

		for (String name : headers.keySet()) {
			String headerLine = name + ": " + headers.get(name);
			stringResponse.append(headerLine);
			stringResponse.append(NEW_LINE);
		}

		stringResponse.append(NEW_LINE);
		
		return stringResponse.toString();
	}

	/**
	 * Gets the bytes representing the response 
	 * 
	 * @return
	 * @throws IOException
	 */
	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream finalResponse = new ByteArrayOutputStream();
		
		byte[] initial = toString().getBytes();
		
		finalResponse.write(initial, 0, initial.length);

		if(body.size()>0) {
			finalResponse.write(body.toByteArray(), 0, body.size());
		}
		
		return finalResponse.toByteArray();
	}

	/**
	 * Writes the response to the socket output stream
	 * 
	 * @throws IOException
	 */
	public void flush() throws IOException {
		if(flushed) {
			return;
		}
		try {
			outputStream.write(getBytes());
		}
		catch(Exception e) {
			logger.error("Error while flushing request", e);
			throw e;
		}
		finally {
			flushed = true;
		}
		logger.debug("Response sent back");
		logger.debug(toString());
		logger.debug("<Response body>");
	}

	public int getStatus() {
		return status;
	}
}
