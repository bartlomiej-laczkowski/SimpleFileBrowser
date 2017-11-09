package org.sds.file.browser.core.remote;

import java.util.UUID;

/**
 * FTP client configuration.
 */
public class ClientConfiguration {
	
	public static final int ENCRYPTION_NONE = 0;
	public static final int ENCRYPTION_IMPLICIT_TLS_SSL = 1;
	public static final int ENCRYPTION_EXPLICIT_TLS_SSL = 2;
	
	private final String uniqueId;
	private final String name;
	private final int encryptionType;
	private final String host;
	private final int port;
	private final String user;
	private final String password;
	
	public ClientConfiguration(String name, int encryptionType, String host, int port, String user,
			String password) {
		super();
		this.name = name;
		this.encryptionType = encryptionType;
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		this.uniqueId = UUID.randomUUID().toString();
	}
	
	public String getUniqueId() {
		return this.uniqueId;
	}

	public String getName() {
		return name;
	}

	public int getEncryptionType() {
		return encryptionType;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

}
