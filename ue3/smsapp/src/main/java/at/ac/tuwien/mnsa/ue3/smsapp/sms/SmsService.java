package at.ac.tuwien.mnsa.ue3.smsapp.sms;

import java.util.ArrayList;
import java.util.List;

public class SmsService {

	private static final byte DEFAULT_SMSC = (byte) 0x00;
	private static final byte DEFAULT_PDU_HEADER = (byte) 0x11; // MTI set to
																// SMS-SUBMIT
																// and VPF set
																// to relative
																// format
	private static final byte DEFAULT_MESSAGE_REFERENCE = (byte) 0x00;
	private static final byte DEFAULT_PID = (byte) 0x00;
	private static final byte DEFAULT_DCS = (byte) 0x00;
	private static final byte DEFAULT_VALIDITY_PERIOD = (byte) 0xA7;

	/**
	 * Assembles the a list of SMS parts (each containing max. 160 characters of
	 * the SMS) which have to be sent one by one.
	 * 
	 * @param sms
	 *            The SMS containing the recipient and the full text
	 * @return a list of parts in which the original SMS is split into
	 */
	public static List<SmsDataPart> getSmsDataParts(Sms sms) {
		List<SmsDataPart> parts = new ArrayList<SmsDataPart>();

		// Split the message into parts

		// Generate the PDU for each part

		// TODO method stub
		parts.add(new SmsDataPart(sms));

		return parts;
	}

	// TODO This service should also handle the alphabet conversions and PDU
	// creation and storage into the SmsDataParts

}
