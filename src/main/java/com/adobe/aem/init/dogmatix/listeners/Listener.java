/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. The ASF licenses this file to You 
 * under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.adobe.aem.init.dogmatix.listeners;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.util.NetworkUtils;

/**
 * A listener is defined by its capability to listen on a specified port.
 * This is achieved by creating a {@link ServerSocket} on that port and 
 * waiting for client connections.
 * 
 * A listener is a thread because each connection accepted on the {@link ServerSocket} must
 * free up the port for further connections and hence, needs to be processed in a synchronous {@link Thread}
 * 
 * The actual processing is done in the abstract method process() which the {@link Listener} implementations
 * need to provide.
 * 
 * Also exposes a stopListening() method for anyone to call to forcefully close the {@link ServerSocket}
 * 
 * @see CommandListener
 * @see HttpListener
 * 
 * @author vnagpal
 *
 */
public abstract class Listener extends Thread {

	protected static Logger logger = LoggerFactory.getLogger(Listener.class);

	protected int port;
	protected ServerSocket serverSocket;

	public Listener(int port) {
		this.port = port;
	}

	/**
	 * Accept incoming requests on the server socket until Thread is manually 
	 * interrupted or an I/O error is encountered.
	 */
	public void run() {
		listen(this.port);

		if (serverSocket != null) {
			while(!isInterrupted()) {
				try {
					Socket socket = serverSocket.accept();
					socket.setTcpNoDelay(true);
					process(socket);
				} catch (IOException e) {
					logger.debug("Intentionally closing socket listening on {}", this.port);
					interrupt();
				} 
			}
			logger.debug("Exiting listener thread");
		}
	}

	/**
	 * Create a server side socket to listen for incoming transport on this port
	 * @param port
	 */
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

	/**
	 * Process the connection made on this socket
	 * @param socket
	 * @throws IOException
	 */
	protected abstract void process(Socket socket) throws IOException;

	/**
	 * Instruction to stop listening by closing the server side socket.
	 */
	public void stopListening() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			logger.debug("Intentionally closing socket listening on {}", this.port);
		}
	}
}
