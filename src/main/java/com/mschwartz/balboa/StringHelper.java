package com.mschwartz.balboa;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.zip.CRC32;

/**
 * A static helper class for dealing with strings. Created by Mike on 2/19/2016.
 */
public final class StringHelper {

	private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz1234567890";

	/**
	 * Take care that this is not really random. It may be deterministic if you know
	 * when the app started
	 */
	private static final Random RANDOM = new Random(System.currentTimeMillis());

	private static Map<String, String> alphabet;

	/**
	 * The Constant HEXES.
	 */
	private static final char[] HEXES = "0123456789ABCDEF".toCharArray();

	/**
	 * Convert the whole Argument buf[] in to a hexadecimal presentation.
	 *
	 * @param buf
	 *            the buf
	 * @return the string
	 */
	public static String asHex(byte buf[]) {

		StringBuffer strbuf = new StringBuffer(buf.length * 2);

		for (final byte b : buf) {
			strbuf.append(HEXES[(b & 0xF0) >> 4]).append(HEXES[(b & 0x0F)]);
		}
		return strbuf.toString();
	}

	/**
	 * As hex colon.
	 *
	 * @param buf
	 *            the buf
	 * @return the string
	 */
	public static String asHexColon(byte buf[]) {
		return asHexColon(buf, 0, buf.length);
	}

	/**
	 * As hex colon.
	 *
	 * @param buf
	 *            the buf
	 * @param start
	 *            the start
	 * @param length
	 *            the length
	 * @return the string
	 */
	public static String asHexColon(byte buf[], int start, int length) {
		StringBuffer strbuf = new StringBuffer(length * 3);

		for (int i = start; i < start + length; i++) {
			if (i != start)
				strbuf.append(':');

			strbuf.append(HEXES[(buf[i] & 0xF0) >> 4]).append(HEXES[(buf[i] & 0x0F)]);
		}

		return strbuf.toString();
	}
	
	public static String asString(byte buf[]) {
		String s = new String(buf);
		return s;
//		StringBuffer strbuf = new StringBuffer(buf.length);
//		for (int i = 0; i < buf.length; i++) {
//
//			strbuf.append(buf[i]);
//		}
//		return strbuf.toString();
	}

	/**
	 * Returns a string which is at most maxLen characters long. Truncates the
	 * string at the end and inserts 3 dots ("...") near the end if necessary.
	 *
	 * @param text
	 *            to truncate
	 * @param maxLen
	 *            the maximum number of characters for the returned string
	 * @return the truncated String
	 */
	public static String truncate(String text, int maxLen) {
		assert (maxLen > 10);
		if (text == null)
			return text;
		if (text.length() <= maxLen)
			return text;
		return text.substring(0, maxLen - 10) + "..." + text.substring(text.length() - 7);
	}

	/**
	 * Returns a string which is at most maxLen characters long. Truncates the
	 * string at the middle and inserts 3 dots ("...") if necessary.
	 *
	 * @param text
	 *            to truncate
	 * @param maxLen
	 *            the maximum number of characters for the returned string
	 * @return the truncated String
	 */
	public static String truncateMiddle(String text, int maxLen) {
		assert (maxLen > 10);
		if (text == null)
			return text;
		if (text.length() <= maxLen)
			return text;
		return text.substring(0, maxLen / 2 - 2) + "..." + text.substring(text.length() - maxLen / 2 + 1);
	}

	public static String maxLen(String value, int maxlen) {
		if (value == null || value.length() <= maxlen)
			return value;
		return value.substring(0, maxlen);
	}

	/**
	 * Creates a random string with random length between minChars and maxChars
	 *
	 * @param minChars
	 * @param maxChars
	 * @return
	 */
	public static String randomString(int minChars, int maxChars) {
		return randomString(CHARACTERS, minChars, maxChars);
	}

	/**
	 * Creates a random string with characters specified by the parameter characters
	 * whose length is between minChars and maxChars
	 *
	 * @param characters
	 * @param minChars
	 * @param maxChars
	 * @return
	 */
	public static String randomString(String characters, int minChars, int maxChars) {
		String result = "";
		int length = maxChars == minChars ? minChars : RANDOM.nextInt(maxChars - minChars) + minChars;
		for (int i = 0; i < length; ++i) {
			result += characters.charAt(RANDOM.nextInt(characters.length()));
		}
		return result;
	}

