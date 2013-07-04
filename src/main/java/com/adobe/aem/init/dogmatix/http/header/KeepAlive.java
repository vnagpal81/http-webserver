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

import java.io.IOException;
import java.net.SocketException;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.adobe.aem.init.dogmatix.config.ServerConfig;
import com.adobe.aem.init.dogmatix.core.ReusableSocket;
import com.adobe.aem.init.dogmatix.http.handlers.HttpContext;
import com.adobe.aem.init.dogmatix.http.request.HttpRequest;
import com.adobe.aem.init.dogmatix.http.request.Version;
import com.adobe.aem.init.dogmatix.http.response.HttpResponse;
import com.adobe.aem.init.dogmatix.util.Constants;

/**
 * Intercepts the request processing and performs Keep-Alive behaviour, if
 * required. Should be used at the end of the interceptor chain as the last
 * interceptor in case the underlying I/O streams are closed.
 * 
 * @author vnagpal
 * 
 */
public class KeepAlive implements HeaderInterceptor {
	
	private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
	private static final Logger logger = LoggerFactory.getLogger(KeepAlive.class);
	
	private int timeout = -1;
	private int max = -1;

	@Override
	public boolean preProcess(HttpContext ctx) {
		if(!isValid(ctx, true)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean postProcess(HttpContext ctx) {
		ReusableSocket socket = ctx.getSocket();
		HttpRequest request = ctx.getRequest();
		HttpResponse response = ctx.getResponse();
		
		if (applicable(ctx)) {
			logger.debug("Checking keep-alive behaviour");
			String connection = request.getHeader(Constants.HEADERS.CONNECTION);
			boolean keepAlive = connection != null && connection.equalsIgnoreCase("Keep-Alive");

			ServerConfig serverConfig = ServerConfig.getInstance();
			Version version = Version.getVersion(serverConfig.httpVersion());

			//keep alive behaviour is protocol version specific
			switch (version) {
			case VERSION_0_9:
				break;
			case VERSION_1_0:
				if (keepAlive) {
					logger.debug("{} Keeping connection alive", version);
					socket.setPersist(true);
				}
				break;
			case VERSION_1_1:
				if (keepAlive) {
					if (max == -1) {//if max is not set then always persist
						socket.setPersist(true);
					} else if (socket.getCount() >= max) {//if max is set, persist only if max is not reached
						logger.debug("{} Not keeping connection alive because max={} is reached", version, max);
						socket.setPersist(false);
					} else {
						socket.setPersist(true);
					}
					
					if(socket.isPersist()) {
						if (timeout == -1) {//if timeout is not set then always persist till server keepAliveTimeout occurs
							int to = serverConfig.keepAliveTimeout();
							logger.debug("{} Keeping connection alive. Will timeout after {} seconds", version, to);
							socket.setPersist(true);
							try {
								socket.getSocket().setSoTimeout(to);
							} catch (SocketException e) {
							}
							worker.schedule(new KeepAliveTimeoutCleaner(socket, to), to, TimeUnit.SECONDS);
						} else {//if timeout is set, persist only if timeout is not reached
							logger.debug("{} Keeping connection alive. Will timeout after {} seconds", version, timeout);
							socket.setPersist(true);
							try {
								socket.getSocket().setSoTimeout(timeout);
							} catch (SocketException e) {
							}
							worker.schedule(new KeepAliveTimeoutCleaner(socket, timeout), timeout, TimeUnit.SECONDS);
						}
					}
				}
				break;
			default:
				break;
			}
		}
		
		
		if(socket.isPersist()) {
			logger.debug("Requesting client to keep connection alive");
			response.addHeader(Constants.HEADERS.CONNECTION, "Keep-Alive");
		}
		else {
			logger.debug("Requesting client to close the connection");
			response.addHeader(Constants.HEADERS.CONNECTION, "Close");
		}
		
		return false;
	}

	/**
	 * Checks if keep-alive behaviour should be applied to this context or not.
	 * If response is 3xx(redirection), 4xx(client error) or 5xx(server error)
	 * server should not keep-alive. OTOH, if response is 1xx(informational) or
	 * 2xx(success), server may keep-alive depending on its policy and request
	 * headers.
	 */
	@Override
	public boolean applicable(HttpContext ctx) {
		return ctx.getResponse().getStatus() < 300;
	}

	/**
	 * Validates Header syntax and initializes 'timeout' and 'max' values
	 */
	@Override
	public boolean isValid(HttpContext ctx, boolean writeResponse) {
		String keepAliveHeader = ctx.getRequest().getHeader(
				Constants.HEADERS.KEEP_ALIVE);
		if (StringUtils.hasText(keepAliveHeader)) {
			for (String part : keepAliveHeader.split(",")) {
				if (part.toLowerCase().contains("timeout")
						&& part.contains("=")) {
					try {
						timeout = Integer.parseInt(part.split("=")[1]);
					} catch (NumberFormatException e) {
						// log
						if(writeResponse) {
							ctx.getResponse().err(400);
						}
						return false;
					}
				} else if (part.toLowerCase().contains("max")
						&& part.contains("=")) {
					try {
						max = Integer.parseInt(part.split("=")[1]);
					} catch (NumberFormatException e) {
						// log
						if(writeResponse) {
							ctx.getResponse().err(400);
						}
						return false;
					}
				}
			}
		}
		
		return true;
	}

	
	public static class KeepAliveTimeoutCleaner implements Runnable {
		private static final Logger logger = LoggerFactory.getLogger(KeepAliveTimeoutCleaner.class);
		private ReusableSocket socket;
		private int timeout;

		public KeepAliveTimeoutCleaner(ReusableSocket socket, int timeout) {
			super();
			this.socket = socket;
			this.timeout = timeout;
		}

		@Override
		public void run() {
			try {
				long idleTime = (System.currentTimeMillis() - socket.getLastAccess()) / 1000;
				if (idleTime >= timeout) {
					logger.debug("Connection {} has been idle for {} seconds", socket.getSocket().getInetAddress(), idleTime);
					logger.debug("Closing connection {}", socket.getSocket().getInetAddress());
					socket.close();
				}
				
			} catch (IOException e) {
				logger.error("Error closing connection {}", socket.getSocket().getInetAddress());
			}
		}
		
	}
}
