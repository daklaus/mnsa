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
				ProxyMIDlet.textbox.setString(targetUID);
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
					// byte[] response = smc.exchangeData(commands);
					// handle response
				}
			} catch (ClassNotFoundException e) {
				// handle exception
			} catch (IOException e) {
				// handle exception
				// } catch (ContactlessException ce) {
				// // handle exception
			}
		}
	}

	// External reader has been detected (tag emulation mode)
	public void externalReaderDetected(byte slot) {
		// update the UI based on the application ID received
	}
}