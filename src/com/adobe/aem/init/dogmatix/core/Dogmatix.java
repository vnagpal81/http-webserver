package com.adobe.aem.init.dogmatix.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.config.ServerConfig;
import com.adobe.aem.init.dogmatix.exceptions.InvalidConfigException;
import com.adobe.aem.init.dogmatix.listeners.CommandListener;
import com.adobe.aem.init.dogmatix.listeners.HttpListener;
import com.adobe.aem.init.dogmatix.listeners.Listener;

/**
 * A loyal HTTP server that does what you ask it to do. Dedicated to the Asterix
 * animal character {@link http://en.wikipedia.org/wiki/Dogmatix Dogmatix}
 * 
 * @author vnagpal
 * 
 */
public class Dogmatix {

	private static final Logger logger = LoggerFactory
			.getLogger(Dogmatix.class);

	/**
	 * Entry point into the server. Initiates two threads which listen for
	 * incoming HTTP requests and Command requests. Takes optional command-line
	 * arguments which can be used to override the settings specified in file
	 * server.properties
	 */
	public static void main(String[] args) throws Exception {

		// read server.xml
		ServerConfig config = null;
		try {
			logger.debug("Reading Server config");
			config = ServerConfig.getInstance();
		} catch (InvalidConfigException e) {
			logger.error("Unable to load server config", e);
			System.exit(-1);
		}

		Listener http = new HttpListener(config);
		Listener cmd = new CommandListener(config, http);
		
		Runtime.getRuntime().addShutdownHook(new Finisher(cmd));

		http.start();
		cmd.start();

		logger.info("Woof Woof.. Dogmatix at your service!");

		try {
			cmd.join();
			http.join();
		} catch (InterruptedException e) {
			logger.error("Error stopping server", e);
			System.exit(-1);
		}

	}

}
