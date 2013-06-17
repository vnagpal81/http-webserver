package com.adobe.aem.init.dogmatix.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.config.ServerConfig;
import com.adobe.aem.init.dogmatix.exceptions.InvalidConfigException;
import com.adobe.aem.init.dogmatix.listeners.CommandListener;
import com.adobe.aem.init.dogmatix.listeners.HttpListener;
import com.adobe.aem.init.dogmatix.listeners.Listener;
import com.adobe.aem.init.dogmatix.util.Constants;

/**
 * A loyal HTTP server that does what you ask it to do. Dedicated to the Asterix
 * animal character 
 * @see <a href="http://en.wikipedia.org/wiki/Dogmatix">Dogmatix</a>
 * 
 * @author vnagpal
 * 
 */
public class Dogmatix {

	private static final Logger logger = LoggerFactory
			.getLogger(Dogmatix.class);

	private static Map<String, String> commandLineArguments = new HashMap<String, String>();

	/**
	 * Entry point into the server. Initiates two threads which listen for
	 * incoming HTTP requests and Command requests. Takes optional command-line
	 * arguments which can be used to override the settings specified in file
	 * server.properties
	 */
	public static void main(String[] args) throws Exception {

		parse(args);

		// read server.xml
		ServerConfig config = null;
		try {
			logger.debug("Reading Server config");
			config = ServerConfig.getInstance(commandLineArguments
					.get("server.xml"));
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

	/**
	 * Parse and process the command line arguments. 
	 * Stores any value against a data option in a map to be re-used later.
	 * 
	 * @param args String array of whitespace separated command line args
	 */
	private static void parse(String[] args) {
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				String option = args[i];
				if (!Constants.SUPPORTED_OPTIONS.contains(option)) {
					System.out.println("Invalid option");
					help();
				}
				if (option.equals("-h") || option.equals("--help")) {
					help();
				}
				if (option.equals("-d") || option.equals("--debug")) {
					//Change the log level to DEBUG in case of log4j
					LogManager.getRootLogger().setLevel(Level.DEBUG);
				}
				if (option.equals("-f") || option.equals("--file")) {
					//Use an alternate user defined server.xml
					commandLineArguments.put("server.xml", args[++i]);
				}
				if (option.equals("-v") || option.equals("--version")) {
					//Displays server version information and exits
					System.out.println("Dogmatix HTTP Web-Server Ver. "
							+ Constants.VERSION
							+ " (c) Varun Nagpal (vnagpal@adobe.com)");
					System.exit(0);
				}
			}
		}
	}

	/**
	 * Displays help information and exits
	 */
	private static void help() {
		System.out.println("usage: java -jar <jar-filename> [options]");
		System.out.println("");
		System.out.println("Options:");
		System.out.println("\t-d,--debug\t\tSets the log level to DEBUG");
		System.out
				.println("\t-f,--file <filename>\tAlternate path for the server settings XML file");
		System.out.println("\t-h,--help\t\tDisplay help information");
		System.out.println("\t-v,--version\t\tDisplay version information");
		System.exit(0);
	}
}
