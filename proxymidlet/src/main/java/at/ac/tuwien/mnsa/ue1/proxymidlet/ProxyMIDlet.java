package at.ac.tuwien.mnsa.ue1.proxymidlet;

import javax.microedition.contactless.ContactlessException;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextBox;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import at.ac.tuwien.mnsa.ue1.proxymidlet.conn.ISO14443Conn;
import at.ac.tuwien.mnsa.ue1.proxymidlet.conn.SerialConn;

public class ProxyMIDlet extends MIDlet implements CommandListener {

	private Command exitCommand;
	private Display display;
	private TextBox textbox;
	private Form form;
	private ISO14443Conn nfcConnection;
	private SerialConn serialConnection;
	private Thread serialThread;
	private static final Logger LOG = Logger.getLogger("MainMidlet");

	public ProxyMIDlet() {
		display = Display.getDisplay(this);
		exitCommand = new Command("Exit", Command.EXIT, 1);
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		if (serialConnection != null)
			serialConnection.close();
		if (serialThread != null) {
			if (serialThread.isAlive())
				serialThread.interrupt();
		}

		if (nfcConnection != null)
			nfcConnection.closeConnection();

	}

	protected void pauseApp() {
	}

	protected void startApp() throws MIDletStateChangeException {
		try {
			// textbox = new TextBox("NFC-Midlet", "", 8000, 0);
			// textbox.addCommand(exitCommand);
			// textbox.setCommandListener(this);
			// display.setCurrent(textbox);
			
			form = new Form("Cardterminal Form");
			Display.getDisplay(this).setCurrent(form);
			Logger.init(form);
			form.addCommand(exitCommand);

			form.setCommandListener(new CommandListener() {
				public void commandAction(Command c, Displayable d) {
					if (c == exitCommand) {
						LOG.print("Exiting...");
						notifyDestroyed();
					}
				}
			});

			// Start NFC Connection
			nfcConnection = new ISO14443Conn();
			LOG.print("Started NFC connection");

			// Start NFC Card Listening
			serialConnection = new SerialConn();
			serialThread = new Thread(serialConnection);
			serialThread.start();
			LOG.print("Started USB");

		} catch (ContactlessException e) {
		}
	}

	public void commandAction(Command arg0, Displayable arg1) {
		if (arg0 == exitCommand) {
			notifyDestroyed();
		}
	}
}