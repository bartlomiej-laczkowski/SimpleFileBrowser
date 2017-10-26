package org.sds.file.browser.core;

import java.io.InputStream;

/**
 * Common interface for stream readers. Stream reader is responsible for reading
 * data from provided input stream and creating the result that can be fetched
 * after reading is finished. Implementors should perform frequent check of
 * provided monitor to see if reading operation should be cancelled.
 * 
 * @param <T>
 *            Expected object type created with the use of the data from input
 *            stream.
 */
public interface IStreamReader<T> {

	/**
	 * Reads provided input stream.
	 * 
	 * @param inputStream
	 * @param monitor
	 */
	void read(InputStream inputStream, ICancellable monitor);

	/**
	 * Returns result object created with the use of input stream data.
	 * 
	 * @return result object created with the use of input stream data
	 */
	T getResult();

}
