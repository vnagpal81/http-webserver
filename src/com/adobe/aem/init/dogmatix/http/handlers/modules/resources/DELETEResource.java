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
import com.adobe.aem.init.dogmatix.http.request.HttpRequest;
import com.adobe.aem.init.dogmatix.http.response.HttpResponse;

/**
 * A resource request processor which processes DELETE requests for a resources
 * module.
 * 
 * Delegates the actual CRUD operations to a repository as configured in the
 * module level settings.
 * 
 * @author vnagpal
 * 
 */
public class DELETEResource extends ResourcesRequestProcessor {

	@Override
	public void processRequest(HttpRequest request, HttpResponse response)
			throws HttpError {

	}

}
