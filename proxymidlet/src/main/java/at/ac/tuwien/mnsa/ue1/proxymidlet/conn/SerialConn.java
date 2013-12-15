package at.ac.tuwien.mnsa.ue1.proxymidlet.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
	private static final Logger LOG = Logger.getLogger("CardConnection");
	
	public void run() {

		try {
			USBConnection = (CommConnection) Connector.open("comm:USB1");
			inStream = USBConnection.openInputStream();
			outStream = USBConnection.openOutputStream();
		} catch (IOException e) {
			running = false;
		}

		running = true;
		while (running) {
			SerialPacket packet = null;
			SerialPacket responsePacket = null;
			byte[] responsePayload;

			try {
				packet = SerialPacket.readFromStream(inStream);
			} catch (IOException e) {
				close();
			} catch (TooLongPayloadException e) {
				close();
			}
			
			LOG.print("Tests", null);

			byte packetType = packet.getMessageType();

			switch (packetType) {

			case SerialPacket.TYPE_WAIT:
				// TODO
				break;

			case SerialPacket.TYPE_APDU:
				try {
					responsePayload = nfcConnection.sendAPDU(packet
							.getPayload());
					if (responsePayload != null) {
						responsePacket = new SerialPacket(
								SerialPacket.TYPE_APDU,
								SerialPacket.DEFAULT_NAD, responsePayload);
						LOG.print("value: " + new String(responsePayload), null);
					}
					else
						responsePacket = new SerialPacket(
								SerialPacket.TYPE_ERROR,
								SerialPacket.DEFAULT_NAD);
				} catch (TooLongPayloadException e1) {
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
				// TODO
				break;

			case SerialPacket.TYPE_TERMINFO:
				// TODO
				break;

			default:
				break;
			}

			try {
				if (responsePacket != null) {
					responsePacket.write(outStream);
					outStream.flush();
				}
			} catch (IOException e) {
				close();
			}
		}
	}

	public void close() {
		try {
			running = false;
			if (outStream != null)
				outStream.close();
			if (inStream != null)
				inStream.close();
			if (USBConnection != null)
				USBConnection.close();
		} catch (IOException e) {
		}
	}
}