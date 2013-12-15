package at.ac.tuwien.mnsa.ue1.client;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import at.ac.tuwien.mnsa.ue1.properties.PropertiesServiceFactory;
import at.ac.tuwien.mnsa.ue1.properties.USBConnectionPropertiesService;
import at.ac.tuwien.mnsa.ue1.protocol.SerialPacket;
import at.ac.tuwien.mnsa.ue1.protocol.TooLongPayloadException;

public class ProxyMIDletTest {

	private static SerialPort serialPort;
	private static InputStream inputStream;
	private static OutputStream outputStream;
	private static final int CONNECTION_TIMEOUT = 100;

	public static void main(String[] args) throws IOException,
			PortInUseException, NoSuchPortException,
			UnsupportedCommOperationException {

		Properties prop = PropertiesServiceFactory.getPropertiesService()
				.getProperties();
		String comPort = prop
				.getProperty(USBConnectionPropertiesService.PORT_KEY);

		serialPort = (SerialPort) CommPortIdentifier.getPortIdentifier(comPort)
				.open("Test Terminal", CONNECTION_TIMEOUT);
		serialPort.enableReceiveTimeout(CONNECTION_TIMEOUT);

		inputStream = serialPort.getInputStream();
		outputStream = serialPort.getOutputStream();

		SerialPacket expectedP = null;
		try {
			expectedP = new SerialPacket(SerialPacket.TYPE_APDU,
					SerialPacket.DEFAULT_NAD);

			expectedP.write(outputStream);

			SerialPacket actualP = SerialPacket.readFromStream(inputStream);

		} catch (TooLongPayloadException e) {
		}

		serialPort.close();
	}

}
