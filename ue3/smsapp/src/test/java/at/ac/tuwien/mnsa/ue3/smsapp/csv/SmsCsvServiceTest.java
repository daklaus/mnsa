package at.ac.tuwien.mnsa.ue3.smsapp.csv;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.mnsa.ue3.smsapp.csv.CsvServiceFactory;
import at.ac.tuwien.mnsa.ue3.smsapp.sms.Sms;

public class SmsCsvServiceTest {

	private static final Logger log = LoggerFactory
			.getLogger(SmsCsvServiceTest.class);

	private static List<Sms> smsList;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		smsList = CsvServiceFactory.getCsvService().getSMSList();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	// TODO Make the class more testable and add tests
}