package org.sds.file.browser.core.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sds.file.browser.core.ICancellable;
import org.sds.file.browser.core.IContainer;
import org.sds.file.browser.core.IFile;
import org.sds.file.browser.core.ISession;
import org.sds.file.browser.core.IStreamReader;
import org.sds.file.browser.core.Status;

/**
 * Local session implementation.
 */
public class LocalSession implements ISession {

	public static String LOCAL_SESSION_NAME;
	
	static {
		LOCAL_SESSION_NAME = "LOCAL";
		// Set up session name
		String userName = System.getProperty("user.name");
		java.net.InetAddress localMachine;
		try {
			localMachine = InetAddress.getLocalHost();
			String hostName = localMachine.getHostName();
			LOCAL_SESSION_NAME = userName + '@' + hostName;
		} catch (UnknownHostException e) {
			// ignore
		}
	}
	
	private List<IContainer> roots;

	@Override
	public Status startup() {
		// Set up local session roots
		// Win
		if (System.getProperty("os.name").contains("Windows")) {
			roots = new ArrayList<>();
			for (char i = 'C'; i <= 'Z'; ++i) {
				String drive = i + ":" + File.separator;
				File root = new File(drive);
				if (root.isDirectory() && root.exists()) {
					roots.add(new LocalContainer(Paths.get(root.getPath())));
				}
			}
		} else {
			// Others
			roots = Arrays.asList(new LocalContainer(Paths.get(File.separator)));
		}
		return Status.OK_STATUS;
	}

	@Override
	public Status shutdown() {
		// Ignore, nothing to be done here
		return Status.OK_STATUS;
	}

	@Override
	public List<IContainer> getRoots() {
		return roots;
	}
	
	@Override
	public IContainer getContainer(Path path) {
		return new LocalContainer(path);
	}

	@Override
	public List<IFile> getContents(IContainer container) {
		List<IFile> contents = new ArrayList<>();
		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(container.getPath())) {
			for (Path element : dirStream) {
				contents.add(new LocalFile(element));
			}
		} catch (IOException e) {
			// TODO logging
		}
		return contents;
	}
	
	@Override
	public boolean hasChildren(IContainer container) {
		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(container.getPath(), new Filter<Path>() {
			@Override
			public boolean accept(Path entry) throws IOException {
				return Files.isDirectory(entry);
			}
		})) {
			return dirStream.iterator().hasNext();
		} catch (IOException e) {
			// TODO logging
		}
		// Assume that it has sub-directories if check has failed...
		return true;
	}
	
	@Override
	public List<IContainer> getChildren(IContainer container) {
		List<IContainer> children = new ArrayList<>();
		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(container.getPath(), new Filter<Path>() {
			@Override
			public boolean accept(Path entry) throws IOException {
				return Files.isDirectory(entry);
			}
		})) {
			for (Path dir : dirStream) {
				children.add(new LocalContainer(dir));
			}
		} catch (IOException e) {
			// TODO logging
		}
		return children;
	}

	@Override
	public void readData(IStreamReader<?> inputStreamReader, Path path, ICancellable monitor) {
		File file = path.toFile();
		try {
			InputStream inputStream = new FileInputStream(file);
			inputStreamReader.read(inputStream, monitor);
		} catch (FileNotFoundException e) {
			// TODO logging
		}
	}

}
