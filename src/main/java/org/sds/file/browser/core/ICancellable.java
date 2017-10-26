package org.sds.file.browser.core;

/**
 * Simple interface for cancellable operation.
 */
public interface ICancellable {

	/**
	 * Cancel operation.
	 */
	void cancel();

	/**
	 * Checks if this operation is cancelled,
	 * 
	 * @return <code>true</code> if this monitor is cancelled, <code>false</code>
	 *         otherwise
	 */
	boolean isCanceled();

}
