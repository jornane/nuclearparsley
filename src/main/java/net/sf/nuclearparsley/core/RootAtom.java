package net.sf.nuclearparsley.core;

import java.io.File;
import java.io.IOException;

/**
 * Root Atom which holds all Atoms contained in a file.
 */
public class RootAtom extends ParentAtom {

	/**
	 * Construct a new RootAtom from a file
	 * The Atom spans the entire payload of the file 
	 * @param input	The input file for this Atom
	 * @throws AtomException	Reading the {@link Atom} failed
	 * @throws IOException	Reading the file failed
	 */
	public RootAtom(File input)
			throws AtomException, IOException {
		super(null, input, 0, input.length(), 0);
	}

}
