package com.adobe.aem.init.dogmatix.http.response;

import java.util.HashMap;
import java.util.Map;

/**
 * Pre-load and cache popular mime types based on file extensions
 * to save the expensive mimetype lookup at runtime
 * 
 * @author vnagpal
 *
 */
public interface Mime {
	
	@SuppressWarnings("serial")
	Map<String, String> Mapping = new HashMap<String, String>() {{
		put("css","text/css");
	}};

}
