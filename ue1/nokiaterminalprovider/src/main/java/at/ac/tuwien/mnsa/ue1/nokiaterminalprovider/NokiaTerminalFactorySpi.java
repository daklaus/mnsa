package at.ac.tuwien.mnsa.ue1.nokiaterminalprovider;

import java.util.Properties;

import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactorySpi;

@SuppressWarnings("restriction")
public class NokiaTerminalFactorySpi extends TerminalFactorySpi {

	private final CardTerminals cardTerminals;

	public NokiaTerminalFactorySpi() {
		this(null);
	}

	public NokiaTerminalFactorySpi(Object parameter) {
		Properties prop = null;
		if (parameter instanceof Properties) {
			prop = (Properties) parameter;
		} else {
			throw new RuntimeException(
					"The parameter of the NokiaTerminalFactorySpi has to be of type java.util.Properties");
		}

		cardTerminals = new NokiaCardTerminals(prop);
	}

	@Override
	protected CardTerminals engineTerminals() {
		return cardTerminals;
	}

}