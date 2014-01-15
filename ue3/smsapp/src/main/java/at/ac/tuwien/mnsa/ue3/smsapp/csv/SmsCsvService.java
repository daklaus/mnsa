package at.ac.tuwien.mnsa.ue3.smsapp.csv;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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
			smsList = loadSms();
		}

		return smsList;
	}

	/**
	 * Loads content from globally CSV-File (specified in SmsPropertiesService)
	 * and creates a List of Sms out of it.
	 * 
	 * @return A List containing Sms Elements
	 * @throws IOException
	 */
	private List<Sms> loadSms() throws IOException {

		String csvFile = PropertiesServiceFactory.getPropertiesService()
				.getProperties().getProperty(SmsPropertiesService.CSV_KEY);

		Reader reader = null;
		List<Sms> smsList;
		try {
			try {
				reader = new InputStreamReader(
						ClassLoader.getSystemResourceAsStream(csvFile));

				smsList = loadSms(reader, csvFile);
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

	/**
	 * Loads content from a CSV file specified in a stream and creates a List of
	 * SMS out of it.
	 * 
	 * @param reader
	 *            a Reader containing a stream of a CSV file
	 * @param csvFileName
	 *            the name of the file which will be parsed
	 * @return A List containing Sms Elements
	 * @throws IOException
	 */
	List<Sms> loadSms(Reader reader, String csvFileName) throws IOException {
		String[] line;

		CSVReader csvReader = null;
		List<Sms> smsList = new ArrayList<Sms>();
		try {
			try {
				csvReader = new CSVReader(reader, SEPERATOR);

				// Read contents per line...
				for (int i = 0; (line = csvReader.readNext()) != null; i++) {

					if (line.length != 2) {
						log.error(
								"Values of the line should count 2 but the actual number is {}",
								line.length);
						continue;
					}

					try {
						// Create a Sms and save it into the smsList
						log.debug("Sms going to be saved.");
						smsList.add(new Sms(line[0], line[1]));

					} catch (IllegalArgumentException e) {
						log.error("Error while parsing line {} of {}: {}",
								i + 1, csvFileName, e.getMessage());
					}

				}

			} finally {
				if (csvReader != null) {
					csvReader.close();
				}
			}
		} catch (Exception e) {
			throw new IOException("Couldn't load " + csvFileName + "!", e);
		}

		return smsList;
	}
}