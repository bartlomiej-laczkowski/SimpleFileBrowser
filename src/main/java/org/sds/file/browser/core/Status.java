package org.sds.file.browser.core;

/**
 * Simple status implementation.
 */
public class Status {

	public static final int OK = 0;
	public static final int INFO = 0x01;
	public static final int WARNING = 0x02;
	public static final int ERROR = 0x04;
	public static final int CANCEL = 0x08;
	
	public static final Status OK_STATUS = new Status(OK, "OK");
	
	private final int severity;
	private final String message;
	private final Throwable exception;
	
	public Status(int severity, String message, Throwable exception) {
		this.severity = severity;
		this.message = message;
		this.exception = exception;
	}

	public Status(int severity, String message) {
		this(severity, message, null);
	}

	public int getSeverity() {
		return severity;
	}

	public String getMessage() {
		return message;
	}

	public Throwable getException() {
		return exception;
	}
	
	public boolean isOK() {
		return severity == OK;
	}
	
}
