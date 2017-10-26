package org.sds.file.browser.core.local;

import java.nio.file.Path;

import org.sds.file.browser.core.AbstractContainer;

/**
 * Default local container implementation.
 */
public class LocalContainer extends AbstractContainer {

	public LocalContainer(Path path) {
		super(path);
	}

}
