package at.ac.tuwien.mnsa.ue1.nokiaterminalprovider;

import java.util.List;
import java.util.ListIterator;

import javax.smartcardio.Card;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;

@SuppressWarnings("restriction")
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		List<CardTerminal> l = null;
		Card card = null;

		TerminalFactory tf = TerminalFactory.getInstance("NokiaProvider", null);


		CardTerminals cts = tf.terminals();
		List<CardTerminal> ctl = cts.list();

		System.out.println("List of NokiaTerminals connected:");
		ListIterator<CardTerminal> i = ctl.listIterator();

		while (i.hasNext()) {
			CardTerminal ct = i.next();
			
			System.out.println("Reader: " + (ct).getName());
			
			// don't care about the protocol (either T=0 or T=1)
			card = ct.connect("*");
		}
		System.out.println("ATR: " + card.getATR());
	}

}
