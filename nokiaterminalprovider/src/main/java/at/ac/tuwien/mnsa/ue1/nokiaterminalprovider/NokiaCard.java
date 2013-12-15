package at.ac.tuwien.mnsa.ue1.nokiaterminalprovider;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

/**
 * Card implementation class.
 */
@SuppressWarnings("restriction")
public class NokiaCard extends Card {
	// default protocol
	private static final String T0_PROTOCOL = "T=0";
	// default ATR - NXP JCOP 31/36K
	private static final String DEFAULT_ATR = "3BFA1800008131FE454A434F5033315632333298";

	// ATR
	private ATR atr;

	private NokiaChannel basicChannel;

	public NokiaCard() {
		atr = new ATR(DEFAULT_ATR.getBytes());
		basicChannel = new NokiaChannel(this, 0);
	}

	/**
	 * Returns ATR configured by system property
	 */
	@Override
	public ATR getATR() {
		return atr;
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
	}

	public ResponseAPDU transmitCommand(CommandAPDU capdu) {

		System.out.println("Hier kommt ein Transmit Command");
		return null;
	}

}
