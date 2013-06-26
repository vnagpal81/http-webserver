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
 * Defines a REST handler.
 * 
 * Has methods corresponding each of the HTTP {@link Method}s and a default 
 * method handle() in case an unknown HTTP method is encountered.
 * 
 * @author vnagpal
 *
 */
public interface Handler {

	void handle(HttpRequest request, HttpResponse response) throws HttpError;
	
	void GET(HttpRequest request, HttpResponse response) throws HttpError;
	
	void PUT(HttpRequest request, HttpResponse response) throws HttpError;
	
	void POST(HttpRequest request, HttpResponse response) throws HttpError;
	
	void DELETE(HttpRequest request, HttpResponse response) throws HttpError;
	
	void TRACE(HttpRequest request, HttpResponse response) throws HttpError;
	
	void OPTIONS(HttpRequest request, HttpResponse response) throws HttpError;
	
	void CONNECT(HttpRequest request, HttpResponse response) throws HttpError;
	
	void HEAD(HttpRequest request, HttpResponse response) throws HttpError;
}
