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

package com.adobe.aem.init.dogmatix.exceptions;

/**
 * Thrown when the server is started with an invalid configuration.
 * May occur if the server.xml does not confirm to the XML schema definition
 * OR incorrect data values against any of the configs/settings.
 * 
 * Thrown when {@link ServerConfig} is being loaded at the time of 
 * server startup.
 * 
 * @author vnagpal
 *
 */
@SuppressWarnings(value = "serial")
public class InvalidConfigException extends Exception {

	public InvalidConfigException(String message, Throwable t) {
		super(message, t);
	}
	
	public InvalidConfigException(String message) {
		super(message);
	}

	public InvalidConfigException() {
		super();
	}

}
