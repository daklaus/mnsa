package at.ac.tuwien.mnsa.ue3.smsapp.sms;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import at.ac.tuwien.common.binary.NumberConverter;

public class SmsServiceTest {

	public SmsServiceTest() {
	}

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
	public void testGetSmsDataParts() {
		// TODO method stub
		// fail("Not yet implemented");
	}

	@Test
	public void testEncodeMsgInSeptets() {
		// TODO method stub
		// fail("Not yet implemented");
	}

	@Test
	public void testConvertWith7BitAlphabet() {
		// TODO method stub
		// fail("Not yet implemented");
	}

	@Test
	public void testEncodeInternationalNumberInSemiOctets() {

		// Telephone number: +436643080823
		assertArrayEquals(NumberConverter.hexStringToBytes("0C91346634808032"),
				SmsService
						.encodeInternationalNumberInSemiOctets("+436643080823"));

		// Telephone number: +43664308082
		assertArrayEquals(NumberConverter.hexStringToBytes("0B913466348080F2"),
				SmsService
						.encodeInternationalNumberInSemiOctets("+43664308082"));
	}
}