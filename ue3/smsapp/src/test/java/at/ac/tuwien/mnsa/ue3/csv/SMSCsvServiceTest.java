package at.ac.tuwien.mnsa.ue3.csv;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SMSCsvServiceTest {

	private static final Logger log = LoggerFactory
			.getLogger(SMSCsvServiceTest.class);

	private static List<SMS> smsList;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		log.debug("Setting up locale environment...");

		log.debug("Loading SMS list...");
		smsList = CsvServiceFactory.getCsvService().getSMSList();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void showSendableSMS() {

		log.debug("The following SMS will be sent:");
		for (SMS sms : smsList) {
			log.debug("Recipient: \"{}\", Message: \"{}\"", sms.getRecipient(),
					sms.getMessage());
		}
	}
}