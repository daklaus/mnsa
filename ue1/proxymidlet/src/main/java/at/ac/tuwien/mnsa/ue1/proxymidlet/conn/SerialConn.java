package at.ac.tuwien.mnsa.ue1.proxymidlet.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.contactless.ContactlessException;
import javax.microedition.io.CommConnection;
import javax.microedition.io.Connector;

import at.ac.tuwien.mnsa.ue1.protocol.SerialPacket;
import at.ac.tuwien.mnsa.ue1.protocol.TooLongPayloadException;
import at.ac.tuwien.mnsa.ue1.proxymidlet.Logger;

public class SerialConn implements Runnable {

	private boolean running = true;
	private ISO14443Conn nfcConnection;
	private CommConnection USBConnection;
	private InputStream inStream;
	private OutputStream outStream;
	private static final Logger LOG = Logger.getLogger("SerialConnection");

	public SerialConn(ISO14443Conn nfcConn) {
		nfcConnection = nfcConn;
	}

	public void run() {

		LOG.print("Got into Serial Thread");

		try {
			LOG.print("Initializing Connections...");
			USBConnection = (CommConnection) Connector.open("comm:USB1");
			inStream = USBConnection.openInputStream();
			outStream = USBConnection.openOutputStream();
		} catch (IOException e) {
			LOG.print("Error: Initializing Connections");
			running = false;
		}

		LOG.print("Connections successfully initialized!");

		running = true;
		while (running) {
			SerialPacket packet = null;
			SerialPacket responsePacket = null;
			byte[] responsePayload;

			try {
				LOG.print("whileloop: Waiting for stream...");
				packet = SerialPacket.readFromStream(inStream);
				LOG.print("whileloop: saved stream");

			} catch (IOException e) {
				LOG.print("Error: IOException while reading SerialStream");
				close();
			}

			byte packetType = packet.getMessageType();
			LOG.print("whileloop: got packettype...");

			switch (packetType) {

			case SerialPacket.TYPE_WAIT:
				// TODO
				break;

			case SerialPacket.TYPE_APDU:
				try {
					LOG.print("Switch: Got into ADPU");
					responsePayload = nfcConnection.sendAPDU(packet
							.getPayload());
					LOG.print("Switch: ADPU - got response from nfc card");
					if (responsePayload != null) {
						LOG.print("Switch: ADPU - if responsepacket != null");
						responsePacket = new SerialPacket(
								SerialPacket.TYPE_APDU,
								SerialPacket.DEFAULT_NAD, responsePayload);
						LOG.print("value: " + SerialPacket.bytesToHex(responsePayload));
					} else {
						LOG.print("Switch: No response APDU, sending ERROR");
						responsePacket = new SerialPacket(
								SerialPacket.TYPE_ERROR,
								SerialPacket.DEFAULT_NAD);
					}
					LOG.print("Switch: Before Sending");
				} catch (TooLongPayloadException e1) {
					LOG.print("Switch: TooLongPayloadException");
				} catch (ContactlessException e) {
					LOG.print("Switch: ContactlessException");
				}
				// TODO
				break;

			case SerialPacket.TYPE_DEBUGINFO:
				// TODO
				break;

			case SerialPacket.TYPE_ERROR:
				// TODO
				break;

			case SerialPacket.TYPE_INFOTEXT:
				// TODO
				break;

			case SerialPacket.TYPE_INIT:
				// TODO
				break;

			case SerialPacket.TYPE_STATUS:
				try {
					LOG.print("Switch: Got into Status");
					responsePayload = nfcConnection
							.sendAPDU(new byte[] { (byte) 0x00, (byte) 0xA4,
									(byte) 0x04, (byte) 0x00 });
					LOG.print("Switch: Status - got response from nfc card");
					if (responsePayload != null) {
						LOG.print("Switch: Status - if responsepacket != null");
						responsePacket = new SerialPacket(
								SerialPacket.TYPE_STATUS,
								SerialPacket.DEFAULT_NAD,
								new byte[] { (byte) 0x01 });
						LOG.print("status value: "
								+ new String(responsePayload));
					} else {
						LOG.print("Switch: ADPU - else responsepacket...");
						responsePacket = new SerialPacket(
								SerialPacket.TYPE_ERROR,
								SerialPacket.DEFAULT_NAD);
					}
					LOG.print("Switch: Before Sending");
				} catch (TooLongPayloadException e1) {
					LOG.print("Switch: ADPU - Error");
				} catch (ContactlessException e) {
					try {
						responsePacket = new SerialPacket(
								SerialPacket.TYPE_STATUS,
								SerialPacket.DEFAULT_NAD,
								new byte[] { (byte) 0x00 });
					} catch (TooLongPayloadException e1) {
					}
				}
				// TODO
				break;

			case SerialPacket.TYPE_ATR:
				try {
					LOG.print("Switch: Got type ATR");

					String atr = nfcConnection.getAtr();
					LOG.print("Switch: Status - got response from nfc card");

					if (atr != null && atr.length() > 0) {
						LOG.print("Switch: Status - if responsepacket != null");
						responsePacket = new SerialPacket(
								SerialPacket.TYPE_ATR,
								SerialPacket.DEFAULT_NAD, atr);
						LOG.print("returning ATR value: " + atr);
					} else {
						LOG.print("Switch: ATR is null or empty");
						responsePacket = new SerialPacket(
								SerialPacket.TYPE_ERROR,
								SerialPacket.DEFAULT_NAD);
					}
					LOG.print("Switch: Before Sending");
				} catch (TooLongPayloadException e1) {
					LOG.print("Switch: TooLongPayloadException with ATR");
				}
				// TODO
				break;

			case SerialPacket.TYPE_TERMINFO:
				// TODO
				break;

			default:
				break;
			}

			try {
				if (responsePacket == null) {
					try {
						responsePacket = new SerialPacket(
								SerialPacket.TYPE_ERROR,
								SerialPacket.DEFAULT_NAD);
					} catch (TooLongPayloadException e) {
						// Cannot happen since there is no payload
					}
				}

				LOG.print("Sending Packet...");
				responsePacket.write(outStream);
				outStream.flush();
			} catch (IOException e) {
				LOG.print("Error: Sending packet");
				close();
			}
		}
	}

	public void close() {
		try {
			LOG.print("Closing connections...");
			running = false;
			if (outStream != null)
				outStream.close();
			if (inStream != null)
				inStream.close();
			if (USBConnection != null)
				USBConnection.close();
		} catch (IOException e) {
			LOG.print("Error: Closing Connections");
		}
	}
}