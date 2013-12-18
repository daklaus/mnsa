package at.ac.tuwien.mnsa.ue2.jcardcalc;

import javacard.framework.APDU;
import javacard.framework.ISO7816;
import javacard.framework.Applet;
import javacard.framework.ISOException;
import javacard.framework.Util;

public class JCardCalc extends Applet {

	// Constants for Instructions
	public static final byte NOP = (byte) 0x00;
	public static final byte ADD = (byte) 0x01;
	public static final byte SUB = (byte) 0x02;
	public static final byte MUL = (byte) 0x03;
	public static final byte DIV = (byte) 0x04;
	public static final byte AND = (byte) 0x05;
	public static final byte OR = (byte) 0x06;
	public static final byte NOT = (byte) 0x07;

	/**
	 * Calls {@link #register()}.
	 */
	protected JCardCalc() {
		register();
	}

	/**
	 * Calls the constructor {@link #JCardCalc()}.
	 * 
	 * @see Applet#install(byte[], short, byte)
	 */
	public static void install(byte[] bArray, short bOffset, byte bLength) {
		new JCardCalc();
	}

	/**
	 * Processes an incoming APDU.
	 * 
	 * @see {@link APDU}, {@link Applet#process(APDU)}
	 * @param apdu
	 *            the incoming APDU
	 */
	@Override
	public void process(APDU apdu) {
		// Good practice: Return 9000 on SELECT
		if (selectingApplet()) {
			return;
		}

		byte[] buf = apdu.getBuffer();
		short p1 = buf[ISO7816.OFFSET_P1];
		short p2 = buf[ISO7816.OFFSET_P2];
		short result = 0;

		switch (buf[ISO7816.OFFSET_INS]) {
		case NOP:
			result = 0;
			break;
		case ADD:
			result = (short) (p1 + p2);
			break;
		case SUB:
			result = (short) (p1 - p2);
			break;
		case MUL:
			result = (short) (p1 * p2);
			break;
		// case DIV:
		// break;
		case AND:
			result = (short) (p1 & p2);
			break;
		case OR:
			result = (short) (p1 | p2);
			break;
		case NOT:
			result = (short) (~p1);
			break;
		default:
			// Good practice: If you don't know the INStruction, say so
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}

		// Convert short to byte[2], then copy to the output buffer
		Util.arrayCopyNonAtomic(toBytes(result), (short) 0, buf, (short) 0,
				(short) 2);
		// Set the length of the output buffer and flush
		apdu.setOutgoingAndSend((short) 0, (short) 2);
	}

	/**
	 * Helper function to convert a short to byte array.
	 * 
	 * @param s
	 *            short which needs to be converted to a byte array
	 * @return byte array with length of 2 containing the high and low byte of
	 *         the short.
	 */
	public static byte[] toBytes(short s) {
		// byte highx = (byte) ((s & 0xFF00) >> 8);
		// Causes a memory leak by allocating memory on the heap EVERY time it
		// is called. You should shift right then mask

		// 1. Shift by 8
		// 2. Cast to byte
		// 3. Mask as Byte
		// 4. Cast to byte
		byte high = (byte) ((byte) (s >> 8) & 0xff);
		byte low = (byte) (s & 0xff);
		return new byte[] { high, low };
	}

}