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

import net.sf.nuclearparsley.util.LimitedInputStream;

/**
 * Atom in a media file.
 * This is the generic Atom type which does not imply anything about its contents.
 */
public class Atom implements Cloneable {

	/**
	 * Instantiate an {@link Atom} from a {@link File}
	 * @param file	The {@link File} to instantiate the {@link Atom} from
	 * @return	The {@link RootAtom} in file
	 * @throws IOException	If reading the {@link File} fails for some reason.
	 */
	public static RootAtom fromFile(File file) throws IOException {
		return new RootAtom(file);
	}
	
	/**
	 * Read an atom from a file. 
	 * 
	 * @param name	4 bytes name of the Atom
	 * @param file	File containing the Atom
	 * @param pointer	Starting pointer of the original (unmodified) Atom
	 * @param len	Length of the original (unmodified) Atom
	 * @param offset	Location of the payload data relative to the start
	 * @return	Atom object matching the input data as specific as possible
	 * @throws IOException
	 */
	protected static Atom instantiate(String name, File file, long pointer, long len, int offset)
			throws IOException {
		try {
			return new ParentAtom(name, file, pointer, len, offset);
		} catch (AtomException e) {
			return new Atom(name, file, pointer, len, offset, e);
		}
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
	/** The reason why a more specific {@link Atom} could not be used */
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
	protected Atom(String name, File file, long start, long length, int offset,
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
	 * Get the header of this {@link Atom}.
	 * The header consists of all bytes from the beginning of the atom up to the start of the payload.
	 * Thus, the size of the header is the same as {@link #offset}.
	 * @throws IOException Reading the file fails
	 */
	public byte[] getHeader() throws IOException {
		final InputStream input = getStream();
		try {
			final byte[] result = new byte[offset];
			input.read(result);
			return result;
		} finally {
			if (input != null)
				input.close();
		}
	}
	
	/**
	 * Get the payload of this {@link Atom}.
	 * Better not call this on <code>mdat</code> or any other {@link Atom} bigger than a few megabytes.
	 * Look at {@link #getPayloadStream()} for reading these bigger {@link Atom}s.
	 * @throws IOException Reading the file fails
	 */
	public byte[] getPayload() throws IOException {
		int l = (int) (0x7FFFFFFFL & length-offset);
		if (l != length-offset)
			throw new UnsupportedOperationException(
					"Atom "+name+" is bigger than 2^31 bytes. " +
					"Java does not support byte arrays that big. " +
					"Try #getPayloadStream()"
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
	 * Get a stream which contains the header and the payload
	 * @return	the stream
	 * @throws IOException	seeking within the file to find the payload failed
	 */
	public InputStream getStream() throws IOException {
		return new LimitedInputStream(new FileInputStream(file), start, length);
	}

	/**
	 * Get a stream which contains the payload
	 * @return	the stream
	 * @throws IOException	seeking within the file to find the payload failed
	 */
	public InputStream getPayloadStream() throws IOException {
		return new LimitedInputStream(new FileInputStream(file), start+offset, length-offset);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		result = prime * result + (int) (length ^ (length >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + offset;
		result = prime * result + (int) (start ^ (start >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Atom other = (Atom) obj;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		if (length != other.length)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (offset != other.offset)
			return false;
		if (start != other.start)
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Atom clone() throws CloneNotSupportedException {
		return (Atom) super.clone();
	}
	
}
