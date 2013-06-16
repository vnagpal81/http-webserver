package com.adobe.aem.init.dogmatix.listeners;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.config.ServerConfig;

public class CommandListener extends Listener {
	
	Logger logger = LoggerFactory.getLogger(CommandListener.class);
	
	private List<Listener> listeners;
	
	private ServerConfig serverConfig;
	
	public CommandListener(ServerConfig serverConfig, Listener... listeners) {
		super(serverConfig.commandPort());
		this.serverConfig = serverConfig;
		if(listeners != null && listeners.length > 0) {
			if(this.listeners == null) {
				this.listeners = new ArrayList<Listener>();
			}
			for(Listener listener : listeners) {
				registerListener(listener);
			}
		}
	}

	protected void process(Socket socket) throws IOException {
		logger.debug("Processing command");
		BufferedReader in = new BufferedReader(
					new InputStreamReader(
					socket.getInputStream()));

		String inputLine = in.readLine();
		String[] cmd = inputLine.split("\\s");
		String command = cmd[1].substring(1);
		logger.debug("Received command {}", command);

		cleanup(socket);
		
		if(command.toLowerCase().contains(serverConfig.stopCommand().toLowerCase())) {
			//stopListening();
			System.exit(0);
		}

	}	
	
	public void registerListener(Listener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void stopListening() {
		logger.debug("Shutting down server...");
		for(Listener listener : listeners) {
			logger.debug("Stopping all registered listeners");
			listener.stopListening();
		}
		logger.debug("Stop listening myself");
		super.stopListening();
		logger.debug("_RIP_");
	}
	
	private void cleanup(Socket socket) throws IOException {
		socket.getOutputStream().close();
		
	}
}
