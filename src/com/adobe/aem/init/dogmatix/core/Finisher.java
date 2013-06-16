package com.adobe.aem.init.dogmatix.core;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.listeners.Listener;

public class Finisher extends Thread {
	
	private static final Logger logger = LoggerFactory.getLogger(Finisher.class);
	
	private List<Listener> listeners;
	
	public Finisher(Listener... listeners) {
		logger.debug("Created a Finisher");
		if(this.listeners == null) {
			this.listeners = new ArrayList<Listener>();
		}
		for(Listener listener : listeners) {
			registerListener(listener);
		}
	}

	public void registerListener(Listener listener) {
		logger.debug("Register a listener with the finisher");
		this.listeners.add(listener);
	}
	
	@Override
	public void run() {
		logger.debug("Invoked the finisher");
		for(Listener listener : listeners) {
			logger.debug("Telling listener {} to stop listening", listener);
			listener.stopListening();
		}
	}
	
}
