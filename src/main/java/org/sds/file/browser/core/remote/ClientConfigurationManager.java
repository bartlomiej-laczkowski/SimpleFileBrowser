package org.sds.file.browser.core.remote;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * FTP client configurations manager. Stores saved configurations data (only for
 * the application lifetime, no persisting state for now)
 */
public enum ClientConfigurationManager {

	INSTANCE;

	private final Map<String, ClientConfiguration> configurations = new HashMap<>();

	public void addConfiguration(ClientConfiguration configuration) {
		configurations.put(configuration.getId(), configuration);
	}

	public void removeConfiguration(String configurationId) {
		configurations.remove(configurationId);
	}

	public ClientConfiguration findConfiguration(String configurationId) {
		return configurations.get(configurationId);
	}

	public Collection<ClientConfiguration> getAllConfigurations() {
		return configurations.values();
	}

}
