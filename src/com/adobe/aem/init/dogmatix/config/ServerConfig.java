package com.adobe.aem.init.dogmatix.config;

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
import com.adobe.aem.init.dogmatix.util.NetworkUtils;
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

	private static final Logger logger = LoggerFactory
			.getLogger(ServerConfig.class);

	private static ServerConfig serverConfigInstance = null;

	public static interface CONFIGS {
		String HTTPPort = "HTTPPort";
		String CommandPort = "CommandPort";
		String HTTPVersion = "HTTPVersion";
		String MaxThreads = "MaxThreads";
		String ShutdownGraceTime = "ShutdownGraceTime";
		String StopCommand = "StopCommand";
	}

	private ServerConfig() {

	}

	public static ServerConfig getInstance() throws InvalidConfigException {

		if (serverConfigInstance == null) {
			buildServerConfig();
		}

		return serverConfigInstance;

	}

	/**
	 * @source 
	 *         http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser
	 * 
	 */
	private static void buildServerConfig() throws InvalidConfigException {
		// read server.xml
		try {
			logger.debug("Create ServerConfig Object");
			serverConfigInstance = new ServerConfig();

			logger.debug("Read server.xml from classpath");
			InputStream xmlStream = ServerConfig.class.getClassLoader()
					.getResourceAsStream("server.xml");
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

			logger.debug("Read modules from xml");
			NodeList modules = XmlUtils.lookup(doc, "modules>module", ">");
			List<ModuleConfig> moduleConfigs = new ArrayList<ModuleConfig>();
			for (int i = 0; i < modules.getLength(); i++) {
				Element module = (Element) modules.item(i);
				String className = XmlUtils.text(module, "class", null).get(0);
				String url = XmlUtils.text(module, "url", null).get(0);
				ModuleConfig moduleConfig = new ModuleConfig(className, url);
				// read module level settings
				moduleConfigs.add(moduleConfig);
			}
			if (moduleConfigs.size() > 0) {
				logger.debug("Load module classes");
				ModuleFactory.load(moduleConfigs);
			}
			logger.debug("Read module scan path from xml");
			List<String> scanPaths = XmlUtils.text(doc, "modules>scan", ">");
			for (String scanPath : scanPaths) {
				logger.debug("Scan for modules at {}", scanPath);
				ModuleFactory.annotatedLoad(scanPath);
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
						if (!NetworkUtils.available(Integer.parseInt(value))) {
							throw new InvalidConfigException(String.format(
									"Port {} already in use", value));
						}
					} catch (Exception e) {
						throw new InvalidConfigException("Invalid Command Port");
					}
					serverConfigInstance
							.setProperty(CONFIGS.CommandPort, value);
					logger.debug("CommandPort set to {}", value);
					break;
				case CONFIGS.HTTPPort:
					try {
						if (!NetworkUtils.available(Integer.parseInt(value))) {
							throw new InvalidConfigException(String.format(
									"Port {} already in use", value));
						}
					} catch (Exception e) {
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
					} catch(Exception e) {
						throw new InvalidConfigException(String.format(
								"Invalid Shutdown Grace Time {}", value));
					}
					serverConfigInstance.setProperty(CONFIGS.ShutdownGraceTime, value);
					logger.debug("ShutdownGraceTime set to {} seconds", value);
					break;
				case CONFIGS.StopCommand:
					serverConfigInstance.setProperty(CONFIGS.StopCommand, value);
					logger.debug("StopCommand set to {}", value);
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
			throw new InvalidConfigException();
		}
	}

	// HTTP Port
	// Command Port
	// Max threads
	// HTTP version
	// Graceful Shutdown time
	// Stop Command

	public int httpPort() {
		return Integer.parseInt(getProperty(CONFIGS.HTTPPort, "8080"));
	}

	public int commandPort() {
		return Integer.parseInt(getProperty(CONFIGS.CommandPort, "5250"));
	}

	public int maxThreads() {
		return Integer.parseInt(getProperty(CONFIGS.MaxThreads, "100"));
	}

	public String httpVersion() {
		return getProperty(CONFIGS.HTTPVersion, "1.1");
	}
	
	public int shutdownGraceTime() {
		return Integer.parseInt(getProperty(CONFIGS.ShutdownGraceTime, "60"));
	}

	public String stopCommand() {
		return getProperty(CONFIGS.StopCommand, "stop");
	}
}
