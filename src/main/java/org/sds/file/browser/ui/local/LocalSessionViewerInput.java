package org.sds.file.browser.ui.local;

import org.eclipse.swt.graphics.Image;
import org.sds.file.browser.core.ISession;
import org.sds.file.browser.ui.AbstractSessionViewerInput;
import org.sds.file.browser.ui.ISessionLabelProvider;
import org.sds.file.browser.ui.ImageRegistry;
import org.sds.file.browser.ui.ImageRegistry.Images;

/**
 * Local session viewer input.
 */
public class LocalSessionViewerInput extends AbstractSessionViewerInput {

	public LocalSessionViewerInput(String name, ISession session) {
		super(name, session);
	}

	@Override
	public Image getImage() {
		return ImageRegistry.getImage(Images.IMG_LOCAL_SESSION);
	}

	@Override
	public ISessionLabelProvider getLabelProvider() {
		return new LocalSessionLabelProvider();
	}

}
