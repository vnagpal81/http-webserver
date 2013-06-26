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

package com.adobe.aem.init.dogmatix.http.request;

/**
 * HTTP protocol defines a set of request methods. A client can use one of these
 * request methods to send a request message to an HTTP server. The methods are:
 * 
 * GET: A client can use the GET request to get a web resource from the server.
 * 
 * HEAD: A client can use the HEAD request to get the header that a GET request
 * would have obtained. Since the header contains the last-modified date of the
 * data, this can be used to check against the local cache copy. 
 * 
 * POST: Used to post data up to the web server. 
 * 
 * PUT: Ask the server to store the data.
 * 
 * DELETE: Ask the server to delete the data. 
 * 
 * TRACE: Ask the server to return a diagnostic trace of the actions it takes. 
 * 
 * OPTIONS: Ask the server to return the list of request methods it supports. 
 * 
 * CONNECT: Used to tell a proxy to make a connection to another host and simply 
 * reply the content, without attempting to parse or cache it. This is often used 
 * to make SSL connection through the proxy. 
 * 
 * Other extension methods.
 * 
 * @author vnagpal
 * 
 */
public enum Method {

	GET, PUT, POST, DELETE, TRACE, CONNECT, HEAD, OPTIONS

}
