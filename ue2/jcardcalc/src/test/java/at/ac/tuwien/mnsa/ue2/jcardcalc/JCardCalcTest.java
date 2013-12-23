package at.ac.tuwien.mnsa.ue2.jcardcalc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import javacard.framework.AID;
import javacard.framework.Applet;

import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.licel.jcardsim.base.Simulator;

@SuppressWarnings("restriction")
public class JCardCalcTest {
	private static final Logger LOG = LoggerFactory
			.getLogger(JCardCalcTest.class);

	// Constants
	private static final AID AID = new AID(hexToBytes("8985C0E78B8117F6D8"),
			(short) 0, (byte) 9);
	private static final Class<? extends Applet> CLAZZ = JCardCalc.class;

	private static final byte[] SW_OK = new byte[] { (byte) 0x90, (byte) 0x00 };
	private static final byte[] SW_UNKNOWN = new byte[] { (byte) 0x6D,
			(byte) 0x00 };
	private static final byte[] ZERO_RESPONSE = new byte[] { 0x00, 0x00 };
	private static final byte ZERO = 0x00;

	// Fields
	private Simulator simulator;

	@Before
	public void setUp() throws Exception {
		simulator = new Simulator();
		simulator.installApplet(AID, CLAZZ);
		simulator.selectApplet(AID);
	}

	@After
	public void tearDown() throws Exception {
		simulator.reset();
		simulator.resetRuntime();
	}

	@Test
	public void testNop() {
		LOG.info("testNop:");
		LOG.info("========");

		assertCalcResponse(JCardCalc.NOP, ZERO, ZERO, ZERO_RESPONSE, SW_OK);

		LOG.info(" ");
	}

	@Test
	public void testAdd() {
		LOG.info("testAdd:");
		LOG.info("========");

		// Dez: -5 + 2 = -3 Hex: FB - 02 = FD (Two's complement)
		assertCalcResponse(JCardCalc.ADD, (byte) 0xfb, (byte) 0x02, new byte[] {
				(byte) 0xFF, (byte) 0xFD }, SW_OK);

		// Dez: 20 + 4 = 24 Hex: 14 + 04 = 18
		assertCalcResponse(JCardCalc.ADD, (byte) 0x14, (byte) 0x04, new byte[] {
				(byte) 0x00, (byte) 0x18 }, SW_OK);

		// Dez: 127 + 127 = 254 Hex: 7F + 7F = FE
		assertCalcResponse(JCardCalc.ADD, (byte) 0x7F, (byte) 0x7F, new byte[] {
				(byte) 0x00, (byte) 0xFE }, SW_OK);

		// Dez: -23 + 23 = 0 Hex: E9 + 17 = 00 (Two's complement)
		assertCalcResponse(JCardCalc.ADD, (byte) 0xE9, (byte) 0x17, new byte[] {
				(byte) 0x00, (byte) 0x00 }, SW_OK);

		// Dez: -8 + -12 = -20 Hex: F8 + F4 = EC (Two's complement)
		assertCalcResponse(JCardCalc.ADD, (byte) 0xF8, (byte) 0xF4, new byte[] {
				(byte) 0xFF, (byte) 0xEC }, SW_OK);

		// Dez: 0 + 0 = 0 Hex: 00 + 00 = 00
		assertCalcResponse(JCardCalc.ADD, (byte) 0x00, (byte) 0x00, new byte[] {
				(byte) 0x00, (byte) 0x00 }, SW_OK);

		// Dez: 20 + -56 = -36 Hex: 14 + C8 = DC (Two's complement)
		assertCalcResponse(JCardCalc.ADD, (byte) 0x14, (byte) 0xC8, new byte[] {
				(byte) 0xFF, (byte) 0xDC }, SW_OK);

		// Dez: 27 + 100 = 127 Hex: 1B + 64 = 7F
		assertCalcResponse(JCardCalc.ADD, (byte) 0x1B, (byte) 0x64, new byte[] {
				(byte) 0x00, (byte) 0x7F }, SW_OK);

		// Dez: 127 + 0 = 127 Hex: 7F + 00 = 7F
		assertCalcResponse(JCardCalc.ADD, (byte) 0x7F, (byte) 0x00, new byte[] {
				(byte) 0x00, (byte) 0x7F }, SW_OK);

		// Dez: -128 + 28 = -100 Hex: 80 + 1C = 9C (Two's complement)
		assertCalcResponse(JCardCalc.ADD, (byte) 0x80, (byte) 0x1C, new byte[] {
				(byte) 0xFF, (byte) 0x9C }, SW_OK);

		LOG.info(" ");
	}

