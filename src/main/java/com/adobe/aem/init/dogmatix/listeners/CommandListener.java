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

package com.adobe.aem.init.dogmatix.listeners;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.config.ServerConfig;
import com.adobe.aem.init.dogmatix.core.ServerStatistics;
import com.adobe.aem.init.dogmatix.http.response.HttpResponse;
import com.adobe.aem.init.dogmatix.util.Constants;

/**
 * A listener which listens on a port for incoming Command Requests.
 * A command request is nothing but an HTTP GET Request sent at a
 * specific URI determined by the command.
 * For example, a stop command request will be sent to /STOP
 * 
 * Upon receiving the request, the listener then processes it by performing
 * an action on that command.
 * 
 * Currently, only STOP and STATS commands are understood. Any other 
 * is conveniently ignored.
 * 
 * @author vnagpal
 *
 */
public class CommandListener extends Listener {
	
	Logger logger = LoggerFactory.getLogger(CommandListener.class);
	
	private List<Listener> listeners;
	
	private ServerConfig serverConfig;
	
	public CommandListener(ServerConfig serverConfig, Listener... listeners) {
		super(serverConfig.commandPort());
		this.serverConfig = serverConfig;
		if(listeners != null && listeners.length > 0) {
			if(this.listeners == null) {
				this.listeners = new ArrayList<Listener>();
			}
			for(Listener listener : listeners) {
				registerListener(listener);
			}
		}
	}

	protected void process(Socket socket) throws IOException {
		logger.debug("Processing command");
		BufferedReader in = new BufferedReader(
					new InputStreamReader(
					socket.getInputStream()));

		String inputLine = in.readLine();
		String[] cmd = inputLine.split("\\s");
		String command = cmd[1].substring(1);
		logger.debug("Received command {}", command);
		
		if(command.toLowerCase().contains(serverConfig.stopCommand().toLowerCase())) {
			OutputStream outputStream = socket.getOutputStream();
			HttpResponse response = new HttpResponse(outputStream);
			response
			.status(200)
			.addHeader(Constants.HEADERS.CONTENT_TYPE, "application/json")
			.append("callbackShutdown({})")
			.flush();
			
			cleanup(socket);
			
			//stopListening();
			System.exit(0);
		}
		else if(command.toLowerCase().contains("stats")) {
			String callbackFunc = command.split("\\?")[1];
			//send server statistics in response
			OutputStream outputStream = socket.getOutputStream();
			HttpResponse response = new HttpResponse(outputStream);
			response
			.status(200)
			.addHeader(Constants.HEADERS.CONTENT_TYPE, "application/json")
			.append("callbackServerStats("+ServerStatistics.getStatsAsJSON()+")")
			.flush();
			//out.close();
			cleanup(socket);
		}

	}	
	
	public void registerListener(Listener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void stopListening() {
		logger.debug("Shutting down server...");
		for(Listener listener : listeners) {
			logger.debug("Stopping all registered listeners");
			listener.stopListening();
		}
		logger.debug("Stop listening myself");
		super.stopListening();
		logger.debug("_RIP_");
	}
	
	private void cleanup(Socket socket) throws IOException {
		socket.getOutputStream().close();
	}
}
