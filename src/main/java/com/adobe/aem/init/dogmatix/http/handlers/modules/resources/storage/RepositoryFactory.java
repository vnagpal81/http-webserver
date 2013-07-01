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

import java.util.Properties;

import com.adobe.aem.init.dogmatix.http.handlers.modules.resources.ResourcesModule;

/**
 * A Factory class responsible for generating {@link Repository} instances.
 * The type of {@link Repository} to create is determined by settings passed as an argument.
 * These settings also contain the connection parameters required to connect to any
 * repository while initializing.
 * 
 * @see LocalRepository
 * @see S3Repository
 * @see RemoteRepository
 * @see FTPRepository
 * 
 * @author vnagpal
 *
 */
public class RepositoryFactory {

	public static Repository getRepository(Properties settings) throws RepositoryNotAccessibleException {
		RepositoryType type = RepositoryType.valueOf(settings.getProperty(ResourcesModule.SETTINGS.REPOSITORY).toUpperCase());
		Repository repository = null;
		switch(type) {
		case FTP:
			repository = new FTPRepository();
			break;
		case LOCAL:
			repository = new LocalRepository(settings.getProperty(ResourcesModule.SETTINGS.BASE_DIR));
			break;
		case REMOTE:
			repository = new RemoteRepository();
			break;
		case S3:
			repository = new S3Repository();
			break;
		default:
			repository = new LocalRepository(settings.getProperty(ResourcesModule.SETTINGS.BASE_DIR));
			break;
		}
		return repository;
	}

}
