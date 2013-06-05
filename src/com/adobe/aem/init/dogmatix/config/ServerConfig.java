package com.adobe.aem.init.dogmatix.config;

import java.util.Properties;

public class ServerConfig extends Properties {
	
	private static ServerConfig serverConfigInstance = null;

	private ServerConfig() {
			
	}

	public static ServerConfig getInstance() {
	
		if(serverConfigInstance == null) {
			buildServerConfig();
		}	

		return serverConfigInstance;

	}

	private static void buildServerConfig() {
		//read server.xml
		ServerConfig.class.getResourceAsStream("server.xml");
	}
	
	//HTTP Port
	//Command Port
	//Max threads
	//HTTP version
	
	public int httpPort() {
		return Integer.parseInt(getProperty("HTTPPort", "8080"));
	}
	
	public int commandPort() {
		return Integer.parseInt(getProperty("CommandPort", "5250"));
	}
	
	public int maxThreads() {
		return Integer.parseInt(getProperty("MaxThreads", "100"));
	}
	
	public String httpVersion() {
		return getProperty("HTTPVersion", "1.1");
	}
}
