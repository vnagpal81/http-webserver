package com.adobe.aem.init.dogmatix.exceptions;

@SuppressWarnings("serial")
public class InvalidHeaderException extends Exception {
	
	private String headerName;
	private String headerValue;

	public InvalidHeaderException(String headerName, String headerValue, String message) {
		super(message);
		this.headerName = headerName;
		this.headerValue = headerValue;
	}

	public InvalidHeaderException() {}

	public String getHeaderName() {
		return headerName;
	}

	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}

	public String getHeaderValue() {
		return headerValue;
	}

	public void setHeaderValue(String headerValue) {
		this.headerValue = headerValue;
	}
	
}