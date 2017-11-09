package org.sds.file.browser.core.remote;

import org.apache.commons.net.ftp.FTPClient;

/**
 * FTP client handler implementation. All access methods are synchronized as
 * wrapped FTP client is not thread safe.
 */
public class FTPClientHandler extends AbstractFTPClientHandler<FTPClient> {

	protected FTPClientHandler(ClientConfiguration clientConfiguration) {
		super(clientConfiguration);
	}

	@Override
	protected FTPClient createClient() {
		return new FTPClient();
	}

}
