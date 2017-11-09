package org.sds.file.browser.core;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Common executor service.
 */
public final class CommonExecutorService {

	private static final ExecutorService executor = Executors.newCachedThreadPool();

	public static Executor getExecutor() {
		return executor;
	}

	public static void shutdown() {
		executor.shutdown();
	}

}
