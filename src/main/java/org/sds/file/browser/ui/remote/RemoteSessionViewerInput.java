package org.sds.file.browser.ui.remote;

import org.eclipse.swt.graphics.Image;
import org.sds.file.browser.core.ISession;
import org.sds.file.browser.ui.AbstractSessionViewerInput;
import org.sds.file.browser.ui.ISessionLabelProvider;
import org.sds.file.browser.ui.ImageRegistry;
import org.sds.file.browser.ui.ImageRegistry.Images;

/**
 * Remote session viewer input.
 */
public class RemoteSessionViewerInput extends AbstractSessionViewerInput {

	public RemoteSessionViewerInput(String name, ISession session) {
		super(name, session);
	}

	@Override
	public Image getImage() {
		return ImageRegistry.getImage(Images.IMG_FTP_SESSION);
	}

	@Override
	public ISessionLabelProvider getLabelProvider() {
		return new RemoteSessionLabelProvider();
	}

}
