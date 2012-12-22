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
import java.util.Arrays;

import net.sf.nuclearparsley.core.Atom;
import net.sf.nuclearparsley.core.ParentAtom;
import net.sf.nuclearparsley.util.HexFormat;

/**
 * Program which will show all non-nested Atoms in a file
 */
public class AtomList {

	public static final String USAGE =
			"Usage: AtomList [ -d|-D ] [ -s bytes ] [ -v|-V ] [ -x|-X ] [ -- ] filename [ atom path ]";
	
	public boolean printAddress = true; // -d
	public long maxHumanSize = 255; // -s
	public boolean printStringValue = true; // -v
	public boolean printHex = false; // -x

	/**
	 * Run the program using an input file.
	 * The output is similar to the output of AtomicParsley.
	 * @param args	Should contain the file being read.
	 */
	public static void main(String... args) {
		AtomList cli = new AtomList();
		File file = null;
		String[] atomPath = new String[0];
		boolean forceFile = false;
		for(int i=0;i<args.length;i++) {
			if (!forceFile && args[i].charAt(0) == '-') {
				if (args[i].length() != 2) {
					System.err.println("Invalid flag: "+args[i]);
					System.err.println(USAGE);
					System.exit(3);
				}
				switch(args[i].charAt(1)) {
				case 'd':cli.printAddress = false;break;
				case 'D':cli.printAddress = true;break;
				case 'v':cli.printStringValue = false;break;
				case 'V':cli.printStringValue = true;break;
				case 'x':cli.printHex = false;break;
				case 'X':cli.printHex = true;break;
				case 's':cli.maxHumanSize = Integer.parseInt(args[i+1]);break;
				case '-':forceFile = true;
				}
			} else if (file == null) {
				file = new File(args[i]);
			} else if (atomPath.length == 0) {
				atomPath = args[i].split("\\.");
			} else {
				System.err.println("Only one file can be specified");
				System.err.println(USAGE);
				System.exit(3);
			}
		}
		if (file == null) {
			System.err.println(USAGE);
			System.exit(1);
		}
		Atom atom = null;
		try {
			atom = Atom.fromFile(file);
		} catch (IOException e) {
			System.err.println("Unable to read file "+args[0]);
			System.exit(2);
		}
		
		atom = resolveAtom(atom, atomPath);
		System.out.println(cli.atomToString(atom));
	}
	
	/**
	 * Find the atom indicated by the path in the atom tree
	 * @param atom	The root atom
	 * @param path	The path to the desired atom
	 * @return	The desired atom
	 */
	protected static Atom resolveAtom(Atom atom, String... path) {
		if (path.length == 0)
			return atom;
		if (!(atom instanceof ParentAtom))
			return null;
		if (path[0].length() < 4)
			throw new IllegalArgumentException("An atom name consists of four bytes");
		String name = path[0];
		int index = 0;
		if (path[0].length() > 4) 
			if (path[0].charAt(4) == '[' && path[0].charAt(path[0].length()-1) == ']') {
			name = path[0].substring(0, 4);
			index = Integer.parseInt(path[0].substring(5, path[0].length()-1));
			} else
				throw new IllegalArgumentException("An atom name consists of four bytes");
		for(Atom child : (ParentAtom)atom) {
			if (name.equals(child.name))
				if (index == 0)
					if (path.length == 1)
						return child;
					else
						return resolveAtom(child, 
								Arrays.asList(path)
								.subList(1, path.length)
								.toArray(new String[0]));
				else
					index--;
		}
		return null;
	}

	/**
	 * Convenience function to convert an atom to a string
	 * It will display a tree, hiding the root node if the name is null
	 * @param atom	The root atom
	 * @return	The tree
	 */
	public String atomToString(Atom atom) {
		StringBuilder sb = new StringBuilder();
		if (atom instanceof ParentAtom)
			for (Atom childAtom : (ParentAtom)atom)
				printAtom(childAtom, sb, (byte) 0);
		else
			printAtom(atom, sb, (byte) 0);
		return sb.toString();
	}

	/**
	 * Print an atom
	 * @param atom	The atom to print
	 * @param out	The output (where to print)
	 * @param depth	Depth inside the tree (used for indenting, start with 0)
	 */
	protected void printAtom(Atom atom, StringBuilder out, byte depth) {
		StringBuilder prefix = new StringBuilder();
		for(int i=0;i<depth;i++)
			prefix.append("\t");
		out.append(prefix);
		out.append("Atom "+atom.name);
		if (printAddress)
			out.append(" @ "+atom.start+
					" of size: "+atom.length+
					", ends @ "+(atom.start+atom.length)
				);
		boolean bin = true;
		if (atom.length < maxHumanSize && !(atom instanceof ParentAtom)) {
			bin = false;
			byte[] payload = new byte[0];
			try {
				payload = atom.getPayload();
			} catch (IOException e) {}
			for(int i=0;i<payload.length && !bin;i++)
				bin = payload[i] < 32 || payload[i] >= 127;
			if (!bin && printStringValue) {
				out.append("\n");
				out.append(prefix);
				out.append("\tValue \""+new String(payload)+"\"");
			}
			if (printHex)
				out.append(HexFormat.format(payload, prefix, atom.start, 0));
			else
				out.append("\n");
		} else
			out.append("\n");
		if (atom instanceof ParentAtom)
			for (Atom childAtom : (ParentAtom)atom)
			printAtom(childAtom, out, (byte) (depth+1));
	}

}
