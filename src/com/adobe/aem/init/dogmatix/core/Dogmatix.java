package com.adobe.aem.init.dogmatix.core;

import java.util.Properties;

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
import com.adobe.aem.init.dogmatix.util.NetworkUtils;

/**
 * A loyal HTTP server that does what you ask it to do. Dedicated to the Asterix
 * animal character
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Dogmatix">Dogmatix</a>
 * 
 * @author vnagpal
 * 
 */
public class Dogmatix {

	private static final Logger logger = LoggerFactory.getLogger(Dogmatix.class);

	/**
	 * Entry point into the server. Initiates two threads which listen for
	 * incoming HTTP requests and Command requests. Takes optional command-line
	 * arguments which can be used to override the settings specified in file
	 * server.xml
	 */
	public static void main(String[] args) throws Exception {

		Properties commandLineArguments = parse(args);

		// read server.xml
		ServerConfig config = null;
		try {
			logger.debug("Reading Server config");
			config = ServerConfig.getInstance(commandLineArguments.getProperty("server.xml"));
		} catch (InvalidConfigException e) {
			logger.error("Unable to load server config", e);
			System.exit(-1);
		}

		//Either perform an action or continue with server startup
		if(commandLineArguments.containsKey("action")) {
			doAction(config, commandLineArguments.getProperty("action"));
		}
		else {
			Listener http = new HttpListener(config);
			Listener cmd = new CommandListener(config, http);

			Runtime.getRuntime().addShutdownHook(new Finisher(cmd));

			http.start();
			cmd.start();

			ServerStatistics.serverStarted();
			
			logger.info("Woof Woof.. Dogmatix at your service!");

			// Do not exit main thread. Wait for listeners to finish. Just being
			// nice.
			try {
				cmd.join();
				http.join();
			} catch (InterruptedException e) {
				logger.error("Error stopping server", e);
				System.exit(-1);
			}
		}

		System.exit(0);

	}

	/**
	 * Parse and process the command line arguments. Stores any value against a
	 * data option in a map to be re-used later.
	 * 
	 * @param args
	 *            String array of whitespace separated command line args
	 * @return 
	 */
	private static Properties parse(String[] args) {
		Properties commandLineArguments = new Properties();
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				String option = args[i];
				if (!Constants.SUPPORTED_OPTIONS.contains(option)) {
					System.out.println("Invalid option");
					help();
				}
				if (option.equals("-a") || option.equals("--action")) {
					// Perform an action
					try {
						commandLineArguments.put("action", args[++i]);
					} catch (Exception e) {
						System.out.println("Missing action");
						help();
					}
					// Need to perform an action. No logs will be generated.
					LogManager.getRootLogger().setLevel(Level.OFF);
				}
				if (option.equals("-h") || option.equals("--help")) {
					help();
				}
				if (option.equals("-l") || option.equals("--level")) {
					// Change the log level to specified level in case of log4j
					try {
						LogManager.getRootLogger().setLevel(Level.toLevel(args[++i]));
					} catch (Exception e) {
						System.out.println("Missing/Invalid log level");
						help();
					}
					
				}
				if (option.equals("-f") || option.equals("--file")) {
					// Use an alternate user defined server.xml
					try {
						commandLineArguments.put("server.xml", args[++i]);
					} catch (Exception e) {
						System.out.println("Missing filename");
						help();
					}
				}
				if (option.equals("-v") || option.equals("--version")) {
					// Displays server version information and exits
					System.out.println("Dogmatix HTTP Web-Server Ver. "
							+ Constants.VERSION
							+ " (c) Varun Nagpal (vnagpal@adobe.com)");
					System.exit(0);
				}
			}
		}
		return commandLineArguments;
	}

	/**
	 * Displays help information and exits
	 */
	private static void help() {
		System.out.println("usage: java -jar <jar-filename> [options]");
		System.out.println("");
		System.out.println("Options:");
		System.out
				.println("\t-a,--action <action>\tPerform an action out of (stop)");
		System.out.println("\t-l,--level <loglevel>\tSets the log level");
		System.out
				.println("\t-f,--file <filename>\tAlternate path for the server settings XML file");
		System.out.println("\t-h,--help\t\tDisplay help information");
		System.out.println("\t-v,--version\t\tDisplay version information");
		System.exit(0);
	}
	
	/**
	 * If requested any action, perform the action.
	 * Possible actions are 
	 * <ul>
	 * 	<li>Stop</li>
	 * </ul>
	 */
	private static void doAction(ServerConfig config, String action) throws Exception {
		// if server needs to be stopped, send a GET request to
		// http://localhost:{cmdPort}/{stopCommand}
		if (action.equalsIgnoreCase(config.stopCommand())) {
			boolean alive = true;
			while (alive) {
				alive = NetworkUtils.ping(config.stopURL());

				System.out.println("Sending STOP signal to Dogmatix");

				// retry after 1 sec
				if (alive) {
					Thread.sleep(1000);
				}
			}
			System.exit(0);
		}
	}
}
