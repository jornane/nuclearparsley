/*
 * Nuclear Parsley - GPL 3.0 licensed
 * Copyright (C) 2012  Yørn de Jong
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.nuclearparsley.core;

/**
 * Indicating that something went wrong while parsing or writing an {@link Atom}.
 */
public class AtomException extends RuntimeException {
	/** Basic {@link RuntimeException} implementation */
	private static final long serialVersionUID = 1L;

	/**
	 * Declare an {@link AtomException} with a message
	 * @param message	The message indicating what went wrong
	 */
	public AtomException(String message) {
		super(message);
	}

	/**
	 * Declare an {@link AtomException} with a cause
	 * @param cause	The exception that caused the {@link AtomException} to be thrown
	 */
	public AtomException(Throwable cause) {
		super(cause);
	}

	/**
	 * Declare an {@link AtomException} with a message and a cause
	 * @param message	The message indicating what went wrong
	 * @param cause	The exception that caused the {@link AtomException} to be thrown
	 */
	public AtomException(String message, Throwable cause) {
		super(message, cause);
	}

}
