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
import java.net.Socket;
import java.net.URL;

/**
 * Utility class for various network related routines
 * 
 * @author vnagpal
 *
 */
public class NetworkUtils {

	/**
	 * Checks if a port is available by attempting to create a socket 
	 * 
	 * @param port the port on which to check
	 * @return true, if socket creation failed i.e. port is available
	 * 		   false, if socket creation succeeded i.e. port is in use
	 */
	public static boolean available(int port) {
		try {
			Socket ignored = new Socket("localhost", port);
			ignored.close();
			return false;
		} catch (IOException ignored) {
			return true;
		}
	}
	
	/**
	 * Pings a url via HTTP by sending an empty GET request.
	 * 
	 * @param url the url to ping
	 * @return true, if the ping was successful
	 * 		   false, if the accessing the url gave an error
	 */
	public static boolean ping(String url) {
		try {
			new URL(url).openStream().close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
