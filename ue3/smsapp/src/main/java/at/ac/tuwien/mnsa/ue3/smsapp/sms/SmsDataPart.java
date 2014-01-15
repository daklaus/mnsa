package at.ac.tuwien.mnsa.ue3.smsapp.sms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SmsDataPart {
	// UDH constants
	/**
	 * Concatenated short message w/ 8-bit reference number
	 */
	private static final byte INFORMATION_ELEMENT_IDENTIFIER_8BIT_CSMS = (byte) 0x00;
	/**
	 * Concatenated short message w/ 16-bit reference number
	 */
	private static final byte INFORMATION_ELEMENT_IDENTIFIER_16BIT_CSMS = (byte) 0x08;

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

	private byte[] finalPdu;

	/**
	 * Constructor with UDH
	 */
	public SmsDataPart(Sms parentSms, byte[] smscInfo, byte pduHeader,
			byte messageReference, byte[] encodedDestinationAddress,
			byte protocolIdentifier, byte dataCodingScheme,
			byte validityPeriod, byte userDataLength,
			byte[] csmsReferenceNumber, byte noOfParts, byte partNo,
			byte[] encodedMsg) {

		// Checks for PDU header data
		if (parentSms == null)
			throw new IllegalArgumentException("parentSms is null");
		if (smscInfo == null || smscInfo.length < 1)
			throw new IllegalArgumentException(
					"The SMSC info byte array must at least contain one byte (0x00) for a zero byte SMSC info");
		if ((pduHeader & 0x03) != 1)
			throw new IllegalArgumentException(
					"The PDU header is not set to SMS-SUBMIT type");
		if (encodedDestinationAddress == null
				|| encodedDestinationAddress.length <= 0)
			throw new IllegalArgumentException(
					"The encoded destination address must at least contain one byte");

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

		// Check UDH specific parameters
		if (csmsReferenceNumber == null || csmsReferenceNumber.length < 1
				|| csmsReferenceNumber.length > 2)
			throw new IllegalArgumentException(
					"The CSMS reference number in the UDH has to consist of at least one and maximal two bytes.");
		if (noOfParts == 0 || partNo == 0)
			throw new IllegalArgumentException(
					"The \"number of parts\" field or the \"part number\" field in the UDH must not be zero!");

		// UDH
		this.userDataHeaderLength = (byte) (csmsReferenceNumber.length + 4);
		if (csmsReferenceNumber.length < 2) {
			this.informationElementIdentifier = INFORMATION_ELEMENT_IDENTIFIER_8BIT_CSMS;
		} else {
			this.informationElementIdentifier = INFORMATION_ELEMENT_IDENTIFIER_16BIT_CSMS;
		}
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

		this(parentSms, smscInfo, pduHeader, messageReference,
				encodedDestinationAddress, protocolIdentifier,
				dataCodingScheme, validityPeriod, userDataLength, new byte[] {
						(byte) 0x00, (byte) 0x00 }, (byte) 1, (byte) 1,
				encodedMsg);

		if (isUdhiSet())
			throw new IllegalArgumentException(
					"The UDHI bit in the PDU header is set! Either you set it by accident or you use the wrong constructor for this class");
	}

	public Sms getParentSms() {
		return parentSms;
	}

	public int getMsgByteLengthWithoutSmscPart() {
		return getPdu().length - smscInfo.length;
	}

	public byte[] getPdu() {
		if (finalPdu == null) {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			try {
				outputStream.write(smscInfo);
				outputStream.write(pduHeader);
				outputStream.write(messageReference);
				outputStream.write(encodedDestinationAddress);
				outputStream.write(protocolIdentifier);
				outputStream.write(dataCodingScheme);
				if ((pduHeader & 0x18) != 0)
					outputStream.write(validityPeriod);
				outputStream.write(userDataLength);
				if (isUdhiSet())
					outputStream.write(getUdh());
				outputStream.write(encodedMsg);
			} catch (IOException ignored) {
			}
			finalPdu = outputStream.toByteArray();
		}

		return finalPdu;
	}

	public byte[] getUdh() {
		if (!isUdhiSet())
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

	public boolean isUdhiSet() {
		// If UDHI in the PDU header is set
		return (pduHeader & 0x40) != 0;
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
		if ((pduHeader & 0x18) != 0)
			return 0;

		return validityPeriod;
	}

	public byte getUserDataLength() {
		return userDataLength;
	}

	public byte getUserDataHeaderLength() {
		if (!isUdhiSet())
			return 0;

		return userDataHeaderLength;
	}

	public byte getInformationElementIdentifier() {
		if (!isUdhiSet())
			return 0;

		return informationElementIdentifier;
	}

	public byte getInformationElementLenght() {
		if (!isUdhiSet())
			return 0;
		return informationElementLenght;
	}

	public byte[] getCsmsReferenceNumber() {
		if (!isUdhiSet())
			return null;

		return csmsReferenceNumber;
	}

	public byte getNoOfParts() {
		if (!isUdhiSet())
			return 0;

		return noOfParts;
	}

	public byte getPartNo() {
		if (!isUdhiSet())
			return 0;

		return partNo;
	}

	public byte[] getEncodedMsg() {
		return encodedMsg;
	}
}
