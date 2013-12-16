package at.ac.tuwien.mnsa.ue1.nokiaterminalprovider;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("restriction")
public class NokiaCardTerminals extends CardTerminals {

	private static final Logger LOG = LoggerFactory
			.getLogger(NokiaCardTerminals.class);

	private final List<CardTerminal> terminals;
	private final Properties properties;

	protected NokiaCardTerminals(Properties prop) {
		super();

		this.properties = prop;
		terminals = new LinkedList<>();
		terminals.add(new NokiaCardTerminal(prop));
	}

	/**
	 * Returns only one terminal with state ALL|CARD_PRESENT|CARD_INSERTION, in
	 * other case returns empty list.
	 */
	@Override
	public List<CardTerminal> list(State state) throws CardException {
		switch (state) {
		case ALL:
			return terminals;
			// TODO Implement the other cases as well
		case CARD_PRESENT:
		case CARD_INSERTION:
			// terminals.add(new NokiaCardTerminal(comPort));
		case CARD_ABSENT:
		case CARD_REMOVAL:
		default:
			return new LinkedList<CardTerminal>();
		}
	}

	/**
	 * Immediately returns true
	 */
	@Override
	public boolean waitForChange(long l) throws CardException {
		// TODO Implement

		return true;
	}
}
