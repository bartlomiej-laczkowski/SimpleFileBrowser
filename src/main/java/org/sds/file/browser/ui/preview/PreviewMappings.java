package org.sds.file.browser.ui.preview;

import static org.sds.file.browser.ui.preview.PreviewMappings.PreviewType.*;

import java.util.HashMap;
import java.util.Map;

import org.sds.file.browser.core.IFile;

/**
 * File extensions to preview loader type mappings.
 */
final class PreviewMappings {
	
	public enum PreviewType {
		
		UNKNOWN, IMAGE, TEXT, BROWSER;
		
	}
	
	@SuppressWarnings("serial")
	private static final Map<String, PreviewType> mappings = new HashMap<String, PreviewType>() {
		{
			// Supported image formats
			put("bmp", IMAGE);
			put("ico", IMAGE);
			put("jpeg", IMAGE);
			put("jpg", IMAGE);
			put("gif", IMAGE);
			put("png", IMAGE);
			put("tiff", IMAGE);
			// Supported text content formats
			put("txt", TEXT);
			put("rtf", TEXT);
			put("bat", TEXT);
			put("sh", TEXT);
			put("log", TEXT);
			put("ini", TEXT);
			// Supported browser formats
			put("html", BROWSER);
			put("htm", BROWSER);
			put("xml", BROWSER);
		}
	};

	public static PreviewType getMapping(IFile file) {
		String fileName = file.getName();
		int dot = fileName.lastIndexOf('.');
		if (dot != -1) {
			String extension = fileName.substring(dot + 1).toLowerCase();
			PreviewType mapping = mappings.get(extension);
			return mapping != null ? mapping : UNKNOWN;
		}
		return UNKNOWN;
	}
	
}
