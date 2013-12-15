package at.ac.tuwien.mnsa.ue1.nokiaterminalprovider;

import static org.junit.Assert.*;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

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

import at.ac.tuwien.mnsa.ue1.properties.PropertiesService;
import at.ac.tuwien.mnsa.ue1.properties.PropertiesServiceFactory;
import at.ac.tuwien.mnsa.ue1.properties.USBConnectionPropertiesService;
import at.ac.tuwien.mnsa.ue1.protocol.SerialPacket;

@SuppressWarnings("restriction")
public class NokiaProviderTest {

	private static Properties prop;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Security.addProvider(new NokiaProvider());

		prop = PropertiesServiceFactory.getPropertiesService().getProperties();
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

	// Moved to client module
	//
	// @Test
	// public void testNokiaProvider() throws NoSuchAlgorithmException,
	// NoSuchProviderException, CardException {
	//
	// System.out.println("List of NokiaCardTerminals connected:");
	//
	// TerminalFactory tf = TerminalFactory.getInstance("NokiaProvider", prop);
	// CardTerminals cts = tf.terminals();
	// List<CardTerminal> ctl = cts.list();
	//
	// Card card = null;
	//
	// for (CardTerminal cardTerminal : ctl) {
	// System.out.println("Reader: " + cardTerminal.getName());
	//
	// // don't care about the protocol (either T=0 or T=1)
	// card = cardTerminal.connect("*");
	// }
	// System.out.println("ATR: " + card.getATR());
	//
	// }

}
