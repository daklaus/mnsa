package at.ac.tuwien.mnsa.ue1.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This is a packet. A unit for sending and receiving data over a serial
 * communication to a smart card. This protocol is based on the one found at <a
 * href=
 * "http://www.win.tue.nl/pinpasjc/docs/apis/offcard/com/ibm/jc/terminal/RemoteJCTerminal.html"
 * >this link</a>.
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
	private static final byte OFFSET_MTY = 0;
	private static final byte OFFSET_NAD = 1;
	private static final byte OFFSET_LNH = 2;
	private static final byte OFFSET_LNL = 3;
	private static final byte OFFSET_PY = 4;
	private static final byte HEADER_LENGTH = OFFSET_PY;

	// TODO Add fields according to the reference
	private final byte messageType;
	private final byte nodeAddress;
	private final short length;
	private final byte[] payload;

	private SerialPacket(byte messageType, byte nodeAddress, short length,
			byte[] payload) {
		this.messageType = messageType;
		this.nodeAddress = nodeAddress;
		this.length = length;
		this.payload = payload;
	}

	public SerialPacket(byte messageType, byte nodeAddress) {
		this(messageType, nodeAddress, (short) 0, null);
	}

	public static SerialPacket readFromStream(InputStream inStream)
			throws IOException {
		byte[] buffer = new byte[HEADER_LENGTH];
		readFully(inStream, buffer, 0, HEADER_LENGTH);

		byte messageType = buffer[OFFSET_MTY];
		byte nodeAddress = buffer[OFFSET_NAD];
		// TODO Check if it this calculation is correct
		short length = (short) (buffer[OFFSET_LNH] * 256 + buffer[OFFSET_LNL]);

		byte[] payload = null;
		if (length > 0) {
			payload = new byte[length];
			readFully(inStream, payload, 0, length);
		}
		return new SerialPacket(messageType, nodeAddress, length, payload);
	}

	// TODO Write javadoc
	public byte[] getBytes() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			this.write(os);
		} catch (IOException e) {
			// This cannot happen since we are just using an in memory stream
		}
		return os.toByteArray();
	}

	// TODO Write javadoc
	public void write(OutputStream outStream) throws IOException {
		outStream.write(messageType);
		outStream.write(length);
		if (length > 0)
			outStream.write(payload, 0, length);
	}

	public byte getMessageType() {
		return messageType;
	}

	public byte getNodeAddress() {
		return nodeAddress;
	}

	public short getLength() {
		return length;
	}

	public byte[] getPayload() {
		return payload;
	}

	public String toString() {
		return "MTY[" + messageType + "] NAD[" + nodeAddress + "] LN[" + length
				+ "] PY[" + payload + "]";
	}

	/**
	 * Try to read as long and as often until we get all the bytes requested by
	 * the length
	 * 
	 * @param inStream
	 *            The stream to be read from
	 * @param output
	 *            The array where to be written
	 * @param offset
	 *            An offset in the stream and output array
	 * @param length
	 *            The number of bytes to be read
	 * @throws IOException
	 *             if and only if java.io.InputStream.read
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