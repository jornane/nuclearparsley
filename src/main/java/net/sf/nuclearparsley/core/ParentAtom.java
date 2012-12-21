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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
	 * @param length	Length of this {@link Atom} in bytes
	 * @throws AtomException	Reading the {@link Atom} failed
	 */
	protected ParentAtom(
			String name, File input, long start, long length, int offset)
				throws AtomException {
		super(name, input, start, length, offset);
		
		try {
			children = parse();
		} catch (IOException e) {
			throw new AtomException(
					"The stream containing the Atom could not be read while trying to read the children.",
					e
				);
		}
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
				long len = input.readInt() & 0x00000000FFFFFFFFL;
				final byte[] name = new byte[4];
				input.read(name);
				int offset = 0x8;
				if (len == 1) {
					len = input.readLong();
					offset = 0x10;
				}
				result.add(Atom.instantiate(new String(name), file, pointer, len, offset));
				if (len <= 0 || pointer + len <= pointer)
					throw new AtomException(
							"Pointer is overflowing after Atom \"" +
							new String(name)+"\" at address "+pointer+" and length "+len+". "
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

}
