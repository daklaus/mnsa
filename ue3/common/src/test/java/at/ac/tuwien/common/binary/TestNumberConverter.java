package at.ac.tuwien.common.binary;

import static org.junit.Assert.*;

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

	@Test
	public void testBytesToHex1() {
		fail("Not yet implemented");
	}

	/*******************************
	 * hexToBytes
	 *******************************/

	@Test
	public void testHexToBytesShouldPass() {
		NumberConverter.hexToBytes("0x00");
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
