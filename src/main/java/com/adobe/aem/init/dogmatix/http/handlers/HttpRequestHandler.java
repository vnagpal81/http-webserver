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

package com.adobe.aem.init.dogmatix.http.handlers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.config.ModuleConfig;
import com.adobe.aem.init.dogmatix.core.ReusableSocket;
import com.adobe.aem.init.dogmatix.exceptions.ConnectionTimeOut;
import com.adobe.aem.init.dogmatix.exceptions.HttpError;
import com.adobe.aem.init.dogmatix.exceptions.InvalidHeaderException;
import com.adobe.aem.init.dogmatix.http.handlers.modules.AbstractHttpRequestHandlerModule;
import com.adobe.aem.init.dogmatix.http.handlers.modules.ModuleFactory;
import com.adobe.aem.init.dogmatix.http.handlers.modules.URLMapping;
import com.adobe.aem.init.dogmatix.http.header.HeaderInterceptor;
import com.adobe.aem.init.dogmatix.http.header.KeepAlive;
import com.adobe.aem.init.dogmatix.http.request.HttpRequest;
import com.adobe.aem.init.dogmatix.http.request.Method;
import com.adobe.aem.init.dogmatix.http.request.Version;
import com.adobe.aem.init.dogmatix.http.response.HttpResponse;
import com.adobe.aem.init.dogmatix.listeners.HttpListener;
import com.adobe.aem.init.dogmatix.util.Constants;
import com.adobe.aem.init.dogmatix.util.IOUtils;

/**
 * Handles an incoming HTTP Request.
 * Run inside a thread kicked off by the {@link HttpListener}
 * Has access to the {@link ServerSocket} via the {@link ReusableSocket} instance.
 * The handler
 * 
 * (0) Extracts the I/O streams from the {@link ServerSocket}
 * (1) Creates a HTTP context by parsing the input 
 * (2) Invokes the header interceptors for any pre-processing
 * (3) Delegates the request handling to a module determined via URL mapping
 * (4) Invokes the header interceptors for any post-processing
 * (5) Finally, flushes the request which writes the buffer onto the stream
 * (6) Closes the socket
 * 
 * @author vnagpal
 *
 */
public class HttpRequestHandler implements Runnable {

	private static final Logger logger = LoggerFactory
			.getLogger(HttpRequestHandler.class);

	private ReusableSocket socket;

	public HttpRequestHandler(Socket socket) {
		this.socket = new ReusableSocket(socket);
	}

	public void run() {
		do {//continue handling requests on this connection till socket is closed
			try {
				HttpContext ctx = new HttpContext();
				
				OutputStream out = this.socket.getOutputStream();
				InputStream in = this.socket.getInputStream();
				
				//wait till client starts transmitting or connection times out
				try {
					while(in.available() == 0) {
						Thread.sleep(50);
					}
				}
				catch(IOException e) {
					//socket has been closed.. connection timed out
					throw new ConnectionTimeOut();
				}
				
				logger.debug("Incoming Request...");
	
				// Parse Request and create Response
				HttpResponse response = new HttpResponse(out);
				try {
					logger.debug("Trying to parse the request");
					HttpRequest request = parseRequest(in);
	
					ctx.setRequest(request);
					ctx.setResponse(response);
					ctx.setSocket(this.socket);
	
					HeaderInterceptor[] headerInterceptors = {new KeepAlive()};
					boolean processed = false;
	
					// Pre process the request
					for (int i = 0; i < headerInterceptors.length; i++) {
						processed = headerInterceptors[i].preProcess(ctx);
						if (processed) {
							break;
						}
					}
	
					if (!processed) {
						// Determine URL
						String url = request.getURI();
						
						if(url.startsWith("..") || url.startsWith("./")) {
							throw new HttpError(403, "(Don't act smart. Relative paths not allowed)");
						}
	
						// Map from config and get Module
						ModuleConfig config = URLMapping.getModuleConfig(url);
	
						if (config == null) {
							throw new HttpError(404, url);
						}
	
						// Get Module Instance from ModuleFactory
						AbstractHttpRequestHandlerModule module = ModuleFactory
								.getModule(config);
	
						// Delegate Request handling to module.consume() which will
						// also build the Response
						processed = module.consume(ctx);
					}
	
					if (!processed) {
						// Post process the request
						for (int i = 0; i < headerInterceptors.length; i++) {
							processed = headerInterceptors[i].postProcess(ctx);
							if (processed) {
								break;
							}
						}
					}
				} catch (HttpError e) {
					response.err(e);
				} catch(Exception e) {
					logger.error("Internal server error", e);
					response.err(new HttpError(500, e.getMessage()));
			    } finally {
			    	response.flush();
				}
			} catch (ConnectionTimeOut e) {
				logger.info("Connection timed out");
				socket.setPersist(false);
			} catch (Exception e) {
				logger.error("Error while handling HTTP request", e);
				socket.setPersist(false);
			} finally {
				try {
					cleanup();
				} catch (IOException e) {
					logger.error("Unable to close socket connection");
				}
			}
		} while(this.socket.isPersist());
	}

