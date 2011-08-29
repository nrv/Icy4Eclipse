/*
 * Copyright 2011 Nicolas Hervé.
 * 
 * This file is part of Icy4Eclipse.
 * 
 * Icy4Eclipse is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Icy4Eclipse is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Icy4Eclipse. If not, see <http://www.gnu.org/licenses/>.
 */

package icy.icy4eclipse.core;

/**
 * @author Nicolas Hervé - n.herve@laposte.net
 * 
 */
public class Icy4EclipseException extends Exception {
	private static final long serialVersionUID = 8657898346139444165L;

	public Icy4EclipseException() {
		super();
	}

	public Icy4EclipseException(String message, Throwable cause) {
		super(message, cause);
	}

	public Icy4EclipseException(String message) {
		super(message);
	}

	public Icy4EclipseException(Throwable cause) {
		super(cause);
	}

}
