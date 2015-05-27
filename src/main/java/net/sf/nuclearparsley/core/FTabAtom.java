package net.sf.nuclearparsley.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Font Table Atom
 */
public class FTabAtom extends Atom implements Map<Integer, String> {

	public static final String NAME = "ftab";

	public static final int OFFSET = 8;

	protected static final Map<Integer, String> M_SERIF;
	protected static final Map<Integer, String> M_SANS_SERIF;

	public static final String SERIF = "Serif";
	public static final String SANS_SERIF = "Sans-Serif";
	
	private final LinkedHashMap<Integer, String> fonts;

	/**
	 * @param input	Atoms are read from this file
	 * @param start	Starting pointer of this {@link Atom} in input file
	 * @param length	Length of this {@link Atom} in bytes (including offset)
	 * @param offset	The starting point of the atom payload from the start of the atom
	 * @throws AtomException	Reading the {@link Atom} failed
	 * @throws IOException	Reading the file failed
	 */
	protected FTabAtom(File input, long start, long length, int offset)
			throws AtomException, IOException {
		super(NAME, input, start, length, offset+8);
		fonts = new LinkedHashMap<Integer, String>();
		InputStream stream = super.getPayloadStream();
		int count = readUnsignedWord(stream);
		
	}
	
	static {
		Map<Integer, String> serif = new LinkedHashMap<Integer, String>();
		Map<Integer, String> sansSerif = new LinkedHashMap<Integer, String>();
		serif.put(1, SERIF);
		sansSerif.put(1, SANS_SERIF);
		M_SERIF = Collections.unmodifiableMap(serif);
		M_SANS_SERIF = Collections.unmodifiableMap(sansSerif);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	@Override
	public int size() {
		return fonts.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return fonts.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object key) {
		return fonts.containsKey(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	@Override
	public boolean containsValue(Object value) {
		return fonts.containsValue(value);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	@Override
	public String get(Object key) {
		return fonts.get(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public String put(Integer key, String value) {
		return fonts.put(key, value);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	@Override
	public String remove(Object key) {
		return fonts.remove(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	@Override
	public void putAll(Map<? extends Integer, ? extends String> m) {
		fonts.putAll(m);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	@Override
	public void clear() {
		fonts.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	@Override
	public Set<Integer> keySet() {
		return fonts.keySet();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	@Override
	public Collection<String> values() {
		return fonts.values();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	@Override
	public Set<java.util.Map.Entry<Integer, String>> entrySet() {
		return fonts.entrySet();
	}

}
