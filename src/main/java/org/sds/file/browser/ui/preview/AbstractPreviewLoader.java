package org.sds.file.browser.ui.preview;

import java.nio.file.Path;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.sds.file.browser.core.ICancellable;
import org.sds.file.browser.core.ISession;

/**
 * Abstract preview loader implementation.
 * 
 * @param <T>
 *            Expected type of data to be presented by preview loader.
 */
abstract class AbstractPreviewLoader<T> {

	protected Control control;
	protected Composite composite;

	public AbstractPreviewLoader(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		control = createControl(composite);
		control.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent event) {
				updateControl(event);
			}
		});
	}

	Control getControl() {
		return control;
	}

	/**
	 * Prepares and return data that should be handled by this preview loader.
	 * IMPORTANT: This method is called in non-UI thread
	 * 
	 * @param path
	 * @param session
	 * @param monitor
	 * @return data that should be handled by this preview loader
	 */
	public abstract T prepareData(Path path, ISession session, ICancellable monitor);

	/**
	 * Updates data that will be drawn by this preview loader.
	 * IMPORTANT: This method is called in non-UI thread
	 * 
	 * @param previewData
	 */
	public abstract void updateData(T previewData);

	/**
	 * Creates and returns main control for this preview loader.
	 * 
	 * @param parent
	 * @return main control for this preview loader
	 */
	protected abstract Control createControl(Composite parent);

	/**
	 * Implementors should update/redraw control responsible for presenting preview
	 * loader data.
	 * 
	 * @param event
	 */
	protected abstract void updateControl(PaintEvent event);

}