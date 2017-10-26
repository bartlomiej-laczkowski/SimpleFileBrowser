package org.sds.file.browser.ui.preview;

import java.nio.file.Path;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.sds.file.browser.core.ICancellable;
import org.sds.file.browser.core.ISession;
import org.sds.file.browser.core.Messages;

/**
 * "NO PREVIEW" loader.
 */
class NoPreviewLoader extends AbstractPreviewLoader<String> {

	private String message;
	private Label messageLabel;
	private Composite preview;

	public NoPreviewLoader(Composite parent) {
		super(parent);
	}

	protected Control createControl(Composite parent) {
		preview = new Composite(parent, SWT.NONE);
		preview.setLayout(new GridLayout());
		messageLabel = new Label(preview, SWT.NONE);
		messageLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		messageLabel.setEnabled(false);
		return preview;
	}

	@Override
	protected void updateControl(PaintEvent event) {
		if (message != null) {
			messageLabel.setText(message);
			message = null;
		}
	}

	@Override
	public String prepareData(Path path, ISession session, ICancellable monitor) {
		return Messages.get(Messages.MSG_PREVIEW_NO_PREVIEW);
	}

	@Override
	public void updateData(String previewData) {
		message = previewData;
	}

}