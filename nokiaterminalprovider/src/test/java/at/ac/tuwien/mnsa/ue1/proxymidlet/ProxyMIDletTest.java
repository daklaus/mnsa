package at.ac.tuwien.mnsa.ue1.proxymidlet;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import at.ac.tuwien.mnsa.ue1.properties.PropertiesServiceFactory;
import at.ac.tuwien.mnsa.ue1.properties.USBConnectionPropertiesService;
import at.ac.tuwien.mnsa.ue1.protocol.SerialPacket;
import at.ac.tuwien.mnsa.ue1.protocol.TooLongPayloadException;

public class ProxyMIDletTest {

	private static SerialPort serialPort;
	private static InputStream inputStream;
	private static OutputStream outputStream;
	private static final int CONNECTION_TIMEOUT = 1000;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		Properties prop = PropertiesServiceFactory.getPropertiesService()
				.getProperties();
		String comPort = prop
				.getProperty(USBConnectionPropertiesService.PORT_KEY);

		serialPort = (SerialPort) CommPortIdentifier.getPortIdentifier(comPort)
				.open("Test Terminal", CONNECTION_TIMEOUT);
		serialPort.enableReceiveTimeout(CONNECTION_TIMEOUT);

		inputStream = serialPort.getInputStream();
		outputStream = serialPort.getOutputStream();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		serialPort.close();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEcho() throws IOException {
		SerialPacket expectedP = null;
		try {
			expectedP = new SerialPacket(SerialPacket.TYPE_APDU,
					SerialPacket.DEFAULT_NAD, new byte[] { (byte) 0x00,
							(byte) 0xA4, (byte) 0x04, (byte) 0x00 });

			expectedP.write(outputStream);

			SerialPacket actualP = SerialPacket.readFromStream(inputStream);
			System.out.println("Got Payload: " + actualP.getPayloadAsString());

		} catch (TooLongPayloadException e) {
		}
	}
}
