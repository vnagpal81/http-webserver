package com.adobe.aem.init.dogmatix.listeners;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import com.adobe.aem.init.dogmatix.core.Dogmatix;

public class CommandListener extends Listener {
	
	public CommandListener(int port) {
		super(port);
	}

	protected void process(Socket socket) throws IOException {
		BufferedReader in = new BufferedReader(
					new InputStreamReader(
					socket.getInputStream()));

		String inputLine = in.readLine();

		if(inputLine.toLowerCase().contains("/stop")) {
			System.out.println("\nReceived Command STOP");
			Dogmatix.stopListening();
			//System.exit(1);	
		}

		socket.close();
	}		
}