	/**
	 * Returns true if both strings are null or length zero or both strings are
	 * equal.
	 *
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	public static boolean equalNull(String lhs, String rhs) {
		// if ((lhs == null || lhs.length() == 0) && (rhs == null || rhs.length() == 0))
		if (lhs == null && rhs == null)
			return true;
		if (lhs == null)
			lhs = "";
		if (rhs == null)
			rhs = "";
		return lhs.equals(rhs);
	}

	/**
	 * Returns true if the given string is null or has a length of zero
	 *
	 * @param value
	 *            the string to test
	 * @return true if the given string is null or has a length of zero
	 */
	public static boolean nullOrEmpty(String value) {
		if (value == null)
			return true;
		return value.length() == 0;
	}

	public static String getMorsecode(String name) {
		if (name == null)
			return null;
		if (alphabet == null) {
			alphabet = new HashMap<>();
			alphabet.put("A", ".-");
			alphabet.put("B", "-...");
			alphabet.put("C", "-.-.");
			alphabet.put("D", "-..");
			alphabet.put("E", ".");
			alphabet.put("F", "..-.");
			alphabet.put("G", "--.");
			alphabet.put("H", "....");
			alphabet.put("I", "..");
			alphabet.put("J", ".---");
			alphabet.put("K", "-.-");
			alphabet.put("L", ".-..");
			alphabet.put("M", "--");
			alphabet.put("N", "-.");
			alphabet.put("O", "---");
			alphabet.put("P", ".--.");
			alphabet.put("Q", "--.-");
			alphabet.put("R", ".-.");
			alphabet.put("S", "...");
			alphabet.put("T", "-");
			alphabet.put("U", "..-");
			alphabet.put("V", "...-");
			alphabet.put("W", ".--");
			alphabet.put("X", "-..-");
			alphabet.put("Y", "-.--");
			alphabet.put("Z", "--..");
		}
		String result = "";
		for (int i = 0; i < name.length(); ++i) {
			String code = alphabet.get(name.substring(i, i + 1));
			if (result.length() > 0)
				result += "/";
			if (code == null)
				result += "?";
			else
				result += code;
		}
		return result;
	}

	/**
	 * Gets the int from array.
	 *
	 * @param array
	 *            the array
	 * @param idx
	 *            the idx
	 * @param count
	 *            the count
	 * @return the int from array
	 */
	public static int getIntFromArray(byte[] array, int idx, int count) {
		int result = 0;
		for (int i = 0; i < count; i++) {
			result <<= 8;
			result |= (array[idx + i] & 0xFF);
		}
		return result;
	}

	/**
	 * Gets the long from array.
	 *
	 * @param array
	 *            the array
	 * @param idx
	 *            the idx
	 * @param count
	 *            the count
	 * @return the long from array
	 */
	public static long getLongFromArray(byte[] array, int idx, int count) {
		long result = 0;
		for (int i = 0; i < count; i++) {
			result <<= 8;
			result |= (array[idx + i] & 0xFF);
		}
		return result;
	}

	public static float getFloatFromArray(byte[] array, int idx, int countBeforeComma, int countAfterComma) {
		long result1 = 0;
		for (int i = 0; i < countBeforeComma; i++) {
			result1 <<= 8;
			result1 |= (array[idx + i] & 0xFF);
		}

		long result2 = 0;
		for (int i = 0; i < countAfterComma; i++) {
			result2 <<= 8;
			result2 |= (array[idx + countBeforeComma + i] & 0xFF);
		}

		return (float) (result1 + result2 / Math.pow(2, 8 * countAfterComma));
	}

	/**
	 * Write short to array.
	 *
	 * @param array
	 *            the array
	 * @param idx
	 *            the idx
	 * @param number
	 *            the number
	 * @param count
	 *            the count
	 */
	public static void writeShortToArray(byte[] array, int idx, short number, int count) {
		for (int i = 0; i < count; ++i) {
			array[idx + count - i - 1] = (byte) (number & 0xff);
			number >>= 8;
		}
	}

	/**
	 * Write int to array with MSB (Most significant byte) first.
	 *
	 * @param array
	 *            the array
	 * @param idx
	 *            the idx
	 * @param number
	 *            the number
	 * @param count
	 *            the count
	 */
	public static void writeIntToArray(byte[] array, int idx, int number, int count) {
		for (int i = 0; i < count; ++i) {
			array[idx + count - i - 1] = (byte) (number & 0xff);
			number >>= 8;
		}
	}

