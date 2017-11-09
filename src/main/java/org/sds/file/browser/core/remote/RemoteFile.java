package org.sds.file.browser.core.remote;

import java.nio.file.Path;

import org.apache.commons.net.ftp.FTPFile;
import org.sds.file.browser.core.AbstractFile;

/**
 * Remote file default implementation.
 */
public class RemoteFile extends AbstractFile {

	private final FTPFile ftpFile;

	public RemoteFile(FTPFile ftpFile, Path path) {
		super(path);
		this.ftpFile = ftpFile;
	}
	
	@Override
	public String getName() {
		return ftpFile.getName();
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
