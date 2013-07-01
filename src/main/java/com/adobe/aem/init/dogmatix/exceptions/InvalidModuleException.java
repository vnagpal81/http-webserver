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
 * Thrown while attempting to instantiate and initialize a Module class.
 * All {@link InvalidModuleConfigException}s thrown are ultimately
 * caught and rethrown as an {@link InvalidModuleConfigException}
 * 
 * Has a moduleName to identify the module class which was deemed invalid.
 * 
 * @author vnagpal
 *
 */
@SuppressWarnings("serial")
public class InvalidModuleException extends Exception {
	
	private String moduleName;
	
	public InvalidModuleException(String moduleName) {
		this.moduleName = moduleName;
	}

	public InvalidModuleException(String moduleName, Throwable cause) {
		super(cause);
		this.moduleName = moduleName;
	}
	
	

}
