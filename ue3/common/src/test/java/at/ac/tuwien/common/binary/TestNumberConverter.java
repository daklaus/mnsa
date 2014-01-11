package at.ac.tuwien.common.binary;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestNumberConverter {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	public TestNumberConverter() {
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

	/* **********************************
	 * bytesToHex *********************************
	 */

	@Test
	public void testBytesToHexShouldPass() {
		assertEquals("Empty array doesn't work", "",
				NumberConverter.bytesToHex(new byte[] {}));
		assertEquals("Single byte doesn't work", "42",
				NumberConverter.bytesToHex((byte) 0x42));
		assertEquals("Multiple bytes as paramters don't work", "4F00F2",
				NumberConverter.bytesToHex((byte) 0x4F, (byte) 0x00,
						(byte) 0xF2));
		assertEquals(
				"Byte array doesn't work",
				"4F00F2",
				NumberConverter.bytesToHex(new byte[] { (byte) 0x4F,
						(byte) 0x00, (byte) 0xF2 }));
	}

	@Test
	public void testBytesToHexShouldFailNull() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("null");
		NumberConverter.bytesToHex(null);
	}

	/* **********************************
	 * hexToBytes *********************************
	 */

	@Test
	public void testHexToBytesShouldPass() {
		assertArrayEquals("0x prefix doesn't work", new byte[] { (byte) 0x42 },
				NumberConverter.hexToBytes("0x42"));
		assertArrayEquals("h postfix doesn't work", new byte[] { (byte) 0x42 },
				NumberConverter.hexToBytes("42h"));
		assertArrayEquals("Single byte doesn't work",
				new byte[] { (byte) 0x42 }, NumberConverter.hexToBytes("42"));
		assertArrayEquals("Single character doesn't work",
				new byte[] { (byte) 0x02 }, NumberConverter.hexToBytes("2"));
		assertArrayEquals("Odd string length doesn't work",
				NumberConverter.hexToBytes("042"), new byte[] { (byte) 0x00,
						(byte) 0x42 });
		assertArrayEquals("Lowercase hex-letters don't work",
				new byte[] { (byte) 0x3f }, NumberConverter.hexToBytes("3f"));
		assertArrayEquals("Uppercase hex-letters don't work",
				new byte[] { (byte) 0x3f }, NumberConverter.hexToBytes("0x3F"));
		assertArrayEquals("Spaces between don't work",
				new byte[] { (byte) 0x5a },
				NumberConverter.hexToBytes("0x   5a    h"));
		assertArrayEquals("Tabs and newlines inbetween don't work", new byte[] {
				(byte) 0x5a, (byte) 0xe6, (byte) 0x78 },
				NumberConverter.hexToBytes(" 5 a e    6 \t  7  \n  8   h  "));
		assertArrayEquals(
				"Underscores and spaces as byte seperators don't work",
				new byte[] { (byte) 0x46, (byte) 0x5f, (byte) 0x13,
						(byte) 0x28, (byte) 0xa4, (byte) 0x63, (byte) 0x18,
						(byte) 0xe9, (byte) 0x6b, (byte) 0x13 },
				NumberConverter.hexToBytes("46_5F_13_28 a4_63 18_e9_6B 13"));
	}

	@Test
	public void testHexToBytesShouldFailNull() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("null");
		NumberConverter.hexToBytes(null);
	}

	@Test
	public void testHexToBytesShouldFailEmpty01() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("empty");
		NumberConverter.hexToBytes("");
	}

	@Test
	public void testHexToBytesShouldFailEmpty02() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("empty");
		NumberConverter.hexToBytes("0x");
	}

	@Test
	public void testHexToBytesShouldFailEmpty03() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("empty");
		NumberConverter.hexToBytes("h");
	}

	@Test
	public void testHexToBytesShouldFailEmpty04() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("empty");
		NumberConverter.hexToBytes("0x  h");
	}

	@Test
	public void testHexToBytesShouldFailEmpty05() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("empty");
		NumberConverter.hexToBytes("   0x     ");
	}

	@Test
	public void testHexToBytesShouldFailEmpty06() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("empty");
		NumberConverter.hexToBytes("   0x  \t _    h    ");
	}

}
