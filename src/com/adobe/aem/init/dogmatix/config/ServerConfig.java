package com.adobe.aem.init.dogmatix.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.adobe.aem.init.dogmatix.exceptions.InvalidConfigException;
import com.adobe.aem.init.dogmatix.http.handlers.modules.ModuleFactory;
import com.adobe.aem.init.dogmatix.http.request.Version;
import com.adobe.aem.init.dogmatix.util.XmlUtils;

@SuppressWarnings("serial")
public class ServerConfig extends Properties {

	private static final Logger logger = LoggerFactory
			.getLogger(ServerConfig.class);

	private static ServerConfig serverConfigInstance = null;

	public static interface TAGS {
		String HTTPPort = "HTTPPORT";
		String CommandPort = "COMMANDPORT";
		String HTTPVersion = "HTTPVERSION";
		String MaxThreads = "MAXTHREADS";
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
	 * @source http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser
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
			List<String> moduleClasses = new ArrayList<String>();
			NodeList modules = ((Element)doc.getElementsByTagName("modules").item(0)).getElementsByTagName("module");
			for (int i = 0; i < modules.getLength(); i++) {
				Node module = modules.item(i);
				moduleClasses.add(module.getTextContent());
			}
			logger.debug("Load module classes");
			ModuleFactory.load(moduleClasses);

			logger.debug("Read configs from xml");
			NodeList configs = ((Element)doc.getElementsByTagName("configs").item(0)).getElementsByTagName("config");
			for (int i = 0; i < configs.getLength(); i++) {
				Element config = (Element) configs.item(i);
				String name = config.getAttribute("name");
				String value = config.getAttribute("value");
				switch (name.toUpperCase()) {
				case TAGS.HTTPVersion:
					try {
						Version.getVersion(value);
					} catch (Exception e) {
						throw new InvalidConfigException(String.format(
								"Invalid HTTPVersion {}", value));
					}
					serverConfigInstance.setProperty(TAGS.HTTPVersion, value);
					logger.debug("HTTPVersion set to {}", value);
					break;
				case TAGS.CommandPort:
					try {
						if (!available(Integer.parseInt(value))) {
							throw new InvalidConfigException(String.format(
									"Port {} already in use", value));
						}
					} catch (Exception e) {
						throw new InvalidConfigException("Invalid Command Port");
					}
					serverConfigInstance.setProperty(TAGS.CommandPort, value);
					logger.debug("CommandPort set to {}", value);
					break;
				case TAGS.HTTPPort:
					try {
						if (!available(Integer.parseInt(value))) {
							throw new InvalidConfigException(String.format(
									"Port {} already in use", value));
						}
					} catch (Exception e) {
						throw new InvalidConfigException("Invalid HTTP Port");
					}
					serverConfigInstance.setProperty(TAGS.HTTPPort, value);
					logger.debug("HTTPPort set to {}", value);
					break;
				case TAGS.MaxThreads:
					try {
						Integer.parseInt(value);
					} catch (Exception e) {
						throw new InvalidConfigException(String.format(
								"Invalid Max Threads {}", value));
					}
					serverConfigInstance.setProperty(TAGS.MaxThreads, value);
					logger.debug("MaxThreads set to {}", value);
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

	private static boolean available(int port) {
		try (Socket ignored = new Socket("localhost", port)) {
			return false;
		} catch (IOException ignored) {
			return true;
		}
	}

	// HTTP Port
	// Command Port
	// Max threads
	// HTTP version

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
