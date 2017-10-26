package org.sds.file.browser.core;

import java.nio.file.Path;

/**
 * A container is an entry visible in the directory structure tree for browsing
 * session.
 */
public interface IContainer {

	/**
	 * Returns container name.
	 * 
	 * @return container name
	 */
	String getName();

	/**
	 * Returns container path
	 * 
	 * @return container path
	 */
	Path getPath();

	/**
	 * Checks if this container is a root.
	 * 
	 * @return <code>true</code> if this container is root,
	 *         <code>false</code>otherwise
	 */
	boolean isRoot();

}