	@Test
	public void testSub() {
		LOG.info("testSub:");
		LOG.info("========");

		// Dez: 10 - 6 = 4 Hex: 0A - 06 = 04
		assertCalcResponse(JCardCalc.SUB, (byte) 0x0A, (byte) 0x06, new byte[] {
				(byte) 0x00, (byte) 0x04 }, SW_OK);

		// Dez: -110 - 18 = -128 Hex: 92 - 12 = 80 (Two's complement)
		assertCalcResponse(JCardCalc.SUB, (byte) 0x92, (byte) 0x12, new byte[] {
				(byte) 0xFF, (byte) 0x80 }, SW_OK);

		// Dez: -15 - -30 = 15 Hex: F1 - E2 = 0F (Two's complement)
		assertCalcResponse(JCardCalc.SUB, (byte) 0xF1, (byte) 0xE2, new byte[] {
				(byte) 0x00, (byte) 0x0F }, SW_OK);

		// Dez: 126 - -1 = 127 Hex: 7E - FF = 7F (Two's complement)
		assertCalcResponse(JCardCalc.SUB, (byte) 0x7E, (byte) 0xFF, new byte[] {
				(byte) 0x00, (byte) 0x7F }, SW_OK);

		// Dez: 127 - 127 = 0 Hex: 7F - 7F = 00
		assertCalcResponse(JCardCalc.SUB, (byte) 0x7F, (byte) 0x7F, new byte[] {
				(byte) 0x00, (byte) 0x00 }, SW_OK);

		// Dez: 0 - 0 = 0 Hex: 00 - 00 = 00
		assertCalcResponse(JCardCalc.SUB, (byte) 0x00, (byte) 0x00, new byte[] {
				(byte) 0x00, (byte) 0x00 }, SW_OK);

		// Dez: 25 - 26 = -1 Hex: 19 - 1A = FF (Two's complement)
		assertCalcResponse(JCardCalc.SUB, (byte) 0x19, (byte) 0x1A, new byte[] {
				(byte) 0xFF, (byte) 0xFF }, SW_OK);

		// Dez: 0 - 127 = -127 Hex: 00 - 7F = 81 (Two's complement)
		assertCalcResponse(JCardCalc.SUB, (byte) 0x00, (byte) 0x7F, new byte[] {
				(byte) 0xFF, (byte) 0x81 }, SW_OK);

		// Dez: 54 - 4 = 50 Hex: 36 - 04 = 32
		assertCalcResponse(JCardCalc.SUB, (byte) 0x36, (byte) 0x04, new byte[] {
				(byte) 0x00, (byte) 0x32 }, SW_OK);

		// Dez: -128 - -127 = -1 Hex: 80 - 81 = FF (Two's complement)
		assertCalcResponse(JCardCalc.SUB, (byte) 0x80, (byte) 0x81, new byte[] {
				(byte) 0xFF, (byte) 0xFF }, SW_OK);

		LOG.info(" ");
	}

