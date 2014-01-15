package at.ac.tuwien.common.binary;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.BitSet;

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
	 * @return a string with the uppercase hexadecimal representation
	 */
	public static final String bytesToHexString(byte... bytes) {
		if (bytes == null)
			throw new IllegalArgumentException("Byte array is null");

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
	 * Converts an array of bytes into it's binary string representation (e.g.
	 * "10010101")
	 * 
	 * @param bytes
	 *            the array of input bytes
	 * @return a string with the binary representation
	 */
	public static final String bytesToBinaryString(byte... bytes) {
		if (bytes == null)
			throw new IllegalArgumentException("Byte array is null");

		char[] chars = new char[bytes.length * 8];
		int v;
		for (int i = 0; i < bytes.length; i++) {
			v = bytes[i] & 0xFF;

			for (int j = 0; j < 8; j++) {
				chars[i * 8 + j] = ((v >> (7 - j)) & 1) == 1 ? '1' : '0';
			}
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
	public static final byte[] hexStringToBytes(String s) {
		if (s == null)
			throw new IllegalArgumentException("Input string is null");

		s = s.replaceAll("\\s|_", "").replaceAll("(^0x|h$)", "");
		if (s.length() < 1)
			throw new IllegalArgumentException("Input number is empty");

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

	/**
	 * Converts a string representation of an binary value into an array of
	 * bytes. It ignores whitespaces (everything a \s in an regex matches),
	 * underscores, the prefix "0b" and the postfix "b". If number represented
	 * in the string has a length which is not divisible by 8 the number will be
	 * preceded by zeros, i.e. the string "1001" will get "00001001" (0x09).
	 * 
	 * @param s
	 *            the string containing the representation of the binary number
	 * @return a byte array containing the bytes of the converted number
	 * @throws NumberFormatException
	 *             If the string contains any character which is neither a valid
	 *             hexadecimal character (upper or lowercase) nor one of the
	 *             ignored characters mentioned above.
	 */
	public static final byte[] binaryStringToBytes(String s) {
		if (s == null)
			throw new IllegalArgumentException("Input string is null");

		s = s.replaceAll("\\s|_", "").replaceAll("(^0b|b$)", "");
		if (s.length() < 1)
			throw new IllegalArgumentException("Input number is empty");

		int len = s.length();
		while (len % 8 != 0) {
			s = "0" + s;
			len++;
		}

		byte[] data = new byte[len / 8];
		for (int i = 0; i < len; i += 8) {

			data[i / 8] = 0;

			for (int j = 0; j < 8; j++) {
				int b = Character.digit(s.charAt(i + j), 2);
				if (b < 0)
					throw new NumberFormatException(
							"There is an invalid character in the number at position "
									+ (i + j) + ": " + s);

				data[i / 8] = (byte) (data[i / 8] + (b << (7 - j)));
			}
		}
		return data;
	}

	/**
	 * Helps to know what bit is at some position in some number. Maybe you
	 * should also take a look at {@link BitSet}.
	 * 
	 * @param number
	 *            in which we search a bit
	 * @param bitPosition
	 *            at which we want to know a bit
	 * @return bit that is on bitPosition at number
	 * @throws IllegalArgumentException
	 *             if bitPosition is negative
	 */
	public final static boolean bitAt(int number, int bitPosition)
			throws IllegalArgumentException {
		if (bitPosition < 0) {
			throw new IllegalArgumentException("bitPosition is negative");
		}

		return ((number >> bitPosition) & 1) == 1;
	}

	/**
	 * Set bit at a specific position. Maybe you should also take a look at
	 * {@link BitSet}.
	 * 
	 * @param index
	 *            the index starting from the right (LSB)
	 * @param n
	 * @return the same integer with the specified bit set
	 */
	public static int setBit(int index, final int n) {
		if (index < 0 || index > 31)
			throw new IndexOutOfBoundsException();

		return ((1 << index) | n);
	}

	/**
	 * Set bit at a specific position. Maybe you should also take a look at
	 * {@link BitSet}.
	 * 
	 * @param index
	 *            the index starting from the right (LSB)
	 * @param n
	 * @return the same byte with the specified bit set
	 */
	public static byte setBit(int index, final byte n) {
		if (index < 0 || index > 7)
			throw new IndexOutOfBoundsException();

		return (byte) ((1 << index) | n);
	}

	/**
	 * <p>
	 * Creates a boolean array representation of the integer argument as an
	 * unsigned integer in base 2.
	 * </p>
	 * <p>
	 * The unsigned integer value is the argument plus 2^32 if the argument is
	 * negative; otherwise it is equal to the argument. This value is converted
	 * to a binary array (base 2) with no extra leading 0s. If the unsigned
	 * magnitude is zero, it is represented by a single zero character '0'
	 * (false); otherwise, the last character of the representation of the
	 * unsigned magnitude will not be the zero character. The characters '0'
	 * (false) and '1' (true) are used as binary digits.
	 * </p>
	 * <p>
	 * The bit with index 0 is the LSB. So indexing starts from the right side
	 * a.k.a. little endian format.
	 * </p>
	 * 
	 * @param n
	 * @return the boolean array representation of the unsigned integer value
	 *         represented by the argument in binary (base 2).
	 */
	public final static boolean[] intToBinaryArray(int n) {
		int l = 0;
		int nShifted = n;

		while (nShifted != 0) {
			l++;
			nShifted >>>= 1;
		}
		if (l == 0) {
			return new boolean[] { false };
		}
		boolean[] result = new boolean[l];

		for (int i = 0; i < l; i++) {
			result[i] = ((n >> i) & 1) == 1 ? true : false;
		}
		return result;
	}

	/**
	 * <p>
	 * Creates a boolean array representation of the byte argument as an
	 * unsigned byte in base 2.
	 * </p>
	 * <p>
	 * The unsigned byte value is the argument plus 2^8 if the argument is
	 * negative; otherwise it is equal to the argument. This value is converted
	 * to a binary array (base 2) with no extra leading 0s. If the unsigned
	 * magnitude is zero, it is represented by a single zero character '0'
	 * (false); otherwise, the last character of the representation of the
	 * unsigned magnitude will not be the zero character. The characters '0'
	 * (false) and '1' (true) are used as binary digits.
	 * </p>
	 * <p>
	 * The bit with index 0 is the LSB. So indexing starts from the right side
	 * a.k.a. little endian format.
	 * </p>
	 * 
	 * @param n
	 * @return the boolean array representation of the unsigned byte value
	 *         represented by the argument in binary (base 2).
	 */
	public final static boolean[] byteToBinaryArray(byte n) {
		int l = 0;
		byte nShifted = n;

		while (nShifted != 0 && l < 8) {
			l++;
			nShifted >>>= 1;
		}
		if (l == 0) {
			return new boolean[] { false };
		}
		boolean[] result = new boolean[l];

		for (int i = 0; i < l; i++) {
			result[i] = ((n >> i) & 1) == 1 ? true : false;
		}
		return result;
	}

	/**
	 * <p>
	 * Creates a byte array representation of the integer argument as an
	 * unsigned integer.
	 * </p>
	 * <p>
	 * The unsigned integer value is the argument plus 2^32 if the argument is
	 * negative; otherwise it is equal to the argument. This value is converted
	 * to a byte array with size 4 with leading zeros if necessary.
	 * </p>
	 * <p>
	 * The most significant byte is at index 0, so indexing starts from the left
	 * side a.k.a. big endian format.
	 * </p>
	 * <strong>Hint:</strong> Original method from <a href=
	 * "http://stackoverflow.com/questions/6374915/java-convert-int-to-byte-array-of-4-bytes"
	 * >stackoverflow.com</a> </p>
	 * 
	 * @param n
	 * @return the byte array representation of the unsigned integer value
	 *         represented by the argument.
	 */
	public final static byte[] intToBytes(int n) {
		return ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(n)
				.array();
	}

	/**
	 * <p>
	 * Creates an integer representation of the byte array.
	 * </p>
	 * <p>
	 * The most significant byte has to be at index 0, so indexing starts from
	 * the left side a.k.a. big endian format.
	 * </p>
	 * 
	 * @param n
	 * @return the byte array representation of the unsigned integer value
	 *         represented by the argument.
	 */
	public final static int bytesToInt(byte... bytes) {
		if (bytes == null)
			throw new IllegalArgumentException("Byte array is null");
		if (bytes.length > 4)
			throw new IllegalArgumentException(
					"Byte array is bigger than 4, integers can only hold 4 byte numbers.");

		return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getInt();
	}

	/**
	 * Creates an integer from boolean array treating it as binary number. It
	 * will only take the first 32 "bits" of the array since integer is limited
	 * to this.
	 * 
	 * @param arr
	 *            binary number
	 */
	public final static int binaryArrayToInt(boolean[] arr) {
		if (arr == null)
			throw new IllegalArgumentException("binary array is null");

		int result = 0;

		for (int i = 0; i < arr.length && i < 32; i++) {
			result += (arr[i] ? 1 << i : 0);
		}

		return result;
	}

	/**
	 * Creates a byte from boolean array treating it as binary number
	 * 
	 * @param arr
	 *            binary number
	 */
	public final static byte binaryArrayToByte(boolean[] arr) {
		if (arr == null)
			throw new IllegalArgumentException("The binary array is null");
		if (arr.length > 8)
			throw new IllegalArgumentException(
					"The binary array has more than 8 bits");

		byte result = 0;

		for (int i = 0; i < arr.length; i++) {
			result += (arr[i] ? 1 << i : 0);
		}

		return result;
	}
}
