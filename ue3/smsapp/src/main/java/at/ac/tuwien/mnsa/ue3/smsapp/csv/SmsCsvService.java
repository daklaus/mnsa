package at.ac.tuwien.mnsa.ue3.smsapp.csv;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.mnsa.ue3.smsapp.properties.PropertiesServiceFactory;
import at.ac.tuwien.mnsa.ue3.smsapp.properties.SmsPropertiesService;
import at.ac.tuwien.mnsa.ue3.smsapp.sms.Sms;
import au.com.bytecode.opencsv.CSVReader;

public class SmsCsvService implements CsvService {

	private static final char SEPERATOR = ',';

	private static final Logger log = LoggerFactory
			.getLogger(SmsCsvService.class);

	private List<Sms> smsList;

	// Private constructor prevents instantiation from other classes
	private SmsCsvService() {
		smsList = null;
	}

	private static class CsvServiceHolder {
		public static final CsvService INSTANCE = new SmsCsvService();
	}

	static CsvService getInstance() {
		return CsvServiceHolder.INSTANCE;
	}

	@Override
	public List<Sms> getSMSList() throws IOException {
		if (smsList == null) {
			smsList = loadSMS();
		}

		return smsList;
	}

	/**
	 * Loads content from globally CSV-File (specified in SmsPropertiesService)
	 * and creates a List of Sms out of it
	 * 
	 * @return A List containing Sms Elements
	 * @throws IOException
	 */
	private List<Sms> loadSMS() throws IOException {

		String csvFile = PropertiesServiceFactory.getPropertiesService()
				.getProperties().getProperty(SmsPropertiesService.CSV_KEY);

		String[] line;
		CSVReader reader = null;

		try {
			try {
				reader = new CSVReader(new InputStreamReader(
						ClassLoader.getSystemResourceAsStream(csvFile)),
						SEPERATOR);

				smsList = new ArrayList<Sms>();

				// Read contents per line...
				for (int i = 0; (line = reader.readNext()) != null; i++) {
					// while ((line = reader.readNext()) != null) {

					if (line.length != 2) {
						log.error(
								"Values of the line should count 2 but the actual number is {}",
								line.length);
						continue;
					}

					try {
						// Check for correctness of recipient and message
						// Is now done in the Sms class constructor (in a much
						// more efficient and compact way)
						// TODO delete this
						// rawSms = checkRawSms(line[0], line[1]);

						// Create a Sms and save it into the smsList
						log.debug("Sms going to be saved.");
						smsList.add(new Sms(line[0], line[1]));

					} catch (IllegalArgumentException e) {
						log.error("Error while parsing line {} of {}: {}",
								i + 1, csvFile, e.getMessage());
					}

				}

			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} catch (Exception e) {
			throw new IOException("Couldn't load " + csvFile + "!", e);
		}

		return smsList;
	}

	// TODO delete this
	// private String[] checkRawSms(String recipient, String message)
	// throws IllegalArgumentException, IndexOutOfBoundsException {
	//
	// // Trim whitespace of recipient and message...
	// recipient = recipient.trim();
	// message = message.trim();
	//
	// log.debug("Got recipient \"{}\" with message \"{}\"", recipient,
	// message);
	//
	// // Check if recipient or message have 0 length...
	// if ((recipient.equalsIgnoreCase("")) || (message.equalsIgnoreCase("")))
	// throw new IllegalArgumentException(
	// "No recipent or message specified");
	//
	// // Filter rn from the end of message...
	// if (message.length() > 1) {
	// if (message.substring(message.length() - 2, message.length())
	// .contains("rn")) {
	// message = message.substring(0, message.length() - 2);
	// log.debug("Message without CRs: {}", message);
	// }
	// }
	//
	// // Check telephone number of recipient...
	// if (recipient.length() > 1) {
	// if ((!recipient.substring(0, 1).equalsIgnoreCase("+"))
	// || (!recipient.substring(1).matches("\\d+(\\.\\d+)?")))
	// throw new IllegalArgumentException(
	// "Format of international telephone number not correct!");
	// } else
	// throw new IllegalArgumentException(
	// "Format of international telephone number not correct! Telephone number is too short.");
	//
	// return new String[] { recipient, message };
	// }
}