package com.adobe.aem.init.dogmatix.exceptions;

@SuppressWarnings("serial")
public class HttpError extends Exception {

	private int status;
	private String message;
	
	public HttpError() {
	}
	
	public HttpError(int status) {
		this.status = status;
	}

	public HttpError(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
