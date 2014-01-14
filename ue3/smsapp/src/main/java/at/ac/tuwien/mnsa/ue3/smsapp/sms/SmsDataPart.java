package at.ac.tuwien.mnsa.ue3.smsapp.sms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SmsDataPart {
	// UDH constants
	/**
	 * Concatenated short message w/ 16-bit reference number
	 */
	private static final byte INFORMATION_ELEMENT_IDENTIFIER_16BIT_CSMS = (byte) 0x08;
	/**
	 * Concatenated short message w/ 8-bit reference number
	 */
	private static final byte INFORMATION_ELEMENT_IDENTIFIER_8BIT_CSMS = (byte) 0x00;

	// PDU header
	private final Sms parentSms;
	private final byte[] smscInfo;
	private final byte pduHeader;
	private final byte messageReference;
	private final byte[] encodedDestinationAddress;
	private final byte protocolIdentifier;
	private final byte dataCodingScheme;
	private final byte validityPeriod; // should be optional
	private final byte userDataLength;

	// User data header
	private final byte userDataHeaderLength;
	private final byte informationElementIdentifier;
	private final byte informationElementLenght; // UDH length - 2
	private final byte[] csmsReferenceNumber;
	private final byte noOfParts;
	private final byte partNo;

	// User data
	private final byte[] encodedMsg;

	private final boolean hasUdh;

	/**
	 * Constructor with UDH
	 */
	public SmsDataPart(Sms parentSms, byte[] smscInfo, byte pduHeader,
			byte messageReference, byte[] encodedDestinationAddress,
			byte protocolIdentifier, byte dataCodingScheme,
			byte validityPeriod, byte userDataLength,
			byte[] csmsReferenceNumber, byte noOfParts, byte partNo,
			byte[] encodedMsg) {

		if (parentSms == null)
			throw new IllegalArgumentException("parentSms is null");
		if (csmsReferenceNumber == null || csmsReferenceNumber.length != 2)
			throw new IllegalArgumentException(
					"The CSMS reference number in the UDH has to consist of exactly two bytes.");
		// TODO check all the parameters for null, valid length, valid values,
		// ...

		// PDU header
		this.parentSms = parentSms;
		this.smscInfo = smscInfo;
		this.pduHeader = pduHeader;
		this.messageReference = messageReference;
		this.encodedDestinationAddress = encodedDestinationAddress;
		this.protocolIdentifier = protocolIdentifier;
		this.dataCodingScheme = dataCodingScheme;
		this.validityPeriod = validityPeriod;
		this.userDataLength = userDataLength;

		// UDH
		this.hasUdh = true;
		this.userDataHeaderLength = (byte) (csmsReferenceNumber.length + 4);
		this.informationElementIdentifier = INFORMATION_ELEMENT_IDENTIFIER_16BIT_CSMS;
		this.informationElementLenght = (byte) (userDataHeaderLength - 2);
		this.csmsReferenceNumber = csmsReferenceNumber;
		this.noOfParts = noOfParts;
		this.partNo = partNo;

		// Message
		this.encodedMsg = encodedMsg;
	}

	/**
	 * Constructor without UDH
	 */
	public SmsDataPart(Sms parentSms, byte[] smscInfo, byte pduHeader,
			byte messageReference, byte[] encodedDestinationAddress,
			byte protocolIdentifier, byte dataCodingScheme,
			byte validityPeriod, byte userDataLength, byte[] encodedMsg) {
		if (parentSms == null)
			throw new IllegalArgumentException("parentSms is null");
		// TODO check all the parameters for null, valid length, valid values,
		// ...

		// PDU header
		this.parentSms = parentSms;
		this.smscInfo = smscInfo;
		this.pduHeader = pduHeader;
		this.messageReference = messageReference;
		this.encodedDestinationAddress = encodedDestinationAddress;
		this.protocolIdentifier = protocolIdentifier;
		this.dataCodingScheme = dataCodingScheme;
		this.validityPeriod = validityPeriod;
		this.userDataLength = userDataLength;

		// UDH
		this.hasUdh = false;
		this.userDataHeaderLength = 0;
		this.informationElementIdentifier = 0;
		this.informationElementLenght = 0;
		this.csmsReferenceNumber = null;
		this.noOfParts = 0;
		this.partNo = 0;

		// Message
		this.encodedMsg = encodedMsg;
	}

	public Sms getParentSms() {
		return parentSms;
	}

	public int getMsgByteLengthWithoutSmscPart() {
		// TODO stub
		return 21;
	}

	public byte[] getPdu() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			outputStream.write(smscInfo);
			outputStream.write(pduHeader);
			outputStream.write(messageReference);
			outputStream.write(encodedDestinationAddress);
			outputStream.write(protocolIdentifier);
			outputStream.write(dataCodingScheme);
			outputStream.write(validityPeriod);
			outputStream.write(userDataLength);
			if (hasUdh)
				outputStream.write(getUdh());
			outputStream.write(encodedMsg);
		} catch (IOException ignored) {
		}

		return outputStream.toByteArray();
	}

	public byte[] getUdh() {
		if (!hasUdh)
			return null;

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			outputStream.write(userDataHeaderLength);
			outputStream.write(informationElementIdentifier);
			outputStream.write(informationElementLenght);
			outputStream.write(csmsReferenceNumber);
			outputStream.write(noOfParts);
			outputStream.write(partNo);
		} catch (IOException ignored) {
		}

		return outputStream.toByteArray();
	}

	public byte[] getSmscInfo() {
		return smscInfo;
	}

	public byte getPduHeader() {
		return pduHeader;
	}

	public byte getMessageReference() {
		return messageReference;
	}

	public byte[] getEncodedDestinationAddress() {
		return encodedDestinationAddress;
	}

	public byte getProtocolIdentifier() {
		return protocolIdentifier;
	}

	public byte getDataCodingScheme() {
		return dataCodingScheme;
	}

	public byte getValidityPeriod() {
		return validityPeriod;
	}

	public byte getUserDataLength() {
		return userDataLength;
	}

	public byte getUserDataHeaderLength() {
		return userDataHeaderLength;
	}

	public byte getInformationElementIdentifier() {
		return informationElementIdentifier;
	}

	public byte getInformationElementLenght() {
		return informationElementLenght;
	}

	public byte[] getCsmsReferenceNumber() {
		return csmsReferenceNumber;
	}

	public byte getNoOfParts() {
		return noOfParts;
	}

	public byte getPartNo() {
		return partNo;
	}

	public byte[] getEncodedMsg() {
		return encodedMsg;
	}
}
