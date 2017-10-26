package org.sds.file.browser.core;

import java.nio.file.Path;

/**
 * Abstract container implementation.
 */
public abstract class AbstractContainer implements IContainer {

	private final Path path;
	
	protected AbstractContainer(Path path) {
		this.path = path;
	}
	
	@Override
	public String getName() {
		Path name = path.getFileName();
		if (name != null) {
			return name.toString();
		}
		return path.getRoot().toString();
	}

	@Override
	public Path getPath() {
		return path;
	}
	
	@Override
	public boolean isRoot() {
		return path.getParent() == null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractContainer other = (AbstractContainer) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	
	
}
