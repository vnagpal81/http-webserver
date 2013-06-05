package com.adobe.aem.init.dogmatix.exceptions;

@SuppressWarnings(value = "serial")
public class InvalidConfigException extends Exception {

	public InvalidConfigException(String message) {
		super(message);
	}

	public InvalidConfigException() {
		super();
	}

}
