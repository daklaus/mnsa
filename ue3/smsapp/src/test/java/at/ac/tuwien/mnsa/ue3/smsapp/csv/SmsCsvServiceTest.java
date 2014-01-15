package at.ac.tuwien.mnsa.ue3.smsapp.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import at.ac.tuwien.mnsa.ue3.smsapp.sms.Sms;

public class SmsCsvServiceTest {

	// private static final Logger log = LoggerFactory
	// .getLogger(SmsCsvServiceTest.class);

	@Rule
	public TestName name = new TestName();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLoadSmsSpaceBeforeUnquoted() throws IOException {
		generalTest("+12345,  Hallo", new Sms("+12345", "  Hallo"));
	}

	@Test
	public void testLoadSmsSpaceAfterUnquoted() throws IOException {
		generalTest("+12345   ,Hallo", new Sms("+12345", "Hallo"));
	}

	@Test
	public void testLoadSmsSpaceBeforeQuoted() throws IOException {
		generalTest("\"+12345\",   \"Hallo\"", new Sms("+12345", "Hallo"));
	}

	@Test
	public void testLoadSmsSpaceAfterQuoted() throws IOException {
		// This will lead to Sms("+12345  ", "Hallo") where the number is not
		// valid so it should be skipped
		generalTest("\"+12345\"  ,\"Hallo\"");
	}

	@Test
	public void testLoadSmsEscapeCharactersUnquoted() throws IOException {
		generalTest("+12345,Hallo\tHallo", new Sms("+12345", "Hallo\tHallo"));
	}

	@Test
	public void testLoadSmsEscapeCharactersQuoted() throws IOException {
		generalTest("\"+12345\",\"Hallo\tHallo\"", new Sms("+12345",
				"Hallo\tHallo"));
	}

	@Test
	public void testLoadSmsMultilines() throws IOException {
		generalTest("+12345,Hallo1\r\n+67890,Hallo2", new Sms[] {
				new Sms("+12345", "Hallo1"), new Sms("+67890", "Hallo2") });
	}

	/*
	 * Helper methods
	 */

	private void generalTest(String csvData, Sms... expected)
			throws IOException {
		StringReader sr = new StringReader(csvData);
		List<Sms> actual = SmsCsvService.loadSms(sr, name.getMethodName()
				+ ".csv");

		assertNotNull(actual);
		assertEquals(expected.length, actual.size());
		assertEquals(Arrays.asList(expected), actual);
	}
}