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
	
	private int severity = OK;
	private String message;
	private Throwable exception = null;
	
	public Status(int severity, String message, Throwable exception) {
		super();
		this.severity = severity;
		this.message = message;
		this.exception = exception;
	}

	public Status(int severity, String message) {
		super();
		this.severity = severity;
		this.message = message;
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
