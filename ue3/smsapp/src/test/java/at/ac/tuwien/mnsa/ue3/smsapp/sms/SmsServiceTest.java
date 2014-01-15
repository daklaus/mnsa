package at.ac.tuwien.mnsa.ue3.smsapp.sms;

import static org.junit.Assert.*;

import java.util.List;

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
	public void testGetSmsDataPartsWithSinglePartSms() {
		Sms sms = new Sms("+436641234567",
				"This is a test message which fits easily in a single SMS.");
		List<SmsDataPart> list = SmsService.getSmsDataParts(sms);

		assertNotNull(list);
		assertEquals("List size", 1, list.size());
		SmsDataPart sdp = list.get(0);
		assertNotNull(sdp);

		byte[] expected = NumberConverter
				.hexStringToBytes("0011000C913466143254760000A73954747A0E4ACF416110BD3CA783DAE5F93C7C2E83EEE8F4180D32A7E97350393C4FB3F3A0B41B1406CDD3EE33BB0C9A36A72E");
		byte[] actual = sdp.getPdu();

		assertNotNull(actual);
		assertEquals(expected.length, actual.length);
		// Set the actual message reference to zero because otherwise we cannot
		// compare it since it's a random number
		actual[2] = 0;
		assertArrayEquals(expected, actual);
	}

	@SuppressWarnings({ "unused" })
	@Test
	public void testGetSmsDataPartsWithMultiPartSms() {
		Sms sms = new Sms(
				"+436641234567",
				"This is a very long text so it won't fit into 160 characters which is the lenght of a character string which can be transmitted in a single short message over the GSM network.");
		List<SmsDataPart> list = SmsService.getSmsDataParts(sms);

		assertNotNull(list);
		assertEquals("List size", 2, list.size());

		/*
		 * Compare first partial SMS
		 */
		SmsDataPart sdp = list.get(0);
		assertNotNull(sdp);

		byte[] expected = NumberConverter
				.hexStringToBytes("0051000C913466143254760000A7A00608040000020154747A0E4ACF416190BD2CCF83D86FF719442FE3E9A0F91B94A683EE6FF7890E32A7E9A0B49BFE06C56C30D0181D9687C7F4B27C0EBAA3D36334283D07D1D16510BBEC3EA3E9A0B71914068DD16179784C2FCB4173BA3CED3E83EEE8F4180D1A87DD207119449787DDF3769A4E2F93416937280C9AA7DD6776193447BFE57450BB3C9F87CF");
		byte[] actual = sdp.getPdu();

		assertNotNull(actual);
		assertEquals(expected.length, actual.length);
		// Set the actual message reference to zero because otherwise we cannot
		// compare it since it's a random number
		actual[2] = 0;

		// Set the actual CSMS message reference to zero because otherwise we
		// cannot compare it since it's a random number
		if (SmsService.CSMS_REFERENCE_NUMBER_BYTES != 2) {
			fail("A CSMS reference number with fewer or more than two bytes is not tested");
		} else {
			actual[18] = 0;
			actual[19] = 0;
		}

		assertArrayEquals(expected, actual);

		/*
		 * Compare second partial SMS
		 */
		sdp = list.get(1);
		assertNotNull(sdp);

		expected = NumberConverter
				.hexStringToBytes("0051000C913466143254760000A71F0608040000020265D0DB5E9683E8E832E8386D82DC65FAFD2D5FBB");
		actual = sdp.getPdu();

		assertNotNull(actual);
		assertEquals(expected.length, actual.length);
		// Set the actual message reference to zero because otherwise we cannot
		// compare it since it's a random number
		actual[2] = 0;

		// Set the actual CSMS message reference to zero because otherwise we
		// cannot compare it since it's a random number
		if (SmsService.CSMS_REFERENCE_NUMBER_BYTES != 2) {
			fail("A CSMS reference number with fewer or more than two bytes is not tested");
		} else {
			actual[18] = 0;
			actual[19] = 0;
		}

		assertArrayEquals(expected, actual);

	}

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
		assertArrayEquals(
				"This is a much longer version of a test string for testing longer messages",
				NumberConverter
						.hexStringToBytes("54747A0E4ACF416150BB3E4683D86FF7B92C07D9CBF279FAED06BDCDA030885E9ED34173BA3CED3E83CC6F39885E9ED3D3EE3388FD769FCB7250BB3C9F87CFE539"),
				SmsService.encodeMsgInSeptets(NumberConverter
						.hexStringToBytes("546869732069732061206D756368206C6F6E6765722076657273696F6E206F662061207465737420737472696E6720666F722074657374696E67206C6F6E676572206D65737361676573")));
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
