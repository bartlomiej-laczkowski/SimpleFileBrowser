package org.sds.file.browser.ui.preview;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.sds.file.browser.core.ICancellable;
import org.sds.file.browser.core.ISession;
import org.sds.file.browser.core.IStreamReader;

/**
 * Image preview loader.
 */
class ImagePreviewLoader extends AbstractPreviewLoader<ImageData> {

	private static final class DataReader implements IStreamReader<ImageData> {

		private static final int BUFFER_SIZE = 2048;
		
		private ImageData result;

		@Override
		public void read(InputStream inputStream, ICancellable monitor) {
			try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
				int read;
				byte[] data = new byte[BUFFER_SIZE];
				while ((read = inputStream.read(data, 0, data.length)) != -1) {
					buffer.write(data, 0, read);
					if (monitor.isCanceled()) {
						return;
					}
				}
				buffer.flush();
				result = new ImageData(new ByteArrayInputStream(buffer.toByteArray()));
			} catch (IOException e) {
				// TODO logging
			} catch (SWTException ex) {
				// TODO logging
			}
		}

		@Override
		public ImageData getResult() {
			return result;
		}

	}

	private ImageData imageData;

	public ImagePreviewLoader(Composite parent) {
		super(parent);
	}

	protected Control createControl(Composite parent) {
		return new Canvas(parent, SWT.NONE);
	}

	@Override
	protected void updateControl(PaintEvent event) {
		if (imageData == null)
			return;
		GC gc = event.gc;
		float maxWidth = getControl().getSize().x;
		float maxHeight = getControl().getSize().y;
		Image originalImage = new Image(getControl().getDisplay(), imageData);
		// Some math to check scale factor, etc.
		float imgWidth = imageData.width;
		float imgHeight = imageData.height;
		float scaleWidth = maxWidth / imgWidth;
		float scaleHeight = maxHeight / imgHeight;
		float scale = Math.min(scaleHeight, scaleWidth);
		if (scale >= 1) {
			// No need to scale anything, original will fit...
			gc.drawImage(originalImage, ((int) (maxWidth - imgWidth) / 2), ((int) (maxWidth - imgHeight) / 2));
			// Dispose!
			originalImage.dispose();
			return;
		}
		int newWidth = (int) (imgWidth * scale);
		int newHeight = (int) (imgHeight * scale);
		// Prepare scaled image
		Image scaledImage = new Image(Display.getDefault(), newWidth, newHeight);
		GC tmpGC = new GC(scaledImage);
		tmpGC.setAntialias(SWT.ON);
		tmpGC.setInterpolation(SWT.LOW);
		tmpGC.drawImage(originalImage, 0, 0, imageData.width, imageData.height, 0, 0, newWidth, newHeight);
		// Draw prepared image in the center
		int x = (int) Math.floor((maxWidth - newWidth) / 2);
		int y = (int) Math.floor((maxHeight - newHeight) / 2);
		gc.drawImage(scaledImage, x, y);
		// Dispose!
		tmpGC.dispose();
		originalImage.dispose();
		scaledImage.dispose();
	}

	@Override
	public ImageData prepareData(Path path, ISession session, ICancellable monitor) {
		IStreamReader<ImageData> streamReader = new DataReader();
		session.readData(streamReader, path, monitor);
		return streamReader.getResult();
	}

	@Override
	public void updateData(ImageData previewData) {
		imageData = previewData;
	}

}