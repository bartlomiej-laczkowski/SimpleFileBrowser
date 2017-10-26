package org.sds.file.browser.ui.preview;

import static org.sds.file.browser.ui.preview.PreviewMappings.PreviewType.BROWSER;
import static org.sds.file.browser.ui.preview.PreviewMappings.PreviewType.IMAGE;
import static org.sds.file.browser.ui.preview.PreviewMappings.PreviewType.TEXT;
import static org.sds.file.browser.ui.preview.PreviewMappings.PreviewType.UNKNOWN;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.sds.file.browser.core.ICancellable;
import org.sds.file.browser.core.IFile;
import org.sds.file.browser.core.ISession;
import org.sds.file.browser.core.Messages;
import org.sds.file.browser.ui.ImageRegistry;
import org.sds.file.browser.ui.ImageRegistry.Images;
import org.sds.file.browser.ui.preview.PreviewMappings.PreviewType;

/**
 * Preview viewer composite responsible for loading and updating selected file
 * preview.
 */
public class PreviewViewer extends Composite {

	/**
	 * Runnable responsible for loading, processing and drawing data supported by
	 * available preview loaders.
	 */
	private class LoadPreviewTask implements Runnable, ICancellable {

		private IFile file;
		private volatile boolean isCanceled;

		public LoadPreviewTask(IFile file) {
			this.file = file;
			PreviewExecutor.INSTANCE.submit(this);
			this.isCanceled = false;
		}

		@Override
		public void run() {
			PreviewType previewType = PreviewMappings.getMapping(file);
			final AbstractPreviewLoader<?> printer = printers.get(previewType);
			if (isCanceled()) {
				return;
			}
			if (canPrint(printer)) {
				if (isCanceled()) {
					return;
				}
				getDisplay().asyncExec(() -> {
					if (isCanceled)
						return;
					Control control = printer.getControl().getParent();
					stackLayout.topControl = control;
					control.redraw();
					layout(true, true);
				});
				return;
			}

		}

		private <T> boolean canPrint(AbstractPreviewLoader<T> printer) {
			final T previewData = printer.prepareData(file.getPath(), session, this);
			if (previewData == null) {
				return false;
			}
			printer.updateData(previewData);
			return true;
		}

		public void cancel() {
			isCanceled = true;
		}

		@Override
		public boolean isCanceled() {
			return isCanceled;
		}

	}

	private final class PreviewStateInfo extends Composite {

		private CLabel info;

		public PreviewStateInfo(Composite parent) {
			super(parent, SWT.NONE);
			setLayout(new GridLayout());
			info = new CLabel(this, SWT.NONE);
			info.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
			info.setEnabled(false);
			info.setForeground(getDisplay().getSystemColor(SWT.COLOR_GRAY));
			info.setText(Messages.get(Messages.MSG_PREVIEW_SELECT_FILE));
		}

		public void showSelectFile() {
			info.setImage(null);
			info.setText(Messages.get(Messages.MSG_PREVIEW_SELECT_FILE));
			stackLayout.topControl = this;
			redraw();
			PreviewViewer.this.layout(true, true);
		}

		public void showLoading() {
			// Would be nice to show some progress bar here...
			info.setImage(ImageRegistry.getImage(Images.IMG_SANDGLASS));
			info.setText(Messages.get(Messages.MSG_PREVIEW_LOADING));
			stackLayout.topControl = this;
			redraw();
			PreviewViewer.this.layout(true, true);
		}

	}

	private final StackLayout stackLayout;
	private LoadPreviewTask loadPreviewTask;
	private Map<PreviewType, AbstractPreviewLoader<?>> printers;
	private PreviewStateInfo previewStateInfo;
	private ISession session;

	public PreviewViewer(Composite parent, ISession session) {
		super(parent, SWT.BORDER);
		this.session = session;
		this.stackLayout = new StackLayout();
		setLayout(stackLayout);
		previewStateInfo = new PreviewStateInfo(this);
		stackLayout.topControl = previewStateInfo;
		setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		setBackgroundMode(SWT.INHERIT_FORCE);
		registerPrinters();
	}

	public void preview(final IFile file) {
		if (file == null) {
			previewStateInfo.showSelectFile();
			return;
		}
		previewStateInfo.showLoading();
		// Cancel ongoing load preview task if there is any
		if (loadPreviewTask != null) {
			loadPreviewTask.cancel();
		}
		// Trigger new load preview task
		loadPreviewTask = new LoadPreviewTask(file);
	}

	private void registerPrinters() {
		printers = new HashMap<>();
		printers.put(IMAGE, new ImagePreviewLoader(this));
		printers.put(TEXT, new TextPreviewLoader(this));
		printers.put(BROWSER, new BrowserPreviewLoader(this));
		printers.put(UNKNOWN, new NoPreviewLoader(this));
	}

}
