package org.sds.file.browser.ui;

import org.eclipse.swt.graphics.Image;
import org.sds.file.browser.core.IContainer;
import org.sds.file.browser.core.IFile;

/**
 * Session viewer label provider.
 */
public interface ISessionLabelProvider {

	public Image getImage(IContainer element, boolean expanded);
	
	public Image getImage(IFile element);
	
	public String getText(IContainer element);
	
	public String getText(IFile element, int column); 
	
}
