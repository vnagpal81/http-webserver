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

package com.adobe.aem.init.dogmatix.http.handlers.modules.rest;

import com.adobe.aem.init.dogmatix.exceptions.HttpError;
import com.adobe.aem.init.dogmatix.http.request.HttpRequest;
import com.adobe.aem.init.dogmatix.http.response.HttpResponse;

/**
 * Base skeletal implementation of a REST handler.
 * All method implementations throw a 405 Unsupported error.
 * 
 * REST Handlers written by extending this BaseHandler must ensure overriding
 * those methods which they will be supporting and writing their logic.
 * 
 * @author vnagpal
 *
 */
public class BaseHandler implements Handler {

	@Override
	public void handle(HttpRequest request, HttpResponse response)
			throws HttpError {
		throw new HttpError(405);
	}

	@Override
	public void GET(HttpRequest request, HttpResponse response)
			throws HttpError {
		throw new HttpError(405);
	}

	@Override
	public void PUT(HttpRequest request, HttpResponse response)
			throws HttpError {
		throw new HttpError(405);
	}

	@Override
	public void POST(HttpRequest request, HttpResponse response)
			throws HttpError {
		throw new HttpError(405);
	}

	@Override
	public void DELETE(HttpRequest request, HttpResponse response)
			throws HttpError {
		throw new HttpError(405);
	}

	@Override
	public void TRACE(HttpRequest request, HttpResponse response)
			throws HttpError {
		throw new HttpError(405);
	}

	@Override
	public void OPTIONS(HttpRequest request, HttpResponse response)
			throws HttpError {
		throw new HttpError(405);
	}

	@Override
	public void CONNECT(HttpRequest request, HttpResponse response)
			throws HttpError {
		throw new HttpError(405);
	}

	@Override
	public void HEAD(HttpRequest request, HttpResponse response)
			throws HttpError {
		throw new HttpError(405);
	}

}
