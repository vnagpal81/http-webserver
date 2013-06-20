package com.adobe.aem.init.dogmatix.http.handlers.modules.echo;

import com.adobe.aem.init.dogmatix.http.handlers.HttpContext;
import com.adobe.aem.init.dogmatix.http.handlers.modules.AbstractHttpRequestHandlerModule;
import com.adobe.aem.init.dogmatix.http.handlers.modules.Module;
import com.adobe.aem.init.dogmatix.http.handlers.modules.Setting;
import com.adobe.aem.init.dogmatix.http.request.HttpRequest;
import com.adobe.aem.init.dogmatix.util.Constants;

@Module(url = "echo/**", settings = {
		@Setting(name=EchoModule.SETTINGS.HEADER, value="=========="),
		@Setting(name=EchoModule.SETTINGS.FOOTER, value="==========")
})
public class EchoModule extends AbstractHttpRequestHandlerModule {
	
	public interface SETTINGS {
		String HEADER = "header";
		String FOOTER = "footer";
	}

	@Override
	public boolean consume(HttpContext context) {
		HttpRequest request = context.getRequest();
		StringBuffer response = new StringBuffer();
		response.append(config.getSetting(EchoModule.SETTINGS.HEADER, ""));
		response.append(Constants.NEW_LINE);
		response.append(request.getRaw());
		response.append(Constants.NEW_LINE);
		response.append(config.getSetting(EchoModule.SETTINGS.FOOTER, ""));
		context.getResponse().append(response);

		return false;

	}

}
