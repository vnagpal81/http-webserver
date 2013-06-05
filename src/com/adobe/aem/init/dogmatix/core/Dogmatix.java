package com.adobe.aem.init.dogmatix.core;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.listeners.CommandListener;
import com.adobe.aem.init.dogmatix.listeners.HttpListener;

/**
 * A loyal HTTP server that does what you ask it to do.
 * Dedicated to the Asterix animal character
 * {@link http://en.wikipedia.org/wiki/Dogmatix Dogmatix} 
 * 
 * @author vnagpal
 *
 */
public class Dogmatix {

	private static int _HTTP_PORT = 4444;

	private static int _CMD_PORT = 1913;
	
	private static int maxThreads = 1000;

	private static boolean listening = true;
	
	private static final Logger logger = LoggerFactory.getLogger(Dogmatix.class);

	/**
	 * Entry point into the server.
	 * Initiates two threads which listen for incoming HTTP requests and Command requests.
	 * Takes optional command-line arguments which can be used to override the settings specified in file server.properties 
	 */
	public static void main(String[] args) throws IOException {

		// read server.properties

		Thread http = new HttpListener(_HTTP_PORT, maxThreads);
		Thread cmd = new CommandListener(_CMD_PORT);

		http.start();
		cmd.start();

		logger.info("Woof Woof.. Dogmatix at your service!");

		try {
			cmd.join();
			if (!listening) {
				http.interrupt();
			}
		} catch (InterruptedException e) {
			logger.error("Error stopping server", e);
		}

	}

	public synchronized static boolean isListening() {
		return listening;
	}

	public synchronized static void startListening() {
		Dogmatix.listening = true;
	}

	public synchronized static void stopListening() {
		Dogmatix.listening = false;
	}

}
