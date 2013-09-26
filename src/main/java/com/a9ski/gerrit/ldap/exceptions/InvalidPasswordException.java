package com.a9ski.gerrit.ldap.exceptions;

import com.a9ski.gerrit.exceptions.PluginException;

public class InvalidPasswordException extends PluginException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3832983987717613792L;

	public InvalidPasswordException() {
		// do nothing
	}

	public InvalidPasswordException(String message) {
		super(message);
	}

	public InvalidPasswordException(Throwable cause) {
		super(cause);
	}

	public InvalidPasswordException(String message, Throwable cause) {
		super(message, cause);	
	}
}
