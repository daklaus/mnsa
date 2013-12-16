package at.ac.tuwien.mnsa.ue1.nokiaterminalprovider;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.mnsa.ue1.properties.USBConnectionPropertiesService;
import at.ac.tuwien.mnsa.ue1.protocol.SerialPacket;
import at.ac.tuwien.mnsa.ue1.protocol.TooLongPayloadException;

/**
 * CardTerminal implementation class.
 */
@SuppressWarnings("restriction")
public class NokiaCardTerminal extends CardTerminal {

	private static Logger LOG = LoggerFactory
			.getLogger(NokiaCardTerminal.class);

	public final static String NAME = "NokiaTerminal.Terminal";
	public static final int CONNECTION_TIMEOUT = 100;
	public static final byte nodeAddress = SerialPacket.DEFAULT_NAD;

	private static NokiaCard card = null;
	private final String comPort;
	private SerialPort serialPort;

	NokiaCardTerminal(Properties prop) {
		this.comPort = prop
				.getProperty(USBConnectionPropertiesService.PORT_KEY);

		if (comPort == null || comPort.isEmpty())
			throw new IllegalArgumentException("");
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Card connect(String protocol) throws CardException {
		if (card == null) {
			try {
				serialPort = (SerialPort) CommPortIdentifier.getPortIdentifier(
						comPort).open(NAME, CONNECTION_TIMEOUT);
			} catch (PortInUseException | NoSuchPortException e) {
				throw new CardException("The port " + comPort
						+ " is either in use or does not exist", e);
			}

			try {
				serialPort.enableReceiveTimeout(CONNECTION_TIMEOUT);
			} catch (UnsupportedCommOperationException e) {
				// If timeout cannot be set, don't bother
			}

			card = new NokiaCard(serialPort);
		}

		return card;
	}

	/**
	 * Always returns true
	 */
	@Override
	public boolean isCardPresent() throws CardException {
		SerialPacket sp = null;
		try {
			sp = new SerialPacket(SerialPacket.TYPE_STATUS, nodeAddress);
		} catch (TooLongPayloadException e) {
		}

		try {
			OutputStream os = serialPort.getOutputStream();
			InputStream is = serialPort.getInputStream();
			sp.write(os);
			os.flush();

			SerialPacket responsePacket = SerialPacket.readFromStream(is);

			// Don't bother about the node address, we don't use it since we
			// just have a one-to-one connection

			byte[] payload = responsePacket.getPayload();

			if (responsePacket.getMessageType() != SerialPacket.TYPE_STATUS)
				return false;

			return payload != null && responsePacket.getLength() == 1
					&& payload[0] == 1;

		} catch (IOException e) {
			LOG.error("In method isCardPresent: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Immediately returns true
	 */
	@Override
	public boolean waitForCardPresent(long l) throws CardException {
		return true;
	}

	/**
	 * Immediately returns true
	 */
	@Override
	public boolean waitForCardAbsent(long l) throws CardException {
		return false;
	}
}
