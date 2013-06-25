package com.adobe.aem.init.dogmatix.util;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;

public class NetworkUtils {

	private static final int RETRY_READING_FROM_STREAM_AFTER = 500;
	
	public static boolean available(int port) {
		try {
			Socket ignored = new Socket("localhost", port);
			ignored.close();
			return false;
		} catch (IOException ignored) {
			return true;
		}
	}
	
	public static boolean ping(String url) {
		try {
			new URL(url).openStream().close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
