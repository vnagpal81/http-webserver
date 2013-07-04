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

package com.adobe.aem.init.dogmatix.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adobe.aem.init.dogmatix.exceptions.InvalidConfigException;
import com.adobe.aem.init.dogmatix.http.handlers.modules.ModuleFactory;
import com.adobe.aem.init.dogmatix.http.request.Version;
import com.adobe.aem.init.dogmatix.util.XmlUtils;

/**
 * A singleton Server Config holder. Single point of access for all classes in
 * the server. <b>Must</b> be loaded before server threads start listening.
 * 
 * Currently does not support update of properties once server is up and running
 * 
 * @author vnagpal
 * 
 */
@SuppressWarnings("serial")
public class ServerConfig extends Properties {

	private static final Logger logger = LoggerFactory.getLogger(ServerConfig.class);

	/**
	 * Single instance shared across the application.
	 */
	private static ServerConfig serverConfigInstance = null;

	/**
	 * Possible configuration keys in the server XML settings file
	 * 
	 */
	public static interface CONFIGS {
		/**
		 * A valid system port on which the server listens for incoming 
		 * HTTP requests. It should be available at the time of startup.
		 * [REQUIRED]
		 */
		String HTTPPort = "HTTPPort";
		
		/**
		 * A valid system port on which the server listens for commands. 
		 * Commands are actions requests sent as HTTP GETs. Upon receiving 
		 * a command request the server is expected to perform an action. 
		 * Currently, it is only used to instruct the server to shutdown and
		 * fetch the server runtime statistics.
		 * [REQUIRED]
		 */
		String CommandPort = "CommandPort";
		
		/**
		 * Maximum number of threads allowed in the JVM instance that the server 
		 * is running in before it starts rejecting further incoming requests. 
		 * Very broadly, it translates into the concurrency limit. 
		 * This should not be tampered with unless you know what you are doing. 
		 * A low value might result in a high rate of request rejections. 
		 * A high value might result in frequent OutOfMemory exceptions.
		 * [REQUIRED]
		 */
		String MaxThreads = "MaxThreads";
		
		/**
		 * The protocol to be adhered while serving requests and building responses.
		 * [REQUIRED]
		 */
		String HTTPVersion = "HTTPVersion";
		
		/**
		 * The waiting period in seconds, before halting the server forcefully. 
		 * This time is utilized by threads to exit gracefully while releasing 
		 * any blocked I/O resources. May be increased if your application has 
		 * frequent long I/O blocking operations, like 3rd party web service requests. 
		 * If too high, will cause a delay in server shutdown and have no other effect 
		 * on its operation whatsoever.
		 * [REQUIRED]
		 */
		String ShutdownGraceTime = "ShutdownGraceTime";
		
		/**
		 * Linked to the CommandListener, the Stop command is the command 
		 * to look for in the GET request which will be received on the 
		 * CommandPort for shutting down the server. This has been made 
		 * configurable for an installation to have the flexibility in 
		 * declaring its own protocol. It is case-insensitive.
		 * [REQUIRED]
		 */
		String StopCommand = "StopCommand";
		
		/**
		 * The timeout value in seconds when the server responds with a Keep-Alive header. 
		 * Clients having the capability to persist connections will wait for this amount
		 * of time before closing the connection.
		 * [REQUIRED]
		 */
		String KeepAliveTimeout = "KeepAliveTimeout";
	}

	/**
	 * Make constructor private to enforce singleton behaviour.
	 * Can now only be created from within.
	 */
	private ServerConfig() {

	}
	
	/**
	 * Gets the created ServerConfig instance
	 * 
	 * @return ServerConfig instance
	 */
	public static ServerConfig getInstance() {
		return serverConfigInstance;
	}

	/**
	 * Get the ServerConfig instance, creating it if not already done.
	 * Allows the caller to specify a custom config file location.
	 * 
	 * @param filename An alternate server.xml location
	 * @return ServerConfig instance
	 * @throws InvalidConfigException thrown if encountered any error 
	 * during reading, parsing, validating the config
	 */
	public static ServerConfig getInstance(String filename)
			throws InvalidConfigException {

		if (serverConfigInstance == null) {
			buildServerConfig(filename);
		}

		return serverConfigInstance;

	}

