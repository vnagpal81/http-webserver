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

package com.adobe.aem.init.dogmatix.http.handlers.modules.proxy;

import com.adobe.aem.init.dogmatix.http.handlers.HttpContext;
import com.adobe.aem.init.dogmatix.http.handlers.modules.AbstractHttpRequestHandlerModule;
import com.adobe.aem.init.dogmatix.http.handlers.modules.Module;

/**
 * A Proxy module bears the responsibility of delegating the request to a 
 * backend server URL specified in the settings.
 * Basically, it acts as an intermediary for requests from clients seeking 
 * resources from other servers. 
 * 
 * Modeled on the apache httpd module mod_proxy
 * 
 * @author vnagpal
 *
 */
@Module(url="proxy/*")
public class ProxyServerModule extends AbstractHttpRequestHandlerModule {

	@Override
	public boolean consume(HttpContext context) {
		return false;
		// TODO Auto-generated method stub
		
	}

}
