package com.adobe.aem.init.dogmatix.http.handlers.modules.resources.storage;

import com.adobe.aem.init.dogmatix.config.ModuleConfig;
import com.adobe.aem.init.dogmatix.http.handlers.modules.resources.ResourcesModule;

public class RepositoryFactory {

	public static Repository getRepository(ModuleConfig config) {
		RepositoryType type = RepositoryType.valueOf(config.getSetting(ResourcesModule.SETTINGS.REPOSITORY).toUpperCase());
		Repository repository = null;
		switch(type) {
		case FTP:
			repository = new FTPRepository();
			break;
		case LOCAL:
			repository = new LocalRepository(config.getSetting(ResourcesModule.SETTINGS.BASE_DIR));
			break;
		case REMOTE:
			repository = new RemoteRepository();
			break;
		case S3:
			repository = new S3Repository();
			break;
		default:
			repository = new LocalRepository(config.getSetting(ResourcesModule.SETTINGS.BASE_DIR));
			break;
		}
		return repository;
	}

}
