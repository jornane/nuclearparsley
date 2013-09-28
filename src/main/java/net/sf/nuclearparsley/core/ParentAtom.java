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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.sf.nuclearparsley.util.HexFormat;

/**
 * Atom in a media file. The Parent Atom contains other Atoms.
 * @todo	Instead of simply compositing the internal {@link List},
 *      	the methods implemented from {@link List} should be implemented
 *      	in such a way that changes can be tracked,
 *      	so the modified {@link Atom}s can be saved.
 */
public class ParentAtom extends Atom implements List<Atom> {

	/**	{@link List} with all the children originally contained in this {@link Atom} */
	protected List<Atom> children;

	/**
	 * Construct a new {@link ParentAtom} and parse the content to find the children {@link Atom}s.
	 * @param name	4-character name of the {@link Atom}
	 * @param input	Datasource
	 * @param start	Starting pointer of this {@link Atom} in the Datasource
	 * @param length	Length of this {@link Atom} in bytes (including offset)
	 * @throws AtomException	Reading the {@link Atom} failed
	 * @throws IOException 
	 */
	protected ParentAtom(
			String name, File input, long start, long length, int offset)
				throws AtomException, IOException {
		super(name, input, start, length, offset);
		
		children = parse();
	}
	
	/**
	 * Parse the content of input to find the children {@link Atom}s
	 * @return	A {@link List} containing all the children of this {@link Atom}
	 * @throws IOException	Reading the file failed
	 */
	private List<Atom> parse() throws IOException {
		long pointer = start+offset;
		final ArrayList<Atom> result = new ArrayList<Atom>();
		final RandomAccessFile input = new RandomAccessFile(file, "r");
		try {
			while(pointer < start+length) {
				input.seek(pointer);
				long len = input.readInt() & 0x00000000FFFFFFFFL; // Unsigned positive integer
				final byte[] name = new byte[4];
				input.read(name);
				int offset = 0x8;
				if (len == 0 && name[0] == 0 && name[1] == 0 && name[2] == 0 && name[3] == 1) {
					// FIXME: I don't understand why some atoms have 0x0000000000000001 at their beginning,
					// and appear to be a normal atom from there
					// This has nothing to do with long (> 4 bytes) lengths, 
					// because they only have the len field 0x01, not len+name.
					len = input.readLong();
					input.read(name);
					offset += 0x8;
				}
				if (len == 1) {
					len = input.readLong();
					offset += 0x8;
				}
				if (pointer+len > start+length)
					throw new AtomException("Atom "+new String(name, Charset.forName("US-ASCII"))+" is larger than its enclosing atom.");
				result.add(Atom.instantiate(
						new String(name, Charset.forName("US-ASCII")),
						file,
						pointer,
						len,
						offset
					));
				if (len <= 0 || pointer + len <= pointer)
					throw new AtomException(
							"Pointer is overflowing after Atom \"" +
							new String(name, Charset.forName("US-ASCII"))+"\" at 0x"+Long.toHexString(pointer)+" and length "+len+". "
						);
				pointer += len;
			}
			return result;
		} finally {
			input.close();
		}
	}

	/** {@inheritDoc} */
	public int size() {
		return children.size();
	}

	/** {@inheritDoc} */
	public boolean isEmpty() {
		return children.isEmpty();
	}

	/** {@inheritDoc} */
	public boolean contains(Object o) {
		return children.contains(o);
	}

	/** {@inheritDoc} */
	public Iterator<Atom> iterator() {
		return children.iterator();
	}

	/** {@inheritDoc} */
	public Object[] toArray() {
		return children.toArray();
	}

	/** {@inheritDoc} */
	public <T> T[] toArray(T[] a) {
		return children.toArray(a);
	}

	/** {@inheritDoc} */
	public boolean add(Atom e) {
		return children.add(e);
	}

	/** {@inheritDoc} */
	public boolean remove(Object o) {
		return children.remove(o);
	}

	/** {@inheritDoc} */
	public boolean containsAll(Collection<?> c) {
		return children.containsAll(c);
	}

	/** {@inheritDoc} */
	public boolean addAll(Collection<? extends Atom> c) {
		return children.addAll(c);
	}

	/** {@inheritDoc} */
	public boolean addAll(int index, Collection<? extends Atom> c) {
		return children.addAll(c);
	}

	/** {@inheritDoc} */
	public boolean removeAll(Collection<?> c) {
		return children.removeAll(c);
	}

	/** {@inheritDoc} */
	public boolean retainAll(Collection<?> c) {
		return children.retainAll(c);
	}

	/** {@inheritDoc} */
	public void clear() {
		children.clear();
	}

	/** {@inheritDoc} */
	public Atom get(int index) {
		return children.get(index);
	}

	/** {@inheritDoc} */
	public Atom set(int index, Atom element) {
		return children.set(index, element);
	}

	/** {@inheritDoc} */
	public void add(int index, Atom element) {
		children.add(index, element);
	}

	/** {@inheritDoc} */
	public Atom remove(int index) {
		return children.remove(index);
	}

	/** {@inheritDoc} */
	public int indexOf(Object o) {
		return children.indexOf(o);
	}

	/** {@inheritDoc} */
	public int lastIndexOf(Object o) {
		return children.lastIndexOf(o);
	}

	/** {@inheritDoc} */
	public ListIterator<Atom> listIterator() {
		return children.listIterator();
	}

	/** {@inheritDoc} */
	public ListIterator<Atom> listIterator(int index) {
		return children.listIterator(index);
	}

	/** {@inheritDoc} */
	public List<Atom> subList(int fromIndex, int toIndex) {
		return children.subList(fromIndex, toIndex);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return children.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		try {
			return ((ParentAtom) obj).children.equals(children);
		} catch (Exception e) {
			return false;
		}
	}

}
