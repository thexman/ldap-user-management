package com.a9ski.gerrit.ldap.exceptions;

import com.a9ski.gerrit.exceptions.PluginException;

public class UserNotFoundException extends PluginException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 163525022927020099L;

	public UserNotFoundException() {
		// do nothing
	}

	public UserNotFoundException(String message) {
		super(message);
	}

	public UserNotFoundException(Throwable cause) {
		super(cause);
	}

	public UserNotFoundException(String message, Throwable cause) {
		super(message, cause);	
	}
}
