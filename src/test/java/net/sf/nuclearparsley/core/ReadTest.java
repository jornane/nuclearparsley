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

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public final class ReadTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private File basicTest;
	private File negativeTest;

	private File longLengthTest;
	
	/** Atom payload with 0x0C length (correct) 
	 * 0x66,0x74,0x79,0x70 = "ftyp"
	 * 0x74,0x65,0x73,0x74 = "test"
	 */
	public static final byte[] BASICTEST = new byte[]{
			0x00,0x00,0x00,0x0C,
			0x66,0x74,0x79,0x70,
			0x74,0x65,0x73,0x74
		};
	/** Atom with a long (first word = 1) 
	 * length below zero (errorneous)
	 * 0x66,0x74,0x79,0x70 = "ftyp"
	 * 0x74,0x65,0x73,0x74 = "test"
	 */
	public static final byte[] NEGATIVETEST = new byte[]{
		0x00, 0x00, 0x00, 0x01,
		0x66, 0x74, 0x79, 0x70, 
		-0x1, -0x1, -0x1, -0x1, 
		-0x1, -0x1, -0x1,-0x14, 
		0x74, 0x65, 0x73, 0x74
	};
	/** Atom with long (first word = 1) length of 0x14 (unusual, but correct)
	 * Normally you only use the long notation
	 * if the length doesn't fit in the first word,
	 * but that would make for a very big testcase.
	 * 0x66,0x74,0x79,0x70 = "ftyp"
	 * 0x74,0x65,0x73,0x74 = "test"
	 */
	public static final byte[] LONGLENGTHTEST = new byte[]{
		0x00, 0x00, 0x00, 0x01,
		0x66, 0x74, 0x79, 0x70,
		0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x14, 
		0x74, 0x65, 0x73, 0x74
	};
	
	@Before
	public void createTestData() throws IOException {
		basicTest = folder.newFile();
		FileOutputStream basicTestWriter = new FileOutputStream(basicTest);
		basicTestWriter.write(BASICTEST);
		basicTestWriter.close();
		
		negativeTest = folder.newFile();
		FileOutputStream negativeTestWriter = new FileOutputStream(negativeTest);
		negativeTestWriter.write(NEGATIVETEST);
		negativeTestWriter.close();
		
		longLengthTest = folder.newFile();
		FileOutputStream longLengthTestWriter = new FileOutputStream(longLengthTest);
		longLengthTestWriter.write(LONGLENGTHTEST);
		longLengthTestWriter.close();
	}
	
	/**
	 * Parse file and check if the expectedOffset 
	 * after parsing is the same as expected
	 * 
	 * @param file the file to parse
	 * @param expectedOffset expected 
	 * @throws IOException reading the stream failed (test fails)
	 * @throws AtomException the atom is invalid (test fails)
	 */
	protected void test(File file, int expectedOffset) throws IOException, AtomException {
		ParentAtom atom = Atom.fromFile(file);
		assertEquals("ftyp", atom.get(0).name);
		assertSame(expectedOffset, atom.get(0).offset);
		assertEquals("test", new String(atom.get(0).getPayload()));
	}
	
	/**
	 * Conform that {@link #BASICTEST} works
	 * @throws IOException reading the stream failed (test fails)
	 * @throws AtomException the atom is invalid (test fails)
	 */
	@Test
	public void basicTest() throws IOException, AtomException {
		test(basicTest, 8);
	}
	
	/**
	 * Confirms that {@link #NEGATIVETEST} doesn't work
	 * @throws IOException reading the stream failed (test fails)
	 */
	@Test
	public void negativeTest() throws IOException {
		try {
			/* Not calling #test(File,int) because we have no expectedOffset */
			Atom.fromFile(negativeTest);
			fail("An exception should have been thrown.");
		} catch (AtomException ae) {
			assertTrue(ae.getMessage(), ae.getMessage().toLowerCase().contains("pointer"));
			assertTrue(ae.getMessage(), ae.getMessage().toLowerCase().contains("overflow"));
		}
	}
	
	/**
	 * Confirms that {@link #LONGLENGTHTEST} works
	 * @throws IOException reading the stream failed (test fails)
	 * @throws AtomException the atom is invalid (test fails)
	 */
	@Test
	public void longTest() throws IOException, AtomException {
		test(longLengthTest, 16);
	}

}
