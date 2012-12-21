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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sf.nuclearparsley.util.LimitedInputStream;

/**
 * Atom in a media file.
 * This is the generic Atom type which does not imply anything about it's contents
 */
public class Atom {

	/**
	 * List of all known {@link ParentAtom}s
	 */
	public static final List<String> PARENTS = Collections.unmodifiableList(
			Arrays.asList(
					new String[] {
							null,
							"moov",
							"trak",
							"mdia",
							"minf",
							"dinf",
							"stbl",
							"stsd",
							"avc1",
							"tref",
							"edts",
							"mp4a",
							"udta",
							"ilst",
							"meta",
							new String(
									new byte[]{(byte) 0xA9, 0x74, 0x6F, 0x6F}
								), // ©too
						}
				)
		);
	
	/**
	 * Instantiate an {@link Atom} from a {@link File}
	 * @param file	The {@link File} to instantiate the {@link Atom} from
	 * @return	The {@link Atom} (this will always be a {@link ParentAtom}
	 * @throws IOException	If reading the {@link File} fails for some reason.
	 */
	public static ParentAtom fromFile(File file) throws IOException {
		RandomAccessFile input = new RandomAccessFile(file, "r");
		long length = input.length();
		input.close();
		return new ParentAtom(null, file, 0, length, 0);
	}
	
	/**
	 * 
	 * @param string
	 * @param file
	 * @param pointer
	 * @param len
	 * @param offset 
	 * @return
	 * @throws IOException
	 */
	protected static Atom instantiate(String name, File file, long pointer, long len, int offset)
			throws IOException {
		try {
			if (PARENTS.contains(name))
				return new ParentAtom(name, file, pointer, len, offset);
		} catch (Exception e) {
			return new Atom(name, file, pointer, len, offset, e);
		}
		return new Atom(name, file, pointer, len, offset);
	}
	
	/** The datasource */
	public final File file;
	/** Starting pointer of the original (unmodified) Atom */
	public final long start;
	/** Length of the original (unmodified) Atom */
	public final long length;
	/** Location of the payload data relative to the start */
	public final int offset;
	/** Name of the original (unmodified) Atom */
	public final String name;
	/** The reason the more specific {@link Atom} could not be used */
	public final Exception error;

	/**
	 * Construct a new generic Atom
	 * @param name	4-character name of the {@link Atom}
	 * @param file	Datasource
	 * @param start	Starting pointer of this {@link Atom} in the Datasource
	 * @param length	Length of this {@link Atom} in bytes
	 * @param offset	The starting point of this atom in the file
	 */
	protected Atom(String name, File file, long start, long length, int offset) {
		this(name, file, start, length, offset, null);
	}
	
	/**
	 * 
	 * @param name	4-character name of the {@link Atom}
	 * @param file	Datasource
	 * @param start	Starting pointer of this {@link Atom} in the Datasource
	 * @param length	Length of this {@link Atom} in bytes
	 * @param offset	The starting point of this atom in the file
	 * @param error	The reason the more specific {@link Atom} could not be used
	 */
	public Atom(String name, File file, long start, long length, int offset,
			Exception error) {
		assert name == null || name.length() == 4;
		this.name = name;
		this.file = file;
		this.start = start;
		this.length = length;
		this.offset = offset;
		this.error = error;
	}
	
	/**
	 * Get the payload of this {@link Atom}.
	 * Better not call this on <code>mdat</code> or any other {@link Atom} bigger than a few megabytes.
	 * @throws IOException Reading the file fails
	 */
	public byte[] getPayload() throws IOException {
		int l = (int) (0x7FFFFFFFL & length);
		if (l != length)
			throw new UnsupportedOperationException(
					"Atom "+name+" is bigger than 2^31 bytes. " +
					"Java does not support byte arrays that big."
				);
		final InputStream input = getPayloadStream();
		try {
			final byte[] result = new byte[l];
			input.read(result);
			return result;
		} finally {
			if (input != null)
				input.close();
		}
	}
	
	/**
	 * Get a stream which contains the payload
	 * @return	the stream
	 * @throws IOException	seeking within the file to find the payload failed
	 */
	public InputStream getPayloadStream() throws IOException {
		return new LimitedInputStream(new FileInputStream(file), start+offset, length-offset);
	}
	
}