	@Test
	public void testMul() {
		LOG.info("testMul:");
		LOG.info("========");

		// Dez: 5 * 4 = 20 Hex: 05 * 04 = 14
		assertCalcResponse(JCardCalc.MUL, (byte) 0x05, (byte) 0x04, new byte[] {
				(byte) 0x00, (byte) 0x14 }, SW_OK);

		// Dez: 6 * -10 = -60 Hex: 06 * F6 = C4 (Two's complement)
		assertCalcResponse(JCardCalc.MUL, (byte) 0x06, (byte) 0xF6, new byte[] {
				(byte) 0xFF, (byte) 0xC4 }, SW_OK);

		// Dez: -7 * 14 = -98 Hex: F9 * 0E = 9E (Two's complement)
		assertCalcResponse(JCardCalc.MUL, (byte) 0xF9, (byte) 0x0E, new byte[] {
				(byte) 0xFF, (byte) 0x9E }, SW_OK);

		// Dez: -46 * -3 = 138 Hex: D2 * FD = 8A (Two's complement)
		assertCalcResponse(JCardCalc.MUL, (byte) 0xD2, (byte) 0xFD, new byte[] {
				(byte) 0x00, (byte) 0x8A }, SW_OK);

		// Dez: 45 * 63 = 2835 Hex: 2D * 3F = 0B13
		assertCalcResponse(JCardCalc.MUL, (byte) 0x2D, (byte) 0x3F, new byte[] {
				(byte) 0x0B, (byte) 0x13 }, SW_OK);

		// Dez: 127 * 127 = 16129 Hex: 7F * 7F = 3F01
		assertCalcResponse(JCardCalc.MUL, (byte) 0x7F, (byte) 0x7F, new byte[] {
				(byte) 0x3F, (byte) 0x01 }, SW_OK);

		// Dez: -128 * -128 = 16384 Hex: 80 * 80 = 4000 (Two's complement)
		assertCalcResponse(JCardCalc.MUL, (byte) 0x80, (byte) 0x80, new byte[] {
				(byte) 0x40, (byte) 0x00 }, SW_OK);

		// Dez: 0 * 0 = 0 Hex: 00 * 00 = 00
		assertCalcResponse(JCardCalc.MUL, (byte) 0x00, (byte) 0x00, new byte[] {
				(byte) 0x00, (byte) 0x00 }, SW_OK);

		// Dez: 0 * 127 = 0 Hex: 00 * 7F = 00
		assertCalcResponse(JCardCalc.MUL, (byte) 0x00, (byte) 0x7F, new byte[] {
				(byte) 0x00, (byte) 0x00 }, SW_OK);

		// Dez: -128 * 127 = -16256 Hex: 80 * 7F = C080 (Two's complement)
		assertCalcResponse(JCardCalc.MUL, (byte) 0x80, (byte) 0x7F, new byte[] {
				(byte) 0xC0, (byte) 0x80 }, SW_OK);

		LOG.info(" ");
	}

