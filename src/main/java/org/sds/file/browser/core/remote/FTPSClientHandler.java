package org.sds.file.browser.core.remote;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.TrustManagerUtils;
import org.sds.file.browser.core.ICancellable;
import org.sds.file.browser.core.IStreamReader;
import org.sds.file.browser.core.Status;

/**
 * FTPS client handler implementation. All access methods are synchronized as
 * wrapped FTPS client is not thread safe.
 */
public class FTPSClientHandler implements IClientHandler {

	private ClientConfiguration configuration;
	private FTPSClient client;

	protected FTPSClientHandler(ClientConfiguration configuration) {
		this.configuration = configuration;
		this.client = new FTPSClient(
				configuration.getEncryptionType() == ClientConfiguration.ENCRYPTION_IMPLICIT_TLS_SSL ? true : false);
		client.setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());
	}

	@Override
	public synchronized Status connect() {
		try {
			client.connect(configuration.getHost(), configuration.getPort());
			if (!client.login(configuration.getUser(), configuration.getPassword())) {
				return new Status(Status.ERROR, "Access denied.");
			}
			client.setFileType(FTP.BINARY_FILE_TYPE);
		} catch (UnknownHostException e) {
			return new Status(Status.ERROR, "Unknown host: " + e.getMessage());
		} catch (IOException e) {
			return new Status(Status.ERROR, e.getMessage(), e);
		}
		client.enterLocalPassiveMode();
		client.setAutodetectUTF8(true);
		return Status.OK_STATUS;
	}

	@Override
	public synchronized Status disconnect() {
		if (client.isConnected()) {
			try {
				client.disconnect();
			} catch (IOException e) {
				return new Status(Status.ERROR, e.getMessage(), e);
			}
		}
		return Status.OK_STATUS;
	}

	public synchronized FTPFile[] listDirectories(String path) {
		try {
			if (client.isConnected())
				return client.listDirectories(path);
		} catch (IOException e) {
			// TODO logging
		}
		return new FTPFile[] {};
	}

	public synchronized FTPFile[] listFiles(String path) {
		try {
			if (client.isConnected())
				return client.listFiles(path);
		} catch (IOException e) {
			// TODO logging
		}
		return new FTPFile[] {};
	}

	public synchronized void readData(IStreamReader<?> reader, String path, ICancellable monitor) {
		if (!client.isConnected()) {
			return;
		}
		try (InputStream inputStream = client.retrieveFileStream(path)) {
			reader.read(inputStream, monitor);
			if (monitor.isCanceled()) {
				client.abort();
			}
			client.completePendingCommand();
		} catch (IOException e) {
			// TODO logging
		}
	}

}
