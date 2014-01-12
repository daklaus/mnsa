package at.ac.tuwien.mnsa.ue3.smsapp;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.mnsa.ue3.csv.CsvServiceFactory;
import at.ac.tuwien.mnsa.ue3.csv.SMS;
import at.ac.tuwien.mnsa.ue3.properties.PropertiesServiceFactory;
import at.ac.tuwien.mnsa.ue3.properties.SMSPropertiesService;

public class SMSApp {

	private static final Logger LOG = LoggerFactory.getLogger(SMSApp.class);

	private SerialPort serialPort;

	private BufferedReader reader;

	private PrintWriter writer;

	private static Properties prop;

	private static final int CONNECTION_TIMEOUT = 1000;
	public static final int DELAY_DEFAULT = 0;
	public static final int DELAY_SMS = 10000;
	public static final int DELAY_CALL = 10000;

	public static void main(String[] args) {

		List<SMS> smsList = null;
		try {
			prop = PropertiesServiceFactory.getPropertiesService()
					.getProperties();

			smsList = CsvServiceFactory.getCsvService().getSMSList();

			LOG.info("The following SMS will be sent:");
			for (SMS sms : smsList) {
				LOG.info("Recipient: \"{}\", Message: \"{}\"",
						sms.getRecipient(), sms.getMessage());
			}

		} catch (IOException e) {
			LOG.info(e.getMessage());
			LOG.info("Halting application...");
		}

	}

	private SMSApp() {
		try {
			LOG.info("================================");
			LOG.info("Setting up locale environment...");
			LOG.info("================================");

			LOG.info("Getting properties...");

			prop = PropertiesServiceFactory.getPropertiesService()
					.getProperties();

			String comPort = prop.getProperty(SMSPropertiesService.PORT_KEY);
			String csvFile = prop.getProperty(SMSPropertiesService.CSV_KEY);

			LOG.info("COM-Port: {}", comPort);
			LOG.info("CSV-File: {}", csvFile);

			LOG.info("Opening Serial Port...");
			serialPort = (SerialPort) CommPortIdentifier.getPortIdentifier(
					comPort).open("Test Terminal", CONNECTION_TIMEOUT);
			serialPort.enableReceiveTimeout(CONNECTION_TIMEOUT);

			LOG.info("Setting up I/O communication...");
			reader = new BufferedReader(new InputStreamReader(
					serialPort.getInputStream()));
			writer = new PrintWriter(serialPort.getOutputStream(), true);

			LOG.info("Environment setup done.");

			LOG.info(" ");
			LOG.info("=================================");
			LOG.info("Bringing telephone up to speed...");
			LOG.info("=================================");
			initializeTelephone();

		} catch (Exception e) {
			LOG.error(e.getMessage());

			close();

			LOG.error("Halting application...");

			System.exit(1);
		}
	}

	private static class SMSAppHolder {
		public static final SMSApp INSTANCE = new SMSApp();
	}

	public static SMSApp getInstance() {
		return SMSAppHolder.INSTANCE;
	}

	private void initializeTelephone() throws Exception {
		LOG.info("Resetting telephone...");
		// Doing 3 times, just to be sure... ;) 1 times doesn't work 100%
		resetTelephone();
		resetTelephone();
		resetTelephone();

		LOG.info("Turning off echo of telephone...");
		turnEchoOff();

		LOG.info("Checking for PIN-Code...");
		checkPIN();

		LOG.info("Checking SMSC...");
		checkSMSC();

		LOG.info("Initial telephone setup done. Lookup the LOG, if there are any errors!");
	}

	/**
	 * Frees up resources
	 */
	public void close() {
		if (writer != null)
			writer.close();
		if (writer != null)
			try {
				reader.close();
			} catch (IOException e1) {
			}
		if (serialPort != null)
			serialPort.close();
		prop.clear();
	}

