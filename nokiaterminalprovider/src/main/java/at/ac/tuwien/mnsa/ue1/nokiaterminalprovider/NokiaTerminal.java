package at.ac.tuwien.mnsa.ue1.nokiaterminalprovider;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

/**
 * CardTerminal implementation class.
 */
@SuppressWarnings("restriction")
public class NokiaTerminal extends CardTerminal {

    public final static String NAME="NokiaTerminal.Terminal";
    private static NokiaCard card = null;
            
    public String getName() {
        return NAME;
    }

	public Card connect(String string) throws CardException {
        if (card == null) {
            card = new NokiaCard();
        }
        return card;
    }

    /**
     * Always returns true
     */
    public boolean isCardPresent() throws CardException {
        return true;
    }

    /**
     * Immediately returns true
     */
    public boolean waitForCardPresent(long l) throws CardException {
        return true;
    }

    /**
     * Immediately returns true
     */
    public boolean waitForCardAbsent(long l) throws CardException {
        return false;
    }
}
