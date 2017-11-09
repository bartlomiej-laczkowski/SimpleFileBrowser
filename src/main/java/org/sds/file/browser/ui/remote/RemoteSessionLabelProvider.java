package org.sds.file.browser.ui.remote;

import static org.sds.file.browser.ui.ImageRegistry.Images.IMG_FTP_FOLDER;

import org.eclipse.swt.graphics.Image;
import org.sds.file.browser.core.IContainer;
import org.sds.file.browser.ui.AbstractSessionLabelProvider;
import org.sds.file.browser.ui.ImageRegistry;

/**
 * Remote session viewer label provider.
 */
public class RemoteSessionLabelProvider extends AbstractSessionLabelProvider {

	@Override
	public Image getImage(IContainer element, boolean expanded) {
		if (element.isRoot()) {
			return ImageRegistry.getImage(IMG_FTP_FOLDER);
		}
		return super.getImage(element, expanded);
	}

}
