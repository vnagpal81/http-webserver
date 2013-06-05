package com.adobe.aem.init.dogmatix.http.handlers;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.http.handlers.modules.AbstractHttpRequestHandlerModule;
import com.adobe.aem.init.dogmatix.http.handlers.modules.ModuleFactory;
import com.adobe.aem.init.dogmatix.http.handlers.modules.resources.ResourcesModule;

public class HttpRequestHandler implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(HttpRequestHandler.class);
	
	
	private Socket socket;	

	public HttpRequestHandler(Socket socket) {
		this.socket = socket;
	}

	public void run() {

		try {
			logger.info("Incoming Request...");
			
			//Parse Request
			
			//Determine URL
			
			//Map from config and get Module
			
			//Get Module Instance from ModuleFactory
			AbstractHttpRequestHandlerModule module = ModuleFactory.getModule(ResourcesModule.class.getCanonicalName());

			OutputStream out = this.socket.getOutputStream();
			InputStream in = this.socket.getInputStream();

			//Delegate Request handling to module.consume() which will also build the Response
			module.consume(in, out);

			//If server config dictates SERVER_HTTP_VERSION = 1.1 do not close the socket streams yet
			cleanup();
		}
		catch(Exception e) {
			logger.error("Error while handling HTTP request", e);
		}

	}
	
	private void cleanup() throws Exception {
		this.socket.close();
	}


}
