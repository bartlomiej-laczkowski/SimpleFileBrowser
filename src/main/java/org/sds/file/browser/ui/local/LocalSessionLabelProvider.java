package org.sds.file.browser.ui.local;

import static org.sds.file.browser.ui.ImageRegistry.Images.IMG_FILE_HIDDEN;
import static org.sds.file.browser.ui.ImageRegistry.Images.IMG_FOLDER_HIDDEN;
import static org.sds.file.browser.ui.ImageRegistry.Images.IMG_FOLDER_HIDDEN_OPEN;

import org.eclipse.swt.graphics.Image;
import org.sds.file.browser.core.IContainer;
import org.sds.file.browser.core.IFile;
import org.sds.file.browser.ui.AbstractSessionLabelProvider;
import org.sds.file.browser.ui.ImageRegistry;
import org.sds.file.browser.ui.ImageRegistry.Images;

/**
 * Local session viewer label provider.
 */
public class LocalSessionLabelProvider extends AbstractSessionLabelProvider {

	@Override
	public Image getImage(IContainer element, boolean expanded) {
		if (element.isRoot()) {
			return ImageRegistry.getImage(Images.IMG_DRIVE);
		}
		boolean isHidden = (element.getPath().toFile().isHidden());
		if (!isHidden) {
			return super.getImage(element, expanded);
		}
		return expanded ? ImageRegistry.getImage(IMG_FOLDER_HIDDEN_OPEN) : ImageRegistry.getImage(IMG_FOLDER_HIDDEN);
	}

	@Override
	public Image getImage(IFile element) {
		boolean isHidden = (element.getPath().toFile().isHidden());
		if (!isHidden) {
			return super.getImage(element);
		}
		if (element.isDirectory()) {
			return ImageRegistry.getImage(IMG_FOLDER_HIDDEN);
		} else {
			return ImageRegistry.getImage(IMG_FILE_HIDDEN);
		}
	}

}
