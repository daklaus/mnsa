package at.ac.tuwien.mnsa.ue1.client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.List;
import java.util.Properties;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

import at.ac.tuwien.mnsa.ue1.nokiaterminalprovider.NokiaProvider;
import at.ac.tuwien.mnsa.ue1.properties.PropertiesServiceFactory;

@SuppressWarnings("restriction")
public class TerminalClient {

	private static Properties prop;

	public static void main(String[] args) throws IOException,
			NoSuchAlgorithmException, CardException {
		Security.addProvider(new NokiaProvider());

		prop = PropertiesServiceFactory.getPropertiesService().getProperties();

		System.out.println("List of NokiaCardTerminals connected:");

		TerminalFactory tf = TerminalFactory.getInstance("NokiaProvider", prop);
		CardTerminals cts = tf.terminals();
		List<CardTerminal> ctl = cts.list();

		Card card = null;

		for (CardTerminal cardTerminal : ctl) {
			System.out.println("Reader: " + cardTerminal.getName());

			// don't care about the protocol (either T=0 or T=1)
			card = cardTerminal.connect("*");

			// System.out
			// .println("isCardPresent: " + cardTerminal.isCardPresent());
			System.out.println("ATR: " + card.getATR());

			System.out.println("Sending SELECT 0x00A40400");
			CardChannel cc = card.getBasicChannel();
			ResponseAPDU rApdu = cc.transmit(new CommandAPDU(0x00, 0xA4, 0x04,
					0x00));
			if (rApdu == null) {
				System.out.println("No response APDU");
			} else {
				System.out.println(String.format("SW: 0x%04X", rApdu.getSW()));
			}
		}

		card.disconnect(false);
	}

}
