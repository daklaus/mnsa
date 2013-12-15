package at.ac.tuwien.mnsa.ue1.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * <p>
 * This is a packet. A unit for sending and receiving data over a serial
 * communication to a smart card. This protocol is based on the one found at <a
 * href=
 * "http://www.win.tue.nl/pinpasjc/docs/apis/offcard/com/ibm/jc/terminal/RemoteJCTerminal.html"
 * >this link</a>. The packet structure look like the following table:
 * </p>
 * 
 * <table border="1">
 * <tbody>
 * <tr>
 * <th>Byte #</th>
 * <th>Abbr.</th>
 * <th>Meaning</th>
 * </tr>
 * <tr>
 * <td>0</td>
 * <td>MTY</td>
 * <td>Message type</td>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>NAD</td>
 * <td>Node address</td>
 * </tr>
 * <tr>
 * <td>2</td>
 * <td>LNH</td>
 * <td>High byte of payload length</td>
 * </tr>
 * <tr>
 * <td>3</td>
 * <td>LNL</td>
 * <td>Low byte of payload length</td>
 * </tr>
 * <tr>
 * <td>4...</td>
 * <td>PY0</td>
 * <td>First byte of payload (interpretation depends on message type)</td>
 * </tr>
 * <tr>
 * <td>...3+(LNH*256)+LNL</td>
 * <td>PYn</td>
 * <td>Last byte of payload</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * @see <a
 *      href="http://www.win.tue.nl/pinpasjc/docs/apis/offcard/com/ibm/jc/terminal/RemoteJCTerminal.html">RemoteJCTerminal</a>
 */
public class SerialPacket {

	/*
	 * The types available for the MTY field
	 */

	/**
	 * Wait for card (MTY=0x00)<br/>
	 * The payload contains four bytes denoting the time in milliseconds the
	 * remote part will wait for card insertion. The bytes are sent in big
	 * endian format. The reply message contains the full ATR as payload. A
	 * reply message with 0 bytes length means that the terminal could not
	 * trigger an ATR (reason might be retrieved using MTY=3 or MTY=2.
	 */
	public static final byte TYPE_WAIT = 0x00;
	/**
	 * APDU data (MTY=0x01)<br/>
	 * This message transports APDU data. The payload contains the byte of the
	 * APDU and the reply the response APDU.
	 */
	public static final byte TYPE_APDU = 0x01;
	/**
	 * Status (MTY=0x02)<br/>
	 * The request message contains no payload (LNH==LNL==0) and the reply has
	 * length 4 bytes and denotes big endian integer. It contains values as
	 * described in the abstract class JCTerminal as legal return values the
	 * getStatus method. If status is not supported return value 0.
	 */
	public static final byte TYPE_STATUS = 0x02;
	/**
	 * Error message (MTY=0x03)<br/>
	 * Request the remote error message. Request payload has length 0 and return
	 * payload contains the last error message in ASCII encoding. If this
	 * message is not supported then an empty string is returned.
	 */
	public static final byte TYPE_ERROR = 0x03;
	/**
	 * Terminal info (MTY=0x04)<br/>
	 * The request message has payload length 0 and the reply contains an ASCII
	 * string specifiying terminal details like device type, port, etc. If this
	 * message is not supported then an empty string is returned.
	 */
	public static final byte TYPE_TERMINFO = 0x04;
	/**
	 * Initialization data (MTY=0x05)<br/>
	 * The request message contains a terminal specification. The interpretation
	 * of this data depends on the server. The reply has length 0 if
	 * initialization parameter have been accepted by server. Otherwise an ASCII
	 * string is sent describing the rejection reason.
	 */
	public static final byte TYPE_INIT = 0x05;
	/**
	 * Information text (MTY=0x06)<br/>
	 * The data is a string in encoding ISO8859_1 to be displayed at remote
	 * terminal server for informational purposes to its operator.
	 */
	public static final byte TYPE_INFOTEXT = 0x06;
	/**
	 * Debug information (MTY=0x07)<br/>
	 * The command and response data is a JCOP-simulator debug message. This
	 * message sets or retrieves information in the simulator.
	 */
	public static final byte TYPE_DEBUGINFO = 0x07;

