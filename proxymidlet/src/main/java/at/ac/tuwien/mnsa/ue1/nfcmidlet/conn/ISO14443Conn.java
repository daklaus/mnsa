package at.ac.tuwien.mnsa.ue1.nfcmidlet.conn;

import java.io.IOException;
import javax.microedition.contactless.ContactlessException;
import javax.microedition.contactless.DiscoveryManager;
import javax.microedition.contactless.TargetListener;
import javax.microedition.contactless.TargetProperties;
import javax.microedition.contactless.TargetType;
import javax.microedition.contactless.sc.ISO14443Connection;
import javax.microedition.io.Connector;

public class ISO14443Conn implements TargetListener {

	private ISO14443Connection smc;
	private TargetProperties target;

	public ISO14443Conn() throws ContactlessException {
		DiscoveryManager dm = DiscoveryManager.getInstance();
		dm.addTargetListener(this, TargetType.ISO14443_CARD);
		// dm.addTransactionListener(this);
	}

	public void targetDetected(TargetProperties[] arg0) {
		if (arg0.length > 0)
			target = arg0[0];
	}

	public String getAtr() {
		if (target != null)
			return target.getUid();
		else
			return null;
	}

	// Some parts from Nokia (jsr-257-spec-1.0.pdf)
	public byte[] sendAPDU(byte[] payload) {
		if (target == null) {
			return null;
		}

		if (smc == null) {
			Class[] classes = target.getConnectionNames();

			try {
				for (int i = 0; i < classes.length; i++) {

					if (classes[i]
							.equals(Class
									.forName("javax.microedition.contactless.sc.ISO14443Connection"))) {
						String url = target.getUrl(classes[i]);
						smc = (ISO14443Connection) Connector.open(url);
					}

				}

				// Send command to smart card and return response
				return smc.exchangeData(payload);

			} catch (ClassNotFoundException e) {
			} catch (IOException e) {
				closeConnection();
			} catch (ContactlessException e) {
				closeConnection();
			}
		}

		return null;
	}

	public void closeConnection() {
		if (smc != null) {
			try {
				smc.close();

			} catch (IOException e) {
			}
			// smc = null;
		}
	}
}
