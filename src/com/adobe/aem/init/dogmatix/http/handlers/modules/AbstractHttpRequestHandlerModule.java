package com.adobe.aem.init.dogmatix.http.handlers.modules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Hashtable;

import com.adobe.aem.init.dogmatix.config.ModuleConfig;
import com.adobe.aem.init.dogmatix.exceptions.HttpError;
import com.adobe.aem.init.dogmatix.exceptions.InvalidHeaderException;
import com.adobe.aem.init.dogmatix.http.request.HttpRequest;
import com.adobe.aem.init.dogmatix.http.request.Method;
import com.adobe.aem.init.dogmatix.http.request.Version;
import com.adobe.aem.init.dogmatix.http.response.HttpResponse;
import com.adobe.aem.init.dogmatix.util.Constants;

public abstract class AbstractHttpRequestHandlerModule {

	private static final int MAX_HEADER_FIELD_NAME_LENGTH = 0;

	private static final int MAX_HEADER_FIELD_VALUE_LENGTH = 0;

	protected int status;

	protected ModuleConfig config;

	public ModuleConfig getConfig() {
		return config;
	}

	public void setConfig(ModuleConfig config) {
		this.config = config;
	}

	protected void init() throws Exception {
		// load module config
	}

	/**
	 * abstract method. to be implemented by child classes of modules wishing to
	 * consume the http requests.
	 * 
	 * @param in
	 *            inputstream on the serversocket to read from
	 * @param out
	 *            outputstream of the serversocket to write on
	 */
	public abstract void consume(InputStream in, OutputStream out);

	/**
	 * dumps the whole request as a string. useful for debugging puposes.
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
	 * parse request headers into a table for easier access
	 * 
	 * @source http://www.java2s.com/Code/Java/Network-Protocol/HttpParser.htm
	 * @param reader
	 * @return a hash table of header name (lower case) and values
	 * @throws HttpError
	 */
	private Hashtable<String, String> parseHeaders(BufferedReader reader)
			throws HttpError {
		Hashtable<String, String> headers = null;
		String line = null;
		try {
			int idx;

			// that fscking rfc822 allows multiple lines, we don't care now
			line = reader.readLine();
			while (!line.isEmpty()) {
				idx = line.indexOf(':');
				if (idx < 0) {
					break;
				} else {
					if (headers == null) {
						headers = new Hashtable<String, String>();
					}
					String headerName = line.substring(0, idx).toLowerCase();
					String headerValue = line.substring(idx + 1).trim();
					
					if(headerName.length() > MAX_HEADER_FIELD_NAME_LENGTH) {
						throw new InvalidHeaderException("Name too long");
					}
					if(headerValue.length() > MAX_HEADER_FIELD_VALUE_LENGTH) {
						throw new InvalidHeaderException("Value too long");
					}
					
					headers.put(headerName, headerValue);
				}
				line = reader.readLine();
			}
		} catch (InvalidHeaderException e) {
			throw new HttpError(400, "(Invalid Header - " + e.getMessage() + ")");
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
	 * @return the http request object thus formed
	 * @throws HttpError
	 */
	protected HttpRequest parseRequest(InputStream inputStream)
			throws HttpError {
		HttpRequest httpRequest = new HttpRequest();
		String firstLine = null;

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		try {
			firstLine = reader.readLine();
		} catch (IOException e) {
			throw new HttpError(400);
		}

		// non-existent request no-op
		if (firstLine == null || firstLine.length() == 0) {
			throw new HttpError(400);
		}

		if (Character.isWhitespace(firstLine.charAt(0))) {
			// starting whitespace, return bad request
			throw new HttpError(400, "(First character is space)");
		}

		String[] cmd = firstLine.split("\\s");
		if (cmd.length != 3) {
			throw new HttpError(400, "(Invalid Format - " + firstLine + ")");
		}

		if (cmd[2].indexOf("HTTP/") == 0 && cmd[2].indexOf('.') > 5) {
			String ver = cmd[2].substring(5);
			try {
				Float.parseFloat(ver);
				httpRequest.setProtocol(ver);
			} catch (NumberFormatException nfe) {
				throw new HttpError(400, "(Invalid version - " + ver+ ")");
			}
		} else {
			throw new HttpError(400, "(Version missing)");
		}

		try {
			httpRequest.setMethod(Method.valueOf(cmd[0]));
		} catch (Exception e) {
			throw new HttpError(400, "(Invalid method - " + cmd[0] + "");
		}

		if (httpRequest.getMethod() == Method.GET
				|| httpRequest.getMethod() == Method.HEAD) {

			int idx = cmd[1].indexOf('?');
			if (idx < 0) {
				httpRequest.setURI(cmd[1]);
			} else {
				try {
					httpRequest.setURI(URLDecoder.decode(
							cmd[1].substring(0, idx), "ISO-8859-1"));

					String[] prms = cmd[1].substring(idx + 1).split("&");

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
					throw new HttpError(400, "(Invalid encoding - " + cmd[1] + ")");
				}
			}
		}

		Hashtable<String, String> headers = parseHeaders(reader);
		httpRequest.setHeaders(headers);

		String line = null;
		StringBuffer entity = new StringBuffer();
		try {
			while (!(line = reader.readLine()).isEmpty()) {
				entity.append(line);
			}
		} catch (IOException e) {
			throw new HttpError(400, "(Invalid entity - " + line + ")");
		}
		httpRequest.setEntity(entity.toString().getBytes());

		return httpRequest;
	}

	/**
	 * Validates the http request based on certain standard rules in the
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

		if (Version.valueOf(httpRequest.getProtocol()).ordinal() >= Version.VERSION_1_1
				.ordinal() && httpRequest.getHeader("Host") == null) {
			throw new HttpError(400);
		}
		
		String auth = httpRequest.getHeader("Authorization");
		//not yet implemented authorization
		//TODO read auth credentials from module config and validate
		
		

	}

	protected void sendResponse(OutputStream outputStream, HttpResponse response) {
		PrintWriter out = new PrintWriter(outputStream, true);
		out.println(response.publish());
	}

}