	/**
	 * Checks if the SMS Service Center Address (SMSC) is set. If not, the
	 * telephone number specified in the properties file is used; if not
	 * specified, the SMSC telephone number is typed in by the user
	 * 
	 * @throws Exception
	 */
	public void checkSMSC() throws Exception {
		String answer[];
		String smsc = "";

		answer = sendATCommand("AT+CSCA?", DELAY_DEFAULT);

		LOG.info("Return-Code: {}", answer[1]);
		LOG.info("Telephone sent: {}", answer[0]);

		if (answer[0].contains("+CSCA: \"\",")) {

			// SMSC needs to be set
			LOG.info("SMSC not set; Looking up properties file for SMSC telephone number...");

			// Is a there valid SMSC_KEY available in the properties file?
			if ((prop.getProperty(SMSPropertiesService.SMSC_KEY) != null)
					&& (prop.getProperty(SMSPropertiesService.SMSC_KEY)
							.length() > 0)) {

				// Try to set the SMSC...
				LOG.info("Using the specified SMSC telephone number...");
				if (setSMSC(prop.getProperty(SMSPropertiesService.SMSC_KEY)))
					LOG.info("Succesfully set SMSC telephone number!");
				else {
					LOG.error("Couldn't set the SMSC telephone number! Did you mention the wrong SMSC telephone number?");
					throw new Exception();
				}

			} else {
				LOG.info("No SMSC telephone number specified!");

				// Specify SMSC by the keyboard...
				// Update: Can't get console while unit testing... So skipping
				// this part...

				LOG.info("There is no SMSC telephone number specified in the properties file... Starting keyboard input of the SMSC number...");
				System.out
						.println("Please enter a valid international SMSC telephone number:");
				smsc = new String(System.console().readPassword());

				LOG.info("Sending the specified SMSC to telephone...");
				if (setSMSC(smsc))
					LOG.info("Succesfully set SMSC telephone number!");
				else {
					LOG.error("Couldn't set the SMSC telephone number! Did you mention the wrong SMSC telephone number?");
					throw new Exception();
				}
			}

		} else
			LOG.info("CSCA already set...");

		smsc = "";
	}

	/**
	 * Tries to set the specified international SMSC telephone number. Adds
	 * \",145\" to the number, indicating an international SMSC telephone number
	 * 
	 * @param smsc
	 *            The international format of a SMSC telephone number (e.g.
	 *            +436990008999 --> YESSS)
	 * @return True if SMSC was successfully set; False otherwise
	 */
	private boolean setSMSC(String smsc) {
		String[] answer;

		// Send AT+CSCA="smsc",145 to the telephone...
		answer = sendATCommand("AT+CSCA=\"" + smsc + "\",145", DELAY_DEFAULT);

		LOG.info("Return-Code: {}", answer[1]);
		LOG.info("Telephone sent: {}", answer[0]);

		// Check SMSC setting...
		answer = sendATCommand("AT+CSCA?", DELAY_DEFAULT);

		LOG.info("Return-Code: {}", answer[1]);
		LOG.info("Telephone sent: {}", answer[0]);

		if (answer[0].contains("+CSCA: \"" + smsc + "\",145")) {
			return true;
		}

		return false;
	}

