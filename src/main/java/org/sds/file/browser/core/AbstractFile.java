package org.sds.file.browser.core;

import java.nio.file.Path;

/**
 * Abstract file implementation.
 */
public abstract class AbstractFile implements IFile {

	private Path path;

	protected AbstractFile(Path path) {
		this.path = path;
	}

	@Override
	public Path getPath() {
		return path;
	}

}