	@Test
	public void testAnd() {
		LOG.info("testAnd:");
		LOG.info("========");

		// Dez: 15 AND 15 = 15 Hex: F AND F = F
		assertCalcResponse(JCardCalc.AND, (byte) 0x0F, (byte) 0x0F, new byte[] {
				(byte) 0x00, (byte) 0x0F }, SW_OK);

		// Dez: 127 AND 65 = 65 Hex: 7F AND 41 = 41
		assertCalcResponse(JCardCalc.AND, (byte) 0x7F, (byte) 0x41, new byte[] {
				(byte) 0x00, (byte) 0x41 }, SW_OK);

		// Dez: -128 AND 127 = 0 Hex: 128 AND 7F = 00 (Two's complement)
		assertCalcResponse(JCardCalc.AND, (byte) 0x80, (byte) 0x7F, new byte[] {
				(byte) 0x00, (byte) 0x00 }, SW_OK);

		// Dez: 0 AND 0 = 0 Hex: 00 AND 00 = 00
		assertCalcResponse(JCardCalc.AND, (byte) 0x00, (byte) 0x00, new byte[] {
				(byte) 0x00, (byte) 0x00 }, SW_OK);

		// Dez: 127 AND 5 = 5 Hex: 7F AND 05 = 05
		assertCalcResponse(JCardCalc.AND, (byte) 0x7F, (byte) 0x05, new byte[] {
				(byte) 0x00, (byte) 0x05 }, SW_OK);

		// Dez: 20 AND 48 = 16 Hex: 14 AND 30 = 10
		assertCalcResponse(JCardCalc.AND, (byte) 0x14, (byte) 0x30, new byte[] {
				(byte) 0x00, (byte) 0x10 }, SW_OK);

		// Dez: 18 AND 4 = 0 Hex: 12 AND 04 = 00
		assertCalcResponse(JCardCalc.AND, (byte) 0x12, (byte) 0x04, new byte[] {
				(byte) 0x00, (byte) 0x00 }, SW_OK);

		// Dez: -61 AND 7 = 3 Hex: C3 AND 07 = 03 (Two's complement)
		assertCalcResponse(JCardCalc.AND, (byte) 0xC3, (byte) 0x07, new byte[] {
				(byte) 0x00, (byte) 0x03 }, SW_OK);

		// Dez: -47 AND -69 = 65 Hex: D1 AND BB = 91 (Two's complement)
		assertCalcResponse(JCardCalc.AND, (byte) 0xD1, (byte) 0xBB, new byte[] {
				(byte) 0xFF, (byte) 0x91 }, SW_OK);

		// Dez: -54 AND -37 = -54 Hex: CA AND DB = CA (Two's complement)
		assertCalcResponse(JCardCalc.AND, (byte) 0xCA, (byte) 0xDB, new byte[] {
				(byte) 0xFF, (byte) 0xCA }, SW_OK);

		LOG.info(" ");
	}

	@Test
	public void testOr() {
		LOG.info("testOr:");
		LOG.info("========");

		// Dez: 15 OR 15 = 15 Hex: F OR F = F
		assertCalcResponse(JCardCalc.OR, (byte) 0x0F, (byte) 0x0F, new byte[] {
				(byte) 0x00, (byte) 0x0F }, SW_OK);

		// Dez: 127 OR 65 = 127 Hex: 7F OR 41 = 7F
		assertCalcResponse(JCardCalc.OR, (byte) 0x7F, (byte) 0x41, new byte[] {
				(byte) 0x00, (byte) 0x7F }, SW_OK);

		// Dez: -128 OR 127 = -1 Hex: 128 OR 7F = FF (Two's complement)
		assertCalcResponse(JCardCalc.OR, (byte) 0x80, (byte) 0x7F, new byte[] {
				(byte) 0xFF, (byte) 0xFF }, SW_OK);

		// Dez: 0 OR 0 = 0 Hex: 00 OR 00 = 00
		assertCalcResponse(JCardCalc.OR, (byte) 0x00, (byte) 0x00, new byte[] {
				(byte) 0x00, (byte) 0x00 }, SW_OK);

		// Dez: 127 OR 5 = 127 Hex: 7F OR 05 = 7F
		assertCalcResponse(JCardCalc.OR, (byte) 0x7F, (byte) 0x05, new byte[] {
				(byte) 0x00, (byte) 0x7F }, SW_OK);

		// Dez: 20 OR 48 = 52 Hex: 14 OR 30 = 34
		assertCalcResponse(JCardCalc.OR, (byte) 0x14, (byte) 0x30, new byte[] {
				(byte) 0x00, (byte) 0x34 }, SW_OK);

		// Dez: 18 OR 4 = 22 Hex: 12 OR 04 = 16
		assertCalcResponse(JCardCalc.OR, (byte) 0x12, (byte) 0x04, new byte[] {
				(byte) 0x00, (byte) 0x16 }, SW_OK);

		// Dez: -61 OR 7 = -57 Hex: C3 OR 07 = C7 (Two's complement)
		assertCalcResponse(JCardCalc.OR, (byte) 0xC3, (byte) 0x07, new byte[] {
				(byte) 0xFF, (byte) 0xC7 }, SW_OK);

		// Dez: -47 OR -69 = -5 Hex: D1 OR BB = FB (Two's complement)
		assertCalcResponse(JCardCalc.OR, (byte) 0xD1, (byte) 0xBB, new byte[] {
				(byte) 0xFF, (byte) 0xFB }, SW_OK);

		// Dez: -54 OR -37 = -37 Hex: CA OR DB = DB (Two's complement)
		assertCalcResponse(JCardCalc.OR, (byte) 0xCA, (byte) 0xDB, new byte[] {
				(byte) 0xFF, (byte) 0xDB }, SW_OK);

		LOG.info(" ");
	}

