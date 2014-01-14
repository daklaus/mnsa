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

import at.ac.tuwien.common.binary.NumberConverter;
import at.ac.tuwien.mnsa.ue3.smsapp.csv.CsvServiceFactory;
import at.ac.tuwien.mnsa.ue3.smsapp.properties.PropertiesServiceFactory;
import at.ac.tuwien.mnsa.ue3.smsapp.properties.SmsPropertiesService;
import at.ac.tuwien.mnsa.ue3.smsapp.sms.Sms;
import at.ac.tuwien.mnsa.ue3.smsapp.sms.SmsDataPart;
import at.ac.tuwien.mnsa.ue3.smsapp.sms.SmsService;

public class SmsApp {

	private static final Logger log = LoggerFactory.getLogger(SmsApp.class);

	private static SerialPort serialPort;
	private static BufferedReader reader;
	private static PrintWriter writer;

	private static final int CONNECTION_TIMEOUT = 1000;
	private static final int PIN_MAX_RETRY = 3;
	private static final int SMSC_MAX_RETRY = 3;
	private static final int WAIT_FOR_SEND_REPLY = 5000;

	public static void main(String[] args) throws Exception {
		try {
			try {
				log.debug("Setting up locale environment...");

				log.debug("Getting properties...");
				String comPort = getComPort();

				log.debug("Loading SMS list...");
				List<Sms> smsList = CsvServiceFactory.getCsvService()
						.getSMSList();

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

				ATCommandReturn cmdReturn;
				String msg;
				for (Sms sms : smsList) {
					msg = "Processing SMS to \"" + sms.getRecipient()
							+ "\": \"" + sms.getMessage() + "\"";
					System.out.println(msg);
					log.info(msg);

					log.debug("Splitting into parts...");
					List<SmsDataPart> parts = SmsService.getSmsDataParts(sms);
					if (parts == null || parts.isEmpty())
						continue;

					if (parts.size() > 1)
						log.debug("SMS got " + parts.size() + " parts.");

					int i = 0;
					for (SmsDataPart smsDataPart : parts) {

						// Initiating the SMS sending by sending the number of
						// bytes to be sent
						sendATCommand("AT+CMGS="
								+ smsDataPart.getMsgByteLengthWithoutSmscPart());

						// Send the PDU terminated with a SUB character (hex 1A,
						// on keyboard Ctrl+Z)
						cmdReturn = sendATCommand(
								NumberConverter
										.bytesToHexString(smsDataPart.getPdu()),
								"\u001a", WAIT_FOR_SEND_REPLY);

						// Check if the SMS part was sent successfully
						if (cmdReturn.getReturnCode().contains("OK")) {
							msg = "Part " + (i + 1) + " of " + parts.size()
									+ " successfully sent!";
						} else {
							msg = "Something went wrong while sending part "
									+ (i + 1) + " of " + parts.size()
									+ ". Got answer: " + cmdReturn.getAnswer();
						}
						System.out.println(msg);
						log.debug(msg);

						i++;
					}
				}

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

		log.debug("Set PDU mode...");
		setPduMode();

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

	private static void setPduMode() {
		sendATCommand("AT+CMGF=0");
	}

	/**
	 * Checks if the Sms Service Center Address (SMSC) is set. If not, the
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
		sendATCommand("\u001a");
		sendATCommand("ATZ");
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
	 * Sends an AT command like {@link #sendATCommand(String, String)}
	 * specifying a carriage return (CR) followed by a line feed (LF) as
	 * terminator (i.e. "\r\n") and a zero waiting time for the answer.
	 * 
	 * @param command
	 *            see {@link #sendATCommand(String, String)}
	 * 
	 * @see #sendATCommand(String, String)
	 */
	private static ATCommandReturn sendATCommand(String command) {
		return sendATCommand(command, "\r\n", 0);
	}

	/**
	 * Sends the specified AT Command to the globally specified COM Port,
	 * including the option to wait specified milliseconds for the answer
	 * 
	 * @param command
	 *            the AT command which will be sent (without the terminating
	 *            CR/LF/CRLF!)
	 * @param commandTerminator
	 *            the string which should terminate the command (most of the
	 *            time "\r\n")
	 * @param waitForAnswer
	 *            the number of milliseconds the function should wait before
	 *            reading the answer from the phone
	 * @return an {@link ATCommandReturn} object containing the answer from the
	 *         phone
	 */
	private static ATCommandReturn sendATCommand(String command,
			String commandTerminator, long waitForAnswer) {
		String answer = "";
		String returnCode = "";
		boolean first = true;
		boolean readOn = true;

		log.debug("Sending \"{}\"", command);
		writer.write(command + commandTerminator);
		writer.flush();

		try {
			Thread.sleep(waitForAnswer);
		} catch (InterruptedException ignored) {
		}

		while (readOn) {
			try {
				returnCode = reader.readLine();

				// The return code is always the last line; the answer is
				// everything read
				if (!first) {
					answer += "\n";
				} else {
					first = false;
				}
				answer += returnCode;

			} catch (IOException e) {
				readOn = false;
			}
		}

		log.debug("Return-Code: {}", returnCode);
		log.debug("Telephone sent: {}", answer);

		return new ATCommandReturn(answer, returnCode);
	}

	private static String getComPort() throws IOException {
		return PropertiesServiceFactory.getPropertiesService().getProperties()
				.getProperty(SmsPropertiesService.PORT_KEY);
	}

	private static String getCsvFileName() throws IOException {
		return PropertiesServiceFactory.getPropertiesService().getProperties()
				.getProperty(SmsPropertiesService.CSV_KEY);
	}

	private static String getSmsc() throws IOException {
		return PropertiesServiceFactory.getPropertiesService().getProperties()
				.getProperty(SmsPropertiesService.SMSC_KEY);
	}

	private static String getPin() throws IOException {
		return PropertiesServiceFactory.getPropertiesService().getProperties()
				.getProperty(SmsPropertiesService.PIN_KEY);
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