	/**
	 * Builds the server config object in the following steps:
	 * 
	 * 1) If specified, reads the filename XML
	 * 2) Else, tries to locate a server.xml in the classpath
	 * 3) Reads the file into a String and validates the XML against the schema in server.xsd
	 * 4) Parses the String into a W3C DOM object
	 * 5) Looks for <modules></modules> and constructs ModuleConfig for each module
	 * 6) Module definitions may be via XML or through Annotation, in which case there should be a package scan path
	 * 7) ModuleConfigs are loaded into a module Cache against a re-usable Object Pool of each module class
	 * 8) URL mappings are built and cached for each module. This is referred to at runtime to resolve modules per request.
	 * 9) Looks for <configs></configs> and stores the server-level configurations while validating each value.
	 * 
	 * At any point during processing, if ANY error is encountered an InvalidConfigException with a brief message is thrown
	 * 
	 * @source http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser
	 * 
	 */
	private static void buildServerConfig(String filename)
			throws InvalidConfigException {
		// read server.xml
		try {
			logger.debug("Create ServerConfig Object");
			serverConfigInstance = new ServerConfig();

			InputStream xmlStream;
			if (filename == null) {
				logger.debug("Read server.xml from conf");
				xmlStream = ServerConfig.class.getClassLoader().getResourceAsStream("../conf/server.xml");
				if(xmlStream == null) {
					logger.debug("Read server.xml from classpath");
					xmlStream = ServerConfig.class.getClassLoader().getResourceAsStream("server.xml");
				}
			} else {
				logger.debug("Read server.xml from {}", filename);
				xmlStream = new FileInputStream(filename);
			}
			String inputXml = XmlUtils.read(xmlStream);

			logger.debug("Read server.xsd from classpath");
			InputStream xsdStream = ServerConfig.class.getClassLoader()
					.getResourceAsStream("server.xsd");

			logger.debug("Validate XML against server.xsd");
			if (!XmlUtils.validate(inputXml.toString(), xsdStream)) {
				throw new InvalidConfigException("Xml not well formed");
			}

			logger.debug("Parse XML into Document object");
			Document doc = XmlUtils.parse(inputXml, true);
			
			logger.debug("Read module scan path from xml");
			List<String> scanPaths = XmlUtils.text(doc, "modules>scan", ">");
			for (String scanPath : scanPaths) {
				logger.debug("Scan for modules at {}", scanPath);
				ModuleFactory.annotatedLoad(scanPath);
			}

			logger.debug("Read modules from xml");
			NodeList modules = XmlUtils.lookup(doc, "modules>module", ">");
			List<ModuleConfig> moduleConfigs = new ArrayList<ModuleConfig>();
			for (int i = 0; i < modules.getLength(); i++) {
				logger.debug("Reading module..");
				Element module = (Element) modules.item(i);
				String className = XmlUtils.text(module, "class", null).get(0);
				logger.debug("Classname : {}", className);
				String url = XmlUtils.text(module, "url", null).get(0);
				logger.debug("URL : {}", url);
				ModuleConfig moduleConfig = new ModuleConfig(className, url);
				// read module level settings
				Properties settings = XmlUtils.importProperties(XmlUtils
						.lookup(module, "property", null));
				moduleConfig.setSettings(settings);
				if (logger.isDebugEnabled()) {
					for (String name : settings.stringPropertyNames()) {
						logger.debug("Setting : {} = {}", name,
								settings.getProperty(name));
					}
				}
				moduleConfigs.add(moduleConfig);
			}
			if (moduleConfigs.size() > 0) {
				logger.debug("Load module classes");
				ModuleFactory.load(moduleConfigs);
			}

			logger.debug("Read configs from xml");
			NodeList configs = XmlUtils.lookup(doc, "configs>config", ">");
			for (int i = 0; i < configs.getLength(); i++) {
				Element config = (Element) configs.item(i);
				String name = config.getAttribute("name");
				String value = config.getAttribute("value");
				switch (name) {
				case CONFIGS.HTTPVersion:
					try {
						Version.getVersion(value);
					} catch (Exception e) {
						throw new InvalidConfigException(String.format(
								"Invalid HTTPVersion {}", value));
					}
					serverConfigInstance
							.setProperty(CONFIGS.HTTPVersion, value);
					logger.debug("HTTPVersion set to {}", value);
					break;
				case CONFIGS.CommandPort:
					try {
						Integer.parseInt(value);
					} catch (NumberFormatException e) {
						throw new InvalidConfigException("Invalid Command Port");
					}
					serverConfigInstance
							.setProperty(CONFIGS.CommandPort, value);
					logger.debug("CommandPort set to {}", value);
					break;
				case CONFIGS.HTTPPort:
					try {
						Integer.parseInt(value);
					} catch (NumberFormatException e) {
						throw new InvalidConfigException("Invalid HTTP Port");
					}
					serverConfigInstance.setProperty(CONFIGS.HTTPPort, value);
					logger.debug("HTTPPort set to {}", value);
					break;
				case CONFIGS.MaxThreads:
					try {
						Integer.parseInt(value);
					} catch (Exception e) {
						throw new InvalidConfigException(String.format(
								"Invalid Max Threads {}", value));
					}
					serverConfigInstance.setProperty(CONFIGS.MaxThreads, value);
					logger.debug("MaxThreads set to {}", value);
					break;
				case CONFIGS.ShutdownGraceTime:
					try {
						Integer.parseInt(value);
					} catch (Exception e) {
						throw new InvalidConfigException(String.format(
								"Invalid Shutdown Grace Time {}", value));
					}
					serverConfigInstance.setProperty(CONFIGS.ShutdownGraceTime,
							value);
					logger.debug("ShutdownGraceTime set to {} seconds", value);
					break;
				case CONFIGS.StopCommand:
					serverConfigInstance
							.setProperty(CONFIGS.StopCommand, value);
					logger.debug("StopCommand set to {}", value);
					break;
				case CONFIGS.KeepAliveTimeout:
					try {
						Integer.parseInt(value);
					} catch (Exception e) {
						throw new InvalidConfigException(String.format(
								"Invalid Keep Alive Timeout {}", value));
					}
					serverConfigInstance
							.setProperty(CONFIGS.KeepAliveTimeout, value);
					logger.debug("KeepAliveTimeout set to {}", value);
					break;
				default:
					throw new InvalidConfigException(String.format(
							"Unknown config parameter {}", name));
				}
			}

		} catch (InvalidConfigException e) {
			logger.error("Invalid configuration", e);
			throw e;
		} catch (Exception e) {
			logger.error("Unknown error while configuring server", e);
			throw new InvalidConfigException("Unknown error");
		}
	}

