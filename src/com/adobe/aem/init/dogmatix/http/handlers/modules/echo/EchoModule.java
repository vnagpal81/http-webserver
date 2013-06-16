package com.adobe.aem.init.dogmatix.http.handlers.modules.echo;

import com.adobe.aem.init.dogmatix.http.handlers.HttpContext;
import com.adobe.aem.init.dogmatix.http.handlers.modules.AbstractHttpRequestHandlerModule;
import com.adobe.aem.init.dogmatix.http.handlers.modules.Module;

@Module(url="echo/*")
public class EchoModule extends AbstractHttpRequestHandlerModule {

	@Override
	public boolean consume(HttpContext context) {
		return false;
		
	}

}
