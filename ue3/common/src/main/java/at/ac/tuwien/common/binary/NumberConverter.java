package at.ac.tuwien.common.binary;

public class NumberConverter {

	private final static char[] hexChars = "0123456789ABCDEF".toCharArray();

	/**
	 * <p>
	 * Converts an array of bytes into it's uppercase hexadecimal string
	 * representation (e.g. "0F34ED")
	 * </p>
	 * <p>
	 * <strong>Hint:</strong> Original method from <a href=
	 * "http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java"
	 * >stackoverflow.com</a>
	 * </p>
	 * 
	 * @param bytes
	 *            the array of input bytes
	 * @return a String with the uppercase hexadecimal representation
	 */
	public static final String bytesToHex(byte... bytes) {
		if(bytes == null)
			throw new IllegalArgumentException("Bytes array is null");
		
		char[] chars = new char[bytes.length * 2];
		int v;
		for (int i = 0; i < bytes.length; i++) {
			v = bytes[i] & 0xFF;
			chars[i * 2] = hexChars[v >>> 4];
			chars[i * 2 + 1] = hexChars[v & 0x0F];
		}
		return new String(chars);
	}

	/**
	 * <p>
	 * Converts a string representation of an hexadecimal value into an array of
	 * bytes. It ignores whitespaces (everything a \s in an regex matches),
	 * underscores, the prefix "0x" and the postfix "h". If number represented
	 * in the string has an odd length the number will be preceded by a zero,
	 * i.e. the string "3F2" will get [0x03, 0xF2].
	 * </p>
	 * <p>
	 * <strong>Hint:</strong> Original method from <a href=
	 * "http://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java"
	 * >stackoverflow.com</a>
	 * </p>
	 * 
	 * @param s
	 *            the string containing the representation of the hexadecimal
	 *            number
	 * @return a byte array containing the bytes of the converted number
	 * @throws NumberFormatException
	 *             If the string contains any character which is neither a valid
	 *             hexadecimal character (upper or lowercase) nor one of the
	 *             ignored characters mentioned above.
	 */
	public static final byte[] hexToBytes(String s) {
		if (s == null)
			throw new IllegalArgumentException("Input string is null");

		s = s.replaceAll("\\s|_", "").replaceAll("(^0x|h$)", "");
		if (s.length() < 1)
			throw new IllegalArgumentException("Input number is empty: " + s);

		int len = s.length();
		if (len % 2 != 0) {
			s = "0" + s;
			len++;
		}

		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			int msb = Character.digit(s.charAt(i), 16);
			if (msb < 0)
				throw new NumberFormatException(
						"There is an invalid character in the number at position "
								+ i + ": " + s);

			int lsb = Character.digit(s.charAt(i + 1), 16);
			if (lsb < 0)
				throw new NumberFormatException(
						"There is an invalid character in the number at position "
								+ (i + 1) + ": " + s);

			data[i / 2] = (byte) ((msb << 4) + lsb);
		}
		return data;
	}
}
