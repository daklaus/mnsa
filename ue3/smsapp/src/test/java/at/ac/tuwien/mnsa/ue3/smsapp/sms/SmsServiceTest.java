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

	// @Test
	// public void testGetSmsDataParts() {
	// TODO test the getSmsDataParts method
	// fail("Not yet implemented");
	// }

	@Test
	public void testEncodeMsgInSeptets() {
		assertArrayEquals("E", NumberConverter.hexStringToBytes("45"),
				SmsService.encodeMsgInSeptets(NumberConverter
						.hexStringToBytes("45")));
		assertArrayEquals("ABC", NumberConverter.hexStringToBytes("41E110"),
				SmsService.encodeMsgInSeptets(NumberConverter
						.hexStringToBytes("414243")));
		assertArrayEquals("XD", NumberConverter.hexStringToBytes("5822"),
				SmsService.encodeMsgInSeptets(NumberConverter
						.hexStringToBytes("5844")));
		assertArrayEquals(";-)", NumberConverter.hexStringToBytes("BB560A"),
				SmsService.encodeMsgInSeptets(NumberConverter
						.hexStringToBytes("3b2d29")));
		assertArrayEquals("Test PDU",
				NumberConverter.hexStringToBytes("D4F29C0E8212AB"),
				SmsService.encodeMsgInSeptets(NumberConverter
						.hexStringToBytes("5465737420504455")));
		// assertArrayEquals(
		// "This is a much longer version of a test string for testing longer messages",
		// NumberConverter
		// .hexStringToBytes("54747A0E4ACF416150BB3E4683D86FF7B92C07D9CBF279FAED06BDCDA030885E9ED34173BA3CED3E83CC6F39885E9ED3D3EE3388FD769FCB7250BB3C9F87CFE539"),
		// SmsService
		// .encodeMsgInSeptets("This is a much longer version of a test string for testing longer messages"));
	}

	@Test
	public void testConvertWith7BitAlphabet() {
		assertArrayEquals("ABC", NumberConverter.hexStringToBytes("414243"),
				SmsService.convertWith7BitAlphabet("ABC"));

		assertArrayEquals(";-)", NumberConverter.hexStringToBytes("3b2d29"),
				SmsService.convertWith7BitAlphabet(";-)"));

		assertArrayEquals("A\nB", NumberConverter.hexStringToBytes("410a42"),
				SmsService.convertWith7BitAlphabet("A\nB"));

		// Test with CR LF SP ESC
		assertArrayEquals("\r\n \u001b",
				NumberConverter.hexStringToBytes("0d0a1b"),
				SmsService.convertWith7BitAlphabet("\r\n\u001b"));

		// With extensions
		assertArrayEquals(
				";-| {test} x[]",
				NumberConverter
						.hexStringToBytes("3b2d1b40201b28746573741b2920781b3c1b3e"),
				SmsService.convertWith7BitAlphabet(";-| {test} x[]"));

		// All Basic characters
		assertArrayEquals(
				"@£$¥èéùìòÇ\nØø\rÅåΔ_ΦΓΛΩΠΨΣΘΞ\u001BÆæßÉ !\"#¤%&'()*+,-./0123456789:;<=>?¡ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÑÜ§¿abcdefghijklmnopqrstuvwxyzäöñüà",
				NumberConverter
						.hexStringToBytes("000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f202122232425262728292a2b2c2d2e2f303132333435363738393a3b3c3d3e3f404142434445464748494a4b4c4d4e4f505152535455565758595a5b5c5d5e5f606162636465666768696a6b6c6d6e6f707172737475767778797a7b7c7d7e7f"),
				SmsService
						.convertWith7BitAlphabet("@£$¥èéùìòÇ\nØø\rÅåΔ_ΦΓΛΩΠΨΣΘΞ\u001BÆæßÉ !\"#¤%&'()*+,-./0123456789:;<=>?¡ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÑÜ§¿abcdefghijklmnopqrstuvwxyzäöñüà"));

		// All extensions
		assertArrayEquals(
				"\u000c^{}\\[~]|€",
				NumberConverter
						.hexStringToBytes("1b0a1b141b281b291b2f1b3c1b3d1b3e1b401b65"),
				SmsService.convertWith7BitAlphabet("\u000c^{}\\[~]|€"));
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

		// Telephone number: +436646311689
		assertArrayEquals(NumberConverter.hexStringToBytes("0C91346664136198"),
				SmsService
						.encodeInternationalNumberInSemiOctets("+436646311689"));
	}
}
