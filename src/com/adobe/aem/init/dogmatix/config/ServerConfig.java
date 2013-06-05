package com.adobe.aem.init.dogmatix.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.adobe.aem.init.dogmatix.exceptions.InvalidConfigException;
import com.adobe.aem.init.dogmatix.http.request.Version;

@SuppressWarnings("serial")
public class ServerConfig extends Properties {

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
	 * @source http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
	 *   
	 */
	private static void buildServerConfig() throws InvalidConfigException {
		// read server.xml
		try {
			serverConfigInstance = new ServerConfig();
			InputStream is = ServerConfig.class
					.getResourceAsStream("server.xml");

			if (!validate(is)) {
				throw new InvalidConfigException("Xml not well formed");
			}

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			NodeList configs = doc.getElementsByTagName("configs").item(0)
					.getChildNodes();
			for (int i = 0; i < configs.getLength(); i++) {
				Element config = (Element) configs.item(i);
				String name = config.getAttribute("name");
				String value = config.getAttribute("value");
				switch (name.toUpperCase()) {
				case TAGS.HTTPVersion:
					try {
						Version.valueOf(value);
					}
					catch(Exception e) {
						throw new InvalidConfigException("Invalid HTTPVersion");
					}
					serverConfigInstance.setProperty(TAGS.HTTPVersion, value);
					break;
				case TAGS.CommandPort:
					try {
						if(!available(Integer.parseInt(value))) {
							throw new InvalidConfigException(String.format("Port %s already in use", value));
						}
					}
					catch(Exception e) {
						throw new InvalidConfigException("Invalid Command Port");
					}
					serverConfigInstance.setProperty(TAGS.CommandPort, value);
					break;
				case TAGS.HTTPPort:
					try {
						if(!available(Integer.parseInt(value))) {
							throw new InvalidConfigException(String.format("Port %s already in use", value));
						}
					}
					catch(Exception e) {
						throw new InvalidConfigException("Invalid HTTP Port");
					}
					serverConfigInstance.setProperty(TAGS.HTTPPort, value);
					break;
				case TAGS.MaxThreads:
					try {
						Integer.parseInt(value);
					}
					catch(Exception e) {
						throw new InvalidConfigException("Invalid Max Threads");
					}
					serverConfigInstance.setProperty(TAGS.MaxThreads, value);
					break;
				default:
					throw new InvalidConfigException(String.format(
							"Unknown config parameter %s", name));
				}
			}

		} catch (Exception e) {
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
	
	private static boolean validate(InputStream inputXml) {

		boolean isValid = true;

		try {
			// build the schema
			SchemaFactory factory = SchemaFactory
					.newInstance("http://www.w3.org/2001/XMLSchema");
			File schemaFile = new File("server.xsd");
			Schema schema = factory.newSchema(schemaFile);
			Validator validator = schema.newValidator();

			// create a source from the input stream
			Source source = new StreamSource(new InputStreamReader(inputXml));

			// check input
			validator.validate(source);
		} catch (Exception e) {
			isValid = false;
		}

		return isValid;
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
