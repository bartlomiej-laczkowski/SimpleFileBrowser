package org.sds.file.browser.core;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Messages accessor.
 */
public class Messages {

	private static final ResourceBundle messageBundle = ResourceBundle.getBundle("org.sds.file.browser.core.messages");

	public static final String MSG_APP_TITLE = "app.title";
	public static final String MSG_MENU_FILE_TITLE = "menu.file.title";
	public static final String MSG_MENU_FILE_CLOSE = "menu.file.close";
	public static final String MSG_MENU_HELP_TITLE = "menu.help.title";
	public static final String MSG_MENU_HELP_ABOUT = "menu.help.about";
	public static final String MSG_DIALOG_ABOUT_TITLE = "dialog.about.title";
	public static final String MSG_DIALOG_ABOUT_DESCRIPTION = "dialog.about.description";
	public static final String MSG_TABLE_COLUMN_FILE_NAME = "table.file.contents.name";
	public static final String MSG_TABLE_COLUMN_FILE_TYPE = "table.file.contents.type";
	public static final String MSG_TABLE_COLUMN_FILE_SIZE = "table.file.contents.size";
	public static final String MSG_TABLE_COLUMN_FILE_MODIFIED = "table.file.contents.modified";
	public static final String MSG_TABLE_FILE_TYPE_UNKNOWN = "table.file.contents.type.unknown";
	public static final String MSG_TABLE_FILE_TYPE_FILE = "table.file.contents.type.file";
	public static final String MSG_TABLE_FILE_TYPE_FOLDER = "table.file.contents.type.folder";
	public static final String MSG_TABLE_FILE_TYPE_LOCAL_DRIVE = "table.file.contents.type.volume";
	public static final String MSG_TABLE_FILE_SIZE_KB = "table.file.contents.size.kb";
	public static final String MSG_PREVIEW_NO_PREVIEW = "preview.no.preview";
	public static final String MSG_PREVIEW_SELECT_FILE = "preview.select.file";
	public static final String MSG_PREVIEW_LOADING = "preview.loading";
	public static final String MSG_LABEL_ROOT = "label.root";

	public static final String ERROR_COULD_NOT_LOAD_RESOURCE = "error.resource.load.failed";

	/**
	 * Returns message stored under given key.
	 * 
	 * @param key
	 * @return message stored under given key
	 */
	public static String get(String key) {
		try {
			return messageBundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		} catch (NullPointerException e) {
			return '!' + key + '!';
		}
	}

	/**
	 * Returns message stored under given key and created with the use of provided
	 * message pattern elements.
	 * 
	 * @param key
	 * @param args
	 * @return message stored under given key and created with the use of provided
	 *         message pattern elements
	 */
	public static String get(String key, Object... args) {
		try {
			return MessageFormat.format(get(key), args);
		} catch (MissingResourceException e) {
			return key;
		} catch (NullPointerException e) {
			return '!' + key + '!';
		}
	}

}
