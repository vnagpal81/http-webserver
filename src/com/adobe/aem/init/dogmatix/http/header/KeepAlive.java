package com.adobe.aem.init.dogmatix.http.header;

import com.adobe.aem.init.dogmatix.http.handlers.HttpContext;
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
		if (ctx.getRequest().getHeader(Constants.HEADERS.CONNECTION)
				.equalsIgnoreCase("Keep-Alive")) {

			return true;
		}
		return false;
	}

}
