package com.adobe.aem.init.dogmatix.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Util class exposing routines for XML parsing, reading, validation and traversing.
 * 
 * @author vnagpal
 *
 */
public class XmlUtils {
	
	private static final Logger logger = LoggerFactory
			.getLogger(XmlUtils.class);
	
	/**
	 * Reads the XML input file line by line and returns a String containing the contents
	 * 
	 * @param is the inputStream pointing to the XML source
	 * @return the XML file content in a String
	 * @throws IOException
	 */
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
	
	
	/**
	 * Validates the inputXml string against an XML Schema Definition file
	 * 
	 * @param inputXml XML string to validate
	 * @param xsd the inputStream pointing to the XSD file containing the schema definition
	 * @return true if the XML confirms to the schema defined in a valid XSD, false otherwise.
	 */
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
	
	/**
	 * Convert a String with XML content into a Document object model.
	 * 
	 * @param inputXml the string to parse
	 * @param normalize boolean to instruct parser to normalize the DOM tree. If true, coalesces the Text Nodes. {@code Node.normalize()}
	 * @return the org.w3c.Document DOM tree representation of the XML string passed in
	 * @throws SAXException thrown when the document builder SAX parser encounters any error
	 * @throws IOException thrown if any I/O error occurs while reading the input XML
	 * @throws ParserConfigurationException thrown if any error encountered while configuring the parser in the document builder
	 */
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

	/**
	 * Traverses the DOM tree to lookup the XPath.
	 * 
	 * A sample XPath would look like "Parent > Child > Grandchild" for a DOM tree as following:
	 * 
	 * <pre>
	 * <Parent>
	 * 		<Child>
	 * 			<Grandchild>
	 * 			</Grandchild>
	 * 		</Child>
	 * </Parent>
	 * </pre>
	 * 
	 * @param doc The DOM tree to traverse
	 * @param xPath A path to lookup
	 * @param separator String separator used to break up level hierarchy with in the path
	 * @return Node(s) found at the given path
	 */
	public static NodeList lookup(Document doc, String xPath, String separator) {
		String[] trail = xPath.split(separator);
		Element parent = doc.getDocumentElement();
		for(int i = 0; i < trail.length - 1; i++) {
			parent = (Element) parent.getElementsByTagName(trail[i].trim()).item(0);
		}
		return parent.getElementsByTagName(trail[trail.length-1].trim());
	}
	
	/**
	 * Gives the string representation of the XML element at the specified XPath in the DOM tree
	 * 
	 * @param doc The DOM tree to traverse
	 * @param xPath A path to lookup
	 * @param separator String separator used to break up level hierarchy with in the path
	 * @return List<String> List of string representations of the Node(s) found at the given path
	 */
	public static List<String> text(Document doc, String xPath, String separator) {
		NodeList nodes = lookup(doc, xPath, separator);
		List<String> texts = new ArrayList<String>();
		for(int i = 0; i < nodes.getLength(); i++) {
			texts.add(nodes.item(i).getTextContent());
		}
		return texts;
	}
}
