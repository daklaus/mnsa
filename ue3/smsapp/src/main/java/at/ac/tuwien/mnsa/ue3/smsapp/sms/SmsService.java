package at.ac.tuwien.mnsa.ue3.smsapp.sms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

import at.ac.tuwien.common.binary.NumberConverter;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.primitives.Bytes;

public class SmsService {

	// PDU constants
	private static final byte[] DEFAULT_SMSC = new byte[] { (byte) 0x00 };
	/**
	 * MTI set to SMS-SUBMIT and VPF set to relative format
	 */
	private static final byte DEFAULT_PDU_HEADER = (byte) 0x11;
	private static final byte DEFAULT_PROTOCOL_IDENTIFIER = (byte) 0x00;
	private static final byte DEFAULT_DATA_CODING_SCHEME = (byte) 0x00;
	private static final byte DEFAULT_VALIDITY_PERIOD = (byte) 0xA7;

	public static final int MAXIMUM_SEPTETS_IN_SINGLEPART = 160;
	// Attention: If you change the below constant to one byte don't forget to
	// pad the encoded message by one bit for the alignment to septets!
	public static final int CSMS_REFERENCE_NUMBER_BYTES = 2;
	public static final int UDH_LENGHT_IN_BYTES = CSMS_REFERENCE_NUMBER_BYTES + 5;
	public static final int UDH_LENGHT_IN_SEPTETS = (int) Math
			.ceil((double) UDH_LENGHT_IN_BYTES / 7 * 8);
	public static final int MAXIMUM_SEPTETS_IN_MULTIPART = MAXIMUM_SEPTETS_IN_SINGLEPART
			- UDH_LENGHT_IN_SEPTETS;

	private static final String INT_NUMBER_FORMAT_PREFIX = "91";

	private static final BiMap<Character, Byte> GSM0338_ALPHABET;
	private static final BiMap<Character, Byte> GSM0338_ALPHABET_EXTENSION;

	static {
		GSM0338_ALPHABET = new ImmutableBiMap.Builder<Character, Byte>()
				// 0x00 - 0x0F
				.put('@', (byte) 0x00)
				.put('£', (byte) 0x01)
				.put('$', (byte) 0x02)
				.put('¥', (byte) 0x03)
				.put('è', (byte) 0x04)
				.put('é', (byte) 0x05)
				.put('ù', (byte) 0x06)
				.put('ì', (byte) 0x07)
				.put('ò', (byte) 0x08)
				.put('Ç', (byte) 0x09)
				.put('\n', (byte) 0x0A)
				.put('Ø', (byte) 0x0B)
				.put('ø', (byte) 0x0C)
				.put('\r', (byte) 0x0D)
				.put('Å', (byte) 0x0E)
				.put('å', (byte) 0x0F)

				// 0x10 - 0x1F
				.put('Δ', (byte) 0x10)
				.put('_', (byte) 0x11)
				.put('Φ', (byte) 0x12)
				.put('Γ', (byte) 0x13)
				.put('Λ', (byte) 0x14)
				.put('Ω', (byte) 0x15)
				.put('Π', (byte) 0x16)
				.put('Ψ', (byte) 0x17)
				.put('Σ', (byte) 0x18)
				.put('Θ', (byte) 0x19)
				.put('Ξ', (byte) 0x1A)
				.put('\u001B', (byte) 0x1B)
				.put('Æ', (byte) 0x1C)
				.put('æ', (byte) 0x1D)
				.put('ß', (byte) 0x1E)
				.put('É', (byte) 0x1F)

				// 0x20 - 0x2F
				.put(' ', (byte) 0x20)
				.put('!', (byte) 0x21)
				.put('"', (byte) 0x22)
				.put('#', (byte) 0x23)
				.put('¤', (byte) 0x24)
				.put('%', (byte) 0x25)
				.put('&', (byte) 0x26)
				.put('\'', (byte) 0x27)
				.put('(', (byte) 0x28)
				.put(')', (byte) 0x29)
				.put('*', (byte) 0x2A)
				.put('+', (byte) 0x2B)
				.put(',', (byte) 0x2C)
				.put('-', (byte) 0x2D)
				.put('.', (byte) 0x2E)
				.put('/', (byte) 0x2F)

				// 0x30 - 0x3F
				.put('0', (byte) 0x30)
				.put('1', (byte) 0x31)
				.put('2', (byte) 0x32)
				.put('3', (byte) 0x33)
				.put('4', (byte) 0x34)
				.put('5', (byte) 0x35)
				.put('6', (byte) 0x36)
				.put('7', (byte) 0x37)
				.put('8', (byte) 0x38)
				.put('9', (byte) 0x39)
				.put(':', (byte) 0x3A)
				.put(';', (byte) 0x3B)
				.put('<', (byte) 0x3C)
				.put('=', (byte) 0x3D)
				.put('>', (byte) 0x3E)
				.put('?', (byte) 0x3F)

				// 0x40 - 0x4F
				.put('¡', (byte) 0x40)
				.put('A', (byte) 0x41)
				.put('B', (byte) 0x42)
				.put('C', (byte) 0x43)
				.put('D', (byte) 0x44)
				.put('E', (byte) 0x45)
				.put('F', (byte) 0x46)
				.put('G', (byte) 0x47)
				.put('H', (byte) 0x48)
				.put('I', (byte) 0x49)
				.put('J', (byte) 0x4A)
				.put('K', (byte) 0x4B)
				.put('L', (byte) 0x4C)
				.put('M', (byte) 0x4D)
				.put('N', (byte) 0x4E)
				.put('O', (byte) 0x4F)

				// 0x50 - 0x5F
				.put('P', (byte) 0x50)
				.put('Q', (byte) 0x51)
				.put('R', (byte) 0x52)
				.put('S', (byte) 0x53)
				.put('T', (byte) 0x54)
				.put('U', (byte) 0x55)
				.put('V', (byte) 0x56)
				.put('W', (byte) 0x57)
				.put('X', (byte) 0x58)
				.put('Y', (byte) 0x59)
				.put('Z', (byte) 0x5A)
				.put('Ä', (byte) 0x5B)
				.put('Ö', (byte) 0x5C)
				.put('Ñ', (byte) 0x5D)
				.put('Ü', (byte) 0x5E)
				.put('§', (byte) 0x5F)

				// 0x60 - 0x6F
				.put('¿', (byte) 0x60).put('a', (byte) 0x61)
				.put('b', (byte) 0x62).put('c', (byte) 0x63)
				.put('d', (byte) 0x64)
				.put('e', (byte) 0x65)
				.put('f', (byte) 0x66)
				.put('g', (byte) 0x67)
				.put('h', (byte) 0x68)
				.put('i', (byte) 0x69)
				.put('j', (byte) 0x6A)
				.put('k', (byte) 0x6B)
				.put('l', (byte) 0x6C)
				.put('m', (byte) 0x6D)
				.put('n', (byte) 0x6E)
				.put('o', (byte) 0x6F)

				// 0x70 - 0x7F
				.put('p', (byte) 0x70).put('q', (byte) 0x71)
				.put('r', (byte) 0x72).put('s', (byte) 0x73)
				.put('t', (byte) 0x74).put('u', (byte) 0x75)
				.put('v', (byte) 0x76).put('w', (byte) 0x77)
				.put('x', (byte) 0x78).put('y', (byte) 0x79)
				.put('z', (byte) 0x7A).put('ä', (byte) 0x7B)
				.put('ö', (byte) 0x7C).put('ñ', (byte) 0x7D)
				.put('ü', (byte) 0x7E).put('à', (byte) 0x7F).build();

		GSM0338_ALPHABET_EXTENSION = new ImmutableBiMap.Builder<Character, Byte>()
				.put('\u000c', (byte) 0x0A)
				// .put('', (byte) 0x0D) CR2
				.put('^', (byte) 0x14)
				// .put('', (byte) 0x1B) SS2
				.put('{', (byte) 0x28).put('}', (byte) 0x29)
				.put('\\', (byte) 0x2F).put('[', (byte) 0x3C)
				.put('~', (byte) 0x3D).put(']', (byte) 0x3E)
				.put('|', (byte) 0x40).put('€', (byte) 0x65).build();
	}

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
		if (msg == null || msg.length <= 0)
			return parts;

