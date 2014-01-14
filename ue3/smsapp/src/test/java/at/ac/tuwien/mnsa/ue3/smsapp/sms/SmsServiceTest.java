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
		assertArrayEquals("ABC", NumberConverter.hexStringToBytes("41E110"),
				SmsService.encodeMsgInSeptets("ABC"));
		assertArrayEquals("XD", NumberConverter.hexStringToBytes("5822"),
				SmsService.encodeMsgInSeptets("XD"));
		assertArrayEquals(";-)", NumberConverter.hexStringToBytes("BB560A"),
				SmsService.encodeMsgInSeptets(";-)"));
		assertArrayEquals("Test PDU",
				NumberConverter.hexStringToBytes("D4F29C0E8212AB"),
				SmsService.encodeMsgInSeptets("Test PDU"));
		assertArrayEquals(
				"This is a much longer version of a test string for testing longer messages",
				NumberConverter
						.hexStringToBytes("54747A0E4ACF416150BB3E4683D86FF7B92C07D9CBF279FAED06BDCDA030885E9ED34173BA3CED3E83CC6F39885E9ED3D3EE3388FD769FCB7250BB3C9F87CFE539"),
				SmsService
						.encodeMsgInSeptets("This is a much longer version of a test string for testing longer messages"));
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
