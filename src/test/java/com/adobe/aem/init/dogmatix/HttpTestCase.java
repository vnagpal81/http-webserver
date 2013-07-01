package com.adobe.aem.init.dogmatix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.adobe.aem.init.dogmatix.config.ServerConfig;
import com.adobe.aem.init.dogmatix.core.Finisher;
import com.adobe.aem.init.dogmatix.listeners.CommandListener;
import com.adobe.aem.init.dogmatix.listeners.HttpListener;
import com.adobe.aem.init.dogmatix.listeners.Listener;
import com.adobe.aem.init.dogmatix.util.IOUtils;

public class HttpTestCase {
	private Listener cmd;

	private static final int httpPort = 4444;
	private static final int cmdPort = 1913;
	private static final String boundary = "------WebKitFormBoundaryuXxBDyVvIq59byvl";
	private static final String pwd = System.getProperty("user.dir");

	private void startServer(int port) throws Exception {
		ServerConfig config = ServerConfig.getInstance(pwd + "/target//test-classes//server.xml");
		Listener http = new HttpListener(config);
		cmd = new CommandListener(config, http);
		http.start();
		cmd.start();
	}

	private void stopServer(int cmdPort) throws Exception {
		if(cmd != null) {
		Finisher finisher = new Finisher(cmd);
		finisher.start();}
	}

	@Before
	public void setup() {
		try {
			cleanUp();
			startServer(httpPort);
			Thread.sleep(5000);
		} catch (Exception e) {
			fail("init error : " + e.getMessage());
		}

	}
	
	private void cleanUp() {
		File f = new File(pwd + "/logo.png");
		f.delete();
	}

	@Test
	public void testSinglePostAndGetRequest() {
		try {
			String fileName = "logo.png";
			String entityStart = IOUtils.CRLF + "--" + boundary + IOUtils.CRLF + 
					"Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"" + IOUtils.CRLF + 
					"Content-Type: image/png" + IOUtils.CRLF +
					IOUtils.CRLF;
			String entityEnd = IOUtils.CRLF + "--" + boundary + "--" + IOUtils.CRLF;
			InputStream uploadFile = getClass().getClassLoader().getResourceAsStream(fileName);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int cnt = 0;
			while (uploadFile.available() > 0) {
				baos.write(uploadFile.read());
				cnt++;
			}
			uploadFile.close();
			
			HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:" + httpPort + "/").openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Length", String.valueOf(entityStart.getBytes().length + cnt + entityEnd.getBytes().length));
			connection.setRequestProperty("Content-Type", "multipart/form-data, boundary=" + boundary);
			//connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			OutputStream outputStream = connection.getOutputStream();
			
			outputStream.write(entityStart.getBytes());
			outputStream.write(baos.toByteArray());
			outputStream.write(entityEnd.getBytes());
			outputStream.close();
			
			assertEquals(201, connection.getResponseCode());
			System.out.println(connection.getResponseCode() + " " + connection.getResponseMessage());
			
			if (connection != null) {
				connection.disconnect();
			}
			
			connection = (HttpURLConnection) new URL("http://localhost:" + httpPort + "/" + fileName).openConnection();
			InputStream inputStream = connection.getInputStream();
			assertEquals(200, connection.getResponseCode());
			
			System.out.println(connection.getResponseCode() + " " + connection.getResponseMessage());
			uploadFile = getClass().getClassLoader().getResourceAsStream(fileName);
			int diff = 0;
			while (uploadFile.available() > 0) {
				if(uploadFile.read() != inputStream.read()) {
					diff++;
				}
			}
			uploadFile.close();
			inputStream.close();
			assertEquals(diff, 0);
			
			if (connection != null) {
				connection.disconnect();
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@After
	public void teardown() {
		try {
			stopServer(cmdPort);
			cleanUp();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
