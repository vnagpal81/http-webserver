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

package com.adobe.aem.init.dogmatix.http.header;

import com.adobe.aem.init.dogmatix.http.handlers.HttpContext;

/**
 * Interface defining the behaviour of a class intercepting the processing of a
 * request. Hooks are provided for pre-processing, which happens <b>before</b>
 * the module consumes the request and for post-processing, which is invoked
 * <b>after</b> the module consumes the request.
 * 
 * @author vnagpal
 * 
 */
public interface HeaderInterceptor {

	/**
	 * To be implemented method which is invoked before the module gets to
	 * consume the request
	 * 
	 * @param ctx
	 *            the context to process
	 * @return true if this method has handled the request and sent out the
	 *         response by itself and no further execution is required, false
	 *         otherwise.
	 */
	boolean preProcess(HttpContext ctx);

	/**
	 * To be implemented method which is invoked after the module has consumed
	 * the request
	 * 
	 * @param ctx
	 *            the context to process
	 * @return true if this method has handled the request and sent out the
	 *         response by itself and no further execution is required, false
	 *         otherwise.
	 */
	boolean postProcess(HttpContext ctx);

	/**
	 * To be implemented method which tests whether this intercepter is
	 * applicable in the given context. Usually done by checking HTTP Request
	 * headers.
	 * 
	 * @param ctx
	 *            the HTTP context
	 * @return
	 */
	boolean applicable(HttpContext ctx);

	/**
	 * Performs a validation of the Header syntax in the HTTP request. Returns
	 * the validity of the syntax of the header in question. Also optionally
	 * writes the error to the response.
	 * 
	 * This method should usually be invoked from preProcess and further
	 * processing should be terminated in case of invalid Header info.
	 * 
	 * @param ctx
	 *            the HTTP context
	 * @param writeResponse
	 *            if true, instructs the interceptor to write the error to
	 *            response if header infor is invalid
	 * @return
	 */
	boolean isValid(HttpContext ctx, boolean writeResponse);

}
