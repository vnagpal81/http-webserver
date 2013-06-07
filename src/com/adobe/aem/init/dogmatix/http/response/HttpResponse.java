package com.adobe.aem.init.dogmatix.http.response;

import static com.adobe.aem.init.dogmatix.util.Constants.NEW_LINE;
import static com.adobe.aem.init.dogmatix.util.Constants.SERVER_HTTP_VERSION;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

import com.adobe.aem.init.dogmatix.exceptions.HttpError;
import com.adobe.aem.init.dogmatix.util.Constants;

public class HttpResponse {

	private String statusLine = SERVER_HTTP_VERSION + " 200 "
			+ Status.HttpReplies.get(200) + NEW_LINE;
	private Hashtable<String, String> headers = new Hashtable<String, String>();
	private StringBuffer body = new StringBuffer();

	public HttpResponse() {

	}

	public HttpResponse(String body) {
		this.body.append(body);
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
				+ Status.HttpReplies.get(code) + NEW_LINE;
		return this;
	}

	public HttpResponse append(InputStream in) {
		int rollBackTo = body.length();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				body.append(line);
			}
		} catch (IOException e) {
			body.delete(rollBackTo, body.length());
		}
		return this;
	}

	public HttpResponse err(HttpError error) {
		return status(error.getStatus()).append(error.getMessage());
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
	
		if (!headers.containsKey(Constants.HEADERS.CONTENT_TYPE)) {
			try {
				headers.put(Constants.HEADERS.CONTENT_TYPE, Magic
						.getMagicMatch(body.toString().getBytes())
						.getMimeType());
			} catch (MagicParseException | MagicMatchNotFoundException
					| MagicException e) {
				//log
			}
		}
		
		//if server config dictates SERVER_HTTP_VERSION is 1.1
		headers.put(Constants.HEADERS.CONNECTION, "Keep-Alive");
		
		for (String name : headers.keySet()) {
			finalResponse.append(name + ": " + headers.get(name));
			finalResponse.append(NEW_LINE);
		}
		finalResponse.append(body);
		return finalResponse.toString();
	}
}
