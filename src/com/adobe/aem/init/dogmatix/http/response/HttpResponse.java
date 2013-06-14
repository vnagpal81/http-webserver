package com.adobe.aem.init.dogmatix.http.response;

import static com.adobe.aem.init.dogmatix.util.Constants.NEW_LINE;
import static com.adobe.aem.init.dogmatix.util.Constants.SERVER_HTTP_VERSION;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Hashtable;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.exceptions.HttpError;
import com.adobe.aem.init.dogmatix.util.Constants;

public class HttpResponse {

	private static final Logger logger = LoggerFactory
			.getLogger(HttpResponse.class);

	private String statusLine = SERVER_HTTP_VERSION + " 200 "
			+ Status.HttpReplies.get(200);
	private Hashtable<String, String> headers = new Hashtable<String, String>();
	private StringBuffer body = new StringBuffer();

	private OutputStream outputStream;

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

	public String getStatusLine() {
		return statusLine;
	}

	public Hashtable<String, String> getHeaders() {
		return headers;
	}

	public HttpResponse headers(Hashtable<String, String> headers) {
		this.headers.putAll(headers);
		return this;
	}

	public StringBuffer getBody() {
		return body;
	}

	public HttpResponse body(StringBuffer body) {
		this.body = body;
		return this;
	}

	public HttpResponse append(Object o) {
		body.append(o);
		return this;
	}

	public HttpResponse addHeader(String name, String value) {
		headers.put(name, value);
		return this;
	}

	public HttpResponse write(byte[] chunk) {
		body.append(chunk);
		return this;
	}

	public HttpResponse status(int code) {
		statusLine = SERVER_HTTP_VERSION + " " + code + " "
				+ Status.HttpReplies.get(code);
		return this;
	}

	public HttpResponse append(InputStream in) {
		int resetTo = body.length();
		try {
			body.append(IOUtils.toString(in, "UTF-8"));
		} catch (Exception e) {
			body.delete(resetTo, body.length());
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

	public String publish() {
		StringBuffer finalResponse = new StringBuffer();

		if (!statusLine.isEmpty()) {
			finalResponse.append(statusLine);
			finalResponse.append(NEW_LINE);
		}

		if (!headers.containsKey(Constants.HEADERS.CONTENT_LENGTH)) {
			headers.put(Constants.HEADERS.CONTENT_LENGTH,
					String.valueOf(body.length()));
		}

		if (body.length()>0 && !headers.containsKey(Constants.HEADERS.CONTENT_TYPE)) {
			try {
				headers.put(Constants.HEADERS.CONTENT_TYPE, Magic
						.getMagicMatch(body.toString().getBytes())
						.getMimeType());
			} catch (MagicParseException | MagicMatchNotFoundException
					| MagicException e) {
				logger.error("Error while determining content type");
			}
		}

		// if server config dictates SERVER_HTTP_VERSION is 1.1
		headers.put(Constants.HEADERS.CONNECTION, "Keep-Alive");

		for (String name : headers.keySet()) {
			finalResponse.append(name + ": " + headers.get(name));
			finalResponse.append(NEW_LINE);
		}

		finalResponse.append(NEW_LINE);

		if(body.length()>0) {
			finalResponse.append(body);
		}
		
		return finalResponse.toString();
	}

	public void flush() {
		String responseStr = publish();
		PrintWriter out = new PrintWriter(outputStream, true);
		out.println(responseStr);
		out.close();
		logger.debug("Response sent back");
		logger.debug(responseStr);
	}
}
