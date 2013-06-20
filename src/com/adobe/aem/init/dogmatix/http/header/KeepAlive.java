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
		if (!applicable(ctx))
			return false;

		ReusableSocket socket = (ReusableSocket) ctx.getSocket();
		String connection = ctx.getRequest().getHeader(
				Constants.HEADERS.CONNECTION);
		boolean keepAlive = connection != null
				&& connection.equalsIgnoreCase("Keep-Alive");

		ServerConfig serverConfig = ServerConfig.getInstance();
		Version version = Version.getVersion(serverConfig.httpVersion());

		//keep alive behaviour is protocol version specific
		switch (version) {
		case VERSION_0_9:
			break;
		case VERSION_1_0:
			if (keepAlive) {
				ctx.getResponse().addHeader(Constants.HEADERS.CONNECTION, "Keep-Alive");
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
					} else {
						socket.setPersist(true);
					}
				}
				
				if(socket.isPersist()) {
					ctx.getResponse().addHeader(Constants.HEADERS.CONNECTION, "Keep-Alive");
				}
			}
			break;
		default:
			break;
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

}
