package com.adobe.aem.init.dogmatix.http.header;

import org.springframework.util.StringUtils;

import com.adobe.aem.init.dogmatix.config.ServerConfig;
import com.adobe.aem.init.dogmatix.core.ReusableSocket;
import com.adobe.aem.init.dogmatix.http.handlers.HttpContext;
import com.adobe.aem.init.dogmatix.http.request.Version;
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

	@Override
	public boolean preProcess(HttpContext ctx) {
		return false;
	}

	@Override
	public boolean postProcess(HttpContext ctx) {
		ReusableSocket socket = (ReusableSocket) ctx
				.get(HttpContext.SOCKET_HANDLE);
		String connection = ctx.getRequest().getHeader(
				Constants.HEADERS.CONNECTION);
		boolean keepAlive = connection != null
				&& connection.equalsIgnoreCase("Keep-Alive");

		int timeout = -1;
		int max = -1;
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
					}
				} else if (part.toLowerCase().contains("max")
						&& part.contains("=")) {
					try {
						max = Integer.parseInt(part.split("=")[1]);
					} catch (NumberFormatException e) {
						// log
					}
				}
			}
		}
		ServerConfig serverConfig = ServerConfig.getInstance();
		Version version = Version.valueOf(serverConfig.httpVersion());

		switch (version) {
		case VERSION_0_9:
			break;
		case VERSION_1_0:
			if (keepAlive) {
				socket.setPersist(true);
			}
			break;
		case VERSION_1_1:
			if (keepAlive) {
				if (max == -1) {
					socket.setPersist(true);
				} else if (socket.getCount() == max) {
					socket.setPersist(false);
				}
				if (timeout == -1) {
					socket.setPersist(true);
				} else {
					long idleTime = (System.currentTimeMillis() - socket
							.getLastAccess()) / 1000;
					if (idleTime >= timeout) {
						socket.setPersist(false);
					}
					else {
						socket.setPersist(true);
					}
				}
			}
			break;
		default:
			break;
		}
		return false;
	}

}
