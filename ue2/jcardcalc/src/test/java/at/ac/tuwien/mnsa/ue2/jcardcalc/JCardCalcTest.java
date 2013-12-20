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
		assertCalcResponse(JCardCalc.NOP, ZERO, ZERO, ZERO_RESPONSE, SW_OK);
	}

	@Test
	public void testAdd() {
		// -5 + 2 = -3
		assertCalcResponse(JCardCalc.ADD, (byte) 0xfb, (byte) 0x02, new byte[] {
				(byte) 0xff, (byte) 0xfd }, SW_OK);
		// TODO Write tests like the one in testAdd; mind 10 per test method;
		// cover all
		// cases; for negative numbers use the two's complement
	}

	@Test
	public void testSub() {
		// TODO Write tests like the one in testAdd; mind 10 per test method;
		// cover all
		// cases; for negative numbers use the two's complement
	}

	@Test
	public void testMul() {
		// TODO Write tests like the one in testAdd; mind 10 per test method;
		// cover all
		// cases; for negative numbers use the two's complement
	}

	@Test
	public void testAnd() {
		// TODO Write tests like the one in testAdd; mind 10 per test method;
		// cover all
		// cases; for negative numbers use the two's complement
	}

	@Test
	public void testOr() {
		// TODO Write tests like the one in testAdd; mind 10 per test method;
		// cover all
		// cases; for negative numbers use the two's complement
	}

	@Test
	public void testNot() {
		// TODO Write tests like the one in testAdd; mind 10 per test method;
		// cover all
		// cases; for negative numbers use the two's complement
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
