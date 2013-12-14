package at.ac.tuwien.mnsa.ue1.sc;

import java.io.IOException;
import java.io.OutputStream;

import javax.microedition.contactless.*;
import javax.microedition.contactless.sc.ISO14443Connection;
import javax.microedition.io.Connector;

import at.ac.tuwien.mnsa.ue1.proxymidlet.ProxyMIDlet;

//
// Example class of how to use SmartCardConnection in JSR 257 Contactless
// Communication API
//
public class TestSmartCard implements TargetListener, TransactionListener {

	private OutputStream os = null;

	public TestSmartCard(OutputStream os) {
		try {
			this.os = os;
			DiscoveryManager dm = DiscoveryManager.getInstance();
			dm.addTargetListener(this, TargetType.ISO14443_CARD);
			dm.addTransactionListener(this);
		} catch (ContactlessException ce) {
		}
	}

	// Smart card target has been found (reader mode)
	public void targetDetected(TargetProperties[] properties) {
		String targetUID = "";
		TargetProperties target = properties[0];
		Class[] classes = target.getConnectionNames();

		for (int i = 0; i < classes.length; i++) {
			try {
				targetUID = target.getUid();
				ProxyMIDlet.textbox.setString(ProxyMIDlet.textbox.getString() + targetUID + "\n");
				os.write(targetUID.getBytes("UTF-8"));
				os.flush();
				if (classes[i]
						.equals(Class
								.forName("javax.microedition.contactless.sc.ISO14443Connection"))) {
					String url = target.getUrl(classes[i]);
					// Open connection to external smart card
					ISO14443Connection smc = (ISO14443Connection) Connector
							.open(url);
					// Generate command
					// Send command to smart card
					byte[] response = smc
							.exchangeData(new byte[] { (byte) 0x00,
									(byte) 0xA4, (byte) 0x04, (byte) 0x00 });
					// handle response
					ProxyMIDlet.textbox.setString(ProxyMIDlet.textbox.getString() + bytesToHex(response) + "\n");
					os.write(" --- ".getBytes("UTF-8"));
					os.flush();
					os.write(bytesToHex(response).getBytes("UTF-8"));
					os.flush();
					os.write("\n".getBytes("UTF-8"));
					os.flush();
				}
			} catch (ClassNotFoundException e) {
				// handle exception
			} catch (IOException e) {
				// handle exception
			} catch (ContactlessException ce) {
				// handle exception
			}
		}
	}

	// External reader has been detected (tag emulation mode)
	public void externalReaderDetected(byte slot) {
		// update the UI based on the application ID received
	}
	
	
	// From http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	
	private static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
}