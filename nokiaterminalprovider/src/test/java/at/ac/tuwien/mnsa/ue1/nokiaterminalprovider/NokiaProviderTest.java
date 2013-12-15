package at.ac.tuwien.mnsa.ue1.nokiaterminalprovider;

import static org.junit.Assert.*;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.util.List;
import java.util.ListIterator;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import at.ac.tuwien.mnsa.ue1.protocol.SerialPacket;

@SuppressWarnings("restriction")
public class NokiaProviderTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Security.addProvider(new NokiaProvider());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProviderList() {
		Provider[] x = Security.getProviders();

		for (int i = 0; i < x.length; i++)
			System.out.println("Provider: " + x[i].getName());
	}

	@Test
	public void testNokiaProvider() throws NoSuchAlgorithmException,
			NoSuchProviderException, CardException {

		TerminalFactory tf = TerminalFactory.getInstance("NokiaProvider", null);

		CardTerminals cts = tf.terminals();
		List<CardTerminal> ctl = cts.list();

		System.out.println("List of NokiaTerminals connected:");
		ListIterator<CardTerminal> i = ctl.listIterator();

		Card card = null;
		while (i.hasNext()) {
			CardTerminal ct = i.next();

			System.out.println("Reader: " + (ct).getName());

			// don't care about the protocol (either T=0 or T=1)
			card = ct.connect("*");
		}
		System.out.println("ATR: " + card.getATR());

	}

}
