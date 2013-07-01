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

package com.adobe.aem.init.dogmatix.http.handlers.modules.resources;

import com.adobe.aem.init.dogmatix.exceptions.HttpError;
import com.adobe.aem.init.dogmatix.http.handlers.modules.RequestProcessor;
import com.adobe.aem.init.dogmatix.http.handlers.modules.resources.storage.Repository;
import com.adobe.aem.init.dogmatix.http.request.HttpRequest;
import com.adobe.aem.init.dogmatix.http.response.HttpResponse;

/**
 * Defines a Processor whose responsibility is to process the {@link HttpRequest}
 * received by a {@link ResourcesModule}
 * 
 * Since serving a resource request requires accessing an underlying {@link Repository},
 * a Processor also implements a builder pattern to set the {@link Repository}
 * 
 * A urlPattern is also needed to process any resources request. This is the base context 
 * path in the URI which is mapped to the {@link ResourcesModule}. The resource path is 
 * to be determined by stripping of the module's urlPattern mapping from the URI in the request
 * to get the actual URI.
 * 
 * @author vnagpal
 *
 */
public abstract class ResourcesRequestProcessor implements RequestProcessor {
	
	protected Repository repository;
	
	protected String urlPattern;

	public ResourcesRequestProcessor setRepository(Repository repository) {
		this.repository = repository;
		return this;
	}
	
	public ResourcesRequestProcessor setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
		return this;
	}

	@Override
	public abstract void processRequest(HttpRequest request, HttpResponse response) throws HttpError;
}
