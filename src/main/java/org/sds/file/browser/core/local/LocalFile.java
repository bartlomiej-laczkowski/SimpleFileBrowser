package org.sds.file.browser.core.local;

import java.io.File;
import java.nio.file.Path;

import org.sds.file.browser.core.IFile;

/**
 * Local session file implementation.
 */
public class LocalFile implements IFile {

	private Path path;
	private File file;

	public LocalFile(Path path) {
		this.path = path;
		this.file = path.toFile();
	}
	
	@Override
	public String getName() {
		return path.getFileName().toString();
	}

	@Override
	public Path getPath() {
		return path;
	}

	@Override
	public long getSize() {
		return file.length();
	}

	@Override
	public long getTimestamp() {
		return file.lastModified();
	}
	
	@Override
	public boolean isDirectory() {
		return file.isDirectory();
	}

}
