package com.adobe.aem.init.dogmatix.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;

public class NetworkUtils {

	private static final int RETRY_READING_FROM_STREAM_AFTER = 500;
	
	public static boolean available(int port) {
		try (Socket ignored = new Socket("localhost", port)) {
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
	
	public static byte[] readFrom(InputStream in, boolean retry) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			while(in.available() > 0) {
				baos.write(in.read());
			}
		} catch (IOException e) {
			
			if(retry) {
				try {
					Thread.sleep(RETRY_READING_FROM_STREAM_AFTER);
				} catch (InterruptedException e1) {
				}
				try {
					baos.write(readFrom(in, false));
				} catch (IOException e1) {
				}
			}
		}
		return baos.toByteArray();
	}
}
