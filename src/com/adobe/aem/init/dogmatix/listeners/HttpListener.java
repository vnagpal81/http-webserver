package com.adobe.aem.init.dogmatix.listeners;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.adobe.aem.init.dogmatix.http.handlers.HttpRequestHandler;

/**
 * A listener which listens on a port for incoming HTTP Requests.
 * Each request is served in a new thread so as to keep the main thread free.
 * The execution is implemented using {@code ExecutorService}
 * The {@code ThreadPoolExecutor} implementation maintains a core pool of threads
 * the size of which can be extended upto the maxPoolSize.
 * By default the Executor fills up a queue with incoming tasks before creating a new worker thread.
 * To overcome this behaviour, we declare a pool with core pool size as maxThreads 
 * and allowing these threads to time out in case of inactivity for keepAlive time units. 
 * 
 * @author vnagpal
 *
 */
public class HttpListener extends Listener {
	
	private ThreadPoolExecutor pool;
	
	public HttpListener(int port, int maxThreads) {
		super(port);
		pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThreads);
		pool.allowCoreThreadTimeOut(true);
		pool.setKeepAliveTime(1, TimeUnit.MINUTES);
	}

	@Override
	protected void process(Socket socket) throws IOException {
		pool.execute(new HttpRequestHandler(socket));
	}
}