	/*
	 * Byte offsets of the fields
	 */
	public static final byte OFFSET_MTY = 0;
	public static final byte OFFSET_NAD = 1;
	public static final byte OFFSET_LNH = 2;
	public static final byte OFFSET_LNL = 3;
	public static final byte OFFSET_PY = 4;
	public static final byte HEADER_LENGTH = OFFSET_PY;

	public static final int MAX_LENGTH = 65535; // 2 bytes = 2^16 - 1
	public static final byte DEFAULT_NAD = 0x00;

	private static final String STRING_CHARSET = "UTF-8";

	/*
	 * Fields
	 */
	private final byte messageType;
	private final byte nodeAddress;
	private final byte[] payload;

	/**
	 * Creates a serial packet for the specified message type and node address
	 * with the specified payload.
	 * 
	 * @param messageType
	 *            the message type of the packet. For all types see the constant
	 *            fields of this class beginning with "TYPE_".
	 * @param nodeAddress
	 *            the node address of listening process where the package should
	 *            arrive. Node addresses have to be arranged on your own.
	 * @param payload
	 *            the bytes of the payload you want to send with this package.
	 * @throws TooLongPayloadException
	 *             If the size of the payload array exceeds the maximum length
	 *             as defined in {@link #MAX_LENGTH}.
	 */
	public SerialPacket(byte messageType, byte nodeAddress, byte[] payload)
			throws TooLongPayloadException {
		if (payload != null && payload.length >= MAX_LENGTH)
			throw new TooLongPayloadException(payload.length);

		this.messageType = messageType;
		this.nodeAddress = nodeAddress;
		this.payload = payload;
	}

	public SerialPacket(byte messageType, byte nodeAddress, String payload)
			throws TooLongPayloadException {
		this.messageType = messageType;
		this.nodeAddress = nodeAddress;

		if (payload == null) {
			this.payload = null;
			return;
		}

		byte[] bPayload = new byte[] {};
		try {
			bPayload = payload.getBytes(STRING_CHARSET);
		} catch (UnsupportedEncodingException e) {
			// We trust in that the charset constant given in this class is a
			// valid charset
		}

		if (bPayload.length >= MAX_LENGTH)
			throw new TooLongPayloadException(bPayload.length);

		this.payload = bPayload;
	}

	/**
	 * Used for zero payload. For the parameters see
	 * {@link #SerialPacket(byte, byte, byte[])}
	 * 
	 * @throws TooLongPayloadException
	 *             If and only if {@link #SerialPacket(byte, byte, byte[])}
	 *             throws the exception
	 * 
	 * @see {@link #SerialPacket(byte, byte, byte[])}
	 */
	public SerialPacket(byte messageType, byte nodeAddress)
			throws TooLongPayloadException {
		this(messageType, nodeAddress, (byte[]) null);
	}

	/**
	 * Reads the contents of a serial packet from an input stream and creates a
	 * SerialPacket from it. This method <strong>blocks</strong> until there are
	 * all the bytes of the packet present on the stream (at least the bytes of
	 * the header if the payload length is zero).
	 * 
	 * @param inStream
	 *            the stream to be read from
	 * @return a SerialPacket class with the contents from the stream
	 * @throws IOException
	 * @throws TooLongPayloadException
	 *             If and only if {@link #SerialPacket(byte, byte, byte[])}
	 *             throws the exception
	 */
	public static SerialPacket readFromStream(InputStream inStream)
			throws IOException {
		byte[] buffer = new byte[HEADER_LENGTH];
		readFully(inStream, buffer, 0, HEADER_LENGTH);

		byte messageType = buffer[OFFSET_MTY];
		byte nodeAddress = buffer[OFFSET_NAD];
		int length = getIntFromUnsignedShortBytes(buffer[OFFSET_LNH],
				buffer[OFFSET_LNL]);

		byte[] payload = null;
		if (length > 0) {
			payload = new byte[length];
			readFully(inStream, payload, 0, length);
		}

		SerialPacket p = null;
		try {
			p = new SerialPacket(messageType, nodeAddress, payload);
		} catch (TooLongPayloadException e) {
			// Cannot happen since we are just reading not more than we can
			// handle
		}

		return p;
	}

