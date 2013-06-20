package com.adobe.aem.init.dogmatix.http.response;

import static com.adobe.aem.init.dogmatix.util.Constants.NEW_LINE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Hashtable;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.config.ServerConfig;
import com.adobe.aem.init.dogmatix.exceptions.HttpError;
import com.adobe.aem.init.dogmatix.util.Constants;

public class HttpResponse {

	private static final Logger logger = LoggerFactory
			.getLogger(HttpResponse.class);
	
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
			} catch (MagicParseException | MagicMatchNotFoundException
					| MagicException e) {
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

	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream finalResponse = new ByteArrayOutputStream();
		
		byte[] initial = toString().getBytes();
		
		finalResponse.write(initial, 0, initial.length);

		if(body.size()>0) {
			finalResponse.write(body.toByteArray(), 0, body.size());
		}
		
		return finalResponse.toByteArray();
	}

	public void flush() throws IOException {
		if(flushed) {
			return;
		}
		try {
			outputStream.write(getBytes());
		}
		catch(Exception e) {
			//log
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
