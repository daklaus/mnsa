package at.ac.tuwien.mnsa.ue3.csv;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.mnsa.ue3.properties.PropertiesServiceFactory;
import at.ac.tuwien.mnsa.ue3.properties.SMSPropertiesService;

public class SMSCsvService implements CsvService {

	private static final Logger LOG = LoggerFactory
			.getLogger(SMSCsvService.class);

	private List<SMS> smsList;

	// Private constructor prevents instantiation from other classes
	private SMSCsvService() {
		smsList = null;
	}

	private static class CsvServiceHolder {
		public static final CsvService INSTANCE = new SMSCsvService();
	}

	static CsvService getInstance() {
		return CsvServiceHolder.INSTANCE;
	}

	@Override
	public List<SMS> getSMSList() throws IOException {
		if (smsList == null) {
			smsList = loadSMS();
		}

		return smsList;
	}

	/**
	 * Loads content from globally CSV-File (specified in SMSPropertiesService)
	 * and creates a List of SMS out of it
	 * 
	 * @return A List containing SMS Elements
	 * @throws IOException
	 */
	private List<SMS> loadSMS() throws IOException {

		String csvFile = PropertiesServiceFactory.getPropertiesService()
				.getProperties().getProperty(SMSPropertiesService.CSV_KEY);

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		String[] rawSms = { "", "" };

		try {
			br = new BufferedReader(new InputStreamReader(
					ClassLoader.getSystemResourceAsStream(csvFile)));

			smsList = new ArrayList<SMS>();

			// Read contents per line...
			while ((line = br.readLine()) != null) {

				// first comma separates recipient from message
				rawSms = line.trim().split(cvsSplitBy, 2);

				// Check for correctness of recipient and message
				try {
					rawSms = checkRawSms(rawSms);
				} catch (IllegalArgumentException e) {
					LOG.error(e.getMessage());
				}

				// Create a SMS and save it into the smsList
				LOG.info(
						"SMS going to be saved. Recipient: \"{}\", Message: \"{}\"",
						rawSms[0], rawSms[1]);
				smsList.add(new SMS(rawSms[0], rawSms[1]));
			}

		} catch (FileNotFoundException e) {
			throw new IOException("Couldn't load " + csvFile + "!", e);
		} catch (IOException e) {
			throw new IOException("Couldn't load " + csvFile + "!", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}

		return smsList;
	}

	private String[] checkRawSms(String[] rawSms)
			throws IllegalArgumentException {
		String recipient = "";
		String message = "";

		// Trim whitespace of recipient and message...
		recipient = rawSms[0].trim();
		message = rawSms[1].trim();

		// Check if recipient or message have 0 length...
		if ((recipient == "") || (message == ""))
			throw new IllegalArgumentException(
					"No recipent or message specified");

		// Filter \r\n from the end of message...
		if (message.lastIndexOf("\r\n") != -1) {
			message = message.substring(message.lastIndexOf("\r\n"));
			LOG.info("Message without CRs: {}", message);
		}

		// Check telephone number of recipient...
		if ((!recipient.substring(0, 1).equalsIgnoreCase("+"))
				|| (!recipient.substring(1).matches("\\d+(\\.\\d+)?")))
			throw new IllegalArgumentException(
					"Format of international telephone number not correct!");

		return new String[] { recipient, message };
	}
}