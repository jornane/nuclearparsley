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
package net.sf.nuclearparsley.cli;

import java.io.File;
import java.io.IOException;

import net.sf.nuclearparsley.core.Atom;
import net.sf.nuclearparsley.core.ParentAtom;

/**
 * Program which will show all non-nested Atoms in a file
 */
public class AtomList {

	private AtomList() {/*no instantiating*/}

	/**
	 * Run the program using an input file.
	 * The output will be one line per {@link Atom},
	 * printing the name (4 bytes), starting position and length.
	 * @param args	Should contain the file being read.
	 */
	public static void main(String... args) {
		if (args.length != 1) {
			System.err.println("Usage: AtomList filename");
			System.exit(1);
		}
		try {
			ParentAtom children = Atom.fromFile(new File(args[0]));
			for(Atom a : children) {
				System.out.println(a.name+" "+a.start+" "+a.length);
			}
		} catch (IOException e) {
			System.err.println("Unable to read file "+args[0]);
			System.exit(2);
		}
	}

}
