package org.sds.file.browser.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.sds.file.browser.core.Messages;

/**
 * Images registry.
 */
public final class ImageRegistry {

	public enum Images {

		IMG_FILE_BROWSER("icon-file-browser.gif"),
		IMG_LOCAL_SESSION("icon-local-session.gif"), 
		IMG_FTP_SESSION("icon-ftp-session.gif"), 
		IMG_FTP_FOLDER("icon-ftp-folder.gif"), 
		IMG_DRIVE("icon-drive.gif"), 
		IMG_FILE("icon-file.gif"), 
		IMG_FILE_HIDDEN("icon-file-hidden.gif"), 
		IMG_FILE_IMAGE("icon-image-file.gif"), 
		IMG_FOLDER("icon-folder.gif"), 
		IMG_FOLDER_HIDDEN("icon-folder-hidden.gif"), 
		IMG_FOLDER_OPEN("icon-folder-open.gif"),
		IMG_FOLDER_HIDDEN_OPEN("icon-folder-hidden-open.gif"),
		IMG_ARROW_UP("icon-arrow-up.gif"),
		IMG_ERROR("icon-error.gif"),
		IMG_SANDGLASS("icon-sandglass.gif");

		private Images(String location) {
			this.location = location;
		}

		private String location;

	}

	private static Map<Images, Image> imageCache = null;
	private static Map<Program, Image> appImageCache = new HashMap<>();

	public static void init(Display display) {
		if (imageCache == null) {
			imageCache = new HashMap<>();

			for (Images descriptor : Images.values()) {
				Image image = createImage(display, descriptor.location);
				if (image == null) {
					dispose();
					throw new IllegalStateException(Messages.get(Messages.ERROR_COULD_NOT_LOAD_RESOURCE));
				}
				imageCache.put(descriptor, image);
			}
		}
	}

	private static Image createImage(Display display, String path) {
		InputStream stream = ImageRegistry.class.getClassLoader().getResourceAsStream("icons/" + path);
		ImageData imageData = new ImageData(stream);
		ImageData mask = imageData.getTransparencyMask();
		Image result = new Image(display, imageData, mask);
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void dispose() {
		if (imageCache != null) {
			for (Image image : imageCache.values()) {
				if (image != null)
					image.dispose();
			}
			imageCache = null;
		}
		if (appImageCache != null) {
			for (Image image : appImageCache.values()) {
				if (image != null)
					image.dispose();
			}
			appImageCache = null;
		}
	}

	public static Image getImage(Images descriptor) {
		return imageCache.get(descriptor);
	}
	
	public static Image getImage(Program program) {
		Image image = appImageCache.get(program);
		if (image == null) {
			ImageData imageData = program.getImageData();
			if (imageData != null) {
				image = new Image(null, imageData, imageData.getTransparencyMask());
				appImageCache.put(program, image);
			} else {
				image = getImage(Images.IMG_FILE);
			}
		}
		return image;
	}

}
