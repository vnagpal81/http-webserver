package com.adobe.aem.init.dogmatix.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class Client {
	private static int successCount = 0;
	private static int failureCount = 0;
	
	private static Runnable http_4444 = new Runnable() {

		@Override
		public void run() {
			try {
				URL url = new URL("http://localhost:4444");
				URLConnection uc = url.openConnection();
				System.out.println("Connecting to localhost:4444 ...");
				BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
				String inputLine;

				while ((inputLine = in.readLine()) != null)
					System.out.println(inputLine);
				in.close();
				successCount++;
			} catch (Exception e) {
				System.out.println(e);
				failureCount++;
			}
		}
	};

	private static int CONCURRENCY = 10000;

	public static void main(String[] args) throws Exception {

		List<Thread> connections = new ArrayList<Thread>();

		for (int i = 0; i < CONCURRENCY; i++) {

			Thread t = new Thread(http_4444);
			t.start();
			connections.add(t);
		}

		for (Thread t : connections) {
			t.join();
		}
		
		System.out.println("FAILURES:"+failureCount);
		System.out.println("SUCCESSES:"+successCount);
	}
}