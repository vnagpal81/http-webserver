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
 * A fine grained version of the {@link InvalidConfigException} thrown by
 * {@link AbstractHttpRequestHandlerModule.init()} when there is a error
 * initializing modules.
 * The cause, though, is module specific but signifies that the module settings
 * are not as expected.
 * @see ResourcesModule.init()
 * 
 * @author vnagpal
 * 
 */
public class InvalidModuleConfigException extends Exception {

	public InvalidModuleConfigException() {
		super();
	}

	public InvalidModuleConfigException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidModuleConfigException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidModuleConfigException(String message) {
		super(message);
	}

	public InvalidModuleConfigException(Throwable cause) {
		super(cause);
	}

}
