package org.sds.file.browser.core.remote;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.net.ftp.FTPFile;
import org.sds.file.browser.core.ICancellable;
import org.sds.file.browser.core.IContainer;
import org.sds.file.browser.core.IFile;
import org.sds.file.browser.core.ISession;
import org.sds.file.browser.core.IStreamReader;
import org.sds.file.browser.core.Status;

/**
 * Remote session implementation.
 */
public class RemoteSession implements ISession {

	private final IClientHandler clientHandler;

	public RemoteSession(IClientHandler clientHandler) {
		this.clientHandler = clientHandler;
	}

	@Override
	public Status startup() {
		return clientHandler.connect();
	}

	@Override
	public Status shutdown() {
		return clientHandler.disconnect();
	}

	@Override
	public List<IContainer> getRoots() {
		return Arrays.asList(new RemoteContainer(Paths.get(File.separator)));
	}
	
	@Override
	public IContainer getContainer(Path path) {
		return new RemoteContainer(path);
	}

	@Override
	public List<IFile> getContents(IContainer container) {
		String remotePath = container.getPath().toString();
		FTPFile[] remoteContents = clientHandler.listFiles(remotePath);
		List<IFile> contents = new ArrayList<>();
		for (FTPFile ftpFile : remoteContents) {
			contents.add(new RemoteFile(ftpFile, container.getPath().resolve(ftpFile.getName())));
		}
		return contents;
	}

	@Override
	public boolean hasChildren(IContainer container) {
		FTPFile[] folders = clientHandler.listDirectories(container.getPath().toString());
		if (folders != null && folders.length != 0) {
			return true;
		}
		return false;
	}

	@Override
	public List<IContainer> getChildren(IContainer container) {
		List<IContainer> children = new ArrayList<>();
		FTPFile[] ftpFolders = clientHandler.listDirectories(container.getPath().toString());
		if (ftpFolders != null && ftpFolders.length != 0) {
			for (FTPFile ftpFolder : ftpFolders) {
				children.add(new RemoteContainer(container.getPath().resolve(ftpFolder.getName())));
			}
		}
		return children;
	}

	@Override
	public void readData(IStreamReader<?> inputStreamReader, Path path, ICancellable monitor) {
		clientHandler.readData(inputStreamReader, path.toString(), monitor);
	}

}
