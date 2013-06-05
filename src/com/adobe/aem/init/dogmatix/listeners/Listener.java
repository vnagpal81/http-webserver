package com.adobe.aem.init.dogmatix.listeners;

import java.net.*;
import java.io.*;

import com.adobe.aem.init.dogmatix.core.Dogmatix;

public abstract class Listener extends Thread {

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
			System.out.println("Could not close socket");
			System.exit(1);
		}
	}

	protected ServerSocket listen(int port) {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("Could not listen on port: " + port);
			System.exit(1);
		}

		System.out.println("Listening on port: " + port);
		return serverSocket;
	}

	protected boolean isListening() {
		return Dogmatix.isListening();
	}

	protected abstract void process(Socket socket) throws IOException;
}
