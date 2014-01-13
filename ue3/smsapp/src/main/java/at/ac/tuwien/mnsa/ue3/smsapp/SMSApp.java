package at.ac.tuwien.mnsa.ue3.smsapp;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.mnsa.ue3.csv.CsvServiceFactory;
import at.ac.tuwien.mnsa.ue3.csv.SMS;
import at.ac.tuwien.mnsa.ue3.properties.PropertiesServiceFactory;
import at.ac.tuwien.mnsa.ue3.properties.SMSPropertiesService;

public class SMSApp {

	private static final Logger log = LoggerFactory.getLogger(SMSApp.class);

	private static SerialPort serialPort;
	private static BufferedReader reader;
	private static PrintWriter writer;

	private static final int CONNECTION_TIMEOUT = 1000;
	private static final int PIN_MAX_RETRY = 3;
	private static final int SMSC_MAX_RETRY = 3;

	public static void main(String[] args) throws Exception {
		try {
			try {
				log.debug("Setting up locale environment...");

				log.debug("Getting properties...");
				String comPort = getComPort();

				log.debug("Loading SMS list...");
				List<SMS> smsList = CsvServiceFactory.getCsvService()
						.getSMSList();

				log.debug("The following SMS will be sent:");
				for (SMS sms : smsList) {
					log.debug("Recipient: \"{}\", Message: \"{}\"",
							sms.getRecipient(), sms.getMessage());
				}

				log.debug("Opening Serial Port...");
				serialPort = (SerialPort) CommPortIdentifier.getPortIdentifier(
						comPort).open("Test Terminal", CONNECTION_TIMEOUT);
				serialPort.enableReceiveTimeout(CONNECTION_TIMEOUT);

				log.debug("Setting up I/O communication...");
				reader = new BufferedReader(new InputStreamReader(
						serialPort.getInputStream()));
				writer = new PrintWriter(serialPort.getOutputStream(), true);

				log.debug("Environment setup done.");

				log.debug("Bringing telephone up to speed...");
				initializeTelephone();

			} finally {
				close();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	private static void initializeTelephone() throws Exception {
		log.debug("Resetting telephone...");
		resetTelephone();

		log.debug("Turning off echo of telephone...");
		turnEchoOff();

		log.debug("Checking for PIN-Code...");
		checkPinLock();

		log.debug("Checking SMSC...");
		checkSmsc();

		log.debug("Initial telephone setup done.");
	}

	/**
	 * Frees up resources
	 */
	private static void close() {
		if (writer != null)
			writer.close();
		if (reader != null)
			try {
				reader.close();
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		if (serialPort != null)
			serialPort.close();
	}

	/**
	 * Checks if the SMS Service Center Address (SMSC) is set. If not, the
	 * telephone number specified in the properties file is used; if not
	 * specified, the SMSC telephone number is typed in by the user
	 * 
	 * @throws Exception
	 */
	private static void checkSmsc() throws Exception {
		String answer = sendATCommand("AT+CSCA?").getAnswer();

		if (!answer.contains("+CSCA: \"\",")) {
			log.debug("CSCA already set...");
			return;
		}

		// SMSC needs to be set
		log.debug("SMSC not set; Looking up properties file for SMSC telephone number...");

		// Is a there valid SMSC_KEY available in the properties file?
		String smsc = getSmsc();

		int tryCount = 0;
		boolean successfullySet = false;
		do {
			if (smsc == null || smsc.length() <= 0 || tryCount > 1) {
				// Specify SMSC by the keyboard...
				log.debug("There is no SMSC telephone number specified in the properties file... Starting keyboard input of the SMSC number...");

				System.out.print("Enter SMSC in international format: ");
				smsc = new String(System.console().readLine());
			}

			successfullySet = trySetSmsc(smsc);
			if (!successfullySet) {
				System.out
						.println("The phone didn't accept the specifyed SMSC (try "
								+ (tryCount + 1) + "/" + SMSC_MAX_RETRY + ")!");
			}

			tryCount++;
		} while (!successfullySet && tryCount < SMSC_MAX_RETRY);

		if (successfullySet)
			log.debug("Succesfully set SMSC telephone number!");
		else {
			throw new RuntimeException(
					"Couldn't set the SMSC telephone number! Did you mention the wrong SMSC telephone number?");
		}
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
	private static boolean trySetSmsc(String smsc) {
		// Send AT+CSCA="smsc",145 to the telephone...
		sendATCommand("AT+CSCA=\"" + smsc + "\",145");

		// Check SMSC setting...
		String answer = sendATCommand("AT+CSCA?").getAnswer();

		if (answer.contains("+CSCA: \"" + smsc + "\",145")) {
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
	private static void checkPinLock() throws Exception {
		String answer = sendATCommand("AT+CPIN?").getAnswer();

		if (answer.contains("READY")) {
			log.debug("Telephone already unlocked...");
			return;
		}

		log.debug("Telephone is protected by a PIN-Code and needs to be unlocked!");
		log.debug("Reading PIN from properties file...");

		// Is there a valid PIN_KEY available in the properties file?
		String pin = getPin();

		int tryCount = 0;
		boolean unlocked = false;
		do {
			if (pin == null || pin.length() <= 0 || tryCount > 1) {
				// Specify PIN Code by the keyboard...
				log.debug("There is no PIN-Code specified in the properties file... Starting keyboard input of the PIN-Code...");

				System.out.print("Enter PIN-Code: ");
				pin = new String(System.console().readPassword());
			}

			unlocked = tryPinUnlock(pin);
			if (!unlocked) {
				System.out.println("The specifyed PIN was wrong (try "
						+ (tryCount + 1) + "/" + PIN_MAX_RETRY + ")!");
			}

			tryCount++;
		} while (!unlocked && tryCount < PIN_MAX_RETRY);

		if (unlocked)
			log.debug("Succesfully logged in using the specified PIN!");
		else {
			throw new RuntimeException(
					"Couldn't unlock the SIM-Card! Did you mention the wrong PIN-Code?");
		}
	}

	/**
	 * Tries to unlock the SIM-Card in the telephone using the specified
	 * PIN-Code
	 * 
	 * @param pin
	 *            The PIN-Code
	 * @return True if Login was successful; False otherwise
	 */
	private static boolean tryPinUnlock(String pin) {
		// Send AT+CPIN=pin to the telephone...
		sendATCommand("AT+CPIN=" + pin);

		// Check lockstate...
		String answer = sendATCommand("AT+CPIN?").getAnswer();

		if (answer.contains("READY")) {
			return true;
		}

		return false;
	}

	/**
	 * Resets the telephone to its default values
	 */
	private static void resetTelephone() {
		// Doing 3 times, just to be sure... ;) 1 times doesn't work 100%
		sendATCommand("\u001aATZ");
		sendATCommand("ATZ");
		sendATCommand("ATZ");
	}

	/**
	 * Turns off the telephones echo function
	 */
	private static void turnEchoOff() {
		sendATCommand("ATE0");
	}

	/**
	 * Sends the specified AT Command to the globally specified COM Port,
	 * including the option to wait specified milliseconds for the answer
	 * 
	 * @param command
	 *            The AT command, starting with "AT" (do not specify CR!)
	 * @return String[] containing the answer ([0]) and the Return-Code ([1])
	 *         from the telephone
	 */
	private static ATCommandReturn sendATCommand(String command) {
		String answer = "";
		String returnCode = "";
		boolean first = true;

		log.debug("Sending \"{}\"", command);
		writer.write(command + "\r\n");
		writer.flush();

		// TODO This is suboptimal, maybe there is a cleaner implementation for
		// this. CHO: I know, also tried scanner (with "hasNext()") and so on,
		// but couldn't find a "good" (non-workaround) solution... So I used
		// exceptions.
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

		log.debug("Return-Code: {}", returnCode);
		log.debug("Telephone sent: {}", answer);

		return new ATCommandReturn(answer, returnCode);
	}

	private static String getComPort() throws IOException {
		return PropertiesServiceFactory.getPropertiesService().getProperties()
				.getProperty(SMSPropertiesService.PORT_KEY);
	}

	private static String getCsvFileName() throws IOException {
		return PropertiesServiceFactory.getPropertiesService().getProperties()
				.getProperty(SMSPropertiesService.CSV_KEY);
	}

	private static String getSmsc() throws IOException {
		return PropertiesServiceFactory.getPropertiesService().getProperties()
				.getProperty(SMSPropertiesService.SMSC_KEY);
	}

	private static String getPin() throws IOException {
		return PropertiesServiceFactory.getPropertiesService().getProperties()
				.getProperty(SMSPropertiesService.PIN_KEY);
	}

	private static class ATCommandReturn {
		private final String returnCode;
		private final String answer;

		public ATCommandReturn(String answer, String returnCode) {
			if (answer == null || returnCode == null)
				throw new IllegalArgumentException(
						"answer or returnCode is null");

			this.returnCode = returnCode;
			this.answer = answer;
		}

		public String getReturnCode() {
			return returnCode;
		}

		public String getAnswer() {
			return answer;
		}

	}
}