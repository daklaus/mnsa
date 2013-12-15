package at.ac.tuwien.mnsa.ue1.proxymidlet;

import javax.microedition.contactless.ContactlessException;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import at.ac.tuwien.mnsa.ue1.nfcmidlet.conn.ISO14443Conn;
import at.ac.tuwien.mnsa.ue1.nfcmidlet.conn.SerialConn;

public class MainMidlet extends MIDlet implements CommandListener {

	private Command exitCommand;
	private Display display;
	private TextBox textbox;
	private ISO14443Conn nfcConnection;
	private SerialConn serialConnection;
	private Thread serialThread;

	public MainMidlet() {
		display = Display.getDisplay(this);
		exitCommand = new Command("Exit", Command.EXIT, 1);
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO

		if (nfcConnection != null)
			nfcConnection.closeConnection();

	}

	protected void pauseApp() {
	}

	protected void startApp() throws MIDletStateChangeException {
		try {
			textbox = new TextBox("NFC-Midlet", "", 8000, 0);
			textbox.addCommand(exitCommand);
			textbox.setCommandListener(this);
			display.setCurrent(textbox);

			// Start NFC Connection
			nfcConnection = new ISO14443Conn();

			// Start NFC Card Listening
			serialConnection = new SerialConn();
			serialThread = new Thread(serialConnection);
			serialThread.start();

		} catch (ContactlessException e) {
		}
	}

	public void commandAction(Command arg0, Displayable arg1) {
		if (arg0 == exitCommand) {
			notifyDestroyed();
		}
	}
}