	/**
	 * Write int to array with lsb (least significant byte) first.
	 *
	 * @param array
	 *            the array
	 * @param idx
	 *            the idx
	 * @param number
	 *            the number
	 * @param count
	 *            the count
	 */
	public static void writeIntToArrayLSB(byte[] array, int idx, int number, int count) {
		for (int i = 0; i < count; ++i) {
			array[idx + i] = (byte) (number & 0xff);
			number >>= 8;
		}
	}

	public static void copyToArray(byte[] array, int idx, byte[] source) {
		for (int i = 0; i < source.length; ++i) {
			array[idx + i] = source[i];
		}
	}

	/**
	 * Write long to array.
	 *
	 * @param array
	 *            the array
	 * @param idx
	 *            the idx
	 * @param number
	 *            the number
	 * @param count
	 *            the count
	 */
	public static void writeLongToArray(byte[] array, int idx, long number, int count) {
		for (int i = 0; i < count; ++i) {
			array[idx + count - i - 1] = (byte) (number & 0xff);
			number >>= 8;
		}
	}

	public static void writeFloatToArray(byte[] array, int idx, float number, int countBeforeComma,
			int countAfterComma) {
		long nbr = (long) number;
		for (int i = 0; i < countBeforeComma; ++i) {
			array[idx + countBeforeComma - i - 1] = (byte) (nbr & 0xff);
			nbr >>= 8;
		}

		long nbr2 = (long) ((number - (long) number) * Math.pow(2, 8 * countAfterComma));
		// System.out.println("number: " + number + ", nbr: " + (long)number + ", diff:
		// " + (number - (long)number) + ", pow: " + Math.pow(2 , 8 * countAfterComma) +
		// ", remain: " + nbr2);
		for (int i = 0; i < countAfterComma; ++i) {
			array[idx + countBeforeComma + countAfterComma - i - 1] = (byte) (nbr2 & 0xff);
			nbr2 >>= 8;
		}
	}

	/**
	 * Copy from array.
	 *
	 * @param array
	 *            the array
	 * @param idx
	 *            the idx
	 * @param length
	 *            the length
	 * @return the byte[]
	 */
	public static byte[] copyFromArray(byte[] array, int idx, int length) {
		return Arrays.copyOfRange(array, idx, idx + length);
	}

	public static long crc32(byte[] value) {
		CRC32 crc32 = new CRC32();
		crc32.update(value);
		return crc32.getValue();
	}
	
	/**
	 * Returns true if both strings are null or both strings are equal. Will
	 * never throw a {@link NullPointerException}.
	 * 
	 * @param lhs
	 * @param rhs
	 * @return true if the given strings are both null or equal.
	 */
	public static boolean equalsNullable(String lhs, String rhs) {
		if (lhs == null && rhs == null)
			return true;
		if (lhs != null && rhs == null)
			return false;
		if (lhs == null && rhs != null)
			return false;
		return rhs.equals(lhs);
	}


    /**
     * Search the data byte array for the first occurrence of the byte array pattern within given boundaries.
     * @param data
     * @param start First index in data
     * @param stop Last index in data so that stop-start = length
     * @param pattern What is being searched. '*' can be used as wildcard for "ANY character"
     * @return
     */
    public static int indexOf( byte[] data, int start, int stop, byte[] pattern) {
        if( data == null || pattern == null) return -1;

        int[] failure = computeFailure(pattern);

        int j = 0;

        for( int i = start; i < stop; i++) {
            while (j > 0 && ( pattern[j] != '*' && pattern[j] != data[i])) {
                j = failure[j - 1];
            }
            if (pattern[j] == '*' || pattern[j] == data[i]) {
                j++;
            }
            if (j == pattern.length) {
                return i - pattern.length + 1;
            }
        }
        return -1;
    }

    /**
     * Computes the failure function using a boot-strapping process,
     * where the pattern is matched against itself.
     */
    private static int[] computeFailure(byte[] pattern) {
        int[] failure = new int[pattern.length];

        int j = 0;
        for (int i = 1; i < pattern.length; i++) {
            while (j>0 && pattern[j] != pattern[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == pattern[i]) {
                j++;
            }
            failure[i] = j;
        }

        return failure;
    }

}
