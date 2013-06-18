package com.adobe.aem.init.dogmatix.config;

import java.util.Properties;

public class ModuleConfig {
	
	private String className;
	private String url;
	private Properties settings;

	public ModuleConfig() {}
	
	public ModuleConfig(String className, String url) {
		super();
		this.className = className;
		this.url = url;
	}
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Properties getSettings() {
		return settings;
	}
	public void setSettings(Properties settings) {
		this.settings = settings;
	}
	
	public void addSetting(String name, String value) {
		if(this.settings == null) {
			this.settings = new Properties();
		}
		this.settings.setProperty(name, value);
	}
	
	public String getSetting(String name) {
		if(this.settings == null) {
			return null;
		}
		return this.settings.getProperty(name);
	}
	
	public String getSetting(String name, String defaultValue) {
		if(this.settings == null) {
			return null;
		}
		return this.settings.getProperty(name, defaultValue);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		
		if(!(obj instanceof ModuleConfig)) return false;
		
		ModuleConfig cfg = (ModuleConfig) obj;
		
		if(!this.className.equals(cfg.className)) return false;
		
		if(!this.url.equals(cfg.url)) return false;
		
		return true;
	}
}
