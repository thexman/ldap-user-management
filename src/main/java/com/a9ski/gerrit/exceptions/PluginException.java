/**
 * Copyright (C) 2013 Kiril Arabadzhiyski
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.a9ski.gerrit.exceptions;

public class PluginException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5408458300195635465L;

	/**
	 * Creates new runtime exception
	 */
	public PluginException() {
		// do nothing
	}

	/**
	 * Creates a new runtime exception with provided message
	 * 
	 * @param message
	 *            the exception message
	 */
	public PluginException(String message) {
		super(message);
	}

	/**
	 * Creates new runtime exception with provided cause
	 * 
	 * @param cause
	 *            the exception cause
	 */
	public PluginException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates new runtime exception with provided message and cause
	 * 
	 * @param message
	 *            the exception message
	 * @param cause
	 *            the exception cause
	 */
	public PluginException(String message, Throwable cause) {
		super(message, cause);
	}
}
