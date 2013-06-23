package com.adobe.aem.init.dogmatix.http.handlers;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Hashtable;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.config.ModuleConfig;
import com.adobe.aem.init.dogmatix.core.ReusableSocket;
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
import com.adobe.aem.init.dogmatix.util.Constants;
import com.adobe.aem.init.dogmatix.util.NetworkUtils;

public class HttpRequestHandler implements Runnable {

	private static final Logger logger = LoggerFactory
			.getLogger(HttpRequestHandler.class);

	private ReusableSocket socket;

	public HttpRequestHandler(Socket socket) {
		this.socket = new ReusableSocket(socket);
	}

	public void run() {

		try {
			logger.debug("Incoming Request...");

			HttpContext ctx = new HttpContext();

			OutputStream out = this.socket.getOutputStream();
			InputStream in = this.socket.getInputStream();

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

					// Map from config and get Module
					ModuleConfig config = URLMapping.getModuleConfig(url);

					if (config == null) {
						throw new HttpError(404);
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
		} catch (Exception e) {
			logger.error("Error while handling HTTP request", e);
			
		} finally {
			try {
				//cleanup();
				logger.debug("Closing socket");
				this.socket.close();
			} catch (IOException e) {
				logger.error("Unable to close socket connection");
			}
		}
	}

	private void cleanup() throws IOException {
		if(!this.socket.isPersist()) {
			logger.debug("Closing socket");
			this.socket.close();
		}
		else {
			logger.debug("Persisting the socket : Count - {}", this.socket.getCount()+1);
			this.socket.setCount(this.socket.getCount()+1);
			this.socket.setLastAccess(System.currentTimeMillis());
		}
	}

	/**
	 * dumps the whole request as a string.
	 * 
	 * @param inputStream
	 * @return string representation of the http request
	 * @throws IOException
	 */
	protected String readRequest(InputStream inputStream) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				inputStream));
		String inputLine;
		StringBuffer request = new StringBuffer();

		while (!(inputLine = in.readLine()).isEmpty()) {
			request.append(inputLine);
			request.append(Constants.NEW_LINE);
		}

		return request.toString();
	}

	/**
	 * parse request headers into a table for easier access. also optionally
	 * stores the header lines in a buffer
	 * 
	 * @source http://www.java2s.com/Code/Java/Network-Protocol/HttpParser.htm
	 * @param reader
	 * @param raw
	 *            buffer to copy into
	 * @return a hash table of header name (lower case) and values
	 * @throws HttpError
	 */
	private Hashtable<String, String> parseHeaders(BufferedReader reader,
			ByteArrayOutputStream raw) throws HttpError {
		if (reader == null) {
			throw new HttpError(400);
		}

		Hashtable<String, String> headers = null;
		String line = null;
		try {
			int idx;

			// rfc822 allows multiple lines, we don't care now
			line = reader.readLine();
			while (!line.isEmpty()) {
				if(raw!= null) {
					raw.write((line + Constants.NEW_LINE).getBytes());
				}

				idx = line.indexOf(':');
				if (idx < 0) {
					break;
				} else {
					if (headers == null) {
						headers = new Hashtable<String, String>();
					}
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
				line = reader.readLine();
			}
		} catch (InvalidHeaderException e) {
			throw new HttpError(400, "(Invalid Header - " + e.getMessage()
					+ ")");
		} catch (IOException e) {
			throw new HttpError(400, "(Invalid Header - " + line + ")");
		}

		return headers;
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

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));

		try {
			firstLine = reader.readLine();
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

		logger.debug("Trying to parse Request headers");
		logger.debug("*******************************");
		Hashtable<String, String> headers = parseHeaders(reader, raw);
		httpRequest.setHeaders(headers);
		if (logger.isDebugEnabled()) {
			for (Entry<String, String> header : headers.entrySet()) {
				logger.debug(header.getKey() + ":" + header.getValue());
			}
		}
		logger.debug("*******************************");

		if (httpRequest.getMethod() != Method.GET) {
			throw new HttpError(405);
			/*try {
				logger.debug(reader.readLine());
			} catch (IOException e) {
			}
			finally {}
			
			int contentLength = -1;
			try {
				contentLength = Integer.parseInt(httpRequest.getHeader(Constants.HEADERS.CONTENT_LENGTH));
			}
			catch(Exception e) {
				
			}
			byte[]  buffer      = new byte[contentLength];
			String  postData    = "";

			System.out.println("Reading "+ contentLength + "bytes");
			try {
				inputStream.read(buffer, 0, contentLength);
			} catch (IOException e1) {
			}
			postData = new String(buffer, 0, buffer.length);
			System.out.println(postData);
			
			logger.debug("Trying to read the entity in the request");
			byte[] entityArr = NetworkUtils.readFrom(inputStream, true);
			raw.write(entityArr, 0, entityArr.length);
			
			
			
			ByteArrayOutputStream entity = new ByteArrayOutputStream();
		    int byteRead = -1;
		    try {
				while ((byteRead = inputStream.read()) != -1) {
					entity.write(byteRead);
					if(contentLength == 0) {
						break;
					}
					contentLength--;
				}
				
				httpRequest.setEntity(entity.toByteArray());
				logger.debug("Entity in the request - {} bytes", entity.size());
				raw.write(httpRequest.getEntity(), 0, entity.size());
				
			} catch (IOException e) {
				logger.error("Error reading entity in HTTP request", e);
				throw new HttpError(400, "(Invalid entity)");
			}*/
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
