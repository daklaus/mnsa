package at.ac.tuwien.mnsa.ue3.smsapp.sms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
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

	private static final int MAXIMUM_SEPTETS_IN_SINGLEPART = 160;
	private static final int UDH_LENGHT_IN_BYTES = 7;
	private static final int UDH_LENGHT_IN_SEPTETS = (int) Math
			.ceil((double) UDH_LENGHT_IN_BYTES / 7 * 8);
	// private static final int MAXIMUM_BYTES_IN_SINGLEPART =
	// MAXIMUM_SEPTETS_IN_SINGLEPART / 8 * 7;
	private static final int MAXIMUM_SEPTETS_IN_MULTIPART = MAXIMUM_SEPTETS_IN_SINGLEPART
			- UDH_LENGHT_IN_SEPTETS;

	private static final String INT_NUMBER_FORMAT = "91";

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
		byte[] msg = convertWith7BitAlphabet(sms.getMessage());

		// Calculate common fields
		byte[] smscInfo = DEFAULT_SMSC;
		byte[] encodedRecipient = encodeInternationalNumberInSemiOctets(sms
				.getRecipient());
		// TODO Generate the reference number with a random generator
		byte messageReference = DEFAULT_MESSAGE_REFERENCE;
		byte pduHearder = DEFAULT_PDU_HEADER;
		byte protocolIdentifier = DEFAULT_PROTOCOL_IDENTIFIER;
		byte dataCodingScheme = DEFAULT_DATA_CODING_SCHEME;
		byte validityPeriod = DEFAULT_VALIDITY_PERIOD;

		if (msg.length <= MAXIMUM_SEPTETS_IN_SINGLEPART) {
			// Send message as single SMS without UDH
			// Generate PDU for the SMS using the default PDU header and no UDH
			SmsDataPart sdp = new SmsDataPart(sms, smscInfo, pduHearder,
					messageReference, encodedRecipient, protocolIdentifier,
					dataCodingScheme, validityPeriod, (byte) msg.length,
					encodeMsgInSeptets(msg));

			parts.add(sdp);
			return parts;
		}

		// Split the message into parts
		int numParts = msg.length / MAXIMUM_SEPTETS_IN_MULTIPART + 1;

		// TODO Generate the reference number with a random generator
		byte[] csmsReferenceNumber = new byte[] { (byte) 0x00, (byte) 0x00 };
		// Sets the UDHI bit in the PDU header
		pduHearder = NumberConverter.setBit(6, pduHearder);

		for (int i = 0; i < numParts; i++) {
			byte[] msgPart = Arrays.copyOfRange(msg, i
					* MAXIMUM_SEPTETS_IN_MULTIPART, Math.min((i + 1)
					* MAXIMUM_SEPTETS_IN_MULTIPART, msg.length));

			// Add length of UDH
			byte userDataLength = (byte) (msgPart.length + UDH_LENGHT_IN_SEPTETS);

			// Generate the PDU for each part for sending as a concatenated SMS
			// with the PDUs containing a UDH for reassembling
			SmsDataPart sdp = new SmsDataPart(sms, smscInfo, pduHearder,
					messageReference, encodedRecipient, protocolIdentifier,
					dataCodingScheme, validityPeriod, userDataLength,
					csmsReferenceNumber, (byte) numParts, (byte) (i + 1),
					encodeMsgInSeptets(msgPart));

			parts.add(sdp);
		}

		return parts;
	}

	// TODO For Klaus ;)
	static byte[] encodeMsgInSeptets(byte[] msg) {

		if (msg == null || msg.length <= 0) {
			return new byte[] {};
		}
		// in 7 bit alphabet ABC = 414243 = 0100_0001 0100_0010 0100_0011 =
		// 1000001 1000010 1000011
		// back: ABC = 41e110 = 01000001 11100001 00010000

		// Hint: BitMap indexing starts at the most significant octet (byte)
		// (form left) but in the octets from the LSB means from the right

		BitSet outcome = new BitSet();
		BitSet input = BitSet.valueOf(msg);

		int outOctet = 0;
		int outBit = 0;
		int fromPos, toPos;

		// Run through octets of input from left to right (from most significant
		// octet to least significant octet)
		for (int inOctet = 0; inOctet < msg.length; inOctet++) {
			// Run through each bit in the octet from right to left (from LSB to
			// MSB) ignoring the last (MSB)
			for (int inBit = 0; inBit < 7; inBit++) {

				// Calculate fromPos
				fromPos = inOctet * 8 + inBit;

				// Calculate toPos
				toPos = outOctet * 8 + outBit;

				// Copy the bits
				outcome.set(toPos, input.get(fromPos));

				// Advance counters
				outBit++;
				if (outBit >= 8) {
					outOctet++;
					outBit = 0;
				}
			}
		}

		return outcome.toByteArray();
	}

	// TODO For Christian ;)
	static byte[] convertWith7BitAlphabet(String msg) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Converts the specified international telephone number to a 7-Bit format.
	 * The format consists of the Address length in Hex, the Type-of-Address
	 * (91) and the 7-Bit representation of the actual telephone number
	 * 
	 * @param number
	 *            An international telephone number (including preceding +)
	 * @return byte[] representation of the converted telephone number
	 */
	static byte[] encodeInternationalNumberInSemiOctets(String number) {
		number = number.trim();
		if (!number.matches("^\\+\\d{3,}"))
			throw new IllegalArgumentException(
					"The number is not in international format (e.g. +436641234567)");

		char[] number7BitRaw;
		String numberLengthHex;

		// Exclude + from number
		if (number.substring(0, 1).equals("+")) {
			number = number.substring(1);
		}

		// Get length of number in Hex
		numberLengthHex = Integer.toHexString(number.length()).toUpperCase();

		// Add an 0, if number length < 16
		if (number.length() < 16)
			numberLengthHex = "0" + numberLengthHex;

		// Add F, if length of number is odd
		if (number.length() % 2 != 0) {
			number += 'F';
		}

		// Calculate 7-Bit number
		number7BitRaw = new char[number.length()];

		for (int i = 0; i < number.length(); ++i) {
			if (i % 2 == 0) {
				number7BitRaw[i] = number.charAt(i + 1);
			} else {
				number7BitRaw[i] = number.charAt(i - 1);
			}
		}

		return NumberConverter.hexStringToBytes(numberLengthHex
				+ INT_NUMBER_FORMAT + new String(number7BitRaw));
	}
}
