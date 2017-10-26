package org.sds.file.browser.core.remote;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.sds.file.browser.core.ICancellable;
import org.sds.file.browser.core.IStreamReader;
import org.sds.file.browser.core.Status;

/**
 * FTP client handler implementation. All access methods are synchronized as
 * wrapped FTP client is not thread safe.
 */
public class FTPClientHandler implements IClientHandler {

	private ClientConfiguration configuration;
	private FTPClient client;

	protected FTPClientHandler(ClientConfiguration configuration) {
		this.configuration = configuration;
		this.client = new FTPClient();
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
			monitor.cancel();
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
