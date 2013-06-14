package com.adobe.aem.init.dogmatix.listeners;

import java.net.*;
import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.core.Dogmatix;

public abstract class Listener extends Thread {
	
	protected static Logger logger = LoggerFactory.getLogger(Listener.class);
	
	protected int port;

	public Listener(int port) {
		this.port = port;
	}

	public void run() {

		ServerSocket serverSocket = listen(this.port);

		try {
			while (this.isListening()) {
				Socket socket = serverSocket.accept();
				process(socket);
			}

			serverSocket.close();
		} catch (IOException e) {
			logger.debug("Could not close socket");
			System.exit(1);
		}
	}

	protected ServerSocket listen(int port) {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			logger.debug("Could not listen on port: " + port);
			System.exit(1);
		}

		logger.debug("Listening on port: " + port);
		return serverSocket;
	}

	protected boolean isListening() {
		return Dogmatix.isListening();
	}

	protected abstract void process(Socket socket) throws IOException;
}
