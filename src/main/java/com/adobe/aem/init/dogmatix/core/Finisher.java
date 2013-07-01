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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.listeners.Listener;

/**
 * A finisher thread whose responsibility is to halt the server 
 * systematically by registering the threads listening in the server
 * and asking each of them gracefully to stop listening.
 * 
 * A finisher should be used as a JVM instance exit hook provided by
 * Runtime.getRuntime().addShutdownHook()
 * 
 * @author vnagpal
 *
 */
public class Finisher extends Thread {
	
	private static final Logger logger = LoggerFactory.getLogger(Finisher.class);
	
	/**
	 * List of Listener threads which will be stopped when the finisher executes
	 */
	private List<Listener> listeners;
	
	/**
	 * Construct a finisher with listener threads
	 * @param listeners
	 */
	public Finisher(Listener... listeners) {
		logger.debug("Created a Finisher");
		if(this.listeners == null) {
			this.listeners = new ArrayList<Listener>();
		}
		for(Listener listener : listeners) {
			registerListener(listener);
		}
	}

	/**
	 * Register a listener with this finisher.
	 * Each listener is stopped when the finisher executes.
	 * 
	 * @param listener A listener thread
	 */
	public void registerListener(Listener listener) {
		logger.debug("Register a listener with the finisher");
		this.listeners.add(listener);
	}
	
	/**
	 * When the finisher runs, it iterates over all registered listeners
	 * instructing them to stop listening by invoking Listener.stopListening()
	 */
	@Override
	public void run() {
		logger.debug("Invoked the finisher");
		
		for(Listener listener : listeners) {
			logger.debug("Telling listener {} to stop listening", listener);
			listener.stopListening();
		}
		
		logger.debug("Shutting down log daemon");
		LogManager.shutdown();
	}
	
}
