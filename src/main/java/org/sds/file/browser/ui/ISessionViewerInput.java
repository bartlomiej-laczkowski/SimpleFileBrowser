package org.sds.file.browser.ui;

import org.eclipse.swt.graphics.Image;
import org.sds.file.browser.core.ISession;

/**
 * Common interface for session viewer input.
 */
public interface ISessionViewerInput {

	/**
	 * Returns viewer input session.
	 * 
	 * @return viewer input session
	 */
	ISession getSession();

	/**
	 * Returns session's viewer tab name.
	 * 
	 * @return session's viewer tab name
	 */
	String getName();
	
	/**
	 * Returns session's viewer tab icon.
	 * 
	 * @return session's viewer tab icon
	 */
	Image getImage();
	
	/**
	 * Returns label provider for this input.
	 * 
	 * @return label provider for this input
	 */
	ISessionLabelProvider getLabelProvider();
	
}
