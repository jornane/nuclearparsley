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
	
	public static final byte[] BASICTEST = new byte[]{
			0x00,0x00,0x00,0x0C,
			0x66,0x74,0x79,0x70,
			0x74,0x65,0x73,0x74
		};
	public static final byte[] NEGATIVETEST = new byte[]{
		0x00, 0x00, 0x00, 0x01,
		0x66, 0x74, 0x79, 0x70, 
		-0x1, -0x1, -0x1, -0x1, 
		-0x1, -0x1, -0x1,-0x14, 
		0x74, 0x65, 0x73, 0x74
	};
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
	
	public void test(File file) throws IOException {
		ParentAtom atom = Atom.fromFile(file);
		assertEquals("ftyp", atom.get(0).name);
		assertEquals("test", new String(atom.getPayload()).substring((int) atom.offset));
	}
	
	@Test
	public void basicTest() throws IOException {
		test(basicTest);
	}
	
	@Test
	public void negativeTest() throws IOException {
		try {
			Atom.fromFile(negativeTest);
			fail("An exception should have been thrown.");
		} catch (AtomException ae) {
			assertTrue(ae.getMessage(), ae.getMessage().toLowerCase().contains("pointer"));
			assertTrue(ae.getMessage(), ae.getMessage().toLowerCase().contains("overflow"));
		}
	}
	
	@Test
	public void longTest() throws IOException {
		test(longLengthTest);
	}

}
