package com.adobe.aem.init.dogmatix.exceptions;

@SuppressWarnings("serial")
public class ConnectionTimeOut extends Exception {

	public ConnectionTimeOut() {
		super();
	}

	public ConnectionTimeOut(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ConnectionTimeOut(String message, Throwable cause) {
		super(message, cause);
	}

	public ConnectionTimeOut(String message) {
		super(message);
	}

	public ConnectionTimeOut(Throwable cause) {
		super(cause);
	}

	
}
