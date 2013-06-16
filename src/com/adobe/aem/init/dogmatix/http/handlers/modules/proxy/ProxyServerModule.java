package com.adobe.aem.init.dogmatix.http.handlers.modules.proxy;

import com.adobe.aem.init.dogmatix.http.handlers.HttpContext;
import com.adobe.aem.init.dogmatix.http.handlers.modules.AbstractHttpRequestHandlerModule;
import com.adobe.aem.init.dogmatix.http.handlers.modules.Module;

@Module(url="proxy/*")
public class ProxyServerModule extends AbstractHttpRequestHandlerModule {

	@Override
	public boolean consume(HttpContext context) {
		return false;
		// TODO Auto-generated method stub
		
	}

}
