package org.sds.file.browser.ui.local;

import static org.sds.file.browser.core.Messages.MSG_TABLE_FILE_SIZE_KB;
import static org.sds.file.browser.core.Messages.MSG_TABLE_FILE_TYPE_FILE;
import static org.sds.file.browser.core.Messages.MSG_TABLE_FILE_TYPE_FOLDER;
import static org.sds.file.browser.core.Messages.MSG_TABLE_FILE_TYPE_UNKNOWN;
import static org.sds.file.browser.ui.ImageRegistry.Images.IMG_FILE;
import static org.sds.file.browser.ui.ImageRegistry.Images.IMG_FILE_HIDDEN;
import static org.sds.file.browser.ui.ImageRegistry.Images.IMG_FILE_IMAGE;
import static org.sds.file.browser.ui.ImageRegistry.Images.IMG_FOLDER;
import static org.sds.file.browser.ui.ImageRegistry.Images.IMG_FOLDER_HIDDEN;
import static org.sds.file.browser.ui.ImageRegistry.Images.IMG_FOLDER_HIDDEN_OPEN;
import static org.sds.file.browser.ui.ImageRegistry.Images.IMG_FOLDER_OPEN;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Date;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.sds.file.browser.core.IContainer;
import org.sds.file.browser.core.IFile;
import org.sds.file.browser.core.Messages;
import org.sds.file.browser.ui.ISessionViewerLabelProvider;
import org.sds.file.browser.ui.ImageRegistry;
import org.sds.file.browser.ui.ImageRegistry.Images;

/**
 * Local session viewer label provider.
 */
public class LocalSessionLabelProvider implements ISessionViewerLabelProvider {

	@Override
	public Image getImage(IContainer element, boolean expanded) {
		boolean isHidden = (element.getPath().toFile().isHidden());
		if (element.isRoot()) {
			return ImageRegistry.getImage(Images.IMG_DRIVE);
		}
		if (isHidden) {
			return expanded ? ImageRegistry.getImage(IMG_FOLDER_HIDDEN_OPEN)
					: ImageRegistry.getImage(IMG_FOLDER_HIDDEN);
		}
		return expanded ? ImageRegistry.getImage(IMG_FOLDER_OPEN) : ImageRegistry.getImage(IMG_FOLDER);
	}

	@Override
	public String getText(IContainer element) {
		return element.getName();
	}

	@Override
	public Image getImage(IFile element) {
		boolean isHidden = (element.getPath().toFile().isHidden());
		if (element.isDirectory()) {
			return isHidden ? ImageRegistry.getImage(IMG_FOLDER_HIDDEN) : ImageRegistry.getImage(IMG_FOLDER);
		} else {
			String fileName = element.getName();
			int dot = fileName.lastIndexOf('.');
			if (dot != -1) {
				String extension = fileName.substring(dot);
				Program program = Program.findProgram(extension);
				if (program != null) {
					return ImageRegistry.getImage(program);
				} else {
					try {
						String mimeType = Files.probeContentType(Paths.get(fileName));
						if (mimeType != null && mimeType.startsWith("image/"))
							return ImageRegistry.getImage(IMG_FILE_IMAGE);
					} catch (IOException e) {
						// ignore
					}
					return ImageRegistry.getImage(IMG_FILE);
				}
			} else {
				return isHidden ? ImageRegistry.getImage(IMG_FILE_HIDDEN) : ImageRegistry.getImage(IMG_FILE);
			}
		}
	}

	@Override
	public String getText(IFile element, int column) {
		switch (column) {
		case 0: {
			return element.getName();
		}
		case 1: {
			if (element.isDirectory()) {
				return Messages.get(MSG_TABLE_FILE_TYPE_FOLDER);
			}
			String fileName = element.getName();
			int dot = fileName.lastIndexOf('.');
			if (dot != -1) {
				String extension = fileName.substring(dot);
				Program program = Program.findProgram(extension);
				if (program != null) {
					return program.getName();
				} else {
					return Messages.get(MSG_TABLE_FILE_TYPE_UNKNOWN, extension.substring(1).toUpperCase());
				}
			} else {
				return Messages.get(MSG_TABLE_FILE_TYPE_FILE);
			}
		}
		case 2: {
			if (!element.isDirectory()) {
				return Messages.get(MSG_TABLE_FILE_SIZE_KB, new Long((element.getSize() + 512) / 1024));
			}
			return "";
		}
		case 3: {
			return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM)
					.format(new Date(element.getTimestamp()));
		}
		default:
			break;
		}
		return null;
	}

}