	// Convenience short-cut methods follow to fetch specific config properties
	
	// HTTP Port
	// Command Port
	// Max threads
	// HTTP version
	// Graceful Shutdown time
	// Stop Command
	// Keep-Alive Timeout
	
	/**
	 * Gets the HTTP port server is listening on
	 * 
	 * @return http port
	 */
	public int httpPort() {
		return Integer.parseInt(getProperty(CONFIGS.HTTPPort));
	}

	/**
	 * Gets the port server is listening on for Command requests
	 * 
	 * @return command port
	 */
	public int commandPort() {
		return Integer.parseInt(getProperty(CONFIGS.CommandPort));
	}

	/**
	 * Gets the maximum number of threads allowed in the system simultaneously
	 * 
	 * @return max threads
	 */
	public int maxThreads() {
		return Integer.parseInt(getProperty(CONFIGS.MaxThreads));
	}

	/**
	 * Gets the HTTP versoin the server is running on
	 * 
	 * @return http version
	 */
	public String httpVersion() {
		return getProperty(CONFIGS.HTTPVersion);
	}

	/**
	 * Gets the shutdown grace time given to existing threads to wrap up in the scenario of a server halt
	 * 
	 * @return shutdown grace time
	 */
	public int shutdownGraceTime() {
		return Integer.parseInt(getProperty(CONFIGS.ShutdownGraceTime));
	}

	/**
	 * Gets the stop command which if sent to the command port will halt the server instance
	 * 
	 * @return stop command
	 */
	public String stopCommand() {
		return getProperty(CONFIGS.StopCommand);
	}

	/**
	 * Convenience method which constructs the entire URL to hit for a server halt
	 * 
	 * @return stop URL
	 */
	public String stopURL() {
		return "http://localhost:" + commandPort() + "/" + stopCommand();
	}
	
	public int keepAliveTimeout() {
		return Integer.parseInt(getProperty(CONFIGS.KeepAliveTimeout));
	}
}
