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

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.config.ServerConfig;
import com.adobe.aem.init.dogmatix.http.handlers.HttpRequestHandler;

/**
 * A listener which listens on a port for incoming HTTP Requests. Each request
 * is served in a new thread so as to keep the main thread free. The execution
 * is implemented using {@link ExecutorService} The {@link ThreadPoolExecutor}
 * implementation maintains a core pool of threads the size of which can be
 * extended upto the maxPoolSize. By default the Executor fills up a queue with
 * incoming tasks before creating a new worker thread. To overcome this
 * behaviour, we declare a pool with core pool size as maxThreads and allowing
 * these threads to time out in case of inactivity for keepAlive time units.
 * 
 * @author vnagpal
 * 
 */
public class HttpListener extends Listener {

	protected static Logger logger = LoggerFactory
			.getLogger(HttpListener.class);

	private ThreadPoolExecutor pool;

	private ServerConfig serverConfig;

	public HttpListener(ServerConfig serverConfig) {
		super(serverConfig.httpPort());
		pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(serverConfig
				.maxThreads());
		pool.setKeepAliveTime(1, TimeUnit.MINUTES);
		pool.allowCoreThreadTimeOut(true);
		this.serverConfig = serverConfig;
	}

	@Override
	protected void process(Socket socket) throws IOException {
		pool.execute(new HttpRequestHandler(socket));
	}

	@Override
	public void stopListening() {
		shutdownAndAwaitTermination();
		super.stopListening();
	}

	/**
	 * @see http://docs.oracle.com/javase/6/docs/api/java/util/concurrent/
	 *      ExecutorService.html
	 */
	private void shutdownAndAwaitTermination() {
		logger.debug("Do not entertain any new requests");
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			logger.debug("Waiting for existing requests to finish");
			if (!pool.awaitTermination(serverConfig.shutdownGraceTime(),
					TimeUnit.SECONDS)) {
				logger.debug("Cannot wait any longer. Shutting down forcefully now. Any unserved request will be dropped");
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(serverConfig.shutdownGraceTime(),
						TimeUnit.SECONDS)) {
					logger.error("Pool did not terminate");
				}
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}
}