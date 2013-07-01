/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. The ASF licenses this file to You 
 * under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.adobe.aem.init.dogmatix.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * A re-usable socket implementation.
 * Wraps a java.net.Socket instance adding on additional behaviour to it.
 * 
 * Maintains the 
 * 
 * access count, 
 * last access timestamp, 
 * persistability 
 * 
 * of the underlying Socket.
 * 
 * A re-usable socket is used for implementing keep-alive behaviour i.e. 
 * persistent TCP connections
 * 
 * @author vnagpal
 *
 */
public class ReusableSocket {

	private Socket socket;
	
	private int count;
	
	private long lastAccess;
	
	private boolean persist;

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
	
	public long getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(long lastAccess) {
		this.lastAccess = lastAccess;
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

	public boolean isPersist() {
		return persist;
	}

	public void setPersist(boolean persist) {
		this.persist = persist;
	}
	
	
}
