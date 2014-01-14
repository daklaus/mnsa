package at.ac.tuwien.mnsa.ue3.smsapp.sms;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.common.binary.NumberConverter;

public class SmsService {

	// PDU constants
	private static final byte[] DEFAULT_SMSC = new byte[] { (byte) 0x00 };
	private static final byte DEFAULT_PDU_HEADER = (byte) 0x11; // MTI set to
																// SMS-SUBMIT
																// and VPF set
																// to relative
																// format
	private static final byte DEFAULT_MESSAGE_REFERENCE = (byte) 0x00;
	private static final byte DEFAULT_PROTOCOL_IDENTIFIER = (byte) 0x00;
	private static final byte DEFAULT_DATA_CODING_SCHEME = (byte) 0x00;
	private static final byte DEFAULT_VALIDITY_PERIOD = (byte) 0xA7;

	private static final int MAXIMUM_CHARS_IN_MULTIPART = 152;
	private static final int MAXIMUM_CHARS_IN_SINGLEPART = 160;

	/**
	 * Assembles the a list of SMS parts (each containing max. 160 characters of
	 * the SMS) which have to be sent one by one.
	 * 
	 * @param sms
	 *            The SMS containing the recipient and the full text
	 * @return a list of parts in which the original SMS is split into
	 */
	public static List<SmsDataPart> getSmsDataParts(Sms sms) {
		if (sms == null)
			throw new IllegalArgumentException("SMS is null");

		List<SmsDataPart> parts = new ArrayList<SmsDataPart>();
		String msg = sms.getMessage();

		if (msg.length() <= MAXIMUM_CHARS_IN_SINGLEPART) {
			// Send message as single SMS without UDH
			// Generate PDU for the SMS using the default PDU header and no UDH
			SmsDataPart sdp = new SmsDataPart(sms, DEFAULT_SMSC,
					DEFAULT_PDU_HEADER, DEFAULT_MESSAGE_REFERENCE,
					encodeInternationalNumberInSemiOctets(sms.getRecipient()),
					DEFAULT_PROTOCOL_IDENTIFIER, DEFAULT_DATA_CODING_SCHEME,
					DEFAULT_VALIDITY_PERIOD, (byte) msg.length(),
					encodeMsgInSeptets(msg));

			parts.add(sdp);
			return parts;
		}

		// Split the message into parts
		int numParts = msg.length() / MAXIMUM_CHARS_IN_MULTIPART + 1;

		// TODO Generate the reference number with a random generator
		byte[] csmsReferenceNumber = new byte[] { (byte) 0x00, (byte) 0x00 };
		// Sets the UDHI bit in the PDU header
		byte pduHearder = NumberConverter.setBit(6, DEFAULT_PDU_HEADER);

		for (int i = 0; i < numParts; i++) {
			String msgPart = msg.substring(i * MAXIMUM_CHARS_IN_MULTIPART, Math
					.min((i + 1) * MAXIMUM_CHARS_IN_MULTIPART, msg.length()));

			// Add 8 septetts (7 bytes) for the UDH
			byte userDataLength = (byte) (msgPart.length() + (MAXIMUM_CHARS_IN_SINGLEPART - MAXIMUM_CHARS_IN_MULTIPART));

			// Generate the PDU for each part for sending as a concatenated SMS
			// with the PDUs containing a UDH for reassembling
			SmsDataPart sdp = new SmsDataPart(sms, DEFAULT_SMSC, pduHearder,
					DEFAULT_MESSAGE_REFERENCE,
					encodeInternationalNumberInSemiOctets(sms.getRecipient()),
					DEFAULT_PROTOCOL_IDENTIFIER, DEFAULT_DATA_CODING_SCHEME,
					DEFAULT_VALIDITY_PERIOD, userDataLength,
					csmsReferenceNumber, (byte) numParts, (byte) (i + 1),
					encodeMsgInSeptets(msgPart));

			parts.add(sdp);
		}

		return parts;
	}

	// TODO For Klaus ;)
	static byte[] encodeMsgInSeptets(String msg) {
		byte[] bMsg = convertWith7BitAlphabet(msg);

		// TODO stub method

		// "Test PDU" without UDH padding
		return NumberConverter.hexStringToBytes("D4F29C0E8212AB");
	}

	// TODO For Christian ;)
	private static byte[] convertWith7BitAlphabet(String msg) {
		// TODO Auto-generated method stub
		return null;
	}

	// TODO For Christian ;)
	static byte[] encodeInternationalNumberInSemiOctets(String number) {
		// TODO stub method

		// Klaus' number: +436646311689
		return NumberConverter.hexStringToBytes("0C91346664136198");
	}

}