	/**
	 * Checks if the SIM-Card of the telephone needs to be unlocked. If so, the
	 * PIN Code is looked up in the properties file; if not specified, the
	 * PIN-Code is typed in by the user
	 * 
	 * @throws Exception
	 */
	public void checkPIN() throws Exception {
		String answer[];
		String pin = "";

		answer = sendATCommand("AT+CPIN?", DELAY_DEFAULT);

		LOG.info("Return-Code: {}", answer[1]);
		LOG.info("Telephone sent: {}", answer[0]);

		if (!answer[0].contains("READY")) {
			LOG.info("Telephone is protected by a PIN-Code and needs to be unlocked!");
			LOG.info("Reading PIN from properties file...");

			// Is there a valid PIN_KEY available in the properties file?
			if ((prop.getProperty(SMSPropertiesService.PIN_KEY) != null)
					&& (prop.getProperty(SMSPropertiesService.PIN_KEY).length() > 0)) {

				// Try to unlock the SIM-Card...
				LOG.info("Using the PIN-Code of the properties file...");
				if (loginUsingPIN(prop
						.getProperty(SMSPropertiesService.PIN_KEY)))
					LOG.info("Succesfully logged in using the specified PIN!");
				else {
					LOG.error("Couldn't unlock the SIM-Card! Did you mention the wrong PIN-Code?");
					throw new Exception();
				}

			} else {
				LOG.info("No PIN-Code specified!");

				// Specify PIN Code by the keyboard...
				// Update: Can't get console while unit testing... So skipping
				// this part...

				LOG.info("There is no PIN-Code specified in the properties file... Starting keyboard input of the PIN-Code...");
				System.out.println("Please enter a valid PIN-Code:");
				pin = new String(System.console().readPassword());

				LOG.info("Sending PIN to telephone...");
				if (loginUsingPIN(pin))
					LOG.info("Succesfully logged in using the specified PIN!");
				else {
					LOG.error("Couldn't unlock the telephone! Did you mention the wrong PIN-Code?");
					throw new Exception();
				}
			}

		} else
			LOG.info("Telephone already unlocked...");

		pin = "";
	}

	/**
	 * Tries to unlock the SIM-Card in the telephone using the specified
	 * PIN-Code
	 * 
	 * @param pin
	 *            The PIN-Code
	 * @return True if Login was successful; False otherwise
	 */
	public boolean loginUsingPIN(String pin) {
		String[] answer;

		// Send AT+CPIN=pin to the telephone...
		answer = sendATCommand("AT+CPIN=" + pin, DELAY_DEFAULT);

		LOG.info("Return-Code: {}", answer[1]);
		LOG.info("Telephone sent: {}", answer[0]);

		// Check lockstate...
		answer = sendATCommand("AT+CPIN?", DELAY_DEFAULT);

		LOG.info("Return-Code: {}", answer[1]);
		LOG.info("Telephone sent: {}", answer[0]);

		if (answer[0].contains("READY")) {
			return true;
		}

		return false;
	}

	/**
	 * Resets the telephone to its default values
	 */
	public void resetTelephone() {
		String answer[];

		answer = sendATCommand("ATZ", DELAY_DEFAULT);

		LOG.info("Return-Code: {}", answer[1]);
		LOG.info("Telephone sent: {}", answer[0]);
	}

	/**
	 * Turns off the telephones echo function
	 */
	public void turnEchoOff() {
		String answer[];

		answer = sendATCommand("ATE0", DELAY_DEFAULT);

		LOG.info("Return-Code: {}", answer[1]);
		LOG.info("Telephone sent: {}", answer[0]);
	}

	/**
	 * Sends the specified AT Command to the globally specified COM Port,
	 * including the option to wait specified milliseconds for the answer
	 * 
	 * @param command
	 *            The AT command, starting with "AT" (do not specify CR!)
	 * @param delay
	 *            The period in milliseconds the the application waits before
	 *            processing the telephones return values
	 * @return String[] containing the answer ([0]) and the Return-Code ([1])
	 *         from the telephone
	 */
	public String[] sendATCommand(String command, int delay) {
		String answer = "";
		String returnCode = "";
		boolean first = true;

		LOG.info("Sending \"{}\"", command);
		writer.write(command + "\r\n");
		writer.flush();

		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
		}

		while (true) {
			try {
				if (first) {
					returnCode = reader.readLine();
					answer = returnCode;
					first = false;
				} else {
					returnCode = reader.readLine();
					answer = answer + "\n" + returnCode;
				}
			} catch (IOException e) {
				break;
			}
		}

		return new String[] { answer, returnCode };
	}
}