package org.sds.file.browser.core.remote;

/**
 * FTP client handler builder.
 */
public class ClientHandlerBuilder {

	public IClientHandler build(ClientConfiguration configuration) {
		switch (configuration.getEncryptionType()) {
		case ClientConfiguration.ENCRYPTION_NONE: {
			return buildFTPClientHandler(configuration);
		}
		case ClientConfiguration.ENCRYPTION_EXPLICIT_TLS_SSL:
		case ClientConfiguration.ENCRYPTION_IMPLICIT_TLS_SSL: {
			return buildFTPSClientHandler(configuration);
		}
		}
		return null;
	}

	private IClientHandler buildFTPClientHandler(ClientConfiguration configuration) {
		return new FTPClientHandler(configuration);
	}

	private IClientHandler buildFTPSClientHandler(ClientConfiguration configuration) {
		return new FTPSClientHandler(configuration);
	}

}
