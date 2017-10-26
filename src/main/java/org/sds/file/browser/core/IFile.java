package org.sds.file.browser.core;

import java.nio.file.Path;

/**
 * A file is an entry visible in the container's contents table for browsing session.
 */
public interface IFile {

	/**
	 * Returns file name.
	 * 
	 * @return file name
	 */
	String getName();

	/**
	 * Returns file path.
	 * 
	 * @return file path
	 */
	Path getPath();

	/**
	 * Returns file size.
	 * 
	 * @return file size
	 */
	long getSize();

	/**
	 * Returns file modification time.
	 * 
	 * @return file modification time
	 */
	long getTimestamp();

	/**
	 * Checks if given entry is a directory.
	 * 
	 * @return <code>true</code> if given entry is a directory, <code>false</code>
	 *         otherwise
	 */
	boolean isDirectory();

}