	/**
	 * Returns the whole packet as a byte array
	 * 
	 * @return the package as a byte array
	 */
	// TODO Change to private and rewrite unit tests accordingly
	// It is only for the use of unit tests not set to private
	byte[] getBytes() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			this.write(os);
		} catch (IOException e) {
			// This cannot happen since we are just using an in-memory stream
		}
		return os.toByteArray();
	}

	/**
	 * Writes the packet out in a stream
	 * 
	 * @param outStream
	 *            the stream to which the packet will be written
	 * @throws IOException
	 *             If and only if {@link java.io.OutputStream#write(int)} throws
	 *             the exception
	 */
	public void write(OutputStream outStream) throws IOException {
		outStream.write(messageType);
		outStream.write(nodeAddress);

		byte[] length = getLengthAsByteArray();
		outStream.write(length[0]);
		outStream.write(length[1]);

		if (payload != null && payload.length > 0)
			outStream.write(payload, 0, payload.length);
	}

	/**
	 * Get the length of the payload as a byte array with the length 2. The two
	 * bytes can be interpreted as an unsigned short.
	 * 
	 * @return a byte array of length two representing the length of the payload
	 */
	private byte[] getLengthAsByteArray() {
		if (payload == null || payload.length <= 0)
			return new byte[] { 0x00, 0x00 };

		int length = payload.length;

		if (length <= 0)
			return new byte[] { 0x00, 0x00 };

		byte[] ret = new byte[2];
		ret[0] = (byte) ((length & 0x0000ff00) >>> 8);
		ret[1] = (byte) ((length) & 0x000000ff);
		return ret;
	}

	/**
	 * Returns the number assembled from the high and low byte of a short
	 * interpreted as unsigned number.
	 * 
	 * @param lnh
	 *            the high byte of the short
	 * @param lnl
	 *            the low byte of the short
	 * @return the integer of the interpreted short
	 */
	// TODO Change to private and rewrite unit tests accordingly
	// It is only for the use of unit tests not set to private
	static int getIntFromUnsignedShortBytes(byte lnh, byte lnl) {
		return ((lnh & 0x000000ff) << 8) + (lnl & 0x000000ff);
	}

	public byte getMessageType() {
		return messageType;
	}

	public byte getNodeAddress() {
		return nodeAddress;
	}

	public int getLength() {
		if (payload == null)
			return 0;
		return payload.length;
	}

	public byte[] getPayload() {
		return payload;
	}

	public String getPayloadAsString() {
		if (payload == null)
			return null;

		String ret = null;
		try {
			ret = new String(payload, STRING_CHARSET);
		} catch (UnsupportedEncodingException e) {
			// We trust in that the charset constant given in this class is a
			// valid charset
		}
		return ret;
	}

	public String toString() {
		String out = "MTY[" + messageType + "] NAD[" + nodeAddress + "]";
		if (payload != null && payload.length > 0)
			out += " LN[" + payload.length + "] PY[" + payload + "]";
		return out;
	}

	/**
	 * Try to read as long and as often until we get all the bytes requested by
	 * the length
	 * 
	 * @param inStream
	 *            the stream to be read from
	 * @param output
	 *            the array where to be written
	 * @param offset
	 *            an offset in the stream and output array
	 * @param length
	 *            the number of bytes to be read
	 * @throws IOException
	 *             If and only if
	 *             {@link java.io.InputStream#read(byte[], int, int)} throws the
	 *             exception
	 */
	private static void readFully(InputStream inStream, byte[] output,
			int offset, int length) throws IOException {
		int alreadyRead = 0;
		while (alreadyRead < length) {
			int read = inStream.read(output, offset + alreadyRead, length
					- alreadyRead);
			if (read == -1)
				break;
			alreadyRead += read;
		}
	}

}