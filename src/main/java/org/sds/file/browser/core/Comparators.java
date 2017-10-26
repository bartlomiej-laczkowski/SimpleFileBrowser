package org.sds.file.browser.core;

import java.util.Comparator;

/**
 * Comparators container class.
 */
public final class Comparators {

	private Comparators() {
	};

	/**
	 * Compares files by name.
	 */
	public static class FileNameComparator implements Comparator<IFile> {

		@Override
		public int compare(IFile o1, IFile o2) {
			return o1.getName().compareToIgnoreCase(o2.getName());
		}

	}

	/**
	 * Compares files by their kind (directory vs file) and name.
	 */
	public static class FileNameKindComparator implements Comparator<IFile> {

		@Override
		public int compare(IFile o1, IFile o2) {
			if (o1.isDirectory() && !o2.isDirectory()) {
				return -1;
			} else if (!o1.isDirectory() && o2.isDirectory()) {
				return 1;
			}
			return o1.getName().compareToIgnoreCase(o2.getName());
		}
	}
	
	/**
	 * Compares containers by name.
	 */
	public static class ContainerNameComparator implements Comparator<IContainer> {

		@Override
		public int compare(IContainer o1, IContainer o2) {
			return o1.getName().compareToIgnoreCase(o2.getName());
		}

	}

}
