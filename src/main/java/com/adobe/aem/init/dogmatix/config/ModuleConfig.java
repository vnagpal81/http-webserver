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

package com.adobe.aem.init.dogmatix.config;

import java.util.Properties;

/**
 * Module level configuration POJO
 * 
 * A module is uniquely identified by its Class and URL. This implies that the
 * same module can consume requests from 2 different URLs while still exhibiting
 * separate handling behaviour.
 * 
 * @author vnagpal
 * 
 */
public class ModuleConfig {

	/**
	 * Fully qualified class name of the module implementation.
	 */
	private String className;
	
	/**
	 * A regular expression of the path to be matched against request URI. 
	 * All requests having URIs matching this pattern will be consumed by this module. 
	 */
	private String url;
	
	/**
	 * Module level extra settings. Each setting is a String-String
	 * Key-Value pair which influences the module implementation logic and hence,
	 * the setting keys are module dependent.
	 */
	private Properties settings;

	/**
	 * Default no-arg constructor
	 */
	public ModuleConfig() {
	}

	/**
	 * Construct a module config with className and urlPattern
	 * 
	 * @param className Fully qualified class name of the class implementing the module
	 * @param url URL pattern used to map the requests to this module
	 */
	public ModuleConfig(String className, String url) {
		super();
		this.className = className;
		this.url = url;
	}

	/**
	 * Get the class name for this module
	 * 
	 * @return Fully qualified class name of the class implementing the module
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Sets the class name for this module
	 * 
	 * @param className Fully qualified class name of the class implementing the module
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * Gets the URL pattern mapping to this module
	 * 
	 * @return URL pattern used to map the requests to this module
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the URL pattern mapping to this module
	 * 
	 * @param url URL pattern used to map the requests to this module
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Gets the module instance settings as Key-Value pairs
	 * 
	 * @return A java.util.Properties object representing the module config settings
	 */
	public Properties getSettings() {
		return settings;
	}

	/**
	 * Sets the module instance settings
	 * 
	 * @param settings A java.util.Properties object representing the module config settings
	 */
	public void setSettings(Properties settings) {
		this.settings = settings;
	}

	/**
	 * Adds a module instance setting key-value pair to the config
	 * 
	 * @param name Setting keyword
	 * @param value Setting value
	 */
	public void addSetting(String name, String value) {
		if (this.settings == null) {
			this.settings = new Properties();
		}
		this.settings.setProperty(name, value);
	}

	/**
	 * Retrieves the module instance setting value in the config
	 * against the key
	 * 
	 * @param name Setting key
	 * @return Setting value
	 */
	public String getSetting(String name) {
		if (this.settings == null) {
			return null;
		}
		return this.settings.getProperty(name);
	}

	/**
	 * Retrieves the module instance setting value in the config
	 * against the key. Returns a defaultValue if no config 
	 * setting found.
	 * 
	 * @param name Setting key
	 * @param defaultValue Default value to return
	 * @return Setting value or defaultValue
	 */
	public String getSetting(String name, String defaultValue) {
		if (this.settings == null) {
			return null;
		}
		return this.settings.getProperty(name, defaultValue);
	}

	/**
	 * Checks if two module configs are equal or not.
	 * Equality is established if both modules have the same 
	 * class implementation and the url pattern mapping.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (!(obj instanceof ModuleConfig))
			return false;

		ModuleConfig cfg = (ModuleConfig) obj;

		if (!this.className.equals(cfg.className))
			return false;

		if (!this.url.equals(cfg.url))
			return false;

		return true;
	}
}