	@Test
	public void testNot() {
		LOG.info("testNot:");
		LOG.info("========");

		// Dez: NOT 12 = -13 Hex: NOT 0C = F3 (Two's complement)
		assertCalcResponse(JCardCalc.NOT, (byte) 0x0C, (byte) 0x00, new byte[] {
				(byte) 0xFF, (byte) 0xF3 }, SW_OK);

		// Dez: NOT 0 = -1 Hex: NOT 00 = FF (Two's complement)
		assertCalcResponse(JCardCalc.NOT, (byte) 0x00, (byte) 0x00, new byte[] {
				(byte) 0xFF, (byte) 0xFF }, SW_OK);

		// Dez: NOT 1 = -2 Hex: NOT 01 = FE (Two's complement)
		assertCalcResponse(JCardCalc.NOT, (byte) 0x01, (byte) 0x00, new byte[] {
				(byte) 0xFF, (byte) 0xFE }, SW_OK);

		// Dez: NOT 127 = -128 Hex: NOT 7F = 80 (Two's complement)
		assertCalcResponse(JCardCalc.NOT, (byte) 0x7F, (byte) 0x00, new byte[] {
				(byte) 0xFF, (byte) 0x80 }, SW_OK);

		// Dez: NOT -128 = 127 Hex: NOT 80 = 7F (Two's complement)
		assertCalcResponse(JCardCalc.NOT, (byte) 0x80, (byte) 0x00, new byte[] {
				(byte) 0x00, (byte) 0x7F }, SW_OK);

		// Dez: NOT -1 = 0 Hex: NOT FF = 00 (Two's complement)
		assertCalcResponse(JCardCalc.NOT, (byte) 0xFF, (byte) 0x00, new byte[] {
				(byte) 0x00, (byte) 0x00 }, SW_OK);

		// Dez: NOT 7 = -8 Hex: NOT 07 = F8 (Two's complement)
		assertCalcResponse(JCardCalc.NOT, (byte) 0x07, (byte) 0x00, new byte[] {
				(byte) 0xFF, (byte) 0xF8 }, SW_OK);

		// Dez: NOT -2 = 1 Hex: NOT FE = 01 (Two's complement)
		assertCalcResponse(JCardCalc.NOT, (byte) 0xFE, (byte) 0x00, new byte[] {
				(byte) 0x00, (byte) 0x01 }, SW_OK);

		// Dez: NOT -80 = 79 Hex: NOT B0 = 4F (Two's complement)
		assertCalcResponse(JCardCalc.NOT, (byte) 0xB0, (byte) 0x00, new byte[] {
				(byte) 0x00, (byte) 0x4F }, SW_OK);

		// Dez: NOT -27 = 26 Hex: NOT E5 = 1A (Two's complement)
		assertCalcResponse(JCardCalc.NOT, (byte) 0xE5, (byte) 0x00, new byte[] {
				(byte) 0x00, (byte) 0x1A }, SW_OK);

		LOG.info(" ");
	}

