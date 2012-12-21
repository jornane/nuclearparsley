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
import net.sf.nuclearparsley.util.HexFormat;

/**
 * Program which will show all non-nested Atoms in a file
 */
public final class AtomList {

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
			System.out.println(
					atomToString(Atom.fromFile(new File(args[0])))
				);
		} catch (IOException e) {
			System.err.println("Unable to read file "+args[0]);
			System.exit(2);
		}
	}
	
	public static String atomToString(Atom atom) {
		StringBuilder sb = new StringBuilder();
		printAtom(atom, sb, (byte) 0);
		return sb.toString();
	}

	private static void printAtom(Atom atom, StringBuilder out, byte depth) {
		StringBuilder prefix = new StringBuilder();
		for(int i=0;i<depth;i++)
			prefix.append("\t");
		if (atom instanceof ParentAtom) {
			ParentAtom pa = (ParentAtom) atom;
			for(Atom childAtom : pa) {
				out.append(prefix);
				out.append("Atom "+childAtom.name+
						" @ "+childAtom.start+
						" of size: "+childAtom.length+
						", ends @ "+(childAtom.start+childAtom.length)
					);
				boolean bin = true;
				if (childAtom.length < 255 && !(childAtom instanceof ParentAtom)) {
					bin = false;
					byte[] payload = new byte[0];
					try {
						payload = childAtom.getPayload();
					} catch (IOException e) {}
					for(int i=0;i<payload.length && !bin;i++)
						bin = payload[i] < 32 || payload[i] >= 127;
					if (!bin) {
						out.append(prefix);
						out.append("\tValue \""+new String(payload)+"\"");
					}
					out.append(HexFormat.format(payload, prefix, childAtom.start, 0));
				} else
				out.append("\n");
				printAtom(childAtom, out, (byte) (depth+1));
			}
		}
	}

}
