package at.ac.tuwien.mnsa.ue3.csv;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.mnsa.ue3.smsapp.csv.CsvServiceFactory;
import at.ac.tuwien.mnsa.ue3.smsapp.sms.Sms;

public class SMSCsvServiceTest {

	private static final Logger log = LoggerFactory
			.getLogger(SMSCsvServiceTest.class);

	private static List<Sms> smsList;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		// TODO Please do not log that much and please not in INFO level,
		// maximal at DEBUG level
		// log.debug("Setting up locale environment...");
		// log.debug("Loading Sms list...");
		smsList = CsvServiceFactory.getCsvService().getSMSList();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	// TODO Since there are no assertions this unit test doesn't test anything!
	// TODO The test is not independent from the resources (csv file)!
	// @Test
	// public void showSendableSMS() {
	//
	// log.debug("The following Sms will be sent:");
	// for (Sms sms : smsList) {
	// log.debug("Recipient: \"{}\", Message: \"{}\"", sms.getRecipient(),
	// sms.getMessage());
	// }
	// }
}