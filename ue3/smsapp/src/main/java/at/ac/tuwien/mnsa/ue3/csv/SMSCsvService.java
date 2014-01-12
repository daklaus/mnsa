package at.ac.tuwien.mnsa.ue3.csv;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.mnsa.ue3.properties.PropertiesServiceFactory;
import at.ac.tuwien.mnsa.ue3.properties.SMSPropertiesService;
import au.com.bytecode.opencsv.CSVReader;

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

		String[] line;
		char cvsSplitBy = ',';
		String[] rawSms = { "", "" };
		CSVReader reader = null;

		try {
			reader = new CSVReader(new InputStreamReader(
					ClassLoader.getSystemResourceAsStream(csvFile)), cvsSplitBy);

			smsList = new ArrayList<SMS>();

			// Read contents per line...
			while ((line = reader.readNext()) != null) {

				// Check for correctness of recipient and message
				try {
					rawSms = checkRawSms(line[0], line[1]);

					// Create a SMS and save it into the smsList
					LOG.info("SMS going to be saved.");
					smsList.add(new SMS(rawSms[0], rawSms[1]));

				} catch (IllegalArgumentException e) {
					LOG.error(e.getMessage());
				}

				LOG.info(" ");
			}

		} catch (FileNotFoundException e) {
			throw new IOException("Couldn't load " + csvFile + "!", e);
		} catch (IOException e) {
			throw new IOException("Couldn't load " + csvFile + "!", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}

		return smsList;
	}

	private String[] checkRawSms(String recipient, String message)
			throws IllegalArgumentException, IndexOutOfBoundsException {

		// Trim whitespace of recipient and message...
		recipient = recipient.trim();
		message = message.trim();

		LOG.info("Got recipient \"{}\" with message \"{}\"", recipient, message);

		// Check if recipient or message have 0 length...
		if ((recipient.equalsIgnoreCase("")) || (message.equalsIgnoreCase("")))
			throw new IllegalArgumentException(
					"No recipent or message specified");

		// Filter rn from the end of message...
		if (message.length() > 1) {
			if (message.substring(message.length() - 2, message.length())
					.contains("rn")) {
				message = message.substring(0, message.length() - 2);
				LOG.info("Message without CRs: {}", message);
			}
		}

		// Check telephone number of recipient...
		if (recipient.length() > 1) {
			if ((!recipient.substring(0, 1).equalsIgnoreCase("+"))
					|| (!recipient.substring(1).matches("\\d+(\\.\\d+)?")))
				throw new IllegalArgumentException(
						"Format of international telephone number not correct!");
		} else
			throw new IllegalArgumentException(
					"Format of international telephone number not correct! Telephone number is too short.");

		return new String[] { recipient, message };
	}
}