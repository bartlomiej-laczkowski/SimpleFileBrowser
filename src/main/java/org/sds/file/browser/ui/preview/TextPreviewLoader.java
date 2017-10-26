package org.sds.file.browser.ui.preview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.sds.file.browser.core.ICancellable;
import org.sds.file.browser.core.ISession;
import org.sds.file.browser.core.IStreamReader;

/**
 * Text file preview loader.
 */
class TextPreviewLoader extends AbstractPreviewLoader<StringBuffer> {

	private static final class DataReader implements IStreamReader<StringBuffer> {

		private StringBuffer result;
		
		@Override
		public void read(InputStream inputStream, ICancellable monitor) {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
				String line;
				result = new StringBuffer();
				while ((line = br.readLine()) != null && !monitor.isCanceled()) {
					result.append(line);
					result.append(System.getProperty("line.separator"));
				}
			} catch (IOException e) {
				// TODO log
			}
		}

		@Override
		public StringBuffer getResult() {
			return result;
		}
		
	}
	
	private StringBuffer textContent;
	private Text textArea;

	public TextPreviewLoader(Composite parent) {
		super(parent);
	}

	protected Control createControl(Composite parent) {
		textArea = new Text(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		textArea.setEditable(false);
		return textArea;
	}

	@Override
	protected void updateControl(PaintEvent event) {
		if (textContent != null) {
			textArea.setText(textContent.toString());
			textContent = null;
		}
	}

	@Override
	public StringBuffer prepareData(Path path, ISession session, ICancellable monitor) {
		IStreamReader<StringBuffer> streamReader = new DataReader();
		session.readData(streamReader, path, monitor);
		return streamReader.getResult();
	}

	@Override
	public void updateData(StringBuffer previewData) {
		textContent = previewData;
	}

}