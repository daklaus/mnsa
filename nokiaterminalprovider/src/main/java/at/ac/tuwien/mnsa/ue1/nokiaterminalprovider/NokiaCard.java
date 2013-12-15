package at.ac.tuwien.mnsa.ue1.nokiaterminalprovider;

import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.mnsa.ue1.protocol.SerialPacket;
import at.ac.tuwien.mnsa.ue1.protocol.TooLongPayloadException;

/**
 * Card implementation class.
 */
@SuppressWarnings("restriction")
public class NokiaCard extends Card {
	private static Logger LOG = LoggerFactory.getLogger(NokiaCard.class);

	// default protocol
	private static final String T0_PROTOCOL = "T=0";
	// default ATR - NXP JCOP 31/36K
	private static final String DEFAULT_ATR = "3BFA1800008131FE454A434F5033315632333298";

	// ATR
	private ATR atr;

	private final NokiaCardChannel basicChannel;

	private final SerialPort serialPort;

	public NokiaCard(SerialPort serialPort) {
		this.serialPort = serialPort;
		atr = new ATR(DEFAULT_ATR.getBytes());
		basicChannel = new NokiaCardChannel(this, 0);
	}

	/**
	 * Returns ATR configured by system property
	 */
	@Override
	public ATR getATR() {

		SerialPacket requestPacket = null;
		try {
			requestPacket = new SerialPacket(SerialPacket.TYPE_ATR,
					SerialPacket.DEFAULT_NAD);
		} catch (TooLongPayloadException e) {
		}
		try {
			OutputStream os = serialPort.getOutputStream();
			InputStream is = serialPort.getInputStream();
			requestPacket.write(os);
			os.flush();

			SerialPacket responsePacket = SerialPacket.readFromStream(is);

			byte[] payload = responsePacket.getPayload();
			if (payload == null || responsePacket.getLength() == 0)
				return null;

			atr = new ATR(responsePacket.getPayloadAsString().getBytes());
			return atr;
		} catch (IOException e) {
			LOG.error("In method getATR: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Always returns T=0.
	 */
	@Override
	public String getProtocol() {
		return T0_PROTOCOL;
	}

	@Override
	public CardChannel getBasicChannel() {
		return basicChannel;
	}

	/**
	 * Always returns basic channel with id = 0
	 * 
	 * @throws CardException
	 */
	@Override
	public CardChannel openLogicalChannel() throws CardException {
		return basicChannel;
	}

	/**
	 * Do nothing.
	 */
	@Override
	public void beginExclusive() throws CardException {
	}

	/**
	 * Do nothing.
	 */
	@Override
	public void endExclusive() throws CardException {
	}

	@Override
	public byte[] transmitControlCommand(int i, byte[] bytes)
			throws CardException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 * Do nothing.
	 */
	@Override
	public void disconnect(boolean bln) throws CardException {
		serialPort.close();
	}

	public ResponseAPDU transmitCommand(CommandAPDU capdu) throws CardException {

		if (capdu == null)
			throw new IllegalArgumentException(
					"The CommandAPDU must not be null");

		SerialPacket requestPacket = null;
		try {
			requestPacket = new SerialPacket(SerialPacket.TYPE_APDU,
					SerialPacket.DEFAULT_NAD, capdu.getBytes());
		} catch (TooLongPayloadException e) {
			throw new CardException(e);
		}

		try {
			OutputStream os = serialPort.getOutputStream();
			InputStream is = serialPort.getInputStream();
			requestPacket.write(os);
			os.flush();

			SerialPacket responsePacket = SerialPacket.readFromStream(is);

			byte[] payload = responsePacket.getPayload();
			if (payload == null || responsePacket.getLength() == 0)
				return null;

			return new ResponseAPDU(responsePacket.getPayload());
		} catch (IOException e) {
			LOG.error("In method transmitCommand: " + e.getMessage());
		}
		return null;
	}

}
