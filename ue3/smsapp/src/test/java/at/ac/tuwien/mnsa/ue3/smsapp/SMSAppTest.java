package at.ac.tuwien.mnsa.ue3.smsapp;

import static org.junit.Assert.*;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.mnsa.ue3.properties.PropertiesServiceFactory;
import at.ac.tuwien.mnsa.ue3.properties.SMSPropertiesService;

public class SMSAppTest {
	private static final Logger LOG = LoggerFactory.getLogger(SMSAppTest.class);

	private static SerialPort serialPort;

	private static InputStream input;

	private static OutputStream output;

	private static BufferedReader reader;

	private static PrintWriter writer;

	private static final int CONNECTION_TIMEOUT = 1000;

	private static Properties prop;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String answer[];

		LOG.info("================================");
		LOG.info("Setting up locale environment...");
		LOG.info("================================");

		LOG.info("Getting properties...");
		prop = PropertiesServiceFactory.getPropertiesService().getProperties();
		String comPort = prop.getProperty(SMSPropertiesService.PORT_KEY);
		String csvFile = prop.getProperty(SMSPropertiesService.CSV_KEY);

		LOG.info("COM-Port: {}", comPort);
		LOG.info("CSV-File: {}", csvFile);

		LOG.info("Opening Serial Port...");
		serialPort = (SerialPort) CommPortIdentifier.getPortIdentifier(comPort)
				.open("Test Terminal", CONNECTION_TIMEOUT);
		serialPort.enableReceiveTimeout(CONNECTION_TIMEOUT);

		LOG.info("Setting up I/O communication...");
		input = serialPort.getInputStream();
		output = serialPort.getOutputStream();
		reader = new BufferedReader(new InputStreamReader(input));
		writer = new PrintWriter(output, true);

		LOG.info("Environment setup done.");

		LOG.info(" ");
		LOG.info("=================================");
		LOG.info("Bringing telephone up to speed...");
		LOG.info("=================================");

		LOG.info("Resetting telephone...");
		resetTelephone();

		LOG.info("Turning off echo of telephone...");
		turnEchoOff();

		LOG.info("Checking for PIN-Code...");
		checkPIN();

		LOG.info("Checking SMSC...");
		checkSMSC();

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		LOG.info(" ");
		LOG.info("====================");
		LOG.info("Starting teardown...");
		LOG.info("====================");

		writer.close();
		reader.close();
		output.close();
		input.close();
		serialPort.close();
		prop.clear();

		LOG.info("Teardown done. Shutting down now...");
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getModemInformation() {
		String answer[];

		LOG.info("\n");
		LOG.info("============");
		LOG.info("Sending ATI7");
		LOG.info("============");

		answer = sendATCommand("ATI7");

		LOG.info("Return-Code: {}", answer[1]);
		LOG.info("Telephone sent: {}", answer[0]);

		assertTrue(answer[1].equalsIgnoreCase("ok"));
	}

	// @Test
	// public void testCall() {
	// String answer[];
	//
	// LOG.info(" ");
	// LOG.info("====================");
	// LOG.info("Sending AT+CPIN=5855");
	// LOG.info("====================");
	//
	// answer = sendATCommand("AT+CPIN=5855");
	//
	// LOG.info("Return-Code: {}", answer[1]);
	// LOG.info("Telephone sent: {}", answer[0]);
	//
	// LOG.info(" ");
	// LOG.info("========================");
	// LOG.info("SendingATD+436643080823;");
	// LOG.info("========================");
	//
	// answer = sendATCommand("ATD+436643080823;");
	//
	// LOG.info("Return-Code: {}", answer[1]);
	// LOG.info("Telephone sent: {}", answer[0]);
	//
	// assertTrue(answer[1].equalsIgnoreCase("ok"));
	// }

	/**
	 * Checks if the SMS Service Center Address (SMSC) is set. If not, the
	 * telephone number specified in the properties file is used; if not
	 * specified, the SMSC telephone number is typed in by the user
	 */
	public static void checkSMSC() {
		String answer[];

		answer = sendATCommand("AT+CSCA?");

		LOG.info("Return-Code: {}", answer[1]);
		LOG.info("Telephone sent: {}", answer[0]);

		// TODO
	}

