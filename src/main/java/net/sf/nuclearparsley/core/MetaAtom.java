package net.sf.nuclearparsley.core;

import java.io.File;
import java.io.IOException;

/**
 * @author @jornane
 *
 */
public class MetaAtom extends ParentAtom {

	public static final String NAME = "meta";

	public static final int OFFSET = 4;

	/**
	 * @param input	Atoms are read from this file
	 * @param start	Starting pointer of this {@link Atom} in input file
	 * @param length	Length of this {@link Atom} in bytes (including offset)
	 * @param offset	The starting point of the atom payload from the start of the atom
	 * @throws AtomException	Reading the {@link Atom} failed
	 * @throws IOException	Reading the file failed
	 */
	protected MetaAtom(File input, long start, long length, int offset)
			throws AtomException, IOException {
		super(NAME, input, start, length, offset+OFFSET);
		// TODO Auto-generated constructor stub
	}

}
