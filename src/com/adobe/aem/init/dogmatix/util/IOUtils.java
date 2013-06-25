package com.adobe.aem.init.dogmatix.util;

import java.io.IOException;
import java.io.InputStream;

public class IOUtils {
	
	private static final char LF = '\n';
	private static final char CR = '\r';
	
	/**
	 * Read a line from an input stream. A line is ended with \n or \r\n
	 *  
	 * @param in the input stream to read from
	 * @return the line as a string of characters
	 * @throws IOException thrown if any error encountered while reading
	 */
	public static String readLine(InputStream in) throws IOException {
		String line = "";

		int b = in.read();
		char c = (char) b;

		while (b != -1 && c != LF) {
			line = line + Character.toString(c);
			c = (char) (in.read());
		}
		return line.substring(0, line.lastIndexOf(CR));
	}
	
	public static byte[] read(InputStream inputStream, int length) throws IOException {
		byte[] buffer = new byte[length];
		inputStream.read(buffer, 0, length);
		return buffer;
	}
}