		// Calculate common fields
		byte[] smscInfo = DEFAULT_SMSC;
		byte[] encodedRecipient = encodeInternationalNumberInSemiOctets(sms
				.getRecipient());
		// Generate the reference number with a random generator
		// Weak random generator is sufficient for this purpose
		Random rnd = new Random();
		byte[] rndBytes = new byte[1];
		rnd.nextBytes(rndBytes);
		byte messageReference = rndBytes[0];
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

		// Generate the CSMS reference number with a random generator
		// Weak random generator is sufficient for this purpose
		byte[] csmsReferenceNumber = new byte[CSMS_REFERENCE_NUMBER_BYTES];
		rnd.nextBytes(csmsReferenceNumber);
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

	/**
	 * Converts a message encoded in the 7 bit alphabet (according to GSM 03.38)
	 * to the PDU encoding.
	 * 
	 * @param msg
	 *            the message encoded with the 7-bit alphabet
	 * @return the byte array conatining the septets of the message
	 */
	static byte[] encodeMsgInSeptets(byte[] msg) {
		if (msg == null || msg.length <= 0) {
			return new byte[] {};
		}

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

	/**
	 * Converts a String into a byte-Array using the GSM 03.38 alphabet (Basic
	 * Character set with extensions)
	 * 
	 * @param msg
	 *            The message being converted
	 * @return byte[] representation of the converted String
	 */
	static byte[] convertWith7BitAlphabet(String msg) {
		if (msg == null)
			throw new IllegalArgumentException("The message is null");

		char tempChar;
		List<Byte> msgByteList;

		// Initialize Byte ArrayList
		msgByteList = new ArrayList<Byte>();

		// Loop through all characters of msg
		for (int i = 0; i < msg.length(); i++) {

			// Store current char
			tempChar = msg.charAt(i);

			// if the Basic GSM 03.38 alphabet contains the key, store it
			// into the list...
			if (GSM0338_ALPHABET.containsKey(tempChar)) {
				msgByteList.add(GSM0338_ALPHABET.get(tempChar));

			} else {

				// ..., if not, search it in the extensions of the GSM 03.38
				// alphabet and store it into the list
				if (GSM0338_ALPHABET_EXTENSION.containsKey(tempChar)) {
					msgByteList.add((byte) 0x1B);
					msgByteList.add(GSM0338_ALPHABET_EXTENSION.get(tempChar));
				}
			}
		}

		return Bytes.toArray(msgByteList);
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

		char[] numberInSwappedSemiOctets;
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

		// Calculate number as swapped semi-octets (nibbles)
		numberInSwappedSemiOctets = new char[number.length()];

		for (int i = 0; i < number.length(); i++) {
			if (i % 2 == 0) {
				numberInSwappedSemiOctets[i] = number.charAt(i + 1);
			} else {
				numberInSwappedSemiOctets[i] = number.charAt(i - 1);
			}
		}

		return NumberConverter.hexStringToBytes(numberLengthHex
				+ INT_NUMBER_FORMAT_PREFIX
				+ new String(numberInSwappedSemiOctets));
	}
}
