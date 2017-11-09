package org.sds.file.browser.core.remote;

import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.TrustManagerUtils;

/**
 * FTPS client handler implementation. All access methods are synchronized as
 * wrapped FTPS client is not thread safe.
 */
public class FTPSClientHandler extends AbstractFTPClientHandler<FTPSClient> {

	protected FTPSClientHandler(ClientConfiguration clientConfiguration) {
		super(clientConfiguration);
	}

	@Override
	protected FTPSClient createClient() {
		FTPSClient client = new FTPSClient(
				clientConfiguration.getEncryptionType() == ClientConfiguration.ENCRYPTION_IMPLICIT_TLS_SSL ? true
						: false);
		client.setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());
		return client;
	}

}
