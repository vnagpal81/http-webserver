package com.adobe.aem.init.dogmatix.http.handlers.modules;

import com.adobe.aem.init.dogmatix.exceptions.InvalidModuleException;
import com.adobe.aem.init.dogmatix.util.ObjectPool;


public class ModuleInstancePool extends ObjectPool<AbstractHttpRequestHandlerModule> {
	
	private String moduleClassName;

	public ModuleInstancePool(String moduleClassName) throws InvalidModuleException {
		super();
		try {
			Class.forName(moduleClassName).newInstance();
		}
		catch (Exception e) {
			throw new InvalidModuleException(moduleClassName);
		}
		finally {
			
		}
		
		this.moduleClassName = moduleClassName;
	}
	
	@Override
	protected AbstractHttpRequestHandlerModule create() {
		try {
			return (AbstractHttpRequestHandlerModule) Class.forName(moduleClassName).newInstance();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean validate(AbstractHttpRequestHandlerModule o) {
		return true;
	}

	@Override
	public void expire(AbstractHttpRequestHandlerModule o) {
		
	}

}
