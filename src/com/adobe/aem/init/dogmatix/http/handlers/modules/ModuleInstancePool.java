package com.adobe.aem.init.dogmatix.http.handlers.modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.init.dogmatix.exceptions.InvalidModuleException;
import com.adobe.aem.init.dogmatix.util.ObjectPool;

public class ModuleInstancePool extends
		ObjectPool<AbstractHttpRequestHandlerModule> {

	private static final Logger logger = LoggerFactory
			.getLogger(ModuleInstancePool.class);

	private String moduleClassName;

	public ModuleInstancePool(String moduleClassName)
			throws InvalidModuleException {
		super();
		try {
			Class.forName(moduleClassName).newInstance();
		} catch (Exception e) {
			logger.error("Invalid class name", e);
			throw new InvalidModuleException(moduleClassName);
		} finally {

		}

		this.moduleClassName = moduleClassName;
		logger.debug("Created object pool for {}", moduleClassName);
	}

	@Override
	protected AbstractHttpRequestHandlerModule create() {
		try {
			AbstractHttpRequestHandlerModule moduleObj = (AbstractHttpRequestHandlerModule) Class
					.forName(moduleClassName).newInstance();
			logger.debug("Adding new object to pool for {}",
					this.moduleClassName);
			return moduleObj;
		} catch (Exception e) {
			logger.error("Error adding new object to pool for {}",
					this.moduleClassName);
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
