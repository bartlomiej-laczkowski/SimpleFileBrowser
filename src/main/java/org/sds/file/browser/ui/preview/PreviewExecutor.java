package org.sds.file.browser.ui.preview;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Common executor for all preview viewers. It is responsible for preparing
 * preview data in a separate non-UI thread.
 */
public enum PreviewExecutor {

	INSTANCE;

	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	Future<?> submit(Runnable task) {
		return executor.submit(task);
	}

	public void shutdown() {
		executor.shutdown();
	}

}
