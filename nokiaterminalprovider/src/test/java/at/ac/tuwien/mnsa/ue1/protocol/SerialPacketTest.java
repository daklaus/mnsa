package at.ac.tuwien.mnsa.ue1.protocol;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Categories.ExcludeCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerialPacketTest {
	Logger log = LoggerFactory.getLogger(SerialPacketTest.class);

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
	public void testGetBytes() throws TooLongPayloadException {
		final byte nodeAddress = 0x10;
		final int expectedLength = 0x300;
		final byte pattern = 0x13;
		final byte msgType = SerialPacket.TYPE_DEBUGINFO;

		SerialPacket p = new SerialPacket(msgType, nodeAddress,
				createByteArrayFromPattern(pattern, expectedLength));

		byte[] b = p.getBytes();

		assertEquals(b[SerialPacket.OFFSET_MTY], msgType);
		assertEquals(b[SerialPacket.OFFSET_NAD], nodeAddress);
		assertEquals(b[SerialPacket.OFFSET_LNH],
				(expectedLength >> 8) & 0x000000ff);
		assertEquals(b[SerialPacket.OFFSET_LNL], expectedLength & 0x000000ff);
		for (int i = 0; i < expectedLength; i++) {
			assertEquals(pattern, b[SerialPacket.OFFSET_PY + i]);
		}
	}

	@Test
	public void testGetIntFromUnsignedShortBytes()
			throws TooLongPayloadException {
		final byte nodeAddress = 0x10;
		final int expectedLength = 0x900;
		final byte pattern = 0x13;
		final byte msgType = SerialPacket.TYPE_DEBUGINFO;

		SerialPacket p = new SerialPacket(msgType, nodeAddress,
				createByteArrayFromPattern(pattern, expectedLength));

		byte[] b = p.getBytes();
		int actualLength = SerialPacket.getIntFromUnsignedShortBytes(
				b[SerialPacket.OFFSET_LNH], b[SerialPacket.OFFSET_LNL]);

		// logByteArray(b);

		assertEquals(expectedLength, actualLength);
	}

	@Test(expected = TooLongPayloadException.class)
	public void testConstructorShouldFail() throws Exception {
		final byte nodeAddress = 0x10;
		final int expectedLength = SerialPacket.MAX_LENGTH + 1;
		final byte pattern = 0x13;
		final byte msgType = SerialPacket.TYPE_DEBUGINFO;

		new SerialPacket(msgType, nodeAddress, createByteArrayFromPattern(
				pattern, expectedLength));
	}

	@Test
	public void testGetLength() throws TooLongPayloadException {
		final byte nodeAddress = 0x10;
		final int expectedLength = 0x900;
		final byte pattern = 0x13;
		final byte msgType = SerialPacket.TYPE_DEBUGINFO;

		SerialPacket p = new SerialPacket(msgType, nodeAddress,
				createByteArrayFromPattern(pattern, expectedLength));

		assertEquals(expectedLength, p.getLength());
	}

	private byte[] createByteArrayFromPattern(byte pattern, int length) {
		byte[] ret = new byte[length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = pattern;
		}
		return ret;
	}

	private void logByteArray(byte[] b) {
		StringBuilder sb = new StringBuilder("0x");
		for (int i = 0; i < b.length; i++) {
			sb.append(String.format("%02X", b[i]));
		}
		log.debug(sb.toString());
	}
}