	private void cleanup() throws IOException {
		if(!socket.isPersist()) {
			if(!socket.getSocket().isClosed()) {
				logger.debug("Closing socket");
				this.socket.close();
			}
		}
		else {
			logger.debug("Persisting the socket : Count - {}", this.socket.getCount()+1);
			this.socket.accessed();
			this.socket.getOutputStream().flush();
			try {
				this.socket.getSocket().setKeepAlive(true);
			} catch (SocketException e) {
				logger.error("Error keeping socket alive. Closing now..");
				this.socket.close();
			}
		}
	}

	/**
	 * parses the http request into its various components (method, uri,
	 * version, headers, entity) throws an HttpError in case of any exceptions
	 * encountered while parsing the request
	 * 
	 * @source http://www.java2s.com/Code/Java/Network-Protocol/HttpParser.htm
	 * @param inputStream
	 *            the stream to read from
	 * @return the http request object thus formed
	 * @throws HttpError
	 */
	protected HttpRequest parseRequest(InputStream inputStream)
			throws HttpError {
		
		HttpRequest httpRequest = new HttpRequest();

		ByteArrayOutputStream raw = new ByteArrayOutputStream();
		String firstLine = null;

		try {
			firstLine = IOUtils.readLine(inputStream);
			raw.write((firstLine + Constants.NEW_LINE).getBytes());
		} catch (IOException e) {
			throw new HttpError(400);
		}

		logger.debug("Read first line - {}", firstLine);

		// non-existent request no-op
		if (firstLine == null || firstLine.length() == 0) {
			logger.error("Empty request");
			throw new HttpError(400);
		}

		if (Character.isWhitespace(firstLine.charAt(0))) {
			// starting whitespace, return bad request
			logger.error("Request starts with whitespace");
			throw new HttpError(400, "(First character is space)");
		}

		String[] cmd = firstLine.split("\\s");
		if (cmd.length != 3) {
			logger.error("Request first line is invalid format");
			throw new HttpError(400, "(Invalid Format - " + firstLine + ")");
		}

		if (cmd[2].indexOf("HTTP/") == 0 && cmd[2].indexOf('.') > 5) {
			String ver = cmd[2].substring(5);
			try {
				Float.parseFloat(ver);
				httpRequest.setProtocol(ver);
			} catch (NumberFormatException nfe) {
				logger.error("HTTP version {} is invalid", ver);
				throw new HttpError(400, "(Invalid version - " + ver + ")");
			}
		} else {
			logger.error("HTTP version is missing");
			throw new HttpError(400, "(Version missing)");
		}

		try {
			httpRequest.setMethod(Method.valueOf(cmd[0]));
		} catch (Exception e) {
			logger.error("HTTP method {} is invalid or not yet supported",
					cmd[0]);
			throw new HttpError(400, "(Invalid method - " + cmd[0] + "");
		}

		String uri = cmd[1];

		if (httpRequest.getMethod() == Method.GET
				|| httpRequest.getMethod() == Method.HEAD) {
			logger.debug("Parsing request URI and query params from {}", uri);

			int idx = uri.indexOf('?');
			if (idx < 0) {
				httpRequest.setURI(uri);
			} else {
				try {
					httpRequest.setURI(URLDecoder.decode(uri.substring(0, idx),
							"ISO-8859-1"));

					String[] prms = uri.substring(idx + 1).split("&");

					Hashtable<String, String> params = new Hashtable<String, String>();
					for (int i = 0; i < prms.length; i++) {
						String[] temp = prms[i].split("=");
						if (temp.length == 2) {
							// we use ISO-8859-1 as temporary
							// charset
							// and
							// then
							// String.getBytes("ISO-8859-1") to get
							// the
							// data
							params.put(
									URLDecoder.decode(temp[0], "ISO-8859-1"),
									URLDecoder.decode(temp[1], "ISO-8859-1"));
						} else if (temp.length == 1
								&& prms[i].indexOf('=') == prms[i].length() - 1) {
							// handle empty string separately
							params.put(
									URLDecoder.decode(temp[0], "ISO-8859-1"),
									"");
						}
					}

					httpRequest.setParams(params);

				} catch (UnsupportedEncodingException e) {
					logger.error("Invalid query params encoding in {}", uri);
					throw new HttpError(400, "(Invalid encoding - " + uri + ")");
				}
			}
		} else {
			httpRequest.setURI(uri);
		}

		
		//read headers
		
		logger.debug("Trying to parse Request headers");
		logger.debug("*******************************");
		Hashtable<String, String> headers = new Hashtable<String, String>();
		
		while(true) {
			String line = null;
			try {
				line = IOUtils.readLine(inputStream);
			} catch (IOException e) {
				throw new HttpError(400, "(Invalid request line " + line + ")");
			}
			
			if(line.isEmpty()) {
				break;
			}
			
			logger.debug(line);
			// rfc822 allows multiple lines, we don't care now
			try {
				int idx = line.indexOf(':');
				if (idx < 0) {
					break;
				} else {
					String headerName = line.substring(0, idx).toLowerCase();
					String headerValue = line.substring(idx + 1).trim();

					if (headerName.length() > Constants.MAX_HEADER_FIELD_NAME_LENGTH) {
						throw new InvalidHeaderException(headerName,
								headerValue, "Name too long");
					}
					if (headerValue.length() > Constants.MAX_HEADER_FIELD_VALUE_LENGTH) {
						throw new InvalidHeaderException(headerName,
								headerValue, "Value too long");
					}

					headers.put(headerName, headerValue);
				}
			} catch (InvalidHeaderException e) {
				throw new HttpError(400, "(Invalid Header - " + e.getMessage()
					+ ")");
			} catch (Exception e) {
				throw new HttpError(400, "(Invalid Header - " + line + ")");
			}
		}
		
		httpRequest.setHeaders(headers);
		logger.debug("*******************************");

		
		//read post data
		
		if (httpRequest.getMethod() == Method.POST) {
			logger.debug("Trying to read the entity in the request");

			try {
				int contentLength = -1;
				contentLength = Integer.parseInt(httpRequest.getHeader(Constants.HEADERS.CONTENT_LENGTH));
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				for(int i=0; i< contentLength; i++) {
					int b = inputStream.read();
					if(b == -1) {
						break;
					}
					baos.write(b);
				}
				
				httpRequest.setEntity(baos.toByteArray());
				logger.debug("Entity in the request - {} bytes", contentLength);
				raw.write(httpRequest.getEntity(), 0, contentLength);
			} catch (IOException e) {
				logger.error("Error reading entity in HTTP request", e);
				throw new HttpError(400, "(Invalid entity)");
			} catch (NumberFormatException e) {
				logger.error("Error reading content length", e);
				throw new HttpError(400, "(Missing Content Length)");
			}
		}

		httpRequest.setRaw(raw.toByteArray());

		return httpRequest;
	}

	/**
	 * Validates the HTTP request based on certain standard rules in the
	 * {@link RFC http://www.ietf.org/rfc/rfc2616.txt} stops validation at the
	 * first failure encountered
	 * 
	 * @param httpRequest
	 *            request object to be validated
	 * @throws HttpError
	 *             raises an error with status and message if validation fails
	 */
	protected void validateHttpRequest(HttpRequest httpRequest)
			throws HttpError {

		// HTTP/1.1 dictates Host header is mandatory
		// http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.23
		if (Version.valueOf(httpRequest.getProtocol()).ordinal() >= Version.VERSION_1_1
				.ordinal() && httpRequest.getHeader("Host") == null) {
			throw new HttpError(400);
		}

	}
}
