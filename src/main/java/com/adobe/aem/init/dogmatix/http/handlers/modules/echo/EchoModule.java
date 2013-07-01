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

package com.adobe.aem.init.dogmatix.http.handlers.modules.echo;

import com.adobe.aem.init.dogmatix.exceptions.HttpError;
import com.adobe.aem.init.dogmatix.http.handlers.HttpContext;
import com.adobe.aem.init.dogmatix.http.handlers.modules.AbstractHttpRequestHandlerModule;
import com.adobe.aem.init.dogmatix.http.handlers.modules.Module;
import com.adobe.aem.init.dogmatix.http.handlers.modules.Setting;
import com.adobe.aem.init.dogmatix.http.request.HttpRequest;
import com.adobe.aem.init.dogmatix.http.request.Method;
import com.adobe.aem.init.dogmatix.util.Constants;

/**
 * An Echo module for the server.
 * 
 * Prints the incoming request's body as-is to the response.
 * 
 * Only GET requests are echoed back because requests with entities
 * may be misused to cause OutOfMemory exceptions by POSTing
 * large data to the echo module.
 * 
 * Has optional header and footer settings which are printed above
 * and below, respectively, the echoed request.
 * 
 * This module comes in handy while testing clients.
 */
@Module(url = "echo/**", settings = {
		@Setting(name=EchoModule.SETTINGS.HEADER, value="=========="),
		@Setting(name=EchoModule.SETTINGS.FOOTER, value="==========")
})
public class EchoModule extends AbstractHttpRequestHandlerModule {
	
	public interface SETTINGS {
		String HEADER = "header";
		String FOOTER = "footer";
	}
	
	private static final String ECHOES = 
					"Overhead the albatross hangs motionless upon the air<br />"+
	"And deep beneath the rolling waves in labyrinths of coral caves<br />"+
					"The echo of a distant tide<br />"+
	"Comes willowing across the sand<br />"+
					"And everything is green and summery<br />"+
	"And no one showed us to the land<br />"+
					"And no one knows the where's or why's<br />"+
	"But something stirs and something tries<br />"+
					"And starts to climb towards the light<br />"+
	"<br />"+
					"Strangers passing in the street<br />"+
	"By chance two separate glances meet<br />"+
					"And I am you and what I see is me<br />"+
	"And do I take you by the hand<br />"+
					"And lead you through the land<br />"+
	"And help me understand the best I can<br />"+
					"And no one calls us to move on<br />"+
	"And no one forces down our eyes<br />"+
					"No one speaks<br />"+
	"And no one tries<br />"+
					"No one flies around the sun<br />"+
	"<br />"+
					"Almost every day you fall upon my waking eyes<br />"+
	"Inviting and inciting me to rise<br />"+
					"And through the window in the wall<br />"+
	"Comes streaming in on sunlight wings<br />"+
					"A million bright ambassadors of morning<br />"+
	"And no one sings me lullabies<br />"+
					"And no one makes me close my eyes<br />"+
	"So I throw the windows wide<br />"+
					"And call to you across the sky.";
	
	@Override
	public boolean consume(HttpContext context) {
		HttpRequest request = context.getRequest();
		if(request.getMethod() == Method.GET) {
			StringBuffer response = new StringBuffer();
			response.append(config.getSetting(EchoModule.SETTINGS.HEADER, ""));
			response.append(Constants.NEW_LINE);
			response.append(request.getRaw());
			response.append(Constants.NEW_LINE);
			response.append(config.getSetting(EchoModule.SETTINGS.FOOTER, ""));
			context.getResponse().append(response);
		}
		else {
			context.getResponse().err(new HttpError(405, ECHOES));
		}

		return false;

	}

}
