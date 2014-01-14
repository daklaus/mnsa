package at.ac.tuwien.mnsa.ue3.smsapp.sms;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.common.binary.NumberConverter;

public class SmsService {

	// PDU constants
	private static final byte DEFAULT_SMSC = (byte) 0x00;
	private static final byte DEFAULT_PDU_HEADER = (byte) 0x11; // MTI set to
																// SMS-SUBMIT
																// and VPF set
																// to relative
																// format
	private static final byte DEFAULT_MESSAGE_REFERENCE = (byte) 0x00;
	private static final byte DEFAULT_PROTOCOL_IDENTIFIER = (byte) 0x00;
	private static final byte DEFAULT_DATA_CODING_SCHEME = (byte) 0x00;
	private static final byte DEFAULT_VALIDITY_PERIOD = (byte) 0xA7;

	// UDH constants
	private static final byte DEFAULT_UDH_LENGTH = (byte) 0x05;
	private static final byte DEFAULT_INFORMATION_ELEMENT_IDENTIFIER = (byte) 0x00; // Concatenated
																					// short
																					// message
																					// w/
																					// 8-bit
																					// reference
																					// number
	private static final byte DEFAULT_LENGTH_OF_HEADER = (byte) 0x03;

	private static final int MAXIMUM_CHARS_IN_MULTIPART = 153;
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
			SmsDataPart sdp = new SmsDataPart(sms, new byte[] { DEFAULT_SMSC },
					DEFAULT_PDU_HEADER, DEFAULT_MESSAGE_REFERENCE,
					encodeInternationalNumberInSemiOctets(sms.getRecipient()),
					DEFAULT_PROTOCOL_IDENTIFIER, DEFAULT_DATA_CODING_SCHEME,
					DEFAULT_VALIDITY_PERIOD, (byte) msg.length(),
					encodeMsgInSeptets(msg, false));

			parts.add(sdp);
			return parts;
		}

		// Split the message into parts
		int numParts = msg.length() / MAXIMUM_CHARS_IN_MULTIPART + 1;

		// TODO generate the reference number with a random generator
		byte csmsReferenceNumber = (byte) 0x00;

		for (int i = 0; i < numParts; i++) {
			String msgPart = msg.substring(i * MAXIMUM_CHARS_IN_MULTIPART, Math
					.min((i + 1) * MAXIMUM_CHARS_IN_MULTIPART, msg.length()));

			// Add seven septets (6 bytes + 1 bit padding) for the UDH
			byte userDataLength = (byte) (msgPart.length() + 7);

			// Generate the PDU for each part for sending as a concatenated SMS
			// with the PDUs containing a UDH for reassembling
			SmsDataPart sdp = new SmsDataPart(sms, new byte[] { DEFAULT_SMSC },
					DEFAULT_PDU_HEADER, DEFAULT_MESSAGE_REFERENCE,
					encodeInternationalNumberInSemiOctets(sms.getRecipient()),
					DEFAULT_PROTOCOL_IDENTIFIER, DEFAULT_DATA_CODING_SCHEME,
					DEFAULT_VALIDITY_PERIOD, userDataLength,
					csmsReferenceNumber, (byte) numParts, (byte) (i + 1),
					encodeMsgInSeptets(msgPart, true));

			parts.add(sdp);
		}

		return parts;
	}

	// TODO For Christian ;)
	private static byte[] encodeMsgInSeptets(String msg, boolean withUdhPadding) {
		// TODO stub method

		// TODO do things before UDH padding (conversion to hex or binary or
		// something)

		if (withUdhPadding) {
			// Do something for UDH padding
			// Since our UDH is always 6 bytes (48 bits) long the padding to
			// septets (multiply of 7 - to 49) will always be one bit

			return null;
		}

		// TODO do your thing

		// "Test PDU" without UDH padding
		return NumberConverter.hexStringToBytes("D4F29C0E8212AB");
	}

	// TODO For Christian ;)
	private static byte[] encodeInternationalNumberInSemiOctets(String number) {
		// TODO stub method

		// Klaus' number: +436646311689
		return NumberConverter.hexStringToBytes("0C91346664136198");
	}

}
