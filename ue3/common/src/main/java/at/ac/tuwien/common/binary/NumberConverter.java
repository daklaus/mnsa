package at.ac.tuwien.common.binary;

public class NumberConverter {

	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	/**
	 * Original method from <a href=
	 * "http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java"
	 * >stackoverflow.com</a>
	 */
	public static final String bytesToHex(byte... bytes) {
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	/**
	 * Original method from <a href=
	 * "http://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java"
	 * >stackoverflow.com</a>
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
