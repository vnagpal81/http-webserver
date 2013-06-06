package com.adobe.aem.init.dogmatix.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlUtils {
	
	private static final Logger logger = LoggerFactory
			.getLogger(XmlUtils.class);
	
	public static String read(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(is));
		String line;
		StringBuffer inputXml = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			inputXml.append(line);
			logger.debug(line);
		}
		return inputXml.toString();
	}
	
	public static boolean validate(String inputXml, InputStream xsd) {
		boolean isValid = true;

		try {
			// build the schema
			SchemaFactory factory = SchemaFactory
					.newInstance("http://www.w3.org/2001/XMLSchema");
			Schema schema = factory.newSchema(new StreamSource(xsd));
			Validator validator = schema.newValidator();

			// create a source from the input stream
			Source source = new StreamSource(new StringReader(inputXml));

			// check input
			validator.validate(source);
		} catch (Exception e) {
			logger.error("Error validating server.xml against schema", e);
			isValid = false;
		}

		return isValid;
	}
	
	public static Document parse(String inputXml, boolean normalize) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new InputSource(new StringReader(inputXml)));
		if(normalize) {
			logger.debug("Normalize XML Document");
			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();
		}
		return doc;
	}

}
