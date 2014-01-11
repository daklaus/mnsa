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
		// There is no checking in this method. It should only be used if you
		// know what you are doing.
		// TODO Abort if length < 1
		// TODO Check the characters (what does Character.digit(.) do with
		// invalid ones?)

		// TODO Extend for calculation on odd lenghts of the string (add
		// preceding 0x0)

		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
}
