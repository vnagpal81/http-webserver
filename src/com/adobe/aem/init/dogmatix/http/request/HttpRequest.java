package com.adobe.aem.init.dogmatix.http.request;

import java.util.Hashtable;

public class HttpRequest {
	
	private Hashtable<String, String> headers;
	private Hashtable<String, String> params;
	private byte[] entity;

	private String raw;
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
	public String getRaw() {
		return raw;
	}
	public void setRaw(String raw) {
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
		URI = uRI;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
}
