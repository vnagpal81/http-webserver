package com.adobe.aem.init.dogmatix.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ReusableSocket {

	private Socket socket;
	
	private int count;
	
	private boolean idle;

	public ReusableSocket(Socket socket) {
		this.socket = socket;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public boolean isIdle() {
		return idle;
	}

	public void setIdle(boolean idle) {
		this.idle = idle;
	}
	
	public OutputStream getOutputStream() throws IOException {
		return this.socket.getOutputStream();
	}
	
	public InputStream getInputStream() throws IOException {
		return this.socket.getInputStream();
	}
	
	public void close() throws IOException {
		this.socket.close();
	}
}
