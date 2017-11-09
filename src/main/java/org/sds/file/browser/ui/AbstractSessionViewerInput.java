package org.sds.file.browser.ui;

import org.sds.file.browser.core.ISession;

/**
 * Abstract session viewer input.
 */
public abstract class AbstractSessionViewerInput implements ISessionViewerInput {

	private final String name;
	private final ISession session;
	
	public AbstractSessionViewerInput(String name, ISession session) {
		this.name = name;
		this.session = session;
	}

	@Override
	public ISession getSession() {
		return session;
	}

	@Override
	public String getName() {
		return name;
	}

}
