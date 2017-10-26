package org.sds.file.browser.core.remote;

import java.nio.file.Path;

import org.apache.commons.net.ftp.FTPFile;
import org.sds.file.browser.core.IFile;

/**
 * Remote file default implementation.
 */
public class RemoteFile implements IFile {

	private FTPFile ftpFile;
	private Path path;

	public RemoteFile(FTPFile ftpFile, Path path) {
		this.ftpFile = ftpFile;
		this.path = path;
	}
	
	@Override
	public String getName() {
		return ftpFile.getName();
	}

	@Override
	public Path getPath() {
		return path;
	}

	@Override
	public long getSize() {
		return ftpFile.getSize();
	}

	@Override
	public long getTimestamp() {
		return ftpFile.getTimestamp().getTimeInMillis();
	}
	
	@Override
	public boolean isDirectory() {
		return ftpFile.isDirectory();
	}

}
