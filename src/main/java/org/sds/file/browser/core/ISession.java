package org.sds.file.browser.core;

import java.nio.file.Path;
import java.util.List;

/**
 * Common interface for different types of browsing sessions.
 */
public interface ISession {

	/**
	 * Creates and returns root containers for the session.
	 * 
	 * @return root containers for the session
	 */
	List<IContainer> getRoots();

	/**
	 * Creates and returns container for given path.
	 * 
	 * @param path
	 * @return container for given path
	 */
	IContainer getContainer(Path path);

	/**
	 * Checks if given container has any sub-containers.
	 * 
	 * @param browsable
	 * @return <code>true</code> if given container has any sub-containers,
	 *         <code>false</code> otherwise
	 */
	boolean hasChildren(IContainer browsable);

	/**
	 * Creates and returns sub-containers for given container.
	 * 
	 * @param browsable
	 * @return sub-containers for given container
	 */
	List<IContainer> getChildren(IContainer browsable);

	/**
	 * Creates and returns all of the contents of given container by means of all
	 * the available files and directories.
	 * 
	 * @param container
	 * @return all of the contents of given container
	 */
	List<IFile> getContents(IContainer container);

	/**
	 * Performs input stream data read with the use of provided reader. Implementors
	 * are responsible for creating input stream from the resource available under
	 * given path and providing it to a stream reader. Implementors should also pass
	 * provided monitor to a reader.
	 * 
	 * @param inputStreamReader
	 *            reader for reading input stream data.
	 * @param path
	 *            path to resource to create input stream from
	 * @param monitor
	 *            cancellable progress monitor
	 */
	void readData(IStreamReader<?> inputStreamReader, Path path, ICancellable monitor);

	/**
	 * Starts browsing session and returns status of this operation.
	 * 
	 * @return startup status 
	 */
	Status startup();

	/**
	 * Shutdowns browsing session and returns status of this operation.
	 * 
	 * @return shutdown status 
	 */
	Status shutdown();

}
