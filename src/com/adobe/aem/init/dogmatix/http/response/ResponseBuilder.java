package com.adobe.aem.init.dogmatix.http.response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ResponseBuilder {
	
	public static String getDateHeader() {
		SimpleDateFormat format;
		String ret;

		format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US);
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		ret = "Date: " + format.format(new Date()) + " GMT";

		return ret;
	}

}
