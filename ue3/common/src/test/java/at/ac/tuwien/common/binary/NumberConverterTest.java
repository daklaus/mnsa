package at.ac.tuwien.common.binary;

import static org.junit.Assert.*;

import static org.hamcrest.core.IsEqual.equalTo;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class NumberConverterTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	public NumberConverterTest() {
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/*
	 * bytesToHexString
	 */

	@Test
	public void testBytesToHexStringShouldPass() {
		assertEquals("Empty array doesn't work", "",
				NumberConverter.bytesToHexString(new byte[] {}));
		assertEquals("Single byte doesn't work", "42",
				NumberConverter.bytesToHexString((byte) 0x42));
		assertEquals("Multiple bytes as paramters don't work", "4F00F2",
				NumberConverter.bytesToHexString((byte) 0x4F, (byte) 0x00,
						(byte) 0xF2));
		assertEquals(
				"Byte array doesn't work",
				"4F00F2",
				NumberConverter.bytesToHexString(new byte[] { (byte) 0x4F,
						(byte) 0x00, (byte) 0xF2 }));
	}

	@Test
	public void testBytesToHexStringShouldFailNull() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("null");
		NumberConverter.bytesToHexString(null);
	}

	/*
	 * hexStringToBytes
	 */

	@Test
	public void testHexStringBytesShouldPass() {
		assertArrayEquals("0x prefix doesn't work", new byte[] { (byte) 0x42 },
				NumberConverter.hexStringToBytes("0x42"));
		assertArrayEquals("h postfix doesn't work", new byte[] { (byte) 0x42 },
				NumberConverter.hexStringToBytes("42h"));
		assertArrayEquals("Single byte doesn't work",
				new byte[] { (byte) 0x42 },
				NumberConverter.hexStringToBytes("42"));
		assertArrayEquals("Single character doesn't work",
				new byte[] { (byte) 0x02 },
				NumberConverter.hexStringToBytes("2"));
		assertArrayEquals("Odd string length doesn't work",
				NumberConverter.hexStringToBytes("042"), new byte[] {
						(byte) 0x00, (byte) 0x42 });
		assertArrayEquals("Lowercase hex-letters don't work",
				new byte[] { (byte) 0x3f },
				NumberConverter.hexStringToBytes("3f"));
		assertArrayEquals("Uppercase hex-letters don't work",
				new byte[] { (byte) 0x3f },
				NumberConverter.hexStringToBytes("0x3F"));
		assertArrayEquals("Spaces between don't work",
				new byte[] { (byte) 0x5a },
				NumberConverter.hexStringToBytes("0x   5a    h"));
		assertArrayEquals("Tabs and newlines inbetween don't work", new byte[] {
				(byte) 0x5a, (byte) 0xe6, (byte) 0x78 },
				NumberConverter
						.hexStringToBytes(" 5 a e    6 \t  7  \n  8   h  "));
		assertArrayEquals(
				"Underscores and spaces as byte seperators don't work",
				new byte[] { (byte) 0x46, (byte) 0x5f, (byte) 0x13,
						(byte) 0x28, (byte) 0xa4, (byte) 0x63, (byte) 0x18,
						(byte) 0xe9, (byte) 0x6b, (byte) 0x13 },
				NumberConverter
						.hexStringToBytes("46_5F_13_28 a4_63 18_e9_6B 13"));
	}

	@Test
	public void testHexStringBytesShouldFailNull() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("null");
		NumberConverter.hexStringToBytes(null);
	}

	@Test
	public void testHexStringBytesShouldFailEmpty01() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("empty");
		NumberConverter.hexStringToBytes("");
	}

	@Test
	public void testHexStringBytesShouldFailEmpty02() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("empty");
		NumberConverter.hexStringToBytes("0x");
	}

	@Test
	public void testHexStringBytesShouldFailEmpty03() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("empty");
		NumberConverter.hexStringToBytes("h");
	}

	@Test
	public void testHexStringBytesShouldFailEmpty04() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("empty");
		NumberConverter.hexStringToBytes("0x  h");
	}

	@Test
	public void testHexStringBytesShouldFailEmpty05() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("empty");
		NumberConverter.hexStringToBytes("   0x     ");
	}

	@Test
	public void testHexStringBytesShouldFailEmpty06() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("empty");
		NumberConverter.hexStringToBytes("   0x  \t _    h    ");
	}

	/*
	 * bytesToBinaryString
	 */

	@Test
	public void testBytesToBinaryStringShouldPass() {
		assertEquals("Empty array doesn't work", "",
				NumberConverter.bytesToBinaryString(new byte[] {}));
		assertEquals("Single byte doesn't work", "01000010",
				NumberConverter.bytesToBinaryString((byte) 0x42));
		assertEquals("Multiple bytes as paramters don't work",
				"010011110000000011110010",
				NumberConverter.bytesToBinaryString((byte) 0x4F, (byte) 0x00,
						(byte) 0xF2));
		assertEquals(
				"Byte array doesn't work",
				"010011110000000011110010",
				NumberConverter.bytesToBinaryString(new byte[] { (byte) 0x4F,
						(byte) 0x00, (byte) 0xF2 }));
	}

	@Test
	public void testBytesToBinaryStringShouldFailNull() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("null");
		NumberConverter.bytesToBinaryString(null);
	}

	/*
	 * binaryStringToBytes
	 */

	@Test
	public void testBinaryStringBytesShouldPass() {
		assertArrayEquals("0b prefix doesn't work", new byte[] { (byte) 0x42 },
				NumberConverter.binaryStringToBytes("0b01000010"));
		assertArrayEquals("b postfix doesn't work", new byte[] { (byte) 0x42 },
				NumberConverter.binaryStringToBytes("01000010b"));
		assertArrayEquals("Single byte doesn't work",
				new byte[] { (byte) 0x42 },
				NumberConverter.binaryStringToBytes("01000010"));
		assertArrayEquals("Single character doesn't work",
				new byte[] { (byte) 0x01 },
				NumberConverter.binaryStringToBytes("1"));
		assertArrayEquals("Odd string length doesn't work",
				NumberConverter.binaryStringToBytes("010"),
				new byte[] { (byte) 0x02 });
		assertArrayEquals("Spaces between don't work",
				new byte[] { (byte) 0x5a },
				NumberConverter.binaryStringToBytes("0b   01011010    b"));
		assertArrayEquals("Tabs and newlines inbetween don't work",
				new byte[] { (byte) 0x5a },
				NumberConverter
						.binaryStringToBytes("  0  10 \t 1 1  \n  010   b  "));
		assertArrayEquals(
				"Underscores and spaces as byte seperators don't work",
				new byte[] { (byte) 0x5a, (byte) 0x0f },
				NumberConverter.binaryStringToBytes("0101_1010 0000_1111"));
	}

	@Test
	public void testBinaryStringBytesShouldFailNull() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("null");
		NumberConverter.binaryStringToBytes(null);
	}

	@Test
	public void testBinaryStringBytesShouldFailEmpty01() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("empty");
		NumberConverter.binaryStringToBytes("");
	}

	@Test
	public void testBinaryStringBytesShouldFailEmpty02() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("empty");
		NumberConverter.binaryStringToBytes("0b");
	}

	@Test
	public void testBinaryStringBytesShouldFailEmpty03() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("empty");
		NumberConverter.binaryStringToBytes("b");
	}

	@Test
	public void testBinaryStringBytesShouldFailEmpty04() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("empty");
		NumberConverter.binaryStringToBytes("0b  b");
	}

	@Test
	public void testBinaryStringBytesShouldFailEmpty05() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("empty");
		NumberConverter.binaryStringToBytes("   0b     ");
	}

	@Test
	public void testBinaryStringBytesShouldFailEmpty06() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("empty");
		NumberConverter.binaryStringToBytes("   0b  \t _    b    ");
	}

	/*
	 * intToBinaryArray
	 */

	@Test
	public void testIntToBinaryArray() {
		boolean[] expected = new boolean[3];
		for (int i = 0; i < expected.length; i++) {
			expected[i] = true;
		}

		assertThat(expected, equalTo(NumberConverter.intToBinaryArray(7)));
	}

	@Test
	public void testIntToBinaryArrayWithBigNumber() {
		boolean[] expected = new boolean[32];
		for (int i = 0; i < expected.length; i++) {
			expected[i] = true;
		}

		assertThat(expected, equalTo(NumberConverter.intToBinaryArray(-1)));
	}

	/*
	 * byteToBinaryArray
	 */

	@Test
	public void testByteToBinaryArray() {
		boolean[] expected = new boolean[3];
		for (int i = 0; i < expected.length; i++) {
			expected[i] = true;
		}

		assertThat(expected,
				equalTo(NumberConverter.byteToBinaryArray((byte) 7)));
	}

	@Test
	public void testByteToBinaryArrayWithBigNumber() {
		boolean[] expected = new boolean[8];
		for (int i = 0; i < expected.length; i++) {
			expected[i] = true;
		}

		assertThat(expected,
				equalTo(NumberConverter.byteToBinaryArray((byte) -1)));
	}

	/*
	 * binaryArrayToInt
	 */

	@Test
	public void testBinaryArrayToInt() {
		boolean[] array = new boolean[3];
		for (int i = 0; i < array.length; i++) {
			array[i] = true;
		}

		assertEquals(7, NumberConverter.binaryArrayToInt(array));
	}

	@Test
	public void testBinaryArrayToIntWithBigNumber() {
		boolean[] array = new boolean[35];
		for (int i = 0; i < array.length; i++) {
			array[i] = true;
		}

		assertEquals(-1, NumberConverter.binaryArrayToInt(array));
	}

}
