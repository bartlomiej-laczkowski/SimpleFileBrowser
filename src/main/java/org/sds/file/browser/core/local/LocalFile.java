package org.sds.file.browser.core.local;

import java.io.File;
import java.nio.file.Path;

import org.sds.file.browser.core.AbstractFile;

/**
 * Local session file implementation.
 */
public class LocalFile extends AbstractFile {

	private final File file;

	public LocalFile(Path path) {
		super(path);
		this.file = path.toFile();
	}
	
	@Override
	public String getName() {
		return file.getName();
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
