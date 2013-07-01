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

import java.io.FileInputStream;
import java.io.IOException;

import com.adobe.aem.init.dogmatix.http.request.FileUpload;

/**
 * Represents a file repository on an Amazon S3 server.
 * @see http://aws.amazon.com/s3/
 * 
 * Parameters required to connect to a S3 Repository:
 * 
 * Access Key	:	userid
 * Secret Key	:	password
 * Endpoint URL	:	where to connect to?
 * Base Bucket	:	name of the bucket in which all files will be read/written
 * 
 * @author vnagpal
 *
 */
public class S3Repository implements Repository {

	@Override
	public FileInputStream lookup(String uri) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void create(FileUpload fileUpload) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean exists(String uri) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void delete(String uri) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Metadata getInfo(String uri) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileInputStream lookin(String parentPath, String fileName)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
