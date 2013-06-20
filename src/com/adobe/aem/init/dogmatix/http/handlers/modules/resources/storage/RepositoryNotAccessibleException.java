package com.adobe.aem.init.dogmatix.http.handlers.modules.resources.storage;

import java.io.IOException;

public class RepositoryNotAccessibleException extends IOException {

	public RepositoryNotAccessibleException(Throwable cause) {
		super(cause);
	}

	public RepositoryNotAccessibleException() {
		super();
	}

	public RepositoryNotAccessibleException(String message) {
		super(message);
	}
}
