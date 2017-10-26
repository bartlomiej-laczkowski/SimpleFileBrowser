package org.sds.file.browser.core.remote;

import java.nio.file.Path;

import org.sds.file.browser.core.AbstractContainer;

/**
 * Remote container default implementation.
 */
public class RemoteContainer extends AbstractContainer {

	public RemoteContainer(Path path) {
		super(path);
	}

}
