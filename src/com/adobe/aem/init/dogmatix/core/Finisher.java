package com.adobe.aem.init.dogmatix.core;

import java.util.ArrayList;
import java.util.List;

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
	}
	
}
