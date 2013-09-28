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
package net.sf.nuclearparsley.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * An input stream that limits which bytes from the underlying input stream can be read.
 */
public class LimitedInputStream extends InputStream {

	/**	The input stream being actually read from */
	final protected InputStream in;
	/**	The amount of bytes read/skipped already (not including the initial skip) */
	protected long read;
	/** The maximal amount of bytes that may be read */
	public final long max;
	/** The mark (needed to reset the read variable when {@link #reset()} is called) */
	protected long mark;

	/**
	 * Creates an input stream
	 * limited to reading <code>length</code> bytes
	 * from <code>start</code>.
	 * @param in	the input stream
	 * @param start	first readable byte
	 * @param length	last readable byte,
	 * 		counting from the first readable byte - not the first byte in the stream
	 * @throws IOException When the input stream cannot be seeked to the initial start position
	 */
	public LimitedInputStream(InputStream in, long start, long length) throws IOException {
		in.skip(start);
		this.in = in;
		read = 0;
		max = length;
	}
	
	/** {@inheritDoc} */
	public int available() throws IOException {
 		return in.available();
	}
	
	public void close() throws IOException {
		in.close();
	}

	/** {@inheritDoc} */
	public void mark(int readLimit) {
		in.mark(readLimit);
		mark = read;
	}
	
	public boolean markSupported() {
		return in.markSupported();
	}
	
	/** {@inheritDoc} */
	@Override
	public int read() throws IOException {
		if (read+1 >= max)
			return -1;
		int result = in.read();
		read++;
		return result;
	}
	
	/** {@inheritDoc} */
	public int read(byte b[]) throws IOException {
		return read(b, 0, b.length);
	}
	
	/** {@inheritDoc} */
	public int read(byte b[], int off, int len) throws IOException {
		if (read+len >= max) {
			assert (max-read & 0xFFFFFFF800000000L) == 0;
			len = (int) (max-read);
		}
		final int result = in.read(b, off, len);
		read += result;
		return result;
	}

	/** {@inheritDoc} */
	public void reset() throws IOException {
		in.reset();
		read = mark;
	}
	
	/** {@inheritDoc} */
	public long skip(long n) throws IOException {
		final long result = in.skip(n);
		read += result;
		return result;
	}

}
