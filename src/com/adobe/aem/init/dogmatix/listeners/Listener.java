package com.adobe.aem.init.dogmatix.listeners;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.util.NetworkUtils;

public abstract class Listener extends Thread {

	protected static Logger logger = LoggerFactory.getLogger(Listener.class);

	protected int port;
	protected ServerSocket serverSocket;

	public Listener(int port) {
		this.port = port;
	}

	public void run() {
		// create a server side socket to listen for
		// incoming transport on this port
		listen(this.port);

		if (serverSocket != null) {
			while(!isInterrupted()) {
				// Accept incoming requests on the server socket until
				// Thread is manually interrupted or an I/O error is
				// encountered
				try {
					Socket socket = serverSocket.accept();
					process(socket);
				} catch (IOException e) {
					logger.debug("Intentionally closing socket listening on {}", this.port);
					interrupt();
				} 
			}
			logger.debug("Exiting listener thread");
		}
	}

	protected void listen(int port) {
		try {
			if(NetworkUtils.available(port)) {
				serverSocket = new ServerSocket(port);
				logger.debug("Listening on port: " + port);
			}
		} catch (IOException e) {
			logger.error("Could not listen on port: " + port);
		}
	}

	protected abstract void process(Socket socket) throws IOException;

	public void stopListening() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			logger.debug("Intentionally closing socket listening on {}", this.port);
		}
	}
}
