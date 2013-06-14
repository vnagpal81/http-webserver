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

}
