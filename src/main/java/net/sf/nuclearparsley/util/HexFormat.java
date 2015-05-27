/*
 * Nuclear Parsley - GPL 3.0 licensed
 * Copyright (C) 2015  Jørn Åne de Jong
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
package net.sf.nuclearparsley.util;

/**
 * Dumps binary data as hexadecimal
 */
public final class HexFormat {

	public static final String HEX_FORMAT = "%x";
	
	private HexFormat() {/* no instantiating */}
	
	/**
	 * Dump data as hexadecimal
	 * @param data	The data to dump
	 * @param prefix	The prefix to add to each line
	 * @return	The formatted data
	 */
	public static StringBuilder format(byte[] data, StringBuilder prefix) {
		return format(data, prefix, 0);
	}
		/**
		 * Dump data as hexadecimal
		 * @param data	The data to dump
		 * @param prefix	The prefix to add to each line
		 * @param first	The first byte number
		 * @return	The formatted data
		 */
	public static StringBuilder format(byte[] data, StringBuilder prefix, long first) {
		final long max = first+data.length;
		final int addrLen = String.format(HEX_FORMAT, max).length();
		StringBuilder result = new StringBuilder();
		for(int i=0;i<=(data.length|0xF)+1;i++) {
			if ((i & 0x7) == 0)
				result.append(' ');
			if ((i & 0xF) == 0) {
				if (i != 0) {
					result.append(" |");
					for(int j=i-16;j<i&&j<data.length;j++)
						result.append(data[j] < 32 || data[j] >= 127
								? '.'
								: (char)data[j]
							);
					result.append('|');
				}
				result.append("\n");
				if (i >= data.length)
					break;
				result.append(prefix);
				result.append("\t");
				String addr = String.format(HEX_FORMAT, first+i);
				while(addr.length() < addrLen)
					addr = "0"+addr;
				result.append(addr+" ");
			}
			if (i >= data.length)
				result.append("   ");
			else if (data[i] < 0x10 && data[i] >= 0)
				result.append(String.format(" 0"+HEX_FORMAT, data[i]));
			else
				result.append(String.format(" "+HEX_FORMAT, data[i]));
		}
		return result;
	}

}
