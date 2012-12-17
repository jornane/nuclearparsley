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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Atom in a media file.
 * This is the generic Atom type which does not imply anything about it's contents
 */
public class Atom {

	/**
	 * Instantiate an {@link Atom} from a {@link File}
	 * @param file	The {@link File} to instantiate the {@link Atom} from
	 * @return	The {@link Atom} (this will always be a {@link ParentAtom}
	 * @throws IOException	If reading the {@link File} fails for some reason.
	 */
	public static ParentAtom fromFile(File file) throws IOException {
		RandomAccessFile input = new RandomAccessFile(file, "r");
		return new ParentAtom(null, input, 0, input.length());
	}
	
	/** The datasource */
	protected final RandomAccessFile input;
	/** Starting pointer of the original (unmodified) Atom */
	public final long start;
	/** Length of the original (unmodified) Atom */
	public final long length;
	/** Name of the original (unmodified) Atom */
	public final String name;

	/**
	 * Construct a new generic Atom
	 * @param name	4-character name of the {@link Atom}
	 * @param input	Datasource
	 * @param start	Starting pointer of this {@link Atom} in the Datasource
	 * @param length	Length of this {@link Atom} in bytes
	 */
	protected Atom(String name, RandomAccessFile input, long start, long length) {
		assert name == null || name.length() == 4;
		this.name = name;
		this.input = input;
		this.start = start;
		this.length = length;
	}
	
}
