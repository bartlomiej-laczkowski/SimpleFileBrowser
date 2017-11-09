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
 * Abstract FTP client handler implementation.
 *
 * @param <T> expected client type
 */
abstract class AbstractFTPClientHandler<T extends FTPClient> implements IClientHandler {

	protected final T client;
	protected final ClientConfiguration clientConfiguration;
	private final Object connectionMutex = new Object();

	protected AbstractFTPClientHandler(ClientConfiguration clientConfiguration) {
		this.clientConfiguration = clientConfiguration;
		this.client = createClient();
	}

	/**
	 * Implementors should create and return FTP client.
	 * 
	 * @return FTP client
	 */
	protected abstract T createClient();

	@Override
	public Status connect() {
		try {
			synchronized (connectionMutex) {
				client.connect(clientConfiguration.getHost(), clientConfiguration.getPort());
				if (!client.login(clientConfiguration.getUser(), clientConfiguration.getPassword())) {
					return new Status(Status.ERROR, "Access denied.");
				}
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
	public Status disconnect() {
		if (client.isConnected()) {
			try {
				synchronized (connectionMutex) {
					client.abort();
					client.disconnect();
				}
			} catch (IOException e) {
				return new Status(Status.ERROR, e.getMessage(), e);
			}
		}
		return Status.OK_STATUS;
	}

	public synchronized FTPFile[] listDirectories(final String path) {
		try {
			if (client.isConnected()) {
				return client.listDirectories(path);
			}
		} catch (IOException e) {
			// TODO logging
		}
		return new FTPFile[] {};
	}

	public synchronized FTPFile[] listFiles(final String path) {
		try {
			if (client.isConnected()) {
				return client.listFiles(path);
			}
		} catch (IOException e) {
			// TODO logging
		}
		return new FTPFile[] {};
	}

	public synchronized void readData(final IStreamReader<?> reader, final String path, final ICancellable monitor) {
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