	/**
	 * Checks if the SIM-Card of the telephone needs to be unlocked. If so, the
	 * PIN Code is looked up in the properties file; if not specified, the
	 * PIN-Code is typed in by the user
	 */
	public static void checkPIN() {
		String answer[];
		String pin = "";

		answer = sendATCommand("AT+CPIN?");

		LOG.info("Return-Code: {}", answer[1]);
		LOG.info("Telephone sent: {}", answer[0]);

		if (!answer[0].contains("READY")) {
			LOG.info("Telephone is protected by a PIN-Code and needs to be unlocked!");
			LOG.info("Reading PIN from properties file...");

			// Is PIN_KEY available in the properties file?
			if (prop.getProperty(SMSPropertiesService.PIN_KEY) != null) {

				// Is the PINs length bigger than 0 characters?
				if (prop.getProperty(SMSPropertiesService.PIN_KEY).length() > 0) {

					// Try to unlock the SIM-Card...
					LOG.info("Using the PIN-Code of the properties file...");
					if (loginUsingPIN(prop
							.getProperty(SMSPropertiesService.PIN_KEY)))
						LOG.info("Succesfully logged in using the specified PIN!");
					else
						LOG.info("Couldn't unlock the telephone! Did you mention the wrong PIN-Code?");

				} else {

					// Specify PIN Code by the keyboard...
					LOG.info("The specified PIN-Code contains 0 characters... Starting keyboard input of the PIN-Code...");
					System.out.println("Please enter a valid PIN-Code:");
					pin = new String(System.console().readPassword());

					LOG.info("Sending PIN to telephone...");
					if (loginUsingPIN(pin))
						LOG.info("Succesfully logged in using the specified PIN!");
					else
						LOG.info("Couldn't unlock the telephone! Did you mention the wrong PIN-Code?");
				}
			} else {

				// Specify PIN Code by the keyboard...
				LOG.info("There is no PIN-Code specified in the properties file... Starting keyboard input of the PIN-Code...");
				System.out.println("Please enter a valid PIN-Code:");
				pin = new String(System.console().readPassword());

				LOG.info("Sending PIN to telephone...");
				if (loginUsingPIN(pin))
					LOG.info("Succesfully logged in using the specified PIN!");
				else
					LOG.info("Couldn't unlock the telephone! Did you mention the wrong PIN-Code?");
			}

		} else
			LOG.info("Telephone already unlocked...");
	}

	/**
	 * Tries to unlock the SIM-Card in the telephone using the specified
	 * PIN-Code
	 * 
	 * @param pin
	 *            The PIN-Code
	 * @return True if Login was succesful; False otherwise
	 */
	public static boolean loginUsingPIN(String pin) {
		String[] answer;

		// Send AT+CPIN=pin to the telephone...
		answer = sendATCommand("AT+CPIN=" + pin);

		LOG.info("Return-Code: {}", answer[1]);
		LOG.info("Telephone sent: {}", answer[0]);

		// Check lockstate...
		answer = sendATCommand("AT+CPIN?");

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
	public static void resetTelephone() {
		String answer[];

		answer = sendATCommand("ATZ");

		LOG.info("Return-Code: {}", answer[1]);
		LOG.info("Telephone sent: {}", answer[0]);
	}

	/**
	 * Turns off the telephones echo function
	 */
	public static void turnEchoOff() {
		String answer[];

		answer = sendATCommand("ATE0");

		LOG.info("Return-Code: {}", answer[1]);
		LOG.info("Telephone sent: {}", answer[0]);
	}

	/**
	 * Sends the specified AT Command to the globally specified COM Port
	 * 
	 * @param command
	 *            The AT command, starting with "AT" (do not specify CR!)
	 * @return String[] containing the answer ([0]) and the Return-Code ([1])
	 *         from the telephone
	 */
	private static String[] sendATCommand(String command) {
		String answer = "";
		String returnCode = "";
		boolean first = true;

		writer.write(command + "\r\n");
		writer.flush();

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