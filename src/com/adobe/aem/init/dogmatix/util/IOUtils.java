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

package com.adobe.aem.init.dogmatix.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class to provide I/O routines
 * 
 * @author vnagpal
 *
 */
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
	
	/**
	 * Reads the specified number of bytes from an input stream
	 * 
	 * @param inputStream the input stream to read from
	 * @param length the number of bytes to read
	 * @return an array of bytes read
	 * @throws IOException thrown if any error occurs while reading
	 */
	public static byte[] read(InputStream inputStream, int length) throws IOException {
		byte[] buffer = new byte[length];
		inputStream.read(buffer, 0, length);
		return buffer;
	}
}