	@Test
	public void testUnsupportedInstructions() {
		LOG.info("testUnsupportedInstructions:");
		LOG.info("============================");

		// Instruction: 0x09 --> Not supported --> Response: SW1=6D, SW2=00
		assertCalcResponse((byte) 0x09, (byte) 0x00, (byte) 0x00,
				"".getBytes(), SW_UNKNOWN);

		// Instruction: 0xA0 --> Not supported --> Response: SW1=6D, SW2=00
		assertCalcResponse((byte) 0xA0, (byte) 0x00, (byte) 0x00,
				"".getBytes(), SW_UNKNOWN);

		// Instruction: 0xB7 --> Not supported --> Response: SW1=6D, SW2=00
		assertCalcResponse((byte) 0xB7, (byte) 0x00, (byte) 0x00,
				"".getBytes(), SW_UNKNOWN);

		// Instruction: 0xC3 --> Not supported --> Response: SW1=6D, SW2=00
		assertCalcResponse((byte) 0xC3, (byte) 0x00, (byte) 0x00,
				"".getBytes(), SW_UNKNOWN);

		// Instruction: 0xD4 --> Not supported --> Response: SW1=6D, SW2=00
		assertCalcResponse((byte) 0xD4, (byte) 0x00, (byte) 0x00,
				"".getBytes(), SW_UNKNOWN);

		// Instruction: 0xE8 --> Not supported --> Response: SW1=6D, SW2=00
		assertCalcResponse((byte) 0xE8, (byte) 0x00, (byte) 0x00,
				"".getBytes(), SW_UNKNOWN);

		// Instruction: 0xF2 --> Not supported --> Response: SW1=6D, SW2=00
		assertCalcResponse((byte) 0xF2, (byte) 0x00, (byte) 0x00,
				"".getBytes(), SW_UNKNOWN);

		// Instruction: 0xCF --> Not supported --> Response: SW1=6D, SW2=00
		assertCalcResponse((byte) 0xCF, (byte) 0x00, (byte) 0x00,
				"".getBytes(), SW_UNKNOWN);

		// Instruction: 0xAA --> Not supported --> Response: SW1=6D, SW2=00
		assertCalcResponse((byte) 0xAA, (byte) 0x00, (byte) 0x00,
				"".getBytes(), SW_UNKNOWN);

		// Instruction: 0x63 --> Not supported --> Response: SW1=6D, SW2=00
		assertCalcResponse((byte) 0x63, (byte) 0x00, (byte) 0x00,
				"".getBytes(), SW_UNKNOWN);

		LOG.info(" ");
	}

	private void assertCalcResponse(byte ins, byte p1, byte p2,
			byte[] expectedResponseData, byte[] expectedSW) {
		assertAPDUResponse(new byte[] { 0x00, ins, p1, p2, 0x00 },
				expectedResponseData, expectedSW);
	}

	private void assertAPDUResponse(byte[] request,
			byte[] expectedResponseData, byte[] expectedResponseSW) {
		// The converts to CommandAPDU and after transmission to ResponseAPDU
		// are just for checking the right alignment of the APDUs. We would also
		// be able to just feed the method with raw byte arrays and evaluate the
		// raw output byte array
		byte[] bResponse = simulator.transmitCommand(new CommandAPDU(request)
				.getBytes());
		ResponseAPDU response = new ResponseAPDU(bResponse);

		LOG.info("Expected: " + bytesToHex(expectedResponseData)
				+ "; Response: " + bytesToHex(response.getData()) + "; SW1: "
				+ bytesToHex((byte) response.getSW1()) + "; SW2: "
				+ bytesToHex((byte) response.getSW2()));

		assertArrayEquals(expectedResponseData, response.getData());
		assertEquals(expectedResponseSW[0], (byte) response.getSW1());
		assertEquals(expectedResponseSW[1], (byte) response.getSW2());
	}

	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	/**
	 * Original method from <a href=
	 * "http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java"
	 * >stackoverflow.com</a>
	 */
	private static final String bytesToHex(byte... bytes) {
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
	private static final byte[] hexToBytes(String s) {
		// There is no checking in this method. It should only be used if you
		// know what you are doing.
		// TODO Check if length > 1
		// TODO Check if lenght % 2 == 0
		// TODO ...

		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

}
