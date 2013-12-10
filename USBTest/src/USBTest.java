import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.io.CommConnection;
import javax.microedition.io.Connector;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * MIDlet reads System properties Comm ports, opens CommConnection to port COM0
 * attempts to get and set Baud rate, writes text string over CommConnection
 * then listens and displays bytes received from CommConnection.
 */
public class USBTest extends MIDlet implements CommandListener {
    private Command startCommand;
    private Command exitCommand;
    private Display display;
    private TextBox textbox;
    private CommConnection comm = null;
    private InputStream is = null;
    private OutputStream os = null;
    private Thread thread;
    private Tracer tracer;

    public USBTest() {
        display = Display.getDisplay(this);
        startCommand = new Command ("Start", Command.SCREEN, 1);
        exitCommand = new Command("Exit", Command.EXIT, 1);
    }

    /**
     * Start up the MIDlet by creating the TextBox and associating
     * the exit command and listener.
     */
    public void startApp() {
        textbox = new TextBox("USBTest", "", 8000, 0);
        textbox.addCommand(startCommand);
        textbox.addCommand(exitCommand);
        textbox.setCommandListener(this);
        display.setCurrent(textbox);
        tracer = new Tracer(textbox);
        openUSBConnection();
    }

    public void pauseApp() { }

    /**
     * Destroy must cleanup everything not handled by the garbage collector.
     */
    public void destroyApp(boolean unconditional) { }

    /*
     * Respond to commands, including exit
     * On the exit command, cleanup and notify that the MIDlet has been destroyed.
     */
    public void commandAction(Command c, Displayable s) {
        if (c == startCommand) {
            thread = new Thread() {
                public void run() {
                    listenUSB();
                }
            };
            thread.start();
        }
        if (c == exitCommand) {
            tracer.outln("Exiting...");
            notifyDestroyed();
        }
    }

    public void openUSBConnection() {
        try {
            tracer.outln("USBTest: Testing CommConnection for USB ports");
            tracer.outln("Calling System.getProperty(microedition.commports)");
            String ports = System.getProperty("microedition.commports");
            tracer.outln("got ports="+ports);
            int index = ports.indexOf("USB", 0);
            if(index == -1) {
                throw new RuntimeException("No USB port found in the device");
            }
            tracer.outln("Going to call Connector.open(comm:USB1)");
            comm = (CommConnection)Connector.open("comm:USB1");
            int orgBaudRate = comm.getBaudRate();
            tracer.outln("Calling getBaudRate(): " + orgBaudRate);
            tracer.outln("Calling openOutputStream()");
            os = comm.openOutputStream();
            tracer.outln("writing to USB1");
            String text = "Hello from USBTest MIDlet! Anybody there at USB?\r\n";
            os.write(text.getBytes());
            os.flush();
            tracer.outln("Calling openInputStream()");
            is = comm.openInputStream();
            tracer.outln("attempting read up to 500 bytes from USB1 (quits when received string:  EXIT and <enter>)\n");
        } catch (IOException e) {
            tracer.outln("IOException: " + e.getMessage());
            return;
        }
    }

    public void listenUSB() {
        tracer.outln("Listening USB port...");
        try {
            byte[] buffer = new byte[500];
            StringBuffer message = new StringBuffer();
            for(int i = 0; i < 500;) {
                try {
                    Thread.sleep(100);
                }
                catch(InterruptedException ie) { }
                int available = is.available();
                if(available == 0) {
                    continue;
                }
                String outText = "";
                int count = is.read(buffer, i, available);
                if(count > 0) {
                    outText = new String(buffer, i, count);
                    i = i + count;
                    message.append(outText);
                    if (outText.endsWith("\n")) {
                        String messageString = message.toString();
                        tracer.outln("Message: " + messageString);
                        message.delete(0, message.length());
                    }
                }
                String total = new String(buffer,0,i);
                if ((i > 3) && (-1 != total.indexOf("EXIT\r\n"))) {
                    tracer.outln("Closing...");
                    break;
                }
            }
            tracer.outln("Calling OutputStream.close()");
            os.close();
            tracer.outln("Calling InputStream.close()");
            is.close();
            tracer.outln("Calling connection.close()");
            comm.close();
        }
        catch (IOException ioe) {
            tracer.outln("IOException: " + ioe.getMessage());
        }
        tracer.outln("SUCCEEDED.");
    }
}

    class Tracer {
        private static TextBox myTextBox;
        public Tracer (TextBox t) {
            myTextBox = t;
        }

        public void outln (String msg) {
        	myTextBox.setString((myTextBox.getString()).concat(msg + "\n"));
        }

        public void out (String msg, TextBox t) {
        	myTextBox.setString((myTextBox.getString()).concat(msg));
        }
    }
