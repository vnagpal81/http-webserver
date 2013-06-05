package com.adobe.aem.init.dogmatix.exceptions;

@SuppressWarnings("serial")
public class InvalidModuleException extends Exception {
	
	private String moduleName;
	
	public InvalidModuleException(String moduleName) {
		this.moduleName = moduleName;
	}

}
