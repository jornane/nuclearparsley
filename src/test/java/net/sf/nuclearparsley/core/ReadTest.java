package net.sf.nuclearparsley.core;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ReadTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private File basicTest;
	
	public static final byte[] BASICTEST = new byte[]{
			0,0,0,0xC,
			0x66,0x74,0x79,0x70,
			0x74,0x65,0x73,0x74
		};
	
	@Before
	public void createTestData() throws IOException {
		basicTest = folder.newFile();
		FileOutputStream basicTestWriter = new FileOutputStream(basicTest);
		basicTestWriter.write(BASICTEST);
		basicTestWriter.close();
	}
	
	@Test
	public void test() throws IOException {
		ParentAtom atom = Atom.fromFile(basicTest);
		assertEquals("ftyp", atom.get(0).name);
		atom.input.seek(atom.start+8);
		byte[] value = new byte[4];
		atom.input.read(value);
		assertEquals("test", new String(value));
	}

}
