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

package com.adobe.aem.init.dogmatix.http.handlers.modules.resources.storage;

import java.io.IOException;

/**
 * Thrown when a class attempts an operation on a {@link Repository} and fails
 * because the repository was not available.
 * 
 * As a fail-safe mechanism, it is recommended to not delay the detection of
 * {@link Repository} connection failure till an operation is attempted. It should be
 * detected at application startup time by eager initialization.
 * 
 * This, however, does not guarantee the availability of the underlying {@link Repository}
 * at all times. An application may still encounter a {@link RepositoryNotAccessibleException}
 * while attempting an operation at a later point of time.
 * 
 * @see RepositoryFactory
 * @see RemoteRepository
 * @see FTPRepository
 * @see S3Repository
 * 
 * @author vnagpal
 *
 */
public class RepositoryNotAccessibleException extends IOException {

	public RepositoryNotAccessibleException(Throwable cause) {
		super(cause);
	}

	public RepositoryNotAccessibleException() {
		super();
	}

	public RepositoryNotAccessibleException(String message) {
		super(message);
	}
}
