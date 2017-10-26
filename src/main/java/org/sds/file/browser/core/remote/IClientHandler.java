package org.sds.file.browser.core.remote;

import org.apache.commons.net.ftp.FTPFile;
import org.sds.file.browser.core.ICancellable;
import org.sds.file.browser.core.IStreamReader;
import org.sds.file.browser.core.Status;

/**
 * Common interface for FTP client handlers.
 */
public interface IClientHandler {

	/**
	 * Connects client and returns operation status.
	 * 
	 * @return connect operation status
	 */
	Status connect();

	/**
	 * Disconnects client and returns operation status.
	 * 
	 * @return disconnect operation status
	 */
	Status disconnect();

	/**
	 * Returns child directories for given remote path.
	 * 
	 * @param path
	 * @return child directories for given remote path
	 */
	FTPFile[] listDirectories(String path);

	/**
	 * Returns child files for given remote path.
	 * 
	 * @param path
	 * @return child files for given remote path
	 */
	FTPFile[] listFiles(String path);

	/**
	 * Performs input stream data read with the use of provided reader. Implementors
	 * are responsible for creating input stream from the resource available under
	 * given path and providing it to a stream reader. Implementors should also pass
	 * provided monitor to a reader.
	 * 
	 * @param reader
	 *            reader for reading input stream data.
	 * @param path
	 *            path to resource to create input stream from
	 * @param monitor
	 *            cancellable progress monitor
	 */
	void readData(IStreamReader<?> reader, String path, ICancellable monitor);

}
