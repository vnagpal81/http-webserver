package com.adobe.aem.init.dogmatix.util;

import java.io.IOException;
import java.net.Socket;

public class NetworkUtils {

	public static boolean available(int port) {
		try (Socket ignored = new Socket("localhost", port)) {
			return false;
		} catch (IOException ignored) {
			return true;
		}
	}
}
